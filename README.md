# BusyBox

Copyright (C) 2015 Anton Skshidlevsky, [GPLv3](http://opensource.org/licenses/gpl-3.0.html)

This application is an BusyBox installer for Android.

[BusyBox](http://busybox.net) combines tiny versions of many common UNIX utilities into a single small executable. It provides replacements for most of the utilities you usually find in GNU fileutils, shellutils, etc. The utilities in BusyBox generally have fewer options than their full-featured GNU cousins; however, the options that are included provide the expected functionality and behave very much like their GNU counterparts. BusyBox provides a fairly complete environment for any small or embedded system.

Latest BusyBox v1.23.2, supported 337 applets:

	[, [[, acpid, adjtimex, ar, arp, arping, ash, awk, base64, basename,
	bbconfig, beep, blkid, blockdev, bootchartd, brctl, bunzip2, bzcat,
	bzip2, cal, cat, catv, chat, chattr, chgrp, chmod, chown, chpst,
	chroot, chrt, chvt, cksum, clear, cmp, comm, cp, cpio, crond, crontab,
	cryptpw, cttyhack, cut, date, dc, dd, deallocvt, depmod, devfsd,
	devmem, df, diff, dirname, dmesg, dnsd, dnsdomainname, dos2unix, dpkg,
	dpkg-deb, du, dumpkmap, echo, ed, egrep, env, envdir, envuidgid,
	ether-wake, expand, expr, fakeidentd, false, fatattr, fbset, fbsplash,
	fdflush, fdformat, fdisk, fgconsole, fgrep, find, findfs, flash_lock,
	flash_unlock, flashcp, flock, fold, free, freeramdisk, fsck,
	fsck.minix, fstrim, fsync, ftpd, ftpget, ftpput, fuser, getopt, grep,
	groups, gunzip, gzip, halt, hd, hdparm, head, hexdump, hostname, httpd,
	hush, hwclock, id, ifconfig, ifdown, ifenslave, ifplugd, ifup, inetd,
	init, inotifyd, insmod, install, ionice, iostat, ip, ipaddr, ipcalc,
	iplink, iproute, iprule, iptunnel, kbd_mode, kill, killall, killall5,
	klogd, less, linux32, linux64, linuxrc, ln, loadkmap, logger, logname,
	losetup, lpd, lpq, lpr, ls, lsattr, lsmod, lsof, lspci, lsusb, lzcat,
	lzma, lzop, lzopcat, makedevs, makemime, man, md5sum, mdev, mesg,
	microcom, mkdir, mkdosfs, mke2fs, mkfifo, mkfs.ext2, mkfs.minix,
	mkfs.reiser, mkfs.vfat, mknod, mkpasswd, mkswap, mktemp, modinfo,
	modprobe, more, mount, mountpoint, mpstat, mt, mv, nameif, nanddump,
	nandwrite, nbd-client, nc, netstat, nice, nmeter, nohup, nslookup,
	ntpd, od, openvt, patch, pgrep, pidof, ping, ping6, pipe_progress,
	pivot_root, pkill, pmap, popmaildir, poweroff, powertop, printenv,
	printf, ps, pscan, pstree, pwd, pwdx, raidautorun, rdate, rdev,
	readlink, readprofile, realpath, reboot, reformime, renice, reset,
	resize, rev, rm, rmdir, rmmod, route, rpm, rpm2cpio, rtcwake,
	run-parts, runsv, runsvdir, rx, script, scriptreplay, sed, sendmail,
	seq, setarch, setconsole, setkeycodes, setlogcons, setserial, setsid,
	setuidgid, sh, sha1sum, sha256sum, sha3sum, sha512sum, showkey, shuf,
	slattach, sleep, smemcap, softlimit, sort, split, start-stop-daemon,
	stat, strings, stty, sum, sv, svlogd, swapoff, swapon, switch_root,
	sync, sysctl, tac, tail, tar, tcpsvd, tee, telnet, telnetd, test, tftp,
	tftpd, time, timeout, top, touch, tr, traceroute, traceroute6, true,
	tty, ttysize, tunctl, tune2fs, ubiattach, ubidetach, ubimkvol,
	ubirmvol, ubirsvol, ubiupdatevol, udpsvd, umount, uname, uncompress,
	unexpand, uniq, unix2dos, unlink, unlzma, unlzop, unxz, unzip, uptime,
	usleep, uudecode, uuencode, vconfig, vi, volname, watch, watchdog, wc,
	wget, which, whoami, whois, xargs, xz, xzcat, yes, zcat, zcip

**Requirements**:

* Device with architecture ARM, x86 or MIPS
* Android 2.3 (API 9) or later
* Superuser permissions (root)

**Use without root permissions**

To access busybox tools without superuser privileges, perform the following command in Android terminal:

    export PATH=/data/data/ru.meefik.busybox/files/bin:$PATH

**Referenses**:

* [Source code](https://github.com/meefik/busybox)
* [Releases](https://github.com/meefik/busybox/releases)
* [Donations](http://meefik.github.io/donate/)
