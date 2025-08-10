import cast.cast
import com.nfeld.jsonpathkt.kotlinx.resolvePathOrNull
import java.util.zip.ZipInputStream
import kotlinx.metadata.jvm.KotlinModuleMetadata
import kotlinx.metadata.jvm.UnstableMetadataApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

plugins {
  id("org.jetbrains.kotlin.jvm").version("1.9.0")
  id("com.gradleup.compat.patrouille")
  id("maven-publish")
}

buildscript {
  dependencies {
    classpath("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    classpath("com.eygraber:jsonpathkt-kotlinx:3.0.2")
    classpath("net.mbonnin.cast:cast:0.0.1")
    classpath("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
    classpath("org.ow2.asm:asm:9.8")
  }
}

compatPatrouille {
  java(11)
  kotlin("1.9.0")
}
abstract class VerifyCompatibility : DefaultTask() {
  @get:InputFiles
  abstract val m2Dir: ConfigurableFileCollection

  /**
   * Verifies that:
   * - Gradle metadata contains has "org.gradle.jvm.version" set to "11"
   * - The .class files have a version of 55 (java 11).
   * - The .kotlin_module files have a Kotlin version of 1.9.0.
   *
   * A future version could test KMP.
   * .klib files can be read using "org.jetbrains.kotlin:kotlin-util-klib".
   * Not sure how to read the commonMetadata .jar artifact, though; it's a .jar that contains .kn data.
   * Interestingly, when using KMP, "org.gradle.jvm.version" is not set, not really sure why.
   */
  @OptIn(UnstableMetadataApi::class)
  @TaskAction
  fun taskAction() {
    val moduleFiles = m2Dir.files.filter { it.extension == "module" }.toList()

    check(moduleFiles.size == 1) {
      "Expected exactly one module file, got ${moduleFiles.size}: $moduleFiles"
    }

    moduleFiles.single().readText().let {
      Json.parseToJsonElement(it)
    }.resolvePathOrNull("$.variants.*.attributes[\"org.gradle.jvm.version\"]")!!
      .cast<JsonArray>()
      .map {
        it.jsonPrimitive.content
      }
      .forEach {
        check(it == "11") {
          "Expected all modules to be built with Java 11, got $it"
        }
      }

    val jarFiles = m2Dir.files.filter { it.extension == "jar" }.toList()
    check(moduleFiles.size == 1) {
      "Expected exactly one jar file, got ${jarFiles.size}: $jarFiles"
    }
    jarFiles.forEach { jarFile ->
      ZipInputStream(jarFile.inputStream()).use { zis ->
        var entry = zis.nextEntry
        while (entry != null) {
          if (entry.name.endsWith(".class")) {

            ClassReader(zis).accept(object : ClassVisitor(Opcodes.ASM9) {
              override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String?>?
              ) {
                check(version == 55) {
                  "Expected class files be of version 55, got $version"
                }
              }
            }, 0)
          } else if (entry.name.endsWith(".kotlin_module")) {
            val metadata = KotlinModuleMetadata.read(zis.readAllBytes())
            metadata.version.apply {
              check("$major.$minor.$patch" == "1.9.0") {
                "Expected Kotlin version 1.9.0, got $major.$minor.$patch."
              }
            }
          }
          entry = zis.nextEntry
        }
      }
    }
  }
}

group = "com.example"
version = "0.0.0-SNAPSHOT"
publishing {
  repositories {
    maven {
      name = "test"
      url = uri(file("build/m2"))
    }
  }
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}

tasks.register("cleanM2", Delete::class.java) {
  delete(file("build/m2"))
}
tasks.named("publishAllPublicationsToTestRepository") {
  dependsOn("cleanM2")
}
tasks.register("verifyCompatibility", VerifyCompatibility::class.java) {
  m2Dir.from(fileTree(layout.buildDirectory.dir("m2")))
  dependsOn("publishAllPublicationsToTestRepository")
}

