
plugins {
  maven
  `java-library`
  kotlin("jvm")
}

tasks["jar"].doLast {
  removePublicModifiers(
      classPath = "io/github/portfoligno/json/disjoint",
      annotationDescriptor = "Lio/github/portfoligno/json/disjoint/utility/JvmPackagePrivate;"
  )
}

dependencies {
  api(kotlinStandardLibrary())
  api(platform(kotlin("bom")))
  api(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))

  compileOnly(project(":utility"))
  testCompileOnly(project(":utility")) // For IDE
  testImplementation(testFixtures(project(":utility")))
  testImplementation(project(":jackson-strict-scalar"))

  compileOnly("com.fasterxml.jackson.core", "jackson-databind")
  implementation(guava())

  testImplementation("com.fasterxml.jackson.module", "jackson-module-kotlin")
}
