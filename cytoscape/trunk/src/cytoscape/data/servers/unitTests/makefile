# makefile for the csplugin.data.servers unitTests
#--------------------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------------------------
JC=javac -source 1.4
JI=java -Xmx1G
PKG=cytoscape.data.servers.unitTests

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = BioDataServerTest.class

default: objs run

objs: $(OBJS)

run: 0

clean:
	- rm *.class

#------ test
#  start the data server with name 'test'
# 

#test: 0


#------ 0
#  run the BioDataServerTest
# 

0: BioDataServerTest.class
	
	$(JI) $(PKG).BioDataServerTest 

#------ reg
#  try to start a rmi registry.  this will fail if one is already running
#  on the default port
#
reg:
	- rmiregistry& 

#------ killtest
#  kill the rmi data server which runs with name 'test'
#

killtest:
	- kill -9 `ps -efl |egrep BioDataServerRmi | egrep test | egrep -v egrep |awk '{print $$4}'`


#------ starttest
#  start the rmi version of the data server with name 'test'
# 

starttest: 
	$(JI) csplugins.data.servers.BioDataServerRmi test &
	#sleep 10
	# $(JI) csplugins.data.servers.loadAnnotation rmi://localhost/test ../../kegg/haloMetabolicPathway.xml
	

help:
	egrep "^#" makefile | sed "s/^#//"

