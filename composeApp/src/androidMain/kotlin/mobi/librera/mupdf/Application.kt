package mobi.librera.mupdf

import android.app.Application
import android.content.Context
import io.github.vinceglb.filekit.core.FileKit
import mobi.librera.mupdf.di.initKoin
import org.koin.android.ext.koin.androidContext


open class Application : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@Application)
        }
        appContext = applicationContext
    }
}