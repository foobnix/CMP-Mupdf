#!/bin/zsh

VERSION=1.25.4
#VERSION=1.23.7
git clone --recursive https://github.com/ArtifexSoftware/mupdf.git --branch $VERSION mupdf

ROOT_FOLDER=$(pwd)
MUPDF_FOLDER=$ROOT_FOLDER/mupdf

#Change namespace for iOS compatibility
folders=("$MUPDF_FOLDER/source" "$MUPDF_FOLDER/thirdparty/harfbuzz" "$MUPDF_FOLDER/include")

for folder in "${folders[@]}"; do
  # Check if the folder exists
  if [ -d "$folder" ]; then
    echo "Processing folder: $folder"
    find "$folder" -type f \( -name "*.h" -o -name "*.c" -o -name "*.hh" -o -name "*.cc" \) | while read -r file; do
      echo "Processing file: $file"
 			sed -i '' "s/hb_/hb2_/g" "$file"
 			sed -i '' "s/HB_/HB2_/g" "$file"
    done
  else
    echo "Folder does not exist: $folder"
  fi
done

sed -i '' "s/hb_/hb2_/g" $MUPDF_FOLDER/Makelists
sed -i '' "s/HB_/HB2_/g" $MUPDF_FOLDER/Makelists


#make clean -C $MUPDF_FOLDER
make generate -C $MUPDF_FOLDER

./mupdf-android/build.sh
./mupdf-jvm/build.sh
./mupdf-ios/build.sh
#./mupdf-wasm/build.sh