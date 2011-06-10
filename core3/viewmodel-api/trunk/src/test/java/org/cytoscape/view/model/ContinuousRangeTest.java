package org.cytoscape.view.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.cytoscape.view.model.ContinuousRange;
import org.junit.Test;


public class ContinuousRangeTest {
	
	@Test
	public void testContinuousNumberRange() {
		final ContinuousRange<Double> range1 = new ContinuousRange<Double>(Double.class, 0.0d, 100.0d, true, true);
		assertTrue(range1.validate(3.0));
		assertTrue(range1.validate(0.0));
		assertTrue(range1.validate(100.0));
		assertFalse(range1.validate(200.0));
		assertFalse(range1.validate(100.001));
		assertFalse(range1.validate(-1.2));
		
		final ContinuousRange<Double> range2 = new ContinuousRange<Double>(Double.class, 0.0d, 100.0d, false, false);
		assertFalse(range2.validate(0.0));
		assertFalse(range2.validate(100.0));
		
		final ContinuousRange<Double> range3 = new ContinuousRange<Double>(Double.class, 0.0d, 100.0d, false, true);
		assertFalse(range3.validate(0.0));
		assertTrue(range3.validate(100.0));
		
		final ContinuousRange<Double> range4 = new ContinuousRange<Double>(Double.class, 0.0d, 100.0d, true, false);
		assertTrue(range4.validate(0.0));
		assertFalse(range4.validate(100.0));
		
		final ContinuousRange<Color> colorRange = new ContinuousRange<Color>(Color.class, Color.BLACK, Color.yellow, false, false);
		// Need to define its own validator
		assertTrue(colorRange.validate(new Color(10, 100, 10)));
	}

}
