package com.uwyn.rife2.gradle

class GraalVMSupportTest extends AbstractFunctionalTest {
    def setup() {
        usesProject("minimal")
        buildFile.text = buildFile.text.replace('id("com.uwyn.rife2")', 'id("com.uwyn.rife2")\n    id("org.graalvm.buildtools.native")')
    }

    def "configures the native image from the RIFE2 settings"() {
        given:
        buildFile << """
            tasks.register("dumpNativeConfiguration") {
                def binary = graalvmNative.binaries.named("main")
                def mainClass = binary.flatMap { it.mainClass }
                def patterns = binary.flatMap { it.resources.includedPatterns }
                def metadata = graalvmNative.metadataRepository.enabled
                doLast {
                    println "Native main class: \${mainClass.get()}"
                    println "Native resource patterns: \${patterns.get()}"
                    println "Reachability metadata enabled: \${metadata.get()}"
                }
            }
        """

        when:
        run("dumpNativeConfiguration", "copyWebappResources")

        then: "the native image uses the RIFE2 main class and bundles the webapp"
        outputContains("Native main class: hello.App")
        outputContains("Native resource patterns: [^webapp/.*\$]")
        outputContains("Reachability metadata enabled: true")
        tasks {
            succeeded ":copyWebappResources"
        }
        file("build/webapp/webapp/css/style.css").exists()
    }
}
