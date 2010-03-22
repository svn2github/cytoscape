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


/**
 *
 */
public class ColumnComparator implements Comparator {
	private static final int EMPTY_STR_LENGTH = 2;
	protected int index;
	protected boolean ascending;

	/**
	 * Creates a new ColumnComparator object.
	 *
	 * @param index  DOCUMENT ME!
	 * @param ascending  DOCUMENT ME!
	 */
	public ColumnComparator(final int index, final boolean ascending) {
		this.index = index;
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
			} else if (firstObj instanceof Comparable && secondObj instanceof Comparable) {
				final Comparable firstComparableObj = (Comparable) firstObj;
				final Comparable secondComparableObj = (Comparable) secondObj;

				/*
				 * If these values are Strings, treat empty values as null.
				 */
				if (firstComparableObj instanceof String && secondComparableObj instanceof String) {
					final int firstLength = ((String) firstComparableObj).trim().length();
					final int secondLength = ((String) secondComparableObj).trim().length();

					if ((firstLength == 0) && (secondLength == 0)) {
						return 0;
					} else if (firstLength == 0) {
						return 1;
					} else if (secondLength == 0) {
						return -1;
					} else {
						return ascending ? firstComparableObj.compareTo(secondComparableObj)
						                 : secondComparableObj.compareTo(firstComparableObj);
					}
				} else {
					return ascending ? firstComparableObj.compareTo(secondComparableObj)
					                 : secondComparableObj.compareTo(firstComparableObj);
				}
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
