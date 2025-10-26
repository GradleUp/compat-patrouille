plugins {
  alias(libs.plugins.kgp.jvm)
  id("com.gradleup.tapmoc")
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
