# makefile for csplugins.common.vectormath
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) -source 1.4 $<

OBJS =	ReadOnlyVectorDataProvider.class \
	ReadOnlyVectorDataAdapter.class \
	VectorDataProvider.class \
	VectorDataAdapter.class \
	DoubleArrayDataProvider.class \
	FloatArrayDataProvider.class \
	ReadOnlyMathVector.class \
	MathVector.class \
	MathVectorFactory.class


default: objs

clean:
	- rm *.class 

## 
##------ objs
## build all out-of-date java source
## 

objs: $(OBJS)
