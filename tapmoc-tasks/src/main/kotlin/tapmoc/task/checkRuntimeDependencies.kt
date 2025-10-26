package tapmoc.task

import gratatouille.tasks.GLogger
import gratatouille.tasks.GOutputFile
import gratatouille.tasks.GTask

@GTask
internal fun checkRuntimeDependencies(
  logger: GLogger,
  warningAsError: Boolean,
  kotlinVersion: String,
  transitiveKotlinVersions: List<String>,
  output: GOutputFile
) {

  val supportedVersion = kotlinVersion.toMinorVersion()
  transitiveKotlinVersions.forEach { version ->
    if (version.toMinorVersion() > supportedVersion) {
      logger.logOrFail(warningAsError, "Found incompatible kotlin-stdlib: '$version'. Maximum supported is '$supportedVersion'. Use `./gradlew dependencies to investigate the dependency tree.")
    }
  }

  output.writeText("Nothing to see here, this file is just a marker that the task executed successfully.")
}

private class MinorVersion(val major: Int, val minor: Int): Comparable<MinorVersion> {
  override fun toString(): String {
    return "$major.$minor"
  }
  override fun compareTo(other: MinorVersion): Int {
    return compareValuesBy(this, other, { it.major }, { it.minor })
  }
}

private fun String.toMinorVersion(): MinorVersion {
  val c = split(".")
  require(c.size >= 2) {
    "Cannot parse Kotlin version '$this'. Expected format is major.minor.{extra}"
  }
  val supportedMajor = c[0].toInt()
  val supportedMinor = c[1].toInt()

  return MinorVersion(supportedMajor, supportedMinor)
}
