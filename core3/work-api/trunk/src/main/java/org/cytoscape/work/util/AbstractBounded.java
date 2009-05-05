
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.work.util;

/**
 * A bounded number object whose bounds values cannot be modified
 * @param <N>  Any type of Number
 * @author Pasteur
 */
public abstract class AbstractBounded<N extends Comparable<N>> {

	protected N value;

	protected N lower;
	protected N upper;
	protected boolean upperStrict;
	protected boolean lowerStrict;

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  The lower bound value
	 * @param upper  The upper bound value
	 * @param lowerStrict	True means that the value cannot be equal to the lower bound
	 * @param upperStrict	True means that the value cannot be equal to the upper bound
	 */
	AbstractBounded(final N lower, final N initValue, final N upper, boolean lowerStrict, boolean upperStrict) {
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
		setValue(initValue);
	}

	/**
	 *  Returns the upper limit of the object
	 *
	 * @return  upper
	 */
	public synchronized N getUpperBound() {
		return upper;
	}

	/**
	 *  Returns the lower limit of the object
	 *
	 * @return  lower
	 */
	public synchronized N getLowerBound() {
		return lower;
	}

	/**
	 * Does the value have to be strictly lower than the upper bound?
	 *
	 * @return  upperStrict
	 */
	public synchronized boolean isUpperBoundStrict() {
		return upperStrict;
	}

	/**
	 * Does the value have to be strictly greater than the lower bound?
	 *
	 * @return  lowerStrict
	 */
	public synchronized boolean isLowerBoundStrict() {
		return lowerStrict;
	}

	/**
	 *  Returns the value
	 *
	 * @return	value
	 */
	public synchronized N getValue() {
		return value;
	}

	/**
	 *	Set the value <code>v</code> as the value of the Bounded Object.
	 *
	 * @param v the Value
	 */
	public void setValue(final N v) {
		if (v == null)
			throw new NullPointerException("value is null!");

		synchronized (this) {
			final int up = v.compareTo(upper);

			if (upperStrict) {
				if (up >= 0)
					throw new IllegalArgumentException("value is greater than or equal to upper limit");
			} else {
				if (up > 0)
					throw new IllegalArgumentException("value is greater than upper limit");
			}

			final int low = v.compareTo(lower);

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

	public abstract void setValue(String s);
}
