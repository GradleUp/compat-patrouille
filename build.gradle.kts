import com.gradleup.librarian.gradle.Librarian

plugins {
  alias(libs.plugins.kgp.jvm).apply(false)
  alias(libs.plugins.librarian).apply(false)
  alias(libs.plugins.nmcp).apply(false)
  alias(libs.plugins.tapmoc).apply(false)
  alias(libs.plugins.gratatouille).apply(false)
  alias(libs.plugins.ksp).apply(false)
}

Librarian.root(project)

dependencies {
  add("nmcpTasks", libs.nmcp.tasks)
}
