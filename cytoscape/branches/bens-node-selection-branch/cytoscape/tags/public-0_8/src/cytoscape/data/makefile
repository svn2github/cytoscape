# makefile for cytoscape.datatypes package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  ExpressionData.class \
        Interaction.class \
        mRNAMeasurement.class \
        BindingPair.class \
        GeneProduct.class \
        GeneSynonym.class \
        GoTerm.class 

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


