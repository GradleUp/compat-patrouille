plugins {
  id("com.gradleup.tapmoc")
  id("org.jetbrains.kotlin.jvm").version("2.2.0").apply(false)
  id("check.publication")
  id("maven-publish")
}

tapmoc {
  java(11)
  kotlin("1.9.0")
  checkDependencies()
}

checkPublication {
  jvmTarget.set(11)
  kotlinMetadataVersion.set("1.9.0")
}

pluginManager.apply("org.jetbrains.kotlin.jvm")
