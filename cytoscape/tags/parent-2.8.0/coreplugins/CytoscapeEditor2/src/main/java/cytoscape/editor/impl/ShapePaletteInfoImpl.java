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
/*
 
 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cytoscape.editor.ShapePaletteInfo;
import cytoscape.visual.VisualPropertyType;


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
