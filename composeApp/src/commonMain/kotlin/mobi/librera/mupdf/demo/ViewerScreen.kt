@file:OptIn(ExperimentalResourceApi::class)

package mobi.librera.mupdf.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cmp_mupdf.composeapp.generated.resources.Res
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



    Column(modifier = Modifier.fillMaxSize().padding(top = 20.dp).onGloballyPositioned {
        if (componentWidth == 0) {
            componentWidth = it.size.width
        }
    }) {

        if (componentWidth > 0) {
            LaunchedEffect(Unit) {
                viewModel.openBook(
                    //Res.readBytes("files/epub30-spec.epub"),
                    Res.readBytes("files/kotlin-reference.pdf"),
                    "epub30-spec.pdf",
                    componentWidth,
                    (componentWidth * 1.4f).toInt(),
                    bookState.fontSize
                )
            }
        }



        Column(Modifier.padding(8.dp)) {
            Text("MuPDF Version: ${viewModel.mudpfVersion}")

            val launcher = rememberFilePickerLauncher(mode = PickerMode.Single) { file ->
                Logger.debug("Open file: ${file!!.baseName} ${file.path}")

                coroutineScope.launch {
                    var bytes:ByteArray
                    withContext(Dispatchers.IO) {
                        bytes = file.readBytes()
                    }
                    withContext(Dispatchers.Main) {
                        viewModel.openBook(
                            bytes,
                            file.name,
                            componentWidth,
                            (componentWidth * 2).toInt(),
                            bookState.fontSize
                        )
                    }
                }
            }

            Button(onClick = { launcher.launch() }) {
                Text("Open file")
            }
            Text("File: ${bookState.path}")

        }



        var number by remember { mutableStateOf(bookState.fontSize) }

        LaunchedEffect(number){
            //viewModel.updateFontSize(number)
           // viewModel.reOpenBook()
        }

        var inputValue by remember { mutableStateOf(number.toString()) }
            Row {
                Button(
                    onClick = {
                    number--
                    inputValue = number.toString() // Update input field
                }) {
                    Text("-")
                }

                OutlinedTextField(
                    value = inputValue,
                    modifier = Modifier.width(80.dp),
                    onValueChange = {
                        inputValue = it
                        number = it.toIntOrNull() ?: 0 // Update number if valid
                    },
                    label = { Text("Font size") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Button(onClick = {
                    number++
                    inputValue = number.toString() // Update input field
                }) {
                    Text("+")
                }
            }



        Row(verticalAlignment = Alignment.CenterVertically,) {
            Text(text = "${bookState.currentPage+1}", modifier = Modifier.wrapContentWidth())
            Slider(
                value = bookState.currentPage.toFloat(),
                valueRange = 0f..bookState.pagesCount.toFloat(),
                onValueChange = { viewModel.updateCurrentPage(it.toInt()) },
                modifier = Modifier.weight(1f).padding(16.dp)
            )
            Text(text = "${bookState.pagesCount}", modifier = Modifier.wrapContentWidth())
        }


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