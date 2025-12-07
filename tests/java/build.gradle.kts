plugins {
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

// Testing that we are robust to plugins being added after the fact
pluginManager.apply("java")
