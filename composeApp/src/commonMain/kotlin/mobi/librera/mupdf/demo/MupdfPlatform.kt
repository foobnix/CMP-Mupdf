package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap

interface MupdfDocument {
    val pageCount: Int
    val title: String
    fun renderPage(page: Int): ImageBitmap
    fun renderPage(page: Int, width: Int): ImageBitmap
    fun close()
}

class EmptyDocument : MupdfDocument {
    override val pageCount: Int
        get() = TODO("Not yet implemented")
    override val title: String
        get() = TODO("Not yet implemented")

    override fun renderPage(page: Int): ImageBitmap {
        TODO("Not yet implemented")
    }

    override fun renderPage(page: Int, width: Int): ImageBitmap {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}


interface MupdfPlatform {
    val version: String
    fun openDocument(document: ByteArray): MupdfDocument
}

internal expect fun getMupdfPlatform(): MupdfPlatform