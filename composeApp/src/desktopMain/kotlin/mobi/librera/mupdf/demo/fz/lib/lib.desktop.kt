package mobi.librera.mupdf.demo.fz.lib

import com.sun.jna.Native
import mobi.librera.mupdf.demo.fz.fz_library
import java.io.File

actual val fz: fz_library =Native.load<fz_library>(
    File(System.getProperty("java.library.path")+"/libmupdf_java64.jnilib").absolutePath,
    fz_library::class.java
)