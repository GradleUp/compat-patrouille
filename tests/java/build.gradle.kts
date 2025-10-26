plugins {
  id("java")
  id("com.gradleup.tapmoc")
  id("check.publication")
}

tapmoc {
  java(11)
}

checkPublication {
  jvmTarget.set(11)
}
