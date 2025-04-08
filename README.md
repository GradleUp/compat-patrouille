[![Maven Central](https://img.shields.io/maven-central/v/com.gradleup.compat.patrouille/module?style=flat-square)](https://central.sonatype.com/namespace/com.gradleup.compat.patrouille)
[![OSS Snapshots](https://img.shields.io/nexus/s/com.gradleup.compat.patrouille/module?server=https%3A%2F%2Foss.sonatype.org&label=oss-snapshots&style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/com/gradleup/compat/patrouille/)


# 🐾 Compat-Patrouille 🐾

The Compat-Patrouille Gradle plugin helps you configure your Java/Kotlin compatibility flags. 

Configuring Java/Kotlin compatibility flags is a mundane task that comes with surprising amounts of questions:

* What is the difference between `sourceCompatibility` and `targetCompatibility`?
* Should I use `release` instead?
* What does `release` even mean on Android?
* Why do I even need to configure Java compatibility if I only do Kotlin?
* How do I configure `release` with Kotlin?
* Should I use `tasks.withType<KotlinCompile>` or `compilerOptions {}` or something else?
* Is it "1.8" or "8" or "JavaVersion"?
* Which `KotlinVersion` do I need to import?
* ...


### Usage

```kotlin
plugins {
  id("java")
  // optional: Kotlin
  id("org.jetbrains.kotlin.jvm").version("2.1.20")
  // optional: Android
  id("org.jetbrains.kotlin.android").version("2.1.20")
  id("com.android.application").version("8.9.1")
}

/*
 * Configure all your Java/Kotlin targets with a single code block.
 * This code block works regardless of if you're using Kotlin/Android/KMP/etc...
 * You can copy/pate it
 */
compatPatrouille {
  // Java takes an int
  java(17)
  // Kotlin takes a string so you have more control of the patch release of the stdlib.
  // languageVersion/apiVersion are configured with the minor version only.
  kotlin("2.0.21")
}
```

That's it, you can now keep on with your life.

> [!NOTE]
> Calling `CompatPatrouilleExtension.java()` or `CompatPatrouille.kotlin()` requires the java/kotlin/android to be applied (this plugin does not use `pluginManager.withId {}`). This is typically the case if you use the `plugins {}` block. But If you're applying your plugins programmatically, make sure to call `CompatPatrouilleExtension.java()` or `CompatPatrouille.kotlin()` only after all your plugins have been applied.

### Requirements:

* Gradle 8+
* For Kotlin: Kotlin Gradle Plugin 1.9.0+
* For Android: Android Gradle Plugin 8.2.0+

