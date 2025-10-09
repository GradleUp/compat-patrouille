plugins {
  id("java")
  id("com.gradleup.compat.patrouille")
  id("check.publication")
}

compatPatrouille {
  java(11)
}

checkPublication {
  jvmTarget.set(11)
}
