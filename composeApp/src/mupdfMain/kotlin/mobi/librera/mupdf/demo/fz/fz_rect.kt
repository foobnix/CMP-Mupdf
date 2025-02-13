package mobi.librera.mupdf.demo.fz

import com.sun.jna.Structure

@Structure.FieldOrder("x0", "y0", "x1", "y1")
class fz_rect : Structure(), Structure.ByValue {
    @JvmField var x0: Float = 0f
    @JvmField var y0: Float = 0f
    @JvmField var x1: Float = 0f
    @JvmField var y1: Float = 0f

     fun make(x0: Float, y0: Float, x1: Float, y1: Float):fz_rect {
        this.x0 = x0
        this.y0 = y0
        this.x1 = x1
        this.y1 = y1
        return this
    }


    override fun toString(): String {
        return "fz_rect [x0: $x0, y0: $y0, x1: $x1, y1: $y1]"
    }
}