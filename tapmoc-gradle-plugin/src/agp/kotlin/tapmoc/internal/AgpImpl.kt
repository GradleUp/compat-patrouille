package tapmoc.internal

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

private class AgpImpl(private val project: Project): Agp {
  override fun configureCompileJavaTasks(javaVersion: JavaVersion) {
    val android = project.extensions.findByName("android") as CommonExtension<*,*,*,*,*>?
    if (android != null) {
      android.compileOptions.apply {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
      }
    } else {
      /**
       * There is androidComponents {} but not android {} => This is the `com.android.kotlin.multiplatform.library` plugin
       * do nothing here, there is no compileJava task
       */
    }
  }

  override fun isAndroidJavaCompileTask(name: String): Boolean {
    // See https://cs.android.com/android-studio/platform/tools/base/+/da94db5fa35cdf1d97a02e705bf99fa4bf4676d4:build-system/gradle-core/src/main/java/com/android/build/gradle/tasks/JavaCompile.kt;l=67
    return Regex("compile.*JavaWithJavac").matches(name)
  }
}


/**
 * Return null if AGP is not applied
 */
internal fun Project.agp(): Agp? {
  if (extensions.findByName("androidComponents") == null) {
    return null
  }

  return AgpImpl(project)
}
