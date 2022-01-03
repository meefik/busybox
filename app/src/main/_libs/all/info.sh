#!/system/bin/sh
# BusyBox information
# (c) 2015-2022 Anton Skshidlevsky <meefik@gmail.com>, GPLv3

[ "$TRACE_MODE" != "true" ] || set -x

printf "System:\n"
DEVICE=$(getprop ro.product.model)
printf "* device: $DEVICE\n"
ANDROID=$(getprop ro.build.version.release)
printf "* android: $ANDROID\n"
ARCH=$(uname -m)
printf "* architecture: $ARCH\n"
if test -d /system/addon.d
then
    printf "* addon.d: supported\n"
else
    printf "* addon.d: unsupported\n"
fi

printf "\nLatest BusyBox:\n"
BB_BIN=$(which busybox.so)
BB_VERSION=$($BB_BIN| head -1 | awk '{print $2}')
printf "* version: $BB_VERSION\n"
BB_APPLETS=$($BB_BIN --list | wc -l)
printf "* applets: $BB_APPLETS items\n"
BB_SIZE=$(stat -c '%s' "$BB_BIN")
printf "* size: $BB_SIZE bytes\n"
BB_MD5=$(md5sum "$BB_BIN" | awk '{print $1}')
printf "* md5: $BB_MD5\n"
if test -e "$(which ssl_helper.so)"
then
    printf "* ssl_helper: yes\n"
else
    printf "* ssl_helper: no\n"
fi

printf "\nInstalled BusyBox:\n"
if test -e "$INSTALL_DIR/busybox"
then
    BB_PATH="$INSTALL_DIR"
elif test -e "/system/bin/busybox"
then
    BB_PATH="/system/bin"
elif test -e "/system/xbin/busybox"
then
    BB_PATH="/system/xbin"
fi
BB_BIN="$BB_PATH/busybox"
if test -e "$BB_BIN"
then
    printf "* location: $BB_PATH\n"
    BB_VERSION=$("$BB_BIN" | head -1 | awk '{print $2}')
    printf "* version: $BB_VERSION\n"
    BB_APPLETS=$("$BB_BIN" --list | wc -l)
    printf "* applets: $BB_APPLETS items\n"
    BB_SIZE=$(stat -c '%s' "$BB_BIN")
    printf "* size: $BB_SIZE bytes\n"
    BB_MD5=$(md5sum "$BB_BIN" | awk '{print $1}')
    printf "* md5: $BB_MD5\n"
    if test -e "$BB_PATH/ssl_helper"
    then
        printf "* ssl_helper: yes\n"
    else
        printf "* ssl_helper: no\n"
    fi
    if test -d /system/addon.d
    then
        if test -f /system/addon.d/99-busybox.sh
        then
            printf "* addon.d: yes\n"
        else
            printf "* addon.d: no\n"
        fi
    fi
else
    printf "* not installed\n"
fi
