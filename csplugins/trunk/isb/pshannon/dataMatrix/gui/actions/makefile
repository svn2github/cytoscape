# makefile for csplugins.browsers
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

OBJS = SelectionPlotter.class \
       CreateNewMatrixFromSelection.class \
       DataMatrixMovieDialog.class \
       RunMovie.class \
       ExportMatrix.class \
       CorrelationFinder.class \
       CorrelationFinderDialog.class \
       FindCorrelations.class \
       GetSelectionsFromNetwork.class \
       PyConsoleLauncher.class



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

