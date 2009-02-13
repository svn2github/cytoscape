package org.cytoscape.work.util;

import org.cytoscape.work.AbstractBounded;

public class BoundedDouble extends AbstractBounded<Double> {

	/**
	 * @param lower = lower bound of the object
	 * @param initValue = initial value of the object(has to be set within the bounds)
	 * @param upper = upper bound of the object
	 * @param lowerStrict = if true, the value can't be equal to lowerBound
	 * @param upperStrict = if true, the value can't be equal to upperBound
	 */
	
	public BoundedDouble(final Double lower, final Double initValue, final Double upper, boolean lowerStrict, boolean upperStrict) {
		super(lower,initValue,upper,lowerStrict,upperStrict);
	}
}
