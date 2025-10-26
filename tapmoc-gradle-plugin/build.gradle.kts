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

val agp = kotlin.target.compilations.create("agp")
dependencies {
  add(agp.compileOnlyConfigurationName, libs.agp)
  add(agp.compileOnlyConfigurationName, libs.gradle.api)
}
val main = kotlin.target.compilations.getByName("main")

main.associateWith(agp)
tasks.jar {
  from(agp.output.classesDirs)
}

/**
 * associateWith() pulls the secondary compilations into the main dependencies,
 * which we don't want.
 *
 * An alternative would be to not use `associateWith()` but that fails in the IDE,
 * probably because there is no way to set `AbstractKotlinCompile.friendSourceSets`
 * from public API.
 */
configurations.compileOnly.get().dependencies.removeIf {
  when {
    it is ExternalDependency && it.group == "com.android.tools.build" && it.name == "gradle" -> true
    else -> false
  }
}


dependencies {
  compileOnly(libs.gradle.api)
  compileOnly(libs.kgp.compile.only)
  implementation(libs.gratatouille.wiring.runtime)
  gratatouille(project(":tapmoc-tasks"))
}

gratatouille {
  codeGeneration {
    addDependencies.set(false)
  }
  pluginLocalPublication("com.gradleup.tapmoc")
}

