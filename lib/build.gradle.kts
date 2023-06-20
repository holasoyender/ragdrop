plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    id("maven-publish")
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

group = "app.lacabra"
description = "YAML verification with JSON schemas"
version = "0.1-dev"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    api("org.json:json:20230227")
    implementation("org.yaml:snakeyaml:2.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/holasoyender/ragdrop")
            credentials {
                username = "holasoyender"
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}