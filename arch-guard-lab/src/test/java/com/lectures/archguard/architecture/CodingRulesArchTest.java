package com.lectures.archguard.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.library.GeneralCodingRules;

@AnalyzeClasses(
        packages = "com.lectures.archguard",
        importOptions = ImportOption.DoNotIncludeTests.class)
class CodingRulesArchTest {

    @ArchTest
    static final ArchRule adr0004_no_test_libraries_in_production = noClasses()
            .should().dependOnClassesThat().resideInAnyPackage("org.junit..", "com.tngtech.archunit..")
            .as("ADR-0004: Production code should not depend on test libraries")
            .because("Test libraries should only be used in test code.");

    @ArchTest
    static final ArchRule adr0004_no_standard_streams
            = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
                    .as("ADR-0004: Do not use System.out / System.err ")
                    .because("Logging via SLF4J is used.");

    @ArchTest
    static final ArchRule adr0004_no_java_util_logging
            = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
                    .as("ADR-0004: java.util.logging should not be used")
                    .because("SLF4J is chosen as the facade; the bridge is connected at runtime.");

    @ArchTest
    static final ArchRule adr0004_no_generic_exceptions
            = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
                    .as("ADR-0004: Generic exception should not be thrown")
                    .because("Error types should be expressed in the domain language.");

    /**
     * Custom rule example: @Inject is forbidden on fields.
     */
    private static final ArchCondition<JavaClass> DO_NOT_USE_INJECT_ON_FIELDS
            = new ArchCondition<>("should not use @Inject on fields") {
        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            for (JavaField field : javaClass.getFields()) {
                if (field.isAnnotatedWith("jakarta.inject.Inject")) {
                    events.add(SimpleConditionEvent.violated(field,
                            String.format("Field %s is annotated with @Inject (%s)",
                                    field.getFullName(),
                                    javaClass.getSourceCodeLocation())));
                }
            }
        }
    };

    @ArchTest
    static final ArchRule adr0004_no_field_injection = classes()
            .should(DO_NOT_USE_INJECT_ON_FIELDS)
            .as("ADR-0004: Constructor injection should be used instead of field injection")
            .because("Constructor injection makes dependencies visible and improves testability.");
}
