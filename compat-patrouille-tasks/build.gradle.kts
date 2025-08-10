import com.gradleup.librarian.core.tooling.init.kotlinPluginVersion
import com.gradleup.librarian.gradle.Librarian
import com.gradleup.librarian.gradle.configureKotlinCompatibility

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.gradleup.gratatouille.tasks")
  id("com.google.devtools.ksp")
}

Librarian.module(project)

dependencies {
  implementation(libs.kotlinx.metadata)
  implementation(libs.gratatouille.tasks.runtime)
}

// Override the default from Librarian, we want to be able to use the latest Kotlin version here.
configureKotlinCompatibility(kotlinPluginVersion)

gratatouille {
  codeGeneration {
    classLoaderIsolation()
  }
}
