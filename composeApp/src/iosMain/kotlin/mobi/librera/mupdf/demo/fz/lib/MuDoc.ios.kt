package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Pixmap

internal actual fun openDocument(document: ByteArray): MuDoc {
    val common = CommonLib(document)
    return object : MuDoc {
        override val pageCount = common.fzPagesCount
        override val title = common.fzTitle

        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
            val (array, width, height) = common.renderPage(page, pageWidth)

            val info = ImageInfo.makeN32(width, height, ColorAlphaType.OPAQUE, ColorSpace.sRGB)
            val p = Pixmap.make(
                info,
                Data.makeFromBytes(array.toByteArray()),
                width * 4  // Row bytes (4 bytes per pixel)
            )

            return Image.makeFromPixmap(p).toComposeImageBitmap()
        }

        override fun close() {
            common.close()
        }
    }
}