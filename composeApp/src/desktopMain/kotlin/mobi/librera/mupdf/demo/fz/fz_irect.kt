package mobi.librera.mupdf.demo.fz

import com.sun.jna.Structure

@Structure.FieldOrder("x0", "y0", "x1", "y1")
class fz_irect : Structure(), Structure.ByValue {
    @JvmField var x0: Int = 0
    @JvmField var y0: Int = 0
    @JvmField var x1: Int = 0
    @JvmField var y1: Int = 0

    override fun toString(): String {
        return "fz_rect [x0: $x0, y0: $y0, x1: $x1, y1: $y1]"
    }
}