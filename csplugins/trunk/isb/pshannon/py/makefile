# makefile for cytoscape/datatypes/unitTests
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

OBJS = SPyConsole.class \
       ThreadedSPyConsole.class \
       WebReader.class \
       PyConsolePlugin.class \
       PyConsoleDemo.class

#-------------------------------------------------------------------------
help:
	egrep "^#" makefile | sed "s/^#//"


#
#------ objs
# build just the classes from source -- no jar
# 


objs: $(OBJS)



PLUGINS=/users/pshannon/cy2
EXTRA=/users/pshannon/jars/pyConsoleExtra
JAR=/users/pshannon/cy2/csplugins/isb/pshannon/py/PythonConsolePlugin.jar

#
#------ jar
# make a single, standalone jar for this plugin
# 


jar: $(OBJS)
	(cd $(PLUGINS); jar cvf $(JAR) `find csplugins/isb/pshannon/py -name "*.class" -print`)
	(cd $(PLUGINS); jar uvf $(JAR) `find csplugins/isb/pshannon/cyInterface -name "*.class" -print`)
	(cd $(EXTRA);   jar uvf $(JAR) `find . -name "*.class" -print`)
	(cd $(EXTRA);   jar uvf $(JAR) `find . -name "*.properties" -print`)



default: objs

objs: $(OBJS)


#
#------ demo
# run a threaded SPyConsole in a JFrame
# 

demo:  $(OBJS)
	java csplugins.isb.pshannon.py.PyConsoleDemo


jar: $(OBJS)

clean:
	- rm *.class



