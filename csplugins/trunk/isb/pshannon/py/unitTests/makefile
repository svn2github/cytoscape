# makefile for cytoscape/datatypes/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------
JC=javac -classpath $(CP)
JC=javac

PKG=csplugins.isb.pshannon.py.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  WebReaderTest.class

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

#------ run
#  run all the tests
#
run: $(OBJS) 0

#------ 0
#  build and run the WebReaderTest
#
0: WebReaderTest.class
	java $(PKG).WebReaderTest


help:
	egrep "^#" makefile | sed "s/^#//"

