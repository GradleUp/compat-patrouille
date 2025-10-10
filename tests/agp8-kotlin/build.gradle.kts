import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.agp8)
  alias(libs.plugins.kgp.android)
  id("com.gradleup.compat.patrouille")
  id("check.publication")
}

val myJvmTarget = 11
val myKotlinMetadataVersion = "2.0.0"

compatPatrouille {
  java(myJvmTarget)
  kotlin(myKotlinMetadataVersion)
}

android {
  publishing {
    singleVariant("release") {
      withSourcesJar()
    }
  }
}

checkPublication {
  jvmTarget.set(myJvmTarget)
  // 2.0.0 ends up as 1.9.9999, see https://github.com/Jetbrains/kotlin/blob/dbb50f240fc955d4207aec7f62000b77be289acd/compiler/backend/src/org/jetbrains/kotlin/codegen/ClassFileFactory.java#L140
  kotlinMetadataVersion.set("1.9.9999")
}

android {
  defaultConfig {
    namespace = "com.example"
    compileSdk = libs.versions.compile.sdk.get().toInt()
  }
}
