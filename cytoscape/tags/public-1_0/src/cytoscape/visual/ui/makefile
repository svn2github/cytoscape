# makefile for UI
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = 	VizMapTab.class \
	VizMapUI.class \
	VizMapSizeTab.class \
	VizMapAttrTab.class \
	VizMapFontTab.class \
	FontChooser.class \
	FontRenderer.class \
	MiscDialog.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 

