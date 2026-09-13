package com.uwyn.rife2.gradle

class ConfigurationCacheTest extends AbstractFunctionalTest {
    def setup() {
        usesProject("minimal")
    }

    def "jar, test and uberjar reuse the configuration cache #description"() {
        given:
        buildFile << configuration

        when:
        run "jar", "test", "uberjar", "--configuration-cache"

        then:
        outputContains("Configuration cache entry stored.")

        when:
        run "jar", "test", "uberjar", "--configuration-cache"

        then:
        outputContains("Reusing configuration cache.")

        where:
        description                            | configuration
        "with the agent"                       | ""
        "without the agent"                    | "\nrife2 {\n    useAgent = false\n}\n"
        "with ahead-of-time instrumentation"   | "\nrife2 {\n    useAgent = false\n    instrumentAheadOfTime = true\n}\n"
    }
}
