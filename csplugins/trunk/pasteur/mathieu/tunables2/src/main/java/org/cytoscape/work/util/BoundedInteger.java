package org.cytoscape.work.util;

import org.cytoscape.work.AbstractBounded;

public class BoundedInteger extends AbstractBounded<Integer> {


	/**
	 * @param lower = lower bound of the object
	 * @param initValue = initial value of the object(has to be set within the bounds)
	 * @param upper = upper bound of the object
	 * @param lowerStrict = if true, the value can't be equal to lowerBound
	 * @param upperStrict = if true, the value can't be equal to upperBound
	 */
	
	public BoundedInteger(final Integer lower, final Integer initValue, final Integer upper, boolean lowerStrict, boolean upperStrict) {
		super(lower,initValue,upper,lowerStrict,upperStrict);
	}
}
