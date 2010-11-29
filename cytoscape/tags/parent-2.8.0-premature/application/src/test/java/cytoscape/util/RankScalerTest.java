package cytoscape.util;


import junit.framework.*;


public class RankScalerTest extends TestCase {
	public void test1() {
		final Scaler rankScaler = new RankScaler();
		final double[] unscaledValues = new double[] { 3.0, 10.2, 5.4, 3.0, 7.7 };
		final double MIN = 0.0;
		final double MAX = 1.0;
		final double[] scaledValues = rankScaler.scale(unscaledValues, MIN, MAX);
		boolean foundMin = false;
		boolean foundMax = false;
		for (final double d : scaledValues) {
			if (MathUtil.almostEqual(MIN, d))
				foundMin = true;
			if (MathUtil.almostEqual(MAX, d))
				foundMax = true;
			assertTrue(d >= MIN && d <= MAX);
		}
		assertFalse(foundMin);
		assertFalse(foundMax);
	}

	public void test2() {
		final Scaler rankScaler = new RankScaler();
		final double[] unscaledValues = new double[] { 3.0, 3.0, 1.4, 7.7 };
		final double MIN = 0.0;
		final double MAX = 1.0;
		final double[] scaledValues = rankScaler.scale(unscaledValues, MIN, MAX);
		assertEquals(0.5, scaledValues[0], 1e-10);
		assertEquals(0.5, scaledValues[1], 1e-10);
		assertEquals(0.125, scaledValues[2], 1e-10);
		assertEquals(0.875, scaledValues[3], 1e-10);
	}
}