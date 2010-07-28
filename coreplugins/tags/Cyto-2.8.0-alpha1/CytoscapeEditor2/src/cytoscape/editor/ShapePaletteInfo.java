/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfo.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Mon Dec 04 18:09:29 2006
* Modified:     Thu May 10 09:18:21 2007 (Michael L. Creech) creech@w235krbza760
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
* Thu May 10 09:17:30 2007 (Michael L. Creech) creech@w235krbza760
*  Changed use of 'calcType' from byte to VisualPropertyType for Cytoscape 2.5.
********************************************************************************
*/
package cytoscape.editor;

import cytoscape.visual.VisualPropertyType;


/**
 * Interface for obtaining information for creating and using
 * ShapePalette entries.
 */
public interface ShapePaletteInfo {
	/**
	 * Add a entry to this info object associating a given type of
	 * Calculator with a given value.
	 * @param calcType the VisualPropertyType repesentation of the
	 * Calculator (e.g., VisualPropertyType.NODE_FILL_COLOR). If
	 * calcType already exists in this object, it's information
	 * will be replaced.
	 * @param value the value associated with the key of this info
	 * object (see getKey() from the Calculator specified by calcType.
	 */
    // MLC 05/09/07:
    // void add(byte calcType, Object value);
    // MLC 05/09/07:
    void add(VisualPropertyType calcType, Object value);

	/**
	 * Return the value that was obtained from the DiscreteMapping
	 * of the given Calculator for the controlling attribute value
	 * specified by the key of this ShapePaletteInfo.  For
	 * example, if this ShapePaletteInfo has a key of "type1",
	 * then a call with
	 * calcType="VisualPropertyType.NODE_FILL_COLOR" would return
	 * the Color value that was associated with key "type1" from
	 * the DiscreteMapping in the Node Color Calculator.
	 * @param calcType the type of the Calculator for which we are
	 * to obtain the stored value.
	 * @return the Object value from the DiscreteMapping. If
	 *         no such calculator exists for this object, null is
	 *         returned.  Note that a default value may be
	 *         returned if calcType doesn't have a mapping for
	 *         this Object's key.
	 */
    // MLC 05/09/07:
    // Object getValue(byte calcType);
    // MLC 05/09/07:
    Object getValue(VisualPropertyType calcType);

	/**
	 * Return the key associated with this ShapePaletteInfo.
	 * This corresponds to a specific value from the
	 * controlling attribute name.
	 */
	String getKey();

	/**
	 * Return the name of the controlling attribute associated with
	 * this ShapePaletteInfo.
	 */
	String getControllingAttributeName();

	String toString();
}
