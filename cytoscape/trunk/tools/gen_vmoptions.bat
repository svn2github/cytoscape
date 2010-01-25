ECHO OFF

REM Generates the cytoscape.vmoptions file based on whether
REM we're dealing with a 32 bit or 64 bit JVM.

java -version 2>&1 | FINDSTR /I 64-Bit > NUL:
GOTO label%ERRORLEVEL%

REM 64-bit JVM
:label0
	echo -Xms250m > cytoscape.vmoptions 
	echo -Xmx20000m >> cytoscape.vmoptions 
	GOTO end:


REM 32-Bit JVM
:label1
	echo -Xms50m > cytoscape.vmoptions 
	echo -Xmx1550m >> cytoscape.vmoptions 


:end
 