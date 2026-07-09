#!/usr/bin/env bash
set -e

# Changed to premake4 and standard gmake
premake4 --file=build-linux.lua gmake
make clean config=release_x64
make config=release_x64

mv libsteamworks4j.so ../src/main/resources/libsteamworks4j.so
mv libsteamworks4j-server.so ../server/src/main/resources/libsteamworks4j-server.so
mv libsteamworks4j-encryptedappticket.so ../server/src/main/resources/libsteamworks4j-encryptedappticket.so