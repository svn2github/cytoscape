REM Simple Cytoscape batch script for windows/dos
REM (c) Trey Ideker June 21, 2002; Owen Ozier March 06, 2003
REM
REM Runs Cytoscape from its jar file with GO data loaded

java -Xmx512M -jar cytoscape.jar cytoscape.CyMain --JLD plugins %*

REM Use this command line to use organism specific gene annotations by default
REM java -Xmx512M -jar cytoscape.jar cytoscape.CyMain -b annotation/manifest --JLD plugins %*

