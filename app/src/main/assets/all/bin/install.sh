#!/system/bin/sh

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

busybox printf "Copying busybox to $INSTALL_DIR ... "
BB_BIN=$(busybox which busybox)
busybox cp $BB_BIN $INSTALL_DIR/busybox
if busybox test $? -eq 0
then
    busybox printf "done\n"
else
    busybox printf "fail\n"
fi

busybox printf "Setting permissions ... "
busybox chown 0:0 $INSTALL_DIR/busybox
busybox chmod 755 $INSTALL_DIR/busybox
if busybox test $? -eq 0
then
    busybox printf "done\n"
else
    busybox printf "fail\n"
fi

if busybox test "$REPLACE_APPLETS" == "true"
then
    busybox printf "Removing old applets ... "
    #busybox --list | busybox xargs -I APPLET busybox rm $INSTALL_DIR/APPLET
    busybox --list | while read f
    do
        if busybox test -e "$INSTALL_DIR/$f" -o -L "$INSTALL_DIR/$f"
        then
            busybox rm "$INSTALL_DIR/$f"
        fi
    done
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
fi

if busybox test "$INSTALL_APPLETS" == "true"
then
    busybox printf "Installing new applets ... "
    $INSTALL_DIR/busybox --install -s $INSTALL_DIR
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
