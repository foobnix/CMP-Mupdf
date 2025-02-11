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
import androidx.compose.runtime.DisposableEffect
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {

        Column(
            Modifier.fillMaxSize().background(Color.LightGray)
                .padding(top = 20.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val mupdf by remember { mutableStateOf(getMupdfPlatform()) }

            Text(
                "Mupdf Version: ${mupdf.version}",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(alignment = Alignment.Start),
            )

            val listState = rememberLazyListState()

            var sliderPosition by remember { mutableStateOf(0f) }


            var pdfBytes by remember { mutableStateOf<ByteArray?>(null) }

            LaunchedEffect(Unit) {
                pdfBytes = withContext(Dispatchers.IO) {
                    Res.readBytes("files/kotlin-reference.pdf")
                }
            }
            if (pdfBytes != null) {
                val doc by remember { mutableStateOf(mupdf.openDocument(pdfBytes!!)) }
                val pageCount by remember { mutableStateOf(doc.pageCount) }
                val documentTitle by remember { mutableStateOf(doc.title) }

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
                    modifier = Modifier.padding(4.dp),
                    state = listState,
                    userScrollEnabled = true
                ) {
                    items(pageCount) { number ->
                        val image = doc.renderPage(number)
                        Image(
                            image,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                            contentDescription = "Mupdf Image"
                        )

                    }
                }

                DisposableEffect(Unit) {
                    onDispose {
                        doc.close()
                    }

                }
            }

        }
    }
}