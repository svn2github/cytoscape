# makefile for layouters
#-------------------------------------------------------------------------
# 1.0 : dramage : 2002.1.8
#-------------------------------------------------------------------------

JC=javac -source 1.4

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = GroupWiseLayouter.class \
       GroupingAlgorithm.class \
       GroupWiseNodeGrouper.class \
       DegreeGrouper.class \
       Subgraph.class \
       EmbeddedLayouter.class \
       ReduceEquivalentNodes.class \
       HierarchicalLayoutDialog.class \
       RotateLayoutHelper.class \
       AttributeLayout.class


default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


