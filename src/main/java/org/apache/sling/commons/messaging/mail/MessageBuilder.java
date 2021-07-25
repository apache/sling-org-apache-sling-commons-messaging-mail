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

import jakarta.mail.Address;
import jakarta.mail.Header;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.InternetHeaders;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Builder for MIME messages.
 *
 * @see MimeMessage
 */
@ProviderType
public interface MessageBuilder {

    // header(s)

    /**
     * Creates a new header from given name and value and adds it to the list of headers.
     *
     * @param name the name of the header
     * @param value the value of the header
     * @return the message builder
     * @see Header
     * @see InternetHeaders
     */
    public abstract @NotNull MessageBuilder header(@NotNull final String name, @Nullable final String value);

    /**
     * Adds the given headers to the list of headers.
     *
     * @param headers the headers to add
     * @return the message builder
     * @see Header
     * @see InternetHeaders
     */
    public abstract @NotNull MessageBuilder headers(@NotNull final Collection<Header> headers);

    // from

    /**
     * Sets the given address for the RFC 822 <code>From</code> header field.
     *
     * @param from the address
     * @return the message builder
     * @see MimeMessage#setFrom(Address)
     */
    public abstract @NotNull MessageBuilder from(@NotNull final InternetAddress from);

    /**
     * Creates a new <code>InternetAddress</code> from given address and sets it for the RFC 822 <code>From</code> header field.
     *
     * @param address the address
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setFrom(Address)
     */
    public abstract @NotNull MessageBuilder from(@NotNull final String address) throws AddressException;

    /**
     * Creates a new <code>InternetAddress</code> from given address and name and sets it for the RFC 822 <code>From</code> header field.
     *
     * @param address the address
     * @param name the (personal) name
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setFrom(Address)
     */
    public abstract @NotNull MessageBuilder from(@NotNull final String address, @NotNull final String name) throws AddressException;

    // to

    /**
     * Adds the given address to the list of primary (<code>to</code>) recipients.
     *
     * @param to the address
     * @return the message builder
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#TO
     */
    public abstract @NotNull MessageBuilder to(@NotNull final InternetAddress to);

    /**
     * Creates a new <code>InternetAddress</code> from given address and adds it to the list of primary (<code>to</code>) recipients.
     *
     * @param address the address
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#TO
     */
    public abstract @NotNull MessageBuilder to(@NotNull final String address) throws AddressException;

    /**
     * Creates a new <code>InternetAddress</code> from given address and name and adds it to the list of primary (<code>to</code>) recipients.
     *
     * @param address the address
     * @param name the (personal) name
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#TO
     */
    public abstract @NotNull MessageBuilder to(@NotNull final String address, @NotNull final String name) throws AddressException;

