# ADR-0004: General coding rules

- **Status:** Accepted
- **Date:** 2026-09-11
- **Deciders:** Burak Selim Şenyurt

## Context

Three recurring habits cause disproportionate pain in production:

- **`System.out.println`.** Lines written this way do not reach Payara's logging
  infrastructure. They cannot be levelled, filtered, correlated to a request, or
  turned off — and they tend to survive long past the debugging session that
  created them.
- **Field injection.** `@Inject` on a field hides the dependency from the
  constructor signature, which means the class cannot be instantiated without a
  container. A test that wants to exercise one method now needs CDI.
- **Generic exceptions.** `throw new RuntimeException("book not found")` forces
  the caller to either catch everything or catch nothing. The type carries no
  information, so the decision about how to react cannot be made in code.

## Options considered

1. **Checkstyle / PMD.** Good at line-level rules. Weak at questions of the form
   "which annotation sits on which field of which class", and it introduces a
   second rule language to learn and maintain.
2. **ArchUnit.** The rules are ordinary Java: the IDE completes them, refactoring
   tools rename through them, and they live next to the architecture rules that
   already exist.

## Decision

- `System.out` / `System.err` are not used; logging goes through SLF4J.
- `java.util.logging` is not used directly (the `slf4j-jdk14` bridge is wired at
  runtime instead).
- Generic exceptions (`Exception`, `RuntimeException`, `Throwable`) are not
  thrown; exceptions are named in the language of the domain.
- Dependencies are injected through the constructor. `jakarta.inject.Inject` may
  not be placed on a **field**.
  - **Exception:** `EntityManager` injection via `@PersistenceContext` on a field
    is allowed. It is the idiomatic form of resource injection in Jakarta EE, and
    constructor injection of an `EntityManager` is not practical there.
- Production code may not depend on any test library (`org.junit..`,
  `com.tngtech.archunit..`).

## Consequences

- Positive: all log output flows through one channel; classes can be constructed
  in a plain unit test; exception types carry enough information for a caller to
  act on them.
- Negative: the habit of dropping a quick `System.out.println` while debugging
  now breaks the build. This is deliberate, and it will annoy someone in their
  first week.

## Note on scoping a rule

ArchUnit ships a ready-made `GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION`,
and we deliberately do **not** use it: it also flags `@Resource` and would
therefore collide with the `@PersistenceContext` exception above. Instead a
custom `ArchCondition` checks only `jakarta.inject.Inject`.

Narrowing the scope of a rule is itself an architectural decision. It is written
down here so that the next person does not "fix" the test by swapping in the
built-in rule and then quietly adding an ignore pattern for `JpaBookRepository`.

## How this is enforced

| Rule | Test |
| --- | --- |
| No standard streams | `CodingRulesArchTest.adr0004_no_standard_streams` |
| No `java.util.logging` | `CodingRulesArchTest.adr0004_no_java_util_logging` |
| No generic exceptions | `CodingRulesArchTest.adr0004_no_generic_exceptions` |
| No field injection via `@Inject` | `CodingRulesArchTest.adr0004_no_field_injection` |
| No test libraries in production code | `CodingRulesArchTest.adr0004_no_test_libraries_in_production` |
