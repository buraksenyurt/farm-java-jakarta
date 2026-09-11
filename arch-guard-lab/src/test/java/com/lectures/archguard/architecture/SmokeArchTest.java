package com.lectures.archguard.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "com.lectures.archguard",
        importOptions = ImportOption.DoNotIncludeTests.class)
class SmokeArchTest {

    @ArchTest
    static final ArchRule domain_classes_are_exists = classes()
            .that().resideInAPackage("com.lectures.archguard.domain..")
            .should().bePublic()
            .as("Smoke Test: is domain classes are exists in the project?");
}
