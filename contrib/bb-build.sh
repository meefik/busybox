#!/bin/bash
# BusyBox build tool
# (C) 2014-2018 Anton Skshidlevsky <meefik@gmail.com>, GPLv3
#
# Requires:
# Android NDK r10e (https://github.com/android-ndk/ndk/wiki)
# export ANDROID_NDK_ROOT="/path/to/ndk"
#
# Make a patch:
# diff -urN ../busybox-${BB_VERSION}.orig/ . > ../patches/${BB_VERSION}/${PATCH_NAME}.patch

set -e -x

BB_VERSION="1.31.1"
ANDROID_NATIVE_API_LEVEL="21"
MARCH="$1"
LINKER="$2"
NCPU="$(grep -ci processor /proc/cpuinfo)"
SOURCE_DIR="$(pwd)"
BUILD_DIR="$SOURCE_DIR/build/$MARCH"
INSTALL_DIR="$BUILD_DIR/dist"
[ -z "$ANDROID_NDK_ROOT" ] && ANDROID_NDK_ROOT="$HOME/android-ndk-r15c"
SYSROOT="$ANDROID_NDK_ROOT/platforms/android-$ANDROID_NATIVE_API_LEVEL/arch-$MARCH"

case "$MARCH" in
arm)
  TARGET_HOST="arm-linux-androideabi"
  CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -D__POSIX_VISIBLE=199209 -D__BSD_VISIBLE -nostdlib -march=armv5te -msoft-float -mfloat-abi=softfp -mthumb -fPIC -I$SYSROOT/usr/include/$TARGET_HOST -I$SYSROOT/usr/include"
  LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined -static -L$SYSROOT/usr/lib"
  LDLIBS="m c gcc"
;;
arm64)
  TARGET_HOST="aarch64-linux-android"
  CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -D__POSIX_VISIBLE=199209 -D__BSD_VISIBLE -nostdlib -march=armv8-a -fno-short-enums -fgcse-after-reload -frename-registers -fPIC -I$SYSROOT/usr/include/$TARGET_HOST -I$SYSROOT/usr/include"
  LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker64 -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined -static -L$SYSROOT/usr/lib"
  LDLIBS="m c gcc"
;;
x86)
  TARGET_HOST="i686-linux-android"
  CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -D__POSIX_VISIBLE=199209 -D__BSD_VISIBLE -nostdlib -march=i686 -mtune=atom -fPIC -I$SYSROOT/usr/include/$TARGET_HOST -I$SYSROOT/usr/include"
  LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined -static -L$SYSROOT/usr/lib"
  LDLIBS="m c gcc"
;;
x86_64)
  TARGET_HOST="x86_64-linux-android"
  CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -D__POSIX_VISIBLE=199209 -D__BSD_VISIBLE -nostdlib -march=x86-64 -mtune=atom -fPIC -I$SYSROOT/usr/include/$TARGET_HOST -I$SYSROOT/usr/include"
  LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker64 -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined -static -L$SYSROOT/usr/lib"
  LDLIBS="m c gcc"
;;
*)
  echo "Usage: $0 <arm|arm64|x86|x86_64>"
  exit 1
;;
esac

PATH="$ANDROID_NDK_ROOT/toolchains/$TARGET_HOST-4.9/prebuilt/linux-x86_64/bin:$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin:$PATH"

[ -e "$BUILD_DIR" ] || mkdir -p "$BUILD_DIR";
[ -e "$INSTALL_DIR" ] || mkdir -p "$INSTALL_DIR";

pkg="busybox-$BB_VERSION"
defconfig="android_ndk_defconfig"

echo ">>> download"
[ -e "$BUILD_DIR/$pkg.tar.bz2" ] || wget http://busybox.net/downloads/$pkg.tar.bz2 -O "$BUILD_DIR/$pkg.tar.bz2" || exit 1

echo ">>> unpack"
[ -d "$BUILD_DIR/$pkg" ] && rm -rf "$BUILD_DIR/$pkg"
tar jxf "$BUILD_DIR/$pkg.tar.bz2" -C "$BUILD_DIR" || exit 1

echo ">>> patch"
cd "$BUILD_DIR/$pkg"
for p in $(ls $SOURCE_DIR/patches/$BB_VERSION/*.patch)
do
    patch -b -p0 < $p || exit 1
done
cp $SOURCE_DIR/patches/$BB_VERSION/$defconfig ./configs/$defconfig

echo ">>> config"
sed -i "s|^CONFIG_CROSS_COMPILER_PREFIX=.*|CONFIG_CROSS_COMPILER_PREFIX=\"$TARGET_HOST-\"|" ./configs/$defconfig
sed -i "s|^CONFIG_SYSROOT=.*|CONFIG_SYSROOT=\"$SYSROOT\"|" ./configs/$defconfig
sed -i "s|^CONFIG_EXTRA_CFLAGS=.*|CONFIG_EXTRA_CFLAGS=\"$CFLAGS\"|" ./configs/$defconfig
sed -i "s|^CONFIG_EXTRA_LDFLAGS=.*|CONFIG_EXTRA_LDFLAGS=\"$LDFLAGS\"|" ./configs/$defconfig
sed -i "s|^CONFIG_EXTRA_LDLIBS=.*|CONFIG_EXTRA_LDLIBS=\"$LDLIBS\"|" ./configs/$defconfig
sed -i "s|^EXTRAVERSION =.*|EXTRAVERSION = -meefik|" ./Makefile
make $defconfig || exit 1

echo ">>> make"
unset CFLAGS LDFLAGS
make -j$NCPU || exit 1

echo ">>> install"
make CONFIG_PREFIX="$INSTALL_DIR" install || exit 1

echo ">>> ssl_helper"
# export AR=llvm-ar
# export AS=llvm-as
# export CC=clang
# export CXX=clang++
# export LD=$TARGET_HOST-ld
# export STRIP=$TARGET_HOST-strip

if [ ! -e "$BUILD_DIR/v3.13.0-stable.tar.gz" ]
then
  wget https://github.com/wolfSSL/wolfssl/archive/v3.13.0-stable.tar.gz -O "$BUILD_DIR/v3.13.0-stable.tar.gz"
fi
[ -e "$BUILD_DIR/wolfssl-3.13.0-stable" ] && rm -r "$BUILD_DIR/wolfssl-3.13.0-stable"
tar xzf "$BUILD_DIR/v3.13.0-stable.tar.gz" -C "$BUILD_DIR"
cd "$BUILD_DIR/wolfssl-3.13.0-stable"
./autogen.sh
./configure --enable-static --enable-singlethreaded --disable-shared --host=$TARGET_HOST
make -j$NCPU

#$CC $CFLAGS $LDFLAGS -Os -Wall -I "$BUILD_DIR/wolfssl-3.13.0-stable" -c "$SOURCE_DIR/ssl_helper.c" -o "$BUILD_DIR/ssl_helper.o"
#$CC $CFLAGS $LDFLAGS -static "$BUILD_DIR/ssl_helper.o" "$BUILD_DIR/wolfssl-3.13.0-stable/src/.libs/libwolfssl.a" -lm -o "$INSTALL_DIR/ssl_helper"
#$STRIP -s "$INSTALL_DIR/ssl_helper"
