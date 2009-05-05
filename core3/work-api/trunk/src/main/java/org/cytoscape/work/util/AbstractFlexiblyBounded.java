
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
 * A bounded number object whose bounds values are modifiable
 * @param <N>  Any type of Number
 * @author Pasteur
 */
public abstract class AbstractFlexiblyBounded<N extends Comparable<N>> extends AbstractBounded<N> {


	/**
	 * Creates a new Flexibly Bounded object.
	 *
	 * @param lower  The lower bound value. 
	 * @param initValue  The initial or default value.
	 * @param upper  The upper bound value. 
	 * @param lowerStrict  Whether the lower bound is reachable
	 * @param upperStrict  Whether the upper bound is reachable
	 */
	AbstractFlexiblyBounded(final N lower, final N initValue, final N upper, boolean lowerStrict, boolean upperStrict) {
		super(lower,initValue,upper,lowerStrict,upperStrict);
	}

	
	/**
	 *	Set the upperbound value <code>up</code>.
	 *
	 * @param up the Value
	 */
	public void setUpperBound(N up) {
		if (up == null)
			throw new NullPointerException("new upper bound is null!");

		synchronized (this) {
		int low = up.compareTo(lower);

		if ( low <= 0 )
			throw new IllegalArgumentException("new upper bound is less than or equal to lower bound");

		int val = up.compareTo(value);

		if ( val <= 0 )
			throw new IllegalArgumentException("new upper bound is less than or equal to value");

		upper = up;
		}
	}

	/**
	 *	Set the lowerbound value <code>low</code>.
	 *
	 * @param low the Value
	 */
	public void setLowerBound(N low) {
		if (low == null)
			throw new NullPointerException("new lower bound is null!");

		synchronized (this) {
		int up = low.compareTo(upper);

		if ( up >= 0 )
			throw new IllegalArgumentException("new lower bound is greater than or equal to upper bound");

		int val = low.compareTo(value);

		if ( val >= 0 )
			throw new IllegalArgumentException("new lower bound is greater than or equal to value");

		lower = low;
		}
	}

	
	/**
	 * Set upperbound accessible or not : true means that the upperbound value cannot be reached.
	 * @param strict upperbound unreachable
	 */
	public synchronized void setUpperBoundStrict(boolean strict) {
		upperStrict = strict;		
	}
	
	
	/**
	 * Set lowerbound accessible or not : true means that the lowerbound value cannot be reached.
	 * @param strict lowerbound unreachable
	 */
	public synchronized void setLowerBoundStrict(boolean strict) {
		lowerStrict = strict;		
	}

	public abstract void setUpperBound(String s);
	public abstract void setLowerBound(String s);
}
