
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.kotlin

fun DependencyHandler.kotlinStandardLibrary(): Dependency =
    create(kotlin("stdlib"), closureOf<ExternalModuleDependency> {
      exclude("org.jetbrains", "annotations")
    })

fun DependencyHandler.guava(): Dependency =
    create("com.google.guava:guava:30.1-jre", closureOf<ExternalModuleDependency> {
      exclude("com.google.code.findbugs", "jsr305")
      exclude("org.checkerframework", "checker-qual")
      exclude("com.google.errorprone", "error_prone_annotations")
      exclude("com.google.j2objc", "j2objc-annotations")
    })
