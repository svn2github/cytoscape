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

run: $(OBJS)
	java $(PKG).InteractionTest
	java $(PKG).mRNAMeasurementTest
	java $(PKG).ExpressionDataTest  $(TestDataDir)/gal1.22x5.mRNA
	java $(PKG).ExpressionDataTest  $(TestDataDir)/gal1.274x20.mRNA
	java $(PKG).ExpressionDataTest  $(TestDataDir)/oneCondition.mRNA


1: ExpressionDataTest.class
	java $(PKG).ExpressionDataTest $(TestDataDir)/gal1.22x5.mRNA
	java $(PKG).ExpressionDataTest $(TestDataDir)/gal1.274x20.mRNA
	java $(PKG).ExpressionDataTest $(TestDataDir)/oneCondition.mRNA


