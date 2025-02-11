package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap

interface MupdfPlatform {

    val version: String
    fun renderPage(document: ByteArray, page: Int): ImageBitmap
}

internal expect fun getMupdfPlatform(): MupdfPlatform