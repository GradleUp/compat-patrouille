@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.gradleup.tapmoc")
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
