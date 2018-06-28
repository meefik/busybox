#!/sbin/sh
INSTALL_DIR=/system/xbin
archive=$3
fdout=/proc/self/fd/$2
ui_print()
{
    echo "ui_print $1" > $fdout
    echo "ui_print" > $fdout
}
ui_print "Making a temporary directory..."
mkdir -p /tmp/busybox
cd /tmp/busybox
unzip -o $archive
ui_print "Mounting /system part..."
mount /system
ui_print "Removing BusyBox from $INSTALL_DIR..."
for link in $(find $INSTALL_DIR -type l)
do
    if readlink $link | grep busybox
    then
        rm $link
    fi
done
for fn in busybox ssl_helper
do
    if [ -e $INSTALL_DIR/$fn ]
    then
        rm $INSTALL_DIR/$fn
    fi
done
ui_print "Installing BusyBox to $INSTALL_DIR..."
for fn in busybox ssl_helper
do
    cp $fn $INSTALL_DIR
    chmod 755 $INSTALL_DIR/$fn
done
$INSTALL_DIR/busybox --install -s $INSTALL_DIR
if [ -d /system/addon.d ]; then
    cp addon.d.sh /system/addon.d/99-busybox.sh
    chmod 755 /system/addon.d/99-busybox.sh
    echo "$INSTALL_DIR" > /system/addon.d/busybox-install-dir
    chmod 644 /system/addon.d/busybox-install-dir
fi
ui_print "Unmounting /system part..."
umount /system
exit 0
