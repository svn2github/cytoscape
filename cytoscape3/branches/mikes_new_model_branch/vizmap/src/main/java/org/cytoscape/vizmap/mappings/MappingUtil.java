/*
  File: MappingUtil.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.vizmap.mappings;


/**
 * Provides simple utility methods for the Mapping classes.
 */
public class MappingUtil {

	public static final byte TYPE_COMPLEX = -4;
	public static final byte TYPE_SIMPLE_MAP = -3;
	public static final byte TYPE_SIMPLE_LIST = -2;
	public static final byte TYPE_UNDEFINED = -1;
	public static final byte TYPE_BOOLEAN = 1;
	public static final byte TYPE_FLOATING = 2;
	public static final byte TYPE_INTEGER = 3;
	public static final byte TYPE_STRING = 4;

    /**
     * This method determines the type of the attr used and
     * returns a string representation of it.
     */
	 // TODO figure out how to serialize new-school attrs
    public static String getAttributeTypeString(String base, String attr) {
        Byte B = new Byte(TYPE_UNDEFINED);

        return B.toString();
    }


    /**
     * This method returns an object of the specified type
     * based on the string read from the props file.
     */
    public static Object parseObjectType(String key, byte attrType) {
        if (attrType == TYPE_INTEGER)
            return Integer.valueOf(key);
        else if (attrType == TYPE_FLOATING)
            return new Double(key);
        else if (attrType == TYPE_BOOLEAN)
            return new Boolean(key);

        // assume string
        else
            return key;
    }
}
