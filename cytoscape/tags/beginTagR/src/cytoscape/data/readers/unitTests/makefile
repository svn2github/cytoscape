# makefile for cytoscape/datatypes/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------

JC=javac
PKG=cytoscape.data.readers.unitTests
TestDataDir=../../../testData
DataDir=../../data

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = InteractionsReaderTest.class \
       GMLReaderTest.class \

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

run: $(OBJS)
	java $(PKG).InteractionsReaderTest $(TestDataDir)
	java $(PKG).GMLReaderTest  $(TestDataDir)


