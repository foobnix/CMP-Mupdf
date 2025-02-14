package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Pixmap
import platform.Foundation.NSLog

internal actual fun openDocument(document: ByteArray): MuDoc {
    val common = CommonLib(document)
    return object : MuDoc() {
        override val pageCount = common.fzPagesCount
        override val title = common.fzTitle

        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
            val (array, width, height) = common.renderPage(page, pageWidth)


            val info = ImageInfo.makeN32(width, height, ColorAlphaType.OPAQUE, ColorSpace.sRGB)
            val buffer = Data.makeFromBytes(array)

            val p = Pixmap.make(
                info,
                buffer,
                width * 4  // Row bytes (4 bytes per pixel)
            )

            val makeFromPixmap = Image.makeFromPixmap(p)
            val result = makeFromPixmap.toComposeImageBitmap()

            makeFromPixmap.close()
            buffer.close()
            p.close()

            return result
        }

        override fun close() {
            common.close()
        }
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Logger {
    actual fun debug(message: String) {
        NSLog("debug: [$message]")
    }

    actual fun info(message: String) {
        NSLog("INFO: $message")
    }

    actual fun warn(message: String) {
        NSLog("WARN: $message")
    }

    actual fun error(message: String) {
        NSLog("ERROR: $message")
    }
}