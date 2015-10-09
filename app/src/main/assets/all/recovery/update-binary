#!/sbin/sh
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
ui_print "Installing BusyBox to /system/xbin..."
rm /system/xbin/busybox
cp busybox /system/xbin
chmod 755 /system/xbin/busybox
/system/xbin/busybox --install -s /system/xbin/
ui_print "Unmounting /system part..."
umount /system
exit 0
