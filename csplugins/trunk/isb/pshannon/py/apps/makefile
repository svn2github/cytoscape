# makefile for csplugins.isb.pshannon.dataMatrix.apps
#-------------------------------------------------------------------------
# $Revision: 1.1 $
# $Date: 2004/05/28 17:59:24 $
# $Author: pshannon $
#-------------------------------------------------------------------------

help:
	egrep "^#" makefile | sed "s/^#//"



#
#------ cy2
# run cy2 from the programmer's CLASSPATH
# 

cy2: 
	java -Xmx1G cytoscape.CyMain -p project \
          -b http://db.systemsbiology.net/cytoscape/annotation/yeast/manifest \
          -s "Saccharomyces cerevisiae"


#
#------ jar
# run cy2 from a jar 
# 

jar: 
	java -Xmx1G -jar /users/pshannon/cy2jars/cytoscape.jar 


