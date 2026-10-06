#!/bin/sh
set -eu

sdk="${ANDROID_SDK_ROOT:-/opt/homebrew/share/android-commandlinetools}"
tools="$sdk/build-tools/36.0.0"
android_jar="$sdk/platforms/android-36/android.jar"
base="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
build="$base/.local/build"
mkdir -p "$build/classes" "$build/dex"

"$tools/aapt" package -f -M "$base/AndroidManifest.xml" -I "$android_jar" -F "$build/base.apk"
javac -source 8 -target 8 -bootclasspath "$android_jar" -d "$build/classes" \
  "$base/src/ar/com/flowbox/motivation/MainActivity.java" \
  "$base/src/ar/com/flowbox/motivation/VideoProvider.java"
"$tools/d8" --min-api 26 --lib "$android_jar" --output "$build/dex" \
  "$build/classes/ar/com/flowbox/motivation/MainActivity.class" \
  "$build/classes/ar/com/flowbox/motivation/VideoProvider.class"
(cd "$build/dex" && "$tools/aapt" add "$build/base.apk" classes.dex)
"$tools/zipalign" -f 4 "$build/base.apk" "$build/aligned.apk"

key="$base/.local/video-sorpresa.keystore"
if [ ! -f "$key" ]; then
  keytool -genkeypair -keystore "$key" -storepass android -keypass android \
    -alias video-sorpresa -keyalg RSA -keysize 2048 -validity 10000 \
    -dname 'CN=Video Sorpresa' -noprompt >/dev/null 2>&1
fi
"$tools/apksigner" sign --ks "$key" --ks-pass pass:android \
  --key-pass pass:android --out "$build/video-sorpresa.apk" "$build/aligned.apk"
"$tools/apksigner" verify "$build/video-sorpresa.apk"
printf '%s\n' "$build/video-sorpresa.apk"
