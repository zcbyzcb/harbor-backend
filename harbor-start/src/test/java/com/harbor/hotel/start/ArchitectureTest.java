package com.harbor.hotel.start;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;

import org.junit.jupiter.api.Test;

class ArchitectureTest {
    @Test
    void domainHasNoFrameworkAndInfrastructureHasNoUpperLayerDependency() {
        com.tngtech.archunit.core.domain.JavaClasses classes =
                new ClassFileImporter().importPackages("com.harbor.hotel");
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "org.apache.ibatis..",
                        "..app..",
                        "..api..",
                        "..infrastructure..")
                .check(classes);
        noClasses()
                .that()
                .resideInAPackage("..infrastructure..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..app..", "..api..")
                .check(classes);
        noClasses()
                .that()
                .haveSimpleNameEndingWith("Processor")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "..repository..")
                .check(classes);
    }
}
