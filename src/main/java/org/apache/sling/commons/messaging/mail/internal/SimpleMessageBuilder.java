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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.sling.commons.messaging.mail.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleMessageBuilder implements MessageBuilder {

    private final Session session;

    private InternetHeaders headers = new InternetHeaders();

    private InternetAddress from;

    private List<InternetAddress> toRecipients = new LinkedList<>();

    private List<InternetAddress> ccRecipients = new LinkedList<>();

    private List<InternetAddress> bccRecipients = new LinkedList<>();

    private List<InternetAddress> replyTos = new LinkedList<>();

    private String subject;

    private String text;

    private String html;

    private List<Attachment> attachments = new LinkedList<>();

    private List<Inline> inlines = new LinkedList<>();

    private static final String CONTENT_TYPE_TEXT_HTML = "text/html; charset=utf-8";

    private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain; charset=utf-8";

    private static final String CHARSET_UTF8 = "utf-8";

    private static final String MULTIPART_SUBTYPE_MIXED = "mixed";

    private static final String MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";

    private static final String MULTIPART_SUBTYPE_RELATED = "related";

    SimpleMessageBuilder(@NotNull final Session session) {
        this.session = session;
    }

    @Override
    public @NotNull MessageBuilder header(@NotNull final String name, @Nullable final String value) {
        headers.setHeader(name, value);
        return this;
    }

    @Override
    public @NotNull MessageBuilder headers(@NotNull final InternetHeaders headers) {
        while (headers.getAllHeaders().hasMoreElements()) {
            final Header header = headers.getAllHeaders().nextElement();
            this.headers.setHeader(header.getName(), header.getValue());
        }
        return this;
    }

    @Override
    public @NotNull MessageBuilder from(@NotNull final InternetAddress from) {
        this.from = from;
        return this;
    }

    @Override
    public @NotNull MessageBuilder from(@NotNull final String address) throws AddressException {
        final InternetAddress from = new InternetAddress(address);
        return from(from);
    }

    @Override
    public @NotNull MessageBuilder from(@NotNull final String address, @NotNull final String name) throws AddressException {
        final InternetAddress from = new InternetAddress(address);
        try {
            from.setPersonal(name, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            //
        }
        return from(from);
    }

    @Override
    public @NotNull MessageBuilder to(@NotNull final InternetAddress to) {
        toRecipients.add(to);
        return this;
    }

    @Override
    public @NotNull MessageBuilder to(@NotNull final String address) throws AddressException {
        final InternetAddress to = new InternetAddress(address);
        return to(to);
    }

    @Override
    public @NotNull MessageBuilder to(@NotNull final String address, @NotNull final String name) throws AddressException {
        final InternetAddress to = new InternetAddress(address);
        try {
            to.setPersonal(name, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            //
        }
        return to(to);
    }

    @Override
    public @NotNull MessageBuilder cc(@NotNull final InternetAddress cc) {
        ccRecipients.add(cc);
        return this;
    }

    @Override
    public @NotNull MessageBuilder cc(@NotNull final String address) throws AddressException {
        final InternetAddress cc = new InternetAddress(address);
        return cc(cc);
    }

    @Override
    public @NotNull MessageBuilder cc(@NotNull final String address, @NotNull final String name) throws AddressException {
        final InternetAddress cc = new InternetAddress(address);
        try {
            cc.setPersonal(name, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            //
        }
        return cc(cc);
    }

    @Override
    public @NotNull MessageBuilder bcc(@NotNull final InternetAddress bcc) {
        bccRecipients.add(bcc);
        return this;
    }

    @Override
    public @NotNull MessageBuilder bcc(@NotNull final String address) throws AddressException {
        final InternetAddress bcc = new InternetAddress(address);
        return bcc(bcc);
    }

    public @NotNull MessageBuilder bcc(@NotNull final String address, final String name) throws AddressException {
        final InternetAddress bcc = new InternetAddress(address);
        try {
            bcc.setPersonal(name, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            //
        }
        return bcc(bcc);
    }

    @Override
    public @NotNull MessageBuilder replyTo(@NotNull final InternetAddress replyTo) {
        replyTos.add(replyTo);
        return this;
    }

    @Override
    public @NotNull MessageBuilder replyTo(@NotNull final String address) throws AddressException {
        final InternetAddress replyTo = new InternetAddress(address);
        return replyTo(replyTo);
    }

    @Override
    public @NotNull MessageBuilder replyTo(@NotNull final String address, @NotNull final String name) throws AddressException {
        final InternetAddress replyTo = new InternetAddress(address);
        try {
            replyTo.setPersonal(name, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            //
        }
        return replyTo(replyTo);
    }

    @Override
    public @NotNull MessageBuilder subject(@NotNull final String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public @NotNull MessageBuilder text(@NotNull final String text) {
        this.text = text;
        return this;
    }

    @Override
    public @NotNull MessageBuilder html(@NotNull final String html) {
        this.html = html;
        return this;
    }

    @Override
    public @NotNull MessageBuilder attachment(@NotNull final byte[] content, @NotNull final String type, @NotNull final String filename) {
        return attachment(content, type, filename, null);
    }

    @Override
    public @NotNull MessageBuilder attachment(@NotNull final byte[] content, @NotNull final String type, @NotNull final String filename, @Nullable Header[] headers) {
        final Attachment attachment = new Attachment(content, type, filename, null);
        this.attachments.add(attachment);
        return this;
    }

    @Override
    public @NotNull MessageBuilder inline(@NotNull final byte[] content, @NotNull final String type, @NotNull final String cid) {
        return inline(content, type, cid, null);
    }

    @Override
    public @NotNull MessageBuilder inline(@NotNull final byte[] content, @NotNull final String type, @NotNull final String cid, @Nullable Header[] headers) {
        final Inline inline = new Inline(content, type, cid, headers);
        this.inlines.add(inline);
        return this;
    }

    private InternetHeaders headers() {
        return headers;
    }

    private InternetAddress from() {
        return from;
    }

    private List<InternetAddress> to() {
        return toRecipients;
    }

    private List<InternetAddress> cc() {
        return ccRecipients;
    }

    private List<InternetAddress> bcc() {
        return bccRecipients;
    }

    private List<InternetAddress> replyTo() {
        return replyTos;
    }

    private String subject() {
        return subject;
    }

    private String text() {
        return text;
    }

    private String html() {
        return html;
    }

    private List<Attachment> attachments() {
        return attachments;
    }

    private List<Inline> inlines() {
        return inlines;
    }

    private boolean hasText() {
        return text() != null;
    }

    private boolean hasHtml() {
        return html() != null;
    }

    private boolean hasAttachments() {
        return !attachments().isEmpty();
    }

    private boolean hasInlines() {
        return !inlines().isEmpty();
    }

    public @NotNull MimeMessage build() throws MessagingException {
        final MimeMessage message = new MimeMessage(session);

        while (headers().getAllHeaders().hasMoreElements()) {
            final Header header = headers.getAllHeaders().nextElement();
            message.setHeader(header.getName(), header.getValue());
        }

        message.setFrom(from());
        message.setRecipients(Message.RecipientType.TO, to().toArray(new Address[0]));
        message.setRecipients(Message.RecipientType.CC, cc().toArray(new Address[0]));
        message.setRecipients(Message.RecipientType.BCC, bcc().toArray(new Address[0]));
        message.setReplyTo(replyTos.toArray(new Address[0]));
        message.setSubject(subject(), StandardCharsets.UTF_8.name());

        if (hasHtml() || hasAttachments() || hasInlines()) {
            final MimeMultipart content = new MimeMultipart(MULTIPART_SUBTYPE_MIXED);

            if (hasText() && hasHtml()) { // text and html
                final MimeMultipart alternative = new MimeMultipart(MULTIPART_SUBTYPE_ALTERNATIVE);
                handleHtmlAndInlines(alternative, html(), inlines());
                addText(alternative, text());
                final MimeBodyPart part = new MimeBodyPart();
                part.setContent(alternative);
                content.addBodyPart(part);
            } else if (hasHtml()) { // html only
                handleHtmlAndInlines(content, html(), inlines());
            } else { // text only
                addText(content, text());
            }

            addAttachments(content, attachments);

            message.setContent(content);
        } else {
            message.setText(text(), CHARSET_UTF8);
        }
        return message;
    }

    private static void handleHtmlAndInlines(final MimeMultipart parent, final String html, final List<Inline> inlines) throws MessagingException {
        if (!inlines.isEmpty()) { // html and inlines
            final MimeMultipart related = new MimeMultipart(MULTIPART_SUBTYPE_RELATED);
            addHtml(related, html);
            addInlines(related, inlines);
            final MimeBodyPart part = new MimeBodyPart();
            part.setContent(related);
            parent.addBodyPart(part);
        } else { // html
            addHtml(parent, html);
        }
    }

    private static void addText(final MimeMultipart parent, final String text) throws MessagingException {
        final MimeBodyPart part = new MimeBodyPart();
        part.setContent(text, CONTENT_TYPE_TEXT_PLAIN);
        parent.addBodyPart(part);
    }

    private static void addHtml(final MimeMultipart parent, final String html) throws MessagingException {
        final MimeBodyPart part = new MimeBodyPart();
        part.setContent(html, CONTENT_TYPE_TEXT_HTML);
        parent.addBodyPart(part);
    }

    private static void addAttachments(final MimeMultipart parent, final List<Attachment> attachments) throws MessagingException {
        for (final Attachment attachment : attachments) {
            try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment.content)) {
                final MimeBodyPart part = new MimeBodyPart();
                part.setDisposition(Part.ATTACHMENT);
                part.setFileName(attachment.filename);
                setDataHandler(part, inputStream, attachment.type);
                if (attachment.headers != null) {
                    setHeaders(part, attachment.headers);
                }
                parent.addBodyPart(part);
            } catch (Exception e) {
                final String message = String.format("Adding attachment failed: %s", attachment.filename);
                throw new MessagingException(message, e);
            }
        }
    }

    private static void addInlines(final MimeMultipart parent, final List<Inline> inlines) throws MessagingException {
        for (final Inline inline : inlines) {
            try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(inline.content)) {
                final MimeBodyPart part = new MimeBodyPart();
                part.setDisposition(Part.INLINE);
                part.setContentID(String.format("<%s>", inline.cid));
                setDataHandler(part, inputStream, inline.type);
                if (inline.headers != null) {
                    setHeaders(part, inline.headers);
                }
                parent.addBodyPart(part);
            } catch (Exception e) {
                final String message = String.format("Adding inline object failed: %s", inline.cid);
                throw new MessagingException(message, e);
            }
        }
    }

    private static void setDataHandler(final MimeBodyPart part, final InputStream inputStream, final String type) throws MessagingException, IOException {
        final DataSource source = new ByteArrayDataSource(inputStream, type);
        final DataHandler handler = new DataHandler(source);
        part.setDataHandler(handler);
    }

    private static void setHeaders(final MimeBodyPart part, final Header[] headers) throws MessagingException {
        for (final Header header : headers) {
            part.setHeader(header.getName(), header.getValue());
        }
    }

    private static class Attachment {

        final byte[] content;

        final String type;

        final String filename;

        final Header[] headers;

        Attachment(@NotNull final byte[] content, @NotNull final String type, @NotNull final String filename, @Nullable final Header[] headers) {
            this.content = content;
            this.type = type;
            this.filename = filename;
            this.headers = headers;
        }

    }

    private static class Inline {

        final byte[] content;

        final String type;

        final String cid;

        final Header[] headers;

        Inline(@NotNull final byte[] content, @NotNull final String type, @NotNull final String cid, @Nullable final Header[] headers) {
            this.content = content;
            this.type = type;
            this.cid = cid;
            this.headers = headers;
        }

    }

}