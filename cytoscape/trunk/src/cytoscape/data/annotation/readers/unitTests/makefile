# makefile for csplugins.data.readers.unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4
JI=java #-DCYTOSCAPE_HOME=$(CYTOSCAPE_HOME)
PKG=csplugins.data.annotation.readers.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = OntologyXmlReaderTest.class \
       AnnotationXmlReaderTest.class

default: objs run

#------ objs
#  compile all java files
# 

objs: $(OBJS)

#------ run
#  run all the unitTests
# 

run: objs 1 2
	

#------ clean
#  delete all compiled files in the current directory, and in subdirectories
# 

clean:
	- rm *.class 


#------ 0
#  run just the AnnotationReaderTest
# 

0: AnnotationReaderTest.class
	$(JI) $(PKG).AnnotationReaderTest

#------ 1
#  run just the OntologyXmlReaderTest
# 

1: OntologyXmlReaderTest.class
	$(JI) $(PKG).OntologyXmlReaderTest

#------ 2
#  run just the AnnoationXmlReaderTest
# 

2: AnnotationXmlReaderTest.class
	$(JI) $(PKG).AnnotationXmlReaderTest



help:
	egrep "^#" makefile | sed "s/^#//"

