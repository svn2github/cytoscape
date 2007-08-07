# makefile for annotate
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = ColorPopupButton.class \
        ColorScaleTable.class \
        ExpressionDataPopupTable.class \
        GoDataServerPopupTable.class \
        GoIDAttributesPopupTable.class \
        NewSlider.class \
        NodeAttributesPopupTable.class \
        PopupTextArea.class \
#        VizChooser.class \


default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


