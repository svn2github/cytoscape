# makefile for csplugins.setVisualProperties
#-------------------------------------------------------------------------
# $Revision$
# $Date$
# $Author$
#-------------------------------------------------------------------------

JC=javac

.SUFFIXES: .java .class
.java.class: 
	$(JC) -g $<

OBJS = TestNewMappingsUI.class \
       NodeAppearanceCalculator.class \
       ValueDisplayer.class \
       PopupIconChooser.class \
       EdgeAppearanceCalculator.class \
       CalculatorCatalog.class \
       VisualMappingManager.class \
       NodeAppearance.class \
       EdgeAppearance.class \
       CalculatorIO.class \
       OldStyleCalculatorIO.class \
       PropertiesTester.class

default: calculatorDir mappingDir uiDir parserDir objs

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

clean:
	- rm *.class
	- (cd dialogs; make clean)
	- (cd calculators; make clean)
	- (cd mappings; make clean)
	- (cd ui; make clean)
