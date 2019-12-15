[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-commons-messaging-mail/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-messaging-mail/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-messaging-mail/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-messaging-mail/job/master/test_results_analyzer/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Commons Messaging Mail

This module is part of the [Apache Sling](https://sling.apache.org) project.

This module provides a simple layer on top of [Jakarta Mail](https://eclipse-ee4j.github.io/mail/) (former [JavaMail](https://javaee.github.io/javamail/)) including a message builder and a service to send mails via SMTPS.

* Mail Service: sends MIME messages.
* Message Builder: builds plain text and HTML messages with attachments and inline images 
* Message ID Provider: allows overwriting default message IDs by custom ones


## Example

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
        .attachment(attachment, "image/png", "attachment.png")
        .inline(inline, "image/png", "inline")
        .build();

    mailService.sendMessage(message);
```


## Integration Tests

Integration tests require a running SMTP server. By default a [GreenMail](http://www.icegreen.com/greenmail/) server is started.

An external SMTP server for validating messages with real mail clients can be used by setting required properties:

    mvn clean install\
      -Dsling.test.mail.smtps.server.external=true\
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

