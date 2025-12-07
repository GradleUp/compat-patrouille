pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.gradlePluginPortal()
  }
}

includeBuild("../../")
includeBuild("../build-logic")
