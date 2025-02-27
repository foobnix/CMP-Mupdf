package mobi.librera.mupdf.demo.fz

import com.sun.jna.Structure

/**
 * {
 * 	int chapter;
 * 	int page;
 * } fz_location;
 */

@Structure.FieldOrder("chapter", "page")
class fz_location : Structure(), Structure.ByValue {
    @JvmField var chapter: Int = 0
    @JvmField var page: Int = 0


}