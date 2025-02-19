package mobi.librera.mupdf.demo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mobi.librera.mupdf.demo.fz.lib.MuDoc

data class MupdfData(
    var muDoc: MuDoc,
    val name: String,
    val pageCount: Int
)

class MuPdfViewModel : ViewModel() {
    val _data = MutableStateFlow(MupdfData(MuDoc.EmptyDoc, "", 0))
    var data = _data.asStateFlow()

}