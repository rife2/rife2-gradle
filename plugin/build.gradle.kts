plugins {
    `maven-publish`
    `java-gradle-plugin`
    groovy
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "com.uwyn.rife2"
version = "1.0.3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    gradleApi()
    testImplementation("org.spockframework:spock-core:2.3-groovy-3.0")
    testImplementation(gradleTestKit())
}

gradlePlugin {
    website.set("https://rife2.com")
    vcsUrl.set("https://github.com/gbevin/rife2-gradle")

    plugins {
        create("rife2") {
            id = "com.uwyn.rife2"
            displayName = "RIFE2 Plugin"
            description = "Provides support for easily building RIFE2 applications with Gradle"
            tags.set(listOf("plugins", "rife2", "web-framework", "java"))
            implementationClass = "com.uwyn.rife2.gradle.Rife2Plugin"
        }
    }
}

tasks {
    javadoc {
        options {
            this as StandardJavadocDocletOptions
            keyWords(true)
            splitIndex(true)
            addBooleanOption("Xdoclint:-missing", true)
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED, org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED, org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
    }
}