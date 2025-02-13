package mobi.librera.mupdf.demo.fz

import com.sun.jna.Structure

@Structure.FieldOrder("a", "b", "c", "d", "e", "f")
class fz_matrix: Structure(), Structure.ByValue {
    @JvmField var a: Float = 1f
    @JvmField var b: Float = 0f
    @JvmField var c: Float = 0f

    @JvmField var d: Float = 1f
    @JvmField var e: Float = 0f
    @JvmField var f: Float = 0f

    fun make (a: Float, b: Float, c: Float, d: Float, e: Float, f: Float):fz_matrix {
        this.a = a
        this.b = b
        this.c = c
        this.d = d
        this.e = e
        this.f = f
        return this
    }


    override fun toString(): String {
        return "fz_matrix [a: $a, b: $b, b: $b, d: $d, e: $e, f: $f]"
    }
}