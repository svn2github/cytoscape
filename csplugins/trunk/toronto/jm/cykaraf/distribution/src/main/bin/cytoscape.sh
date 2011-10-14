#!/bin/bash

cd $(dirname $0)
export KARAF_OPTS="-splash:CytoscapeSplashScreen.png"
framework/bin/karaf
