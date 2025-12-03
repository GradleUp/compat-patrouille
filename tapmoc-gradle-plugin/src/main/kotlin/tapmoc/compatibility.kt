package tapmoc

import tapmoc.internal.agp
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import tapmoc.internal.kgp

fun Project.configureJavaCompatibility(
  javaVersion: Int,
) {
  check(extensions.findByName("java") != null) {
    "Tapmoc: cannot configure Java compatibility since the Java plugin is not applied."
  }
  val agp = project.agp()

  /**
   * Set --release for all JavaCompile tasks except the ones owned by AGP (if any).
   */
  tasks.withType(JavaCompile::class.java).configureEach {
    if (agp?.isAndroidJavaCompileTask(it.name) != true) {
      it.options.release.set(javaVersion)
    }
  }

  /**
   * Set the source and target compatibility for AGP (if any).
   */
  agp?.javaCompatibility(javaVersion.toJavaVersion())

  project.kgp()?.javaCompatibility(javaVersion)
}


fun Project.configureKotlinCompatibility(
  version: String,
) {
  require(version.split(".").size == 3) {
    "Tapmoc: cannot parse Kotlin version '$version'. Expected format is X.Y.Z."
  }

  kgp()?.kotlinCompatibility(version)
}

internal fun Int.toJavaVersion(): JavaVersion {
  return JavaVersion.forClassVersion(this + 44)
}
