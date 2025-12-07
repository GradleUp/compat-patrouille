plugins {
  id("com.gradleup.tapmoc")
  alias(libs.plugins.kgp.jvm).apply(false)
  id("check.publication")
}

tapmoc {
  java(11)
  kotlin("1.9.0")
}

checkPublication {
  jvmTarget.set(11)
  kotlinMetadataVersion.set("1.9.0")
}

pluginManager.apply("org.jetbrains.kotlin.jvm")
