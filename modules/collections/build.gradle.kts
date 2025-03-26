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

    implementation(project(":airflux-commons-text"))

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.knit.test)
    testImplementation(project(":airflux-commons-kotest-assertions"))
}
