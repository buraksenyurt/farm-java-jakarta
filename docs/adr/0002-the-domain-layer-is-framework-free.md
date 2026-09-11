# ADR-0002: The domain layer is framework-free

- **Status:** Accepted
- **Date:** 2026-09-11
- **Deciders:** Burak Selim Şenyurt

## Context

Using the domain model directly as a JPA entity saves code on day one. It also
hands the design of that model to the ORM: lazy loading, detached instances, the
`equals`/`hashCode` contract under a generated identifier, and the requirement
for a no-argument constructor all start dictating how the model may be written.
A value object with an invariant enforced in its constructor — exactly what
`Isbn` is here - is awkward to express once JPA owns the class.

The second-order effect matters more than the first: once the domain class is a
JPA entity, testing a business rule requires an `EntityManager`, which requires a
persistence unit, which requires a database. A rule that could have been verified
in one millisecond now needs a container.

## Options considered

1. **One model (domain class = JPA entity).** Less code, faster start. The domain
   design surrenders to ORM constraints and unit tests need infrastructure.
2. **Two models with mapping (`Book <-> BookEntity`).** Costs explicit mapping
   code in the persistence layer. The domain stays plain Java, is constructed
   with `new`, and is tested without any infrastructure at all.

## Decision

No class in `com.lectures.archguard.domain` may depend on `jakarta..`, `javax..`,
`org.slf4j..`, `com.fasterxml..` or `org.flywaydb..`.

JPA entities are declared only in the `persistence` package, and `EntityManager`
is used only there. Mapping between the domain model and the entity model is the
persistence layer's responsibility.

## Consequences

- Positive: domain tests run in milliseconds with no container; the ORM can be
  replaced; domain classes stay free of annotation and serialisation noise.
- Negative: `toDomain()` / `fromDomain()` mapping code has to be written and kept
  in sync. When a field is added, two places change instead of one.

## How this is enforced

| Rule | Test |
| --- | --- |
| Domain depends on no framework package | `DomainPurityArchTest.adr0002_domain_framework_free` |
| `@Entity` only in `persistence` | `DomainPurityArchTest.adr0002_jpa_only_in_persistence` |
| `EntityManager` only in `persistence` | `DomainPurityArchTest.adr0002_entitymanager_only_in_persistence` |

> Note on rule design: these rules use `dependOnClassesThat()`, not `accessClassesThat()`. An annotation is not an access  `accessClassesThat()` would silently miss `@Entity` on a domain class, which is the single most likely violation of this ADR.
