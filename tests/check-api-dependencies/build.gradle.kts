plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.gradleup.compat.patrouille")
}

compatPatrouille {
  java(11)
  kotlin("1.9.0")
  checkApiDependencies(true)
}

dependencies {
  // Uncomment to make `compatPatrouilleCheckApiDependencies` fail because incompatible metadata was exposed to consumers.
//  api("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
  api(libs.kotlinx.metadata)
}
