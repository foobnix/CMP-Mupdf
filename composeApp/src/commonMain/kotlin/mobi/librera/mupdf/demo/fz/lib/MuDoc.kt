package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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

val mutex = Mutex()
abstract class MuDoc {
    abstract val pageCount: Int
    abstract val title: String
    abstract fun renderPage(page: Int, pageWidth: Int): ImageBitmap
    abstract fun close()

    fun renderPageSafe(page: Int, pageWidth: Int): ImageBitmap = runBlocking {
        mutex.withLock {
            renderPage(page, pageWidth)
        }
    }

    companion object EmptyDoc : MuDoc() {
        override val pageCount: Int = 0;
        override val title: String = "";
        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap =
            ImageBitmap(1, 1)

        override fun close() {}
    }
}