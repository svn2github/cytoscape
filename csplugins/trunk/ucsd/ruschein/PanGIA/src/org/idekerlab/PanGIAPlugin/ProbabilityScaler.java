package org.idekerlab.PanGIAPlugin;


import java.util.Arrays;


/**
 *  This class exists to support scaling of numeric values to the range (0.5,1.0).
 *  @author ruschein
 */
public class ProbabilityScaler {
	private static float min, max;

	public static float[] scale(final float[] values,
	                            final ScalingMethodX scalingMethod,
	                            final StringBuilder errorMessage)
	{
		errorMessage.setLength(0);

		// Sanity check:
		if (values.length < 2) {
			errorMessage.append("the values cannot be scaled since there are/is only "
			                    + values.length + " of them!");
			return null;
		}

		if (scalingMethod == ScalingMethodX.NONE)
			return values;

		findMinAndMax(values);
		if (min == max) {
			errorMessage.append("cannot scale any values because they are all identical!");
			return null;
		}


		switch (scalingMethod) {
		case LINEAR_LOWER:
			return scaleLinearLower(values, errorMessage);
		case LINEAR_UPPER:
			return scaleLinearUpper(values, errorMessage);
		case RANK_LOWER:
			return scaleRankLower(values, errorMessage);
		case RANK_UPPER:
			return scaleRankUpper(values, errorMessage);
		default:
			throw new IllegalStateException("unknown scaling method: " + scalingMethod);
		}
	}

	private static float[] scaleLinearLower(final float[] values, final StringBuilder errorMessage)
	{
		final float eps = 0.5f / (values.length + 1);

		// We map the values from (max,min) to (0.5+eps,1.0-eps)
		final float x0 = max;
		final float x1 = min;
		final float a = (0.5f - 2.0f * eps) / (x1 - x0);
		final float b = 1.0f -eps - (0.5f - 2.0f * eps) * x1 / (x1 - x0);

		for (int i = 0; i < values.length; ++i)
			values[i] = a * values[i] + b;

		return values;
	}

	private static float[] scaleLinearUpper(final float[] values, final StringBuilder errorMessage)
	{
		final float eps = 0.5f / (values.length + 1);

		// We map the values from (min,max) to (0.5+eps,1.0-eps)
		final float x0 = min;
		final float x1 = max;
		final float a = (0.5f - 2.0f * eps) / (x1 - x0);
		final float b = 1.0f - eps - (0.5f - 2.0f * eps) * x1 / (x1 - x0);

		for (int i = 0; i < values.length; ++i)
			values[i] = a * values[i] + b;

		return values;
	}

	private static float[] scaleRankLower(final float[] values, final StringBuilder errorMessage)
	{
		float[] scaledValues = QuantileNorm.quantileNorm(values);
		for (int i = 0; i < scaledValues.length; ++i)
			scaledValues[i] = 0.5f + scaledValues[i] / 2.0f;
		return scaledValues;
	}

	private static float[] scaleRankUpper(final float[] values, final StringBuilder errorMessage)
	{
		for (int i = 0; i < values.length; ++i)
			values[i] = -values[i];
		float[] scaledValues = QuantileNorm.quantileNorm(values);
		for (int i = 0; i < scaledValues.length; ++i)
			scaledValues[i] = 0.5f + scaledValues[i] / 2.0f;
		return scaledValues;
	}

	private static void findMinAndMax(final float[] values) {
		min = max = values[0];
		for (final float f : values) {
			if (f < min)
				min = f;
			else if (f > max)
				max = f;
		}
	}
}