PKG=cytoscape.data.servers
DATA_DIR=$(CYTOSCAPE_DATA)

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

JC=javac

OBJS = FlattenIntVectors.class \
       BioDataServer.class \
       BioDataServerImpl.class \
       IPBioDataServer.class \
       BioDataServerFactory.class \
       loadBind.class  \
       loadGo.class  \
       loadSynonyms.class  \
       loadGoTerms.class  \
       loadBiologicalProcesses.class  \
       loadMolecularFunctions.class  \
       loadCellularComponents.class  \
       loadGoPathTerminators.class  \
       clearGoPathTerminators.class  \
       biologicalProcess.class  \
       getPathsForTerm.class  \
       canonicalName.class  \
       status.class

#
# --- default 
# objs & rmic:  compile all changed java files; run 'rmic' to generate new rmi
# stubs & skeletons
#

default: objs  rmic

#
# --- objs
# compile all java files newer than the corresponding class files
#

objs:  $(OBJS)

#
# --- rmic: 
# run the RMI compiler, rmic, to generate new rmi stubs & skeletons
#

rmic: BioDataServerImpl_Skel.class

BioDataServerImpl_Skel.class: BioDataServerImpl.class
	rmic -d ../../.. $(PKG).BioDataServerImpl

#
# --- init
# after updating all class files (via objs & rmic targets):
#
#   - kill the current rmiregistry and RMI server
#   - sleep awhile
#   - restart the registry
#   - sleep awhile
# 
#  you must 'make restartYeast|restartHalo' to start the appropriate
#  server; then 'make yeast' for yeast data; 'make halo' for halobacterium data

init:
	- kill -9 `ps -efl |egrep rmiregistry |egrep -v egrep |awk '{print $$4}'`
	- kill -9 `ps -efl |egrep BioDataServerImpl |egrep -v egrep |awk '{print $$4}'`
	sleep 5
	rmiregistry &
	sleep 10

#
# --- restartYeast
#  start the yeast (and default) biodata server.  it's name (known to the rmiregistry)
#  is simply 'biodata'
#
#   - restart the RMI server
# 
#  you must 'make load' to get data into the server

restartYeast:  objs rmic
	java $(PKG).BioDataServerImpl &

#
# --- restartHalo
#  start the halo biodata server.  it's name (known to the rmiregistry)
#  is simply 'biodata'
#
#   - restart the RMI server
# 
#  you must 'make load' to get data into the server

restartHalo:  objs rmic
	java $(PKG).BioDataServerImpl halo &

#
# --- loadYeast
# load the standard yeast data into the RMI server
#

loadYeast:
	java $(PKG).loadSynonyms $(DATA_DIR)/GO/synonyms.xml biodata
#	java $(PKG).loadBind bindingPairs.xml
#	java $(PKG).loadGo geneProducts.xml
	java $(PKG).loadGoTerms $(DATA_DIR)/GO/goTerms.xml
	java $(PKG).loadBiologicalProcesses $(DATA_DIR)/GO/yeastGenesAndBiologicalProcessIDs.txt
	java $(PKG).loadMolecularFunctions $(DATA_DIR)/GO/yeastGenesAndMolecularFunctionIDs.txt
	java $(PKG).loadCellularComponents $(DATA_DIR)/GO/yeastGenesAndCellularComponentIDs.txt
	java $(PKG).loadGoPathTerminators $(DATA_DIR)/GO/goPathTerminators.txt

#
# --- loadHalo
# load the standard halo bacterium data into the RMI server
#

loadHalo:
	java $(PKG).loadSynonyms $(DATA_DIR)/haloSynonyms.xml halo

#
# --- status
# get a descritpion of the data, and some status information (requests, start time)
#

status: status.class
	java $(PKG).status
	java $(PKG).status halo

#
# --- clean
# delete all class files
#

clean:
	- rm *.class

help:
	egrep "^#" makefile | sed "s/^#//"

