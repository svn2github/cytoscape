# makefile for csplugins.data.synonyms.readers
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4
JI=java -DCYTOSCAPE_HOME=$(CYTOSCAPE_HOME)

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = ThesaurusFlatFileReader.class 

default: objs 

#------ objs
#  compile all java files
# 

objs: $(OBJS)

#------ clean
#  delete all compiled files in the current directory, and in subdirectories
# 

clean:
	- rm *.class  */*.class

help:
	egrep "^#" makefile | sed "s/^#//"

