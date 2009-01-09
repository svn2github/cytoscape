# makefile for csplugins.data.readers
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

OBJS = TextFileReader.class \
       TextJarReader.class \
       FileReadingAbstractions.class \
       GMLReader.class \
       GMLWriter.class \
       InteractionsReader.class \
       GraphReader.class

default: objs 


#------ objs
#  compile all out-of-date java files
#
objs: $(OBJS)

#------ clean
#  delete all compiled files in the current directory, and in subdirectories
#
clean:
	- rm *.class 

help:
	egrep "^#" makefile | sed "s/^#//"


