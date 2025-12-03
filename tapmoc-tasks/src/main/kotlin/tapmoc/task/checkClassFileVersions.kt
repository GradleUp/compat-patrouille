package tapmoc.task

import gratatouille.tasks.GInputFiles
import gratatouille.tasks.GLogger
import gratatouille.tasks.GOutputFile
import gratatouille.tasks.GTask
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.util.zip.ZipInputStream

@GTask
internal fun tapmocCheckClassFileVersions(
  logger: GLogger,
  warningAsError: Boolean,
  jarFiles: GInputFiles,
  javaVersion: Int?,
  output: GOutputFile
) {
  if (javaVersion == null) {
    output.writeText("Tapmoc: skip checking class file versions as no target Java version is defined")
    return
  }
  val maxAllowedClassFileVersion = 44 + javaVersion

  jarFiles.forEach { fileWithPath ->
    ZipInputStream(fileWithPath.file.inputStream()).use { zis ->
      var entry = zis.nextEntry
      while (entry != null) {
        if (!entry.isDirectory && entry.name.endsWith(".class", ignoreCase = true) && !entry.name.startsWith("META-INF/versions")) {
          val classBytes = zis.readBytes()
          val cr = ClassReader(classBytes)
          var classFileVersion = -1

          cr.accept(
            object : ClassVisitor(Opcodes.ASM9) {
              override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String>?
              ) {
                classFileVersion = version
              }
            },
            ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
          )

          if (classFileVersion > maxAllowedClassFileVersion) {
            val humanReadable = "class file version $classFileVersion (Java ${classFileVersion - 44})"
            val expectedHuman = "<= $maxAllowedClassFileVersion (Java $javaVersion)"
            logger.logOrFail(
              warningAsError,
              "${fileWithPath.file.path}:${entry.name} targets $humanReadable which is newer than supported $expectedHuman."
            )
          }
        }
        entry = zis.nextEntry
      }
    }
  }

  output.writeText("Nothing to see here, this file is just a marker that the task executed successfully.")
}

