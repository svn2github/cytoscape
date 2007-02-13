/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfoImpl.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Sun Dec 03 19:18:11 2006
* Modified:     Mon Dec 04 18:47:44 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.editor.impl;

import cytoscape.editor.ShapePaletteInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Stores information about the values obtains from a certain set of Calculators
 * based on a String key.
 */
public class ShapePaletteInfoImpl implements ShapePaletteInfo {
	private String _key;
	private String _attributeName;
	private Map<Byte, Object> _valueMap = new HashMap<Byte, Object>();

	protected ShapePaletteInfoImpl(String controllingAttributeName, String controllingAttributeKey) {
		_key = controllingAttributeKey;
		_attributeName = controllingAttributeName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param calcType DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public void add(byte calcType, Object value) {
		_valueMap.put(calcType, value);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param calcType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getValue(byte calcType) {
		return _valueMap.get(calcType);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getKey() {
		return _key;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getControllingAttributeName() {
		return _attributeName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("controllingAttributeName: ");
		sb.append(_attributeName);
		sb.append("key: ");
		sb.append(_key);

		Set<Byte> calcTypes = _valueMap.keySet();

		for (Byte calcType : calcTypes) {
			sb.append(" calcType: ");
			sb.append(_valueMap.get(calcType));
		}

		return sb.toString();
	}
}
