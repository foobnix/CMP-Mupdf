package mobi.librera.mupdf.platform

import mobi.librera.mupdf.Application

actual val MuFile: File = object : File {
    override fun createTempFile(name: String, data: ByteArray): String {
        return try {
            val tempFile = java.io.File(Application.appContext.cacheDir, name)
            tempFile.writeBytes(data)
            tempFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to create temp file: ${e.message}")
        }
    }
}
