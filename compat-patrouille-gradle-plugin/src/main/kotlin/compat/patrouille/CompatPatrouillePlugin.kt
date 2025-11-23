package compat.patrouille

import gratatouille.wiring.GPlugin
import org.gradle.api.Project

@GPlugin(id = "com.gradleup.compat.patrouille")
fun compatPatrouillePlugin(target: Project) {
  error("com.gradleup.compat.patrouille has been renamed to com.gradleup.tapmoc. See https://github.com/GradleUp/compat-patrouille/issues/5 for more details.")
}
