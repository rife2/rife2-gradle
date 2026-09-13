/*
 * Copyright 2003-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uwyn.rife2.gradle;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.process.CommandLineArgumentProvider;

import java.util.Collections;

/**
 * Provides the JVM argument that launches a process with the RIFE2 agent.
 */
public abstract class AgentArgumentProvider implements CommandLineArgumentProvider {
    /**
     * Indicates whether the process should be launched with the RIFE2 agent.
     *
     * @return {@code true} when the agent should be used; {@code false} otherwise
     */
    @Input
    public abstract Property<Boolean> getUseAgent();

    /**
     * The classpath that holds the RIFE2 agent jar.
     *
     * @return the agent classpath
     */
    @Classpath
    public abstract ConfigurableFileCollection getAgentClasspath();

    @Override
    public Iterable<String> asArguments() {
        if (Boolean.TRUE.equals(getUseAgent().get())) {
            return Collections.singleton("-javaagent:" + getAgentClasspath().getAsPath());
        }
        return Collections.emptyList();
    }
}
