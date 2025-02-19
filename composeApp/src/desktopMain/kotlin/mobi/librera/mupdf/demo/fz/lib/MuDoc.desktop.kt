package mobi.librera.mupdf.demo.fz.lib

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import mobi.librera.mupdf.platform.MuFile
import java.awt.image.BufferedImage
import java.util.logging.Level
import java.util.logging.Logger.getLogger

internal actual fun openDocument(name:String, document: ByteArray): MuDoc {
    val tempFile = MuFile.createTempFile(name, document)
    val common = CommonLib(tempFile)
    return object : MuDoc() {
        override val pageCount = common.fzPagesCount
        override val title = common.fzTitle

        override  fun renderPage(page: Int, pageWidth: Int): ImageBitmap{
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

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Logger {
    private val logger = getLogger("AppLogger")

    actual fun debug(message: String) {
        println("debug:[$message]")
        logger.log(Level.FINE, message)
    }

    actual fun info(message: String) {
        logger.log(Level.INFO, message)
    }

    actual fun warn(message: String) {
        logger.log(Level.WARNING, message)
    }

    actual fun error(message: String) {
        logger.log(Level.SEVERE, message)
    }
}