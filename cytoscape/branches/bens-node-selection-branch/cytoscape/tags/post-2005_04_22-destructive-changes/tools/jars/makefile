# makefile for tools/jars
# type 'make help' to get a description of the targets
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

#AUXJAR_UNPACKED_DIR=$(PWD)/cyto-aux
AUXJAR_UNPACKED_DIR=/common/aux20030130
JARFILE=$(PWD)/cytoscape.jar
EXTRAJARFILE=$(PWD)/cytoscape-extra.jar
AUXJARFILE=$(PWD)/cyto-aux.jar
YFILESDIR=/common/yfiles2.0.1-unpacked
VISADDIR=/common/visad-unpacked

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
#  for this to work, you must have unjar your cyto-aux.jar so that all of
#  its contents are found in $(PWD)/cyto-aux.  
#  in seattle, we have all of these cyto-auxiliary files; in boston, it may
#  be simpler to just use the cyto-aux.jar file we made for you:  
#     mkdir cyto-aux
#     cd cyto-aux    (you are now in cytoscape/util/cyto-aux)
#     jar xvf someDirectory/cyto-aux.jar
#  

jar:
	(cd ../../..; jar cvmf cytoscape/tools/jars/jar.manifest $(JARFILE))
	(cd ../../..; jar uvf $(JARFILE) `find cytoscape/* -name "*.class" -print`)
	(cd ../../..; jar uvf $(JARFILE) `find cytoscape/* -name "*.jpg" -print`)
	(cd ../../..; jar uvf $(JARFILE) `find cytoscape/* -name "*.gif" -print`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find gnu/* -name "*.class" -print`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find gnu/* -name "*.properties" -print`)
	(cd $(YFILESDIR); jar uvf $(JARFILE) `find y/* -name "*.class"`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find org/* -name "*.class"`)
	(cd $(AUXJAR_UNPACKED_DIR); jar uvf $(JARFILE) `find javax/* -name "*.class"`)

#	(cd ../../..; jar uvf $(JARFILE) `find csplugins/activeModules -name "*.class" -print`)
#	(cd ../../..; jar uvf $(JARFILE) `find csplugins/vectormath -name "*.class" -print`)
#	(cd $(VISADDIR); jar uvf $(JARFILE) `find visad/* -name "*.class"`)
#	(cd ../../..; jar uvf $(JARFILE) `find csplugins/vectormath -name "*.class" -print`)
#	(cd ../../..; jar uvf $(JARFILE) `find csplugins/* -name "*.class" -print`)
#	(cd ../../..; jar uvf $(JARFILE) `find csplugins/expressionData -name "*.class" -print`)
#	(cd ../../..; jar uvf $(JARFILE) `find csplugins/filtersPlugin -name "*.class" -print`)
#	(cd ../../..; jar uvf $(JARFILE) `find csplugins/activeModules -name "*.class" -print`)


#  
#  --- cyto-auxjar
#  make a jar file which has everything auxillary needed to build and
#  run cytoscape, but not including yfiles or our own source code
#  

cyto-auxjar:
	(cd ~/jars; jar cvf $(AUXJARFILE) `find gnu/* -name "*.class" -print`)
	(cd ~/jars; jar uvf $(AUXJARFILE) `find gnu/* -name "*.properties" -print`)
	(cd ~/jars/jdomUnpacked; jar uvf $(AUXJARFILE) `find org/* -name "*.class"`)
	(cd ~/jars/xercesUnpacked; jar uvf $(AUXJARFILE) `find org/* -name "*.class"`)



#
#  --- obf
#  obfuscates cytoscape.jar to cytoscape-obf.jar using the yFiles hacked
#  version of RetroGuard
#
#	java -cp retroguard.jar RetroGuard cytoscape.jar cytoscape-obf.jar script.rgs cytoscape-obf-1_1.log

obf:
	java -cp retroguard.jar RetroGuard cytoscape.jar cytoscape-obf.jar script.rgs cytoscape-obf.log

help:
	egrep "^#  " makefile | sed "s/^#//"

#################################################################################
# plugins

activeModules:
#	make jar
	cp $(JARFILE) $(EXTRAJARFILE) 
	(cd ../../..; jar uvf $(EXTRAJARFILE) `find csplugins/activeModules -name "*.class" -print`)
	cat script.rgs cytoscape-obf-1_1.log > usable_obf_table-1_1.log
	java -cp retroguard.jar RetroGuard $(EXTRAJARFILE) cytoscape-extra-obf.jar usable_obf_table-1_1.log cytoscape-obf-1_1-extra.log
	(cd ../../..; find csplugins/activeModules -name "*.class" -print > cytoscape/tools/jars/activeModsExtract.txt)
	java -cp retroguard.jar RGpatch cytoscape-extra-obf.jar activeModulesPlugin.jar cytoscape-obf-1_1-extra.log activeModsExtract.txt

activeModulesUnobf:
#	make jar
	(cd ../../..; jar cvf $(EXTRAJARFILE) `find csplugins/activeModules -name "*.class" -print`)
	mv $(EXTRAJARFILE) activeModulesPluginUnobf.jar

owenResearch:
#	make jar
	cp $(JARFILE) $(EXTRAJARFILE) 
	(cd ../../..; jar uvf $(EXTRAJARFILE) `find csplugins/owenResearch -name "*.class" -print`)
	cat script.rgs cytoscape-obf-1_1.log > usable_obf_table-1_1.log
	java -cp retroguard.jar RetroGuard $(EXTRAJARFILE) cytoscape-extra-obf.jar usable_obf_table-1_1.log cytoscape-obf-1_1-extra.log
	(cd ../../..; find csplugins/owenResearch -name "*.class" -print > cytoscape/tools/jars/owenResearchExtract.txt)
	java -cp retroguard.jar RGpatch cytoscape-extra-obf.jar owenResearchPlugin.jar cytoscape-obf-1_1-extra.log owenResearchExtract.txt

owenResearchUnobf:
#	make jar
	(cd ../../..; jar cvf $(EXTRAJARFILE) `find csplugins/owenResearch -name "*.class" -print`)
	mv $(EXTRAJARFILE) owenResearchPluginUnobf.jar


# example from the RetroGuard website http://www.retrologic.com/retroguard-docs.html#patchutil
#
# build original jar, saving log.
#	java RetroGuard jtool-unobf-1.0.jar jtool-1.0.jar jtool-1.0.rgs jtool-1.0.log
#
# build full upgrade jar
#	java RetroGuard jtool-unobf-1.1.jar jtool-1.1.jar jtool-1.0.log jtool-1.1.log 
#
# extract patch
#	java RGpatch jtool-1.1.jar jtool-patch-1.0-1.1.jar jtool-1.1.log jtool-patch-1.0-1.1.txt
#
# run


