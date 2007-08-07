# makefile for cytoscape.filter.dialogs package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------


JC=javac -source 1.4

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  InteractionDialog.class \
	BoolPanel.class \
	MainFilterDialog.class \
	CenterDialog.class \
	CombinationDialog.class \
	AttributeDialog.class \
	AttributeSelection.class \
	EdgeTypeDialog.class \
	MutableBoolean.class \
	EdgeTypeDialogIndep.class \
	NodeTypeDialog.class \
	RadioPanel.class \
	ExpressionDialog.class \
	TopologyDialog.class \
	FilterDialog.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 
