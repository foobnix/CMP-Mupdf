#!/bin/zsh

cd "$(dirname "$0")"

MUPDF_FOLDER=../mupdf
SWIG_OUT_FOLDER=src/main/java/mobi/librera/mupdf

rm -rf $SWIG_OUT_FOLDER
mkdir $SWIG_OUT_FOLDER

SWIG_FILE_OUT=$MUPDF_FOLDER/platform/swig/libmupdf-binding.c

swig -java -package mobi.librera.mupdf \
		-o $SWIG_FILE_OUT \
		-outdir $SWIG_OUT_FOLDER \
		mupdf.i

cp Makefile $MUPDF_FOLDER/platform/swig

sed -i '' "2679s/^/\/\//g" $SWIG_FILE_OUT
sed -i '' "2680s/^/\/\//g" $SWIG_FILE_OUT
sed -i '' "2693s/^/\/\//g" $SWIG_FILE_OUT

sed -i '' "14761s/^/\/\//g" $SWIG_FILE_OUT
sed -i '' "14762s/^/\/\//g" $SWIG_FILE_OUT
sed -i '' "14763s/^/\/\//g" $SWIG_FILE_OUT
sed -i '' "14764s/^/\/\//g" $SWIG_FILE_OUT

sed -i '' "15606s/^/\/\//g" $SWIG_FILE_OUT
sed -i '' "16794s/^/\/\//g" $SWIG_FILE_OUT

make -C $MUPDF_FOLDER/platform/swig || { echo "SWIG build failed"; exit 1; }

cp -rf $MUPDF_FOLDER/build/java/release/libmupdf_* libs
