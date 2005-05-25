# makefile for tests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = TestNewMappingsUI.class

default: objs

objs: $(OBJS)

clean:
	- rm *.class
