# makefile for csplugins.browsers
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------
D=/home/pshannon/work/csplugins/trial/pshannon/dataCube
CP=$D/cytoscape.jar:$D/visad.jar:/home/pshannon/work
JC=javac -classpath $(CP)
JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = CheckBoxHeader.class \
       DataMatrixTableModel.class \
       DataMatrixBrowser.class \
#       SubMatrixSelectorDialog.class

default: subdirs objs

subdirs:
	(cd actions; make)

#
#------ objs
# compile all out of date source
# 

objs: $(OBJS)

clean:
	- rm *.class 



help:
	egrep "^#" makefile | sed "s/^#//"

