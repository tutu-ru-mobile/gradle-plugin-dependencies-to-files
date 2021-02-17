package ru.tutu.gradle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class SaveToFileManager(private val saveDirectory: Path) {
  private fun getSavePath(path: String): Path {
    require(!path.startsWith("/")) { "Path should not start with a slash" }
    return saveDirectory.resolve(path)
  }

  suspend fun getFromDiskOrSaveNew(path: String, saveLambda: suspend () -> ArtifactData?): ArtifactData? {
    val savePath = getSavePath(path)
    return if (Files.exists(savePath)) {
      useExistingFile(savePath)
    } else {
      saveLambda()?.also {
        saveResponseToCache(it, savePath)
      }
    }
  }

  private suspend fun saveResponseToCache(artifactData: ArtifactData, cachePath: Path) {
    withContext(Dispatchers.IO) {
      Files.createDirectories(saveDirectory)
      val tempDownloadedFile = Files.createTempFile(saveDirectory, ".", ".tmp")
      Files.newOutputStream(tempDownloadedFile).use { outputStream: OutputStream ->
        artifactData.content.copyTo(outputStream)
      }
      Files.createDirectories(cachePath.parent)
      Files.move(tempDownloadedFile, cachePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
    }
  }

  private fun useExistingFile(cachePath: Path): ArtifactData =
    ArtifactData(Files.newInputStream(cachePath))

}
