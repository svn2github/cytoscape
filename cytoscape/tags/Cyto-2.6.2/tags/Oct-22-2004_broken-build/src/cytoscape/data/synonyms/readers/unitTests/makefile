# makefile for csplugins.data.synonyms.readers.unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4
JI=java #-DCYTOSCAPE_HOME=$(CYTOSCAPE_HOME)
PKG=cytoscape.data.synonyms.readers.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = ThesaurusFlatFileReaderTest.class

default: objs run

#------ objs
#  compile all java files
# 

objs: $(OBJS)

#------ run
#  run all the unitTests
# 

run: objs 0
	

#------ clean
#  delete all compiled files in the current directory, and in subdirectories
# 

clean:
	- rm *.class 


#------ 0
#  run just the ThesaurusFlatFileReaderTest
# 

0: ThesaurusFlatFileReaderTest.class
	$(JI) $(PKG).ThesaurusFlatFileReaderTest


help:
	egrep "^#" makefile | sed "s/^#//"

