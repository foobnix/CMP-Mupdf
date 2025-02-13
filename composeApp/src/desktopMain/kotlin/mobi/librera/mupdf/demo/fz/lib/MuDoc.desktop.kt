package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage

internal actual fun openDocument(document: ByteArray): MuDoc {
    val common = CommonLib(document)
    return object : MuDoc {
        override val pageCount = common.fzPagesCount
        override val title = common.fzTitle

        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
            val (array, width, height) = common.renderPage(page, pageWidth)

            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            image.setRGB(0, 0, width, height, array, 0, width)

            return image.toComposeImageBitmap()
        }

        override fun close() {
            common.close()
        }
    }
}