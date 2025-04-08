package compat.patrouille.internal

import compat.patrouille.CompatPatrouilleExtension
import compat.patrouille.configureJavaCompatibility
import compat.patrouille.configureKotlinCompatibility
import org.gradle.api.Project

internal abstract class CompatPatrouilleExtensionImpl(private val project: Project): CompatPatrouilleExtension {
  override fun java(version: Int) {
    project.configureJavaCompatibility(version)
  }

  override fun kotlin(version: String) {
    project.configureKotlinCompatibility(version)
  }
}