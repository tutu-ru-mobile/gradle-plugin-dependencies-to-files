package ru.tutu.gradle

import io.ktor.utils.io.jvm.javaio.*
import java.io.InputStream

class ArtifactData(
  val content: InputStream
) {
  fun toByteReadChannel() = content.toByteReadChannel()
}
