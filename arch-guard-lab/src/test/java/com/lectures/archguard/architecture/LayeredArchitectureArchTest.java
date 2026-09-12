package com.lectures.archguard.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@AnalyzeClasses(packages = "com.lectures.archguard", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayeredArchitectureArchTest {

    @ArchTest
    static final ArchRule adr0001_layers = Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Api").definedBy("com.lectures.archguard.api..")
            .layer("Application").definedBy("com.lectures.archguard.application..")
            .layer("Domain").definedBy("com.lectures.archguard.domain..")
            .layer("Persistence").definedBy("com.lectures.archguard.persistence..")
            .layer("Config").definedBy("com.lectures.archguard.config..")
            .whereLayer("Api").mayOnlyBeAccessedByLayers("Config")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Api", "Config")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Config")
            .as("ADR-0001: Layered architecture and dependency direction")
            .because("Dependencies should only flow inward.");

    @ArchTest
    static final ArchRule adr0001_packages_no_cycles = SlicesRuleDefinition.slices()
            .matching("com.lectures.archguard.(*)..")
            .should().beFreeOfCycles()
            .as("ADR-0001: Packages should have no cyclic dependencies");

    @ArchTest
    static final ArchRule adr0001_api_persistence_no_bypass = noClasses()
            .that().resideInAPackage("com.lectures.archguard.api..")
            .should().dependOnClassesThat()
            .resideInAPackage("com.lectures.archguard.persistence..")
            .as("ADR-0001: API layer should not bypass the persistence layer")
            .because("The REST endpoint should invoke business rules through the application layer.");

    @ArchTest
    static final ArchRule adr0001_domain_no_outer_dependency = noClasses()
            .that().resideInAPackage("com.lectures.archguard.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "com.lectures.archguard.api..",
                    "com.lectures.archguard.application..",
                    "com.lectures.archguard.persistence..",
                    "com.lectures.archguard.config..")
            .as("ADR-0001: Domain layer should not depend on any external layer")
            .because("The domain is the core of the architecture and should not be aware of other layers.");
}
