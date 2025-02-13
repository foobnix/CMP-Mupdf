package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import mobi.librera.mupdf.demo.fz.lib.CommonLib
import java.awt.image.BufferedImage

internal actual fun getMupdfPlatform(): MupdfPlatform = MupdfDesktop2()

class MupdfDesktop2 : MupdfPlatform {

    override val version: String = "Context.getVersion().version"

    override fun openDocument(document: ByteArray): MyDocument2 {
        return MyDocument2(document)
    }

}

class MyDocument2(document: ByteArray) : MupdfDocument {
    val common = CommonLib(document)

    override val pageCount: Int = common.fzPagesCount
    override val title: String = common.fzTitle

    override fun renderPage(page: Int): ImageBitmap {
        return renderPage(page, -1)
    }

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

