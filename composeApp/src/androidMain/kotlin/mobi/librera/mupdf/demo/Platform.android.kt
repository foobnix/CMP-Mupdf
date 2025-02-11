package mobi.librera.mupdf.demo

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.artifex.mupdf.fitz.ColorSpace
import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.DrawDevice
import com.artifex.mupdf.fitz.Matrix
import org.jetbrains.compose.resources.ExperimentalResourceApi

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getMupdfVersion(): String {
    return Context.getVersion().version
}

@OptIn(ExperimentalResourceApi::class)
actual fun renderPage(
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