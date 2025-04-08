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
}

