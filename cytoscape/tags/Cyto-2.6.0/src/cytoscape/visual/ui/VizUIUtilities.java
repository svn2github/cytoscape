/*
 File: VizUIUtilities.java

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

//--------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------
package cytoscape.visual.ui;

import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;

import cytoscape.visual.calculators.Calculator;


//--------------------------------------------------------------------------------
/**
 * This class provides utility functions for the UI package. Most of these
 * methods involve converting a generic operation on a byte constant specifying
 * the visual attribute type to the corresponding operation specific to the
 * particular attribute.
 *
 * These methods are package-protected because the UI is designed to make sure
 * that the arguments passed to these methods are appropriate.
 * 
 * Deprecated.  Use new VizMapper UI.
 */
@Deprecated
public class VizUIUtilities {
    /**
     * Gets the current default value for the visual attribute specified by the
     * second argument in the visual style specified by the first argument.
     * Returns null if the first argument is null.
	 * @deprecated Use VisualPropertyType.getDefault(style) instead. Going away 4/2008.
     */
    @Deprecated
    static Object getDefault(VisualStyle style, byte type) {
        return VisualPropertyType.getVisualPorpertyType(type).getDefault(style);
    }


    /**
     * Sets the default value for the visual attribute specified by the second
     * argument in the visual style specified by the first argument. The third
     * argument is the new default value. Returns null if the first or third
     * argument is null.
	 * @deprecated Use VisualPropertyType.setDefault(style,obj) instead. Going away 4/2008.
     */
    @Deprecated
    static void setDefault(VisualStyle style, byte type, Object c) {
		VisualPropertyType.getVisualPorpertyType(type).setDefault(style,c);
    }


    /**
     * Gets the current calculator for the visual attribute specified by the
     * second argument in the visual style specified by the first argument. This
     * may be null if no calculator is currently specified. Returns null if the
     * first argument is null.
	 * @deprecated Use VisualPropertyType.getCurrentCalculator(style) instead. Going away 4/2008.
     */
    @Deprecated
    static Calculator getCurrentCalculator(VisualStyle style, byte type) {
        return VisualPropertyType.getVisualPorpertyType(type).getCurrentCalculator(style);
    }

    /**
     * Sets the current calculator for the visual attribute specified by the
     * second argument in the visual style specified by the first argument. The
     * third argument is the new calculator and may be null. This method does
     * nothing if the first argument specifying the visual style is null.
	 * @deprecated Use VisualPropertyType.setCurrentCalculator(style,calc) instead. Going away 4/2008.
     */
    @Deprecated
    static void setCurrentCalculator(VisualStyle style, byte type, Calculator c) {
		VisualPropertyType.getVisualPorpertyType(type).setCurrentCalculator(style,c);
	}
}
