#!/system/bin/sh
# BusyBox uninstaller
# (c) 2015-2022 Anton Skshidlevsky <meefik@gmail.com>, GPLv3

[ "$TRACE_MODE" != "true" ] || set -x

echo "### BEGIN REMOVE"
echo

if test "$MOUNT_RAMDISK" = "true"
then
    IS_RAM=$(grep -c "^tmpfs $INSTALL_DIR" /proc/mounts)
    printf "Unmounting $INSTALL_DIR as tmpfs ... "
    if test "$IS_RAM" -gt 0
    then
        umount "$INSTALL_DIR"
        if test $? -eq 0
        then
            printf "done\n"
        else
            printf "fail\n"
        fi
    else
        printf "skip\n"
    fi
else
    IS_SYSTEM_DIR=$(printf "$INSTALL_DIR" | grep -c "^/system/")

    if test "$IS_SYSTEM_DIR" -gt 0
    then
        printf "Remounting /system to rw ... "
        mount -o rw,remount /system 2>/dev/null || mount -o rw,remount / 2>/dev/null
        if test $? -eq 0
        then
            printf 'done\n'
        else
            printf 'fail\n'
        fi
    fi

    printf "Removing BusyBox from $INSTALL_DIR: \n"
    if test -d "$INSTALL_DIR"
    then
        for fn in busybox ssl_helper
        do
            printf "* $fn ... "
            if test -e "$INSTALL_DIR/$fn"
            then
                rm "$INSTALL_DIR/$fn"
            fi
            if test $? -eq 0
            then
                printf "done\n"
            else
                printf "fail\n"
            fi
        done
        printf "* applets ... "
        ls "$INSTALL_DIR" | while read f
        do
            if test "$(readlink $INSTALL_DIR/$f)" = "$INSTALL_DIR/busybox"
            then
                rm "$INSTALL_DIR/$f"
            fi
        done
        if test $? -eq 0
        then
            printf "done\n"
        else
            printf "fail\n"
        fi
    else
        printf "... path not found.\n"
    fi

    if test -e /system/addon.d/99-busybox.sh
    then
        printf "Removing addon.d script ... "
        rm /system/addon.d/busybox-install-dir
        rm /system/addon.d/99-busybox.sh
        if test $? -eq 0
        then
            printf "done\n"
        else
            printf "fail\n"
        fi
    fi
fi

echo
echo "### END REMOVE"
