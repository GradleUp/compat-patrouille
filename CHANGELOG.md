# 0.0.3
_2025-10-06_

Do not configure `JavaCompile` tasks eagerly (https://github.com/GradleUp/tapmoc/issues/27)

# 0.0.2
_2025-08-20_

A few bugfixes, upgrades and ergonomics improvements. Many thanks @OliverO2 and @Mr3zee for their feedback in this release.

* [NEW] Add tapmocCheckRuntimeDependencies as a lifecycle task https://github.com/GradleUp/tapmoc/pull/23
* [NEW] For JS and Wasm, add the KGP kotlin-stdlib instead of relying on `coreLibrariesVersion` https://github.com/GradleUp/tapmoc/pull/24
* [FIX] Make checkApiDependencies lazier https://github.com/GradleUp/tapmoc/pull/20
* [FIX] Fix KMP with multiple targets https://github.com/GradleUp/tapmoc/pull/21
* [UPGRADE] Use latest kotlin-metadata lib https://github.com/GradleUp/tapmoc/pull/22

# 0.0.1
_2025-08-11_

Version `0.0.1` adds two new tasks to check the API and Runtime dependencies and fixes declaring the Kotlin compatibility of common source sets.

* [NEW] Introduce `checkApiDependencies()` by @martinbonnin in https://github.com/GradleUp/tapmoc/pull/2
* [NEW] Introduce `checkRuntimeDependencies()` by @martinbonnin in https://github.com/GradleUp/tapmoc/pull/16
* [FIX] Fix detecting apiVersion in `commonMain` and `commonTest` source sets by @martinbonnin in https://github.com/GradleUp/tapmoc/pull/7
* [UPDATE] update KGP, simplify GitHub actions files by @martinbonnin in https://github.com/GradleUp/tapmoc/pull/9
* [UPDATE] Bump gradle to 9 by @martinbonnin in https://github.com/GradleUp/tapmoc/pull/15
* [MISC] Add more integration tests by @martinbonnin in https://github.com/GradleUp/tapmoc/pull/10

# 0.0.0
_2025-04-08_

Initial release 🎉
