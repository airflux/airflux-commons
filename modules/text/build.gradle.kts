plugins {
    id("kotlin-library-convention")
    id("kover-merge-convention")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Kotlin */
    implementation(kotlin("stdlib"))

    testImplementation(libs.bundles.kotest)
    testImplementation(project(":airflux-commons-kotest-assertions"))
}

