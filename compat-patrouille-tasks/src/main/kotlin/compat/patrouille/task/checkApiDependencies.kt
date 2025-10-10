package compat.patrouille.task

import gratatouille.tasks.GClasspath
import gratatouille.tasks.GLogger
import gratatouille.tasks.GOutputFile
import gratatouille.tasks.GTask
import java.io.File
import java.util.zip.ZipInputStream
import kotlin.metadata.jvm.JvmMetadataVersion
import kotlin.metadata.jvm.KotlinModuleMetadata
import kotlin.metadata.jvm.UnstableMetadataApi

@OptIn(UnstableMetadataApi::class)
@GTask
internal fun checkApiDependencies(
  logger: GLogger,
  warningAsError: Boolean,
  kotlinVersion: String,
  compileClasspath: GClasspath,
  output: GOutputFile
) {
  val c = kotlinVersion.split(".")
  require(c.size == 3) {
    "Cannot parse Kotlin version $kotlinVersion. Expected format is X.Y.Z."
  }
  var supportedMajor = c[0].toInt()
  var supportedMinor = c[1].toInt()

  if (supportedMajor == 1 && supportedMinor == 9) {
    // 1.9 can read 2.0 metadata
    supportedMajor = 2
    supportedMinor = 0
  } else {
    // n + 1 forward compatibility in the general case
    supportedMinor += 1
  }

  val supportedVersion = JvmMetadataVersion(supportedMajor, supportedMinor, 0)

  compileClasspath.forEach { fileWithPath ->
    fileWithPath.file.forEachModuleInfoFile { name, bytes ->
      val metadata = KotlinModuleMetadata.read(bytes)
      if (metadata.version > supportedVersion) {
        logger.logOrFail(warningAsError, "w: ${fileWithPath.file.path}:$name contains unsupported metadata ${metadata.version} (expected: $kotlinVersion).  Use `./gradlew dependencies to investigate the dependency tree.")
      }
    }
  }

  output.writeText("Nothing to see here, this file is just a marker that the task executed successfully.")
}

private fun File.forEachModuleInfoFile(block: (String, ByteArray) -> Unit) {
  ZipInputStream(inputStream()).use { zis ->
    var entry = zis.nextEntry
    while (entry != null) {
      if (entry.name.matches(Regex("META-INF/.*\\.kotlin_module"))) {
        block(entry.name, zis.readBytes())
      }
      entry = zis.nextEntry
    }
  }
}

internal fun GLogger.logOrFail(warningAsError: Boolean, message: String) {
  if (warningAsError) {
    kotlin.error(message)
  } else {
    warn(message)
  }
}

