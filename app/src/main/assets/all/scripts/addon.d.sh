#!/sbin/sh
#
# /system/addon.d/99-busybox.sh
# Backup and restore busybox
#

. /tmp/backuptool.functions

do_action() {
  action="$1"
  bb_install=$(cat "$2")
  bb="$bb_install/busybox"

  [ -z "$bb_install" ] && exit 1

  if [ "$action" = "backup" ]; then
    local_bb="$bb"
    [ ! -f "$local_bb" ] && exit 1
    backup_file "$bb"
    backup_file "$bb_install/ssl_helper"
    echo "$bb_install" > /tmp/busybox-install-dir
  elif [ "$action" = "restore" ]; then
    local_bb="$C/$bb"
    [ ! -f "$local_bb" ] && exit 1
    restore_file "$bb"
    restore_file "$bb_install/ssl_helper"
  fi

  "$local_bb" --list | while read applet; do
    file="$bb_install/$applet"

    if [ "$action" = "backup" ]; then
      link=$(readlink -f "$file")
      [ -f "$file" -a "$link" = "$bb" ] && backup_file "$file"
    elif [ "$action" = "restore" ]; then
      [ -f "$C/$file" ] && restore_file "$file"
    fi
  done
}

case "$1" in
  backup)
    do_action backup "$S/addon.d/busybox-install-dir"
  ;;

  restore)
    do_action restore "/tmp/busybox-install-dir"
  ;;

  pre-backup)
    # Stub
  ;;
  post-backup)
    # Stub
  ;;
  pre-restore)
    # Stub
  ;;
  post-restore)
    # Stub
  ;;
esac
