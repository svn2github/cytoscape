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
#ColorNodesFromFileDialog.class
OBJS = ColorPopupButton.class \
        ColorScaleTable.class \
        ExpressionDataPopupTable.class \
        NewSlider.class \
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
        RotateSelectionDialog.class \
        EdgePopupMenu.class \
	EdgeAttributesPopupDetails.class \
        ShrinkExpandGraph.class \
        ShrinkExpandGraphUI.class \
        EdgeControlDialog.class


default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


