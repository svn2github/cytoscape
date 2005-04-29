#!/bin/sh
cp=../../src
cmd="java -classpath $cp -server -Xms256M -Xmx256M -XX:NewSize=100M -Xverify:none GBlast -i py -o J"
/usr/bin/time $cmd
