pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.google()
  }
}

includeBuild("../../")
includeBuild("../build-logic")

fun toto(settings: Settings) {
  settings.layout.rootDirectory.files()
}
