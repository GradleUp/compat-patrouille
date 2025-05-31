import com.gradleup.librarian.gradle.Librarian

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.gradleup.compat.patrouille")
  id("com.gradleup.gratatouille")
  id("com.google.devtools.ksp")
}

Librarian.module(project)

dependencies {
  implementation(libs.gratatouille.runtime)
  implementation(libs.kotlinx.metadata)
}

compatPatrouille {
  java(11)
}

gratatouille {
  codeGeneration {
    classLoaderIsolation()
  }
}
