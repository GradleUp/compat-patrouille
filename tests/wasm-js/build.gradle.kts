@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kgp.multiplatform)
  id("com.gradleup.tapmoc")
}

tapmoc {
  java(11)
  kotlin("2.0.0")
  checkDependencies()
}

kotlin {
  jvm()
  wasmJs {
    nodejs()
    binaries.executable()
  }

  sourceSets {
    getByName("commonTest").dependencies {
      implementation(kotlin("test"))
    }
  }
}
