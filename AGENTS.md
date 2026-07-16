# Project overview

Apache Sling Commons Messaging Mail is an OSGi bundle that provides a `MailService` API for sending MIME messages over SMTPS. It exposes three public service interfaces (`MailService`, `MessageBuilder`, `MessageIdProvider`) and ships concrete implementations (`SimpleMailService`, `SimpleMessageBuilder`, `SimpleMessageIdProvider`) as OSGi Declarative Services components. The bundle depends on `sling.commons.messaging`, `sling.commons.crypto` (for password decryption), and `sling.commons.threads` for async dispatch, and uses OSGi-compatible Jakarta Mail / Activation APIs. Sending is always asynchronous via `CompletableFuture`.

# Core commands

```bash
# Build and run all checks (Checkstyle, PMD, SpotBugs) + unit and integration tests
mvn clean verify

# Skip integration tests (faster local iteration)
mvn clean verify -DskipITs

# Run only unit tests
mvn test

# Run a single unit test class
mvn test -Dtest=SimpleMailServiceTest

# Run integration tests only
mvn failsafe:integration-test failsafe:verify

# Run a single integration test class
mvn failsafe:integration-test -Dit.test=SimpleMailServiceIT

# Run Checkstyle only
mvn checkstyle:check

# Run PMD only
mvn pmd:check

# Run SpotBugs only
mvn spotbugs:check
```

No dev server — this is a library bundle, not a standalone application.

# Project layout

```
pom.xml                          Maven build descriptor
bnd.bnd                          OSGi bundle manifest extras
checkstyle-suppressions.xml      Checkstyle suppression rules
pmd-exclude.properties           PMD false-positive exclusions
spotbugs-exclude.xml             SpotBugs exclusion filters

src/
  main/java/org/apache/sling/commons/messaging/mail/
    MailService.java             Public API: send MimeMessage async
    MessageBuilder.java          Public API: build MimeMessage
    MessageIdProvider.java       Public API: custom Message-ID generation
    package-info.java
    internal/
      SimpleMailService.java             Factory OSGi component (SMTPS sender)
      SimpleMailServiceConfiguration.java  @ObjectClassDefinition metatype
      SimpleMessageBuilder.java          Default message builder
      SimpleMessageIdProvider.java       Default message-ID provider
      SimpleMessageIdProviderConfiguration.java
      package-info.java

  test/java/
    org/apache/commons/mail/util/
      MimeMessageParser.java     Test utility (vendored)
    org/apache/sling/commons/messaging/mail/internal/
      SimpleMailServiceTest.java       Unit tests (Mockito)
      SimpleMessageBuilderTest.java
      SimpleMessageIdProviderTest.java
    org/apache/sling/commons/messaging/mail/it/tests/
      MailTestSupport.java             Pax Exam base class
      SimpleMailServiceIT.java         OSGi integration tests

  test/resources/
    mockito-extensions/          Mockito plugin config
    password                     Encrypted test password
    *.html / *.txt / *.png       Email template fixtures
```

# Development patterns & constraints

- **Java 17**, OSGi R7. All source and target set via `sling.java.version=17`; CI also validates on Java 21.
- **OSGi DS annotations only** — use `org.osgi.service.component.annotations.*`. Never use Felix SCR annotations.
- `SimpleMailService` is a **factory component** (`@Designate(factory=true)`). Multiple instances can be registered with different SMTP servers.
- All `@Reference` fields that can change at runtime are declared `volatile` with `DYNAMIC` policy and `GREEDY` option.
- Password stored encrypted; always decrypt via `CryptoService.decrypt()` — never store plaintext passwords.
- Jakarta APIs are consumed in OSGi-compatible form (`jakarta.mail-api` and ServiceMix Activation API).
- Public API interfaces carry `@ProviderType` — do not add default methods without a version bump.
- Nullability: annotate with `@NotNull`/`@Nullable` from `org.jetbrains.annotations`.
- Every source file must have the Apache 2.0 license header. `apache-rat-plugin` enforces this at build time.
- Configuration property names use underscores for dots (OSGi metatype convention): `mail_smtps_host`, `threadpool_name`.

# Git workflow

- Follow the [Apache Sling contribution guidelines](https://sling.apache.org/contributing.html).
- Branch names: `feature/<SLING-XXXXX>-short-description` or `fix/<SLING-XXXXX>-short-description`.
- Commit messages: start with the JIRA issue key, e.g. `SLING-12345 Fix NPE in SimpleMailService`.
- PRs target the `master` branch on GitHub; CI runs via Jenkins (`Jenkinsfile` at repo root).
- Do not push directly to `master`.

# Testing guidelines

- **Framework**: JUnit 4 + Mockito 5 for unit tests; Pax Exam 4 + GreenMail for OSGi integration tests.
- Unit tests live in `src/test/java/.../mail/internal/` and are named `*Test.java`.
- Integration tests live in `src/test/java/.../mail/it/tests/` and are named `*IT.java`. They run inside a real OSGi framework (Apache Felix) provisioned by Pax Exam.
- GreenMail provides a local mock SMTP server for integration tests — no external mail server required.
- Integration tests can also run against an external SMTP server via `-Dsling.test.mail.smtps.server.external=true` and related `sling.test.mail.*` system properties.
- Test resources (templates, images, encrypted password) are in `src/test/resources/`.
- Coverage is not enforced by a specific threshold; rely on code review.

# Gotchas

- **TCCL swap**: `SimpleMailService.send()` replaces the thread context classloader before `Session.getTransport()` to ensure the Jakarta Mail provider is found. Forgetting this causes `NoProviderException` inside OSGi.
- **Password is always encrypted**: the `password` configuration value is ciphertext. Passing a plaintext password to `SimpleMailServiceConfiguration` in tests requires mocking `CryptoService.decrypt()` to return it as-is.
- **Integration tests require Maven local repo**: Pax Exam downloads bundles via `pax-url-aether`. Build must be run with network access or a pre-populated local repo on first run.
- **`redirectTestOutputToFile=true`**: failsafe redirects integration test output to `target/failsafe-reports/`. Check there, not the console, when debugging IT failures.
- **Checkstyle config is external**: the `configLocation` is `checks.xml` resolved from the Checkstyle dependency `de.bildschirmarbeiter:checkstyle:3`, not a file in this repo. Do not add a local `checks.xml`.
- **`spotless` is disabled** (`<skip>true</skip>`) — do not rely on it for formatting.

# Security

<!-- sling-security-default:start -->
The threat model for this project is https://github.com/apache/sling/blob/master/docs/threat-model.md .
<!-- sling-security-default:end -->
