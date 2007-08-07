# makefile for csplugins.data.go
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------
PKG=csplugins.data.servers
default: help



#------ 0
#  validate the xml schemas
# 

0: 
	msv ../annotation/ontology.xsd
	msv ../annotation/annotation.xsd

#------ 1
#  validate the go ontology
# 

1: 
	msv ../annotation/ontology.xsd goOntologies.xml

#------ 2
#  validate the 3 GO yeast ontologies
# 

2: 
	msv ../annotation/annotation.xsd yeastBioProcess.xml
	msv ../annotation/annotation.xsd yeastCellularComponent.xml
	msv ../annotation/annotation.xsd yeastMolFunc.xml


help:
	egrep "^#" makefile | sed "s/^#//"



#------ status
# ask newbiodataserver to report on itself
#
status: $(OBJS)
	java $(PKG).status



#------ prostasome
#  load the 3 latest human prostasome protein annotations
#
PC=prostasomes119Component.xml
PF=prostasomes119Function.xml
PP=prostasomes119Process.xml
prostasome:
	msv ../annotation/annotation.xsd $(PC)
	msv ../annotation/annotation.xsd $(PF)
	msv ../annotation/annotation.xsd $(PP)
	java $(PKG).loadAnnotation rmi://localhost/newbiodata $(PC)
	java $(PKG).loadAnnotation rmi://localhost/newbiodata $(PF)
	java $(PKG).loadAnnotation rmi://localhost/newbiodata $(PP)


#------ yeast
#  load all yeast annotation: biological processes, molecular functions, cellular components
#

yeast: yeastBioProc yeastMolFunc yeastCellularComponents

#------ yeastBioProc
#  load biological processes annotation
#

yeastBioProc:  
	java $(PKG).loadAnnotation rmi://localhost/newbiodata yeastBioProcess.xml

#------ yeastMolfunc
#  load molecular function annotation
#

yeastMolFunc:
	java $(PKG).loadAnnotation rmi://localhost/newbiodata yeastMolFunc.xml

#------ yeastCellularComponents
#  load cellular component annotation
#
yeastCellularComponents:
	java $(PKG).loadAnnotation rmi://localhost/newbiodata yeastCellularComponent.xml


#------ d16s3098
#  human diabetes: annotations for d16s3098 genes
#
d16s3098:
	java $(PKG).loadAnnotation rmi://localhost/newbiodata d16s3098.xml


#------ run
#  test the yeast annotation with a the galFiltered interaction set
#
run:
	java cytoscape.cytoscape -i $(HOME)/data/yeast/galFiltered.sif



