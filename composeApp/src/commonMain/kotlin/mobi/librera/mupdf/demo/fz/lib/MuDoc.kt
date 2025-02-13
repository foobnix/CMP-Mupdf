
package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap

internal expect fun openDocument(document: ByteArray): MuDoc


expect object Logger {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
}

object FZ {
    val FZ_VERSION = "1.25.4"
}

interface MuDoc {
    val pageCount: Int
    val title: String
    fun renderPage(page: Int, pageWidth: Int): ImageBitmap
    fun close()

    companion object EmptyDoc : MuDoc {
        override val pageCount: Int = 0;
        override val title: String = "";
        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap =
            ImageBitmap(1, 1)

        override fun close() {}
    }
}

fun ByteArray.toIntArray(): IntArray {
    require(size % 4 == 0) { "ByteArray size must be a multiple of 4" }
    return IntArray(size / 4) { i ->
        (this[i * 4].toInt() and 0xFF) or
                ((this[i * 4 + 1].toInt() and 0xFF) shl 8) or
                ((this[i * 4 + 2].toInt() and 0xFF) shl 16) or
                ((this[i * 4 + 3].toInt() and 0xFF) shl 24)
    }
}

fun IntArray.toByteArray(): ByteArray {
    val byteArray = ByteArray(size * 4)
    forEachIndexed { index, value ->
        byteArray[index * 4] = (value and 0xFF).toByte()
        byteArray[index * 4 + 1] = ((value shr 8) and 0xFF).toByte()
        byteArray[index * 4 + 2] = ((value shr 16) and 0xFF).toByte()
        byteArray[index * 4 + 3] = ((value shr 24) and 0xFF).toByte()
    }
    return byteArray
}