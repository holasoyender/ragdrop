plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "app.lacabra.ragdrop"

include(":app.lacabra.ragdrop")
project(":app.lacabra.ragdrop").projectDir = file("$rootDir/lib")