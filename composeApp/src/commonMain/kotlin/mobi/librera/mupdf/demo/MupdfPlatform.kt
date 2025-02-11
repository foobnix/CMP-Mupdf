package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap

interface MupdfDocument {
    val pageCount:Int
    val title: String
    fun renderPage(page: Int): ImageBitmap
    fun renderPage(page: Int, width:Int): ImageBitmap
    fun close()
}

interface MupdfPlatform {
    val version: String
    fun openDocument(document: ByteArray): MupdfDocument
}

internal expect fun getMupdfPlatform(): MupdfPlatform