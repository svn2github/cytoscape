# makefile for cytoscape.visual
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) $<

OBJS = NodeAppearanceCalculator.class \
       EdgeAppearanceCalculator.class \
       GlobalAppearanceCalculator.class \
       VisualStyle.class \
       CalculatorCatalog.class \
       VisualMappingManager.class \
       NodeAppearance.class \
       EdgeAppearance.class \
       GlobalAppearance.class \
       CalculatorIO.class \
       OldStyleCalculatorIO.class \
       PropertiesTester.class

default: calculatorDir mappingDir uiDir parserDir testDir objs

objs: $(OBJS)

calculatorDir:
	(cd calculators; make)

dialogDir:
	(cd dialogs; make)

mappingDir:
	(cd mappings; make)

uiDir:
	(cd ui; make)

parserDir:
	(cd parsers; make)

testDir:
	(cd tests; make)

clean:
	- rm *.class
	- (cd calculators; make clean)
	- (cd mappings; make clean)
	- (cd ui; make clean)
	- (cd parsers; make clean)
	- (cd tests; make clean)
