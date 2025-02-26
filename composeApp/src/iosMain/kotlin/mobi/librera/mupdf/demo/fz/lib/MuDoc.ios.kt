package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import mobi.librera.mupdf.platform.MuFile
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Pixmap
import platform.Foundation.NSLog

internal actual fun openDocument(name:String, document: ByteArray,width:Int, height:Int, fontSize:Int): MuDoc {
    val tempFile = MuFile.createTempFile(name, document)
    val common = CommonLib(tempFile,width, height, fontSize)
    return object : MuDoc() {
        override val pageCount = common.fzPagesCount
        override val title = common.fzTitle

        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
            val (array, width1, height1) = common.renderPage(page, pageWidth)


            val info = ImageInfo.makeN32(width1, height1, ColorAlphaType.OPAQUE, ColorSpace.sRGB)
            val buffer = Data.makeFromBytes(array)

            val p = Pixmap.make(
                info,
                buffer,
                width1 * 4  // Row bytes (4 bytes per pixel)
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