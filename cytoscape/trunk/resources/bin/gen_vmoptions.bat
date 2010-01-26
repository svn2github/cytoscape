@ECHO OFF

:: Generates the cytoscape.vmoptions file based on whether
:: we're dealing with a 32 bit or 64 bit JVM.

java -version 2>&1 | FINDSTR /I 64-Bit > NUL:
IF NOT %errorlevel% == 0 GOTO 32bit 


:64bit
	echo -Xms20m > cytoscape.vmoptions 
	echo -Xmx2000000m >> cytoscape.vmoptions 
	GOTO end:


:32bit
	echo -Xms10m > cytoscape.vmoptions 
	echo -Xmx1550m >> cytoscape.vmoptions 


:end
set errorlevel=0

