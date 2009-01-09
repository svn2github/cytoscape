# makefile for cytoscape.jarLoader
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = JarLoaderUI.class \
       JarLoaderAction.class \
       JarClassLoader.class \
       JarPluginLoaderAction.class \
       JarLoaderCommandLineParser.class \
       JarPluginDirectoryAction.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 

help:
	egrep "^#" makefile | sed "s/^#//"

