PKG=cytoscape.data.servers.unitTests
RMIHOST=localhost
DATA_DIR=$(CYTOSCAPE_DATA)/biodata/current

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

JC=javac

OBJS = BioDataServerFactoryTest.class \
       IPBioDataServerTest.class \
       BioDataServerProcessTest.class \
       BioDataServerSynonymsTest.class \
       BioDataServerMolFunctionsTest.class \
       BioDataServerCellularComponentsTest.class \

default: objs run

objs: $(OBJS)

run:  $(OBJS)
	java $(PKG).BioDataServerFactoryTest $(DATA_DIR) rmi://$(RMIHOST)/biodata
	java $(PKG).BioDataServerSynonymsTest rmi://$(RMIHOST)/biodata
	java $(PKG).BioDataServerMolFunctionsTest rmi://$(RMIHOST)/biodata
	java $(PKG).BioDataServerProcessTest rmi://$(RMIHOST)/biodata
	java $(PKG).BioDataServerCellularComponentsTest rmi://$(RMIHOST)/biodata
	java $(PKG).IPBioDataServerTest  $(DATA_DIR)


0: BioDataServerProcessTest.class
	java $(PKG).BioDataServerProcessTest rmi://$(RMIHOST)/biodata

1: IPBioDataServerTest.class
	java $(PKG).IPBioDataServerTest  $(DATA_DIR)

2: BioDataServerFactoryTest.class
	java $(PKG).BioDataServerFactoryTest $(DATA_DIR) rmi://$(RMIHOST)/biodata


clean:
	- rm *.class
