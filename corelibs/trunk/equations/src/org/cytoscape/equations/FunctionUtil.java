/*
  File: FunctionUtil.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.equations;


import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.AttributeList;
import javax.management.relation.RoleList;
import javax.management.relation.RoleUnresolvedList;


/**
 *  A collection of static methods that may be useful for the implementation of built-in functions.
 */
public class FunctionUtil {
	/**
	 *  Assumes that "arg" is a "String", "Boolean", "Long" or a "Double and converts it to "double".
	 *  @return the converted argument as a "double"
	 *  @throws IllegalArgumentException if the argument cannot be converted to a "double"
	 */
	static public double getArgAsDouble(final Object arg) throws IllegalArgumentException {
		if (arg.getClass() == Double.class)
			return (Double)arg;
		if (arg.getClass() == Long.class)
			return (double)(Long)arg;
		if (arg.getClass() == String.class) {
			try {
				return Double.parseDouble((String)arg);
			} catch (final Exception e) {
				throw new IllegalArgumentException("can't convert \"" + arg + "\" to a floating point number!");
			}
		}
		if (arg.getClass() == Boolean.class)
			return (Boolean)arg ? 1.0 : 0.0;

		throw new IllegalArgumentException("can't convert argument to a floating point number!");
	}

	/**
	 *  Assumes that "arg" is a "String", "Boolean", "Long" or a "Double and converts it to "long".
	 *  @return the converted argument as a "long"
	 *  @throws IllegalArgumentException if the argument cannot be converted to a "long"
	 */
	static public long getArgAsLong(final Object arg) throws IllegalArgumentException {
		if (arg.getClass() == Double.class)
			return EquationUtil.doubleToLong((Double)arg);
		if (arg.getClass() == Long.class)
			return (Long)arg;
		if (arg.getClass() == String.class) {
			try {
				return Long.parseLong((String)arg);
			} catch (final Exception e) {
				throw new IllegalArgumentException("can't convert \"" + arg + "\" to a whole number!");
			}
		}
		if (arg.getClass() == Boolean.class)
			return (Boolean)arg ? 1L : 0L;

		throw new IllegalArgumentException("can't convert argument to a whole number!");
	}

	/**
	 *  Assumes that "arg" is a "String", "Boolean", "Long" or a "Double and converts it to "boolean".
	 *  @return the converted argument as a "boolean"
	 *  @throws IllegalArgumentException if the argument cannot be converted to a "boolean"
	 */
	static public boolean getArgAsBoolean(final Object arg) throws IllegalArgumentException {
		if (arg.getClass() == Double.class) {
			final double d = (Double)arg;
			return d == 0.0 ? false : true;
		}
		if (arg.getClass() == Long.class) {
			final long l = (Long)arg;
			return l == 0L ? false : true;
		}
		if (arg.getClass() == String.class) {
			try {
				return Boolean.parseBoolean((String)arg);
			} catch (final Exception e) {
				throw new IllegalArgumentException("can't convert \"" + arg + "\" to a boolean!");
			}
		}
		if (arg.getClass() == Boolean.class)
			return (Boolean)arg;

		throw new IllegalArgumentException("can't convert argument to a boolean!");
	}

	/**
	 *  Carefully adds the numbers in "a" minimising loss of precision.
	 *
	 *  @return the sum of the elements of "a"
	 */
	static public double numericallySafeSum(final double a[]) {
		int positiveCount = 0;
		for (double d : a) {
			if (d >= 0.0)
				++positiveCount;
		}

		// Separate positive and negative values:
		final double[] positiveValues = new double[positiveCount];
		final double[] negativeValues = new double[a.length - positiveCount];
		int positiveIndex = 0;
		int negativeIndex = 0;
		for (double d : a) {
			if (d >= 0.0)
				positiveValues[positiveIndex++] = d;
			else
				negativeValues[negativeIndex++] = d;
		}

		double positiveSum = 0.0;
		if (positiveValues.length > 0) {
			// Add values in increasing order of magnitude:
			Arrays.sort(positiveValues);
			for (double d : positiveValues)
				positiveSum += d;
		}

		double negativeSum = 0.0;
		if (negativeValues.length > 0) {
			// Add values in increasing order of magnitude:
			Arrays.sort(negativeValues);
			for (int i = negativeValues.length - 1; i >= 0; --i)
				negativeSum += negativeValues[i];
		}

		return positiveSum + negativeSum;
	}

	/**
	 *  @return the String representation of the ith ordinal
	 */
	static public String getOrdinal(final int i) {
		if ((i % 100) == 11)
			return Integer.toString(i) + "th";

		switch (i % 10) {
		case 1:
			return Integer.toString(i) + "st";
		case 2:
			return Integer.toString(i) + "nd";
		case 3:
			return Integer.toString(i) + "rd";
		default:
			return Integer.toString(i) + "th";
		}
	}

	/**
	 *  @return the sample variance of the numbers in x[]
	 */
	static public double calcSampleVariance(final double[] x) {
		final int N = x.length;
		if (N < 2)
			throw new IllegalArgumentException("can't calculate a variance with fewer than 2 values!");

		final double[] xSquared = new double[N];
		for (int i = 0; i < N; ++i)
			xSquared[i] = x[i] * x[i];

		final double sumOfX = numericallySafeSum(x);
		final double sumOfXSquared = numericallySafeSum(xSquared);

		return (sumOfXSquared - (sumOfX * sumOfX) / (double)N) / (double)(N - 1);
	}

	/**
	 *  Converts an ArrayList<Double> to a regular double[]
	 */
	static public double[] arrayListToArray(final ArrayList<Double> a) {
		final double[] x = new double[a.size()];
		int i = 0;
		for (double d : a)
			x[i++] = d;

		return x;
	}

	/**
	 *  @return true, if type is Double.class, Long.class, String.class or Boolean.class, else false
	 */
	static public boolean isScalarArgType(final Class type) {
		return type == Double.class || type == Long.class || type == String.class || type == Boolean.class;
	}

	/**
	 *  @return true if "listClassCandidate" is an implementer of interface List, else false
	 */
	static public boolean someKindOfList(final Class listClassCandidate) {
		if (listClassCandidate == ArrayList.class)
			return true;
		if (listClassCandidate == Vector.class)
			return true;
		if (listClassCandidate == Stack.class)
			return true;
		if (listClassCandidate == AttributeList.class)
			return true;
		if (listClassCandidate == CopyOnWriteArrayList.class)
			return true;
		if (listClassCandidate == LinkedList.class)
			return true;
		if (listClassCandidate == RoleList.class)
			return true;
		if (listClassCandidate == RoleUnresolvedList.class)
			return true;

		return false;
	}
}
