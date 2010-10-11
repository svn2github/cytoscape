@echo off

:: Generates the Cytoscape.vmoptions file based on whether
:: we're dealing with a 32 bit or 64 bit JVM.

:: Create the .cytoscape directory if it doesn't already exist
if exist %HOMEPATH%\.cytoscape goto dot_cytoscape_exists
mkdir %HOMEPATH%\.cytoscape
:dot_cytoscape_exists

if exist findstr.out del findstr.out
java -version 2>&1 | findstr /I 64-Bit > findstr.out
for /f %%i in ('dir /b findstr.out') do if %%~zi equ 0 goto 32bit


:64bit
	echo -Xms20m >  %HOMEPATH%\.cytoscape\Cytoscape.vmoptions
	echo -Xmx20g >> %HOMEPATH%\.cytoscape\Cytoscape.vmoptions
	goto shared


:32bit
	echo -Xms10m   >  %HOMEPATH%\.cytoscape\Cytoscape.vmoptions
	echo -Xmx1550m >> %HOMEPATH%\.cytoscape\Cytoscape.vmoptions

:shared
	echo -Dswing.aatext=true               >> %HOMEPATH%\.cytoscape\Cytoscape.vmoptions
	echo -Dawt.useSystemAAFontSettings=lcd >> %HOMEPATH%\.cytoscape\Cytoscape.vmoptions

	del findstr.out
