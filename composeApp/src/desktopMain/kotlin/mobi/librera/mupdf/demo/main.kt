package mobi.librera.mupdf.demo

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import mobi.librera.mupdf.di.initKoin


@OptIn(ExperimentalStdlibApi::class)
fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Mupdf in Compose Multiplatform",
        state = rememberWindowState(
            width = 600.dp, height = 900.dp, position = WindowPosition(
                Alignment.Center
            )
        )
    ) {
        App()
    }
}