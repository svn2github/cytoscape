/*
 =====================================================================

 ColumnComparator.java

 Created by Claude Duguay
 Copyright (c) 2002

 Rewrote by Keiichiro Ono 2006

 =====================================================================
 */
package browser;

import java.util.Comparator;
import java.util.Vector;


import cytoscape.data.CyAttributes;

/**
 *
 */
public class ColumnComparator implements Comparator {
	private static final int EMPTY_STR_LENGTH = 2;
	protected int index;
	protected byte internalColumnType;
	protected boolean ascending;

	/**
	 * Creates a new ColumnComparator object.
	 *
	 * @param index  DOCUMENT ME!
	 * @param ascending  DOCUMENT ME!
	 */
	public ColumnComparator(final int index, final byte type, final boolean ascending) {
		this.index = index;
		this.internalColumnType = type;
		this.ascending = ascending;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param obj1 DOCUMENT ME!
	 * @param obj2 DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int compare(final Object obj1, final Object obj2) {
		if (obj1 instanceof Vector && obj2 instanceof Vector) {
			final Object firstObj = ((Vector) obj1).elementAt(index);
			final Object secondObj = ((Vector) obj2).elementAt(index);

			if ((firstObj == null) && (secondObj == null)) {
				return 0;
			} else if (firstObj == null) {
				return 1;
			} else if (secondObj == null) {
				return -1;
			} else if (firstObj instanceof ValidatedObjectAndEditString && secondObj instanceof ValidatedObjectAndEditString) {
				final ValidatedObjectAndEditString v1 = (ValidatedObjectAndEditString) firstObj;
				final ValidatedObjectAndEditString v2 = (ValidatedObjectAndEditString) secondObj;
				final String errorText1 = v1.getErrorText();
				final String errorText2 = v2.getErrorText();
				if (errorText1 != null && errorText2 != null)
					return ascending ? errorText1.compareToIgnoreCase(errorText2) : errorText2.compareToIgnoreCase(errorText1);
				if (errorText2 != null)
					return ascending? 1: -1;
				if (errorText1 != null)
					return ascending? -1: 1;
				final Object val1 = v1.getValidatedObject();
				final Object val2 = v2.getValidatedObject();

				if (internalColumnType == CyAttributes.TYPE_FLOATING)
					return ascending ? doubleCompare((double)(Double)val1, (double)(Double)val2) :
					                   doubleCompare((double)(Double)val2, (double)(Double)val1);
				if (internalColumnType == CyAttributes.TYPE_INTEGER)
					return ascending ? integerCompare((int)(Integer)val1, (int)(Integer)val2) :
					                   integerCompare((int)(Integer)val2, (int)(Integer)val1);

				if (internalColumnType == CyAttributes.TYPE_BOOLEAN)
					return ascending ? booleanCompare((boolean)(Boolean)val1, (boolean)(Boolean)val2) :
					                   booleanCompare((boolean)(Boolean)val2, (boolean)(Boolean)val1);

				return ascending ? stringCompare(val1.toString(), val2.toString()):
				                   stringCompare(val2.toString(), val1.toString());
			} else {
				/*
				 * If not primitive, just compare as String
				 */
				final String str1 = firstObj.toString();
				final String str2 = secondObj.toString();

				if ((str1.length() == EMPTY_STR_LENGTH) && (str2.length() == EMPTY_STR_LENGTH)) {
					return 0;
				} else if (str1.length() == EMPTY_STR_LENGTH) {
					return 1;
				} else if (str2.length() == EMPTY_STR_LENGTH) {
					return -1;
				} else {
					return ascending ? str1.compareTo(str2) : str2.compareTo(str1);
				}
			}
		}

		return 1;
	}

	private static int doubleCompare(final double d1, final double d2) {
		if (d1 < d2)
			return -1;
		return d1 > d2 ? +1 : 0;
	}

	private static int longCompare(final long l1, final long l2) {
		if (l1 < l2)
			return -1;
		return l1 > l2 ? +1 : 0;
	}

	private static int integerCompare(final int i1, int i2) {
		if (i1 < i2)
			return -1;
		return i1 > i2 ? +1 : 0;
	}

	private static int booleanCompare(final boolean b1, final boolean b2) {
		if ((b1 && b2) || (!b1 && !b2))
			return 0;
		return b1 ? -1 : +1;
	}

	private static int stringCompare(final String s1, final String s2) {
		return s1.compareToIgnoreCase(s2);
	}

	/**
	 * Comparing numbers.
	 *
	 * @param number1
	 * @param number2
	 * @return
	 */
	public int compare(final Number number1, final Number number2) {
		final double firstNumber = number1.doubleValue();
		final double secondNumber = number2.doubleValue();

		if (firstNumber < secondNumber) {
			return -1;
		} else if (firstNumber > secondNumber) {
			return 1;
		} else {
			return 0;
		}
	}
}
