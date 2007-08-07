# makefile for cytoscape.plugins.data.synonyms
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------
JC=javac -source 1.4
JI=java 

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  Thesaurus.class


default: objs subDirectories

subDirectories:
	(cd readers; make)

objs: $(OBJS)

clean:
	- rm *.class  */*.class

help:
	egrep "^#" makefile | sed "s/^#//"


