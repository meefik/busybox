# BusyBox

Copyright (C) 2015-2022 Anton Skshidlevsky, GPLv2

This application is an BusyBox installer for Android.

[BusyBox](http://busybox.net) combines tiny versions of many common UNIX utilities into a single small executable. It provides replacements for most of the utilities you usually find in GNU fileutils, shellutils, etc. The utilities in BusyBox generally have fewer options than their full-featured GNU cousins; however, the options that are included provide the expected functionality and behave very much like their GNU counterparts. BusyBox provides a fairly complete environment for any small or embedded system.

The app is available for download in Google Play and GitHub.

<a href="https://play.google.com/store/apps/details?id=ru.meefik.busybox"><img src="https://gist.githubusercontent.com/meefik/54a54afa7cc1dc600bdb855cb7895a4a/raw/ad617c006a1ac28d067c9a87cec60199ca8fef7c/get-it-on-google-play.png" alt="Get it on Google Play"></a>
<a href="https://github.com/meefik/busybox/releases/latest"><img src="https://gist.githubusercontent.com/meefik/54a54afa7cc1dc600bdb855cb7895a4a/raw/ad617c006a1ac28d067c9a87cec60199ca8fef7c/get-apk-from-github.png" alt="Get it on Github"></a>

Latest BusyBox v1.34.1, supported 378 applets:

        [, [[, acpid, adjtimex, ar, arch, arp, ascii, ash, awk, base32, base64, basename,
        bbconfig, bc, beep, blkdiscard, blkid, blockdev, bootchartd, brctl, bunzip2, busybox,
        bzcat, bzip2, cal, cat, chat, chattr, chgrp, chmod, chown, chpst, chroot, chrt, chvt,
        cksum, clear, cmp, comm, conspy, cp, cpio, crc32, crond, crontab, cryptpw, cttyhack, cut,
        date, dc, dd, deallocvt, depmod, devfsd, devmem, df, dhcprelay, diff, dirname, dmesg,
        dnsd, dnsdomainname, dos2unix, dpkg, dpkg-deb, du, dumpkmap, dumpleases, echo, ed, egrep,
        eject, env, envdir, envuidgid, ether-wake, expand, expr, factor, fakeidentd, fallocate,
        false, fatattr, fbset, fbsplash, fdflush, fdformat, fdisk, fgconsole, fgrep, find,
        findfs, flock, fold, free, freeramdisk, fsck, fsck.minix, fsfreeze, fstrim, fsync, ftpd,
        ftpget, ftpput, fuser, getopt, getty, grep, groups, gunzip, gzip, halt, hd, hdparm, head,
        hexdump, hexedit, hostname, httpd, hush, hwclock, i2cdetect, i2cdump, i2cget, i2cset,
        i2ctransfer, id, ifconfig, ifdown, ifenslave, ifplugd, ifup, inetd, init, inotifyd,
        insmod, install, ionice, iostat, ip, ipaddr, ipcalc, iplink, ipneigh, iproute, iprule,
        iptunnel, kbd_mode, kill, killall, killall5, klogd, less, link, linux32, linux64,
        linuxrc, ln, loadkmap, logger, logname, losetup, lpd, lpq, lpr, ls, lsattr, lsmod, lsof,
        lspci, lsscsi, lsusb, lzcat, lzma, lzop, lzopcat, makedevs, makemime, man, md5sum, mesg,
        microcom, mim, mkdir, mkdosfs, mke2fs, mkfifo, mkfs.ext2, mkfs.minix, mkfs.reiser,
        mkfs.vfat, mknod, mkpasswd, mkswap, mktemp, modinfo, modprobe, more, mount, mountpoint,
        mpstat, mt, mv, nameif, nbd-client, nc, netstat, nice, nl, nmeter, nohup, nologin, nproc,
        nsenter, nslookup, ntpd, nuke, od, openvt, partprobe, paste, patch, pgrep, pidof, ping,
        ping6, pipe_progress, pivot_root, pkill, pmap, popmaildir, poweroff, powertop, printenv,
        printf, ps, pscan, pstree, pwd, pwdx, raidautorun, rdate, rdev, readlink, readprofile,
        realpath, reboot, reformime, renice, reset, resize, resume, rev, rfkill, rm, rmdir,
        rmmod, route, rpm, rpm2cpio, rtcwake, run-init, run-parts, runsv, runsvdir, rx, script,
        scriptreplay, sed, sendmail, seq, setarch, setconsole, setfattr, setkeycodes, setlogcons,
        setpriv, setserial, setsid, setuidgid, sh, sha1sum, sha256sum, sha3sum, sha512sum,
        showkey, shred, shuf, slattach, sleep, smemcap, softlimit, sort, split,
        start-stop-daemon, stat, strings, stty, sum, sv, svc, svlogd, svok, swapoff, swapon,
        switch_root, sync, sysctl, tac, tail, tar, taskset, tc, tcpsvd, tee, telnet, telnetd,
        test, tftp, tftpd, time, timeout, top, touch, tr, traceroute, traceroute6, true,
        truncate, ts, tty, ttysize, tunctl, tune2fs, ubiattach, ubidetach, ubimkvol, ubirmvol,
        ubirsvol, ubiupdatevol, udhcpc, udhcpd, udpsvd, uevent, umount, uname, uncompress,
        unexpand, uniq, unix2dos, unlink, unlzma, unlzop, unshare, unxz, unzip, uptime, usleep,
        uudecode, uuencode, vconfig, vi, volname, watch, watchdog, wc, wget, which, whoami,
        whois, xargs, xxd, xz, xzcat, yes, zcat, zcip

**Requirements**:

* Device with architecture arm, arm64, x86, x86_64
* Android 8 (API 26) or later
* Superuser permissions (root)

**References**:

* [Source code](https://github.com/meefik/busybox)
* [Releases](https://github.com/meefik/busybox/releases)
* [Donations](https://meefik.github.io/donate)
