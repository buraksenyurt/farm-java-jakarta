# ADR-0003: Naming and annotation conventions

- **Status:** Accepted
- **Date:** 2026-09-11
- **Deciders:** Burak Selim Şenyurt

## Context

A class name should tell the reader which layer the class belongs to and how it
is meant to be used, before they open the file. Beyond readability there is a
mechanical reason: most ArchUnit rules are expressed over name and package
patterns. Without a naming convention there is nothing for a rule to bind to,
and ADR-0001 and ADR-0002 lose much of their reach.

The `Impl` suffix deserves a specific mention. `BookRepositoryImpl` says only
"this is the implementation", which is information the compiler already has.
`JpaBookRepository` says *how* it is implemented, which is the thing a reader
actually needs — and it leaves room for `InMemoryBookRepository` to exist beside
it without a numbered suffix.

## Options considered

1. **Free naming, enforced by review.** Flexible; no rules can be written on top
   of it and consistency erodes over time.
2. **Conventions plus automated enforcement.** One discussion up front, then the
   convention maintains itself.

## Decision

- Every class annotated with `@Path` is named `*Resource` and lives in `api`.
- Every class named `*Resource` lives in `api`.
- Every class in `application` is named `*Service` and is annotated with
  `@ApplicationScoped`.
- Implementations of domain ports live in `persistence` and are named
  `*Repository`.
- No class name ends in `Impl`; the implementation class is named after *how* it
  works (`JpaBookRepository`, `InMemoryBookRepository`).

## Consequences

- Positive: a class's role is readable from its name alone; ArchUnit rules can be
  written against name patterns; new team members place new classes correctly
  without asking.
- Negative: adopting this in an existing codebase means a bulk rename, and a
  bulk rename touches every file in a pull request — plan it as its own change,
  never mixed with behaviour changes.

## How this is enforced

| Rule | Test |
| --- | --- |
| `@Path` → `*Resource` in `api` | `NamingConventionArchTest.adr0003_rest_endpoint_naming` |
| `*Resource` only in `api` | `NamingConventionArchTest.adr0003_resource_only_in_api` |
| `application` → `*Service` + `@ApplicationScoped` | `NamingConventionArchTest.adr0003_application_services` |
| Port implementations in `persistence`, named `*Repository` | `NamingConventionArchTest.adr0003_repository_implementations` |
| `*Impl` suffix forbidden | `NamingConventionArchTest.adr0003_impl_suffix_forbidden` |
