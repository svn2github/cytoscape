# makefile for cytoscape.data.util package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  canonicalize.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 

test:
	java cytoscape.data.util.canonicalize rmi://localhost/biodata sample.list 


