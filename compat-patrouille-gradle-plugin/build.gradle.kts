import com.gradleup.librarian.gradle.Librarian
import org.gradle.api.internal.artifacts.ivyservice.projectmodule.ProjectPublicationRegistry
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.Describables
import org.gradle.internal.DisplayName
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.plugin.use.internal.DefaultPluginId
import org.gradle.plugin.use.resolve.internal.local.PluginPublication

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.google.devtools.ksp")
  id("com.gradleup.gratatouille.wiring")
}

Librarian.module(project)

dependencies {
  compileOnly(libs.gradle.api)
  implementation(libs.gratatouille.wiring.runtime)
}

gratatouille {
  codeGeneration {
    addDependencies.set(false)
  }
  pluginLocalPublication("com.gradleup.compat.patrouille")
}

