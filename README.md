[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-commons-messaging-mail/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-messaging-mail/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-messaging-mail/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-commons-messaging-mail/job/master/test_results_analyzer/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Commons Messaging Mail

This module is part of the [Apache Sling](https://sling.apache.org) project.

Provide an OSGi Configuration for `SimpleMailBuilder` or a custom `MailBuilder` to send messages using [Apache Commons Email](https://commons.apache.org/proper/commons-email/).

To extend or override `SimpleMailBuilder`​s configuration call `MessageService#send(String, String, Map):Future<Result>` and supply a configuration map `mail` within the third parameter:

```
{
  "mail" : {
    "mail.subject": <String>,
    "mail.from": <String>,
    "mail.smtp.hostname": <String>,
    "mail.smtp.port": <int>,
    "mail.smtp.username": <String>,
    "mail.smtp.password": <String>
  }
}
```
