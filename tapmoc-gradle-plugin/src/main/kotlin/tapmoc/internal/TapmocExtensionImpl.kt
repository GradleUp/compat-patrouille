package tapmoc.internal

import com.android.tools.r8.internal.bl
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.attributes.Usage
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.language.base.plugins.LifecycleBasePlugin
import tapmoc.Severity
import tapmoc.TapmocExtension
import tapmoc.configureJavaCompatibility
import tapmoc.configureKotlinCompatibility
import tapmoc.task.registerTapmocCheckClassFileVersionsTask
import tapmoc.task.registerTapmocCheckKotlinMetadataVersionsTask
import tapmoc.task.registerTapmocCheckKotlinStdlibVersionsTask

internal abstract class TapmocExtensionImpl(private val project: Project) : TapmocExtension {
  private var kotlinMetadataSeverity = Severity.ERROR
  private var kotlinStdlibSeverity = Severity.ERROR

  private val apiDependencies: Provider<Configuration>
  private val runtimeDependencies: Provider<Configuration>

  abstract val kotlinVersionProvider: Property<String>
  abstract val javaVersionProvider: Property<Int>

  init {
    apiDependencies = project.configurations.register("tapmocApiDependencies") {
      it.isCanBeConsumed = false
      it.isCanBeResolved = true
      it.isVisible = false
    }

    runtimeDependencies = project.configurations.register("tapmocRuntimeDependencies") {
      it.isCanBeConsumed = false
      it.isCanBeResolved = true
      it.isVisible = false
    }

    val checkKotlinMetadatas = project.registerTapmocCheckKotlinMetadataVersionsTask(
      warningAsError = project.provider { kotlinMetadataSeverity == Severity.ERROR },
      kotlinVersion = kotlinVersionProvider,
      files = project.files(apiDependencies),
    )

    val checkKotlinStdlibs = project.registerTapmocCheckKotlinStdlibVersionsTask(
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

    val checkJavaClassFiles = project.registerTapmocCheckClassFileVersionsTask(
      warningAsError = project.provider { kotlinStdlibSeverity == Severity.ERROR },
      javaVersion = javaVersionProvider,
      jarFiles = project.files(apiDependencies, runtimeDependencies)
    )

    project.plugins.withType(LifecycleBasePlugin::class.java) {
      project.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
        it.dependsOn(checkKotlinStdlibs)
        it.dependsOn(checkKotlinMetadatas)
        it.dependsOn(checkJavaClassFiles)
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

  override fun checkDependencies() {
    checkDependencies(Severity.ERROR)
  }

  @Suppress("DEPRECATION")
  override fun checkDependencies(severity: Severity) {
    checkApiDependencies(severity)
    checkRuntimeDependencies(severity)
  }

  @Deprecated("Use checkDependencies instead.", replaceWith = ReplaceWith("checkDependencies(severity)"))
  override fun checkApiDependencies(severity: Severity) {
    if (severity == Severity.IGNORE) {
      return
    }
    kotlinMetadataSeverity = severity

    project.onEachOutgoingConfiguration(UsageWrapper.JAVA_API) {
      apiDependencies.get().extendsFrom(it)
    }
  }

  @Deprecated("Use checkDependencies instead.", replaceWith = ReplaceWith("checkDependencies(severity)"))
  override fun checkRuntimeDependencies(severity: Severity) {
    if (severity == Severity.IGNORE) {
      return
    }
    kotlinStdlibSeverity = severity

    project.onEachOutgoingConfiguration(UsageWrapper.JAVA_RUNTIME) {
      runtimeDependencies.get().extendsFrom(it)
    }
  }
}

private enum class UsageWrapper(val value: String) {
  JAVA_API(Usage.JAVA_API),
  JAVA_RUNTIME(Usage.JAVA_RUNTIME)
}

/**
 * Retrieves the outgoing configurations for this project and the specific usage.
 * Because plugins may be applied at any time, and they set the attributes, we need to iterate
 * the ConfigurationContainer only after they have been applied
 *
 * We currently only check the JVM configurations since .klib files do not support compatibility flags yet.
 */
private fun Project.onEachOutgoingConfiguration(usage: UsageWrapper, block: (Configuration) -> Unit) {
  var hasKgpOrJava = false

  val callback = {
    if (!hasKgpOrJava) {
      hasKgpOrJava = true
      configurations.matching {
        it.isCanBeConsumed
          && it.attributes.getAttribute(Usage.USAGE_ATTRIBUTE)?.name == usage.value
      }.configureEach {
        block(it)
      }
    }
  }

  onKgp {
    callback()
  }
  pluginManager.withPlugin("java") {
    callback()
  }
}


