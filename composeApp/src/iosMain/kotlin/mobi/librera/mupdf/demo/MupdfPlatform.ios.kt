package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.getBytes
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
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Pixmap
import platform.Foundation.NSLog
import platform.posix.memcpy


internal actual fun getMupdfPlatform(): MupdfPlatform = MupdfIOS()

@OptIn(ExperimentalForeignApi::class)
class InnerDocument(document: ByteArray) : MupdfDocument {
    private var fzContext: CPointer<fz_context>? = null;
    private var fzDocument: CPointer<fz_document>? = null;
    private var fzPagesCount = -1;
    private var fzMatrix = fz_identity.readValue()

    init {
        memScoped {
            NSLog(" InnerDocument 1")
            fzContext = fz_new_context_imp(null, null, 1000u, FZ_VERSION)
            NSLog(" InnerDocument 2")
            fz_register_document_handlers(fzContext)
            NSLog(" InnerDocument 3")
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

    override val pageCount = fzPagesCount

    override val title: String = ""

    override fun renderPage(page: Int): ImageBitmap {
        NSLog(" InnerDocument renderPage: $page")
        return renderPage(page, -1)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
        memScoped {
            NSLog(" InnerDocument 6")
            val fzPage = fz_load_page(fzContext, fzDocument, page)
            NSLog(" InnerDocument 7 $fzContext $fzDocument")

            val fzBounds = fz_bound_page(fzContext, fzPage);

            NSLog("fzBounds ${fzBounds.size} [${fzBounds.getBytes().toHexString(HexFormat.Default)}")


            val (fzWidth, fzHeight) = fzBounds.useContents {
                x1 - x0 to y1 - y0
            }

            //val fzPixmap =
            //    fz_new_pixmap_from_page(fzContext, fzPage, fzMatrix, fz_device_bgr(fzContext), 1)

            val fzBbox = cValue<fz_irect>() {
                x0 = 0
                y0 = 0
                x1 = fzWidth.toInt() * 2
                y1 = fzHeight.toInt() * 2
            }
            val fzPixmap =
                fz_new_pixmap_with_bbox(fzContext, fz_device_bgr(fzContext), fzBbox, null, 1);

            NSLog(" InnerDocument 81")


            NSLog(" InnerDocument 8")
            fz_clear_pixmap_with_value(fzContext, fzPixmap, 0xff)
            NSLog(" InnerDocument 9")


            val fzDev = fz_new_draw_device(fzContext, fzMatrix, fzPixmap)
            NSLog(" InnerDocument 10")

            fz_run_page(fzContext, fzPage, fzDev, fzMatrix, null)
            NSLog(" InnerDocument 11")

            val width = fz_pixmap_width(fzContext, fzPixmap)
            val height = fz_pixmap_height(fzContext, fzPixmap)
            NSLog(" InnerDocument 12")


            val byteArray = pixmapToByteArray(fzPixmap!!)

            NSLog(" InnerDocument 13")
            val info = ImageInfo.makeN32(width, height, ColorAlphaType.OPAQUE, ColorSpace.sRGB)
            val p = Pixmap.make(
                info,
                Data.makeFromBytes(byteArray),
                width * 4  // Row bytes (4 bytes per pixel)
            )
            NSLog(" InnerDocument 14")




            fz_drop_page(fzContext, fzPage)
            fz_drop_pixmap(fzContext, fzPixmap)

            fz_close_device(fzContext, fzDev)
            fz_drop_device(fzContext, fzDev)


            return Image.makeFromPixmap(p).toComposeImageBitmap()

        }
    }

    override fun close() {
        memScoped {
            fz_drop_document(fzContext, fzDocument)
            fz_drop_context(fzContext)
        }
    }

}

@OptIn(ExperimentalForeignApi::class)
class MupdfIOS : MupdfPlatform {

    override val version = FZ_VERSION

    override fun openDocument(document: ByteArray): MupdfDocument {
        return InnerDocument(document)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun pixmapToByteArray(pixmap: CPointer<fz_pixmap>): ByteArray {

    val samples = pixmap.pointed.samples
    val width = pixmap.pointed.w
    val height = pixmap.pointed.h
    val stride = pixmap.pointed.stride

    val size = height * stride

    return ByteArray(size.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), samples, size.toULong())
        }
    }
}