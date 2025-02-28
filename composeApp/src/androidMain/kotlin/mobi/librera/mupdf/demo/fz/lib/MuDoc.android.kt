package mobi.librera.mupdf.demo.fz.lib

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import mobi.librera.mupdf.platform.MuFile

internal actual fun openDocument(name:String, document: ByteArray, width:Int, height:Int, fontSize:Int): MuDoc {
    val temp = MuFile.createTempFile(name, document)
    val common = CommonLib(temp, width, height, fontSize)
    return object : MuDoc() {
        override val pageCount = common.fzPagesCount
        override val title = common.fzTitle

        override fun renderPage(page: Int, pageWidth: Int): ImageBitmap {
            Logger.debug("Load page number  $page")
            val (array, width1, height1) = common.renderPage(page, pageWidth)

            val image = Bitmap.createBitmap(
                array,
                width1,
                height1,
                Bitmap.Config.ARGB_8888
            )

            return image.asImageBitmap()

        }

        override fun close() {
            common.close()
        }

        override suspend fun getOutline(): List<Outline> = common.getOutline()
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Logger {
    actual fun debug(message: String) {
        Log.d("AppLogger", "debug: [$message]")
    }

    actual fun info(message: String) {
        Log.i("AppLogger", message)
    }

    actual fun warn(message: String) {
        Log.w("AppLogger", message)
    }

    actual fun error(message: String) {
        Log.e("AppLogger", message)
    }
}