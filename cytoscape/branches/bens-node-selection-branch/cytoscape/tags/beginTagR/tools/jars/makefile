# jar.make:  make jars needed for the cytoscape program
# type 'make help' to get a description of the targets

AUXJAR_UNPACKED_DIR=$(PWD)/aux
JARFILE=$(PWD)/cytoscape.jar
AUXJARFILE=$(PWD)/aux.jar
YFILESDIR=/net/compbio/src/distributions/yfiles/jarUnpacked

default: help

#  
#  --- full
#  make a jar file which has everything needed to be executable
#  via the command "java -jar cytoscape.jar"
#
#  for this to work, you must have unjar your aux.jar so that all of
#  its contents are found in $(PWD)/aux.  
#  in seattle, we have all of these auxiliary files; in boston, it may
#  be simpler to just use the aux.jar file we made for you:  
#     mkdir aux
#     cd aux    (you are now in cytoscape/util/aux)
#     jar xvf someDirectory/aux.jar
#  

full:
	(cd ../../..; jar cvmf cytoscape/tools/jars/jar.manifest $(JARFILE))
	(cd ../../..; jar uvf $(JARFILE) `find cytoscape -name "*.class" -print`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find gnu/ -name "*.class" -print`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find gnu/ -name "*.properties" -print`)
	(cd $(YFILESDIR); jar uvf $(JARFILE) `find y -name "*.class"`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find org -name "*.class"`)


#  
#  --- auxjar
#  make a jar file which has everything auxillary needed to build and
#  run cytoscape, but not including yfiles or our own source code
#  

auxjar:
	(cd ~/jars; jar cvf $(AUXJARFILE) `find gnu/ -name "*.class" -print`)
	(cd ~/jars; jar uvf $(AUXJARFILE) `find gnu/ -name "*.properties" -print`)
	(cd ~/jars/jdomUnpacked; jar uvf $(AUXJARFILE) `find org -name "*.class"`)
	(cd ~/jars/xercesUnpacked; jar uvf $(AUXJARFILE) `find org -name "*.class"`)


help:
	egrep "^#  " makefile | sed "s/^#//"



