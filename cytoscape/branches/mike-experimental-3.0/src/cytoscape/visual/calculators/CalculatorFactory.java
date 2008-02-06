/*
 File: CalculatorFactory.java

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
package cytoscape.visual.calculators;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import java.lang.reflect.Constructor;

import java.util.Properties;


/**
 * This class provides static factory methods for constructing instances of
 * Calculators as specified by arguments and static methods for getting names
 * and labels based on calculator type.
 */
public class CalculatorFactory {

	/**
	 * Creates a caclculator defined by the type identified by the ".visualPropertyType" 
	 * property in the calcProps argument.  If that isn't found, it guesses the visual
	 * property type based on the baseKey.
	 */
    public static Calculator newCalculator(String name, Properties calcProps, String baseKey) {
        final String typeName = calcProps.getProperty(baseKey + ".visualPropertyType");
		VisualPropertyType t = null; 

		try { 

			if ( typeName == null ) 
				t = guessType( baseKey );
			else 
				t = VisualPropertyType.valueOf(typeName);

			if ( t == null ) {
				//System.out.println("Couldn't parse baseKey: " + baseKey);
				return null;	
			}

			return new BasicCalculator(name,calcProps,baseKey,t);
		} catch (IllegalArgumentException e) { 
			//System.out.println("Couldn't parse visual property type: " + typeName);
			return null;	
		}
	}


	private static VisualPropertyType guessType(String key) {
		String lckey = key.toLowerCase();
		for ( VisualPropertyType vpt : VisualPropertyType.values() )
			if ( lckey.startsWith(vpt.getPropertyLabel().toLowerCase()) )
				return vpt;
		return null;
	}
}
