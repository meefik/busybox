#!/sbin/sh
INSTALL_DIR="/system/xbin"
archive="$3"
fdout=/proc/self/fd/$2
ui_print()
{
echo "ui_print $1" > $fdout
echo "ui_print" > $fdout
}
ui_print "Making a temporary directory..."
mkdir -p /tmp/busybox
cd /tmp/busybox
unzip -o "$archive"
ui_print "Mounting /system part..."
mount /system
ui_print "Removing BusyBox from $INSTALL_DIR..."
for link in $(find "$INSTALL_DIR" -type l)
do
    if readlink $link | grep busybox
    then
        rm $link
    fi
done
rm $INSTALL_DIR/busybox
ui_print "Installing BusyBox to $INSTALL_DIR..."
cp busybox $INSTALL_DIR
chmod 755 $INSTALL_DIR/busybox
$INSTALL_DIR/busybox --install -s $INSTALL_DIR
ui_print "Unmounting /system part..."
umount /system
exit 0
