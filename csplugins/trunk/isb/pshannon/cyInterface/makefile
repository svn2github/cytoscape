# 
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------
JC=javac -classpath $(CP)
JC=javac


.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = CyInterface.class \
       Cytoscape2Impl.class

#-------------------------------------------------------------------------
help:
	egrep "^#" makefile | sed "s/^#//"


#
#------ objs
# build just the classes from source -- no jar
# 


objs: $(OBJS)


clean:
	- rm *.class



