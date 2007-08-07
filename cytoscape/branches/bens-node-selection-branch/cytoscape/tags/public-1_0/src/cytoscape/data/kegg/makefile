# makefile for the csplugin.data.kegg
#--------------------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#--------------------------------------------------------------------------------------

#------ 0
#  validate the xml schemas
# 

0: 
	msv ../annotation/ontology.xsd
	msv ../annotation/annotation.xsd

#------ 1
#  validate the kegg ontology 
# 

1: 
	msv ../annotation/ontology.xsd metabolicPathwayOntology.xml

#------ 2
#  validate the halobacterium Kegg annotation
# 

2: 
	msv ../annotation/annotation.xsd haloMetabolicPathway.xml

#------ halo
#  load halo kegg annotations
# 

halo:  $(OBJS)
	java csplugins.data.servers.loadAnnotation rmi://localhost/newbiodata haloMetabolicPathway.xml

#------ haloseq
#  load halo upstream sequences
# 


SEQDIR=$(HOME)/data/import/halo/promoterSequences
haloseq:
	java csplugins.data.servers.loadUpstreamSequences rmi://localhost/newbiodata \
           $(SEQDIR)/chromosome/forward500/all.xml
	java csplugins.data.servers.loadUpstreamSequences rmi://localhost/newbiodata \
           $(SEQDIR)/chromosome/reverse500/all.xml
	java csplugins.data.servers.loadUpstreamSequences rmi://localhost/newbiodata \
           $(SEQDIR)/pnrc100/forward500/all.xml
	java csplugins.data.servers.loadUpstreamSequences rmi://localhost/newbiodata \
           $(SEQDIR)/pnrc100/reverse500/all.xml
	java csplugins.data.servers.loadUpstreamSequences rmi://localhost/newbiodata \
           $(SEQDIR)/pnrc200/forward500/all.xml
	java csplugins.data.servers.loadUpstreamSequences rmi://localhost/newbiodata \
           $(SEQDIR)/pnrc200/reverse500/all.xml




help:
	egrep "^#" makefile | sed "s/^#//"


