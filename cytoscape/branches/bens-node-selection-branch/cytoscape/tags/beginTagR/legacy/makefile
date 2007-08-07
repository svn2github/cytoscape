# makefile for cytoscape package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4
JAVA=java

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = CytoscapeConfig.class \
        GraphObjAttributes.class \
        CytoscapeVersion.class \
        PluginInfo.class \
        PluginLoader.class \
        SelectedSubGraphFactory.class \
        CytoscapeWindow.class \
        cytoscape.class
#        VizChooserClient.class \


default: checkkEnv objs dataDirectories \
         vizmapDirectory dialogsDirectory layoutDirectory

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

vizmapDirectory: 
	(cd vizmap; make)

dialogsDirectory:
	(cd dialogs; make)

layoutDirectory:
	(cd layout; make)

test: objs
	(cd unitTests; make)
	(cd vizmap/unitTests; make)
	(cd data/unitTests; make)

clean:
	- rm *.class core
	- (cd unitTests; make clean)
	- (cd data; make clean)
	- (cd data/unitTests; make clean)
	- (cd data/readers; make clean)
	- (cd data/readers/unitTests; make clean)
	- (cd data/servers; make clean)
	- (cd data/servers/unitTests; make clean)
	- (cd dialogs; make clean)
	- (cd layout; make clean)
	- (cd vizmap; make clean)
	- (cd vizmap/unitTests; make clean)

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


#NATIVE_LIB=-Djava.library.path=plugins/activePaths
#JAVA=java -Xmx1024MB $(NATIVE_LIB) 

#
# --- run0
# start the program without loading any data from the command line
#

run0:
	$(JAVA) cytoscape.cytoscape

#
# --- run1
# Display the 330 "galFiltered" genes, with tideker's expression data 
# (5936 genes by 20 conditions); use the flat-file version of GO gene 
# annotation.  These data are very similar to those used in the Science
# paper, Ideker et al, April 2001.
# 

run1: 
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b $(CYTOSCAPE_DATA)/GO

#
# --- run1a
# like run1, but no GO data and no gene common names
#

run1a: 
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna

#
# --- run1b
# like run1a, but with no expression data
#

run1b: 
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml

#
# --- run1c
# like run1,  adding one (of several possible) active path finding parameters,
# enough to cause the active paths calculation to start automatically
#

run1c: 
	$(JAVA) cytoscape.cytoscape \
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
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b rmi://localhost/biodata

#
# --- run1e
# a small (32 gene) interactions dataset, with a file-based data server
#

run1e: 
	$(JAVA) cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/yeastSmall.intr \
          -b $(CYTOSCAPE_DATA)/GO

#
# --- run1f
# like run1e, but with rmi server
#

run1f: 
	$(JAVA) cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/yeastSmall.intr \
          -b rmi://localhost/biodata

#
# --- run1f0
# like run1e, but no GO data
#

run1f0: 
	$(JAVA) cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/yeastSmall.intr

##
## --- run1f1
## like run1f, but uses a node attributes file, simple.bar
##

#run1f1: 
#	$(JAVA) cytoscape.cytoscape \
#          -i $(CYTOSCAPE_DATA)/yeastSmall.intr \
#          -n $(HOME)/cytoscapeData/pluginSamples/simple.bar

#
# --- run1g
# like run1, but with local RMI biodata server
#

run1g: 
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.intr \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b rmi://localhost/biodata

#
# --- run2
#  Demonstrates the use of node attributes which are not expression data
#  'fooB' is a derived measure of the connectivity-related significance of
#  each node in the graph:  delete this node, and x number of edges drop out.
#

run2:
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -n $(CYTOSCAPE_DATA)/galFiltered.fooB \
          -b $(CYTOSCAPE_DATA)/GO
#
# --- run2a
#  like run2, but without file-based biodata server

run2a:
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/galFiltered.gml \
          -n $(CYTOSCAPE_DATA)/galFiltered.fooB 

#
# --- run3 
# Demonstrates use of a minimal "interactions" file from which a full graph
# is created.  The normal galactose expression data used, along with 
# the GO data
#

run3:
	$(JAVA) cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/yeastSmall.intr \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b $(CYTOSCAPE_DATA)/GO
#
# --- run3a
# like 3, but w/o expression and GO data
#

run3a:
	$(JAVA) cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/yeastSmall.intr \
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
	$(JAVA) cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/yeastSmall.intr \
          -j $(CYTOSCAPE_DATA)/yeastSmall.edgeAttr.0 \
          -j $(CYTOSCAPE_DATA)/yeastSmall.edgeAttr.1

#
# --- run4
#   nitin baliga's halobacterium data (exceedingly preliminary...)
#

run4:
	$(JAVA) cytoscape.cytoscape \
          -i $(CYTOSCAPE_DATA)/nitin/network.intr 
#
# --- run5
#  tideker's 7000 node data set, with 28MB expression data 
#  matrix (does not yet work!)
#     
run5:
	$(JAVA) cytoscape.cytoscape \
          -g /users/tideker/cytoscape$(CYTOSCAPE_DATA)/network7000.gml \
          -e /users/tideker/cytoscape$(CYTOSCAPE_DATA)/Nov10_1.pvals \
          -b $(CYTOSCAPE_DATA)/GO
#
# --- run5
#  tideker's 7000 node data set, with the 'usual' expression data
#     
run5a:
	$(JAVA) cytoscape.cytoscape \
          -g $(CYTOSCAPE_DATA)/tidekerBig/network7000.gml \
          -e $(CYTOSCAPE_DATA)/gal.5936x20.mrna \
          -b $(CYTOSCAPE_DATA)/GO


#
# --- run6
#  sample data for the biomodules window
#     
DIR=$(CYTOSCAPE_DATA)/import/biomodules/current
run6:
	$(JAVA) cytoscape.cytoscape \
          -i $(DIR)/44.intr \
          -n $(DIR)/44.attr \
#          -b $(CYTOSCAPE_DATA)/GO

#
# --- run6a
#  attraction data layout for the biomodules window
#     
DIR=$(CYTOSCAPE_DATA)/import/biomodules/current
run6a:
	$(JAVA) cytoscape.cytoscape \
          -i $(DIR)/ygl075c.intr \
          -j $(DIR)/ygl075c.force \
          -n $(DIR)/44.attr \
#          -b $(CYTOSCAPE_DATA)/GO


help:
	egrep "^#" makefile | sed "s/^#//"

