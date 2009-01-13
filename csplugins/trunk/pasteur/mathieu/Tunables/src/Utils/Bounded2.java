package Utils;

public final class Bounded2<N extends Comparable<N>> {
	private N value;
	final private N lower;
	final private N upper;
	final boolean upperStrict;
	final boolean lowerStrict;

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
	 */
	public Bounded2(final N lower, final N upper, boolean lowerStrict, boolean upperStrict) {
		if (lower == null)
			throw new NullPointerException("lower bound is null!");

		if (upper == null)
			throw new NullPointerException("upper bound is null!");

		if (lower.compareTo(upper) >= 0)
			throw new IllegalArgumentException("lower value is greater than or equal to upper value");

		this.lower = lower;
		this.upper = upper;
		this.lowerStrict = lowerStrict;
		this.upperStrict = upperStrict;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getUpperBound() {
		return upper;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getLowerBound() {
		return lower;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isUpperBoundStrict() {
		return upperStrict;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isLowerBoundStrict() {
		return lowerStrict;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public N getValue() {
		return value;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public void setValue(N v) {
		if (v == null)
			throw new NullPointerException("value is null!");

		int up = v.compareTo(upper);

		if (upperStrict) {
			if (up >= 0)
				throw new IllegalArgumentException("value is greater than or equal to upper limit");
		} else {
			if (up > 0)
				throw new IllegalArgumentException("value is greater than upper limit");
		}

		int low = v.compareTo(lower);

		if (lowerStrict) {
			if (low <= 0)
				throw new IllegalArgumentException("value is less than or equal to lower limit");
		} else {
			if (low < 0)
				throw new IllegalArgumentException("value is less than lower limit");
		}

		value = v;
	}
}
