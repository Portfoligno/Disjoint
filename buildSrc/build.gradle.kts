
plugins {
  `embedded-kotlin`
}

repositories {
  mavenCentral()
}
dependencies {
  compileOnly(gradleKotlinDsl())

  implementation("org.ow2.asm", "asm-tree", "9.1")
}
