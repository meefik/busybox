# How To Build

```sh
apt-get install build-essential autoconf libtool

wget -O - https://github.com/wolfSSL/wolfssl/archive/v3.13.0-stable.tar.gz | tar xz

cd wolfssl-*
export CFLAGS="-Os -static -fomit-frame-pointer -falign-functions=1 -falign-labels=1 -falign-loops=1 -falign-jumps=1 -ffunction-sections -fdata-sections"
export C_EXTRA_FLAGS="-DWOLFSSL_STATIC_RSA"
./autogen.sh
./configure --enable-static --enable-singlethreaded --disable-shared
make

cd ..
gcc -Os -Wall -I wolfssl-* -c ssl_helper.c -o ssl_helper.o
gcc -static ssl_helper.o wolfssl-*/src/.libs/libwolfssl.a -lm -o ssl_helper
strip -s ssl_helper
```

