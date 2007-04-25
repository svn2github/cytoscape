/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfo.java
* RCS:          $Header: $
* Description:  
* Author:       Michael L. Creech
* Created:      Mon Dec 04 18:09:29 2006
* Modified:     Mon Dec 04 18:50:07 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:      
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/

package cytoscape.editor;

/**
 * Interface for obtaining information for creating and using
 * ShapePalette entries.
 */
public interface ShapePaletteInfo {

    /**
     * Add a entry to this info object associating a given type of
     * Calculator with a given value.
     * @param calcType the byte repesentation of the Calculator (e.g.,
     * VizMapUI.NODE_COLOR). If calcType already exists in this
     * object, it's information will be replaced.
     * @param value the value associated with the key of this info
     * object (see getKey() from the Calculator specified by calcType.
     */
    void add(byte calcType, Object value);

    /**
     * Return the value that was obtained from the
     * DiscreteMapping of the given Calculator for the
     * controlling attribute value specified by the
     * key of this ShapePaletteInfo.
     * For example, if this ShapePaletteInfo has a key of "type1",
     * then a call with calcType="VizMapUI.NODE_COLOR" would
     * return the Color value that was associated with key "type1"
     * from the DiscreteMapping in the Node Color Calculator.
     * @param calcType the type of the Calculator for which we are
     * to obtain the stored value.
     * @return the Object value from the DiscreteMapping. If
     *         no such calculator exists for this object, null is
     *         returned.  Note that a default value may be
     *         returned if calcType doesn't have a mapping for
     *         this Object's key.
     */
    Object getValue(byte calcType);

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
