import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.net.*
import java.net.http.*

plugins {
    `maven-publish`
    `java-gradle-plugin`
    groovy
    alias(libs.plugins.publish)
}

group = "com.uwyn.rife2"
version = "1.2.0"

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
    compileOnly(libs.graalvm.plugin) {
        because("In order to configure it if the user applies it")
    }
    testImplementation(libs.spock.core)
    testImplementation(gradleTestKit())
}

gradlePlugin {
    website.set("https://rife2.com")
    vcsUrl.set("https://github.com/rife2/rife2-gradle")

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

    test {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
        addTestListener(object : TestListener {
            override fun beforeTest(p0: TestDescriptor?) = Unit
            override fun beforeSuite(p0: TestDescriptor?) = Unit
            override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
            override fun afterSuite(desc: TestDescriptor, result: TestResult) {
                if (desc.parent == null) {
                    val passed = result.successfulTestCount
                    val failed = result.failedTestCount
                    val skipped = result.skippedTestCount

                    if (project.properties["testsBadgeApiKey"] != null) {
                        val apiKey = project.properties["testsBadgeApiKey"]
                        val response: HttpResponse<String> = HttpClient.newHttpClient()
                            .send(
                                HttpRequest.newBuilder()
                                    .uri(
                                        URI(
                                            "https://rife2.com/tests-badge/update/com.uwyn.rife2/gradle?" +
                                                    "apiKey=$apiKey&" +
                                                    "passed=$passed&" +
                                                    "failed=$failed&" +
                                                    "skipped=$skipped"
                                        )
                                    )
                                    .POST(HttpRequest.BodyPublishers.noBody())
                                    .build(), HttpResponse.BodyHandlers.ofString()
                            )
                        println("RESPONSE: " + response.statusCode())
                        println(response.body())
                    }
                }
            }
        })
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
