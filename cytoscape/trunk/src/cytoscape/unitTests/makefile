# makefile for cytoscape/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------

JC=javac -source 1.4
JR=java -ea

PKG=cytoscape.unitTests
TestDataDir=../testData
DataDir=../data

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  CytoscapeConfigTest.class \
        GraphObjAttributesTest.class \
        PluginInfoTest.class \
        PluginLoaderTest.class \
        SelectedSubGraphFactoryTest.class \

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

run: $(OBJS) 0 1 2 3 4
#	$(JR) $(PKG).CytoscapeConfigTest
#	$(JR) $(PKG).GraphObjAttributesTest
#	$(JR) $(PKG).PluginLoaderTest
#	$(JR) $(PKG).PluginInfoTest
#	$(JR) $(PKG).SelectedSubGraphFactoryTest

#
# --- 0 
#  run GraphObjAttributesTest
#

0: GraphObjAttributesTest.class
	$(JR) $(PKG).GraphObjAttributesTest

#
# --- 1
#  run CytoscapeConfigTest
#

1: CytoscapeConfigTest.class
	$(JR) -DCYTOSCAPE_HOME=. $(PKG).CytoscapeConfigTest

#
# --- 2
#  run PluginInfoTest
#

2: PluginInfoTest.class
	$(JR) $(PKG).PluginInfoTest

#
# --- 3 
#  run PluginLoaderTest
#

3: PluginLoaderTest.class
	$(JR) $(PKG).PluginLoaderTest

#
# --- 4
#  run SelecedSubGraphFactoryTest
#

4: SelectedSubGraphFactoryTest.class
	$(JR) $(PKG).SelectedSubGraphFactoryTest

help:
	egrep "^#" makefile | sed "s/^#//"




