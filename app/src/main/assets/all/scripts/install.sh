#!/system/bin/sh
# BusyBox installer
# (c) 2015-2019 Anton Skshidlevsky <meefik@gmail.com>, GPLv3

IS_SYSTEM=$(busybox printf "$INSTALL_DIR" | busybox grep -c "^/system/")
IS_RAM=$(busybox grep -c "^tmpfs $INSTALL_DIR" /proc/mounts)

if busybox test "$MOUNT_RAMDISK" = "true" -a "$IS_RAM" -eq 0
then
    busybox printf "Mounting $INSTALL_DIR as tmpfs ... "
    busybox mount -o size=50M -t tmpfs tmpfs "$INSTALL_DIR"
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
elif busybox test "$IS_SYSTEM" -ne 0
then
    busybox printf "Remounting /system to rw ... "
    busybox mount -o rw,remount /system
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
fi

for fn in busybox ssl_helper
do
    busybox printf "Copying $fn to $INSTALL_DIR ... "
    SOURCE_BIN=$(busybox which $fn)
    if busybox test -e "$INSTALL_DIR/$fn"
    then
        busybox rm "$INSTALL_DIR/$fn"
    fi
    busybox cp "$SOURCE_BIN" "$INSTALL_DIR/$fn"
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi

    busybox printf "Changing permissions for $fn ... "
    busybox chown 0:0 "$INSTALL_DIR/$fn"
    busybox chmod 755 "$INSTALL_DIR/$fn"
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
done

if busybox test "$REPLACE_APPLETS" = "true"
then
    busybox printf "Removing old applets ... "
    #busybox --list | busybox xargs -I APPLET busybox rm $INSTALL_DIR/APPLET
    busybox --list | busybox grep -v busybox | while read fn
    do
        if busybox test -e "$INSTALL_DIR/$fn" -o -L "$INSTALL_DIR/$fn"
        then
            busybox rm "$INSTALL_DIR/$fn"
        fi
    done
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
fi

if busybox test "$INSTALL_APPLETS" = "true"
then
    busybox printf "Installing new applets ... "
    "$INSTALL_DIR/busybox" --install -s "$INSTALL_DIR"
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
fi

if busybox test "$IS_SYSTEM" -ne 0 -a -d /system/addon.d -a "$MOUNT_RAMDISK" != "true"
then
    busybox printf "Installing addon.d script ... "
    echo "$INSTALL_DIR" > /system/addon.d/busybox-install-dir
    busybox chmod 644 /system/addon.d/busybox-install-dir
    busybox cp "$ENV_DIR/scripts/addon.d.sh" /system/addon.d/99-busybox.sh
    busybox chown 0:0 /system/addon.d/99-busybox.sh
    busybox chmod 755 /system/addon.d/99-busybox.sh
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
fi
