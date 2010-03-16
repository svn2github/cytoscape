# makefile for cytoscape/vizmap/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------
JC=javac -source 1.4
PKG=cytoscape.vizmap.unitTests
TestDataDir=../../testData

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  EdgeVizMapperTest.class \
        NodeVizMapperTest.class \
        EdgeVizMapperPropertiesAdapterTest.class \
        NodeVizMapperPropertiesAdapterTest.class \

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
	java $(PKG).EdgeVizMapperTest
	java $(PKG).NodeVizMapperTest
	java $(PKG).EdgeVizMapperPropertiesAdapterTest
	java $(PKG).NodeVizMapperPropertiesAdapterTest


#
# --- 0
# EdgeVizMapperTest
#

0: EdgeVizMapperTest.class
	java $(PKG).EdgeVizMapperTest

#
# --- 1
# NodeVizMapperTest
#

1: NodeVizMapperTest.class
	java $(PKG).NodeVizMapperTest


#
# --- 2
# EdgeVizMapperPropertiesAdapaterTest
#

2: EdgeVizMapperPropertiesAdapterTest.class
	java $(PKG).EdgeVizMapperPropertiesAdapterTest

#
# --- 3
# NodeVizMapperPropertiesAdapterTest
#

3: NodeVizMapperPropertiesAdapterTest.class
	java $(PKG).NodeVizMapperPropertiesAdapterTest

