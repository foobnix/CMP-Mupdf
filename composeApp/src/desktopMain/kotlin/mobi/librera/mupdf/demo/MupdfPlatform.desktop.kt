package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.artifex.mupdf.fitz.ColorSpace
import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.DrawDevice
import com.artifex.mupdf.fitz.Matrix
import com.artifex.mupdf.fitz.Pixmap
import java.awt.image.BufferedImage

internal actual fun getMupdfPlatform(): MupdfPlatform = MupdfDesktop()

class MyDocument(document: ByteArray) : MupdfDocument {

    private val muDocument: Document = Document.openDocument(document, "pdf")

    override val pageCount: Int
        get() = muDocument.countPages()

    override val title: String
        get() = muDocument.getMetaData(Document.META_INFO_TITLE)


    override fun renderPage(page: Int): ImageBitmap {
        return renderPage(page, -1)
    }

    override fun renderPage(page: Int, width: Int): ImageBitmap {
        val loadPage = muDocument.loadPage(page)
        val scale = Matrix().scale(1.0f)
        val pixmap: Pixmap = loadPage.toPixmap(scale, ColorSpace.DeviceBGR, true, true)
        pixmap.clear(255)
        val drawDevice = DrawDevice(pixmap)
        loadPage.run(drawDevice, scale)

        val pixels: IntArray = pixmap.pixels
        drawDevice.close()
        drawDevice.destroy()

        val image = BufferedImage(pixmap.width, pixmap.height, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, pixmap.width, pixmap.height, pixels, 0, pixmap.width)

        pixmap.destroy()

        return image.toComposeImageBitmap()
    }

    override fun close() {
        muDocument.destroy()
    }

}


class MupdfDesktop : MupdfPlatform {

    override val version: String = Context.getVersion().version

    override fun openDocument(document: ByteArray): MyDocument {
        return MyDocument(document)
    }


}