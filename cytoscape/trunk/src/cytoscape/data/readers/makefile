# makefile for cytoscape.data package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  BindingPairXmlReader.class \
        GeneAndGoTermTextReader.class \
        GeneProductXmlReader.class \
        GeneSynonymXmlReader.class \
        GMLReader.class \
	GMLWriter.class \
        GoPathTerminatorTextReader.class \
        GoTermXmlReader.class \
        InteractionsReader.class \
        TextFileReader.class \
        FileReadingAbstractions.class \
        GraphReader.class \

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


