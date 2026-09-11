# ADR-0001: Layered architecture and dependency direction

- **Status:** Accepted
- **Date:** 2026-09-11
- **Deciders:** Burak Selim Şenyurt

## Context

The `arch-guard-lab` module is packaged as a single deployment unit (one JAR/WAR).
Maven cannot enforce dependency direction for us: inside a single module every
package can see every other package, and the compiler is perfectly happy with a
REST endpoint that reaches straight into a repository.

We have lived with the consequences of that freedom before. In earlier projects,
business rules ended up spread across three places at once — a little validation
in the resource class, a little in the service, a little in the repository —
because nothing stopped the shortcut at the moment it was taken. By the time it
showed up in a code review, the shortcut was already load-bearing.

## Options considered

1. **One Maven module per layer.** The compiler enforces the boundary, which is
   the strongest guarantee available. The price is five modules and five POMs to
   maintain, plus a slower reactor build — disproportionate at this size.
2. **Rely on code review only.** No tooling cost. In practice it depends on who
   reviews and how tired they are; it degrades as the team grows.
3. **One module plus ArchUnit enforcement.** No effect on build structure or
   compile time, violations surface at `mvn test`, and the rationale for each
   rule lives inside the test itself via `.because(...)`.

We chose option 3, with option 1 kept in reserve: if a layer ever needs to be
reused by a second deployment unit, that layer graduates into its own module.

## Decision

The module uses five layers inside a single artifact: `api`, `application`,
`domain`, `persistence`, `config`. Dependencies flow inwards only:

- `api` may use `application` and `domain`.
- `application` may use `domain`.
- `domain` may use nothing outside itself.
- `persistence` may use `domain` and implements the ports declared there.
- `api` may **not** bypass `application` and reach `persistence` directly.
- No cyclic dependency may exist between packages.

Crossing a layer boundary happens through interfaces (ports) declared in `domain`.

## Consequences

- Positive: business rules have exactly one home; the persistence technology
  (JPA -> JDBC -> something else) can be replaced without touching `domain`;
  every layer can be tested in isolation.
- Negative: even a trivial CRUD endpoint requires the full
  `api -> application -> domain -> persistence` chain. "Just this once" shortcuts
  now break the build rather than the design — which is the point, but it is
  still a real cost on a deadline.

## How this is enforced

| Rule | Test |
| --- | --- |
| Layer access matrix | `LayeredArchitectureArchTest.adr0001_layer_access_matrix` |
| `domain` depends on no outer layer | `LayeredArchitectureArchTest.adr0001_domain_no_outer_dependency` |
| `api` does not bypass into `persistence` | `LayeredArchitectureArchTest.adr0001_api_persistence_no_bypass` |
| Packages are free of cycles | `LayeredArchitectureArchTest.adr0001_packages_no_cycles` |
