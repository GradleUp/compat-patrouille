@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.gradleup.compat.patrouille")
}

compatPatrouille {
  java(11)
  kotlin("2.0.0")
}

kotlin {
  wasmJs {
    nodejs()
    binaries.executable()
  }
}
