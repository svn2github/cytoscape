# makefile for cytoscape/undo/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------
JC=javac -source 1.4
PKG=cytoscape.undo.unitTests
TestDataDir=../../testData

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  UndoableGraphHiderTest.class

#
# --- default
# build all out-of-date files and run
#

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

help:
	egrep "^#" makefile | sed "s/^#//"

#
# --- run
# run all tests
#

run: $(OBJS)
	java $(PKG).UndoableGraphHiderTest

