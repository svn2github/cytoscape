@ECHO OFF

:: Generates the cytoscape.vmoptions file based on whether
:: we're dealing with a 32 bit or 64 bit JVM.

java -version 2>&1 > NUL: | FIND /I "64-Bit" > NUL: && GOTO 64bit
IF NOT %errorlevel% == 0 GOTO 32bit 


:32bit
	echo -Xms10m   >  cytoscape.vmoptions 
	echo -Xmx512m >> cytoscape.vmoptions 
	GOTO end:


:64bit
	echo -Xms20m >  cytoscape.vmoptions 
	echo -Xmx20g >> cytoscape.vmoptions 

:: Shared JVM options.
echo -Dswing.aatext=true               >> cytoscape.vmoptions
echo -Dawt.useSystemAAFontSettings=lcd >> cytoscape.vmoptions

:end
set errorlevel=0

