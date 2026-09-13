package com.uwyn.rife2.gradle

import java.util.zip.ZipFile

class InstrumentationTest extends AbstractFunctionalTest {
    static final String REFLECT_CONFIG = "META-INF/native-image/rife-instrumented/reflect-config.json"

    def setup() {
        usesProject("minimal")
    }

    def "doesn't instrument ahead of time by default"() {
        when:
        run "jar"

        then:
        outputDoesNotContain("instrumented: hello.Counter")
        !file("build/classes/java/main/$REFLECT_CONFIG").exists()
    }

    def "instruments the compiled classes ahead of time when enabled"() {
        given:
        buildFile << """
            rife2 {
                instrumentAheadOfTime = true
            }
        """

        when:
        run "jar"

        then:
        outputContains("instrumented: hello.Counter")
        file("build/classes/java/main/$REFLECT_CONFIG").text.contains('"name": "hello.Counter"')

        and: "the jar contains the instrumented classes"
        try (def jar = new ZipFile(file("build/libs/hello-1.0.jar"))) {
            assert jar.getEntry(REFLECT_CONFIG) != null
        }
    }

    def "enabling the instrumentation recompiles already compiled classes"() {
        when:
        run "compileJava"

        then:
        !file("build/classes/java/main/$REFLECT_CONFIG").exists()

        when:
        buildFile << """
            rife2 {
                instrumentAheadOfTime = true
            }
        """
        run "compileJava"

        then:
        outputContains("instrumented: hello.Counter")
        file("build/classes/java/main/$REFLECT_CONFIG").exists()
    }
}
