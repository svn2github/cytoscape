
package org.cytoscape.tunable.util;

/**
 * A bounded number object. 
 */
public final class Bounded<N extends Comparable<N>> {

	private N value; 

	final private N lower; 
	final private N upper; 
	final boolean upperStrict;
	final boolean lowerStrict;

	public Bounded(final N lower, final N upper, boolean lowerStrict, boolean upperStrict) {
		if ( lower == null )
			throw new NullPointerException("lower bound is null!");
		if ( upper == null )
			throw new NullPointerException("upper bound is null!");
		if ( lower.compareTo(upper) >= 0 )
			throw new IllegalArgumentException("lower value is greater than or equal to upper value");

		this.lower = lower;
		this.upper = upper;
		this.lowerStrict = lowerStrict;
		this.upperStrict = upperStrict;
	}

	public N getUpperBound() {
		return upper;
	}

	public N getLowerBound() {
		return lower;
	}

	public boolean isUpperBoundStrict() {
		return upperStrict;
	}

	public boolean isLowerBoundStrict() {
		return lowerStrict;
	}

	public N getValue() {
		return value;
	}

	public void setValue(N v) {
		if ( v == null )
			throw new NullPointerException("value is null!");

		int up = v.compareTo(upper);
		if ( upperStrict ) {
			if ( up >= 0 )
				throw new IllegalArgumentException("value is greater than or equal to upper limit");
		} else {
			if ( up > 0 )
				throw new IllegalArgumentException("value is greater than upper limit");
		}

		int low = v.compareTo(lower);
		if ( lowerStrict ) {
			if ( low <= 0 )
				throw new IllegalArgumentException("value is less than or equal to lower limit");
		} else {
			if ( low < 0 )
				throw new IllegalArgumentException("value is less than lower limit");
		}

		value = v;
	}
}
