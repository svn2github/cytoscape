# makefile for luca/exerciser
#-------------------------------------------------------------------------
# $Revision$   $Date$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	(CLASSPATH=../:$$CLASSPATH; $(JC) $<)

OBJS =  VizChooserExerciser.class
default: objs run

objs: $(OBJS)


clean:
	- rm *.class

run: $(OBJS)
	viz -a data/40k.ratio -a data/40k.lambda 
#	(CLASSPATH=../:$$CLASSPATH; java VizChooserExerciser)
