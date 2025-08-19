pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
  }
}

includeBuild("../")
include(":check-dependencies", ":verify-compatibility", ":wasm-js")
