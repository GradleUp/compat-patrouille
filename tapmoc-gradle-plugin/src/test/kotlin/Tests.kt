import java.io.File
import java.util.Properties
import kotlin.test.Test
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertTrue

class Tests {
  @Test
  fun wrongJavaBytecodeIsDetected() {
    withTestProject("java") {
      val result = GradleRunner.create()
        .withProjectDir(it)
        .withDebug(false)
        .withArguments("build")
        .forwardOutput()
        .buildAndFail()

      assertTrue(result.output.contains("targets class file version 55 (Java 11) which is newer than supported <= 52 (Java 8)."))
    }
  }

  @Test
  fun metaInfIsExcluded() {
    withTestProject("java-meta-inf") {
      GradleRunner.create()
        .withProjectDir(it)
        .withDebug(false)
        .withArguments("build")
        .forwardOutput()
        .build()
    }
  }
}



private fun withTestProject(name: String, block: (File) -> Unit) {
  val src = File("testProjects/$name")
  val dst = File("build/testProject")
  dst.deleteRecursively()

  src.copyRecursively(dst)

  dst.walk().onLeave {
    if (it.isDirectory && it.name == "build") {
      it.deleteRecursively()
    }
  }.count() // count is just used to collect the sequence

  val currentVersion = Properties().apply {
    File("../librarian.root.properties").reader().use {
      load(it)
    }
  }
  dst.resolve("build.gradle.kts").let {
    it.writeText(it.readText().replace("PLACEHOLDER", currentVersion.get("pom.version").toString()))
  }
  block(dst)
}
