# makefile for cytoscape.util package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  Misc.class \
        MutableColor.class \
        MutableString.class \
        MutableInt.class \
        MutableBool.class \
        Exec.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


