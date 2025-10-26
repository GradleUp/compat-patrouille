#!/usr/bin/env kotlin

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:Repository("https://storage.googleapis.com/gradleup/m2")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.gradleup.librarian:librarian-cli:0.1.1-SNAPSHOT-41af255deeb3b644524aed9ffe4c64aad5ebbd0b")

import com.gradleup.librarian.cli.updateRepo

updateRepo(args) {
  file("README.md") {
    replacePluginVersion("com.gradleup.tapmoc")
  }
}
