pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.google()
  }
}

includeBuild("../")
includeBuild("../tests/build-logic")

include(
  ":agp9-kmp",
  ":agp9-kotlin",
)
