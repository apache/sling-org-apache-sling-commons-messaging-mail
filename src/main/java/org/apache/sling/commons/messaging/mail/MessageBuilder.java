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

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface MessageBuilder {

    @NotNull MessageBuilder header(@NotNull final String name, @Nullable final String value);

    @NotNull MessageBuilder headers(@NotNull final InternetHeaders headers);

    @NotNull MessageBuilder from(@NotNull final InternetAddress from);

    @NotNull MessageBuilder from(@NotNull final String address) throws AddressException;

    @NotNull MessageBuilder from(@NotNull final String address, @NotNull final String name) throws AddressException;

    @NotNull MessageBuilder to(@NotNull final InternetAddress to);

    @NotNull MessageBuilder to(@NotNull final String address) throws AddressException;

    @NotNull MessageBuilder to(@NotNull final String address, @NotNull final String name) throws AddressException;

    @NotNull MessageBuilder cc(@NotNull final InternetAddress cc);

    @NotNull MessageBuilder cc(@NotNull final String address) throws AddressException;

    @NotNull MessageBuilder cc(@NotNull final String address, @NotNull final String name) throws AddressException;

    @NotNull MessageBuilder bcc(@NotNull final InternetAddress bcc);

    @NotNull MessageBuilder bcc(@NotNull final String address) throws AddressException;

    @NotNull MessageBuilder bcc(@NotNull final String address, final String name) throws AddressException;

    @NotNull MessageBuilder replyTo(@NotNull final InternetAddress replyTo);

    @NotNull MessageBuilder replyTo(@NotNull final String address) throws AddressException;

    @NotNull MessageBuilder replyTo(@NotNull final String address, final String name) throws AddressException;

    @NotNull MessageBuilder subject(@NotNull final String subject);

    @NotNull MessageBuilder text(@NotNull final String text);

    @NotNull MessageBuilder html(@NotNull final String html);

    @NotNull MessageBuilder attachment(@NotNull final byte[] content, @NotNull final String type, @NotNull final String filename);

    @NotNull MessageBuilder attachment(@NotNull final byte[] content, @NotNull final String type, @NotNull final String filename, @Nullable Header[] headers);

    @NotNull MessageBuilder inline(@NotNull final byte[] content, @NotNull final String type, @NotNull final String cid);

    @NotNull MessageBuilder inline(@NotNull final byte[] content, @NotNull final String type, @NotNull final String cid, @Nullable Header[] headers);

    @NotNull MimeMessage build() throws MessagingException;

}
