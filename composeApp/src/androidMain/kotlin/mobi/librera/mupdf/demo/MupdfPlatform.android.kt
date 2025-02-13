package mobi.librera.mupdf.demo

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.sun.jna.Native
import com.sun.jna.Pointer
import mobi.librera.mupdf.demo.fz.fz_irect
import mobi.librera.mupdf.demo.fz.fz_library
import mobi.librera.mupdf.demo.fz.fz_matrix

internal actual fun getMupdfPlatform(): MupdfPlatform = MupdfDesktop2()

class MupdfDesktop2 : MupdfPlatform {

    override val version: String = "Context.getVersion().version"

    override fun openDocument(document: ByteArray): MyDocument2 {
        return MyDocument2(document)
    }

}

class MyDocument2(document: ByteArray) : MupdfDocument {

    val fz: fz_library = Native.load<fz_library>(
        "mupdf_java",
        fz_library::class.java
    )



    private var fzPagesCount = -1
    private var fzDocument: Pointer? = null
    private var fzContext: Pointer? = null

    init {
        fzContext = fz.fz_new_context_imp(null, null, 2560000, "1.25.4")
        fz.fz_register_document_handlers(fzContext)
        val stream =
            fz.fz_open_memory(
                fzContext,
                document,
                document.size
            )
        fzDocument = fz.fz_open_document_with_stream(fzContext, "pdf", stream)

        fzPagesCount = fz.fz_count_pages(fzContext, fzDocument)
        Log.d("FZ","fzPagesCount $fzPagesCount")
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
        Log.d("FZ","renderPage 1")
        val fzPage = fz.fz_load_page(fzContext, fzDocument, page)
        Log.d("FZ","renderPage 2")
        val fzBounds = fz.fz_bound_page(fzContext, fzPage);
        Log.d("FZ","renderPage 3")
        val fzColor: Pointer? = fz.fz_device_bgr(fzContext)

        Log.d("FZ","renderPage 4")
        val bbox = fz_irect().apply {
            x0 = 0
            y0 = 0
            x1 = fzBounds!!.x1.toInt()
            y1 = fzBounds.y1.toInt()
        }

        Log.d("FZ","renderPage 5")

        val fzPixmap =
            fz.fz_new_pixmap_with_bbox(fzContext, fzColor, bbox, null, 1);

        Log.d("FZ","renderPage 6")

        fz.fz_clear_pixmap_with_value(fzContext, fzPixmap, 0xff)
        val fzMatrix = fz_matrix()

        Log.d("FZ","renderPage 7")
        val fzDev = fz.fz_new_draw_device(fzContext, fzMatrix, fzPixmap)
        fz.fz_run_page(fzContext, fzPage, fzDev, fzMatrix, null)

        Log.d("FZ","renderPage 8")
        val width = fz.fz_pixmap_width(fzContext, fzPixmap)
        val height = fz.fz_pixmap_height(fzContext, fzPixmap)
        val size = fz.fz_pixmap_size(fzContext, fzPixmap)
        val stride = fz.fz_pixmap_stride(fzContext, fzPixmap)
        Log.d("FZ","renderPage 9")
        val samples = fz.fz_pixmap_samples(fzContext, fzPixmap)
        Log.d("FZ","renderPage 10")
        Log.d("FZ","renderPage width $width height $height stride $stride")
        val array = samples.getIntArray(0,height*width)

//        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
//        image.setRGB(0, 0, width, height, array, 0, width)

        Log.d("FZ","renderPage 10+")
        val image = Bitmap.createBitmap(
            array,
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        Log.d("FZ","renderPage 11")

        fz.fz_drop_page(fzContext, fzPage)
        fz.fz_drop_pixmap(fzContext, fzPixmap)

        Log.d("FZ","renderPage 12")

        fz.fz_close_device(fzContext, fzDev)
        fz.fz_drop_device(fzContext, fzDev)

        Log.d("FZ","renderPage 13")
        return image.asImageBitmap()
    }

    override fun close() {
        fz.fz_drop_document(fzContext, fzDocument)
        fz.fz_drop_context(fzContext)
    }

}


