plugins {
    id("kotlin-library-convention")
    id("kover-merge-convention")
}

repositories {
    mavenCentral()
}

version = "0.0.1-SNAPSHOT"
group = "io.github.airflux"

dependencies {

    /* Kotlin */
    implementation(kotlin("stdlib"))

    implementation(project(":airflux-commons-text"))

    testImplementation(libs.bundles.kotest)
    testImplementation(project(":airflux-commons-kotest-assertions"))
}

