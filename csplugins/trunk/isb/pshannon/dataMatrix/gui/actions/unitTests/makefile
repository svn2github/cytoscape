# makefile for cytoscape/datatypes/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------
JC=javac

PKG=csplugins.isb.pshannon.dataMatrix.gui.actions.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  CorrelationFinderTest.class

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

#------ run
#  run all the tests
#
run: $(OBJS) 0

#------ 0
#  build and run the CorrelationFinderTest
#
0: CorrelationFinderTest.class
	java $(PKG).CorrelationFinderTest


help:
	egrep "^#" makefile | sed "s/^#//"

