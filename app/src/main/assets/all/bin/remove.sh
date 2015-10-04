#!/system/bin/sh

SYSTEM_REMOUNT=$(busybox printf "$INSTALL_DIR" | busybox grep -c "^/system/")

if busybox test "$SYSTEM_REMOUNT" -ne 0
then
    busybox printf "Remounting /system to rw ... "
    busybox mount -o rw,remount /system
    if busybox test $? -eq 0
    then
        busybox printf 'done\n'
    else
        busybox printf 'fail\n'
        exit 1
    fi
fi

busybox printf "Removing BusyBox from $INSTALL_DIR: \n"
if busybox test -d "$INSTALL_DIR"
then
    busybox printf "* busybox ... "
    if busybox test -e "$INSTALL_DIR/busybox"
    then
        busybox rm "$INSTALL_DIR/busybox"
    fi
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "fail\n"
    fi
    busybox printf "* applets ... "
    busybox ls "$INSTALL_DIR" | while read f
    do
        if busybox test "$(busybox readlink $INSTALL_DIR/$f)" == "$INSTALL_DIR/busybox"
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

if busybox test "$SYSTEM_REMOUNT" -ne 0
then
    busybox printf 'Remounting /system to ro ... '
    busybox mount -o ro,remount /system
    if busybox test $? -eq 0
    then
        busybox printf "done\n"
    else
        busybox printf "skip\n"
        exit 1
    fi
fi
