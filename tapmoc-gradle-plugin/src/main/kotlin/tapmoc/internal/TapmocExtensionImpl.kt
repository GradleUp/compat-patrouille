package tapmoc.internal

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.attributes.Usage
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import tapmoc.Severity
import tapmoc.TapmocExtension
import tapmoc.configureJavaCompatibility
import tapmoc.configureKotlinCompatibility
import tapmoc.task.registerCheckJavaClassFilesVersionTask
import tapmoc.task.registerCheckKotlinMetadataTask
import tapmoc.task.registerCheckKotlinStdlibVersionsTask
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal abstract class TapmocExtensionImpl(private val project: Project) : TapmocExtension {
  private var kotlinMetadataSeverity = Severity.ERROR
  private var kotlinStdlibSeverity = Severity.ERROR

  private val apiDependencies: Provider<Configuration>
  private val runtimeDependencies: Provider<Configuration>
  private val checkMetadata: TaskProvider<out DefaultTask>
  private val checkKotlinStdlib: TaskProvider<out DefaultTask>

  abstract val kotlinVersionProvider: Property<String>
  abstract val javaVersionProvider: Property<Int>

  init {
    apiDependencies = project.configurations.register("tapmocApiDependencies") {
      it.isCanBeConsumed = false
      it.isCanBeResolved = true
      it.isVisible = false
    }

    checkMetadata = project.registerCheckKotlinMetadataTask(
      taskName = "tapmocCheckKotlinMetadata",
      warningAsError = project.provider { kotlinMetadataSeverity == Severity.ERROR },
      kotlinVersion = kotlinVersionProvider,
      files = project.files(apiDependencies),
    )

    runtimeDependencies = project.configurations.register("tapmocRuntimeDependencies") {
      it.isCanBeConsumed = false
      it.isCanBeResolved = true
      it.isVisible = false
    }

    checkKotlinStdlib = project.registerCheckKotlinStdlibVersionsTask(
      taskName = "tapmocCheckKotlinStdlibVersions",
      warningAsError = project.provider { kotlinStdlibSeverity == Severity.ERROR },
      kotlinVersion = kotlinVersionProvider,
      kotlinStdlibVersions = runtimeDependencies.map {
        it.incoming.resolutionResult.allComponents
          .mapNotNull { (it.id as? ModuleComponentIdentifier) }
          .filter {
            it.group == "org.jetbrains.kotlin" && it.module == "kotlin-stdlib"
          }.map {
            it.version
          }.toSet()
      },
    )

    val checkJavaClassFilesVersionTask = project.registerCheckJavaClassFilesVersionTask(
      taskName = "tapmocCheckJavaClassFilesVersion",
      warningAsError = project.provider { kotlinStdlibSeverity == Severity.ERROR },
      javaVersion = javaVersionProvider,
      jarFiles = project.files(apiDependencies, runtimeDependencies)
    )

    project.plugins.withType(LifecycleBasePlugin::class.java) {
      project.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
        it.dependsOn(checkKotlinStdlib)
        it.dependsOn(checkMetadata)
        it.dependsOn(checkJavaClassFilesVersionTask)
      }
    }
  }

  override fun java(version: Int) {
    javaVersionProvider.set(version)
    project.configureJavaCompatibility(version)
  }

  override fun kotlin(version: String) {
    kotlinVersionProvider.set(version)
    project.configureKotlinCompatibility(version)
  }

  override fun checkDependencies(severity: Severity) {
    checkApiDependencies(severity)
    checkRuntimeDependencies(severity)
  }

  override fun checkApiDependencies(severity: Severity) {
    if (severity == Severity.IGNORE) {
      return
    }
    kotlinMetadataSeverity = severity

    project.getConfigurations(UsageWrapper.JAVA_API).configureEach {
      apiDependencies.get().extendsFrom(it)
    }
  }

  override fun checkRuntimeDependencies(severity: Severity) {
    if (severity == Severity.IGNORE) {
      return
    }
    kotlinStdlibSeverity = severity

    project.getConfigurations(UsageWrapper.JAVA_RUNTIME).configureEach {
      runtimeDependencies.get().extendsFrom(it)
    }
  }
}

private enum class UsageWrapper(val value: String) {
  JAVA_API(Usage.JAVA_API),
  JAVA_RUNTIME(Usage.JAVA_RUNTIME)
}

/**
 * Retrieves the outgoing configurations for this project.
 *
 * We currently only check the JVM configurations. If JVM flags
 */
private fun Project.getConfigurations(usage: UsageWrapper): NamedDomainObjectSet<Configuration> {
  return configurations.matching {
    it.isCanBeConsumed
      && it.attributes.getAttribute(Usage.USAGE_ATTRIBUTE)?.name == usage.value
  }
}
