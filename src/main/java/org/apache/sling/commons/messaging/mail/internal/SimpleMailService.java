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

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.event.ConnectionListener;
import jakarta.mail.event.TransportListener;
import jakarta.mail.internet.MimeMessage;

import org.apache.sling.commons.crypto.CryptoService;
import org.apache.sling.commons.messaging.MessageService;
import org.apache.sling.commons.messaging.mail.MailService;
import org.apache.sling.commons.messaging.mail.MessageBuilder;
import org.apache.sling.commons.messaging.mail.MessageIdProvider;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolManager;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = {
        MessageService.class,
        MailService.class
    },
    property = {
        Constants.SERVICE_DESCRIPTION + "=Apache Sling Commons Messaging Mail – Simple Mail Service",
        Constants.SERVICE_VENDOR + "=The Apache Software Foundation",
        "protocol=SMTPS"
    }
)
@Designate(
    ocd = SimpleMailServiceConfiguration.class,
    factory = true
)
public final class SimpleMailService implements MailService {

    @Reference(
        cardinality = ReferenceCardinality.OPTIONAL,
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile MessageIdProvider messageIdProvider;

    @Reference(
        cardinality = ReferenceCardinality.MANDATORY,
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile ThreadPoolManager threadPoolManager;

    @Reference(
        cardinality = ReferenceCardinality.MANDATORY,
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile CryptoService cryptoService;

    @Reference(
        cardinality = ReferenceCardinality.MULTIPLE,
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile List<ConnectionListener> connectionListeners;

    @Reference(
        cardinality = ReferenceCardinality.MULTIPLE,
        policy = ReferencePolicy.DYNAMIC,
        policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile List<TransportListener> transportListeners;

    private ThreadPool threadPool;

    private SimpleMailServiceConfiguration configuration;

    private Session session;

    private static final String SMTPS_PROTOCOL = "smtps";

    // https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html

    private static final String MAIL_SMTPS_FROM = "mail.smtps.from";

    private static final String MESSAGE_ID_HEADER = "Message-ID";

    private final Logger logger = LoggerFactory.getLogger(SimpleMailService.class);

    public SimpleMailService() { //
    }

    @Activate
    private void activate(final SimpleMailServiceConfiguration configuration) {
        logger.debug("activating");
        this.configuration = configuration;
        configure(configuration);
    }

    @Modified
    private void modified(final SimpleMailServiceConfiguration configuration) {
        logger.debug("modifying");
        this.configuration = configuration;
        configure(configuration);
    }

    @Deactivate
    private void deactivate() {
        logger.debug("deactivating");
        this.configuration = null;
        threadPoolManager.release(threadPool);
        threadPool = null;
        session = null;
    }

    private void configure(final SimpleMailServiceConfiguration configuration) {
        threadPoolManager.release(threadPool);
        threadPool = threadPoolManager.get(configuration.threadpool_name());

        final Properties properties = new Properties();
        final String from = configuration.mail_smtps_from();
        if (Objects.nonNull(from) && !from.isBlank()) {
            properties.setProperty(MAIL_SMTPS_FROM, from.trim());
        }

        session = Session.getInstance(properties);
    }

    @Override
    public @NotNull MessageBuilder getMessageBuilder() {
        return new SimpleMessageBuilder(session);
    }

    @Override
    public @NotNull CompletableFuture<Void> sendMessage(@NotNull final MimeMessage message) {
        return CompletableFuture.runAsync(() -> send(message), runnable -> threadPool.submit(runnable));
    }

    private void send(@NotNull final MimeMessage message) {
        try {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            final String password = cryptoService.decrypt(configuration.password());
            try (final Transport transport = session.getTransport(SMTPS_PROTOCOL)) {
                final List<ConnectionListener> connectionListeners = this.connectionListeners;
                connectionListeners.forEach(transport::addConnectionListener);
                final List<TransportListener> transportListeners = this.transportListeners;
                transportListeners.forEach(transport::addTransportListener);
                transport.connect(configuration.mail_smtps_host(), configuration.mail_smtps_port(), configuration.username(), password);
                message.saveChanges();
                final MessageIdProvider messageIdProvider = this.messageIdProvider;
                if (messageIdProvider != null) {
                    final String messageId = messageIdProvider.getMessageId(message);
                    message.setHeader(MESSAGE_ID_HEADER, String.format("<%s>", messageId));
                }
                logger.debug("sending message '{}'", message.getMessageID());
                transport.sendMessage(message, message.getAllRecipients());
            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        } catch (MessagingException e) {
            throw new CompletionException(e);
        }
    }

}
