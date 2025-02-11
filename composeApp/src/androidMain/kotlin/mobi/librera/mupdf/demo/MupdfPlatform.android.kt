package mobi.librera.mupdf.demo

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.artifex.mupdf.fitz.ColorSpace
import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.DrawDevice
import com.artifex.mupdf.fitz.Matrix

actual fun getMupdfPlatform(): MupdfPlatform = AndroidMupdfPlatform()

class AndroidMupdfPlatform : MupdfPlatform {
    override val version = Context.getVersion().version!!

    override
    fun renderPage(
        document: ByteArray,
        page: Int
    ): ImageBitmap {
        val doc = Document.openDocument(document, "pdf")
        val fzPage = doc.loadPage(page)

        val scale = Matrix().scale(1.0f)
        val pixmap = fzPage.toPixmap(scale, ColorSpace.DeviceBGR, true, true)
        pixmap.clear(255)

        val drawDevice = DrawDevice(pixmap)
        fzPage.run(drawDevice, scale)

        drawDevice.close()
        drawDevice.destroy()

        return Bitmap.createBitmap(
            pixmap.pixels,
            pixmap.width,
            pixmap.height,
            Bitmap.Config.ARGB_8888
        ).asImageBitmap()
    }
}


