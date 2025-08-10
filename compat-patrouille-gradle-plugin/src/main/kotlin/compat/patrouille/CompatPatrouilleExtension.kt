package compat.patrouille

interface CompatPatrouilleExtension {
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
   * If [check] is true, registers a `compatPatrouilleCheckApiDependencies` task that walks all the api dependencies
   * and checks that the metadata version in the `META-INF/${lib}.kotlin_module` file is compatible with
   * the specified kotlin version.
   * This is version n + 1 thanks to kotlinc n + 1 forward compatibility.
   *
   * @param severity what to do when a dependency is found to be incompatible.
   * Defaults to [Severity.IGNORE].
   */
  fun checkApiDependencies(severity: Severity)

  /**
   * If [check] is true, registers a `compatPatrouilleCheckRuntimeDependencies` task that walks all the runtime dependencies
   * and checks that `kotlin-stdlib` is not upgraded to a version > n
   *
   * @param severity what to do when a dependency is found to be incompatible.
   * Defaults to [Severity.IGNORE].
   */
  fun checkRuntimeDependencies(severity: Severity)
}

enum class Severity {
  IGNORE,
  WARNING,
  ERROR
}
