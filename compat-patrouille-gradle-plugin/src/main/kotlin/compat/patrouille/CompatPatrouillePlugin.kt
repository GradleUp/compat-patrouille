package compat.patrouille

import compat.patrouille.internal.CompatPatrouilleExtensionImpl
import gratatouille.wiring.GPlugin
import org.gradle.api.Project

@GPlugin(id = "com.gradleup.compat.patrouille")
fun tapmocPlugin(target: Project) {
  target.extensions.create(
    CompatPatrouilleExtension::class.java,
    "tapmoc",
    CompatPatrouilleExtensionImpl::class.java,
    target,
  )
}
