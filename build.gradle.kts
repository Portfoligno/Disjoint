
plugins {
  kotlin("jvm") version "1.4.30" apply false
}

tasks.getByName<Wrapper>("wrapper") {
  gradleVersion = "6.8.3"
}

subprojects {
  tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
  tasks.withType<Test> {
    useJUnitPlatform()
  }

  repositories {
    mavenCentral()
    maven("https://jitpack.io")
  }
}
