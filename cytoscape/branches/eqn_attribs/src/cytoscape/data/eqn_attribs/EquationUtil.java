/*
  File: EquationUtil.java

  Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data.eqn_attribs;


import java.util.List;
import java.util.Map;
import cytoscape.data.CyAttributes;


public class EquationUtil {

	/**
	 *  Populates "attribNameToTypeMap" with the names from "cyAttribs" and their types as mapped
	 *  to the types used by attribute equations.  Types (and associated names) not used by
	 *  attribute equations are ommitted.
	 *
	 *  @param cyAttribs            the attributes to map
	 *  @param ignore               if not null, skip the attribute with this name
	 *  @param attribNameToTypeMap  the result of the translation from attribute types to
	 *                              attribute equation types
	 */
	public static void initAttribNameToTypeMap(final CyAttributes cyAttribs, final String ignore,
	                             final Map<String, Class> attribNameToTypeMap)
	{
		for (final String attribName : cyAttribs.getAttributeNames()) {
			if (ignore == null || ignore.equals(attribName))
				continue;
			if (!cyAttribs.getUserVisible(attribName))
				continue;

			final byte type = cyAttribs.getType(attribName);
			if (type == CyAttributes.TYPE_BOOLEAN)
				attribNameToTypeMap.put(attribName, Boolean.class);
			else if (type == CyAttributes.TYPE_INTEGER)
				attribNameToTypeMap.put(attribName, Long.class);
			else if (type == CyAttributes.TYPE_FLOATING)
				attribNameToTypeMap.put(attribName, Double.class);
			else if (type == CyAttributes.TYPE_STRING)
				attribNameToTypeMap.put(attribName, String.class);
			else if (type == CyAttributes.TYPE_SIMPLE_LIST)
				attribNameToTypeMap.put(attribName, List.class);
			else
				/* We intentionally ignore everything else! */;
		}
	}

	/**
	 *  @returns "attribName" written as am attribute reference with a leading $-sign
	 */
	public static String attribNameAsReference(final String attribName) {
		if (isSimpleAttribName(attribName))
			return "$" + attribName;
		else
			return "${" + escapeAttribName(attribName) + "}";
	}

	/**
	 *  @param attribName the name to test
	 *  @returns true if "attribName" start with a letter and consists of only letters and digits, else false
	 */
	private static boolean isSimpleAttribName(final String attribName) {
		final int length = attribName.length();
		if (length == 0)
			throw new IllegalStateException("empty attribute names should never happen!");

		if (!Character.isLetter(attribName.charAt(0)))
			return false;

		for (int i = 1; i < length; ++i) {
			final char ch = attribName.charAt(i);
			if (!Character.isLetter(ch) && !Character.isDigit(ch))
				return false;
		}

		return true;
	}

	/**
	 *  @returns "attribName" with characters that need to be backslash-escaped when written as
	 *           part of an attribute refernce, escaped
	 */
	private static String escapeAttribName(final String attribName) {
		final int length = attribName.length();
		final StringBuilder escapedAttribName = new StringBuilder(length * 2);
		for (int i = 0; i < length; ++i) {
			final char ch = attribName.charAt(i);
			switch (ch) {
			case ' ':
			case '\\':
			case '{':
			case '}':
			case ':':
				escapedAttribName.append('\\');
			}
			escapedAttribName.append(ch);
		}

		return escapedAttribName.toString();
	}
}