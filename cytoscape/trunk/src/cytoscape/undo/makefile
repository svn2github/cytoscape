# makefile for cytoscape.undo package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  UndoItem.class \
        UndoManager.class \
        CytoscapeUndoManager.class \
	EmptyUndoManager.class \
        RealizerUndoItem.class \
        NodeCreationUndoItem.class \
        EdgeCreationUndoItem.class \
        NodeRemovalUndoItem.class \
        EdgeRemovalUndoItem.class \
        UndoItemList.class \
        UndoableGraphHider.class \
        NodeHiddenUndoItem.class NodeShownUndoItem.class \
        EdgeHiddenUndoItem.class EdgeShownUndoItem.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


