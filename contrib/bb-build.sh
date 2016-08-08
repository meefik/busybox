#!/bin/bash
# BusyBox build tools
# (C) 2014-2015 Anton Skshidlevsky <meefik@gmail.com>, GPLv3
# Requires:
# Android NDK r10d (http://developer.android.com/ndk/downloads/index.html)
# export ANDROID_NDK_ROOT="/path/to/ndk"
# Make a patch:
# diff -urN ../busybox-${BB_VERSION}.orig/ . > ../patches-${BB_VERSION}/${PATCH_NAME}.patch

helper()
{
    echo "Usage: $0 <arm|arm64|intel|mips> <pie|nopie|static>"
    exit 1
}

BB_VERSION="1.24.2"
ANDROID_NATIVE_API_LEVEL="android-21"
GCC_VERSION="4.9"
MARCH="$1"
PARAM="$2"
NCPU=$(grep -ci processor /proc/cpuinfo)
PREFIX="../compiled/$MARCH"
[ -z "$ANDROID_NDK_ROOT" ] && ANDROID_NDK_ROOT="$HOME/Android/Sdk/ndk-bundle"
[ "$MARCH" == "arm64" ] && ANDROID_NATIVE_API_LEVEL="android-21"
# Extra config required for specific API levels
# This will be prepended to the .config before building
EXTRACONFIG=""
[ ${ANDROID_NATIVE_API_LEVEL#android-} -ge 21 ] && EXTRACONFIG="android_ndk_defconfig-sdk21"

case "$MARCH" in
arm|arm64|intel|mips)
;;
*)
    helper
;;
esac

case "$PARAM" in
pie)
    PREFIX="$PREFIX/pie"
;;
nopie)
    PREFIX="$PREFIX/nopie"
;;
static)
    PREFIX="$PREFIX/static"
;;
*)
    helper
;;
esac

case "$(uname -m)"  in
i[3-6]86)
    HOST_ARCH="x86"
;;
x86_64)
    HOST_ARCH="x86_64"
;;
*)
    echo "Unknown architecture"
    exit 1
;;
esac

pkg="busybox-$BB_VERSION"
defconfig="android_ndk_defconfig"

echo ">>> download"
[ -e "$pkg.tar.bz2" ] || wget --progress=dot http://busybox.net/downloads/$pkg.tar.bz2 || exit 1

echo ">>> unpack"
[ -d "$pkg" ] && rm -rf $pkg
tar jvxf $pkg.tar.bz2 || exit 1

