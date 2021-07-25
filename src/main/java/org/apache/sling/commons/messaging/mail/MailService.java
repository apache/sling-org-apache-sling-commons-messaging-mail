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
package org.apache.sling.commons.messaging.mail;

import java.util.concurrent.CompletableFuture;

import jakarta.mail.internet.MimeMessage;

import org.apache.sling.commons.messaging.MessageService;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Service for sending MIME messages.
 *
 * @see MimeMessage
 */
@ProviderType
public interface MailService extends MessageService<MimeMessage, Void> {

    /**
     * Provides the message builder for this service.
     *
     * @return the message builder for this service
     */
    public abstract @NotNull MessageBuilder getMessageBuilder();

    /**
     * Sends the given MIME message.
     *
     * @param message the MIME message to send
     * @return {@link java.util.concurrent.CompletableFuture} for signaling completion
     */
    public abstract @NotNull CompletableFuture<Void> sendMessage(@NotNull MimeMessage message);

}
