
plugins {
  maven
  `java-library`
  kotlin("jvm")
}

tasks["jar"].doLast {
  removePublicModifiers(
      classPath = "io/github/portfoligno/json/decode",
      annotationDescriptor = "Lio/github/portfoligno/json/disjoint/utility/jvm/JvmPackagePrivate;"
  )
}

dependencies {
  implementation(kotlinStandardLibrary())
  api(platform(kotlin("bom")))
  api(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))

  compileOnly(project(":utility"))

  api("io.github.portfoligno", "jackson-immutable-ast", "1.4.1")
  implementation("com.fasterxml.jackson.core", "jackson-databind")
}
