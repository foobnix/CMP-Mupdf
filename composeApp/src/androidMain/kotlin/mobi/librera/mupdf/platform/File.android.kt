package mobi.librera.mupdf.platform

import mobi.librera.mupdf.Application

actual val MuFile: File = object : File {
    override fun createTempFile(name: String, data: ByteArray): String {
        return try {
            val parent = Application.appContext.cacheDir
            if(!parent.exists()){
                parent.mkdirs()
            }

            val tempFile = java.io.File(parent, name)

            tempFile.writeBytes(data)

            if(!tempFile.exists()){
                throw RuntimeException("Failed to create temp file")
            }
            println("temp file size" + tempFile.length())

            tempFile.path

        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to create temp file: ${e.message}")
        }
    }
}
