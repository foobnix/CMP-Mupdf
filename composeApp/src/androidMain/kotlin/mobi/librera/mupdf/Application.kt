package mobi.librera.mupdf

import android.app.Application
import android.content.Context


open class Application : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}