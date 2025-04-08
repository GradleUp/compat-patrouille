package compat.patrouille.internal

import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.androidJavaVersion(javaVersion: JavaVersion) {
    androidExtension.compileOptions.apply {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}
