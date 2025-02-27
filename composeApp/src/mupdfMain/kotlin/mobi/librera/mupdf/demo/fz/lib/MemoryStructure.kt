package mobi.librera.mupdf.demo.fz.lib

import com.sun.jna.Pointer
import com.sun.jna.Structure

open class MemoryStructure : Structure() {

    public override fun useMemory(m: Pointer?) {
        super.useMemory(m)
    }

}
