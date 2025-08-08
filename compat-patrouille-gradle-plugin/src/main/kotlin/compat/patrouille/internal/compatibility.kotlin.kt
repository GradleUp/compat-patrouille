package compat.patrouille.internal

import compat.patrouille.toInt
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

internal fun Project.configureKotlinJvmTarget(javaVersion: JavaVersion) {
  val kotlin = kotlinExtensionOrNull
  if (kotlin == null) {
    return
  }
  kotlin.forEachCompilerOptions { platformType ->
    when (this) {
      is KotlinJvmCompilerOptions -> {
        val version = javaVersion.toInt()

        if (platformType != KotlinPlatformType.androidJvm) {
          // See https://cs.android.com/android-studio/platform/tools/base/+/mirror-goog-studio-main:build-system/gradle-core/src/main/java/com/android/build/gradle/tasks/JavaCompileUtils.kt;l=410?q=Using%20%27--release%27%20option%20for%20JavaCompile%20is%20not%20supported%20because%20it%20prevents%20the%20Android%20Gradle%20plugin
          freeCompilerArgs.add("-Xjdk-release=${version}")
        }
        /**
         * jvmTarget needs to be set as well, or we get an error such as:
         * e: '-Xjdk-release=11' option conflicts with '-jvm-target 17'. Please remove the '-jvm-target' option
         */
        jvmTarget.set(version.toJvmTarget())
      }
    }
  }
}

private fun Int.toJvmTarget(): JvmTarget {
  return when (this) {
    8 -> JvmTarget.JVM_1_8
    else -> JvmTarget.fromTarget(this.toString())
  }
}

fun KotlinProjectExtension.forEachCompilerOptions(block: KotlinCommonCompilerOptions.(platformType: KotlinPlatformType) -> Unit) {
  when (this) {
    is KotlinJvmProjectExtension -> compilerOptions.block(KotlinPlatformType.jvm)
    is KotlinAndroidProjectExtension -> compilerOptions.block(KotlinPlatformType.androidJvm)
    is KotlinMultiplatformExtension -> {
      targets.all { target ->
        target.compilations.all {
          it.compileTaskProvider.configure {
            it.compilerOptions.block(target.platformType)
          }
        }
      }
    }

    else -> error("Unknown kotlin extension $this")
  }
}
