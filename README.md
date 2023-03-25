[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![GitHub CI](https://github.com/rife2/rife2-gradle/actions/workflows/gradle.yml/badge.svg)](https://github.com/rife2/rife2-gradle/actions/workflows/gradle.yml)
[![Tests](https://rife2.com/tests-badge/badge/com.uwyn.rife2/gradle)](https://github.com/rife2/rife2-gradle/actions/workflows/gradle.yml)

# RIFE2 Gradle Plugin

This project provides a Gradle plugin for RIFE2 applications.

Using this plugin in your project can be done by adding the following to your
Gradle `build.gradle.kts` file:

```kotlin
plugins {
    application
    id("com.uwyn.rife2") version "1.0.8"
    // ...
}
```

> **NOTE:** the RIFE2 Gradle plugin relies on the presence of the `application`
> plugin

Afterwards, the `rife2` extension becomes available, and you can use it like
this:

```kotlin
rife2 {
    version.set("1.5.6")                                    // set the RIFE2 version to use
    useAgent.set(true)                                      // set whether to run with the RIFE2 agent
    uberMainClass.set("hello.AppUber")                      // set a different main class to use for the UberJar
    precompiledTemplateTypes.add(HTML)                      // template types that should be pre-compiled
    templateDirectories.from(file("src/main/templates"))    // additional template directories to use
    includeServerDependencies.set(true)                     // set whether to include the embedded server deps
}
```

The usual `run`, `test`, `jar` tasks are still available, the RIFE2 plugins adds
the following:

* `precompileTemplates` : performs the template pre-compilation of the activated types
* `uberJar` : creates an Uber Jar with everything to run your application standalone

## GraalVM Native Image support

When your project uses the GraalVM Gradle plugin, the RIFE2 Gradle plugin will
automatically configure the GraalVM plugin to properly include the web application
resources into the native image.

## Get in touch

Thanks for using RIFE2!

If you have any questions, suggestions, ideas or just want to chat, feel free
to post on the [forums](https://github.com/rife2/rife2/discussions), to join
me on [Discord](https://discord.gg/DZRYPtkb6J) or to connect with me on
[Mastodon](https://uwyn.net/@gbevin).