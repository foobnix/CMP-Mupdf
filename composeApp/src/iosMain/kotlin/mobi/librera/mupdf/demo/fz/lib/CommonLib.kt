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
import libmupdf.FZ_VERSION
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
import libmupdf.fz_new_context_imp
import libmupdf.fz_new_draw_device
import libmupdf.fz_new_pixmap_with_bbox
import libmupdf.fz_open_document_with_stream
import libmupdf.fz_open_memory
import libmupdf.fz_pixmap
import libmupdf.fz_pixmap_height
import libmupdf.fz_pixmap_width
import libmupdf.fz_register_document_handlers
import libmupdf.fz_run_page
import platform.Foundation.NSLog
import platform.posix.memcpy

class CommonLib(document: ByteArray) {
    private var fzContext: CPointer<fz_context>? = null;
    private var fzDocument: CPointer<fz_document>? = null;
    var fzPagesCount: Int = 0
    var fzTitle: String = "title"
    var fzMupdfVersion: String = "1.25.4"

    init {
        memScoped {
            fzContext = fz_new_context_imp(null, null, 1000u, FZ_VERSION)
            fz_register_document_handlers(fzContext)
            val stream =
                fz_open_memory(
                    fzContext,
                    document.toUByteArray().refTo(0),
                    document.size.convert()
                )
            fzDocument = fz_open_document_with_stream(fzContext, "pdf ${document.size}", stream)
            fzPagesCount = fz_count_pages(fzContext, fzDocument)
            NSLog(" InnerDocument 4 $fzPagesCount")

        }
    }

    fun close() {
        memScoped {
            fz_drop_document(fzContext, fzDocument)
            fz_drop_context(fzContext)
        }
    }

    fun renderPage(page: Int, pageWidth: Int): Triple<IntArray, Int, Int> {

        memScoped {
            NSLog(" InnerDocument 6")
            val fzPage = fz_load_page(fzContext, fzDocument, page)
            NSLog(" InnerDocument 7 $fzContext $fzDocument")

            val fzBounds = fz_bound_page(fzContext, fzPage);


            val (fzWidth, fzHeight) = fzBounds.useContents {
                x1 - x0 to y1 - y0
            }

            //val fzPixmap =
            //    fz_new_pixmap_from_page(fzContext, fzPage, fzMatrix, fz_device_bgr(fzContext), 1)

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

            val fzMatrix = fz_identity.readValue()


            val fzDev = fz_new_draw_device(fzContext, fzMatrix, fzPixmap)
            NSLog(" InnerDocument 10")

            fz_run_page(fzContext, fzPage, fzDev, fzMatrix, null)
            NSLog(" InnerDocument 11")

            val width = fz_pixmap_width(fzContext, fzPixmap)
            val height = fz_pixmap_height(fzContext, fzPixmap)
            NSLog(" InnerDocument 12")


            //val byteArray = toByteArray(fzPixmap!!)
            val byteArray = asIntArray(fzPixmap!!)

            NSLog(" InnerDocument 13")





            fz_drop_page(fzContext, fzPage)
            fz_drop_pixmap(fzContext, fzPixmap)

            fz_close_device(fzContext, fzDev)
            fz_drop_device(fzContext, fzDev)


            return Triple(byteArray, width, height)

        }

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