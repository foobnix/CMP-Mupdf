@file:OptIn(DelicateCoroutinesApi::class)

package mobi.librera.mupdf.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cmp_mupdf.composeapp.generated.resources.Res
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import mobi.librera.mupdf.demo.fz.lib.FZ
import mobi.librera.mupdf.demo.fz.lib.Logger
import mobi.librera.mupdf.demo.fz.lib.MuDoc
import mobi.librera.mupdf.demo.fz.lib.openDocument
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalResourceApi::class, ExperimentalCoroutinesApi::class)
@Composable
@Preview
fun App() {
    Logger.debug("Start Application")
    MaterialTheme {

        Column(
            Modifier.fillMaxSize().background(Color.LightGray)
                .padding(top = 20.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                "Mupdf Version: ${FZ.FZ_VERSION}",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(alignment = Alignment.Start),
            )

            val listState = rememberLazyListState()

            var sliderPosition by remember { mutableStateOf(0f) }

            //var pdfBytes by remember { mutableStateOf<ByteArray?>(null) }

            //var doc: MupdfDocument by remember { mutableStateOf(EmptyDocument()) }

            var muDoc: MuDoc by remember { mutableStateOf(MuDoc.EmptyDoc) }


            var pageCount by remember { mutableStateOf(0) }
            var documentTitle by remember { mutableStateOf("") }
            var pdfBytes by remember { mutableStateOf(ByteArray(0)) }


                LaunchedEffect(Unit) {
                    pdfBytes = withContext(Dispatchers.IO) {
                       Res.readBytes("files/kotlin-reference.pdf")
                    }
                    muDoc = openDocument(pdfBytes)
                    pageCount = muDoc.pageCount
                    documentTitle = muDoc.title
                    sliderPosition = 0f
                }


            if (pdfBytes.isNotEmpty()) {

                val textPages = "Pages: ${sliderPosition.toInt() + 1} / ${pageCount + 1}"

                Text(
                    modifier = Modifier.align(alignment = Alignment.Start),
                    text = textPages,
                )
                if (documentTitle.isNotEmpty()) {
                    Text(
                        modifier = Modifier.align(alignment = Alignment.Start),
                        text = documentTitle,
                    )
                }


                Slider(
                    value = sliderPosition,
                    valueRange = 0f..pageCount.toFloat(),
                    onValueChange = { sliderPosition = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                LaunchedEffect(sliderPosition) {
                    listState.scrollToItem(sliderPosition.toInt())
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).padding(4.dp),
                    state = listState,
                    userScrollEnabled = true,


                    ) {
                    items(pageCount, key = { index -> index }) { number ->

                            val image = muDoc.renderPageSafe(number, 1000)

                            Image(
                                image,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                                contentDescription = "Mupdf Image"
                            )


                    }


                }

            }
        }


    }

}


