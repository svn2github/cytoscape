# makefile for cytoscape/datatypes/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------

JC=javac
PKG=cytoscape.data.readers.unitTests
TestDataDir=../../../testData
DataDir=../../data

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = InteractionsReaderTest.class \
       GMLReaderTest.class \
       TextFileReaderTest.class \
       TextJarReaderTest.class \


#------ default
#  compile all out-of-date java files, run all tests
#

default: objs 0 1 2

#------ objs
#  compile all out-of-date java files
#
objs: $(OBJS)

#------ clean
#  delete all compiled files in the current directory, and in subdirectories
#
clean:
	- rm *.class 

#------ 0
#  build and run InteractionsReaderTest
#
0: InteractionsReaderTest.class
	java $(PKG).InteractionsReaderTest
	

#------ 1
#  build and run GMLReaderTest
#
1: GMLReaderTest.class
	java $(PKG).GMLReaderTest $(TestDataDir)


#------ 2
#  build and run TextFileReaderTest
#
2: TextFileReaderTest.class
	java $(PKG).TextFileReaderTest
	

#------ 3
#  build and run TextJarReaderTest
#
3: TextJarReaderTest.class
	(cd ../../../..; export D=cytoscape/data/readers;  jar cvf $$D/unitTests/readerTest.jar \
          $D/unitTests/TextJarReaderTest.class $D/TextJarReader.class)
	jar uf readerTest.jar jabberwocky.txt
	(CLASSPATH=$$CLASSPATH:readerTest.jar; java $(PKG).TextJarReaderTest)
	



help:
	egrep "^#" makefile | sed "s/^#//"



