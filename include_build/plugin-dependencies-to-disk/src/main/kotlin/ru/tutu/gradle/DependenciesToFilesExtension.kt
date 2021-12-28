package ru.tutu.gradle

import groovy.lang.Closure
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

data class ProxyRepoData(
  val originalRepoUrl: String,
  val pathSegment:String
)

open class DependenciesToFilesExtension(projectDir: String) {

  private var _port: Int? = null
  var port: Int
    @Synchronized
    get() {
      val resultPort = _port ?: Random.nextInt(10_000, 50_000) //todo check availability
      _port = resultPort
      return resultPort
    }
    @Synchronized
    set(value: Int) {
      if (_port != null) {
        throw Error("""
          use plugin extension block:
          dependenciesToFiles {
            ...
            port = $value
            ...
          }
          before repositories {
            ...
          }
        """.trimIndent())
      }
      _port = value
    }

  var saveDependenciesDir: String = Path.of(projectDir).resolve("save_dependencies").toAbsolutePath().toString()
  var offlineMode = false
  internal val repoUrlToPort: MutableMap<String, ProxyRepoData> = ConcurrentHashMap()

  @Suppress("unused")
  fun addRepository(handler: RepositoryHandler, configureClosure: Closure<FilesConfig>) {
    innerFilesRepository(handler) {
      configureClosure.delegate = it
      configureClosure.call()
    }
  }

  @Suppress("unused")
  fun addRepository(handler: RepositoryHandler, lambda: FilesConfig.() -> Unit) {
    innerFilesRepository(handler) {
      it.lambda()
    }
  }

  private fun innerFilesRepository(handler: RepositoryHandler, lambda: (FilesConfig) -> Unit) {
    val filesConfig = object : FilesConfig {
      override fun saveMavenRepo(url: String) {
        val proxyRepoData = repoUrlToPort.getOrPut(url) {
          ProxyRepoData(
            originalRepoUrl = url,
            pathSegment = url.hashCode().toString()
          )
        }
        val pathSegment = proxyRepoData.pathSegment
        handler.maven {
          setUrl("http://localhost:$port/$pathSegment")
          isAllowInsecureProtocol = true
        }
      }
    }
    lambda(filesConfig)
  }

}
