package com.lectures.archguard.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.lectures.archguard",
        importOptions = ImportOption.DoNotIncludeTests.class)
class DomainPurityArchTest {

    @ArchTest
    static final ArchRule adr0002_domain_framework_free = noClasses()
            .that().resideInAPackage("com.lectures.archguard.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "jakarta..",
                    "javax..",
                    "org.slf4j..",
                    "com.fasterxml..",
                    "org.flywaydb..")
            .as("ADR-0002: Domain layer is framework free")
            .because("Domain model should be portable without being coupled to Jakarta EE or any other infrastructure.");

    @ArchTest
    static final ArchRule adr0002_entitymanager_only_in_persistence = noClasses()
            .that().resideOutsideOfPackage("com.lectures.archguard.persistence..")
            .should().dependOnClassesThat()
            .haveFullyQualifiedName("jakarta.persistence.EntityManager")
            .as("ADR-0002: EntityManager should only be used in the persistence layer")
            .because("EntityManager is a persistence concern and should not be used in the domain layer.");

    @ArchTest
    static final ArchRule adr0002_jpa_only_in_persistence = classes()
            .that().areAnnotatedWith("jakarta.persistence.Entity")
            .should().resideInAPackage("com.lectures.archguard.persistence..")
            .as("ADR-0002: JPA entities should only exist in the persistence layer")
            .because("Persistence details should not leak into the domain model.");

}
