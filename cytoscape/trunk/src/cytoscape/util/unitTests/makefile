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
        MinMaxTest.class \
        ExecTest.class

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

run: $(OBJS)
	java $(PKG).MiscTest
	java $(PKG).MinMaxTest
	java $(PKG).ExecTest


0: MiscTest.class
	java $(PKG).MiscTest

1: MinMaxTest.class
	java $(PKG).MinMaxTest

2: ExecTest.class
	java $(PKG).ExecTest


