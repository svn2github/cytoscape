# makefile for csplugins.trial.pshannon.dataCube
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------
D=/home/pshannon/work/csplugins/trial/pshannon/dataCube
CP=$D/cytoscape.jar:$D/visad.jar:/home/pshannon/work
JC=javac -classpath $(CP)
JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

default: objs 

OBJS =  CorrelationFinder.class

objs: $(OBJS)

clean:
	- rm *.class 


