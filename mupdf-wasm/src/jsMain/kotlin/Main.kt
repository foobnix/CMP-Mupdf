
external class MyLib {
    companion object {
        fun then(onReady: (MyLib) -> Unit)
    }

    //fun _add(a: Int, b: Int): Int
}


fun main() {
    // Load the WebAssembly module
    val myLibPromise: dynamic = js("require('./mupdf.js')")
    println("Ok")

}