%module mupdf

%{
#include "mupdf/fitz.h"
%}

%include "typemaps.i"
%include "stdint.i"
%include "arrays_java.i"
%include "carrays.i"
%include "various.i"
%include "java.swg"
%include "typemaps.i"

//%javaconst(1);

/* Basic mappings */
%apply int {unsigned long long};
%apply long[] {unsigned long long *};
%apply int {size_t};
%apply int {uint32_t};
%apply long {uint64_t};

/* unsigned char */
%typemap(jni) unsigned char *       "jbyteArray"
%typemap(jtype) unsigned char *     "byte[]"
%typemap(jstype) unsigned char *    "byte[]"
%typemap(in) unsigned char *{
$1 = (unsigned char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) unsigned char *{
JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) unsigned char *"$javainput"
/* Prevent default freearg typemap from being used */
%typemap(freearg) unsigned char *""

/* uint8_t */
%typemap(jni) uint8_t *"jbyteArray"
%typemap(jtype) uint8_t *"byte[]"
%typemap(jstype) uint8_t *"byte[]"
%typemap(in) uint8_t *{
$1 = (uint8_t *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) uint8_t *{
JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) uint8_t *"$javainput"
%typemap(freearg) uint8_t *""

/* Strings */
%typemap(jni) char *"jbyteArray"
%typemap(jtype) char *"byte[]"
%typemap(jstype) char *"byte[]"
%typemap(in) char *{
$1 = (char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) char *{
JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) char *"$javainput"
%typemap(freearg) char *""


/* char types */
%typemap(jni) char *BYTE "jbyteArray"
%typemap(jtype) char *BYTE "byte[]"
%typemap(jstype) char *BYTE "byte[]"
%typemap(in) char *BYTE {
        $1 = (char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) char *BYTE {
        JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) char *BYTE "$javainput"
/* Prevent default freearg typemap from being used */
%typemap(freearg) char *BYTE ""

/* Fixed size strings/char arrays */
%typemap(jni) char [ANY]"jbyteArray"
%typemap(jtype) char [ANY]"byte[]"
%typemap(jstype) char [ANY]"byte[]"
%typemap(in) char [ANY]{
$1 = (char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) char [ANY]{
JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) char [ANY]"$javainput"
%typemap(freearg) char [ANY]""


%typemap(out) fz_storable {
// Convert struct to byte array
int structSize = sizeof(fz_storable);
jbyteArray jArray = (*jenv)->NewByteArray(jenv, structSize);
if (jArray) {
(*jenv)->SetByteArrayRegion(jenv, jArray, 0, structSize, (jbyte *)&$1);
}
$result = jArray;
}

%typemap(in) fz_storable * {
if (!SWIG_JavaByteArrayToPointer(jenv, $input, (void **)&$1, sizeof(fz_storable))) {
SWIG_JavaThrowException(jenv, SWIG_JavaIllegalArgumentException, "Invalid byte array size");
return NULL;
}
}

%typemap(out) fz_store_drop_fn * {
$result = (jlong) (intptr_t) $1; // Return as a raw pointer
}

// Allow Java to set a function pointer callback
%typemap(in) fz_store_drop_fn * {
$1 = (fz_store_drop_fn *)(intptr_t) $input;
}

%include "../mupdf/include/mupdf/fitz/version.h"
%include "../mupdf/include/mupdf/fitz/system.h"
%include "../mupdf/include/mupdf/fitz/context.h"
%include "../mupdf/include/mupdf/fitz/stream.h"
%include "../mupdf/include/mupdf/fitz/document.h"
%include "../mupdf/include/mupdf/fitz/util.h"
%include "../mupdf/include/mupdf/fitz/export.h"
%include "../mupdf/include/mupdf/fitz/geometry.h"
%include "../mupdf/include/mupdf/fitz/color.h"
%include "../mupdf/include/mupdf/fitz/pixmap.h"
%include "../mupdf/include/mupdf/fitz/device.h"


