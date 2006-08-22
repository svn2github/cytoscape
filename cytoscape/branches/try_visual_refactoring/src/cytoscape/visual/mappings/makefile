# makefile for mappings
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) -source 1.4 $<

OBJS = DiscreteMapping.class \
       ContinuousMapping.class \
       PassThroughMapping.class \
       ObjectMapping.class \
       MappingFactory.class \
       InterpolatorFactory.class \
       Interpolator.class \
       FlatInterpolator.class \
       NumberInterpolator.class \
       LinearNumberInterpolator.class \
       LinearNumberToColorInterpolator.class \
       LinearNumberToNumberInterpolator.class \
       BoundaryRangeValues.class

default: objs 

objs: $(OBJS)

clean:
	- rm *.class 

