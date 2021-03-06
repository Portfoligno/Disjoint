
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
