# makefile for cytoscape.vizmap package
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac -source 1.4

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS =  DomainInfo.class \
	DomainBoundary.class \
        VizMapper.class \
	NodeVizMapper.class \
        EdgeVizMapper.class \
        VizMapperPropertiesAdapter.class \
        EdgeVizMapperPropertiesAdapter.class \
        NodeVizMapperPropertiesAdapter.class \

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 


