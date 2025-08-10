import com.gradleup.librarian.gradle.Librarian

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.google.devtools.ksp")
  id("com.gradleup.gratatouille.wiring")
}

Librarian.module(project)

dependencies {
  compileOnly(libs.gradle.api)
  compileOnly(libs.agp)
  compileOnly(libs.kgp)
  implementation(libs.gratatouille.wiring.runtime)
  gratatouille(project(":compat-patrouille-tasks"))
}

gratatouille {
  codeGeneration()
}
