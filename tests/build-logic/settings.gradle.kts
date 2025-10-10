pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.maven("https://storage.googleapis.com/gradleup/m2") {
      content {
        includeModule("com.gradleup.gratatouille", "gratatouille-processor")
      }
    }
  }
  repositories {
    maven("https://storage.googleapis.com/gradleup/m2") {
      content {
        includeGroupByRegex("com\\.gradleup\\..*")
      }
    }
  }
}
