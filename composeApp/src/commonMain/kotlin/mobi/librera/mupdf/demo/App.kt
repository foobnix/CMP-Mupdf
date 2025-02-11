package mobi.librera.mupdf.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            Modifier.fillMaxSize().padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val mupdf by remember { mutableStateOf(getMupdfPlatform()) }

            Text(
                "Mupdf Version: ${mupdf.version}",
                style = MaterialTheme.typography.h5
            )

            var pdfBytes by remember { mutableStateOf<ByteArray?>(null) }

            LaunchedEffect(Unit) {
                pdfBytes = withContext(Dispatchers.IO) {
                    Res.readBytes("files/demo.pdf")
                }
            }
            if (pdfBytes != null) {
                val image by remember {
                    mutableStateOf(mupdf.renderPage(pdfBytes!!, 0))
                }
                Image(
                    image,
                    contentDescription = "Mupdf Image"
                )
            }
        }
    }
}