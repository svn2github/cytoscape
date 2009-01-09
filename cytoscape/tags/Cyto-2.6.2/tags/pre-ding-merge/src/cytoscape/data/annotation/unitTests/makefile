# makefile for the csplugin.data.annotation unitTests
#--------------------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------------------------
JC=javac -source 1.4
JI=java -Xmx1G
PKG=cytoscape.data.annotation.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = Utils.class \
       AnnotationDescriptionTest.class \
       AnnotationTest.class \
       OntologyTermTest.class \
       OntologyTest.class \
       AnnotationAndOntologyFullTest.class

default: $(OBJS) run

run: 0 1 2 3 4

clean:
	- rm *.class


#------ 0
#  run the AnnotationDescriptionTest
# 

0: AnnotationDescriptionTest.class
	$(JI) $(PKG).AnnotationDescriptionTest

#------ 1
#  run the AnnotationTest
# 

1: AnnotationTest.class
	$(JI) $(PKG).AnnotationTest

#------ 2
#  run the OntologyTermTest
# 

2: OntologyTermTest.class
	$(JI) $(PKG).OntologyTermTest


#------ 3
#  run the OntologyTest
# 

3: OntologyTest.class Utils.class
	$(JI) $(PKG).OntologyTest

#------ 4
#  run a full test of a large annotation:  kegg pathways for halobacterium
# 

4: AnnotationAndOntologyFullTest.class 
	$(JI) $(PKG).AnnotationAndOntologyFullTest



help:
	egrep "^#" makefile | sed "s/^#//"


