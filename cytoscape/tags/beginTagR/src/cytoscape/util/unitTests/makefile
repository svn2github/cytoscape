# makefile for cytoscape/datatypes/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------

JC=javac
PKG=cytoscape.util.unitTests
TestDataDir=../../testData

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  MiscTest.class \

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

run: $(OBJS)
	java $(PKG).MiscTest


0: MiscTest.class
	java $(PKG).MiscTest


