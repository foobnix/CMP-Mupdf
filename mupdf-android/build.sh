#!/bin/zsh

cd "$(dirname "$0")"

MUPDF_FOLDER=../mupdf
pwd
echo $MUPDF_FOLDER
#Mupdf Android

export PATH=$PATH:/Users/ivanivanenko/Library/Android/sdk/ndk/29.0.14206865/

make android -C $MUPDF_FOLDER  || { echo "Android build failed"; exit 1; }
rm -rf
cp -rf $MUPDF_FOLDER/build/android/libs .

