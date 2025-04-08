package compat.patrouille.internal

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal val Project.kotlinExtensionOrNull: KotlinProjectExtension? get() = extensions.findByName("kotlin") as KotlinProjectExtension?

val Project.kotlinExtension: KotlinProjectExtension get() = kotlinExtensionOrNull ?: error("no 'kotlin' extension found")
