# makefile for cytoscape.filter package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  CombinationFilter.class \
	NegFilter.class \
	EdgeTypeFilter.class \
	NodeTypeFilter.class \
	ExpressionFilter.class \
	StableFilter.class \
	ExpressionOnlyFilter.class \
	TopologyCenteredFilter.class \
	Filter.class \
	TopologyFilter.class \
	InteractionFilter.class \
	AttributeFilter.class \
	TopologyOnePassFilter.class \
	ListFilter.class \
	TrueFilter.class

default: objs subdirectories

subdirectories: dialogsDirectory

objs: $(OBJS)

dialogsDirectory:
	(cd dialogs; make)

clean:
	- rm *.class 
	- (cd dialogs; make clean)
