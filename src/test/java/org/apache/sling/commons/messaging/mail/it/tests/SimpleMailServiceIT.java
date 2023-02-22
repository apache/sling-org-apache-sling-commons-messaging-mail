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
package org.apache.sling.commons.messaging.mail.it.tests;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import jakarta.mail.Header;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.event.ConnectionEvent;
import jakarta.mail.event.ConnectionListener;
import jakarta.mail.event.TransportEvent;
import jakarta.mail.event.TransportListener;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.sling.commons.messaging.MessageService;
import org.apache.sling.commons.messaging.mail.MailService;
import org.apache.sling.commons.messaging.mail.MessageBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.propagateSystemProperties;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.factoryConfiguration;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SimpleMailServiceIT extends MailTestSupport {

    private static final boolean local = !Boolean.getBoolean("sling.test.mail.smtps.server.external");

    private static InternetAddress from;

    private static InternetAddress to;

    private static InternetAddress cc;

    private static InternetAddress bcc;

    private static InternetAddress replyTo;

    static {
        final String from_address = local ? "from@example.org" : System.getProperty("sling.test.mail.from.address");
        final String from_name = local ? "From Name" : System.getProperty("sling.test.mail.from.name");
        final String to_address = local ? "to@example.org" : System.getProperty("sling.test.mail.to.address");
        final String to_name = local ? "To Name" : System.getProperty("sling.test.mail.to.name");
        final String replyTo_address = local ? "replyto@example.org" : System.getProperty("sling.test.mail.replyTo.address");
        final String replyTo_name = local ? "ReplyTo Name" : System.getProperty("sling.test.mail.replyTo.name");
        try {
            from = new InternetAddress(from_address, from_name);
            to = new InternetAddress(to_address, to_name);
            replyTo = new InternetAddress(replyTo_address, replyTo_name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private GreenMail greenMail;

    @Inject
    protected MessageService<MimeMessage, Void> messageService;

    @Inject
    @Filter(value = "(protocol=SMTPS)")
    protected MailService mailService;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Logger logger = LoggerFactory.getLogger(SimpleMailServiceIT.class);

    @Configuration
    public Option[] configuration() {
        final int port = findFreePort();
        final String path = String.format("%s/src/test/resources/password", PathUtils.getBaseDir());
        return options(
            baseConfiguration(),
            propagateSystemProperties(
                "sling.test.mail.smtps.server.external",
                "sling.test.mail.smtps.ssl.checkserveridentity",
                "sling.test.mail.smtps.from",
                "sling.test.mail.smtps.host",
                "sling.test.mail.smtps.port",
                "sling.test.mail.smtps.username",
                "sling.test.mail.smtps.password",
                "sling.test.mail.from.address",
                "sling.test.mail.from.name",
                "sling.test.mail.to.address",
                "sling.test.mail.to.name",
                "sling.test.mail.replyTo.address",
                "sling.test.mail.replyTo.name"
            ),
            factoryConfiguration("org.apache.sling.commons.messaging.mail.internal.SimpleMessageIdProvider")
                .put("host", "localhost")
                .asOption(),
            factoryConfiguration("org.apache.sling.commons.messaging.mail.internal.SimpleMailService")
                .put("mail.smtps.ssl.checkserveridentity", local ? "false" : System.getProperty("sling.test.mail.smtps.ssl.checkserveridentity"))
                .put("mail.smtps.from", local ? "envelope-from@example.org" : System.getProperty("sling.test.mail.smtps.from"))
                .put("mail.smtps.host", local ? "localhost" : System.getProperty("sling.test.mail.smtps.host"))
                .put("mail.smtps.port", local ? port : Integer.getInteger("sling.test.mail.smtps.port"))
                .put("username", local ? "username" : System.getProperty("sling.test.mail.smtps.username"))
                .put("password", local ? "OEKPFL5cVJRqVjh4QaDZhvBiqv8wgWBMJ8PGbYHTqev046oV6888mna9w1mIGCXK" : System.getProperty("sling.test.mail.smtps.password"))
                .put("connectionListeners.target", "(is=used)")
                .put("transportListeners.target", "(is=used)")
                .asOption(),
            // Commons Crypto
            factoryConfiguration("org.apache.sling.commons.crypto.jasypt.internal.JasyptStandardPbeStringCryptoService")
                .put("algorithm", "PBEWITHHMACSHA512ANDAES_256")
                .asOption(),
            factoryConfiguration("org.apache.sling.commons.crypto.jasypt.internal.JasyptRandomIvGeneratorRegistrar")
                .put("algorithm", "SHA1PRNG")
                .asOption(),
            factoryConfiguration("org.apache.sling.commons.crypto.internal.FilePasswordProvider")
                .put("path", path)
                .asOption()
        );
    }

    @Before
    public void setUp() throws Exception {
        logger.info("local server : {}", local);
        if (local && Objects.isNull(greenMail)) {
            // set up GreenMail server
            Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
            final org.osgi.service.cm.Configuration[] configurations = configurationAdmin.listConfigurations("(service.factoryPid=org.apache.sling.commons.messaging.mail.internal.SimpleMailService)");
            final org.osgi.service.cm.Configuration configuration = configurations[0];
            final int port = (int) configuration.getProperties().get("mail.smtps.port");
            final ServerSetup serverSetup = new ServerSetup(port, "127.0.0.1", "smtps");
            greenMail = new GreenMail(serverSetup);
            greenMail.setUser("username", "password");
            greenMail.start();
        }
    }

    @After
    public void tearDown() {
        if (local) {
            greenMail.stop();
            greenMail = null;
        }
    }

    private MessageBuilder initializeMessageBuilder() {
        return mailService.getMessageBuilder()
            .from(from)
            .to(to)
            .replyTo(replyTo);
    }

    @Test
    public void testMessageService() throws ExecutionException, InterruptedException {
        exception.expect(ExecutionException.class);
        assertThat(messageService, notNullValue());
        final Properties properties = new Properties();
        final Session session = Session.getDefaultInstance(properties);
        final MimeMessage message = new MimeMessage(session);
        final CompletableFuture<Void> future = messageService.sendMessage(message);
        future.get();
    }

    @Test
    public void testMailService() {
        assertThat(mailService, notNullValue());
    }

    @Test
    public void testSendTextMessage() throws Exception {
        final Map<String, Object> variables = Collections.singletonMap("date", new Date());
        final String subject = "Sling Commons Mail: Text [æåëęïįœøüū] \uD83D\uDCE7";
        final String text = renderTextTemplate("/template.txt", variables);
        final MimeMessage message = initializeMessageBuilder()
            .subject(subject)
            .text(text)
            .build();

        final CompletableFuture<Void> future = mailService.sendMessage(message);
        future.get();

        if (local) {
            greenMail.waitForIncomingEmail(1);
            greenMail.getReceivedMessagesForDomain(to.getAddress());
            final MimeMessage[] messages = greenMail.getReceivedMessages();
            final MimeMessage received = messages[0];
            final MimeMessageParser parser = new MimeMessageParser(message).parse();

            assertThat(received.getMessageID(), endsWith("@localhost>"));
            assertThat(received.getSubject(), is(subject));
            assertThat(received.getFrom()[0], is(from));
            assertThat(received.getRecipients(Message.RecipientType.TO)[0], is(to));
            assertThat(received.getReplyTo()[0], is(replyTo));

            assertThat(parser.getPlainContent(), is(text));

            assertThat(parser.getAttachmentList(), empty());
            assertThat(parser.getContentIds(), empty());
        }
    }

    @Test
    public void testSendTextAndAttachmentMessage() throws Exception {
        final Map<String, Object> variables = Collections.singletonMap("date", new Date());
        final String subject = "Sling Commons Mail: Text and Attachment [æåëęïįœøüū] \uD83D\uDCE7";
        final String text = renderTextTemplate("/template.txt", variables);
        final byte[] support = getResourceAsByteArray("/SupportApache-small.png");
        final MimeMessage message = initializeMessageBuilder()
            .subject(subject)
            .text(text)
            .attachment(support, "image/png", "SupportApache-small.png")
            .build();

        final CompletableFuture<Void> future = mailService.sendMessage(message);
        future.get();

        if (local) {
            greenMail.waitForIncomingEmail(1);
            final MimeMessage[] messages = greenMail.getReceivedMessages();
            final MimeMessage received = messages[0];
            final MimeMessageParser parser = new MimeMessageParser(message).parse();

            assertThat(received.getMessageID(), endsWith("@localhost>"));
            assertThat(received.getSubject(), is(subject));
            assertThat(received.getFrom()[0], is(from));
            assertThat(received.getRecipients(Message.RecipientType.TO)[0], is(to));
            assertThat(received.getReplyTo()[0], is(replyTo));

            assertThat(parser.getPlainContent(), is(text));

            assertThat(parser.getAttachmentList().get(0).getName(), is("SupportApache-small.png"));
            assertThat(parser.getContentIds(), empty());
        }
    }

    @Test
    public void testSendHtmlMessage() throws Exception {
        final Map<String, Object> variables = Collections.singletonMap("date", new Date());
        final String subject = "Sling Commons Mail: HTML [æåëęïįœøüū] \uD83D\uDCE7";
        final String html = renderHtmlTemplate("/template.html", variables);
        final MimeMessage message = initializeMessageBuilder()
            .subject(subject)
            .html(html)
            .build();

        final CompletableFuture<Void> future = mailService.sendMessage(message);
        future.get();

        if (local) {
            greenMail.waitForIncomingEmail(1);
            final MimeMessage[] messages = greenMail.getReceivedMessages();
            final MimeMessage received = messages[0];
            final MimeMessageParser parser = new MimeMessageParser(message).parse();

            assertThat(received.getMessageID(), endsWith("@localhost>"));
            assertThat(received.getSubject(), is(subject));
            assertThat(received.getFrom()[0], is(from));
            assertThat(received.getRecipients(Message.RecipientType.TO)[0], is(to));
            assertThat(received.getReplyTo()[0], is(replyTo));

            assertThat(parser.getHtmlContent(), is(html));

            assertThat(parser.getAttachmentList(), empty());
            assertThat(parser.getContentIds(), empty());
        }
    }

    @Test
    public void testSendHtmlAndAttachmentMessage() throws Exception {
        final Map<String, Object> variables = Collections.singletonMap("date", new Date());
        final String subject = "Sling Commons Mail: HTML and Attachment [æåëęïįœøüū] \uD83D\uDCE7";
        final String html = renderHtmlTemplate("/template.html", variables);
        final byte[] support = getResourceAsByteArray("/SupportApache-small.png");
        final MimeMessage message = initializeMessageBuilder()
            .subject(subject)
            .html(html)
            .attachment(support, "image/png", "SupportApache-small.png")
            .build();

        final CompletableFuture<Void> future = mailService.sendMessage(message);
        future.get();

        if (local) {
            greenMail.waitForIncomingEmail(1);
            final MimeMessage[] messages = greenMail.getReceivedMessages();
            final MimeMessage received = messages[0];
            final MimeMessageParser parser = new MimeMessageParser(message).parse();

            assertThat(received.getMessageID(), endsWith("@localhost>"));
            assertThat(received.getSubject(), is(subject));
            assertThat(received.getFrom()[0], is(from));
            assertThat(received.getRecipients(Message.RecipientType.TO)[0], is(to));
            assertThat(received.getReplyTo()[0], is(replyTo));

            assertThat(parser.getHtmlContent(), is(html));

            assertThat(parser.getAttachmentList().get(0).getName(), is("SupportApache-small.png"));
            assertThat(parser.getContentIds(), empty());
        }
    }

    @Test
    public void testSendHtmlWithInlineImageAndAttachmentMessage() throws Exception {
        final Map<String, Object> variables = Collections.singletonMap("date", new Date());
        final String subject = "Sling Commons Mail: HTML with Inline Images and Attachment [æåëęïįœøüū] \uD83D\uDCE7";
        final String html = renderHtmlTemplate("/template-inlines.html", variables);
        final byte[] sling = getResourceAsByteArray("/sling.png");
        final byte[] support = getResourceAsByteArray("/SupportApache-small.png");
        final MimeMessage message = initializeMessageBuilder()
            .subject(subject)
            .html(html)
            .attachment(support, "image/png", "SupportApache-small.png")
            .inline(sling, "image/png", "sling")
            .build();

        final CompletableFuture<Void> future = mailService.sendMessage(message);
        future.get();

        if (local) {
            greenMail.waitForIncomingEmail(1);
            final MimeMessage[] messages = greenMail.getReceivedMessages();
            final MimeMessage received = messages[0];
            final MimeMessageParser parser = new MimeMessageParser(message).parse();

            assertThat(received.getMessageID(), endsWith("@localhost>"));
            assertThat(received.getSubject(), is(subject));
            assertThat(received.getFrom()[0], is(from));
            assertThat(received.getRecipients(Message.RecipientType.TO)[0], is(to));
            assertThat(received.getReplyTo()[0], is(replyTo));

            assertThat(parser.getHtmlContent(), is(html));

            assertThat(parser.getContentIds(), hasItem("sling"));
        }
    }

    @Test
    public void testSendHtmlWithInlineImageAndTextAndAttachmentMessage() throws Exception {
        final Map<String, Object> variables = Collections.singletonMap("date", new Date());
        final String subject = "Sling Commons Mail: HTML with Inline Images and Text and Attachment [æåëęïįœøüū] \uD83D\uDCE7";
        final String text = renderTextTemplate("/template.txt", variables);
        final String html = renderHtmlTemplate("/template-inlines.html", variables);
        final byte[] sling = getResourceAsByteArray("/sling.png");
        final byte[] support = getResourceAsByteArray("/SupportApache-small.png");
        final MimeMessage message = initializeMessageBuilder()
            .subject(subject)
            .text(text)
            .html(html)
            .attachment(support, "image/png", "SupportApache-small.png", Collections.singleton(new Header("X-Attachment", "Apache Software Foundation")))
            .inline(sling, "image/png", "sling", Collections.singleton(new Header("X-Inline", "Apache Sling")))
            .build();

        final CompletableFuture<Void> future = mailService.sendMessage(message);
        future.get();

        if (local) {
            greenMail.waitForIncomingEmail(1);
            final MimeMessage[] messages = greenMail.getReceivedMessages();
            final MimeMessage received = messages[0];
            final MimeMessageParser parser = new MimeMessageParser(message).parse();

            assertThat(received.getMessageID(), endsWith("@localhost>"));
            assertThat(received.getSubject(), is(subject));
            assertThat(received.getFrom()[0], is(from));
            assertThat(received.getRecipients(Message.RecipientType.TO)[0], is(to));
            assertThat(received.getReplyTo()[0], is(replyTo));

            assertThat(parser.getPlainContent(), is(text));
            assertThat(parser.getHtmlContent(), is(html));

            assertThat(parser.getContentIds(), hasItem("sling"));

            final MimeMultipart content = (MimeMultipart) received.getContent();
            assertThat(content.getContentType(), startsWith("multipart/mixed"));

            final MimeBodyPart alternative = (MimeBodyPart) content.getBodyPart(0);
            assertThat(alternative.getContentType(), startsWith("multipart/alternative"));

            final MimeBodyPart related = (MimeBodyPart) ((MimeMultipart) alternative.getContent()).getBodyPart(1);
            assertThat(related.getContentType(), startsWith("multipart/related"));

            final MimeBodyPart inline = (MimeBodyPart) ((MimeMultipart) related.getContent()).getBodyPart(1);
            assertThat(inline.getContentType(), is("image/png"));
            assertThat(inline.getHeader("X-Inline"), arrayWithSize(1));
            assertThat(inline.getHeader("X-Inline")[0], is("Apache Sling"));

            final MimeBodyPart attachment = (MimeBodyPart) content.getBodyPart(1);
            assertThat(attachment.getContentType(), is("image/png; name=SupportApache-small.png"));
            assertThat(attachment.getHeader("X-Attachment"), arrayWithSize(1));
            assertThat(attachment.getHeader("X-Attachment")[0], is("Apache Software Foundation"));
        }
    }

    @Test
    public void testListeners() throws Exception {
        final Dictionary<String, String> propertiesUsed = new Hashtable<>();
        propertiesUsed.put("is", "used");
        final RecordingConnectionListener usedConnectionListener = new RecordingConnectionListener();
        final ServiceRegistration<ConnectionListener> usedCLSR = bundleContext.registerService(ConnectionListener.class, usedConnectionListener, propertiesUsed);
        final NoopTransportListener usedTransportListener = new NoopTransportListener();
        final ServiceRegistration<TransportListener> usedTLSR = bundleContext.registerService(TransportListener.class, usedTransportListener, propertiesUsed);

        final Dictionary<String, String> propertiesUnused = new Hashtable<>();
        propertiesUnused.put("is", "unused");
        final RecordingConnectionListener unusedConnectionListener = new RecordingConnectionListener();
        final ServiceRegistration<ConnectionListener> unusedCLSR = bundleContext.registerService(ConnectionListener.class, unusedConnectionListener, propertiesUnused);
        final NoopTransportListener unusedTransportListener = new NoopTransportListener();
        final ServiceRegistration<TransportListener> unusedTLSR = bundleContext.registerService(TransportListener.class, unusedTransportListener, propertiesUnused);

        final MimeMessage message = initializeMessageBuilder()
            .subject("Sling Commons Mail: Testing Listeners")
            .text("Testing Connection and Transport Listeners")
            .build();

        final CompletableFuture<Void> future = mailService.sendMessage(message);
        future.get();

        assertThat(unusedCLSR.getReference().getUsingBundles(), nullValue());
        assertThat(unusedTLSR.getReference().getUsingBundles(), nullValue());

        assertThat(usedCLSR.getReference().getUsingBundles(), arrayWithSize(1));
        assertThat(usedTLSR.getReference().getUsingBundles(), arrayWithSize(1));

        assertThat(usedCLSR.getReference().getUsingBundles()[0].getSymbolicName(), is("org.apache.sling.commons.messaging.mail"));
        assertThat(usedTLSR.getReference().getUsingBundles()[0].getSymbolicName(), is("org.apache.sling.commons.messaging.mail"));

        assertThat(usedConnectionListener.opened, hasSize(1));
        assertThat(usedConnectionListener.closed, hasSize(1));
    }

    private static class RecordingConnectionListener implements ConnectionListener {

        public List<ConnectionEvent> opened = new ArrayList<>();

        public List<ConnectionEvent> disconnected = new ArrayList<>();

        public List<ConnectionEvent> closed = new ArrayList<>();

        public RecordingConnectionListener() {
        }

        @Override
        public void opened(final ConnectionEvent connectionEvent) {
            opened.add(connectionEvent);
        }

        @Override
        public void disconnected(final ConnectionEvent connectionEvent) {
            disconnected.add(connectionEvent);
        }

        @Override
        public void closed(final ConnectionEvent connectionEvent) {
            closed.add(connectionEvent);
        }

    }

    private static class NoopTransportListener implements TransportListener {

        public NoopTransportListener() {
        }

        @Override
        public void messageDelivered(final TransportEvent transportEvent) {
        }

        @Override
        public void messageNotDelivered(final TransportEvent transportEvent) {
        }

        @Override
        public void messagePartiallyDelivered(final TransportEvent transportEvent) {
        }

    }

}
