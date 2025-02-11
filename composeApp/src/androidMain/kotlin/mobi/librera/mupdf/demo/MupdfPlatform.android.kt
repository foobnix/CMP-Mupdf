package mobi.librera.mupdf.demo

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.artifex.mupdf.fitz.ColorSpace
import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.DrawDevice
import com.artifex.mupdf.fitz.Matrix

actual fun getMupdfPlatform(): MupdfPlatform = Platform()


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
        val fzPage = muDocument.loadPage(page)
        val scale = Matrix().scale(1.0f)
        val pixmap = fzPage.toPixmap(scale, ColorSpace.DeviceBGR, true, true)
        pixmap.clear(255)

        val drawDevice = DrawDevice(pixmap)
        fzPage.run(drawDevice, scale)

        drawDevice.close()
        drawDevice.destroy()

        val image = Bitmap.createBitmap(
            pixmap.pixels,
            pixmap.width,
            pixmap.height,
            Bitmap.Config.ARGB_8888
        ).asImageBitmap()

        pixmap.destroy()
        return image

    }

    override fun close() {
        muDocument.destroy()
    }

}

class Platform : MupdfPlatform {
    override val version = Context.getVersion().version!!

    override fun openDocument(document: ByteArray): MupdfDocument {
        return MyDocument(document)
    }


}


