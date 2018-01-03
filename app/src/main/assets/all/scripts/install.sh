#!/system/bin/sh
# BusyBox installer
# (c) 2015-2018 Anton Skshidlevsky <meefik@gmail.com>, GPLv3

SYSTEM_REMOUNT=$(busybox printf "$INSTALL_DIR" | busybox grep -c "^/system/")

if busybox test "$SYSTEM_REMOUNT" -ne 0
then
    busybox printf "Remounting /system to rw ... "
    busybox mount -o rw,remount /system
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
        exit 1
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
    busybox --list | while read fn
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
    $INSTALL_DIR/busybox --install -s "$INSTALL_DIR"
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
fi

if busybox test "$SYSTEM_REMOUNT" -ne 0
then
    busybox printf "Remounting /system to ro ... "
    busybox mount -o ro,remount /system
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "skip\n"
        exit 1
    fi
fi
