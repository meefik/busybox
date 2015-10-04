#!/system/bin/sh

if busybox test -z "$INSTALL_DIR"
then
    INSTALL_DIR="/system/xbin"
fi

busybox --list | while read f
do
    printf "Applet: $f\n"
    if test -e "$INSTALL_DIR/$f"
    then
        if test -L "$INSTALL_DIR/$f"
        then
            symlink=$(readlink "$INSTALL_DIR/$f")
            printf "Symlinked to: $symlink\n"
        elif test -e "$INSTALL_DIR/$f"
        then
            printf "Installed to: $INSTALL_DIR/$f\n"
        fi
    else
        printf "Applet not installed.\n"
    fi
    help=$(busybox $f --help 2>&1 | grep -v "^BusyBox")
    if test -n "$help"
    then
        printf "$help\n\n"
    else
        printf "\n"
    fi
done
