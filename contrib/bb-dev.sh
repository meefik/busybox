#!/bin/bash
case "$1" in
patch)
    patch -b -p0 < $2
;;
diff)
    find . -name "*.orig" -o -name "*.rej" | while read f; do rm $f; done
    diff -urN ../busybox-1.23.2.orig/ . > ../patches/$2
;;
*)
    echo "Usage: $0 patch|diff FILE"
;;
esac
