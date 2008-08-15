/*
  File: DiscreteMappingWriter.java

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

//----------------------------------------------------------------------------
// $Revision: 10220 $
// $Date: 2007-05-10 11:45:14 -0700 (Thu, 10 May 2007) $
// $Author: kono $
//----------------------------------------------------------------------------
package org.cytoscape.vizmap.mappings.discrete;

import org.cytoscape.vizmap.ObjectToString;
import org.cytoscape.vizmap.mappings.MappingUtil;

import java.util.Iterator;
import java.util.Properties;
import java.util.SortedMap;


/**
 * Writes DiscreteMapping Properties.
 *
 * Unit Test for this class exists in:
 * cytoscape.visual.mappings.discrete.unitTests.TestDiscreteMappingWriter.
 */
public class DiscreteMappingWriter {
	private String attrName;
	private String baseKey;
	private SortedMap<Object,Object> map;

	/**
	 * Constructor.
	 * @param attrName Controlling Attribute Name.
	 * @param map Discrete Map.
	 */
	public DiscreteMappingWriter(String attrName, String baseKey, SortedMap<Object,Object> map) {
		this.attrName = attrName;
		this.baseKey = baseKey;
		this.map = map;
	}

	/**
	 * Return a Properties object with entries suitable for customizing this
	 * object via the applyProperties method.
	 */
	public Properties getProperties() {
		Properties newProps = new Properties();

		/*
		 * if null, return empty prop.
		 */
		if (attrName == null)
			return newProps;

		String contKey = baseKey + ".controller";
		newProps.setProperty(contKey, attrName);

		String contTypeKey = baseKey + ".controllerType";
		newProps.setProperty(contTypeKey, MappingUtil.getAttributeTypeString(baseKey, attrName));

		String mapKey = baseKey + ".map.";
		Iterator iterator = map.keySet().iterator();

		Object key;
		Object value;
		String stringValue;

		while (iterator.hasNext()) {
			key = iterator.next();
			value = map.get(key);

			if (value != null) {
				stringValue = ObjectToString.getStringValue(value);
				newProps.setProperty(mapKey + key.toString(), stringValue);
			}
		}

		return newProps;
	}
}
