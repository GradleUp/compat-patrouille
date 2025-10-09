import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
  alias(libs.plugins.agp8)
  id("com.gradleup.compat.patrouille")
  id("check.publication")
}

val myJvmTarget = 11
compatPatrouille {
  java(myJvmTarget)
}

android {
  defaultConfig {
    namespace = "com.example"
    minSdk = libs.versions.compile.sdk.get().toInt()
    compileSdk = libs.versions.compile.sdk.get().toInt()
  }


  publishing {
    singleVariant("release") {
      withSourcesJar()
    }
  }
}

checkPublication {
  jvmTarget.set(myJvmTarget)
}

java.sourceSets.create("foo")

// Not sure why this is not automatically setup
tasks.named("build").dependsOn("compileFooJava")
