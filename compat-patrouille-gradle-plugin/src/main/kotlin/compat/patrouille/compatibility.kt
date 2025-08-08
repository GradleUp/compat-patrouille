package compat.patrouille

import compat.patrouille.internal.androidJavaVersion
import compat.patrouille.internal.configureKotlinJvmTarget
import compat.patrouille.internal.forEachCompilerOptions
import compat.patrouille.internal.hasAndroid
import compat.patrouille.internal.kotlinExtensionOrNull
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion


fun Project.configureJavaCompatibility(
  javaVersion: Int,
) {
  check(extensions.findByName("java") != null) {
    "CompatPatrouille: cannot configure Java compatibility since the Java plugin is not applied."
  }
  configureJavaCompatibilityInternal(javaVersion.toJavaVersion())
}

fun Project.configureKotlinCompatibility(
  version: String,
) {
  val kotlin = kotlinExtensionOrNull
  check(kotlin != null) {
    "CompatPatrouille: cannot configure Kotlin compatibility since the Kotlin plugin is not applied."
  }
  val kotlinVersion = KotlinVersion.fromVersion(version.substringBeforeLast("."))
  kotlin.forEachCompilerOptions {
    apiVersion.set(kotlinVersion)
    languageVersion.set(kotlinVersion)
  }

  kotlin.coreLibrariesVersion = version
}

internal fun Project.configureJavaCompatibilityInternal(javaVersion: JavaVersion) {
  if (hasAndroid) {
    androidJavaVersion(javaVersion)
    tasks.withType(JavaCompile::class.java) {
      it.sourceCompatibility = javaVersion.toString()
      it.targetCompatibility = javaVersion.toString()
    }
  } else {
    tasks.withType(JavaCompile::class.java) {
      it.options.release.set(javaVersion.majorVersion.toInt())
    }
  }
  configureKotlinJvmTarget(javaVersion)
}


internal fun Int.toJavaVersion(): JavaVersion {
  return JavaVersion.forClassVersion(this + 44)
}

internal fun JavaVersion.toInt(): Int {
  return majorVersion.toInt()
}
