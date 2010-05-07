package cytoscape.util;


import junit.framework.*;


public class ProbabilityScalerTest extends TestCase {
	public void testNoRanking() {
		final float[] distinctValues = getDistinctValues();
		final float[] unscaledValues1 = (float[])distinctValues.clone();

		final StringBuilder errorMessage = new StringBuilder();
		final float[] scaledValues1 = ProbabilityScaler.scale(distinctValues, ScalingMethod.NONE,
						 		      errorMessage);

		for (int i = 0; i < unscaledValues1.length; ++i)
			assertEquals(unscaledValues1[i], scaledValues1[i]);

		final float[] valuesWithDups = getDistinctValues();
		final float[] unscaledValues2 = (float[])valuesWithDups.clone();

		final float[] scaledValues2 = ProbabilityScaler.scale(valuesWithDups, ScalingMethod.NONE,
						 		      errorMessage);

		for (int i = 0; i < unscaledValues2.length; ++i)
			assertEquals(unscaledValues2[i], scaledValues2[i]);
	}

	public void testLinearUpper() {
		final float[] distinctValues = getDistinctValues();

		final StringBuilder errorMessage = new StringBuilder();
		final float [] scaledValues1 = ProbabilityScaler.scale(distinctValues, ScalingMethod.LINEAR_UPPER,
								       errorMessage);
		final float[] expectedValues1 = new float[] { 0.5999999f, 0.6449999f, 0.74999994f, 0.9f };
		for (int i = 0; i < expectedValues1.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues1[i], scaledValues1[i]));

		final float[] valuesWithDups = getValuesWithDups();
		final float [] scaledValues2 = ProbabilityScaler.scale(valuesWithDups, ScalingMethod.LINEAR_UPPER,
								       errorMessage);
		final float[] expectedValues2 = new float[] { 0.5625f, 0.61875f, 0.61875f, 0.61875f, 0.75f, 0.9375f, 0.9375f };
		for (int i = 0; i < expectedValues2.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues2[i], scaledValues2[i]));
	}

	public void testLinearLower() {
		final float[] distinctValues = getDistinctValues();

		final StringBuilder errorMessage = new StringBuilder();
		final float [] scaledValues1 = ProbabilityScaler.scale(distinctValues, ScalingMethod.LINEAR_LOWER,
								       errorMessage);
		final float[] expectedValues1 = new float[] { 0.9f, 0.85499996f, 0.74999994f, 0.5999999f };
		for (int i = 0; i < expectedValues1.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues1[i], scaledValues1[i]));

		final float[] valuesWithDups = getValuesWithDups();
		final float [] scaledValues2 = ProbabilityScaler.scale(valuesWithDups, ScalingMethod.LINEAR_LOWER,
								       errorMessage);
		final float[] expectedValues2 = new float[] { 0.9375f, 0.88125f, 0.88125f, 0.88125f, 0.75f, 0.5625f, 0.5625f };
		for (int i = 0; i < expectedValues2.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues2[i], scaledValues2[i]));
	}

	public void testRankUpper() {
		final float[] distinctValues = getDistinctValues();

		final StringBuilder errorMessage = new StringBuilder();
		final float [] scaledValues1 = ProbabilityScaler.scale(distinctValues, ScalingMethod.RANK_UPPER,
								       errorMessage);
		final float[] expectedValues1 = new float[] { 0.9375f, 0.8125f, 0.6875f, 0.5625f };
		for (int i = 0; i < expectedValues1.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues1[i], scaledValues1[i]));

		final float[] valuesWithDups = getValuesWithDups();
		final float [] scaledValues2 = ProbabilityScaler.scale(valuesWithDups, ScalingMethod.RANK_UPPER,
								       errorMessage);
		final float[] expectedValues2 = new float[] { 0.96428573f, 0.82142854f, 0.82142854f, 0.82142854f, 0.67857146f, 0.5714286f, 0.5714286f };
		for (int i = 0; i < expectedValues2.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues2[i], scaledValues2[i]));
	}

	public void testRankLower() {
		final float[] distinctValues = getDistinctValues();

		final StringBuilder errorMessage = new StringBuilder();
		final float [] scaledValues1 = ProbabilityScaler.scale(distinctValues, ScalingMethod.RANK_LOWER,
								       errorMessage);
		final float[] expectedValues1 = new float[] { 0.5625f, 0.6875f, 0.8125f, 0.9375f };
		for (int i = 0; i < expectedValues1.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues1[i], scaledValues1[i]));

		final float[] valuesWithDups = getValuesWithDups();
		final float [] scaledValues2 = ProbabilityScaler.scale(valuesWithDups, ScalingMethod.RANK_LOWER,
								       errorMessage);
		final float[] expectedValues2 = new float[] { 0.53571427f, 0.67857146f, 0.67857146f, 0.67857146f, 0.82142854f, 0.92857146f };
		for (int i = 0; i < expectedValues2.length; ++i)
			assertTrue(MathUtil.almostEqual(expectedValues2[i], scaledValues2[i]));
	}

	static private float[] getDistinctValues() {
		return new float[] { 0.2f, 0.5f, 1.2f, 2.2f };
	}

	static private float[] getValuesWithDups() {
		return new float[] { 0.2f, 0.5f, 0.5f, 0.5f, 1.2f, 2.2f, 2.2f };
	}
}
