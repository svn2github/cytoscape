@ECHO OFF

:: Generates the Cytoscape.vmoptions file based on whether
:: we're dealing with a 32 bit or 64 bit JVM.

java -version 2>&1 | FINDSTR /I 64-Bit > %TMP%\MATCH.TXT
FOR /F %%A in ('DIR %TMP%\MATCH.TXT') DO (
	IF %%~zA LSS 1 GOTO 32bit
)


:64bit
	echo -Xms20m >  Cytoscape.vmoptions 
	echo -Xmx20g >> Cytoscape.vmoptions 
	GOTO end:


:32bit
	echo -Xms10m   >  Cytoscape.vmoptions 
	echo -Xmx1550m >> Cytoscape.vmoptions 

:: Shared JVM options.
echo -Dswing.aatext=true               >> Cytoscape.vmoptions
echo -Dawt.useSystemAAFontSettings=lcd >> Cytoscape.vmoptions

:end
set errorlevel=0

