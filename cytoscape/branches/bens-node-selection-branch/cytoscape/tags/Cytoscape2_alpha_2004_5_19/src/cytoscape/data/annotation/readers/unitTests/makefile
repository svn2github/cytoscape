# makefile for cytoscape.data.annotation.readers.unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4
JI=java -Xmx1G
PKG=cytoscape.data.annotation.readers.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = OntologyXmlReaderTest.class \
       AnnotationXmlReaderTest.class \
       OntologyFlatFileReaderTest.class \
       AnnotationFlatFileReaderTest.class

default: objs run

#------ objs
#  compile all java files
# 

objs: $(OBJS)

#------ run
#  run all the unitTests
# 

run: objs 1 2 3 4
	

#------ clean
#  delete all compiled files in the current directory, and in subdirectories
# 

clean:
	- rm *.class 

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


#------ 3
#  run OntologyFlatFileTest
# 

3: OntologyFlatFileReaderTest.class
	$(JI) $(PKG).OntologyFlatFileReaderTest

#------ 4
#  run AnnotationFlatFileReaderTest
# 

4: AnnotationFlatFileReaderTest.class
	$(JI) $(PKG).AnnotationFlatFileReaderTest



help:
	egrep "^#" makefile | sed "s/^#//"

