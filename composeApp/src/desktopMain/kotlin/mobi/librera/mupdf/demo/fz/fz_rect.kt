package mobi.librera.mupdf.demo.fz

import com.sun.jna.Structure

@Structure.FieldOrder("x0", "y0", "x1", "y1")
class fz_rect : Structure(), Structure.ByValue {
    @JvmField var x0: Float = 0f
    @JvmField var y0: Float = 0f
    @JvmField var x1: Float = 0f
    @JvmField var y1: Float = 0f

    override fun toString(): String {
        return "fz_rect [x0: $x0, y0: $y0, x1: $x1, y1: $y1]"
    }
}