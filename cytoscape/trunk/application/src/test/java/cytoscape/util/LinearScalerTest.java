package cytoscape.util;


import junit.framework.*;


public class LinearScalerTest extends TestCase {
	public void test() {
		final Scaler linearScaler = new LinearScaler();
		final double[] unscaledValues = new double[] { 3.0, 10.2, 5.4, 3.0, 7.7 };
		final double MIN = 0.0;
		final double MAX = 1.0;
		final double[] scaledValues = linearScaler.scale(unscaledValues, MIN, MAX);
		boolean foundMin = false;
		boolean foundMax = false;
		for (final double d : scaledValues) {
			if (MathUtil.almostEqual(MIN, d))
				foundMin = true;
			if (MathUtil.almostEqual(MAX, d))
				foundMax = true;
			assertTrue(d >= MIN && d <= MAX);
		}
		assertTrue(foundMin);
		assertTrue(foundMax);
	}
}