package compat.patrouille.internal

import org.gradle.api.Project

internal val Project.hasAndroid: Boolean get() = extensions.findByName("android") != null

internal val Project.hasKotlin: Boolean get() = extensions.findByName("kotlin") != null
