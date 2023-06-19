plugins {
    id("java")
    kotlin("jvm") version "1.8.22"
    application
}

group = "app.lacabra"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ragdrop"))
}

tasks.test {
    useJUnitPlatform()
}