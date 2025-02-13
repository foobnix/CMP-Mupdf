@file:OptIn(ExperimentalForeignApi::class)

package mobi.librera.mupdf.demo.fz.lib

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readValue
import kotlinx.cinterop.refTo
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import libmupdf.fz_bound_page
import libmupdf.fz_clear_pixmap_with_value
import libmupdf.fz_close_device
import libmupdf.fz_context
import libmupdf.fz_count_pages
import libmupdf.fz_device_bgr
import libmupdf.fz_document
import libmupdf.fz_drop_context
import libmupdf.fz_drop_device
import libmupdf.fz_drop_document
import libmupdf.fz_drop_page
import libmupdf.fz_drop_pixmap
import libmupdf.fz_identity
import libmupdf.fz_irect
import libmupdf.fz_load_page
import libmupdf.fz_matrix
import libmupdf.fz_new_context_imp
import libmupdf.fz_new_draw_device
import libmupdf.fz_new_pixmap_with_bbox
import libmupdf.fz_open_document_with_stream
import libmupdf.fz_open_memory
import libmupdf.fz_pixmap
import libmupdf.fz_register_document_handlers
import libmupdf.fz_run_page
import platform.Foundation.NSLog
import platform.posix.memcpy

class CommonLib(document: ByteArray) {
    private var fzContext: CPointer<fz_context>? = null;
    private var fzDocument: CPointer<fz_document>? = null;
    var fzPagesCount: Int = 0
    var fzTitle: String = "title"

    init {
        memScoped {
            fzContext = fz_new_context_imp(null, null, 1000u, FZ.FZ_VERSION)
            fz_register_document_handlers(fzContext)
            val stream =
                fz_open_memory(
                    fzContext,
                    document.toUByteArray().refTo(0),
                    document.size.convert()
                )
            fzDocument = fz_open_document_with_stream(fzContext, "pdf ${document.size}", stream)
            fzPagesCount = fz_count_pages(fzContext, fzDocument)
            NSLog(" Open Document Done")

        }
    }

    fun close() = memScoped {
            fz_drop_document(fzContext, fzDocument)
            fz_drop_context(fzContext)
    }

    fun renderPage(page: Int, pageWidth: Int): Triple<IntArray, Int, Int> = memScoped {
        NSLog(" InnerDocument 6 pageWidth $page $pageWidth $fzContext $fzDocument")
        val fzPage = fz_load_page(fzContext, fzDocument, page)
        NSLog(" InnerDocument 7 $fzContext $fzDocument")

        val fzBounds = fz_bound_page(fzContext, fzPage);

        var (fzWidth, fzHeight) = fzBounds.useContents {
            x1 - x0 to y1 - y0
        }

        val scale: Float = pageWidth / fzWidth
        NSLog(" InnerDocument 6 scale $scale")

        //  fzWidth =  pageWidth.toFloat()
        //  fzHeight = (fzHeight * scale)


        //val fzPixmap =
        //    fz_new_pixmap_from_page(fzContext, fzPage, fzMatrix, fz_device_bgr(fzContext), 1)
        fzWidth = fzWidth * scale
        fzHeight = fzHeight * scale

        NSLog(" InnerDocument $fzWidth $fzHeight ")

        val fzBbox = cValue<fz_irect>() {
            x0 = 0
            y0 = 0
            x1 = fzWidth.toInt()
            y1 = fzHeight.toInt()
        }

        NSLog(" InnerDocument 7 fzWidth $fzWidth  fzHeight $fzHeight")

        val fzPixmap =
            fz_new_pixmap_with_bbox(fzContext, fz_device_bgr(fzContext), fzBbox, null, 1);

        NSLog(" InnerDocument 81")


        NSLog(" InnerDocument 8")
        fz_clear_pixmap_with_value(fzContext, fzPixmap, 0xff)
        NSLog(" InnerDocument 9")


        val fzMatrix3 = cValue<fz_matrix>() {
            a = scale
            b = 0f
            c = 0f
            d = scale
            e = 0f
            f = 0f
        }
        val fzMatrix2 = fz_identity.readValue()


        val fzDev = fz_new_draw_device(fzContext, fzMatrix3, fzPixmap)
        NSLog(" InnerDocument 10")

        fz_run_page(fzContext, fzPage, fzDev, fzMatrix2, null)
        NSLog(" InnerDocument 11")


        //val byteArray = toByteArray(fzPixmap!!)
        val byteArray = asIntArray(fzPixmap!!)

        NSLog(" InnerDocument 13")


        if (fzContext != null) {
            if (fzPage != null) {
                fz_drop_page(fzContext, fzPage)
            }

            fz_drop_pixmap(fzContext, fzPixmap)

            if (fzDev != null) {
                fz_close_device(fzContext, fzDev)
                fz_drop_device(fzContext, fzDev)
            }
        }


        return Triple(byteArray, fzWidth.toInt(), fzHeight.toInt())
    }
}

@OptIn(ExperimentalForeignApi::class)
fun asIntArray(pixmap: CPointer<fz_pixmap>): IntArray {

    val samples = pixmap.pointed.samples
    val width = pixmap.pointed.w
    val height = pixmap.pointed.h
    val stride = pixmap.pointed.stride

    val size = height * stride

    return IntArray(size.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), samples, size.toULong())
        }
    }
}