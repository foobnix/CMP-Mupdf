package mobi.librera.mupdf.demo.fz.lib

import com.sun.jna.Native
import mobi.librera.mupdf.demo.fz.fz_library

actual val fz: fz_library = Native.load<fz_library>(
    "mupdf_java",
    fz_library::class.java
)