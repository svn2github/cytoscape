REM Simple Cytoscape batch script for windows/dos
REM (c) Trey Ideker June 21, 2002; Owen Ozier March 06, 2003
REM 
REM Runs Cytoscape from its jar file with GO data loaded

java -Xmx256M -jar cytoscape.jar cytoscape.CyMain -y giny -b annotation/manifest --JLD plugins %*
