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

import java.util.Collection;

import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface MessageBuilder {

    public abstract @NotNull MessageBuilder header(@NotNull final String name, @Nullable final String value);

    public abstract @NotNull MessageBuilder headers(@NotNull final Collection<Header> headers);

    public abstract @NotNull MessageBuilder from(@NotNull final InternetAddress from);

    public abstract @NotNull MessageBuilder from(@NotNull final String address) throws AddressException;

    public abstract @NotNull MessageBuilder from(@NotNull final String address, @NotNull final String name) throws AddressException;

    public abstract @NotNull MessageBuilder to(@NotNull final InternetAddress to);

    public abstract @NotNull MessageBuilder to(@NotNull final String address) throws AddressException;

    public abstract @NotNull MessageBuilder to(@NotNull final String address, @NotNull final String name) throws AddressException;

    public abstract @NotNull MessageBuilder to(@NotNull final InternetAddress[] addresses);

    public abstract @NotNull MessageBuilder to(@NotNull final String[] addresses) throws AddressException;

    public abstract @NotNull MessageBuilder to(@NotNull final Collection<String> addresses) throws AddressException;

    public abstract @NotNull MessageBuilder cc(@NotNull final InternetAddress cc);

    public abstract @NotNull MessageBuilder cc(@NotNull final String address) throws AddressException;

    public abstract @NotNull MessageBuilder cc(@NotNull final String address, @NotNull final String name) throws AddressException;

    public abstract @NotNull MessageBuilder cc(@NotNull final InternetAddress[] addresses);

    public abstract @NotNull MessageBuilder cc(@NotNull final String[] addresses) throws AddressException;

    public abstract @NotNull MessageBuilder cc(@NotNull final Collection<String> addresses) throws AddressException;

    public abstract @NotNull MessageBuilder bcc(@NotNull final InternetAddress bcc);

    public abstract @NotNull MessageBuilder bcc(@NotNull final String address) throws AddressException;

    public abstract @NotNull MessageBuilder bcc(@NotNull final String address, final String name) throws AddressException;

    public abstract @NotNull MessageBuilder bcc(@NotNull final InternetAddress[] addresses);

    public abstract @NotNull MessageBuilder bcc(@NotNull final String[] addresses) throws AddressException;

    public abstract @NotNull MessageBuilder bcc(@NotNull final Collection<String> addresses) throws AddressException;

    public abstract @NotNull MessageBuilder replyTo(@NotNull final InternetAddress replyTo);

    public abstract @NotNull MessageBuilder replyTo(@NotNull final String address) throws AddressException;

    public abstract @NotNull MessageBuilder replyTo(@NotNull final String address, final String name) throws AddressException;

    public abstract @NotNull MessageBuilder replyTo(@NotNull final InternetAddress[] addresses);

    public abstract @NotNull MessageBuilder replyTo(@NotNull final String[] addresses) throws AddressException;

    public abstract @NotNull MessageBuilder replyTo(@NotNull final Collection<String> addresses) throws AddressException;

    public abstract @NotNull MessageBuilder subject(@NotNull final String subject);

    public abstract @NotNull MessageBuilder text(@NotNull final String text);

    public abstract @NotNull MessageBuilder html(@NotNull final String html);

    public abstract @NotNull MessageBuilder attachment(final byte @NotNull [] content, @NotNull final String type, @NotNull final String filename);

    public abstract @NotNull MessageBuilder attachment(final byte @NotNull [] content, @NotNull final String type, @NotNull final String filename, @Nullable final Collection<Header> headers);

    public abstract @NotNull MessageBuilder inline(final byte @NotNull [] content, @NotNull final String type, @NotNull final String cid);

    public abstract @NotNull MessageBuilder inline(final byte @NotNull [] content, @NotNull final String type, @NotNull final String cid, @Nullable final Collection<Header> headers);

    public abstract @NotNull MimeMessage build() throws MessagingException;

}
