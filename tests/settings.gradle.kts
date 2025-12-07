pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.google()
  }
}

includeBuild("../")
includeBuild("build-logic")

include(
  ":wasm-js",
  ":jvm",
  ":java",
  ":agp8-java",
  ":agp8-kotlin"
)
