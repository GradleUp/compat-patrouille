plugins {
  alias(libs.plugins.kgp).apply(false)
}
buildscript {
  dependencies {
    classpath("com.gradleup.compat.patrouille:compat-patrouille-gradle-plugin")
  }
}


