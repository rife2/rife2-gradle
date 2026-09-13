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

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;

/**
 * Instruments compiled classes ahead of time with RIFE2's instrumentation deployer.
 */
public abstract class InstrumentClassesAction implements Action<Task> {
    static final String DEPLOYER_CLASS = "rife.instrument.InstrumentationDeployer";

    /**
     * Indicates whether the classes should be instrumented.
     *
     * @return {@code true} when the classes should be instrumented; {@code false} otherwise
     */
    public abstract Property<Boolean> getEnabled();

    /**
     * The directory with the compiled classes, which are instrumented in place.
     *
     * @return the classes directory
     */
    public abstract DirectoryProperty getClassesDirectory();

    /**
     * The classpath with RIFE2 and the dependencies of the compiled classes.
     *
     * @return the instrumentation classpath
     */
    public abstract ConfigurableFileCollection getClasspath();

    /**
     * The Java launcher that runs the instrumentation.
     *
     * @return the Java launcher
     */
    public abstract Property<JavaLauncher> getLauncher();

    @Inject
    protected abstract ExecOperations getExecOperations();

    @Override
    public void execute(Task task) {
        if (!Boolean.TRUE.equals(getEnabled().get())) {
            return;
        }
        var classes = getClassesDirectory().get().getAsFile();
        if (!classes.isDirectory()) {
            return;
        }
        getExecOperations().javaexec(javaexec -> {
            javaexec.executable(getLauncher().get().getExecutablePath().getAsFile());
            javaexec.setClasspath(getClasspath());
            javaexec.getMainClass().set(DEPLOYER_CLASS);
            javaexec.args("-verbose", "-d", classes.getPath(), classes.getPath());
        });
    }
}