echo ">>> patch"
cd $pkg
for p in $(ls ../patches-$BB_VERSION/*.patch)
do
    patch -b -p0 < $p || exit 1
done
cp ../patches-$BB_VERSION/$defconfig ./configs/$defconfig

echo ">>> config"
case "$MARCH" in
arm)
    CONFIG_CROSS_COMPILER_PREFIX="$ANDROID_NDK_ROOT/toolchains/arm-linux-androideabi-$GCC_VERSION/prebuilt/linux-$HOST_ARCH/bin/arm-linux-androideabi-"
    CONFIG_SYSROOT="$ANDROID_NDK_ROOT/platforms/$ANDROID_NATIVE_API_LEVEL/arch-arm"
    CONFIG_EXTRA_CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -nostdlib -march=armv5te -msoft-float -mfloat-abi=softfp -mfpu=neon -mthumb -mthumb-interwork -fpic -fno-short-enums -fgcse-after-reload -frename-registers $CFLAGS"
    CONFIG_EXTRA_LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined \${SYSROOT}/usr/lib/crtbegin_dynamic.o \${SYSROOT}/usr/lib/crtend_android.o -fuse-ld=bfd $LDFLAGS"
    CONFIG_EXTRA_LDLIBS="m c gcc"
;;
arm64)
    CONFIG_CROSS_COMPILER_PREFIX="$ANDROID_NDK_ROOT/toolchains/aarch64-linux-android-$GCC_VERSION/prebuilt/linux-$HOST_ARCH/bin/aarch64-linux-android-"
    CONFIG_SYSROOT="$ANDROID_NDK_ROOT/platforms/$ANDROID_NATIVE_API_LEVEL/arch-arm64"
    CONFIG_EXTRA_CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -nostdlib -march=armv8-a -fpic -fno-short-enums -fgcse-after-reload -frename-registers $CFLAGS"
    CONFIG_EXTRA_LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker64 -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined \${SYSROOT}/usr/lib/crtbegin_dynamic.o \${SYSROOT}/usr/lib/crtend_android.o -fuse-ld=bfd $LDFLAGS"
    CONFIG_EXTRA_LDLIBS="m c gcc"
;;
intel)
    CONFIG_CROSS_COMPILER_PREFIX="$ANDROID_NDK_ROOT/toolchains/x86-$GCC_VERSION/prebuilt/linux-$HOST_ARCH/bin/i686-linux-android-"
    CONFIG_SYSROOT="$ANDROID_NDK_ROOT/platforms/$ANDROID_NATIVE_API_LEVEL/arch-x86"
    CONFIG_EXTRA_CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -nostdlib -march=i686 -mtune=atom -fpic $CFLAGS"
    CONFIG_EXTRA_LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined \${SYSROOT}/usr/lib/crtbegin_dynamic.o \${SYSROOT}/usr/lib/crtend_android.o -fuse-ld=bfd $LDFLAGS"
    CONFIG_EXTRA_LDLIBS="m c gcc"
;;
mips)
    CONFIG_CROSS_COMPILER_PREFIX="$ANDROID_NDK_ROOT/toolchains/mipsel-linux-android-$GCC_VERSION/prebuilt/linux-$HOST_ARCH/bin/mipsel-linux-android-"
    CONFIG_SYSROOT="$ANDROID_NDK_ROOT/platforms/$ANDROID_NATIVE_API_LEVEL/arch-mips"
    CONFIG_EXTRA_CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -nostdlib -march=mips32 -fpic -Wno-psabi -fomit-frame-pointer -fno-strict-aliasing -finline-functions -ffunction-sections -funwind-tables -fmessage-length=0 -fno-inline-functions-called-once -fgcse-after-reload -frerun-cse-after-loop -frename-registers $CFLAGS"
    CONFIG_EXTRA_LDFLAGS="-Xlinker -z -Xlinker muldefs -nostdlib -Bdynamic -Xlinker -dynamic-linker -Xlinker /system/bin/linker -Xlinker -z -Xlinker nocopyreloc -Xlinker --no-undefined \${SYSROOT}/usr/lib/crtbegin_dynamic.o \${SYSROOT}/usr/lib/crtend_android.o -fuse-ld=bfd $LDFLAGS"
    CONFIG_EXTRA_LDLIBS="m c gcc"
;;
esac
sed -i "s|^CONFIG_CROSS_COMPILER_PREFIX=.*|CONFIG_CROSS_COMPILER_PREFIX=\"$CONFIG_CROSS_COMPILER_PREFIX\"|" ./configs/$defconfig
sed -i "s|^CONFIG_SYSROOT=.*|CONFIG_SYSROOT=\"$CONFIG_SYSROOT\"|" ./configs/$defconfig
sed -i "s|^CONFIG_EXTRA_CFLAGS=.*|CONFIG_EXTRA_CFLAGS=\"$CONFIG_EXTRA_CFLAGS\"|" ./configs/$defconfig
sed -i "s|^CONFIG_EXTRA_LDFLAGS=.*|CONFIG_EXTRA_LDFLAGS=\"$CONFIG_EXTRA_LDFLAGS\"|" ./configs/$defconfig
sed -i "s|^CONFIG_EXTRA_LDLIBS=.*|CONFIG_EXTRA_LDLIBS=\"$CONFIG_EXTRA_LDLIBS\"|" ./configs/$defconfig
case "$PARAM" in
pie)
    sed -i "s|^CONFIG_STATIC.*|# CONFIG_STATIC is not set|" ./configs/$defconfig
    sed -i "s|^# CONFIG_PIE.*|CONFIG_PIE=y|" ./configs/$defconfig
;;
nopie)
    sed -i "s|^CONFIG_STATIC.*|# CONFIG_STATIC is not set|" ./configs/$defconfig
    sed -i "s|^CONFIG_PIE.*|# CONFIG_PIE is not set|" ./configs/$defconfig
;;
static)
    sed -i "s|^# CONFIG_STATIC.*|CONFIG_STATIC=y|" ./configs/$defconfig
    sed -i "s|^CONFIG_PIE.*|# CONFIG_PIE is not set|" ./configs/$defconfig
;;
esac
sed -i "s|^EXTRAVERSION =.*|EXTRAVERSION = -meefik|" ./Makefile
make $defconfig || exit 1

# This needs to be first otherwise it'll get ignored
if [ -n "$EXTRACONFIG" ]; then
	cat ../patches-$BB_VERSION/$EXTRACONFIG .config > .config.new && mv .config.new .config
	echo ">>> Disabling API 21+ incompatible applets (ignore the warnings)"
	make silentoldconfig
fi

echo ">>> make"
unset CFLAGS LDFLAGS
make -j$NCPU || exit 1

echo ">>> install"
make CONFIG_PREFIX=$PREFIX install || exit 1
