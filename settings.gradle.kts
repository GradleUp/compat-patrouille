pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.apply {
      mavenCentral()
      google()
      maven("https://storage.googleapis.com/gradleup/m2")
    }
  }
}

include(":compat-patrouille-gradle-plugin")
include(":compat-patrouille-tasks")

includeBuild("../gratatouille")