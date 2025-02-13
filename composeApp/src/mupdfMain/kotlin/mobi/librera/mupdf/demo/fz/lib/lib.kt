package mobi.librera.mupdf.demo.fz.lib

import com.sun.jna.Pointer
import mobi.librera.mupdf.demo.fz.fz_irect
import mobi.librera.mupdf.demo.fz.fz_library
import mobi.librera.mupdf.demo.fz.fz_matrix

expect val fz: fz_library

class CommonLib(document: ByteArray) {
    var fzContext: Pointer? = null
    var fzDocument: Pointer? = null
    var fzPagesCount: Int = 0
    var fzTitle: String = "title"
    var fzMupdfVersion: String = "1.25.4"

    init {
        fzContext = fz.fz_new_context_imp(null, null, 2560000, fzMupdfVersion)
        fz.fz_register_document_handlers(fzContext)
        val stream =
            fz.fz_open_memory(
                fzContext,
                document,
                document.size
            )
        fzDocument = fz.fz_open_document_with_stream(fzContext, "pdf", stream)
        fzPagesCount = fz.fz_count_pages(fzContext, fzDocument)
    }
    fun close(){
        fz.fz_drop_document(fzContext, fzDocument)
        fz.fz_drop_context(fzContext)
    }

    fun renderPage(page: Int, pageWidth: Int): Triple<IntArray, Int, Int> {

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

        val array = samples.getIntArray(0, height * width)



        fz.fz_drop_page(fzContext, fzPage)
        fz.fz_drop_pixmap(fzContext, fzPixmap)

        fz.fz_close_device(fzContext, fzDev)
        fz.fz_drop_device(fzContext, fzDev)

        return Triple(array, width, height)

    }
}