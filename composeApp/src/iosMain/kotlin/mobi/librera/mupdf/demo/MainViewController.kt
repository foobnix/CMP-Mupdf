package mobi.librera.mupdf.demo

import androidx.compose.ui.window.ComposeUIViewController
import mobi.librera.mupdf.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {

    App()
}