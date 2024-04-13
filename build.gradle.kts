plugins {
    id("kover-merge-convention")
    id("licenses-convention")
    id("org.sonarqube") version "5.0.0.4638"
}

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1-SNAPSHOT"
    group = "io.github.airflux"
}

dependencies {
    kover(project(":airflux-commons-kotest-assertions"))
    kover(project(":airflux-commons-text"))
}

sonar {
    properties {
        property("sonar.projectKey", "airflux_airflux-commons")
        property("sonar.organization", "airflux")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${projectDir.path}/build/reports/jacoco/test/jacocoTestReport.xml"
        )
    }
}
