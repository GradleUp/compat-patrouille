@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kgp.multiplatform)
  id("com.gradleup.compat.patrouille")
  id("maven-publish")
}

tapmoc {
  java(11)
  kotlin("2.0.0")
}

kotlin {
  jvm()
  wasmJs {
    nodejs()
    binaries.executable()
  }
}
