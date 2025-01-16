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

    implementation(libs.bundles.kotest)
}

