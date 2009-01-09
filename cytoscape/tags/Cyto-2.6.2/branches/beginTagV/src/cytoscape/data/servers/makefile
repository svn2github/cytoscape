PKG=cytoscape.data.servers
DATA_DIR=$(CYTOSCAPE_DATA)/GO

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
# --- restart
# after updating all class files (via objs & rmic targets):
#
#   - kill the current rmiregistry and RMI server
#   - sleep awhile
#   - restart the registry
#   - restart the RMI server
# 
#  you must 'make load' to get data into the server

restart:  objs rmic
	- kill -9 `ps -efl |egrep rmiregistry |egrep -v egrep |awk '{print $$4}'`
	- kill -9 `ps -efl |egrep BioDataServerImpl |egrep -v egrep |awk '{print $$4}'`
	sleep 5
	rmiregistry &
	sleep 10
	java $(PKG).BioDataServerImpl &

#
# --- load
# load the standard data into the RMI server
#

load:
	java $(PKG).loadSynonyms $(DATA_DIR)/synonyms.xml
#	java $(PKG).loadBind bindingPairs.xml
#	java $(PKG).loadGo geneProducts.xml
	java $(PKG).loadGoTerms $(DATA_DIR)/goTerms.xml
	java $(PKG).loadBiologicalProcesses $(DATA_DIR)/yeastGenesAndBiologicalProcessIDs.txt
	java $(PKG).loadMolecularFunctions $(DATA_DIR)/yeastGenesAndMolecularFunctionIDs.txt
	java $(PKG).loadCellularComponents $(DATA_DIR)/yeastGenesAndCellularComponentIDs.txt
	java $(PKG).loadGoPathTerminators $(DATA_DIR)/goPathTerminators.txt

#
# --- status
# get a descritpion of the data, and some status information (requests, start time)
#

status: status.class
	java $(PKG).status

#
# --- clean
# delete all class files
#

clean:
	- rm *.class

help:
	egrep "^#" makefile | sed "s/^#//"

