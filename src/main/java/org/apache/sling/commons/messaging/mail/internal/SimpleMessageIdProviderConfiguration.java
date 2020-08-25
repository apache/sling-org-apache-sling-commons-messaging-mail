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
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Apache Sling Commons Messaging Mail “Simple Message ID Provider”",
    description = "Service to provide a Message ID based on random UUID, timestamp in ms and custom host"
)
@SuppressWarnings("java:S100")
@interface SimpleMessageIdProviderConfiguration {

    @AttributeDefinition(
        name = "Names",
        description = "names of this service",
        required = false
    )
    String[] names() default {"default"};

    @AttributeDefinition(
        name = "Host",
        description = "Host to use in Message ID"
    )
    String host() default "localhost";

    String webconsole_configurationFactory_nameHint() default "{names} {host}";

}
