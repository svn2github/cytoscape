package org.cytoscape.io.internal.util.vizmap;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class CalculatorConverterFactoryTest {

	private CalculatorConverterFactory ccf;

	@Before
	public void setUp() throws Exception {
		ccf = new CalculatorConverterFactory();
	}

	@Test
	public void testConvertersNotNullForNullKey() throws Exception {
		assertNotNull(ccf.getConverters(null));
	}

	public void testConvertersNotNullForEmptyKey() throws Exception {
		assertNotNull(ccf.getConverters(""));
	}

	@Test
	public void testConvertersEmptyForInvalidKey() throws Exception {
		// missing style name
		assertTrue(ccf.getConverters("edgeAppearanceCalculator.defaultEdgeColor").isEmpty());
		// it is just a mapping controller--should not create converters
		assertTrue(ccf.getConverters("edgeLabelCalculator.Solid-Edge Label-Passthrough Mapper.mapping.controller")
				.isEmpty());
		// the first token should be "nodeAppearanceCalculator", because it is a node property
		assertTrue(ccf.getConverters("edgeAppearanceCalculator.My Style.defaultNodeLabel").isEmpty());
		// the first token should be "edgeAppearanceCalculator", because it is an edge property
		assertTrue(ccf.getConverters("nodeAppearanceCalculator.My Style.defaultEdgeLabel").isEmpty());
		// "calculator" is invalid
		assertTrue(ccf.getConverters("calculator.InteractionDB.defaultNodeLabel").isEmpty());
		// completely invalid
		assertTrue(ccf.getConverters("asdfg").isEmpty());
	}

	@Test
	public void testGetOneConverterForRegularKeys() throws Exception {
		assertEquals(1, ccf.getConverters("globalAppearanceCalculator.Universe.defaultBackgroundColor").size());
		assertEquals(1, ccf.getConverters("nodeAppearanceCalculator.Universe.defaultNodeLabel").size());
		assertEquals(1, ccf.getConverters("edgeAppearanceCalculator.My Style.edgeTargetArrowShapeCalculator").size());
	}

	@Test
	public void testGetTwoConvertersForSomeOldKeys() throws Exception {
		testLineTypeConverters("edgeAppearanceCalculator.Sample1.defaultEdgeLineType");
		testLineTypeConverters("edgeAppearanceCalculator.Sample1.edgeLineTypeCalculator");
		testLineTypeConverters("nodeAppearanceCalculator.default.defaultNodeLineType");
		testLineTypeConverters("nodeAppearanceCalculator.default.nodeLineTypeCalculator");

		testArrowConverters("edgeAppearanceCalculator.Sample 1.defaultEdgeSourceArrow");
		testArrowConverters("edgeAppearanceCalculator.Sample 1.defaultEdgeTargetArrow");
		testArrowConverters("edgeAppearanceCalculator.default.edgeSourceArrowCalculator");
		testArrowConverters("edgeAppearanceCalculator.default.edgeTargetArrowCalculator");
	}

	// Private methods -----------

	private void testLineTypeConverters(String key) {
		Set<CalculatorConverter> convs = ccf.getConverters(key);
		assertEquals(2, convs.size());

		boolean hasLineStyleKey = false;
		boolean hasLineWidthKey = false;

		for (CalculatorConverter c : convs) {
			hasLineStyleKey |= c.getVisualPropertyId().matches("(?i)[a-z]*LineStyle");
			hasLineWidthKey |= c.getVisualPropertyId().matches("(?i)[a-z]*LineWidth");
		}

		assertTrue("Old LineType key generates two converters", hasLineStyleKey == true && hasLineWidthKey == true);
	}

	private void testArrowConverters(String key) {
		Set<CalculatorConverter> convs = ccf.getConverters(key);
		assertEquals(2, convs.size());

		boolean hasArrowShapeKey = false;
		boolean hasArrowColorKey = false;

		for (CalculatorConverter c : convs) {
			hasArrowShapeKey |= c.getVisualPropertyId().matches("(?i)[a-z]*ArrowShape");
			hasArrowColorKey |= c.getVisualPropertyId().matches("(?i)[a-z]*ArrowColor");
		}

		assertTrue("Old Arrow key generates two converters", hasArrowShapeKey == true && hasArrowColorKey == true);
	}
}
