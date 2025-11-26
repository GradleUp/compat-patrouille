package tapmoc.internal

import gratatouille.wiring.capitalizeFirstLetter
import tapmoc.TapmocExtension
import tapmoc.Severity
import tapmoc.configureJavaCompatibility
import tapmoc.configureKotlinCompatibility
import tapmoc.task.registerCheckApiDependenciesTask
import tapmoc.task.registerCheckRuntimeDependenciesTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.tasks.SourceSetContainer

internal abstract class TapmocExtensionImpl(private val project: Project) : TapmocExtension {
  var kotlinVersion: String? = null

  override fun java(version: Int) {
    project.configureJavaCompatibility(version)
  }

  override fun kotlin(version: String) {
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
    val configurationProvider = project.configurations.register("tapmocCheck") {
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
      kotlinVersion = project.provider {
        kotlinVersion ?: error("Tapmoc: please call Tapmoc::kotlin(version) to specify the target Kotlin version.")
      },
      taskName = "tapmocCheckApiDependencies",
      compileClasspath = project.files(configurationProvider),
    )

    project.tasks.named("check").configure {
      it.dependsOn(checkApiDependencies)
    }
  }

  override fun checkRuntimeDependencies(severity: Severity) {
    if (severity == Severity.IGNORE) {
      return
    }

    /**
     * The "sourceSets" extension is added by the JvmEcosystemPlugin, which is applied by
     * the java, kotlin-jvm and kotlin-kmp (through `JavaBasePlugin`).
     * Doing this means we are also checking the test dependencies. Might or might not be a problem,
     * not 100% sure.
     * At least it's consistent with the apiVersion and languageVersion flags. If this is changed
     * to only include "main" classpaths, the way we apply flags should probably be changed as well.
     */
    val sourceSets = project.extensions.findByType(SourceSetContainer::class.java)
    if (sourceSets == null) {
      return
    }

    val lifecycleTask = project.tasks.register("tapmocCheckRuntimeDependencies")

    sourceSets.forEach {
      val configuration = project.configurations.named(it.runtimeClasspathConfigurationName)
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
        taskName = "tapmocCheck${it.name.capitalizeFirstLetter()}",
        warningAsError = project.provider { severity == Severity.ERROR },
        kotlinVersion = project.provider { kotlinVersion ?: error("Tapmoc: please call Tapmoc::kotlin(version) to specify the target Kotlin version.")  },
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
