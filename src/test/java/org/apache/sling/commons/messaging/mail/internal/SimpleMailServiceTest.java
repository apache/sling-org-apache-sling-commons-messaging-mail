/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.commons.messaging.mail.internal;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.sling.commons.crypto.CryptoService;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolConfig;
import org.apache.sling.commons.threads.ThreadPoolManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleMailServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testComponentLifecycle() throws Exception {
        final Transport transport = mock(Transport.class);
        final Session session = mock(Session.class);
        when(session.getTransport("smtps")).thenReturn(transport);
        final MimeMessage message = mock(MimeMessage.class);
        when(message.getSession()).thenReturn(session);
        final ThreadPoolManager threadPoolManager = mock(ThreadPoolManager.class);
        when(threadPoolManager.get("default")).thenReturn(new DefaultThreadPool());
        final CryptoService cryptoService = mock(CryptoService.class);
        final SimpleMailService service = new SimpleMailService();
        FieldUtils.writeDeclaredField(service, "threadPoolManager", threadPoolManager, true);
        FieldUtils.writeDeclaredField(service, "cryptoService", cryptoService, true);
        FieldUtils.writeDeclaredField(service, "connectionListeners", Collections.emptyList(), true);
        FieldUtils.writeDeclaredField(service, "transportListeners", Collections.emptyList(), true);
        { // activate
            final SimpleMailServiceConfiguration configuration = mock(SimpleMailServiceConfiguration.class);
            when(configuration.threadpool_name()).thenReturn("default");
            MethodUtils.invokeMethod(service, true, "activate", configuration);
            service.sendMessage(message);
        }
        { // modified
            final SimpleMailServiceConfiguration configuration = mock(SimpleMailServiceConfiguration.class);
            when(configuration.threadpool_name()).thenReturn("default");
            MethodUtils.invokeMethod(service, true, "modified", configuration);
            service.sendMessage(message);
        }
        { // deactivate
            MethodUtils.invokeMethod(service, true, "deactivate");
            exception.expect(NullPointerException.class);
            exception.expectMessage("Thread pool must not be null");
            service.sendMessage(message);
        }
    }

    private static final class DefaultThreadPool implements ThreadPool {

        @Override
        public void execute(Runnable runnable) {
            ForkJoinPool.commonPool().execute(runnable);
        }

        @Override
        public <T> Future<T> submit(Callable<T> callable) {
            return ForkJoinPool.commonPool().submit(callable);
        }

        @Override
        public Future<?> submit(Runnable runnable) {
            return ForkJoinPool.commonPool().submit(runnable);
        }

        @Override
        public String getName() {
            return "default";
        }

        @Override
        public ThreadPoolConfig getConfiguration() {
            return null;
        }

    }

}