    /**
     * Adds the given addresses to the list of primary (<code>to</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#TO
     */
    public abstract @NotNull MessageBuilder to(@NotNull final InternetAddress[] addresses);

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of primary (<code>to</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#TO
     */
    public abstract @NotNull MessageBuilder to(@NotNull final String[] addresses) throws AddressException;

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of primary (<code>to</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#TO
     */
    public abstract @NotNull MessageBuilder to(@NotNull final Collection<String> addresses) throws AddressException;

    // cc

    /**
     * Adds the given address to the list of carbon copy (<code>cc</code>) recipients.
     *
     * @param cc the address
     * @return the message builder
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#CC
     */
    public abstract @NotNull MessageBuilder cc(@NotNull final InternetAddress cc);

    /**
     * Creates a new <code>InternetAddress</code> from given address and adds it to the list of carbon copy (<code>cc</code>) recipients.
     *
     * @param address the address
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#CC
     */
    public abstract @NotNull MessageBuilder cc(@NotNull final String address) throws AddressException;

    /**
     * Creates a new <code>InternetAddress</code> from given address and name and adds it to the list of carbon copy (<code>cc</code>) recipients.
     *
     * @param address the address
     * @param name the (personal) name
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#CC
     */
    public abstract @NotNull MessageBuilder cc(@NotNull final String address, @NotNull final String name) throws AddressException;

    /**
     * Adds the given addresses to the list of carbon copy (<code>cc</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#CC
     */
    public abstract @NotNull MessageBuilder cc(@NotNull final InternetAddress[] addresses);

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of carbon copy (<code>cc</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#CC
     */
    public abstract @NotNull MessageBuilder cc(@NotNull final String[] addresses) throws AddressException;

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of carbon copy (<code>cc</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#CC
     */
    public abstract @NotNull MessageBuilder cc(@NotNull final Collection<String> addresses) throws AddressException;

    // bcc

    /**
     * Adds the given address to the list of blind carbon copy (<code>bcc</code>) recipients.
     *
     * @param bcc the address
     * @return the message builder
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#BCC
     */
    public abstract @NotNull MessageBuilder bcc(@NotNull final InternetAddress bcc);

    /**
     * Creates a new <code>InternetAddress</code> from given address and adds it to the list of blind carbon copy (<code>bcc</code>) recipients.
     *
     * @param address the address
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#BCC
     */
    public abstract @NotNull MessageBuilder bcc(@NotNull final String address) throws AddressException;

    /**
     * Creates a new <code>InternetAddress</code> from given address and name and adds it to the list of blind carbon copy (<code>bcc</code>) recipients.
     *
     * @param address the address
     * @param name the (personal) name
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#BCC
     */
    public abstract @NotNull MessageBuilder bcc(@NotNull final String address, final String name) throws AddressException;

    /**
     * Adds the given addresses to the list of blind carbon copy (<code>bcc</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#BCC
     */
    public abstract @NotNull MessageBuilder bcc(@NotNull final InternetAddress[] addresses);

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of blind carbon copy (<code>bcc</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#BCC
     */
    public abstract @NotNull MessageBuilder bcc(@NotNull final String[] addresses) throws AddressException;

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of blind carbon copy (<code>bcc</code>) recipients.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setRecipients(RecipientType, Address[])
     * @see RecipientType#BCC
     */
    public abstract @NotNull MessageBuilder bcc(@NotNull final Collection<String> addresses) throws AddressException;

    // replyTo

    /**
     * Adds the given address to the list of addresses for the RFC 822 <code>Reply-To</code> header field.
     *
     * @param replyTo the address
     * @return the message builder
     * @see MimeMessage#setReplyTo(Address[])
     */
    public abstract @NotNull MessageBuilder replyTo(@NotNull final InternetAddress replyTo);

    /**
     * Creates a new <code>InternetAddress</code> from given address and adds it to the list of addresses for the RFC 822 <code>Reply-To</code> header field.
     *
     * @param address the address
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setReplyTo(Address[])
     */
    public abstract @NotNull MessageBuilder replyTo(@NotNull final String address) throws AddressException;

    /**
     * Creates a new <code>InternetAddress</code> from given address and name and adds it to the list of addresses for the RFC 822 <code>Reply-To</code> header field.
     *
     * @param address the address
     * @param name the (personal) name
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address string fails
     * @see MimeMessage#setReplyTo(Address[])
     */
    public abstract @NotNull MessageBuilder replyTo(@NotNull final String address, final String name) throws AddressException;

    /**
     * Adds the given addresses to the list of addresses for the RFC 822 <code>Reply-To</code> header field.
     *
     * @param addresses the addresses
     * @return the message builder
     * @see MimeMessage#setReplyTo(Address[])
     */
    public abstract @NotNull MessageBuilder replyTo(@NotNull final InternetAddress[] addresses);

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of addresses for the RFC 822 <code>Reply-To</code> header field.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setReplyTo(Address[])
     */
    public abstract @NotNull MessageBuilder replyTo(@NotNull final String[] addresses) throws AddressException;

    /**
     * Creates new <code>InternetAddress</code>es from given addresses and adds them to the list of addresses for the RFC 822 <code>Reply-To</code> header field.
     *
     * @param addresses the addresses
     * @return the message builder
     * @throws AddressException if creating an <code>InternetAddress</code> from given address strings fails
     * @see MimeMessage#setReplyTo(Address[])
     */
    public abstract @NotNull MessageBuilder replyTo(@NotNull final Collection<String> addresses) throws AddressException;

    // subject

    /**
     * Sets the given subject into the <code>Subject</code> header field.
     *
     * @param subject the subject of the message
     * @return the message builder
     * @see MimeMessage#setSubject(String) 
     */
    public abstract @NotNull MessageBuilder subject(@NotNull final String subject);

    // text

    /**
     * Sets the plain text content.
     *
     * @param text the plain text content
     * @return the message builder
     * @see MimeMessage#setText(String, String)
     * @see MimeBodyPart#setContent(Object, String)
     */
    public abstract @NotNull MessageBuilder text(@NotNull final String text);

    // html

    /**
     * Sets the HTML content.
     *
     * @param html the HTML content
     * @return the message builder
     * @see MimeBodyPart#setContent(Object, String)
     */
    public abstract @NotNull MessageBuilder html(@NotNull final String html);

    // attachment

    /**
     * Attaches the given content to the message.
     *
     * @param content the content to attach
     * @param type the type of the content (content/media/MIME type)
     * @param filename the filename of the attachment
     * @return the message builder
     * @see Part#ATTACHMENT
     */
    public abstract @NotNull MessageBuilder attachment(final byte @NotNull [] content, @NotNull final String type, @NotNull final String filename);

    /**
     * Attaches the given content with headers to the message.
     *
     * @param content the content to attach
     * @param type the type of the content (content/media/MIME type)
     * @param filename the filename of the attachment
     * @param headers the headers for the content
     * @return the message builder
     * @see Part#ATTACHMENT
     */
    public abstract @NotNull MessageBuilder attachment(final byte @NotNull [] content, @NotNull final String type, @NotNull final String filename, @Nullable final Collection<Header> headers);

    // inline

    /**
     * Inlines the given content into the message.
     *
     * @param content the content to inline
     * @param type the type of the content (content/media/MIME type)
     * @param cid the content identifier (<code>Content-ID</code>)
     * @return the message builder
     * @see Part#INLINE
     */
    public abstract @NotNull MessageBuilder inline(final byte @NotNull [] content, @NotNull final String type, @NotNull final String cid);

    /**
     * Inlines the given content with headers into the message.
     *
     * @param content the content to inline
     * @param type the type of the content (content/media/MIME type)
     * @param cid the content identifier (<code>Content-ID</code>)
     * @param headers the headers for the content
     * @return the message builder
     * @see Part#INLINE
     */
    public abstract @NotNull MessageBuilder inline(final byte @NotNull [] content, @NotNull final String type, @NotNull final String cid, @Nullable final Collection<Header> headers);

    // build

    /**
     * Builds the MIME message with the given input.
     *
     * @return the built message
     * @throws MessagingException if building message fails
     */
    public abstract @NotNull MimeMessage build() throws MessagingException;

}
