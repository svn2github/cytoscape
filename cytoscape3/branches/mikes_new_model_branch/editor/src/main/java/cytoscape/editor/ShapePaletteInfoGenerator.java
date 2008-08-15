/* -*-Java-*-
********************************************************************************
*
* File:         ShapePaletteInfoGenerator.java
* RCS:          $Header: $
* Description:
* Author:       Michael L. Creech
* Created:      Tue Dec 05 08:47:35 2006
* Modified:     Thu May 10 09:12:27 2007 (Michael L. Creech) creech@w235krbza760
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
* Thu May 10 09:08:25 2007 (Michael L. Creech) creech@w235krbza760
*  Changed buildShapePaletteInfo 'calcsToUse' from byte[] to
*  VisualPropertyType[] for Cytoscape 2.5.
********************************************************************************
*/
package cytoscape.editor;

import org.cytoscape.vizmap.VisualPropertyType;

import javax.swing.event.ChangeListener;
import java.util.Iterator;


/**
 * Generate information needed to easily build ShapePalette entries.
 */
public interface ShapePaletteInfoGenerator {
    /**
     * Return an Iterator over ShapePaletteInfo objects where each object
     * represents the specific values from each Calculator of
     * interest for a given mapping key.  For example, if we had:
     * <PRE>
     *    ENTITY_TYPE       VisualPropertyType.NODE_FILL_COLOR  VisualPropertyType.NODE_SHAPE
     *       "type1"          Color.GREEN                       &lt;default>
     *       "type2"          &lt;default>                      NodeShape.RECT
     * </PRE>
     * where ENTITY_TYPE is a controllingAttribute and &lt;default>
     * are missing values that should be filled in with the
     * Appearance defaults for this Node/EdgeAppearanceCalculator,
     * then this method would return 2 ShapePaletteInfoImpl entries:
     * 1) key "type1" and map associations for
     *    VisualPropertyType.NODE_COLOR-->Color.GREEN and
     *    VisualPropertyType.NODE_SHAPE--> NodeShape.ELLIPSE (say is
     *    default).
     * 2) key "type2" and map associations for
     *    VisualPropertyType.NODE_COLOR-->Color.WHITE (say is default) and
     *    VisualPropertyType.NODE_SHAPE-->NodeShape.RECT.
     *
     * @param appearanceCalc the Node/EdgeAppearanceCalculator
     *                       from which to derive information.
     * @param calcsToUse an Array of VisualPropertyType that represents what
     *                   specific calculators of appearanceCalc to consider
     *                   in computing shape palette information entries.
     * @param controllingAttribute the attribute name for which we are
     *                            gathering Calculator information.
     * @param listener the ChangeListener to call when each given calculator
     *                 specified by calcsToUse changes state. If null,
     *                 no listening is performed.
     * @param filter a ShapePaletteInfoFilter used to determine which
     *               ShapePaletteInfoImpl entries should be ignored. If filter=null,
     *               no filtering is performed--all entries are used.
     */
    Iterator<ShapePaletteInfo> buildShapePaletteInfo(Object appearanceCalc,
                                                     // 05/09/07:
                                                     // byte[] calcsToUse,
                                                     // 05/09/07:
                                                     VisualPropertyType[] calcsToUse,
                                                     String controllingAttribute,
                                                     ChangeListener listener,
                                                     ShapePaletteInfoFilter filter);
}
