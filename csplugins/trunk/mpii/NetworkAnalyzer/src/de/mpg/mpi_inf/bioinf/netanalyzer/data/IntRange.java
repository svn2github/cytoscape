package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Integer range.
 * 
 * @author Yassen Assenov
 */
public final class IntRange {

	/**
	 * Initializes a new instance of <code>IntRange</code>.
	 * 
	 * @param aMin Minimum value in the integer range.
	 * @param aMax Maximum value in the integer range.
	 */
	public IntRange(Integer aMin, Integer aMax) {
		min = aMin;
		max = aMax;
	}

	/**
	 * Gets the maximum value of this integer range.
	 * 
	 * @return Maximum value of the range; <code>null</code> if no maximum value is present.
	 */
	public Integer getMax() {
		return max;
	}

	/**
	 * Gets the minimum value of this integer range.
	 * 
	 * @return Minimum value of the range; <code>null</code> if no minimum value is present.
	 */
	public Integer getMin() {
		return min;
	}

	/**
	 * Checks if a maximum value for this range is defined.
	 * <p>
	 * This is a convenience method only. Calling this method is equivalent to calling:<br/>
	 * <code>getMax() != null</code>
	 * </p>
	 * 
	 * @return <code>true</code> if this range has a maximum value; <code>false</code> otherwise.
	 */
	public boolean hasMax() {
		return max != null;
	}

	/**
	 * Checks if a minimum value for this range is defined.
	 * <p>
	 * This is a convenience method only. Calling this method is equivalent to calling:<br/>
	 * <code>getMin() != null</code>
	 * </p>
	 * 
	 * @return <code>true</code> if this range has a minimum value; <code>false</code> otherwise.
	 */
	public boolean hasMin() {
		return min != null;
	}

	/**
	 * Checks if this range is fully defined, that is, if it minimum and maximum value are defined.
	 * 
	 * @return <code>true</code> if this range has a minimum and a maximum value; <code>false</code>
	 *         otherwise.
	 */
	public boolean isFullyDefined() {
		return max != null && min != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + (min != null ? String.valueOf(min) : "") + ", "
				+ (max != null ? String.valueOf(max) : "") + "]";
	}

	/**
	 * Maximum value of the range.
	 */
	private Integer max;

	/**
	 * Minimum value of the range.
	 */
	private Integer min;
}
