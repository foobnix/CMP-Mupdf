package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.artifex.mupdf.fitz.ColorSpace
import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.DrawDevice
import com.artifex.mupdf.fitz.Matrix
import com.artifex.mupdf.fitz.Pixmap
import com.sun.jna.Native
import com.sun.jna.Pointer
import mobi.librera.mupdf.demo.fz.fz_irect
import mobi.librera.mupdf.demo.fz.fz_library
import mobi.librera.mupdf.demo.fz.fz_matrix
import java.awt.image.BufferedImage

internal actual fun getMupdfPlatform(): MupdfPlatform = MupdfDesktop2()

class MupdfDesktop2 : MupdfPlatform {

    override val version: String = "Context.getVersion().version"

    override fun openDocument(document: ByteArray): MyDocument2 {
        return MyDocument2(document)
    }

}

class MyDocument2(document: ByteArray) : MupdfDocument {
    val fz: fz_library = Native.load<fz_library>(
        "/Users/ivanivanenko/git/CMP-Mupdf/mupdf-jvm/libs/libmupdf_java64.jnilib",
        fz_library::class.java
    )
    private var fzPagesCount = -1
    private var fzDocument: Pointer? = null
    private var fzContext: Pointer? = null

    init {
        fzContext = fz.fz_new_context_imp(null, null, 2560000, "1.25.4")
        fz.fz_register_document_handlers(fzContext)
//        fzDocument = fz.fz_open_document(
//            fzContext,
//            "/Users/ivanivanenko/git/CMP-Mupdf/composeApp/src/commonMain/composeResources/files/kotlin-reference.pdf"
//        )

        val stream =
            fz.fz_open_memory(
                fzContext,
                document,
                document.size
            )
        fzDocument = fz.fz_open_document_with_stream(fzContext, "pdf ${document.size}", stream)

        fzPagesCount = fz.fz_count_pages(fzContext, fzDocument)
    }

    override
    val pageCount: Int = fzPagesCount


    override
    val title: String
        get() = "asfd"

    override fun renderPage(page: Int): ImageBitmap {
        return renderPage(page, -1)
    }

    override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
        val fzPage = fz.fz_load_page(fzContext, fzDocument, page)
        val fzBounds = fz.fz_bound_page(fzContext, fzPage);
        val fzColor: Pointer? = fz.fz_device_bgr(fzContext)

        val bbox = fz_irect().apply {
            x0 = 0
            y0 = 0
            x1 = fzBounds!!.x1.toInt()
            y1 = fzBounds.y1.toInt()
        }

        val fzPixmap =
            fz.fz_new_pixmap_with_bbox(fzContext, fzColor, bbox, null, 1);


        fz.fz_clear_pixmap_with_value(fzContext, fzPixmap, 0xff)
        val fzMatrix = fz_matrix()
        val fzDev = fz.fz_new_draw_device(fzContext, fzMatrix, fzPixmap)
        fz.fz_run_page(fzContext, fzPage, fzDev, fzMatrix, null)
        val width = fz.fz_pixmap_width(fzContext, fzPixmap)
        val height = fz.fz_pixmap_height(fzContext, fzPixmap)
        val size = fz.fz_pixmap_size(fzContext, fzPixmap)
        val stride = fz.fz_pixmap_stride(fzContext, fzPixmap)

        val samples = fz.fz_pixmap_samples(fzContext, fzPixmap)
        val array = samples.getIntArray(0,height*stride)

        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
         image.setRGB(0, 0, width, height, array, 0, width)

        return image.toComposeImageBitmap()
    }

    override fun close() {

    }

}


class DocumentOriginal(document: ByteArray) : MupdfDocument {

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


class MupdfDesktopOriginal : MupdfPlatform {

    override val version: String = Context.getVersion().version

    override fun openDocument(document: ByteArray): DocumentOriginal {
        return DocumentOriginal(document)
    }
}


