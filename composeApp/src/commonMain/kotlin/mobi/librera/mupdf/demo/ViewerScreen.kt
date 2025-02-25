@file:OptIn(ExperimentalResourceApi::class)

package mobi.librera.mupdf.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.baseName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobi.librera.mupdf.demo.fz.lib.Logger
import mobi.librera.mupdf.presentation.BookModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ViewerScreen(viewModel: BookModel = koinViewModel()) {
    val bookState by viewModel.model.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var componentWidth by remember { mutableStateOf(0) }
    var showFilePicker by remember { mutableStateOf(false) }



    Column(modifier = Modifier.fillMaxSize().padding(top = 20.dp).onGloballyPositioned {
        if (componentWidth == 0) {
            componentWidth = it.size.width
        }
    }) {



        Column(Modifier.padding(8.dp)) {
            Text("MuPDF Version: ${viewModel.mudpfVersion}")

            var bytes by remember { mutableStateOf(ByteArray(0)) }
            val launcher = rememberFilePickerLauncher(mode = PickerMode.Single) { file ->
                Logger.debug("Open file: ${file!!.baseName} ${file.path}")
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        bytes = file.readBytes()
                    }
                    withContext(Dispatchers.Main) {
                        viewModel.openBook(bytes, file.name,componentWidth,(componentWidth*2).toInt(),48) //TODO open as file not bytes
                    }
                }
            }

            Button(onClick = { launcher.launch() }) {
                Text("Open file")
            }
            Text("File: ${bookState.path}")

        }

        Slider(
            value = bookState.currentPage.toFloat(),
            valueRange = 0f..bookState.pagesCount.toFloat(),
            onValueChange = { viewModel.updateCurrentPage(it.toInt()) },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )


        LaunchedEffect(bookState.currentPage) {
            listState.scrollToItem(bookState.currentPage)
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(4.dp),
            state = listState,
            userScrollEnabled = true,
        ) {
            items(bookState.pagesCount, key = { index -> index }) { number ->
                var image by remember(number) { mutableStateOf(ImageBitmap(1, 1)) }

                remember(number) {
                    coroutineScope.launch(Dispatchers.IO) {
                        image = viewModel.renderPage(number, componentWidth)
                    }
                }

                Image(
                    image,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                    contentDescription = "Image $number"
                )

            }
        }


    }

}