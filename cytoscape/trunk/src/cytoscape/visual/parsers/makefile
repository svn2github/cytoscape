# makefile for parsers
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) -g $<

OBJS = ArrowParser.class \
       ColorParser.class \
       DoubleParser.class \
       FontParser.class \
       LineTypeParser.class \
       NodeShapeParser.class \
       StringParser.class \
       ValueParser.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 

