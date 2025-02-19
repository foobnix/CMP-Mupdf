package mobi.librera.mupdf.platform

expect val MuFile: File

interface File {
    fun createTempFile(name: String, data: ByteArray): String
}