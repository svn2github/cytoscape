# makefile for csplugins.browsers
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------
JI=java 
JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = TabbedNodeBrowser.class \
       NodeBrowserTableModel.class \
       NodeBrowsingMode.class \
    

default: objs

#
#------ objs
# compile all out of date source
# 

objs: $(OBJS)

clean:
	- rm *.class 


help:
	egrep "^#" makefile | sed "s/^#//"

