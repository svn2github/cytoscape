:: Simple Cytoscape batch script for windows/dos
:: (c) Trey Ideker June 21, 2002; Owen Ozier March 06, 2003
::
:: Runs Cytoscape from its jar file with GO data loaded

java -d64 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd -Xss100M -Xmx1550M -cp cytoscape.jar cytoscape.CyMain -p plugins %*


