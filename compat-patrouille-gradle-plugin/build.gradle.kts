import com.gradleup.librarian.gradle.Librarian
import org.gradle.api.internal.artifacts.dependencies.DefaultFileCollectionDependency

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("java-gradle-plugin")
  id("com.gradleup.compat.patrouille")
}

Librarian.module(project)

dependencies {
  compileOnly(libs.gradle.api)
  compileOnly(libs.agp)
  compileOnly(libs.kgp)
}

configurations.getByName("api").dependencies.removeIf {
  it is DefaultFileCollectionDependency
}

compatPatrouille {
  java(11)
  kotlin("2.0.21")
}

gradlePlugin {
  plugins {
    create("com.gradleup.compat.patrouille") {
      id = "com.gradleup.compat.patrouille"
      description = "Helps with your compatibility troubles"
      implementationClass = "compat.patrouille.CompatPatrouillePlugin"
    }
  }
}
