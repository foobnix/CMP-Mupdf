package mobi.librera.mupdf.demo

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.Foundation.NSTemporaryDirectory
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite

class FileIO {
}

@OptIn(ExperimentalForeignApi::class)
fun createTempFile(prefix: String, data: ByteArray): String? {
    val tempDir = NSTemporaryDirectory()

    val tempFilePath = "$tempDir/file1.pdf"
    memScoped {
        val file = fopen(tempFilePath, "wb") ?: return null
        try {
            fwrite(data.toCValues(), 1.convert(), data.size.toULong(), file)
        } finally {
            fclose(file)
        }
    }
    return tempFilePath
}
