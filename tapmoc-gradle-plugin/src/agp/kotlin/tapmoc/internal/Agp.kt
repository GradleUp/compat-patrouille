package tapmoc.internal

import org.gradle.api.JavaVersion

internal interface Agp {
  fun javaCompatibility(javaVersion: JavaVersion)
  fun isAndroidJavaCompileTask(name: String): Boolean
}
