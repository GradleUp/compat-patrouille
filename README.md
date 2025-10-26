[![Maven Central](https://img.shields.io/maven-central/v/com.gradleup.compat.patrouille/compat-patrouille-gradle-plugin?style=flat-square)](https://central.sonatype.com/namespace/com.gradleup.compat.patrouille)
[![OSS Snapshots](https://img.shields.io/nexus/s/com.gradleup.compat.patrouille/compat-patrouille-gradle-plugin?server=https%3A%2F%2Foss.sonatype.org&label=oss-snapshots&style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/com/gradleup/compat/patrouille/)


# 🐾 Compat-Patrouille 🐾

The Compat-Patrouille helps you configure your Java/Kotlin compatibility flags: 

```kotlin
tapmoc {
  java(17) // build for Java 17, including source, target and api compatibility
  kotlin("2.1.0") // build for kotlin 2.1.0, including language and api version
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
* Is this working with KMP?
* And more...

La Compat Patrouille handles all of that with just two simple functions!

> [!NOTE]
> Compatibility flags only work for JVM targets. [See below for more details](#kotlin-multiplatform-kmp). 
 
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
  id("com.gradleup.compat.patrouille").version("0.1.0")
}

/*
 * Configure all your Java/Kotlin targets with a single code block.
 * This code block works regardless of if you're using Kotlin/Android/KMP/etc...
 * You can copy/paste it
 */
tapmoc {
  // Java takes an int for simplicity
  java(17)
  // Kotlin takes a string so you have more control of the patch release of the stdlib.
  // languageVersion/apiVersion are configured with the minor version only.
  kotlin("2.1.0")
}
```

If you have convention plugins, you can also use the Compat-Patrouille without all the plugin ceremony:

```kotlin
import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility

class ConventionPlugin: Plugin<Project> {
  override fun apply(target: Project) {
    target.configureJavaCompatibility(17)
    target.configureKotlinCompatibility("2.1.0")
  }
}
```

That's it, you can now keep on with your life.

### Checking transitive dependencies

Enforcing compiler flags works for your own code but doesn't check your dependencies. They may use incompatible APIs that will crash at runtime and/or produce incompatible metadata that will crash at build time.

You can have compat-patrouille fail in such cases with `checkApiDependencies` or `checkRuntimeDependencies`:

```kotlin
tapmoc {
  // Fail the build if any api dependency exposes incompatible Kotlin metadata.
  checkApiDependencies(Severity.ERROR)
  // Fail the build if any runtime dependency relies on an incompatible kotlin-stdlib version.
  checkRuntimeDependencies(Severity.ERROR)
}
```

### Kotlin multiplatform (KMP)

The KMP ecosystem is a lot less mature than the JVM ecosystem.

In particular:
* [non-JVM targets do not support apiVersion/languageVersion](https://youtrack.jetbrains.com/issue/KT-66755/).
* some targets (like wasmJs) require that the compile time `kotlin-stdlib` version matches the compiler version.

Not only does that mean that compat-patrouille cannot configure compatibility flags for non-JVM targets, it also means compat-patrouille may downgrade some of your dependencies. 

Because it relies on `coreLibrariesVersion` to configure the JVM stdlib version, some of your non-JVM libraries may be older than your compiler version, which may cause issues.

If you require a newer version of a core library, you can upgrade it by adding it explicitly to your dependencies:

```kotlin
sourceSets.getByName("wasmJsTest")  {
  dependencies {
    // Upgrade the kotlin-test dependency for wasmJsTest
    implementation("org.jetbrains.kotlin:kotlin-test:${getKotlinPluginVersion()}")
  }
}
```

### Requirements:

* Gradle 8.3+
* For Kotlin: Kotlin Gradle Plugin 1.9.0+
* For Android: Android Gradle Plugin 8.2.0+

