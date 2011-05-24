/*
 Copyright (c) 2011 The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.browser.internal;


import java.util.Comparator;


public class ValidatedObjectAndEditStringComparator implements Comparator<ValidatedObjectAndEditString> {
	private final Class<?> internalColumnType;

	ValidatedObjectAndEditStringComparator(final Class<?> internalColumnType) {
		this.internalColumnType = internalColumnType;
	}

	@Override
	public int compare(final ValidatedObjectAndEditString v1, final ValidatedObjectAndEditString v2) {
		// Deal w/ nulls first:
		if (v1 == null && v2 == null)
			return 0;
		if (v1 == null)
			return -1;
		if (v2 == null)
			return +1;

		// Deal with ValidatedObjectAndEditString objects that must display an error message:
		final String errorText1 = v1.getErrorText();
		final String errorText2 = v2.getErrorText();
		if (errorText1 != null && errorText2 != null)
			return errorText1.compareToIgnoreCase(errorText2);
		if (errorText2 != null)
			return +1;
		if (errorText1 != null)
			return -1;

		if (internalColumnType == Double.class)
			return doubleCompare((Double)v1.getValidatedObject(),
					     (Double)v2.getValidatedObject());
		if (internalColumnType == Long.class)
			return longCompare((Long)v1.getValidatedObject(),
					   (Long)v2.getValidatedObject());
		if (internalColumnType == Integer.class)
			return integerCompare((Integer)v1.getValidatedObject(),
					      (Integer)v2.getValidatedObject());

		if (internalColumnType == Boolean.class)
			return booleanCompare((Boolean)v1.getValidatedObject(),
					      (Boolean)v2.getValidatedObject());

		return stringCompare(v1.getValidatedObject().toString(),
				     v2.getValidatedObject().toString());
	}

	private static int doubleCompare(final Double d1, final Double d2) {
		if (d1 < d2)
			return -1;
		return d1 > d2 ? +1 : 0;
	}

	private static int longCompare(final Long l1, final Long l2) {
		if (l1 < l2)
			return -1;
		return l1 > l2 ? +1 : 0;
	}

	private static int integerCompare(final Integer i1, Integer i2) {
		if (i1 < i2)
			return -1;
		return i1 > i2 ? +1 : 0;
	}

	private static int booleanCompare(final Boolean b1, final Boolean b2) {
		if ((b1 && b2) || (!b1 && !b2))
			return 0;
		return b1 ? -1 : +1;
	}

	private static int stringCompare(final String s1, final String s2) {
		return s1.compareToIgnoreCase(s2);
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof ValidatedObjectAndEditStringComparator)
			&& ((ValidatedObjectAndEditStringComparator)obj).internalColumnType == internalColumnType;
	}
}
