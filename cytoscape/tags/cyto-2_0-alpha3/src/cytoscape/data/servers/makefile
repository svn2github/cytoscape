# makefile for csplugins.data.servers
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------
PKG=cytoscape.data.servers
JC=javac -source 1.4
JI=java -DCYTOSCAPE_HOME=$(CYTOSCAPE_HOME)

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = BioDataServerInterface.class \
       BioDataServerRmi.class \
       BioDataServer.class \
       loadAnnotation.class \
       loadAnnotationFromFlatFiles.class \
       loadThesaurus.class \
       status.class \
       clear.class \
       getCommonName.class \
       getCanonicalName.class

default: objs rmic

#------ objs
#  compile all java files
# 

objs: $(OBJS)

#------ rmic: 
# run the RMI compiler, rmic, to generate new rmi stubs & skeletons
#

rmic: BioDataServerRmi_Skel.class

BioDataServerRmi_Skel.class: BioDataServerRmi.class
	rmic -d ../../.. $(PKG).BioDataServerRmi

#------ reg
#  try to start a rmi registry.  this will fail if one is already running
#  on the default port
#
reg:
	- rmiregistry& 

#------ start
#  start the data server with name defined by $(CSDS_NAME)
# 

start: objs rmic
	java $(PKG).BioDataServerRmi  $(CSDS_NAME) &

#------ test
#  start the data server with name 'test'
# 

test: objs rmic
	java $(PKG).BioDataServerRmi test &

#------ threads
# what rmiregistry and servers are running?
#
threads:
	ps -efl |egrep rmiregistry |egrep -v egrep 
	ps -efl |egrep BioDataServerRmi | egrep -v egrep 


#------ status
# ask the dataserver  to report on itself
#
status: $(OBJS)
	java $(PKG).status $(CSDS_HOST) $(CSDS_NAME)


#------ clear
# ask the dataserver to clear itself
#
clear: $(OBJS)
	java $(PKG).clear $(CSDS_HOST) $(CSDS_NAME)



#------ killreg
#  kill the rmi registry.  don't do this if you are on a machine that provides
#  an operational biodata server!
#
killreg:
	- kill -9 `ps -efl |egrep rmiregistry |egrep -v egrep |awk '{print $$4}'`

#------ killtest
#  kill the data server which runs with name 'test'
#

killtest:
	- kill -9 `ps -efl |egrep BioDataServerRmi | egrep test | egrep -v egrep |awk '{print $$4}'`

#------ killproduction
#  kill the data server which runs with name biodata
#

killproduction:
	- kill -9 `ps -efl |egrep BioDataServerRmi | egrep $(CSDS_NAME) | egrep -v egrep |awk '{print $$4}'`

#------ clean
#  delete all compiled files in the current directory, and in subdirectories
# 

clean:
	- rm *.class */*.class

#------ common
#  is the rmi server running, and does it return expected results? call 'getCommonName'
#  

common:  $(OBJS)
	java $(PKG).getCommonName localhost yeast "Saccharomyces cerevisiae" YPL248C

help:
	egrep "^#" makefile | sed "s/^#//"

