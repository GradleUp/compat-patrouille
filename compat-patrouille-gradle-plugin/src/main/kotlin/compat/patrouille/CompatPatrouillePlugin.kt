package compat.patrouille

import compat.patrouille.internal.CompatPatrouilleExtensionImpl
import compat.patrouille.internal.isKmp
import compat.patrouille.internal.kotlinExtensionOrNull
import compat.patrouille.task.registerCheckApiDependenciesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

class CompatPatrouillePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.extensions.create(
      CompatPatrouilleExtension::class.java,
      "compatPatrouille",
      CompatPatrouilleExtensionImpl::class.java,
      target
    )
  }
}