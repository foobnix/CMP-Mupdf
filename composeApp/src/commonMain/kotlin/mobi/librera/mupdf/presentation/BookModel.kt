package mobi.librera.mupdf.presentation

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cmp_mupdf.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import mobi.librera.mupdf.demo.fz.lib.MuDoc
import mobi.librera.mupdf.demo.fz.lib.Outline
import mobi.librera.mupdf.demo.fz.lib.openDocument
import org.jetbrains.compose.resources.ExperimentalResourceApi

data class BookInfo(
    val title: String = "",
    val path: String = "",
    val pagesCount: Int = 0,
    val currentPage: Int = 0,
    val fontSize: Int = 38,
)

@OptIn(ExperimentalResourceApi::class)
class BookModel() : ViewModel() {
    private val _state = MutableStateFlow(BookInfo())
    val model = _state.asStateFlow()

    var mudpfVersion = "..."
    var muDoc: MuDoc = MuDoc.EmptyDoc

    init {
        //openBook("files/epub30-spec.epub")

    }

    fun updateFontSize(fontSize: Int) {
        _state.update {
            it.copy(fontSize = fontSize)
        }
    }

    fun updateCurrentPage(page: Int) {
        _state.update {
            it.copy(currentPage = page)
        }
    }

    fun openBook(path: String, width: Int, height: Int, fontSize: Int) {
        if (muDoc != MuDoc.EmptyDoc) {
            muDoc.close()
        }

        _state.update {
            it.copy(path = path)
        }
        mudpfVersion = "123"


        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val documentBytes: ByteArray
                if (path.startsWith("files")) {
                    documentBytes = Res.readBytes(path)
                } else {
                    val filePath = Path(path)
                    documentBytes = SystemFileSystem.source(filePath).buffered().readByteArray()
                }

                muDoc = openDocument(
                    path.substringAfterLast("."),
                    documentBytes,
                    width,
                    height,
                    fontSize
                )
                _state.update {
                    it.copy(
                        pagesCount = muDoc.pageCount,
                        currentPage = 0
                    )
                }

            }
        }
    }

    suspend fun getOutline():List<Outline>{
        return muDoc.getOutline()
    }



    fun openBook(bytes: ByteArray, path: String, width: Int, height: Int, fontSize: Int) {
        if (muDoc != MuDoc.EmptyDoc) {
            muDoc.close()
        }


        _state.update {
            it.copy(path = path)
        }
        mudpfVersion = "123"

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                muDoc = openDocument(path.substringAfterLast("."), bytes, width, height, fontSize)
                _state.update {
                    it.copy(
                        pagesCount = muDoc.pageCount,
                        currentPage = 0
                    )
                }

            }
        }
    }

    suspend fun renderPage(page: Int, width: Int): ImageBitmap {
        return muDoc.renderPageSafe(page, width)
    }

    fun updateTitle(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }
}