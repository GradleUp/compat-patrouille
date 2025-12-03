import tapmoc.Severity

buildscript {
  dependencies {
    classpath("com.gradleup.tapmoc:tapmoc-gradle-plugin:PLACEHOLDER")
  }
}

plugins {
  id("java")
}

pluginManager.apply("com.gradleup.tapmoc")
extensions.getByType(tapmoc.TapmocExtension::class.java).apply {
  java(8)
  checkDependencies(Severity.ERROR)
}

dependencies {
  implementation("com.squareup.okhttp3:okhttp-jvm:5.3.2")
}
