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

OBJS =  AttributeMapperPropertiesAdapterTest.class \
	AttributeMapperTest.class \
	BoundaryRangeValuesTest.class \
	ContinuousMapperTest.class \
	DiscreteMapperTest.class \
	LinearNumberToColorInterpolatorTest.class \
	VizMapperCategoriesTest.class

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
	java $(PKG).AttributeMapperPropertiesAdapterTest
	java $(PKG).AttributeMapperTest
	java $(PKG).BoundaryRangeValuesTest
	java $(PKG).ContinuousMapperTest
	java $(PKG).DiscreteMapperTest
	java $(PKG).LinearNumberToColorInterpolatorTest
	java $(PKG).VizMapperCategoriesTest

#
# --- visualTest
# starts cytoscape with a small sample interaction file. A single node
# property for each node is specified by the "node.props" file, while
# the mapping of this property to node fill color is specified by the
# "cytoscape.props" file in this directory. If all works right, the colors
# should be as follows:
#	Gene0: yellow	Gene4: light blue
#	Gene1: red	Gene5: dark blue
#	Gene2: pink	Gene6: cyan
#	Gene3: black	Gene7: grey
# note that Node7 has no associated value and thus gets the default color.
# The node borders should all be red (specified in default cytoscape.props).
#
# Edges: the edges connected to Gene3 are all pp edges and should be colored
# blue; the other edges are pd interactions and should be colored red.
# The pd interactions also should have arrows (at Gene1, Gene5, and Gene6),
# but the VizMapperCategories doesn't parse arrows yet.

visualTest: $(OBJS)
	java cytoscape.cytoscape -i graph.intr -n node.props

