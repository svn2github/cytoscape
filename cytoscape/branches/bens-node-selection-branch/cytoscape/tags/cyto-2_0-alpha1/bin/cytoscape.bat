REM Simple Cytoscape batch script for windows/dos
REM (c) Trey Ideker June 21, 2002; Owen Ozier March 06, 2003
REM 
REM Runs Cytoscape from its jar file with GO data loaded

java -Xmx256M -classpath cytoscape.jar;.;%CLASSPATH% cytoscape.CyMain -y giny -b GO/annotationAndSynonyms --JLD plugins %*
