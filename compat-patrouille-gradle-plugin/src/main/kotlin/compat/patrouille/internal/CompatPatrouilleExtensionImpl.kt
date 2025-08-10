package compat.patrouille.internal

import compat.patrouille.CompatPatrouilleExtension
import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility
import compat.patrouille.task.registerCheckApiDependenciesTask
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

internal abstract class CompatPatrouilleExtensionImpl(private val project: Project) : CompatPatrouilleExtension {
  var kotlinVersion: String? = null

  override fun java(version: Int) {
    project.configureJavaCompatibility(version)
  }

  override fun kotlin(version: String) {
    val c = version.split(".")
    require(c.size == 3) {
      "Cannot parse Kotlin version $version. Expected format is X.Y.Z."
    }
    kotlinVersion = version
    project.configureKotlinCompatibility(version)
  }

  override fun checkApiDependencies(check: Boolean) {
    if (check) {
      val kotlin = project.extensions.findByName("kotlin")
      val isKmp = if (kotlin != null) {
        isKmp(kotlin)
      } else {
        false
      }
      val apiElementsConfigurationName = if (isKmp) {
        "jvmApiElements"
      } else {
        "apiElements"
      }
      val configuration = project.configurations.create("compatPatrouilleCheck") {
        it.isCanBeConsumed = false
        it.isCanBeResolved = true
        it.isVisible = false
      }
      configuration.extendsFrom(project.configurations.getByName(apiElementsConfigurationName))
      val checkApiDependencies = project.registerCheckApiDependenciesTask(
        taskName = "compatPatrouilleCheckApiDependencies",
        compileClasspath = configuration,
        kotlinVersion = project.provider { kotlinVersion ?: project.getKotlinPluginVersion() }
      )

      project.tasks.named("check").configure {
        it.dependsOn(checkApiDependencies)
      }
    }
  }
}
