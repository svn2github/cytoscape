/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfoFilter.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Sun Dec 03 19:16:10 2006
* Modified:     Sun Dec 03 19:18:35 2006 (Michael L. Creech) creech@w235krbza760
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
 * Interface for defining filters to determing which ShapePaletteInfoImpl objects
 * should be used.
 */
public interface ShapePaletteInfoFilter {
	/**
	 * Should we use a given ShapePaletteInfoImpl entry?
	 * @param info the ShapePaletteInfoImpl under consideration.
	 * @return true if the info should be used. false otherwise.
	 */
	public boolean useEntry(ShapePaletteInfo info);
}
