package com.uwyn.rife2.gradle

class AgentTest extends AbstractFunctionalTest {
    def setup() {
        usesProject("minimal")
    }

    def "passes the agent to the JVM of `run`, not to the application"() {
        given:
        buildFile << """
            tasks.named("run") {
                doFirst {
                    def agent = { it.toString().startsWith("-javaagent:") }
                    println "Agent in run JVM arguments: \${allJvmArgs.count(agent)}"
                    println "Agent in run program arguments: \${argumentProviders.collectMany { it.asArguments() as List }.count(agent)}"
                    throw new StopExecutionException()
                }
            }
        """

        when:
        run "run"

        then:
        outputContains("Agent in run JVM arguments: 1")
        outputContains("Agent in run program arguments: 0")
    }

    def "doesn't resolve the agent when it isn't used"() {
        given:
        buildFile.text = buildFile.text.replace("useAgent = true", "useAgent = false")
        buildFile << """
            def agentConfiguration = configurations.named("rife2Agent")
            tasks.named("test") {
                doLast {
                    println "Agent configuration state: \${agentConfiguration.get().state}"
                    println "Agent in test JVM arguments: \${allJvmArgs.count { it.startsWith("-javaagent:") }}"
                }
            }
        """

        when:
        run "test"

        then:
        outputContains("Agent configuration state: UNRESOLVED")
        outputContains("Agent in test JVM arguments: 0")
    }

    def "passes the agent to the JVM of `test`"() {
        given:
        buildFile << """
            tasks.named("test") {
                doFirst {
                    println "Agent in test JVM arguments: \${allJvmArgs.count { it.startsWith("-javaagent:") }}"
                }
            }
        """

        when:
        run "test"

        then:
        outputContains("Agent in test JVM arguments: 1")
    }
}
