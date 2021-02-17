package ru.tutu.gradle

object TodoLog {
  fun logError(message: String, e: Throwable? = null) {
    println("ERROR: $message")
    e?.printStackTrace()
  }

  fun logInfo(message: String) {
    println("INFO: $message")
  }

  fun logDebug(message: String) {
    println("DEBUG: $message")
  }
}
