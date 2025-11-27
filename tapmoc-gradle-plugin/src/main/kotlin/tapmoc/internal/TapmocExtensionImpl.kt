package tapmoc.internal

import org.gradle.api.NamedDomainObjectSet
import tapmoc.TapmocExtension
import tapmoc.Severity
import tapmoc.configureJavaCompatibility
import tapmoc.configureKotlinCompatibility
import tapmoc.task.registerCheckKotlinMetadataTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.attributes.Usage
import org.gradle.api.provider.Provider
import tapmoc.task.registerCheckKotlinStdlibVersionsTask

internal abstract class TapmocExtensionImpl(private val project: Project) : TapmocExtension {
  private var kotlinVersion: String? = null
  private var kotlinMetadataSeverity = Severity.ERROR
  private var kotlinStdlibSeverity = Severity.ERROR

  private val apiDependencies: Provider<Configuration>
  private val runtimeDependencies: Provider<Configuration>

  init {
    val kotlinVersionProvider = project.provider {
      kotlinVersion ?: error("Tapmoc: please call Tapmoc::kotlin(version) to specify the target Kotlin version.")
    }
    apiDependencies = project.configurations.register("tapmocApiDependencies") {
      it.isCanBeConsumed = false
      it.isCanBeResolved = true
      it.isVisible = false
    }

    val checkMetadata = project.registerCheckKotlinMetadataTask(
      taskName = "tapmocCheckKotlinMetadata",
      warningAsError = project.provider { kotlinMetadataSeverity == Severity.ERROR },
      kotlinVersion = kotlinVersionProvider,
      files = project.files(apiDependencies),
    )

    project.tasks.named("check").configure { it.dependsOn(checkMetadata) }

    runtimeDependencies = project.configurations.register("tapmocRuntimeDependencies") {
      it.isCanBeConsumed = false
      it.isCanBeResolved = true
      it.isVisible = false
    }

    val checkKotlinStdlib = project.registerCheckKotlinStdlibVersionsTask(
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
    project.tasks.named("check").configure { it.dependsOn(checkKotlinStdlib) }
  }

  override fun java(version: Int) {
    project.configureJavaCompatibility(version)
  }

  override fun kotlin(version: String) {
    kotlinVersion = version
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
