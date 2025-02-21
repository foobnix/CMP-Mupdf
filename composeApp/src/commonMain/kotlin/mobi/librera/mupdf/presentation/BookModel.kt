package mobi.librera.mupdf.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BookInfo(
    val title: String = "",
    val path: String = ""
)

class BookModel : ViewModel() {
    private val _state = MutableStateFlow(BookInfo())
    val model = _state.asStateFlow()

    fun updateTitle(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }
}