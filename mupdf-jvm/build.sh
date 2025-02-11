#!/bin/zsh

cd "$(dirname "$0")"

cp -rf $MUPDF_FOLDER/platform/java/src $ROOT_FOLDER/mupdf-jvm/src/main/java
rm -rf $ROOT_FOLDER/mupdf-jvm/src/main/java/com/artifex/mupdf/fitz/android

#Copy source to android
cp -a $MUPDF_FOLDER/platform/java/src/. $ROOT_FOLDER/mupdf-android/src/main/java

sed -i '' "s/1.7/8/g" $MUPDF_FOLDER/platform/java/Makefile
make -C $MUPDF_FOLDER/platform/java || { echo "JVM build failed"; exit 1; }

cp -rf $MUPDF_FOLDER/build/java/release/libmupdf_* $ROOT_FOLDER/mupdf-jvm/libs