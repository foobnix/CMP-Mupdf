package mobi.librera.mupdf.platform

actual val MuFile: File = object : File {
    override fun createTempFile(name: String, data: ByteArray): String {
        val tempFile = java.io.File.createTempFile("librera", name)
        tempFile.writeBytes(data)
        return tempFile.path
    }

}
