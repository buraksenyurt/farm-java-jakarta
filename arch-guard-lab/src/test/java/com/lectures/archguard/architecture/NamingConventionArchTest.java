package com.lectures.archguard.architecture;

import com.lectures.archguard.domain.BookRepository;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.lectures.archguard",
        importOptions = ImportOption.DoNotIncludeTests.class)
class NamingConventionArchTest {

    @ArchTest
    static final ArchRule adr0003_rest_endpoint_naming = classes()
            .that().areAnnotatedWith("jakarta.ws.rs.Path")
            .should().haveSimpleNameEndingWith("Resource")
            .andShould().resideInAPackage("com.lectures.archguard.api..")
            .as("ADR-0003: JAX-RS endpoints should have names ending with *Resource and reside in the api package");

    @ArchTest
    static final ArchRule adr0003_resource_only_in_api = classes()
            .that().haveSimpleNameEndingWith("Resource")
            .should().resideInAPackage("com.lectures.archguard.api..")
            .as("ADR-0003: *Resource classes should only exist in the api package");

    @ArchTest
    static final ArchRule adr0003_impl_suffix_forbidden = noClasses()
            .should().haveSimpleNameEndingWith("Impl")
            .as("ADR-0003: The *Impl suffix should not be used")
            .because("The name should describe how the implementation works (e.g., JpaBookRepository).");

    @ArchTest
    static final ArchRule adr0003_application_services = classes()
            .that().resideInAPackage("com.lectures.archguard.application..")
            .should().haveSimpleNameEndingWith("Service")
            .andShould().beAnnotatedWith("jakarta.enterprise.context.ApplicationScoped")
            .as("ADR-0003: Classes in the application layer should have names ending with *Service and be CDI beans");

    @ArchTest
    static final ArchRule adr0003_repository_implementations = classes()
            .that().implement(BookRepository.class)
            .should().resideInAPackage("com.lectures.archguard.persistence..")
            .andShould().haveSimpleNameEndingWith("Repository")
            .as("ADR-0003: Port implementations should reside in the persistence package and have names ending with *Repository");
}
