package ru.tutu.gradle

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.event.Level
import java.nio.file.Path

val client = HttpClient(Apache) {
  engine {
    followRedirects = true
    //todo install compression
  }
}

@Suppress("unused")
class PluginDependenciesToFiles : Plugin<Project> {
  lateinit var extensionConfig: DependenciesToFilesExtension
  override fun apply(project: Project) {
    extensionConfig = project.extensions.create(
      "dependenciesToFiles",
      DependenciesToFilesExtension::class.java,
      project.projectDir.absolutePath
    )
    project.afterEvaluate {
      afterProjectEvaluate(project)
    }
  }

  private fun afterProjectEvaluate(project: Project) {
    val port = extensionConfig.port
    embeddedServer(Netty, port = port) {
      install(CallLogging) {
        level = Level.INFO
      }
      install(AutoHeadResponse)
      routing {
        get("/healthz") {
          call.respondText {
            "I am ok"
          }
        }
      }
      val saveDependenciesDir = Path.of(project.file("./").absolutePath).resolve(extensionConfig.saveDependenciesDir)
      extensionConfig.repoUrlToPort.values.forEach { proxyRepoData ->
        val cacheManager = SaveToFileManager(saveDependenciesDir)
        routing {
          get("/${proxyRepoData.pathSegment}/{path...}") {
            val path = call.parameters.getAll("path").orEmpty().joinToString("/")
            println("call: $call")
            println("path: $path")
            val artifactData = cacheManager.getFromDiskOrSaveNew(path) {
              if (extensionConfig.offlineMode) {
                null
              } else {
                requestFromOriginalServer(proxyRepoData.originalRepoUrl, path)
              }
            }
            if (artifactData == null) {
              call.respondText("missing", status = HttpStatusCode.NotFound)
            } else {
              call.respond(object : OutgoingContent.ReadChannelContent() {
                override fun readFrom() = artifactData.toByteReadChannel()
              })
            }
          }
        }
      }
    }.start(wait = false)

    runBlocking {
      //wait server health check
      do {
        delay(1)
        val isSuccess = try {
          val urlString = "http://localhost:$port/healthz"
          println("healthz request, urlString: $urlString")
          val response = client.get<HttpResponse>(urlString)
          response.status.isSuccess()
        } catch (t: Throwable) {
          false
        }
      } while (!isSuccess)
    }
  }
}

suspend fun requestFromOriginalServer(originalRepoUrl: String, path: String): ArtifactData? {
  val requestUrl = "$originalRepoUrl/$path"
  try {
    val response = client.get<HttpResponse>(requestUrl)
    if (response.status.isSuccess()) {
      TodoLog.logDebug("Found '$path' at '$originalRepoUrl'")
      return withContext(Dispatchers.IO) {
        ArtifactData(response.content.toInputStream(parent = null))
      }
    } else {
      TodoLog.logError("$path, response status: ${response.status}")
      return null
    }
  } catch (t:Throwable) {
    TodoLog.logError("error in requestFromOriginalServer", t)
    return null
  }
}
