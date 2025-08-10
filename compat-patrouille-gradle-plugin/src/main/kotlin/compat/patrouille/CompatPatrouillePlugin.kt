package compat.patrouille

import compat.patrouille.internal.CompatPatrouilleExtensionImpl
import gratatouille.wiring.GPlugin
import org.gradle.api.Project

@GPlugin(id = "com.gradleup.compat.patrouille")
fun plugin(target: Project) {
  target.extensions.create(
    CompatPatrouilleExtension::class.java,
    "compatPatrouille",
    CompatPatrouilleExtensionImpl::class.java,
    target,
  )
}
