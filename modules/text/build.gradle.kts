plugins {
    id("kotlin-library-convention")
    id("kover-merge-convention")
    id("org.sonarqube") version "4.4.1.3373"
}

repositories {
    mavenCentral()
}

version = "0.0.1-SNAPSHOT"
group = "io.github.airflux"

dependencies {

    /* Kotlin */
    implementation(kotlin("stdlib"))

    testImplementation(libs.bundles.kotest)
}

