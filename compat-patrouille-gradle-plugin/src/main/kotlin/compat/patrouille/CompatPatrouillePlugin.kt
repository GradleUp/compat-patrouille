package tapmoc

import tapmoc.internal.CompatPatrouilleExtensionImpl
import gratatouille.wiring.GPlugin
import org.gradle.api.Project

@GPlugin(id = "com.gradleup.tapmoc")
fun tapmocPlugin(target: Project) {
  target.extensions.create(
    CompatPatrouilleExtension::class.java,
    "tapmoc",
    CompatPatrouilleExtensionImpl::class.java,
    target,
  )
}
