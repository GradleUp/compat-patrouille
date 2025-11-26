plugins {
  id("java")
  id("com.gradleup.tapmoc")
  id("check.publication")
}

tapmoc {
  java(11)
  kotlin("2.0.0") // This should be a no-op
}

checkPublication {
  jvmTarget.set(11)
}
