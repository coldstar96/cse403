#!/bin/sh

if [ $# -ne 2 ]; then
	echo "Usage: build-and-install.sh <build action> <path to .apk>"
	echo "  <build action>  Either debug or release"
	echo "  <path to .apk>  Path to the generated .apk file (including the bin/)"
	exit 1
else
	ant $1 && adb install $2
fi
