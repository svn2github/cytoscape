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

default: objs  subDirectories

subDirectories:
	(cd annotation; make);
	(cd synonyms; make);
	(cd readers; make);
	(cd servers; make);

OBJS =  ExpressionData.class \
        Interaction.class \
        mRNAMeasurement.class \
        GraphProps.class \

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


