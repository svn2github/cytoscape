# makefile for mappings
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) -source 1.4 $<

OBJS = DiscreteMapping.class \
       ContinuousMapping.class \
       PassThroughMapping.class \
       ObjectMapping.class \
       MappingFactory.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 

