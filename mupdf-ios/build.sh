#!/bin/zsh

cd "$(dirname "$0")"

MUPDF_FOLDER=../mupdf
rm -rf libs
mkdir libs

OSX_SDKROOT=$(xcrun --sdk macosx --show-sdk-path)
IOS_SDKROOT=$(xcrun --sdk iphoneos --show-sdk-path)
SIM_SDKROOT=$(xcrun --sdk iphonesimulator --show-sdk-path)

ARCH="arm64"
FLAGS_SIM="-DTOFU_CJK_EXT -isysroot $SIM_SDKROOT -miphonesimulator-version-min=12.0 -mios-simulator-version-min=12.0 -arch $ARCH -fembed-bitcode"
FLAGS_IOS="-DTOFU_CJK_EXT -isysroot $IOS_SDKROOT -arch $ARCH -fembed-bitcode"
FLAGS_OSX="-DTOFU_CJK_EXT -isysroot $OSX_SDKROOT -arch $ARCH -fembed-bitcode"

for FLAGS in $FLAGS_SIM $FLAGS_IOS; do

  OUT=$MUPDF_FOLDER/build/build-$ARCH
  XCFRAMEWORK_FOLDER=libs/mupdf.xcframework

  rm -rf $OUT

  make -j4 -C $MUPDF_FOLDER OUT=$OUT XCFLAGS="$FLAGS" XLDFLAGS="$FLAGS" third libs  || { echo "IOS $ARCH build failed"; exit 1; }

  ranlib $OUT/*.a

  xcodebuild -create-xcframework \
      -library $OUT/libmupdf.a -headers $MUPDF_FOLDER/include \
      -output $XCFRAMEWORK_FOLDER

  xcodebuild -create-xcframework \
      -library $OUT/libmupdf-third.a \
      -output $XCFRAMEWORK_FOLDER
  ls $XCFRAMEWORK_FOLDER
done

rm -rf src/nativeInterop/cinterop/include
cp -rf libs/mupdf.xcframework/ios-arm64-simulator/Headers src/nativeInterop/cinterop/include