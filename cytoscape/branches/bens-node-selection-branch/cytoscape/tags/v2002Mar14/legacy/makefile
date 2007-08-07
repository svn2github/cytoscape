# makefile for cytoscape package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =	ActivePathsFinderBridge.class \
        ActivePathViewer.class \
        CytoscapeConfig.class \
        GraphObjAttributes.class \
        VizAttributes.class \
        VizChooserClient.class \
        CytoscapeVersion.class \
        WindowFactory.class \
        CytoscapeWindow.class \
        BioModulesWindow.class \
        cytoscape.class


default: checkkEnv activePathsDirectory objs dataDirectories \
         dialogsDirectory nativelib layoutDirectory

objs: $(OBJS)

checkkEnv:
	./checkEnvironment

currentDirTest: objs
	(cd unitTests; make)


dataDirectories:
	(cd data; make)
	(cd data/readers; make)
	(cd data/servers; make)

dataTest: data
	(cd data/unitTests; make)

dialogsDirectory:
	(cd dialogs; make)

activePathsDirectory:
	(cd activePathsNew; make)

nativelib: activePathsNew
	make -f libActivePaths.make

#deprecated when code moved from activePaths to activePathsNew
#
#nativelibTest: nativelib
#	(cd activePaths/unitTests; make)

layoutDirectory:
	(cd layout; make)

#test: dataTest currentDirTest nativelibTest 
test: dataTest currentDirTest

clean:
	- rm *.class libActivePaths.so core *.o
	- (cd activePathsNew; make clean)
	- (cd data; make clean)
	- (cd data/unitTests; make clean)
	- (cd data/readers; make clean)
	- (cd data/readers/unitTests; make clean)
	- (cd data/servers; make clean)
	- (cd data/servers/unitTests; make clean)
	- (cd dialogs; make clean)
	- (cd layout; make clean)

version:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape  -v


doc:
	javadoc -d ./htdocs \
                -private \
                -overview ./overview.html \
                -sourcepath .. \
                cytoscape   \
                cytoscape.data \
                cytoscape.data.unitTests \
                cytoscape.data.readers \
                cytoscape.data.readers.unitTests \
                cytoscape.dialogs \
                cytoscape.unitTests \
                cytoscape.layout \
            -windowtitle cytoscape \
    -doctitle "cytoscape"

#
# --- run0
# start the program without loading any data from the command line
#

run0:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape

run1o:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e /common/data/exp/gal/data_gal1-20.pvals \
          -b $(CYTOSCAPE_DATA)/GO

#
# --- run1
# Display the 330 "galFiltered" genes, with tideker's expression data 
# (5936 genes by 20 conditions); use the flat-file version of GO gene 
# annotation.  These data are very similar to those used in the Science
# paper, Ideker et al, April 2001.
# 

run1: 
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -i testData/sample.intr \
          -b $(CYTOSCAPE_DATA)/GO

#
# --- run1a
# like run1, but no GO data and no gene common names
#

run1a: 
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna

#
# --- run1b
# like run1a, but with no expression data
#

run1b: 
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml

#
# --- run1c
# like run1,  adding one (of several possible) active path finding parameters,
# enough to cause the active paths calculation to start automatically
#

run1c: 
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          --APsig  40.5 \
          --APni 10000 \
          --APrs 17

#
# --- run1d
# like run1, but biodata comes from a local RMI server, not flat files
#

run1d:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b rmi://localhost/biodata

#
# --- run1e
# a small (32 gene) interactions dataset, with an RMI biodata server
#

run1e: 
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/tideker0/yeastSmall.intr \
          -b rmi://localhost/biodata

#
# --- run1f
# like run1, but with local RMI biodata server
#

run1f: 
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b rmi://localhost/biodata

#
# --- run2
#  Demonstrates the use of node attributes which are not expression data
#  'fooB' is a derived measure of the connectivity-related significance of
#  each node in the graph:  delete this node, and x number of edges drop out.
#

run2:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -n $(CYTOSCAPE_DATA)/galFiltered.fooB \
          -b $(CYTOSCAPE_DATA)/GO
#
# --- run2a
#  like run2, but without file-based biodata server

run2a:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -n $(CYTOSCAPE_DATA)/galFiltered.fooB 

#
# --- run3 
# Demonstrates use of a minimal "interactions" file from which a full graph
# is created.  The normal galactose expression data used, along with 
# the GO data
#

run3:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/tideker0/yeastSmall.intr \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b $(CYTOSCAPE_DATA)/GO
#
# --- run3a
# like 3, but w/o expression and GO data
#

run3a:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/tideker0/yeastSmall.intr \
#
# --- run3b
#  Demonstrates two things:
#    1. how to supply a minimal "interactions" file from which a full graph
#       is created
#    2. provides two small files specifying different edge attributes, to
#       test and illustrate the programs ability to control the appearance
#       of graph edges as a function of edge attribute values
#

run3b:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/tideker0/yeastSmall.intr \
          -j $(CYTOSCAPE_DATA)/tideker0/yeastSmall.edgeAttr.0 \
          -j $(CYTOSCAPE_DATA)/tideker0/yeastSmall.edgeAttr.1

#
# --- run4
#   nitin baliga's halobacterium data (exceedingly preliminary...)
#

run4:
	java -Xmx512MB -Djava.library.path=. cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/nitin/network.intr 
#
# --- run5
#  tideker's 7000 node data set, with 28MB expression data 
#  matrix (does not yet work!)
#     
run5:
	java -Xmx1024MB -Djava.library.path=. cytoscape.cytoscape \
          -g /users/tideker/cytoscape$(CYTOSCAPE_DATA)/network7000.gml \
          -e /users/tideker/cytoscape$(CYTOSCAPE_DATA)/Nov10_1.pvals \
          -b $(CYTOSCAPE_DATA)/GO
#
# --- run5
#  tideker's 7000 node data set, with the 'usual' expression data
#     
run5a:
	java -Xmx1024MB -Djava.library.path=. cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/tidekerBig/network7000.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b $(CYTOSCAPE_DATA)/GO


#
# --- run6
#  sample data for the biomodules window
#     
DIR=$(CYTOSCAPE_DATA)/import/biomodules/current
run6:
	java -Xmx1024MB -Djava.library.path=. cytoscape.cytoscape \
          -i $(DIR)/44.intr \
          -n $(DIR)/44.attr \
#          -b $(CYTOSCAPE_DATA)/GO

#
# --- run6a
#  attraction data layout for the biomodules window
#     
DIR=$(CYTOSCAPE_DATA)/import/biomodules/current
run6a:
	java -Xmx1024MB -Djava.library.path=. cytoscape.cytoscape \
          -i $(DIR)/ygl075c.intr \
          -j $(DIR)/ygl075c.force \
          -n $(DIR)/44.attr \
#          -b $(CYTOSCAPE_DATA)/GO


help:
	egrep "^#" makefile | sed "s/^#//"

