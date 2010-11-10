package cytoscape.util;


public class MathUtil {
	/**
	 *  @returns the unbiased exponent of a single-precision IEEE floating point number
	 */
	static public int getExponent(final float f) {
		final int EXPONENT_MASK = 0x7f800000;
		final int bits = Float.floatToIntBits(f) & EXPONENT_MASK;
		final int BIAS = 127;
		final int BIT_OFFSET = 23;
		return (bits >> BIT_OFFSET) - BIAS;
	}
	/**
	 *  @returns the unbiased exponent of a double-precision IEEE floating point number
	 */
	static public long getExponent(final double f) {
		final long EXPONENT_MASK = 0x7FFFF00000000000L;
		final long bits = Double.doubleToLongBits(f) & EXPONENT_MASK;
		final int BIAS = 1023;
		final int BIT_OFFSET = 52;
		return (bits >> BIT_OFFSET) - BIAS;
	}

	static public boolean almostEqual(final float x1, final float x2) {
		if (x1 == x2)
			return true;

		if (Math.signum(x1) != Math.signum(x2))
			return false;

		if (MathUtil.getExponent(x1) != MathUtil.getExponent(x2))
			return false;

		final float absX1 = Math.abs(x1);
		final float absX2 = Math.abs(x2);

		if (x1 != 0.0f)
			return Math.abs(x1 - x2) / Math.abs(x1) < 1.0e-6f;
		else
			return Math.abs(x1 - x2) / Math.abs(x2) < 1.0e-6f;
	}

	static public boolean almostEqual(final double x1, final double x2) {
		if (x1 == x2)
			return true;

		if (Math.signum(x1) != Math.signum(x2))
			return false;

		if (MathUtil.getExponent(x1) != MathUtil.getExponent(x2))
			return false;

		final double absX1 = Math.abs(x1);
		final double absX2 = Math.abs(x2);

		if (x1 != 0.0)
			return Math.abs(x1 - x2) / Math.abs(x1) < 1.0e-12;
		else
			return Math.abs(x1 - x2) / Math.abs(x2) < 1.0e-12;
	}

	static public boolean isValidDouble(final String s) {
		try {
			double d = Double.valueOf(s);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
}