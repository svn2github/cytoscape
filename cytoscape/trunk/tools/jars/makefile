# jar.make:  make jars needed for the cytoscape program
# type 'make help' to get a description of the targets

#AUXJAR_UNPACKED_DIR=$(PWD)/aux
AUXJAR_UNPACKED_DIR=/common/aux
JARFILE=$(PWD)/cytoscape.jar
AUXJARFILE=$(PWD)/aux.jar
#YFILESDIR=/net/compbio/src/distributions/yfiles/jarUnpacked
YFILESDIR=/common/yfiles1.4-unpacked

default: help


#
#  --- full
#  make cytoscape, make csplugins, make jar, make obf
#

full:
	(cd ../../../cytoscape; make)
	(cd ../../../csplugins; make)
	make jar
	make obf


#  
#  --- jar
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

jar:
	(cd ../../..; jar cvmf cytoscape/tools/jars/jar.manifest $(JARFILE))
	(cd ../../..; jar uvf $(JARFILE) `find cytoscape/* -name "*.class" -print`)
	(cd ../../..; jar uvf $(JARFILE) `find cytoscape/* -name "*.jpg" -print`)
	(cd ../../..; jar uvf $(JARFILE) `find csplugins/* -name "*.class" -print`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find gnu/* -name "*.class" -print`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find gnu/* -name "*.properties" -print`)
	(cd $(YFILESDIR); jar uvf $(JARFILE) `find y/* -name "*.class"`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find org/* -name "*.class"`)


#  
#  --- auxjar
#  make a jar file which has everything auxillary needed to build and
#  run cytoscape, but not including yfiles or our own source code
#  

auxjar:
	(cd ~/jars; jar cvf $(AUXJARFILE) `find gnu/* -name "*.class" -print`)
	(cd ~/jars; jar uvf $(AUXJARFILE) `find gnu/* -name "*.properties" -print`)
	(cd ~/jars/jdomUnpacked; jar uvf $(AUXJARFILE) `find org/* -name "*.class"`)
	(cd ~/jars/xercesUnpacked; jar uvf $(AUXJARFILE) `find org/* -name "*.class"`)



#
#  --- obf
#  obfuscates cytoscape.jar to cytoscape-obf.jar using the yFiles hacked
#  version of RetroGuard
#

obf:
	java -cp retroguard.jar RetroGuard cytoscape.jar cytoscape-obf.jar script.rgs


help:
	egrep "^#  " makefile | sed "s/^#//"



