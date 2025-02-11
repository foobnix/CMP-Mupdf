package mobi.librera.mupdf.demo

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readValue
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import libmupdf.FZ_VERSION
import libmupdf.fz_clear_pixmap_with_value
import libmupdf.fz_count_pages
import libmupdf.fz_device_bgr
import libmupdf.fz_identity
import libmupdf.fz_load_page
import libmupdf.fz_new_context_imp
import libmupdf.fz_new_draw_device
import libmupdf.fz_new_pixmap_from_page
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
import platform.posix.memcpy


internal actual fun getMupdfPlatform(): MupdfPlatform  = MupdfIOS()

@OptIn(ExperimentalForeignApi::class)
class MupdfIOS : MupdfPlatform {
    override val version = FZ_VERSION

    override fun renderPage(document: ByteArray, page: Int): ImageBitmap {
        memScoped {
            val context = fz_new_context_imp(null, null, 1000u, FZ_VERSION)
            fz_register_document_handlers(context)
            val stream =
                fz_open_memory(
                    context,
                    document.toUByteArray().refTo(0),
                    document.size.convert()
                )
            val fzDocument = fz_open_document_with_stream(context, "pdf", stream)
            val pageCount = fz_count_pages(context, fzDocument)

            val ma = fz_identity.readValue()
            val fzPage = fz_load_page(context, fzDocument, 0)
            val pixmap = fz_new_pixmap_from_page(context, fzPage, ma, fz_device_bgr(context), 1)
            fz_clear_pixmap_with_value(context, pixmap, 0xff)

            // Create a drawing device.
            val dev = fz_new_draw_device(context, ma, pixmap)

            fz_run_page(context, fzPage, dev, ma, null)


            val width = fz_pixmap_width(context, pixmap)
            val height = fz_pixmap_height(context, pixmap)


            val byteArray = pixmapToByteArray(pixmap!!)


            val info = ImageInfo.makeN32(width, height, ColorAlphaType.OPAQUE, ColorSpace.sRGB)
            val p = Pixmap.make(
                info,
                Data.makeFromBytes(byteArray),
                width * 4  // Row bytes (4 bytes per pixel)
            )


            return Image.makeFromPixmap(p).toComposeImageBitmap()
        }

    }

    fun pixmapToByteArray(pixmap: CPointer<fz_pixmap>): ByteArray {
        // Get the pixel data pointer
        val samples = pixmap.pointed.samples
        val width = pixmap.pointed.w
        val height = pixmap.pointed.h
        val stride = pixmap.pointed.stride

        // Calculate the total size of the pixel data
        val size = height * stride

        // Copy the pixel data into a ByteArray
        return ByteArray(size.toInt()).apply {
            usePinned { pinned ->
                memcpy(pinned.addressOf(0), samples, size.toULong())
            }
        }
    }
}