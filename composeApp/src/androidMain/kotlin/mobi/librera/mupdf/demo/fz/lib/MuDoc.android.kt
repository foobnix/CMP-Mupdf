package mobi.librera.mupdf.demo.fz.lib

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

internal actual fun openDocument(document: ByteArray): MuDoc {
    val common = CommonLib(document)
    return object : MuDoc {
        override val pageCount = common.fzPagesCount
        override val title = common.fzTitle

        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
            val (array, width, height) = common.renderPage(page, pageWidth)

            val image = Bitmap.createBitmap(
                array,
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
            return image.asImageBitmap()
        }

        override fun close() {
            common.close()
        }
    }
}