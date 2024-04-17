plugins {
    id("kotlin-library-convention")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":airflux-commons-types"))
    implementation(libs.bundles.kotest)
}
