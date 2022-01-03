#!/system/bin/sh
# BusyBox installer
# (c) 2015-2022 Anton Skshidlevsky <meefik@gmail.com>, GPLv3

[ "$TRACE_MODE" != "true" ] || set -x

echo "### BEGIN INSTALL"
echo

IS_SYSTEM_DIR=$(printf "$INSTALL_DIR" | grep -c "^/system/")

if test "$MOUNT_RAMDISK" = "true"
then
    IS_RAM=$(grep -c "^tmpfs $INSTALL_DIR" /proc/mounts)
    if test "$IS_RAM" -eq 0
    then
        printf "Mounting $INSTALL_DIR as tmpfs ... "
        mount -o size=50M -t tmpfs tmpfs "$INSTALL_DIR"
        if test $? -eq 0
        then
            printf "done\n"
        else
            printf "fail\n"
        fi
    fi
elif test "$IS_SYSTEM_DIR" -gt 0
then
    printf "Remounting /system to rw ... "
    mount -o rw,remount /system 2>/dev/null || mount -o rw,remount / 2>/dev/null
    if test $? -eq 0
    then
        printf "done\n"
    else
        printf "fail\n"
    fi
fi

for fn in busybox ssl_helper
do
    printf "Copying $fn to $INSTALL_DIR ... "
    SOURCE_BIN=$(which $fn)
    if test -e "$INSTALL_DIR/$fn"
    then
        rm "$INSTALL_DIR/$fn"
    fi
    cp "$SOURCE_BIN" "$INSTALL_DIR/$fn"
    if test $? -eq 0
    then
        printf "done\n"
    else
        printf "fail\n"
    fi

    printf "Changing permissions for $fn ... "
    chown 0:0 "$INSTALL_DIR/$fn"
    chmod 755 "$INSTALL_DIR/$fn"
    if test $? -eq 0
    then
        printf "done\n"
    else
        printf "fail\n"
    fi
done

if test "$REPLACE_APPLETS" = "true"
then
    printf "Removing old applets ... "
    #busybox --list | xargs -I APPLET rm $INSTALL_DIR/APPLET
    busybox --list | grep -v busybox | while read fn
    do
        if test -e "$INSTALL_DIR/$fn" -o -L "$INSTALL_DIR/$fn"
        then
            rm "$INSTALL_DIR/$fn"
        fi
    done
    if test $? -eq 0
    then
        printf "done\n"
    else
        printf "fail\n"
    fi
fi

if test "$INSTALL_APPLETS" = "true"
then
    printf "Installing new applets ... "
    "$INSTALL_DIR/busybox" --install -s "$INSTALL_DIR"
    if test $? -eq 0
    then
        printf "done\n"
    else
        printf "fail\n"
    fi
fi

if test "$IS_SYSTEM_DIR" -gt 0 -a -d /system/addon.d -a "$MOUNT_RAMDISK" != "true"
then
    printf "Installing addon.d script ... "
    echo "$INSTALL_DIR" > /system/addon.d/busybox-install-dir
    chmod 644 /system/addon.d/busybox-install-dir
    cp $(which addon_d.sh) /system/addon.d/99-busybox.sh
    chown 0:0 /system/addon.d/99-busybox.sh
    chmod 755 /system/addon.d/99-busybox.sh
    if test $? -eq 0
    then
        printf "done\n"
    else
        printf "fail\n"
    fi
fi

echo
echo "### END INSTALL"
