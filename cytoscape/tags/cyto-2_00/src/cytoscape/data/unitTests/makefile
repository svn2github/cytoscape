# makefile for cytoscape/datatypes/unitTests
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------

JC=javac
PKG=cytoscape.data.unitTests
TestDataDir=../../testData

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  InteractionTest.class \
        mRNAMeasurementTest.class \
        ExpressionDataTest.class

default: objs run

objs: $(OBJS)


clean:
	- rm *.class

#------ run
#  run all the tests
#
run: $(OBJS) 1a 1b 1c 2 3

#------ 1a
#  build and run ExpressionDataTest on gal1.22x5.rRNA
#
1a: ExpressionDataTest.class
	java $(PKG).ExpressionDataTest $(TestDataDir)/gal1.22x5.mRNA

#------ 1b
#  build and run ExpressionDataTest on gal1.274x20.mRNA
#
1b: ExpressionDataTest.class
	java $(PKG).ExpressionDataTest $(TestDataDir)/gal1.274x20.mRNA

#------ 1c
#  build and run ExpressionDataTest on oneCondition.mRNA
#
1c: ExpressionDataTest.class
	java $(PKG).ExpressionDataTest $(TestDataDir)/oneCondition.mRNA


#------ 2
#  build and run InteractionTest
#
2: InteractionTest.class
	java $(PKG).InteractionTest


#------ 3
#  mRNAMeasurementTest
#
3: mRNAMeasurementTest.class
	java $(PKG).mRNAMeasurementTest



help:
	egrep "^#" makefile | sed "s/^#//"

