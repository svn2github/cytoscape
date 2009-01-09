
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

package org.example.tunable.util;

/**
 * A bounded number object.
 *
 * @param <N>  DOCUMENT ME!
 */
abstract class AbstractBounded<N extends Comparable<N>> {

	protected N value;

	final protected N lower;
	final protected N upper;
	final protected boolean upperStrict;
	final protected boolean lowerStrict;

	/**
	 * Creates a new Bounded object.
	 *
	 * @param lower  DOCUMENT ME!
	 * @param upper  DOCUMENT ME!
	 * @param lowerStrict  DOCUMENT ME!
	 * @param upperStrict  DOCUMENT ME!
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
