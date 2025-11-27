package tapmoc

interface TapmocExtension {
  /**
   * Configures the version of Java to target.
   * This version is used as:
   * - targetCompatibility
   * - sourceCompatibility
   * - release (if not on android)
   *
   * @param version the version of Java to target.
   * Examples: 8, 11, 17, 21, 24,...
   */
  fun java(version: Int)

  /**
   * Configures the version of Kotlin to target.
   * This version is used as:
   * - languageVersion
   * - apiVersion
   * - coreLibrariesVersion
   *
   *
   * @param version the version of Kotlin to target.
   * This is a string in case you need a specific minor version in `coreLibrariesVersion`
   *
   * Examples: "1.9.0", "1.9.22", "2.0.21", "2.1.20",...
   */
  fun kotlin(version: String)

  /**
   * Makes `tapmocCheckApiDependencies` walk all the api dependencies transitively
   * and check that the metadata version in the `META-INF/${lib}.kotlin_module` file is compatible with
   * the specified kotlin version.
   * This is version n + 1 thanks to kotlinc n + 1 forward compatibility.
   *
   * @param severity what to do when a dependency is found to be incompatible.
   */
  fun checkApiDependencies(severity: Severity)

  /**
   * Makes `tapmocCheckRuntimeDependencies` walk all the runtime dependencies transitively
   * and checks that `kotlin-stdlib` is not upgraded to a version > n
   *
   * @param severity what to do when a dependency is found to be incompatible.
   */
  fun checkRuntimeDependencies(severity: Severity)

  /**
   * Calls both `checkpiDependencies(severity)` and `checkRuntimeDependencies(severity)`.
   *
   * ```kotlin
   * checkApiDependencies(severity)
   * checkRuntimeDependencies(severity)
   * ```
   */
  fun checkDependencies(severity: Severity)
}

enum class Severity {
  IGNORE,
  WARNING,
  ERROR
}
