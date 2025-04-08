package compat.patrouille.internal

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

val Project.androidExtensionOrNull: CommonExtension<*, *, *, *, * >?
  get() {
    return (extensions.findByName("android") as CommonExtension<*, *, *, *, *>?)
  }

val Project.androidExtension: CommonExtension<*,*,*,*, *> get() = androidExtensionOrNull ?: error("No 'android' extension found.")