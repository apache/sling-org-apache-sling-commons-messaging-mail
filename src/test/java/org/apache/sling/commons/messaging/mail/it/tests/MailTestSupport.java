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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.sling.testing.paxexam.SlingOptions;
import org.apache.sling.testing.paxexam.TestSupport;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.options.ModifiableCompositeOption;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;

import static org.apache.sling.testing.paxexam.SlingOptions.scr;
import static org.apache.sling.testing.paxexam.SlingOptions.slingCommonsThreads;
import static org.ops4j.pax.exam.CoreOptions.bootClasspathLibrary;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

public abstract class MailTestSupport extends TestSupport {

    @Inject
    protected BundleContext bundleContext;

    @Inject
    protected ConfigurationAdmin configurationAdmin;

    private final ITemplateEngine templateEngine = new TemplateEngine();

    protected ModifiableCompositeOption baseConfiguration() {
        return composite(
            super.baseConfiguration(),
            // Sling Commons Messaging Mail
            testBundle("bundle.filename"),
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.commons.messaging").versionAsInProject(),
            mavenBundle().groupId("jakarta.activation").artifactId("jakarta.activation-api").versionAsInProject(),
            mavenBundle().groupId("jakarta.mail").artifactId("jakarta.mail-api").versionAsInProject(),
            mavenBundle().groupId("com.sun.mail").artifactId("jakarta.mail").versionAsInProject(),
            mavenBundle().groupId("org.apache.commons").artifactId("commons-lang3").versionAsInProject(),
            scr(),
            slingCommonsCrypto(),
            slingCommonsThreads(),
            // testing
            junitBundles(),
            wrappedBundle(mavenBundle().groupId("com.google.truth").artifactId("truth").versionAsInProject()),
            mavenBundle().groupId("com.google.guava").artifactId("guava").versionAsInProject(),
            mavenBundle().groupId("com.google.guava").artifactId("failureaccess").versionAsInProject(),
            mavenBundle().groupId("com.googlecode.java-diff-utils").artifactId("diffutils").versionAsInProject(),
            mavenBundle().groupId("commons-io").artifactId("commons-io").versionAsInProject(),
            greenmail(),
            thymeleaf(),
            vmOption(System.getProperty("jacoco.command"))
        );
    }

    private static ModifiableCompositeOption greenmail() {
        final MavenArtifactProvisionOption greenmail = mavenBundle().groupId("com.icegreen").artifactId("greenmail").versionAsInProject();
        final MavenArtifactProvisionOption slf4j_api = mavenBundle().groupId("org.slf4j").artifactId("slf4j-api").versionAsInProject();
        final MavenArtifactProvisionOption slf4j_simple = mavenBundle().groupId("org.slf4j").artifactId("slf4j-simple").versionAsInProject();
        return composite(
            greenmail,
            // add GreenMail to boot classpath to allow setting ssl.SocketFactory.provider to GreenMail's DummySSLSocketFactory
            bootClasspathLibrary(greenmail).afterFramework(),
            bootClasspathLibrary(slf4j_api).afterFramework(), // GreenMail dependency
            bootClasspathLibrary(slf4j_simple).afterFramework() // GreenMail dependency
        );
    }

    private static ModifiableCompositeOption thymeleaf() {
        return composite(
            mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.thymeleaf").version(SlingOptions.versionResolver),
            mavenBundle().groupId("org.attoparser").artifactId("attoparser").version(SlingOptions.versionResolver),
            mavenBundle().groupId("org.unbescape").artifactId("unbescape").version(SlingOptions.versionResolver),
            mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.ognl").version(SlingOptions.versionResolver),
            mavenBundle().groupId("org.javassist").artifactId("javassist").version(SlingOptions.versionResolver)
        );
    }

    private static ModifiableCompositeOption slingCommonsCrypto() {
        return composite(
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.commons.crypto").versionAsInProject(),
            mavenBundle().groupId("org.apache.commons").artifactId("commons-lang3").versionAsInProject(),
            mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.jasypt").versionAsInProject()
        );
    }

    // helpers for attachments, inline objects and templates

    String getResourceAsString(final String path) throws IOException {
        try (final InputStream inputStream = getClass().getResourceAsStream(path)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    byte[] getResourceAsByteArray(final String path) throws IOException {
        try (final InputStream inputStream = getClass().getResourceAsStream(path)) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    String renderHtmlTemplate(final String path, final Map<String, Object> variables) throws IOException {
        return renderTemplate(path, variables, TemplateMode.HTML);
    }

    String renderTextTemplate(final String path, final Map<String, Object> variables) throws IOException {
        return renderTemplate(path, variables, TemplateMode.TEXT);
    }

    String renderTemplate(final String path, final Map<String, Object> variables, final TemplateMode templateMode) throws IOException {
        final String template = getResourceAsString(path);
        final IContext context = new Context(Locale.ENGLISH, variables);
        final TemplateSpec templateSpec = new TemplateSpec(template, templateMode);
        return templateEngine.process(templateSpec, context);
    }

}
