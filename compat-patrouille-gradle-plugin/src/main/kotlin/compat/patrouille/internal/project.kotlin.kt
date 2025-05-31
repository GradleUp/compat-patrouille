package compat.patrouille.internal

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal val Project.kotlinExtensionOrNull: KotlinProjectExtension? get() = extensions.findByName("kotlin") as KotlinProjectExtension?

val Project.kotlinExtension: KotlinProjectExtension get() = kotlinExtensionOrNull ?: error("no 'kotlin' extension found")

/**
 * This function is very simple but extracted to a separate file to avoid class loading issues
 * if KGP is not in the classpath
 */
fun isKmp(extension: Any): Boolean {
  return extension is KotlinMultiplatformExtension
}