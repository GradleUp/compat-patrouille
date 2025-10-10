package compat.patrouille.internal

import org.gradle.api.JavaVersion

internal interface Agp {
  fun configureCompileJavaTasks(javaVersion: JavaVersion)
  fun isAndroidJavaCompileTask(name: String): Boolean
}
