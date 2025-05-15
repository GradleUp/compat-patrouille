[![Maven Central](https://img.shields.io/maven-central/v/com.gradleup.compat.patrouille/compat-patrouille-gradle-plugin?style=flat-square)](https://central.sonatype.com/namespace/com.gradleup.compat.patrouille)
[![OSS Snapshots](https://img.shields.io/nexus/s/com.gradleup.compat.patrouille/compat-patrouille-gradle-plugin?server=https%3A%2F%2Foss.sonatype.org&label=oss-snapshots&style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/com/gradleup/compat/patrouille/)


# 🐾 Compat-Patrouille 🐾

The Compat-Patrouille helps you configure your Java/Kotlin compatibility flags: 

```kotlin
compatPatrouille {
  java(17) // build for Java 17, including source, target and api compatibility
  kotlin("2.0.21") // build for kotlin 2.0.21, including language and api version
}
```

### Rationale

Configuring Java/Kotlin compatibility flags is a mundane task that comes with surprising amounts of questions:

* What is the difference between `sourceCompatibility` and `targetCompatibility`?
* Should I use `release` instead?
* What does `release` even mean on Android?
* Why do I need to configure Java compatibility if I only do Kotlin?
* How do I configure `release` with Kotlin?
* Should I use `tasks.withType<KotlinCompile>` or `compilerOptions {}` or something else?
* Is it "1.8" or "8" or `JavaVersion.VERSION_1_8`?
* Is it `org.jetbrains.kotlin.gradle.dsl.KotlinVersion` or `kotlin.KotlinVersion`?
* And more...

La Compat Patrouille handles all of that with just two simple functions!

### Usage

```kotlin
plugins {
  // Add your Java/Kotlin/Android plugins here
  id("java")
  // or
  id("org.jetbrains.kotlin.jvm")
  // or
  id("org.jetbrains.kotlin.multiplatform")
  // or
  id("com.android.library")
  // etc...
  // And add the CompatPatrouille plugin
  id("com.gradleup.compat.patrouille").version("0.0.0")
}

/*
 * Configure all your Java/Kotlin targets with a single code block.
 * This code block works regardless of if you're using Kotlin/Android/KMP/etc...
 * You can copy/pate it
 */
compatPatrouille {
  // Java takes an int for simplicity
  java(17)
  // Kotlin takes a string so you have more control of the patch release of the stdlib.
  // languageVersion/apiVersion are configured with the minor version only.
  kotlin("2.0.21")
}
```

If you have convention plugins, you can also use the Compat-Patrouille without all the plugin ceremony:

```kotlin
import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility

class ConventionPlugin: Plugin<Project> {
  override fun apply(target: Project) {
    target.configureJavaCompatibility(17)
    target.configureKotlinCompatibility("2.0.21")
  }
}
```

That's it, you can now keep on with your life.

### Note

Calling `CompatPatrouilleExtension.java()` or `CompatPatrouille.kotlin()` requires the java/kotlin/android to be applied (this plugin does not use `pluginManager.withId {}`). This is typically the case if you use the `plugins {}` block. But If you're applying your plugins programmatically, make sure to call `CompatPatrouilleExtension.java()` or `CompatPatrouille.kotlin()` only after all your plugins have been applied.

### Requirements:

* Gradle 8+
* For Kotlin: Kotlin Gradle Plugin 1.9.0+
* For Android: Android Gradle Plugin 8.2.0+

