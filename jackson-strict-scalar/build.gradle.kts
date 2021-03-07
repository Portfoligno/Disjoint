
plugins {
  maven
  `java-library`
  kotlin("jvm")
}

tasks["jar"].doLast {
  removePublicModifiers(
      classPath = "io/github/portfoligno/jackson/scalar",
      annotationDescriptor = "Lio/github/portfoligno/json/disjoint/utility/jvm/JvmPackagePrivate;"
  )
}

dependencies {
  implementation(kotlinStandardLibrary())
  api(platform(kotlin("bom")))
  api(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))

  compileOnly(project(":utility"))
  testCompileOnly(project(":utility")) // For IDE
  testImplementation(testFixtures(project(":utility")))

  implementation("com.fasterxml.jackson.core", "jackson-databind")

  testImplementation("io.kotest", "kotest-property-jvm", "4.4.1")
  testImplementation("com.fasterxml.jackson.module", "jackson-module-kotlin")
}
