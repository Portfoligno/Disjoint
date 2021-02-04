
plugins {
  `java-library`
  `java-test-fixtures`
  kotlin("jvm")
}

dependencies {
  testFixturesApi("io.kotest", "kotest-runner-junit5-jvm", "4.4.1")
}
