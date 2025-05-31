plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.gradleup.compat.patrouille")
}

compatPatrouille {
  java(11)
  kotlin("1.9.0")
  checkApiDependencies()
}

dependencies {
//  api("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
  api(libs.kotlinx.metadata)
}
