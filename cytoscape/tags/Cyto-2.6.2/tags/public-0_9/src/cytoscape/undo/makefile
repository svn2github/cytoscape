# makefile for cytoscape.view package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  UndoItem.class \
        UndoManager.class \
        CytoscapeUndoManager.class \
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


