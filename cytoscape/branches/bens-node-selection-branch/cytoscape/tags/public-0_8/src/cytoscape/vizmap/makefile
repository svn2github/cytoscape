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

OBJS =  AttributeMapper.class \
	AttributeMapperCategories.class \
	AttributeMapperPropertiesAdapter.class \
	BoundaryRangeValues.class \
	ContinuousMapper.class \
	DiscreteMapper.class \
	EdgeArrowColor.class \
	Interpolator.class \
	LinearNumberInterpolator.class \
	LinearNumberToColorInterpolator.class \
	NumberInterpolator.class \
	ValueMapper.class \
	VizMapperCategories.class \

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 
