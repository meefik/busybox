#!/system/bin/sh
# BusyBox information
# (c) 2015-2022 Anton Skshidlevsky <meefik@gmail.com>, GPLv3

busybox printf "System:\n"
DEVICE=$(getprop ro.product.model)
busybox printf "* device: $DEVICE\n"
ANDROID=$(getprop ro.build.version.release)
busybox printf "* android: $ANDROID\n"
ARCH=$(busybox uname -m)
busybox printf "* architecture: $ARCH\n"
if busybox test -d /system/addon.d
then
    busybox printf "* addon.d: supported\n"
else
    busybox printf "* addon.d: unsupported\n"
fi

busybox printf "\nFree space:\n"
DATA_FREE=$(busybox df -Ph /data | busybox grep -v ^Filesystem | busybox awk '{print $4}')
busybox printf "* /data: $DATA_FREE\n"
SYSTEM_FREE=$(busybox df -Ph /system | busybox grep -v ^Filesystem | busybox awk '{print $4}')
busybox printf "* /system: $SYSTEM_FREE\n"

busybox printf "\nLatest BusyBox:\n"
BB_BIN=$(busybox which busybox)
BB_VERSION=$(busybox | busybox head -1 | busybox awk '{print $2}')
busybox printf "* version: $BB_VERSION\n"
BB_APPLETS=$(busybox --list | busybox wc -l)
busybox printf "* applets: $BB_APPLETS items\n"
BB_SIZE=$(busybox stat -c '%s' "$BB_BIN")
busybox printf "* size: $BB_SIZE bytes\n"
BB_MD5=$(busybox md5sum "$BB_BIN" | busybox awk '{print $1}')
busybox printf "* md5: $BB_MD5\n"
if busybox test -e "$(which ssl_helper)"
then
    busybox printf "* ssl_helper: yes\n"
else
    busybox printf "* ssl_helper: no\n"
fi

busybox printf "\nInstalled BusyBox:\n"
if busybox test -e "$INSTALL_DIR/busybox"
then
    BB_PATH="$INSTALL_DIR"
elif busybox test -e "/system/bin/busybox"
then
    BB_PATH="/system/bin"
elif busybox test -e "/system/xbin/busybox"
then
    BB_PATH="/system/xbin"
fi
BB_BIN="$BB_PATH/busybox"
if busybox test -e "$BB_BIN"
then
    busybox printf "* location: $BB_PATH\n"
    BB_VERSION=$("$BB_BIN" | busybox head -1 | busybox awk '{print $2}')
    busybox printf "* version: $BB_VERSION\n"
    BB_APPLETS=$("$BB_BIN" --list | busybox wc -l)
    busybox printf "* applets: $BB_APPLETS items\n"
    BB_SIZE=$(busybox stat -c '%s' "$BB_BIN")
    busybox printf "* size: $BB_SIZE bytes\n"
    BB_MD5=$(busybox md5sum "$BB_BIN" | busybox awk '{print $1}')
    busybox printf "* md5: $BB_MD5\n"
    if busybox test -e "$BB_PATH/ssl_helper"
    then
        busybox printf "* ssl_helper: yes\n"
    else
        busybox printf "* ssl_helper: no\n"
    fi
    if busybox test -d /system/addon.d
    then
        if busybox test -f /system/addon.d/99-busybox.sh
        then
            busybox printf "* addon.d: yes\n"
        else
            busybox printf "* addon.d: no\n"
        fi
    fi
else
    busybox printf "* not installed\n"
fi
