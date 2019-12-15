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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Apache Sling Commons Messaging Mail “Simple Mail Service”",
    description = "Simple mail service sending MIME messages via SMTPS"
)
@interface SimpleMailServiceConfiguration {

    @AttributeDefinition(
        name = "Names",
        description = "names of this service",
        required = false
    )
    String[] names() default {"default"};

    @AttributeDefinition(
        name = "ThreadPool name",
        description = "name of the ThreadPool to use for sending mails"
    )
    String threadpool_name() default "default";

    @AttributeDefinition(
        name = "SMTP from",
        description = "from address"
    )
    String mail_smtps_from();

    @AttributeDefinition(
        name = "SMTP host",
        description = "host of SMTP server"
    )
    String mail_smtps_host() default "localhost";

    @AttributeDefinition(
        name = "SMTP port",
        description = "port of SMTP server"
    )
    int mail_smtps_port() default 465;

    @AttributeDefinition(
        name = "Username",
        description = "username for SMTP server"
    )
    String username();

    @AttributeDefinition(
        name = "Password",
        description = "password for SMTP server",
        type = AttributeType.PASSWORD
    )
    String password();

    @AttributeDefinition(
        name = "Message ID Provider target",
        description = "filter expression to target a Message ID Provider",
        required = false
    )
    String messageIdProvider_target();

    @AttributeDefinition(
        name = "Crypto Service target",
        description = "filter expression to target a Crypto Service",
        required = false
    )
    String cryptoService_target();

    @AttributeDefinition(
        name = "Transport Listeners target",
        description = "filter expression to target Transport Listeners",
        required = false
    )
    String transportListeners_target();

    String webconsole_configurationFactory_nameHint() default "{names} {username}@{mail_smtps_host}:{mail_smtps_port}";

}
