package mobi.librera.mupdf.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.Foundation.NSTemporaryDirectory
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite

actual val MuFile: File = object : File {

    @OptIn(ExperimentalForeignApi::class)
    override fun createTempFile(name: String, data: ByteArray): String {
        val tempDir = NSTemporaryDirectory()
        val tempFilePath = "$tempDir/$name"
        memScoped {
            val file = fopen(tempFilePath, "wb")
            try {
                fwrite(data.toCValues(), 1.convert(), data.size.toULong(), file)
            } finally {
                fclose(file)
            }
        }
        return tempFilePath

    }
}
