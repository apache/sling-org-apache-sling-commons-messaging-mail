[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-messaging-mail/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-messaging-mail/job/master/)&#32;[![Test Status](https://img.shields.io/jenkins/tests.svg?jobUrl=https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-messaging-mail/job/master/)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-commons-messaging-mail/job/master/test/?width=800&height=600)&#32;[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-commons-messaging-mail&metric=coverage)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-commons-messaging-mail)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-commons-messaging-mail&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-commons-messaging-mail)&#32;[![JavaDoc](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.commons.messaging.mail.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.commons.messaging.mail)&#32;[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.commons.messaging.mail/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.commons.messaging.mail%22) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Commons Messaging Mail

This module is part of the [Apache Sling](https://sling.apache.org) project.

This module provides a simple layer on top of [Jakarta Mail](https://eclipse-ee4j.github.io/mail/) 2.0 (package `jakarta.mail`) including a message builder and a service to send mails via SMTPS.

* Mail Service: sends MIME messages
* Message Builder: builds plain text and HTML messages with attachments and inline images 
* Message ID Provider: allows overwriting default message IDs by custom ones


## Examples

### Configuration


#### MailService

Example factory configuration ([`SimpleMailServiceConfiguration`](https://github.com/apache/sling-org-apache-sling-commons-messaging-mail/blob/master/src/main/java/org/apache/sling/commons/messaging/mail/internal/SimpleMailServiceConfiguration.java)) for [`SimpleMailService`](https://github.com/apache/sling-org-apache-sling-commons-messaging-mail/blob/master/src/main/java/org/apache/sling/commons/messaging/mail/internal/SimpleMailService.java):

```
{
  "mail.smtps.from": "envelope-from@example.org",
  "mail.smtps.host": "smtp.example.org",
  "mail.smtps.port": 465,
  "username": "SMTP-USERNAME-PLAIN",
  "password": "SMTP-PASSWORD-ENCRYPTED",
  "messageIdProvider.target": "(names=hostname)"
}
```

#### Optional MessageIdProvider

Example factory configuration ([`SimpleMessageIdProviderConfiguration`](https://github.com/apache/sling-org-apache-sling-commons-messaging-mail/blob/master/src/main/java/org/apache/sling/commons/messaging/mail/internal/SimpleMessageIdProviderConfiguration.java)) for optional [`SimpleMessageIdProvider`](https://github.com/apache/sling-org-apache-sling-commons-messaging-mail/blob/master/src/main/java/org/apache/sling/commons/messaging/mail/internal/SimpleMessageIdProvider.java):

```
{
  "names": [
    "hostname"
  ],
  "host": "author.cms.example.org"
}
```

### Usage

Create a multipart MIME message with an attachment (`filename`: `song.flac`) where the HTML part contains an inline image (`cid`: `ska`) and send it:

```
    @Reference
    MailService mailService;

    String subject = "Rudy, A Message to You";
    String text = "Stop your messing around\nBetter think of your future\nTime you straighten right out\nCreating problems in town\n…";
    String html = […];
    byte[] attachment = […];
    byte[] inline = […];

    MimeMessage message = mailService.getMessageBuilder()
        .from("dandy.livingstone@kingston.jamaica.example.net", "Dandy Livingstone")
        .to("the.specials@coventry.england.example.net", "The Specials")
        .replyTo("rocksteady@jamaica.example.net");
        .subject(subject)
        .text(text)
        .html(html)
        .attachment(attachment, "audio/flac", "song.flac")
        .inline(inline, "image/png", "ska")
        .build();

    mailService.sendMessage(message);
```

## Dependencies

* [Sling Commons Messaging](https://github.com/apache/sling-org-apache-sling-commons-messaging) (API)
* [Sling Commons Crypto](https://github.com/apache/sling-org-apache-sling-commons-crypto) (for decrypting encrypted SMTP passwords)
* [Sling Commons Threads](https://github.com/apache/sling-org-apache-sling-commons-threads)
* [Jakarta Mail 2.0](https://jakarta.ee/specifications/mail/2.0/) and [Jakarta Activation 2.0](https://jakarta.ee/specifications/activation/2.0/) (*OSGified*, e.g. `org.apache.servicemix.specs.activation-api-2.0.1`)

## Integration Tests

Integration tests require a running SMTP server. By default a [GreenMail](http://www.icegreen.com/greenmail/) server is started.

An external SMTP server for validating messages with real mail clients can be used by setting required properties:

    mvn clean install\
      -Dsling.test.mail.smtps.server.external=true\
      -Dsling.test.mail.smtps.ssl.checkserveridentity=true\
      -Dsling.test.mail.smtps.from=envelope-from@example.org\
      -Dsling.test.mail.smtps.host=localhost\
      -Dsling.test.mail.smtps.port=465\
      -Dsling.test.mail.smtps.username=username\
      -Dsling.test.mail.smtps.password=password\
      -Dsling.test.mail.from.address=from@example.org\
      -Dsling.test.mail.from.name=From\ Sender\
      -Dsling.test.mail.to.address=to@example.org\
      -Dsling.test.mail.to.name=To\ Recipient\
      -Dsling.test.mail.replyTo.address=replyto@example.org\
      -Dsling.test.mail.replyTo.name=Reply\ To
