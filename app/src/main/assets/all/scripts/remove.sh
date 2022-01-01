#!/system/bin/sh
# BusyBox uninstaller
# (c) 2015-2022 Anton Skshidlevsky <meefik@gmail.com>, GPLv3

IS_SYSTEM=$(busybox printf "$INSTALL_DIR" | busybox grep -c "^/system/")

if busybox test "$IS_SYSTEM" -ne 0
then
    busybox printf "Remounting /system to rw ... "
    busybox mount -o rw,remount /system || busybox mount -o rw,remount /
    if busybox test $? -eq 0
    then
        busybox printf 'done\n'
    else
        busybox printf 'fail\n'
    fi
fi

busybox printf "Removing BusyBox from $INSTALL_DIR: \n"
if busybox test -d "$INSTALL_DIR"
then
    for fn in busybox ssl_helper
    do
        busybox printf "* $fn ... "
        if busybox test -e "$INSTALL_DIR/$fn"
        then
            busybox rm "$INSTALL_DIR/$fn"
        fi
        if busybox test $? -eq 0
        then
            busybox printf "done\n"
        else
            busybox printf "fail\n"
        fi
    done
    busybox printf "* applets ... "
    busybox ls "$INSTALL_DIR" | while read f
    do
        if busybox test "$(busybox readlink $INSTALL_DIR/$f)" = "$INSTALL_DIR/busybox"
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
else
    busybox printf "... path not found.\n"
fi

busybox printf "Removing addon.d script ... "
if busybox test -e /system/addon.d/99-busybox.sh
then
    busybox rm /system/addon.d/busybox-install-dir
    busybox rm /system/addon.d/99-busybox.sh
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
else
    busybox printf "not found\n"
fi
