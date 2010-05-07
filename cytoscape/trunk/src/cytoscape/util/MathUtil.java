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
}