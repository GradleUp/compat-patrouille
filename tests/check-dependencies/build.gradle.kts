import tapmoc.Severity

plugins {
  alias(libs.plugins.kgp.jvm)
  id("com.gradleup.tapmoc")
}

tapmoc {
  java(11)
  kotlin("1.9.0")
  checkApiDependencies(Severity.WARNING)
  checkRuntimeDependencies(Severity.WARNING)
}

dependencies {
  // Uncomment to make `tapmocCheckApiDependencies` fail because incompatible metadata was exposed to consumers.
  api("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
  // Uncomment to make `tapmocCheckRuntimeDependencies` fail because kotlin-stdlib was upgraded to a new version.
  implementation("com.squareup:kotlinpoet:2.2.0")
  api(libs.kotlin.metadata)
}
