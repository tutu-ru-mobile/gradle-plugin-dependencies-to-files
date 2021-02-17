package ru.tutu.gradle

interface FilesConfig {
  fun saveJcenter() = saveMavenRepo("https://jcenter.bintray.com")
  fun saveMavenCentral() = saveMavenRepo("https://repo.maven.apache.org/maven2/")
  fun saveGoogle() = saveMavenRepo("https://dl.google.com/dl/android/maven2/")
  fun saveJitpack() = saveMavenRepo("https://jitpack.io")
  fun saveMavenRepo(url: String)
}
