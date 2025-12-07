pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }
    it.gradlePluginPortal {
      content {
        includeGroupByRegex("org\\.gradle\\.kotlin.*")
      }
    }
  }
}

includeBuild("../../")
includeBuild("../build-logic")
