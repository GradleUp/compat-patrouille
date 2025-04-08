package compat.patrouille

import compat.patrouille.internal.CompatPatrouilleExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project

class CompatPatrouillePlugin: Plugin<Project> {
  override fun apply(target: Project) {
    target.extensions.create(CompatPatrouilleExtension::class.java, "compatPatrouille", CompatPatrouilleExtensionImpl::class.java, target)
  }
}