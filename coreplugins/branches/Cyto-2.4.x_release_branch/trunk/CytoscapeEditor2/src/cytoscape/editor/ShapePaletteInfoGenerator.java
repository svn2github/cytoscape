/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfoGenerator.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Tue Dec 05 08:47:35 2006
* Modified:     Tue Dec 05 08:51:16 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.editor;

import java.util.Iterator;

import javax.swing.event.ChangeListener;


/**
 * Generate information needed to easily build ShapePalette entries.
 */
public interface ShapePaletteInfoGenerator {
    /**
     * Return an Iterator over ShapePaletteInfo objects where each object
     * represents the specific values from each Calculator of
     * interest for a given mapping key.  For example, if we had:
     * <PRE>
     *    ENTITY_TYPE       VizMapUI.NODE_COLOR       VizMapUI.NODE_SHAPE
     *       "type1"          Color.GREEN                 &lt;default>
     *       "type2"          &lt;default>               ShapeNodeRealizer.RECT
     * </PRE>
     * where ENTITY_TYPE is a controllingAttribute and &lt;default>
     * are missing values that should be filled in with the
     * Appearance defaults for this Node/EdgeAppearanceCalculator,
     * then this method would return 2 ShapePaletteInfoImpl entries:
     * 1) key "type1" and map associations for
     *    VizMapUI.NODE_COLOR-->Color.GREEN and
     *    VizMapUI.NODE_SHAPE--> ShapeNodeRealizer.ELLIPSE (say is
     *    default).
     * 2) key "type2" and map associations for
     *    VizMapUI.NODE_COLOR-->Color.WHITE (say is default) and
     *    VizMapUI.NODE_SHAPE-->ShapeNodeRealizer.RECT.
     *
     * @param appearanceCalc the Node/EdgeAppearanceCalculator
     *                       from which to derive information.
     * @param calcsToUse an Array of bytes that represent what
     *                   specific calculators of appearanceCalc to consider
     *                   in computing shape palette information entries.
     * @param controllingAttibute the attribute name for which we are
     *                            gathering Calculator information.
     * @param listener the ChangeListener to call when each given calculator
     *                 specified by calcsToUse changes state. If null,
     *                 no listening is performed.
     * @param filter a ShapePaletteInfoFilter used to determine which
     *               ShapePaletteInfoImpl entries should be ignored. If filter=null,
     *               no filtering is performed--all entries are used.
     */
    Iterator<ShapePaletteInfo> buildShapePaletteInfo(Object appearanceCalc,
                                                          byte[] calcsToUse,
                                                          String controllingAttribute,
                                                          ChangeListener listener,
                                                          ShapePaletteInfoFilter filter);
}
