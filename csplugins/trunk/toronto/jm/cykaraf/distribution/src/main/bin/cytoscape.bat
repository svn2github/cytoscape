@echo off

set KARAF_TITLE=Cytoscape
set DEBUG_PORT=12345

set JAVA_MAX_MEM=1550M

set JAVA_DEBUG_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%DEBUG_PORT%
set KARAF_OPTS=-Xss10M -splash:CytoscapeSplashScreen.png

framework/bin/karaf client %1 %2 %3 %4 %5 %6 %7 %8
