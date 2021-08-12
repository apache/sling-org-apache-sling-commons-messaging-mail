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

import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleMessageIdProviderTest {

    @Test
    public void testComponentLifecycle() throws Exception {
        final MimeMessage message = mock(MimeMessage.class);
        final SimpleMessageIdProvider provider = new SimpleMessageIdProvider();
        { // activate
            final String host = "author.cms.example.org";
            final SimpleMessageIdProviderConfiguration configuration = mock(SimpleMessageIdProviderConfiguration.class);
            when(configuration.host()).thenReturn(host);
            MethodUtils.invokeMethod(provider, true, "activate", configuration);
            assertThat(provider.getMessageId(message)).endsWith("@".concat(host));
        }
        { // modified
            final String host = "publish.cms.example.org";
            final SimpleMessageIdProviderConfiguration configuration = mock(SimpleMessageIdProviderConfiguration.class);
            when(configuration.host()).thenReturn(host);
            MethodUtils.invokeMethod(provider, true, "modified", configuration);
            assertThat(provider.getMessageId(message)).endsWith("@".concat(host));
        }
        { // deactivate
            final String host = "publish.cms.example.org";
            MethodUtils.invokeMethod(provider, true, "deactivate");
            assertThat(provider.getMessageId(message)).endsWith("@".concat(host));
        }
    }

}
