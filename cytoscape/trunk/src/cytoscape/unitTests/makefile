# makefile for cytoscape/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------

JC=javac
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

run: $(OBJS)
	java $(PKG).CytoscapeConfigTest
	java $(PKG).GraphObjAttributesTest
	java $(PKG).PluginLoaderTest
	java $(PKG).PluginInfoTest
	java $(PKG).SelectedSubGraphFactoryTest

0: GraphObjAttributesTest.class
	java $(PKG).GraphObjAttributesTest

1: CytoscapeConfigTest.class
	java -DCYTOSCAPE_HOME=. $(PKG).CytoscapeConfigTest

2: PluginInfoTest.class
	java $(PKG).PluginInfoTest

3: PluginLoaderTest.class
	java $(PKG).PluginLoaderTest

4: SelectedSubGraphFactoryTest.class
	java $(PKG).SelectedSubGraphFactoryTest



