
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.cytable;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import java.net.URL;

import java.util.*;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;



public class CyTableReader {

	private CyAttributes attrs;
	private CSVReader reader;
	private static final byte JUST_A_GUESS = Byte.MIN_VALUE;

	// for testing
	CyTableReader(Reader streamReader, CyAttributes attrs) throws IOException {
		this.reader = new CSVReader(streamReader);
		this.attrs = attrs;	
	}

	public CyTableReader(URL tableUrl, CyAttributes attrs) throws IOException {
		this(new InputStreamReader(tableUrl.openStream()),attrs);
	}

	public void read() throws IOException {
		int count = 0;
		String[] attrNames = null;
		byte[] types = null;
		String[] values;
		while ( (values = reader.readNext()) != null ) {
			if ( count == 0 ) {
				attrNames = values;
			} else if ( count == 1 ) {
				types = getTypes(values);		
				validateTypes(attrNames,types);
				if ( typesAreAllGuesses(types) )
					setAttrValues(attrNames,types,values);
			} else {
				setAttrValues(attrNames, types, values);
			}
			count++;
		}
	}

	private boolean typesAreAllGuesses(byte[] types) {
		boolean ret = true;
		for ( int i = 0; i < types.length; i++ ) {
			// we've found a type that isn't a guess, so
			// assume that the rest of the row is as designed
			if ( types[i] != JUST_A_GUESS )
				ret = false;

			// convert all guesses to attrs of type string
			else
				types[i] = CyAttributes.TYPE_STRING;
		}

		return ret;
	}


	private void validateTypes(String[] attrNames, byte[] types) {
		if ( attrNames == null )
			throw new NullPointerException("attrNames array is null");

		if ( types == null )
			throw new NullPointerException("types array is null");

		if ( attrNames.length != types.length )
			throw new RuntimeException("type array is not the same length as the attr name array");

		for ( int i = 0; i < attrNames.length; i++ ) {
			final byte existingType = attrs.getType(attrNames[i]);
			if ( existingType == CyAttributes.TYPE_UNDEFINED ) 
				continue;
			if ( existingType != types[i] )	
				throw new RuntimeException("attr types for: " + attrNames[i] + " don't match (expected) " + existingType + " (found) " + types[i]);
		}
	}

	private byte[] getTypes(String[] typeStrings) {
		byte[] types = new byte[typeStrings.length]; 
		for ( int i = 0; i < typeStrings.length; i++ )
			types[i] = guessType( typeStrings[i] );
		return types;
	}

	private byte guessType(String typeString) {
		String lc = typeString.trim().toLowerCase();
		if ( lc.equals("string") || lc.equals("java.lang.string") )
			return CyAttributes.TYPE_STRING; 
		else if ( lc.equals("integer") || lc.equals("java.lang.integer") || lc.equals("int") || lc.equals("java.lang.int") )
			return CyAttributes.TYPE_INTEGER;
		else if ( lc.equals("float") || lc.equals("java.lang.float")  || lc.equals("double") || lc.equals("java.lang.double")  )
			return CyAttributes.TYPE_FLOATING; 
		else if ( lc.equals("boolean") || lc.equals("java.lang.boolean")  || lc.equals("bool") )
			return CyAttributes.TYPE_BOOLEAN; 
		else
			return JUST_A_GUESS;
	}

	private void setAttrValues(String[] attrNames, byte[] types, String[] values) {
		if ( values == null || values[0] == null )
			return;
		String id = values[0];
		for ( int i = 1; i < attrNames.length; i++ ) {
			// skip non-existing values
			if ( values.length == i )
				return;
			if ( values[i] == null )
				continue;

			if ( types[i] == CyAttributes.TYPE_STRING ) {
				attrs.setAttribute(id,attrNames[i],values[i]);
			} else if ( types[i] == CyAttributes.TYPE_INTEGER ) { 	
				try {
					Integer val = Integer.valueOf(values[i]);
					attrs.setAttribute(id,attrNames[i],val);
				} catch ( NumberFormatException nfe ) {
					// ignore
				}
			} else if ( types[i] == CyAttributes.TYPE_FLOATING ) { 	
				try {
					Double val = Double.valueOf(values[i]);
					attrs.setAttribute(id,attrNames[i],val);
				} catch ( NumberFormatException nfe ) {
					// ignore
				}
			} else if ( types[i] == CyAttributes.TYPE_BOOLEAN ) { 	
				attrs.setAttribute(id,attrNames[i],Boolean.valueOf(values[i]));
			}
			// we don't support other types of attrs
		}
	}
}
