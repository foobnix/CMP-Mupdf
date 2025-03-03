@file:OptIn(DelicateCoroutinesApi::class)

package mobi.librera.mupdf.demo

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import kotlinx.coroutines.DelicateCoroutinesApi
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        ViewerScreenRoot()
    }
}




