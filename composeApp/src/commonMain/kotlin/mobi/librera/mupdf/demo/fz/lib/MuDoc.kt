package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal expect fun openDocument(name: String, document: ByteArray, width: Int, height: Int, fontSize: Int): MuDoc

object FZ {
    val FZ_VERSION = "1.26.12"
    //val FZ_VERSION = "1.23.7"
}

val mutex = Mutex()

data class Outline(
    val title:String,
    val page:Int,
    val url:String,
    val level:Int,
)



abstract class MuDoc {
    abstract val pageCount: Int
    abstract val title: String
    abstract fun renderPage(page: Int, pageWidth: Int): ImageBitmap
    abstract fun close()

    abstract suspend fun getOutline():List<Outline>

    suspend fun renderPageSafe(page: Int, pageWidth: Int): ImageBitmap {
        mutex.withLock {
            return renderPage(page, pageWidth)
        }
    }

    companion object EmptyDoc : MuDoc() {
        override val pageCount: Int = 0;
        override val title: String = "";
        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap = ImageBitmap(1, 1)

        override fun close() {}
        override suspend fun getOutline(): List<Outline>  = emptyList()

    }
}