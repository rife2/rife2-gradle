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

import org.graalvm.buildtools.gradle.dsl.GraalVMExtension;
import org.gradle.api.Project;
import org.gradle.api.tasks.Sync;

/**
 * Configures the GraalVM plugin.
 * This is done in a separate class so that the types referenced in this
 * class are not eagerly loaded, which would cause issues with users who
 * are not using the GraalVM plugin.
 */
public abstract class GraalVMPluginSupport {

    public static final String WEBAPP_DIR = "webapp";

    private GraalVMPluginSupport() {
    }

    public static void configureGraalVMPlugin(Project project, Rife2Extension rife2Extension) {
        var graalvm = project.getExtensions().findByType(GraalVMExtension.class);
        var copyWebApp = project.getTasks().register("copyWebappResources", Sync.class, task -> {
            task.setDestinationDir(project.getLayout().getBuildDirectory().dir(WEBAPP_DIR).get().getAsFile());
            task.from(Rife2Plugin.WEBAPP_SRCDIR, copy -> copy.into(WEBAPP_DIR));
        });
        graalvm.getBinaries().named("main", options -> {
            options.getMainClass().convention(rife2Extension.getUberMainClass());
            options.getClasspath().from(copyWebApp.map(Sync::getDestinationDir));
            options.getResources().getIncludedPatterns().add("^webapp/.*$");
        });
    }
}
