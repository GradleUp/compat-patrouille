plugins {
  alias(libs.plugins.kgp.jvm)
  alias(libs.plugins.ksp)
  alias(libs.plugins.gratatouille)
  alias(libs.plugins.compat.patrouille)
}

group = "build-logic"

dependencies {
  implementation(libs.kotlinx.json)
  implementation(libs.jsonpathkt)
  implementation(libs.cast)
  implementation(libs.kotlin.metadata)
  implementation(libs.asm)
  implementation(libs.gratatouille.wiring.runtime)
  implementation(libs.gratatouille.tasks.runtime)
  implementation(gradleApi())
}

gratatouille {
  codeGeneration {
    addDependencies.set(false)
  }
  pluginLocalPublication("check.publication")
}

tapmoc {
  java(11)
  kotlin(embeddedKotlinVersion)
}
