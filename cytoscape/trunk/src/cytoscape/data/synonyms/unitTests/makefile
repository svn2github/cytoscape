# makefile for the csplugin.data.annotation unitTests
#--------------------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------------------------
JC=javac -source 1.4
JI=java
PKG=csplugins.data.synonyms.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = ThesaurusTest.class

default: $(OBJS) run

run: 0 

clean:
	- rm *.class


#------ 0
#  run the ThesaurusTest
# 

0: ThesaurusTest.class
	java $(PKG).ThesaurusTest



help:
	egrep "^#" makefile | sed "s/^#//"


