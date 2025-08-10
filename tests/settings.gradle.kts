pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
  }
}

includeBuild("../")
include(":check-api-dependencies", ":verify-compatibility")
