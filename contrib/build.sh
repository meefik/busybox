#!/bin/bash
# BusyBox build tool
# (C) 2014-2022 Anton Skshidlevsky <meefik@gmail.com>, GPLv3
#
# Make custom patch:
# diff -urN ../busybox-${BB_VERSION}.orig/ . > ../patches/${BB_VERSION}/${PATCH_NAME}.patch

set -e

MARCH="$1"
INSTALL_DIR="$2"
BB_VERSION="1.34.1"
ANDROID_NATIVE_API_LEVEL="21"
NCPU="$(grep -ci processor /proc/cpuinfo)"
SOURCE_DIR="${PWD}"
BUILD_DIR="${SOURCE_DIR}/build"
WOLFSSL_VERSION="3.15.7-stable"
DEFCONFIG="android_ndk_defconfig"
NDK="android-ndk-r15c"
NDK_DIR="${BUILD_DIR}/${NDK}"
SYSROOT="${NDK_DIR}/platforms/android-${ANDROID_NATIVE_API_LEVEL}/arch-${MARCH}"
[[ -n "${INSTALL_DIR}" ]] || INSTALL_DIR="${BUILD_DIR}/dist"

if [[ "${MARCH}" = "all" ]]
then
  for MARCH in arm arm64 x86 x86_64
  do
    $0 "${MARCH}" "${INSTALL_DIR}"
  done
  exit $?
fi

case "${MARCH}" in
arm)
  TARGET_HOST="arm-linux-androideabi"
  PATH="${NDK_DIR}/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin:${PATH}"
;;
arm64)
  TARGET_HOST="aarch64-linux-android"
  PATH="${NDK_DIR}/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin:${PATH}"
;;
x86)
  TARGET_HOST="i686-linux-android"
  PATH="${NDK_DIR}/toolchains/x86-4.9/prebuilt/linux-x86_64/bin:${PATH}"
;;
x86_64)
  TARGET_HOST="x86_64-linux-android"
  PATH="${NDK_DIR}/toolchains/x86_64-4.9/prebuilt/linux-x86_64/bin:${PATH}"
;;
*)
  echo "Usage: $0 <arm|arm64|x86|x86_64|all> [INSTALL_DIR]"
  exit 1
;;
esac

export LD_LIBRARY_PATH="${SYSROOT}/usr/lib:${SYSROOT}/usr/lib64"
export CFLAGS="-DANDROID -D__ANDROID__ -DSK_RELEASE -D__POSIX_VISIBLE=199209 -D__BSD_VISIBLE -fPIC -I${SYSROOT}/usr/include/${TARGET_HOST} -I${SYSROOT}/usr/include"
export LDFLAGS="-L${SYSROOT}/usr/lib -L${SYSROOT}/usr/lib64 -static --sysroot=${SYSROOT}"

[[ -e "${BUILD_DIR}" ]] || mkdir -p "${BUILD_DIR}";
[[ -e "${INSTALL_DIR}/${MARCH}" ]] || mkdir -p "${INSTALL_DIR}/${MARCH}";

wget -c https://dl.google.com/android/repository/${NDK}-linux-x86_64.zip -O "${BUILD_DIR}/${NDK}-linux-x86_64.zip"
wget -c http://busybox.net/downloads/busybox-${BB_VERSION}.tar.bz2 -O "${BUILD_DIR}/busybox-${BB_VERSION}.tar.bz2"
wget -c https://github.com/wolfSSL/wolfssl/archive/v${WOLFSSL_VERSION}.tar.gz -O "$BUILD_DIR/wolfssl-${WOLFSSL_VERSION}.tar.gz"

if [[ ! -e "${BUILD_DIR}/${NDK}" ]]
then
  unzip "${BUILD_DIR}/${NDK}-linux-x86_64.zip" -d "${BUILD_DIR}"
fi

[[ ! -e "${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}" ]] || rm -rf "${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}"
tar xvzf "${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}.tar.gz" -C "${BUILD_DIR}"

[[ ! -e "${BUILD_DIR}/busybox-${BB_VERSION}" ]] || rm -rf "${BUILD_DIR}/busybox-${BB_VERSION}"
tar jvxf "${BUILD_DIR}/busybox-${BB_VERSION}.tar.bz2" -C "${BUILD_DIR}"
cd "$BUILD_DIR/busybox-${BB_VERSION}"
for p in $(ls ${SOURCE_DIR}/patches/${BB_VERSION}/*.patch)
do
  patch -b -p0 < ${p}
done

cd "$BUILD_DIR/busybox-${BB_VERSION}"
cp ${SOURCE_DIR}/patches/${BB_VERSION}/${DEFCONFIG} ./configs/${DEFCONFIG}
sed -i "s|^CONFIG_CROSS_COMPILER_PREFIX=.*|CONFIG_CROSS_COMPILER_PREFIX=\"${TARGET_HOST}-\"|" ./configs/${DEFCONFIG}
sed -i "s|^CONFIG_SYSROOT=.*|CONFIG_SYSROOT=\"${SYSROOT}\"|" ./configs/${DEFCONFIG}
sed -i "s|^CONFIG_EXTRA_CFLAGS=.*|CONFIG_EXTRA_CFLAGS=\"${CFLAGS}\"|" ./configs/${DEFCONFIG}
sed -i "s|^CONFIG_EXTRA_LDFLAGS=.*|CONFIG_EXTRA_LDFLAGS=\"${LDFLAGS}\"|" ./configs/${DEFCONFIG}
sed -i "s|^EXTRAVERSION =.*|EXTRAVERSION = -meefik|" ./Makefile
make ${DEFCONFIG}

cd "$BUILD_DIR/busybox-${BB_VERSION}"
make -j${NCPU}
make CONFIG_PREFIX="${INSTALL_DIR}/${MARCH}" install

cd "${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}"
./autogen.sh
./configure --enable-static --enable-singlethreaded --disable-shared --host=${TARGET_HOST}
make -j${NCPU}

CC="${TARGET_HOST}-gcc"
STRIP="${TARGET_HOST}-strip"
$CC $CFLAGS $LDFLAGS -Os -Wall -I"${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}" -c "${SOURCE_DIR}/ssl_helper.c" -o "${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}/ssl_helper.o"
$CC $CFLAGS $LDFLAGS "${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}/ssl_helper.o" "${BUILD_DIR}/wolfssl-${WOLFSSL_VERSION}/src/.libs/libwolfssl.a" -lm -o "${INSTALL_DIR}/${MARCH}/bin/ssl_helper"
$STRIP -s "${INSTALL_DIR}/${MARCH}/bin/ssl_helper"
