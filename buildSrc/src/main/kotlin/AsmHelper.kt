
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION
import java.io.File

private fun fileSystem(uri: String) = fileSystem(URI.create(uri))
private fun fileSystem(uri: URI) = FileSystems.newFileSystem(uri, mapOf<String, String>())

fun Project.removePublicModifiers(
    classPath: String,
    annotationDescriptor: String
) {
  val fileName = if (version == DEFAULT_VERSION) name else "$name-$version"

  fileSystem("jar:" + File("$buildDir/libs/$fileName.jar").toURI()).use { fileSystem ->
    Files
        .walk(fileSystem.getPath(classPath))
        .filter { !Files.isDirectory(it) }
        .forEach { path ->
          val cls = ClassNode()

          Files.newInputStream(path).use {
            ClassReader(it).accept(cls, 0)
          }
          fun overwrite() {
            val writer = ClassWriter(0)
            cls.accept(writer)
            Files.write(path, writer.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING)
          }
          fun isTarget(a: List<AnnotationNode>?) = a != null && a.any { it.desc == annotationDescriptor }

          if (isTarget(cls.invisibleAnnotations)) {
            cls.access = cls.access and Opcodes.ACC_PUBLIC.inv()
            overwrite()
          } else {
            val methods = cls.methods.filter { isTarget(it.invisibleAnnotations) }

            if (methods.isNotEmpty()) {
              methods.forEach {
                it.access = it.access and Opcodes.ACC_PUBLIC.inv()
              }
              overwrite()
            }
          }
        }
  }
}
