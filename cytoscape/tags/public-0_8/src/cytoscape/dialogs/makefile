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
        VisualPropertiesDialog.class \
        LabelTextPanel.class \
        GeneralColorDialogListener.class \
        IntegerEntryField.class \
        IconPopupButton.class \
        MiscDialog.class \
        MiscGB.class \
        GridBagGroup.class \
        EdgeTextPanel.class \
	NewStringPopupDialog.class \
        JointIntegerEntry.class \



default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


