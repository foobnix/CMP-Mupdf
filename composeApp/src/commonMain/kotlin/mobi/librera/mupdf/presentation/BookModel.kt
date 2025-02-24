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
import mobi.librera.mupdf.demo.fz.lib.MuDoc
import mobi.librera.mupdf.demo.fz.lib.openDocument
import org.jetbrains.compose.resources.ExperimentalResourceApi

data class BookInfo(
    val title: String = "",
    val path: String = "",
    val pagesCount: Int = 0
)

@OptIn(ExperimentalResourceApi::class)
class BookModel() : ViewModel() {
    private val _state = MutableStateFlow(BookInfo())
    val model = _state.asStateFlow()

    var mudpfVersion = "..."
    var muDoc: MuDoc = MuDoc.EmptyDoc

    init {
        _state.update {
            it.copy(path = "files/kotlin-reference.pdf")
        }
        mudpfVersion = "123"


        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val documentBytes = Res.readBytes("files/kotlin-reference.pdf")
                muDoc = openDocument("kotlin-reference.pdf", documentBytes)
                _state.update {
                    it.copy(pagesCount = muDoc.pageCount)
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