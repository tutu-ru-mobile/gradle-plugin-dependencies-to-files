import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("java-gradle-plugin")
  `maven-publish`
  id("com.gradle.plugin-publish") version "0.13.0"
  `kotlin-dsl`
}

val PLUGIN_NAME = "dependencies to files"
val PLUGIN_ID = "ru.tutu.dependencies2files"
val VERSION = "0.3.0"
val TAGS = listOf("dependency", "repository", "jcenter", "offline")

group = "ru.tutu"
version = VERSION

repositories {
  mavenCentral()
}

configure<JavaPluginConvention> {//todo redundant?
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"//todo simplify in root build.gralde.kts
}

gradlePlugin {
  plugins {
    create(PLUGIN_ID) {
      id = PLUGIN_ID
      implementationClass = "ru.tutu.gradle.PluginDependenciesToFiles"
    }
  }
}

pluginBundle {
  website = "https://github.com/tutu-ru-mobile/gradle-plugin-dependencies-to-disk"
  vcsUrl = "https://github.com/tutu-ru-mobile/gradle-plugin-dependencies-to-disk.git"
  description = "Plugin save repository dependencies to files. May be helpful to save jcenter dependencies, used by your project"
  tags = TAGS

  (plugins) {
    PLUGIN_ID {
      // id is captured from java-gradle-plugin configuration
      displayName = PLUGIN_NAME
      tags = TAGS
      version = VERSION
    }
  }
}

dependencies {
  compileOnly(gradleApi())

  implementation(if (true) "ch.qos.logback:logback-classic:1.2.3" else "org.slf4j:slf4j-simple:1.7.28")
  implementation("io.ktor:ktor-server-netty:$KTOR_VERSION")
  implementation("io.ktor:ktor-server-cio:$KTOR_VERSION")
  implementation("io.ktor:ktor-client-core:$KTOR_VERSION")
  implementation("io.ktor:ktor-client-apache:$KTOR_VERSION")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:$KOTLIN_VERSION")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION")

  testImplementation("org.slf4j:slf4j-jdk14:1.7.25")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  testImplementation(gradleTestKit())
}
