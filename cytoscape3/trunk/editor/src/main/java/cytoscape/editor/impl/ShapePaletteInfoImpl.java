/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfoImpl.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Sun Dec 03 19:18:11 2006
* Modified:     Thu May 10 09:24:52 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Thu May 10 09:22:58 2007 (Michael L. Creech) creech@w235krbza760
*  Changed use of byte and Byte from byte calcType to
*  VisualPropertyType for Cytoscape 2.5.
********************************************************************************
*/
package cytoscape.editor.impl;

import cytoscape.editor.ShapePaletteInfo;
import org.cytoscape.vizmap.VisualPropertyType;

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
    // MLC 05/09/07:
    // private Map<Byte, Object> _valueMap = new HashMap<Byte, Object>();
    // MLC 05/09/07:
	private Map<VisualPropertyType, Object> _valueMap = new HashMap<VisualPropertyType, Object>();

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
    // MLC 05/09/07:
    // public void add(byte calcType, Object value) {
    // MLC 05/09/07:
    public void add(VisualPropertyType calcType, Object value) {
		_valueMap.put(calcType, value);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param calcType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    // MLC 05/09/07:
    // public Object getValue(byte calcType) {
    // MLC 05/09/07:
    public Object getValue(VisualPropertyType calcType) {
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

		// MLC 05/09/07 BEGIN:
		// Set<Byte> calcTypes = _valueMap.keySet();
		// for (Byte calcType : calcTypes) {
		Set<VisualPropertyType> calcTypes = _valueMap.keySet();
		for (VisualPropertyType calcType : calcTypes) {
		// MLC 05/09/07 END.
			sb.append(" calcType: ");
			sb.append(_valueMap.get(calcType));
		}

		return sb.toString();
	}
}
