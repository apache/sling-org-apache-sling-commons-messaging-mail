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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SimpleMessageBuilderTest {

    private Map<String, InternetAddress> addresses;

    static final Properties properties = new Properties();

    @Before
    public void setUp() throws Exception {
        addresses = new HashMap<>();
        addresses.put("a", new InternetAddress("a@example.org", "a"));
        addresses.put("b", new InternetAddress("b@example.org", "b"));
        addresses.put("c", new InternetAddress("c@example.org", "c"));
        addresses.put("d", new InternetAddress("d@example.org", "d"));
        addresses.put("e", new InternetAddress("e@example.org", "e"));
    }

    @Test
    public void testHeader() throws Exception {
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.header("a", "1");
        builder.header("b", "2");
        builder.header("c", "3");
        final MimeMessage message = builder.build();
        final String[] a = message.getHeader("a");
        assertThat(a).hasLength(1);
        assertThat(a[0]).isEqualTo("1");
        final String[] b = message.getHeader("b");
        assertThat(b).hasLength(1);
        assertThat(b[0]).isEqualTo("2");
        final String[] c = message.getHeader("c");
        assertThat(c).hasLength(1);
        assertThat(c[0]).isEqualTo("3");
    }

    @Test
    public void testHeaders() throws Exception {
        final Map<String, String> headers = new HashMap<>();
        headers.put("a", "1");
        headers.put("b", "2");
        headers.put("c", "3");
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.headers(headers);
        final MimeMessage message = builder.build();
        final String[] a = message.getHeader("a");
        assertThat(a).hasLength(1);
        assertThat(a[0]).isEqualTo("1");
        final String[] b = message.getHeader("b");
        assertThat(b).hasLength(1);
        assertThat(b[0]).isEqualTo("2");
        final String[] c = message.getHeader("c");
        assertThat(c).hasLength(1);
        assertThat(c[0]).isEqualTo("3");
    }

    @Test
    public void testFromInternetAddress() throws Exception {
        final InternetAddress address = addresses.get("a");
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.from(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress from = parser.getFrom();
        assertThat(from).isEqualTo(address);
    }

    @Test
    public void testFromAddress() throws Exception {
        final String address = addresses.get("a").getAddress();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.from(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress from = parser.getFrom();
        assertThat(from.getAddress()).isEqualTo(address);
    }

    @Test
    public void testFromAddressName() throws Exception {
        final String address = addresses.get("a").getAddress();
        final String name = addresses.get("a").getPersonal();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.from(address, name);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress from = parser.getFrom();
        assertThat(from.getAddress()).isEqualTo(address);
        assertThat(from.getPersonal()).isEqualTo(name);
    }

    // single to

    @Test
    public void testToInternetAddress() throws Exception {
        final InternetAddress address = addresses.get("a");
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.to(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress to = (InternetAddress) parser.getTo().get(0);
        assertThat(to).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(1);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testToAddress() throws Exception {
        final String address = addresses.get("a").getAddress();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.to(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress to = (InternetAddress) parser.getTo().get(0);
        assertThat(to.getAddress()).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(1);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testToAddressName() throws Exception {
        final String address = addresses.get("a").getAddress();
        final String name = addresses.get("a").getPersonal();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.to(address, name);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress to = (InternetAddress) parser.getTo().get(0);
        assertThat(to.getAddress()).isEqualTo(address);
        assertThat(to.getPersonal()).isEqualTo(name);
        assertThat(parser.getTo()).hasSize(1);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    // multiple to

    @Test
    public void testToInternetAddressArray() throws Exception {
        final InternetAddress[] addresses = {
            this.addresses.get("a"),
            this.addresses.get("b"),
            this.addresses.get("c")
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.to(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getTo().get(0);
        final InternetAddress b = (InternetAddress) parser.getTo().get(1);
        final InternetAddress c = (InternetAddress) parser.getTo().get(2);
        assertThat(a).isEqualTo(addresses[0]);
        assertThat(b).isEqualTo(addresses[1]);
        assertThat(c).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(3);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testToAddressArray() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.to(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getTo().get(0);
        final InternetAddress b = (InternetAddress) parser.getTo().get(1);
        final InternetAddress c = (InternetAddress) parser.getTo().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(3);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testToAddressCollection() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.to(Arrays.asList(addresses));
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getTo().get(0);
        final InternetAddress b = (InternetAddress) parser.getTo().get(1);
        final InternetAddress c = (InternetAddress) parser.getTo().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(3);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    // single cc

    @Test
    public void testCcInternetAddress() throws Exception {
        final InternetAddress address = addresses.get("a");
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.cc(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress cc = (InternetAddress) parser.getCc().get(0);
        assertThat(cc).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(1);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testCcAddress() throws Exception {
        final String address = addresses.get("a").getAddress();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.cc(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress cc = (InternetAddress) parser.getCc().get(0);
        assertThat(cc.getAddress()).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(1);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testCcAddressName() throws Exception {
        final String address = addresses.get("a").getAddress();
        final String name = addresses.get("a").getPersonal();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.cc(address, name);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress cc = (InternetAddress) parser.getCc().get(0);
        assertThat(cc.getAddress()).isEqualTo(address);
        assertThat(cc.getPersonal()).isEqualTo(name);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(1);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    // multiple cc

    @Test
    public void testCcInternetAddressArray() throws Exception {
        final InternetAddress[] addresses = {
            this.addresses.get("a"),
            this.addresses.get("b"),
            this.addresses.get("c")
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.cc(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getCc().get(0);
        final InternetAddress b = (InternetAddress) parser.getCc().get(1);
        final InternetAddress c = (InternetAddress) parser.getCc().get(2);
        assertThat(a).isEqualTo(addresses[0]);
        assertThat(b).isEqualTo(addresses[1]);
        assertThat(c).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(3);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testCcAddressArray() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.cc(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getCc().get(0);
        final InternetAddress b = (InternetAddress) parser.getCc().get(1);
        final InternetAddress c = (InternetAddress) parser.getCc().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(3);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testCcAddressCollection() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.cc(Arrays.asList(addresses));
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getCc().get(0);
        final InternetAddress b = (InternetAddress) parser.getCc().get(1);
        final InternetAddress c = (InternetAddress) parser.getCc().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(3);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    // single bcc

    @Test
    public void testBccInternetAddress() throws Exception {
        final InternetAddress address = addresses.get("a");
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.bcc(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress bcc = (InternetAddress) parser.getBcc().get(0);
        assertThat(bcc).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(1);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testBccAddress() throws Exception {
        final String address = addresses.get("a").getAddress();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.bcc(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress bcc = (InternetAddress) parser.getBcc().get(0);
        assertThat(bcc.getAddress()).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(1);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testBccAddressName() throws Exception {
        final String address = addresses.get("a").getAddress();
        final String name = addresses.get("a").getPersonal();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.bcc(address, name);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress bcc = (InternetAddress) parser.getBcc().get(0);
        assertThat(bcc.getAddress()).isEqualTo(address);
        assertThat(bcc.getPersonal()).isEqualTo(name);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(1);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    // multiple bcc

    @Test
    public void testBccInternetAddressArray() throws Exception {
        final InternetAddress[] addresses = {
            this.addresses.get("a"),
            this.addresses.get("b"),
            this.addresses.get("c")
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.bcc(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getBcc().get(0);
        final InternetAddress b = (InternetAddress) parser.getBcc().get(1);
        final InternetAddress c = (InternetAddress) parser.getBcc().get(2);
        assertThat(a).isEqualTo(addresses[0]);
        assertThat(b).isEqualTo(addresses[1]);
        assertThat(c).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(3);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testBccAddressArray() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.bcc(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getBcc().get(0);
        final InternetAddress b = (InternetAddress) parser.getBcc().get(1);
        final InternetAddress c = (InternetAddress) parser.getBcc().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(3);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    @Test
    public void testBccAddressCollection() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.bcc(Arrays.asList(addresses));
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getBcc().get(0);
        final InternetAddress b = (InternetAddress) parser.getBcc().get(1);
        final InternetAddress c = (InternetAddress) parser.getBcc().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(3);
        assertThat(parser.getReplyTo()).hasSize(0);
    }

    // single replyTo

    @Test
    public void testReplyToInternetAddress() throws Exception {
        final InternetAddress address = addresses.get("a");
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.replyTo(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress replyTo = (InternetAddress) parser.getReplyTo().get(0);
        assertThat(replyTo).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(1);
    }

    @Test
    public void testReplyToAddress() throws Exception {
        final String address = addresses.get("a").getAddress();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.replyTo(address);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress replyTo = (InternetAddress) parser.getReplyTo().get(0);
        assertThat(replyTo.getAddress()).isEqualTo(address);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(1);
    }

    @Test
    public void testReplyToAddressName() throws Exception {
        final String address = addresses.get("a").getAddress();
        final String name = addresses.get("a").getPersonal();
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.replyTo(address, name);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress replyTo = (InternetAddress) parser.getReplyTo().get(0);
        assertThat(replyTo.getAddress()).isEqualTo(address);
        assertThat(replyTo.getPersonal()).isEqualTo(name);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(1);
    }

    // multiple replyTo

    @Test
    public void testReplyToInternetAddressArray() throws Exception {
        final InternetAddress[] addresses = {
            this.addresses.get("a"),
            this.addresses.get("b"),
            this.addresses.get("c")
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.replyTo(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getReplyTo().get(0);
        final InternetAddress b = (InternetAddress) parser.getReplyTo().get(1);
        final InternetAddress c = (InternetAddress) parser.getReplyTo().get(2);
        assertThat(a).isEqualTo(addresses[0]);
        assertThat(b).isEqualTo(addresses[1]);
        assertThat(c).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(3);
    }

    @Test
    public void testReplyToAddressArray() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.replyTo(addresses);
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getReplyTo().get(0);
        final InternetAddress b = (InternetAddress) parser.getReplyTo().get(1);
        final InternetAddress c = (InternetAddress) parser.getReplyTo().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(3);
    }

    @Test
    public void testReplyToAddressCollection() throws Exception {
        final String[] addresses = {
            this.addresses.get("a").getAddress(),
            this.addresses.get("b").getAddress(),
            this.addresses.get("c").getAddress()
        };
        final Session session = Session.getInstance(properties);
        final SimpleMessageBuilder builder = new SimpleMessageBuilder(session);
        builder.replyTo(Arrays.asList(addresses));
        final MimeMessage message = builder.build();
        final MimeMessageParser parser = new MimeMessageParser(message).parse();
        final InternetAddress a = (InternetAddress) parser.getReplyTo().get(0);
        final InternetAddress b = (InternetAddress) parser.getReplyTo().get(1);
        final InternetAddress c = (InternetAddress) parser.getReplyTo().get(2);
        assertThat(a.getAddress()).isEqualTo(addresses[0]);
        assertThat(b.getAddress()).isEqualTo(addresses[1]);
        assertThat(c.getAddress()).isEqualTo(addresses[2]);
        assertThat(parser.getTo()).hasSize(0);
        assertThat(parser.getCc()).hasSize(0);
        assertThat(parser.getBcc()).hasSize(0);
        assertThat(parser.getReplyTo()).hasSize(3);
    }

}
