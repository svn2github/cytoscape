# makefile for cytoscape/datatypes/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------

JC=javac

PKG=csplugins.isb.pshannon.dataMatrix.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  DataMatrixTest.class \
        DataMatrixFileReaderTest.class \
        DataMatrixReaderFactoryTest.class \
#5~        SubMatrixClusterTest.class

help:
	egrep "^#" makefile | sed "s/^#//"



objs: $(OBJS)


default: objs run

clean:
	- rm *.class

#------ all
#  run all the tests
#
all: $(OBJS) 0 1 2 3 

#------ 0
#  build and run the DataMatrixTest
#
0: DataMatrixTest.class
	java $(PKG).DataMatrixTest


#------ 1
#  build and run the DataMatrixLensTest
#
1: DataMatrixLensTest.class
	java $(PKG).DataMatrixLensTest


#------ 2
#  build and run the DataMatrixFileReaderTest
#
2: DataMatrixFileReaderTest.class
	java $(PKG).DataMatrixFileReaderTest


#------ 3
#  build and run the DataMatrixReaderFactoryTest
#

3: DataMatrixReaderFactoryTest.class
	java $(PKG).DataMatrixReaderFactoryTest


#------ 4
#  build and run the SubMatrixClusterTest
#

4: SubMatrixClusterTest.class
	java $(PKG).SubMatrixClusterTest


#------ 5
#  build and run the SubMatrixClusterReaderTest
#

5: SubMatrixClusterReaderTest.class
	java $(PKG).SubMatrixClusterReaderTest

#------ 6
#  test the python prototype, 'MatrixCombiner'
#

6:
	jython MatrixCombinerTest.py


