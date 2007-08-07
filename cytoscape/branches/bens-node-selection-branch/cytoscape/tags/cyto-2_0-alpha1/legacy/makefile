# makefile for cytoscape package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4
JAVA=java -D"CYTOSCAPE_HOME=$(CYTOSCAPE_HOME)"

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
	AbstractPlugin.class \
        cytoscape.class \
	SplashScreen.class
#        VizChooserClient.class \


default: checkkEnv objs subdirectories

subdirectories: dataDirectories browserDirectory \
         vizmapDirectory dialogsDirectory \
         layoutDirectory utilDirectory \
	 viewDirectory undoDirectory filtersDirectory \
	 visualDirectory jarLoaderDirectory

objs: $(OBJS)

checkkEnv:
	./checkEnvironment

currentDirTest: objs
	(cd unitTests; make)

browserDirectory: 
	(cd browsers; make)


dataDirectories:
	(cd data; make)
	(cd data/readers; make)
	(cd data/servers; make)

dataTest: data
	(cd data/unitTests; make)

visualDirectory:
	(cd visual; make)

vizmapDirectory: 
	(cd vizmap; make)

dialogsDirectory:
	(cd dialogs; make)

layoutDirectory:
	(cd layout; make)

utilDirectory:
	(cd util; make)

viewDirectory:
	(cd view; make)

undoDirectory:
	(cd undo; make)

filtersDirectory: 
	(cd filters; make)

jarLoaderDirectory: 
	(cd jarLoader; make)

test: objs
	(cd unitTests; make)
	(cd vizmap/unitTests; make)
	(cd util/unitTests; make)
	(cd data/unitTests; make)
	(cd data/annotation/unitTests; make)
	(cd data/annotation/readers/unitTests; make)
	(cd data/readers/unitTests; make)
	(cd data/servers/unitTests; make)
	(cd data/synonyms/unitTests; make)
	(cd data/synonyms/readers/unitTests; make)
	(cd visual/unitTests; make)

clean:
	- rm *.class core
	- (cd unitTests; make clean)
	- (cd undo; make clean)
	- (cd data; make clean)
	- (cd data/unitTests; make clean)
	- (cd data/readers; make clean)
	- (cd data/readers/unitTests; make clean)
	- (cd data/servers; make clean)
	- (cd data/servers/unitTests; make clean)
	- (cd dialogs; make clean)
	- (cd filters; make clean)
	- (cd layout; make clean)
	- (cd util; make clean)
	- (cd util/unitTests; make clean)
	- (cd vizmap; make clean)
	- (cd vizmap/unitTests; make clean)
	- (cd visual; make clean)

#
# --- version
# report the current version of cytoscape
#

version:
	java cytoscape.cytoscape  -v


#
# --- doc
# generate javadoc
#

doc:
	javadoc \
                -source 1.4 \
                -d ./htdocs\
                -overview ./overview.html \
                -sourcepath .. \
                cytoscape.data \
                cytoscape   \
                cytoscape.data.unitTests \
                cytoscape.data.readers \
                cytoscape.data.readers.unitTests \
                cytoscape.dialogs \
                cytoscape.unitTests \
                cytoscape.layout \
                cytoscape.vizmap \
                cytoscape.vizmap.unitTests \
                cytoscape.browsers \
                cytoscape.filters \
                cytoscape.undo \
                cytoscape.util \
                cytoscape.view \
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

help:
	egrep "^#" makefile | sed "s/^#//"

