package compat.patrouille

import compat.patrouille.internal.agp
import compat.patrouille.internal.configureKotlinJvmTarget
import compat.patrouille.internal.forEachCompilerOptions
import compat.patrouille.internal.kotlinExtensionOrNull
import java.lang.reflect.Method
import kotlin.text.toInt
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion


fun Project.configureJavaCompatibility(
  javaVersion: Int,
) {
  check(extensions.findByName("java") != null) {
    "CompatPatrouille: cannot configure Java compatibility since the Java plugin is not applied."
  }
  val agp = project.agp()
  val javaVersion = javaVersion.toJavaVersion()
  if (agp != null) {
    agp.configureCompileJavaTasks(javaVersion)

    /**
     * It's very unlikely, but there might be non-Android compileJava classes.
     * For those, we set the --release flag.
     */
    tasks.withType(JavaCompile::class.java).configureEach {
      if (!agp.isAndroidJavaCompileTask(it.name)) {
        it.options.release.set(javaVersion.majorVersion.toInt())
      }
    }
  } else {
    tasks.withType(JavaCompile::class.java).configureEach {
      it.options.release.set(javaVersion.majorVersion.toInt())
    }
  }
  configureKotlinJvmTarget(javaVersion)
}

private var method: Method? = null
private var firstTime = true

@Synchronized
private fun compilerOptionsMethod(): Method? {
  if (firstTime) {
    firstTime = false

    method = try {
      KotlinMultiplatformExtension::class.java.getMethod("getCompilerOptions")
    } catch (_: NoSuchMethodException) {
      null
    }
  }

  return method
}

fun Project.configureKotlinCompatibility(
  version: String,
) {
  val kotlin = kotlinExtensionOrNull
  check(kotlin != null) {
    "CompatPatrouille: cannot configure Kotlin compatibility since the Kotlin plugin is not applied."
  }
  val kotlinVersion = KotlinVersion.fromVersion(version.substringBeforeLast("."))
  when (kotlin) {
    is KotlinAndroidProjectExtension -> {
      kotlin.compilerOptions {
        apiVersion.set(kotlinVersion)
        languageVersion.set(kotlinVersion)
      }
    }
    is KotlinJvmProjectExtension -> {
      kotlin.compilerOptions {
        apiVersion.set(kotlinVersion)
        languageVersion.set(kotlinVersion)
      }
    }
    is KotlinMultiplatformExtension -> {
      val compilerOptions = compilerOptionsMethod()
      if (compilerOptions != null) {
        (compilerOptions.invoke(kotlin) as KotlinCommonCompilerOptions).apply {
          /**
           * Kotlin 2.0+: it's important to set the version at the extension level for the shared source sets
           * like `commonMain` and `commonTest`.
           *
           * See https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-dsl-reference.html#compiler-options
           */
          apiVersion.set(kotlinVersion)
          languageVersion.set(kotlinVersion)
        }
      } else {
        /**
         * Kotlin <2.0: not sure how we do the same thing. The IDE won't be able to get the proper information in
         * common source sets, but the final binaries should still target the correct version.
         */
        kotlin.forEachCompilerOptions {
          apiVersion.set(kotlinVersion)
          languageVersion.set(kotlinVersion)
        }
      }
    }
  }

  kotlin.coreLibrariesVersion = version

  /**
   * Wasm and JS require the latest kotlin stdlib
   *
   * See https://youtrack.jetbrains.com/issue/KT-66755/
   */
  if (kotlin is KotlinMultiplatformExtension && findProperty("kotlin.stdlib.default.dependency") != "false") {
    kotlin.targets.configureEach { target ->
      target.compilations.configureEach {
        when (target.platformType) {
          KotlinPlatformType.js,
          KotlinPlatformType.wasm,
              // do we need native here as well?
            -> {
            it.defaultSourceSet.dependencies {
              api("org.jetbrains.kotlin:kotlin-stdlib:${getKotlinPluginVersion()}")
            }
          }
          else -> return@configureEach
        }
      }
    }
  }
}

internal fun Int.toJavaVersion(): JavaVersion {
  return JavaVersion.forClassVersion(this + 44)
}

internal fun JavaVersion.toInt(): Int {
  return majorVersion.toInt()
}
