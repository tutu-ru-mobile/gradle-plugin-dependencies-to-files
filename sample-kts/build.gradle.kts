plugins {
  id("ru.tutu.dependencies2files") version "SNAPSHOT"
  kotlin("jvm") version "1.4.30"
}

dependenciesToFiles {
  saveDependenciesDir = "save_dependencies2"
  offlineMode = false
}

repositories {
  mavenCentral()
  dependenciesToFiles.addRepository(this) {
    saveJcenter()
  }
}

dependencies {
  implementation("io.ktor:ktor-html-builder:1.5.1")
}
