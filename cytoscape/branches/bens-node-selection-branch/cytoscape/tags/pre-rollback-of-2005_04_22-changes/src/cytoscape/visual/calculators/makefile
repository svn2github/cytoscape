# makefile for calculators
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) -source 1.4 $<

OBJS = GenericEdgeArrowCalculator.class \
       GenericEdgeColorCalculator.class \
       GenericEdgeLabelCalculator.class \
       GenericEdgeLineTypeCalculator.class \
       GenericEdgeToolTipCalculator.class \
       GenericNodeColorCalculator.class \
       GenericNodeLabelCalculator.class \
       GenericNodeLineTypeCalculator.class \
       GenericNodeShapeCalculator.class \
       GenericNodeSizeCalculator.class \
       GenericNodeToolTipCalculator.class \
	GenericNodeFontFaceCalculator.class \
	GenericEdgeFontFaceCalculator.class \
	GenericNodeFontSizeCalculator.class \
	GenericEdgeFontSizeCalculator.class \
       NodeCalculator.class \
       EdgeCalculator.class \
       AbstractCalculator.class \
       Calculator.class \
	CalculatorFactory.class


default: objs 

objs: $(OBJS)

clean:
	- rm *.class 

