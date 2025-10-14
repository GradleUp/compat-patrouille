package compat.patrouille.internal

import compat.patrouille.CompatPatrouilleExtension
import compat.patrouille.Severity
import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility
import compat.patrouille.task.registerCheckApiDependenciesTask
import compat.patrouille.task.registerCheckRuntimeDependenciesTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
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

  override fun checkApiDependencies(severity: Severity) {
    if (severity == Severity.IGNORE) {
      return
    }
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
    val configurationProvider = project.configurations.register("compatPatrouilleCheck") {
      it.isCanBeConsumed = false
      it.isCanBeResolved = true
      it.isVisible = false
    }
    project.configurations
      .withType(Configuration::class.java)
      .matching { it.name == apiElementsConfigurationName }
      .configureEach { configurationProvider.get().extendsFrom(it) }
    val checkApiDependencies = project.registerCheckApiDependenciesTask(
      warningAsError = project.provider { severity == Severity.ERROR },
      kotlinVersion = project.provider { kotlinVersion ?: project.getKotlinPluginVersion() },
      taskName = "compatPatrouilleCheckApiDependencies",
      compileClasspath = configurationProvider.get(),
    )

    project.tasks.named("check").configure {
      it.dependsOn(checkApiDependencies)
    }
  }

  override fun checkRuntimeDependencies(severity: Severity) {
    if (severity == Severity.IGNORE) {
      return
    }
    val kotlin = project.extensions.findByName("kotlin")
    val targets = if (kotlin is KotlinMultiplatformExtension) {
      kotlin.targets
    } else if (kotlin is KotlinJvmProjectExtension) {
      listOf(kotlin.target)
    } else {
      return
    }
    val lifecycleTask = project.tasks.register("compatPatrouilleCheckRuntimeDependencies")

    targets.forEach { target ->
      target.compilations.forEach {
        val configuration = it.runtimeDependencyConfigurationName?.let {
          project.configurations.named(it)
        }

        if (configuration != null) {
          val stdlibVersions = configuration.map {
            it.incoming.resolutionResult.allComponents
              .mapNotNull { (it.id as? ModuleComponentIdentifier) }
              .filter {
                it.group == "org.jetbrains.kotlin" && it.module == "kotlin-stdlib"
              }.map {
                it.version
              }
          }
          val task = project.registerCheckRuntimeDependenciesTask(
            taskName = "compatPatrouilleCheck${target.name}${it.name}RuntimeDependencies",
            warningAsError = project.provider { severity == Severity.ERROR },
            kotlinVersion = project.provider { kotlinVersion ?: project.getKotlinPluginVersion() },
            transitiveKotlinVersions = stdlibVersions,
          )
          project.tasks.named("check").configure {
            it.dependsOn(task)
          }
          lifecycleTask.configure {
            it.dependsOn(task)
          }
        }
      }
    }
  }
}
