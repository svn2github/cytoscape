/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeEditor.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdgeEditor/src/cytoscape/hyperedge/editor/HyperEdgeEditor.java,v 1.1 2007/07/04 01:19:09 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Jul 21 10:41:18 2006
* Modified:     Fri Jan 23 10:37:45 2009 (Michael L. Creech) creech@w235krbza760
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
* Fri Jan 23 10:33:36 2009 (Michael L. Creech) creech@w235krbza760
*  Removed arrowShapeToArrow() in favor of inhertied  method.
* Wed Dec 19 14:25:53 2007 (Michael L. Creech) creech@w235krbza760
*  Removed addNodeContextMenuItems() and addEdgeContextMenuItems().
* Sun Oct 21 16:15:50 2007 (Michael L. Creech) creech@w235krbza760
*  Various changes to use ArrowShape versus Arrows.
* Thu May 17 07:37:53 2007 (Michael L. Creech) creech@w235krbza760
*  Moved from cytoscape.editor.editors package to
*  cytoscape.hyperedge.editor package.
* Wed Jan 31 13:29:14 2007 (Michael L. Creech) creech@w235krbza760
*  Relaxed some restrictions in convertEdgeIntoHyperEdge() and added
*  handleEdgeDropOnHyperEdgeEdge() and createHyperEdgeWithOneRegularNode().
* Tue Jan 16 09:19:41 2007 (Michael L. Creech) creech@w235krbza760
*  Commented out some debugging statements.
* Tue Jan 09 09:33:28 2007 (Michael L. Creech) creech@w235krbza760
*  Various changes to convertEdgeIntoHyperEdge() to present dialogs
*  when illegal operations are performed. Also Fixed NPE.
*  Changed the direction of edges added in between two HyperEdge
*  ConnectorNodes in addEdge().
* Sat Jan 06 10:21:05 2007 (Michael L. Creech) creech@w235krbza760
*  Changed reference to addReaction.gif-->addReaction.png in
*  generateHyperEdgePaletteEntries().
* Fri Dec 29 07:52:10 2006 (Michael L. Creech) creech@w235krbza760
*  Overriding addNodeContextMenuItems() and addEdgeContextMenuItems()
*  from BasicCytoscapeEditor to add custom popup delete menu items.
* Tue Dec 26 05:20:14 2006 (Michael L. Creech) creech@w235krbza760
*  Renamed "Reaction" to "Add Reaction" and moved to front of reaction palette
*  entries.
* Sun Dec 17 05:58:09 2006 (Michael L. Creech) creech@w235krbza760
*  Updated use of CytoscapeEditor DragSourceContextCursorSetters and
*  added HyperEdgePaletteItemDragCursorSetter.
* Wed Dec 13 11:41:48 2006 (Michael L. Creech) creech@w235krbza760
*  Added addEdge() to allow regular edges to be dropped and add
*  nodes to HyperEdges.
* Tue Nov 07 09:03:46 2006 (Michael L. Creech) creech@w235krbza760
*  Updated for use with HyperEdge 2.4 alfa 1.
* Mon Nov 06 08:35:02 2006 (Michael L. Creech) creech@w235krbza760
*  Updated for use with HyperEdge 2.1.1.
********************************************************************************
*/
package cytoscape.hyperedge.editor;

import com.agilent.labs.lsiutils.gui.MiscGUI;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.DragSourceContextCursorSetter;
import cytoscape.editor.ShapePaletteInfo;
import cytoscape.editor.ShapePaletteInfoFilter;
import cytoscape.editor.ShapePaletteInfoGenerator;

import cytoscape.editor.editors.DefaultCytoscapeEditor;

import cytoscape.editor.event.PaletteNetworkEditEventHandler;

import cytoscape.editor.impl.CytoShapeIcon;

import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.HyperEdgeManager;

// MLC 12/18/07:
// import cytoscape.hyperedge.editor.actions.HyperEdgeDeleteAction;
import cytoscape.hyperedge.editor.actions.SelectHyperEdgeAction;

import cytoscape.hyperedge.impl.HyperEdgeImpl;
import cytoscape.hyperedge.impl.utils.HEUtils;

import cytoscape.view.CyNetworkView;

import cytoscape.visual.Arrow;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

import ding.view.DGraphView;

import giny.model.Node;

import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


/**
 * An example editor that extends the basic Cytoscape editor and is based upon a
 * drag-and-drop and palette framework into which developers plug in semantics.
 * The framework consists of
 * <ul>
 * <li> a palette, from which the user drags and drops shapes onto the canvas
 * <li> an extensible shape class for the palette,
 * <li> a drawing canvas upon which shapes are dropped, and
 * <li> event handlers which respond to drop events generated by the canvas.
 * </ul>
 * <p>
 * The dropping of shapes onto the canvas results in the addition of nodes and
 * edges to the current Cytoscape network, as defined by the behavior of the
 * event handler that responds to the drop events. In the simple hyperedge
 * editor, there are <TO BE FILLED IN>
 * <p>
 *
 * @author Michael L. Creech
 * @version 1.0
 * @see PaletteNetworkEditEventHandler
 *
 */
public class HyperEdgeEditor extends DefaultCytoscapeEditor {
    static final private String IMAGE_REL_LOC       = "images/";
    public static final int     EDGE_LENGTH         = 40;
    public static final int     NODE_VERTICAL_SEP   = 10;
    public static final int     NODE_HORIZONTAL_SEP = 10;

    // Used for defining more complex structures then nodes and edges (e.g., Reactions).
    public static final String  COMPLEX_TYPE = "COMPLEX_TYPE";
    private static final String REACTION = "reaction";

    // MLC 05/11/07 BEGIN:
    // A hack to stop infinite recursion when visual manager is
    // changed (see notes in initializeControls()):
    private boolean alreadyInitializing = false;

    // MLC 05/11/07 END.

    // remembered HyperEdges:
    private List<HyperEdge> _hyperEdgeClipboard = new ArrayList<HyperEdge>();

    //    public static final String EDGE_TYPE = "EDGE_TYPE";
    //    public static final String ACTIVATION = "Activation";
    //    public static final String INHIBITION = "Inhibition";
    //    public static final String CATALYSIS = "Catalysis";

    //    /**
    //     * class used to construct visual style used by the HyperEdgeEditor
    //     */
    //    private BioChemicalReactionVisualStyle _bcrVisualStyle = new BioChemicalReactionVisualStyle();

    /**
     * flag used to determine when to construct a visual style vs. use an existing one
     */

    // MLC 11/25/06:
    //    private static boolean regeneratedVizStyle = false;
    private HyperEdgeFactory _factory = HyperEdgeFactory.INSTANCE;
    private HyperEdgeManager _manager = _factory.getHyperEdgeManager();

    public HyperEdgeEditor() {
        super();
    }

    // overrides BasicCytoscapeEditor.buildVisualStyle():
    public void buildVisualStyle() {
        // do visual style creation at time that editor is created, to
        // accommodate the current visual style potentially being
        // clobbered by other plugins
        BioChemicalReactionVisualStyle.getVisualStyle()
                                      .setupVisualStyle(true, false);
        //        VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        //
        //        CalculatorCatalog    catalog = manager.getCalculatorCatalog();
        //
        //        VisualStyle vizStyle = catalog.getVisualStyle(
        //            BioChemicalReactionVisualStyle.BIOCHEMICAL_REACTION_VISUAL_STYLE);
        //
        //        //		CyLogger.getLogger().debug ("Got visual Style from catalog: " + catalog 
        //        //				+ " = " + vizStyle);
        //        if (_bcrVisualStyle == null) {
        //            _bcrVisualStyle = new BioChemicalReactionVisualStyle();
        //        }
        //
        //        if (vizStyle == null) {
        //            vizStyle = _bcrVisualStyle.createVizMapper();
        //        } else {
        //            //			CyLogger.getLogger().debug("Calling defineVisualStyle for: " + vizStyle);
        //            _bcrVisualStyle.defineVisualStyle(vizStyle, manager, catalog);
        //        }
    }

    // overrides generateEdgePaletteEntries in DefaultCytoscapeEditor 
    protected void generatePaletteEntries() {
        super.generatePaletteEntries();
        generateHyperEdgePaletteEntries();
    }

    // overrides generateEdgePaletteEntries in DefaultCytoscapeEditor 
    protected void generateEdgePaletteEntries(String controllingAttribute) {
        EdgeAppearanceCalculator eac = Cytoscape.getVisualMappingManager()
                                                .getVisualStyle()
                                                .getEdgeAppearanceCalculator();

        if (eac == null) {
            return;
        }

        // CyLogger.getLogger().debug("HEE: Got edgeAppearanceCalculator: " + eac);		
        ShapePaletteInfoGenerator palGen = CytoscapeEditorFactory.INSTANCE.createShapePaletteInfoGenerator();

        // CyLogger.getLogger().debug("HEE: Got edge target arrow calculator: " + edgeCalc);
        // MLC 05/10/07 BEGIN:
        //        Iterator<ShapePaletteInfo> spEntries = palGen.buildShapePaletteInfo(eac,
        //                                                                            new byte[] {
        //                                                                                VizMapUI.EDGE_TGTARROW
        //                                                                            },
        //                                                                            controllingAttribute,
        //                                                                            this,
        //                                                                            null);
        Iterator<ShapePaletteInfo> spEntries = palGen.buildShapePaletteInfo(eac,
                                                                            new VisualPropertyType[] {
                                                                                // VisualPropertyType.EDGE_TGTARROW
										// MLC 10/20/07:
										VisualPropertyType.EDGE_TGTARROW_SHAPE
                                                                            },
                                                                            controllingAttribute,
                                                                            this,
                                                                            null);

        // MLC 05/10/07 END.
        if (!spEntries.hasNext()) {
            getShapePalette().addShape(controllingAttribute,
                                       "DirectedEdge",
                                       // MLC 05/10/07:
            // new CytoShapeIcon(Arrow.BLACK_DELTA),
            // MLC 05/10/07:
            new CytoShapeIcon(new Arrow(ArrowShape.DELTA, Color.BLACK)),
                                       "Directed Edge",
                                       getDefaultEdgePaletteItemDragCursorSetter());
        } else {
            while (spEntries.hasNext()) {
                ShapePaletteInfo spi = spEntries.next();
                // CyLogger.getLogger().debug("   edge palette info = " + spi);
                getShapePalette().addShape(spi.getControllingAttributeName(),
                                           spi.getKey(),
                                           // MLC 05/10/07:
                // new CytoShapeIcon((Arrow) spi.getValue(VizMapUI.EDGE_TGTARROW)),
                // MLC 05/10/07:
                // MLC 10/20/07:
                // new CytoShapeIcon((Arrow) spi.getValue(VisualPropertyType.EDGE_TGTARROW)),
                // MLC 10/20/07:
                new CytoShapeIcon(arrowShapeToArrow((ArrowShape) spi.getValue(VisualPropertyType.EDGE_TGTARROW_SHAPE))),
                                           // generateSmarterNameOfEntry(spi.getKey()));
                spi.getKey(),
                                           getDefaultEdgePaletteItemDragCursorSetter());
            }
        }
    }

 
    protected void generateNodePaletteEntries(String controllingAttribute) {
        NodeAppearanceCalculator nac = Cytoscape.getVisualMappingManager()
                                                .getVisualStyle()
                                                .getNodeAppearanceCalculator();

        if (nac == null) {
            return;
        }

        ShapePaletteInfoGenerator palGen = CytoscapeEditorFactory.INSTANCE.createShapePaletteInfoGenerator();

        // MLC 05/10/07 BEGIN:
        //        byte[]                     calcsToUse = new byte[] {
        //                                                    VizMapUI.NODE_COLOR,
        //                                                    VizMapUI.NODE_SHAPE,
        //                                                    VizMapUI.NODE_SIZE
        //                                                };
        VisualPropertyType[] calcsToUse = new VisualPropertyType[] {
                                              VisualPropertyType.NODE_FILL_COLOR,
                                              VisualPropertyType.NODE_SHAPE,
                                              VisualPropertyType.NODE_SIZE
                                          };

        // MLC 05/10/07 END.
        Iterator<ShapePaletteInfo> spEntries = palGen.buildShapePaletteInfo(nac,
                                                                            calcsToUse,
                                                                            controllingAttribute,
                                                                            this,
                                                                            new ConnectorNodeIgnorer());

        if (!spEntries.hasNext()) {
            getShapePalette().addShape(controllingAttribute,
                                       "DefaultNode",
                                       new CytoShapeIcon((NodeShape) (nac.getDefaultAppearance()
                                                                         .get(VisualPropertyType.NODE_SHAPE)),
                                                         (Color) (nac.getDefaultAppearance()
                                                                     .get(VisualPropertyType.NODE_FILL_COLOR))),
                                       // BEGIN 06/30/07 END.
            "Add a Node",
                                       null);
        } else {
            while (spEntries.hasNext()) {
                ShapePaletteInfo spi = spEntries.next();

                // CyLogger.getLogger().debug("   node palette entry = " + spi);
                // MLC 05/10/07 BEGIN:
                //                Color nodeColor = (Color) spi.getValue(VizMapUI.NODE_COLOR);
                //                byte  nodeShape = (Byte) spi.getValue(VizMapUI.NODE_SHAPE);
                //                int   nodeSize  = (int) ((Double) spi.getValue(VizMapUI.NODE_SIZE)).longValue();
                Color     nodeColor = (Color) spi.getValue(VisualPropertyType.NODE_FILL_COLOR);
                NodeShape nodeShape = (NodeShape) spi.getValue(VisualPropertyType.NODE_SHAPE);
                int       nodeSize  = (int) ((Double) spi.getValue(VisualPropertyType.NODE_SIZE)).longValue();
                // MLC 05/10/07 END.
                getShapePalette().addShape(spi.getControllingAttributeName(),
                                           spi.getKey(),
                                           new CytoShapeIcon(
                                                             nodeShape,
                                                             nodeColor,
                                                             new Dimension(nodeSize,
                                                                           nodeSize)),
                                           spi.getKey(),
                                           null);
            }
        }
    }

    public void resetHyperEdgeClipboard() {
        _hyperEdgeClipboard.clear();
    }

    public void addToHyperEdgeClipboard(HyperEdge he) {
        _hyperEdgeClipboard.add(he);
        // MLC 01/15/07:
        // HEUtils.log("HyperEdge Clipboard is: ");

        // for (HyperEdge clipHE : _hyperEdgeClipboard) {
        //    HEUtils.log("   " + HEUtils.toString(clipHE));
        //}
    }

    public Iterator<HyperEdge> getHyperEdgeClipboardEntries() {
        return HEUtils.buildUnmodifiableCollectionIterator(_hyperEdgeClipboard);
    }

    private void generateHyperEdgePaletteEntries() {
        HyperEdgePaletteItemDragCursorSetter cursorSetter = new HyperEdgePaletteItemDragCursorSetter();

        // MLC 12/26/06 BEGIN:
        // Add Reaction:
        // MLC 1/06/07:
        // ImageIcon icon = MiscGUI.loadImageIcon("addReaction.gif", null,
        // MLC 1/06/07:
        ImageIcon icon = MiscGUI.loadImageIcon("addReaction.png", null,
                                               IMAGE_REL_LOC,
                                               cytoscape.hyperedge.editor.HyperEdgeEditorPlugin.class);

        if (icon != null) {
            getShapePalette().addShape(COMPLEX_TYPE,
                                       REACTION,
                                       new CytoShapeIcon(icon.getImage()),
                                       "Add Reaction",
                                       null); // reaction can be dropped anywhere
        }

        // MLC 12/26/06 END.
        // Add Product:
        icon = MiscGUI.loadImageIcon("add-product.png", null, // new Dimension(28, 35),
                                     IMAGE_REL_LOC,
                                     cytoscape.hyperedge.editor.HyperEdgeEditorPlugin.class);

        if (icon != null) {
            getShapePalette().addShape(COMPLEX_TYPE,
                                       EdgeTypeMap.PRODUCT,
                                       new CytoShapeIcon(icon.getImage()),
                                       "Add Product",
                                       cursorSetter);
        }

        // Add Substrate:
        icon = MiscGUI.loadImageIcon("add-substrate.png", null, // new Dimension(28, 35),
                                     IMAGE_REL_LOC,
                                     cytoscape.hyperedge.editor.HyperEdgeEditorPlugin.class);

        if (icon != null) {
            getShapePalette().addShape(COMPLEX_TYPE,
                                       EdgeTypeMap.SUBSTRATE,
                                       new CytoShapeIcon(icon.getImage()),
                                       "Add Substrate",
                                       cursorSetter);
        }

        // Add Inhibiting Mediator:
        icon = MiscGUI.loadImageIcon("add-inhibiting-mediator.png", null,
                                     // new Dimension(28, 35),
        IMAGE_REL_LOC, cytoscape.hyperedge.editor.HyperEdgeEditorPlugin.class);

        if (icon != null) {
            getShapePalette().addShape(COMPLEX_TYPE,
                                       EdgeTypeMap.INHIBITING_MEDIATOR,
                                       new CytoShapeIcon(icon.getImage()),
                                       "Add Inhibiting Mediator",
                                       cursorSetter);
        }

        // Add Activating Mediator:
        icon = MiscGUI.loadImageIcon("add-activating-mediator.png", null,
                                     // new Dimension(28, 35),
        IMAGE_REL_LOC, cytoscape.hyperedge.editor.HyperEdgeEditorPlugin.class);

        if (icon != null) {
            getShapePalette().addShape(COMPLEX_TYPE,
                                       EdgeTypeMap.ACTIVATING_MEDIATOR,
                                       new CytoShapeIcon(icon.getImage()),
                                       "Add Activating Mediator",
                                       cursorSetter);
        }

        // MLC 12/26/06 BEGIN:
        //        // Reaction is not a Node or an Edge:
        //        // icon = MiscGUI.loadImageIcon("reaction.png",
        //        icon = MiscGUI.loadImageIcon("addReaction.gif",
        //				     null,
        //                                     // new Dimension(54, 35),
        //                                     IMAGE_REL_LOC,
        //                                     cytoscape.hyperedge.editor.HyperEdgeEditorPlugin.class);
        //
        //        if (icon != null) {
        //            getShapePalette().addShape(COMPLEX_TYPE,
        //                                       REACTION,
        //                                       new CytoShapeIcon(icon.getImage()),
        //                                       "Reaction",
        //                                       null); // reaction can be dropped anywhere
        //        }
        // MLC 12/26/06 END.
        //        if (CytoscapeEditorManager.isEditingEnabled()) {
        //            shapePalette.showPalette();
        //        }
    }

    //    private String generateSmarterNameOfEntry(String entryKey) {
    //        // remove "hyperedge." from names:
    //        String toMatch = "hyperedge.";
    //
    //        if (entryKey.startsWith(toMatch)) {
    //            return entryKey.substring(toMatch.length());
    //        }
    //
    //        return entryKey;
    //    }

    /**
     * specialized initialization code for editor, called by
     * CytoscapeEditorManager when a new editor is built.
     * gets the mappings from the visual style and uses them to construct
     * shapes for the palette
     *
     * @param args
     *            an arbitrary list of arguments passed to initialization
     *            routine. Not used in this editor
     */

    // overrides DefaultCytoscapeEditor.initializeControls():
    public void initializeControls(List args) {
        // MLC 05/11/07 BEGIN:
        // TODO: This needs to be untangled. We use
        // alreadyInitializing as a bandaid for now.  The problem is
        // that this method can be called from several different
        // places and events. One of these events originates from
        // VisualMappingManager.setVisualStyle().  In this case, we
        // get infinite loop:
        // VisualMappingManager.setVisualStyle()
        //   CytoscapeEditorManagerSupport.stateChanged()
        //     CytoscapeEditorManagerSupport.updateEditorPalette()
        //        HyperEdgeEditor.initializeControls()
        //          BioChemicalReactionVisualStyle.setupVisualStyle()
        //            BioChemicalReactionVisualStyle.primSetupStyle()
        //              VisualMappingManager.setVisualStyle() [loop]
        // To get this loop:
        //   1) bring up a clean Cytoscape (with HyperEdgeEditor loaded)
        //   2) click on the Editor tab and drop a RegularNode into Network0
        //   3) Now load any saved session that has BioChemicalReaction visual style.
        // We need to figure out when BioChemicalReactionVisualStyle should call
        // setVisualStyle and when it shouldn't, then change the code accordingly.
        if (alreadyInitializing) {
            return;
        }

        alreadyInitializing = true;
        // MLC 05/11/07 END.
        // MLC 01/15/07:
        // CyLogger.getLogger().debug("HEE: BEGIN INITIALIZECONTROLS");
        BioChemicalReactionVisualStyle.getVisualStyle()
                                      .setupVisualStyle(false, false);
        super.initializeControls(args);
        alreadyInitializing = false;
        // AJK: 09/29/06 BEGIN
        // do visual style creation at time that editor is created, to
        // accommodated the current
        // visual style potentially being clobbered by other plugins
        // MLC 11/25/06 BEGIN:

        //        shapePalette = CytoscapeEditorManager.getCurrentShapePalette();
        //        shapePalette.clear();

        //        VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        //
        //        CalculatorCatalog    catalog = manager.getCalculatorCatalog();
        //        VisualStyle vizStyle = catalog.getVisualStyle(
        //            BioChemicalReactionVisualStyle.BIOCHEMICAL_REACTION_VISUAL_STYLE);
        //        VisualStyle baseStyle = catalog.getVisualStyle(
        //						       BioChemicalReactionVisualStyle.BIOCHEMICAL_REACTION_VISUAL_STYLE);
        //	if ((baseStyle == null) ||
        //	    (!(baseStyle instanceof BioChemicalReactionVisualStyle))) {
        //            String expDescript = "Cannot find BioChemicalReaction Visual Style.";
        //            String title = "Cannot build palette for HyperEdgeEditor";
        //            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
        //                                          expDescript,
        //                                          title,
        //                                          JOptionPane.PLAIN_MESSAGE);
        //            return;
        //	}
        //		CyLogger.getLogger().debug ("Got visual Style from catalog: " + catalog 
        //				+ " = " + vizStyle);
        //        if (vizStyle == null) {
        //            if (bcrVisualStyle == null) {
        //                bcrVisualStyle = new BioChemicalReactionVisualStyle();
        //            }
        //
        //            vizStyle = bcrVisualStyle.createVizMapper();
        //        } else {
        //            CyLogger.getLogger().debug("Calling defineVisualStyle for: " + vizStyle);
        //            bcrVisualStyle.defineVisualStyle(vizStyle, manager, catalog);
        //        }
        //
        //        if (vizStyle == null) {
        //            String expDescript = "Cannot find BioChemicalReaction Visual Style.";
        //            String title = "Cannot build palette for HyperEdgeEditor";
        //            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
        //                                          expDescript,
        //                                          title,
        //                                          JOptionPane.PLAIN_MESSAGE);
        //
        //            return;
        //        } else {
        //            if (!regeneratedVizStyle) {
        //                regeneratedVizStyle = true;
        //                bcrVisualStyle.defineVisualStyle(vizStyle, manager, catalog);
        //            }
        // MLC 11/25/06 END.
        // AJK: 09/29/06 END

        // AJK: 06/10/06 BEGIN
        // no longer rebuilding shape palette, just its shape pane
        // shapePalette = new ShapePalette();

        // AJK: 06/10/06 END
        // String controllingEdgeAttribute = this.getControllingEdgeAttribute();

        // MLC 11/25/06:
        // NodeAppearanceCalculator nac = vizStyle.getNodeAppearanceCalculator();
        // MLC 11/25/06:
        // MLC 11/27/06 BEGIN:
        /*
        NodeAppearanceCalculator nac = bcrVisualStyle.getNodeAppearanceCalculator();
        
        //                CyLogger.getLogger().debug("NodeAppearanceCalculator for visual style: "
        //                                + vizStyle + " is " + nac);
        if (nac == null) {
            String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Color to CytoscapeEditorManager.NODE_TYPE attribute.";
            String title = "Cannot build palette for HyperEdgeEditor";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          expDescript,
                                          title,
                                          JOptionPane.PLAIN_MESSAGE);
        
            return;
        }
        
        //        GenericNodeColorCalculator nfill = (GenericNodeColorCalculator) nac.getNodeFillColorCalculator();
        Calculator nfill = nac.getCalculator(VisualPropertyType.NODE_FILL_COLOR);
        CyLogger.getLogger().debug("NodeColorCalculator for visual style: " +
                      bcrVisualStyle + " is " + nfill);
        
        if (nfill == null) {
            String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Color to CytoscapeEditorManager.NODE_TYPE attribute.";
            String title = "Cannot build palette for HyperEdgeEditor";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          expDescript,
                                          title,
                                          JOptionPane.PLAIN_MESSAGE);
        
            return;
        }
        
        Vector          mappings = nfill.getMappings();
        DiscreteMapping dfill = null;
        
        for (int i = 0; i < mappings.size(); i++) {
            DiscreteMapping dfillCandidate = (DiscreteMapping) mappings.get(i);
            String          attr = dfillCandidate.getControllingAttributeName();
        
            if (attr.equals(CytoscapeEditorManager.NODE_TYPE)) {
                dfill = dfillCandidate;
        
                break;
            }
        }
        
        CyLogger.getLogger().debug("DiscreteMapping for visual style: " + bcrVisualStyle +
                      " is " + dfill);
        
        if (dfill == null) {
            String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Shape to CytoscapeEditorManager.NODE_TYPE attribute.";
            String title = "Cannot build palette for HyperEdgeEditor";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          expDescript,
                                          title,
                                          JOptionPane.PLAIN_MESSAGE);
        
            return;
        }
        
        // GenericNodeShapeCalculator nshape = (GenericNodeShapeCalculator) nac.getNodeShapeCalculator();
        Calculator nshape = nac.getCalculator(VisualPropertyType.NODE_SHAPE);
        
        if (nshape == null) {
            String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Color to CytoscapeEditorManager.NODE_TYPE attribute.";
            String title = "Cannot build palette for HyperEdgeEditor";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          expDescript,
                                          title,
                                          JOptionPane.PLAIN_MESSAGE);
        
            return;
        }
        
        mappings = nshape.getMappings();
        
        DiscreteMapping dshape = null;
        
        for (int i = 0; i < mappings.size(); i++) {
            DiscreteMapping dshapeCandidate = (DiscreteMapping) mappings.get(i);
            String          attr = dshapeCandidate.getControllingAttributeName();
        
            if (attr.equals(CytoscapeEditorManager.NODE_TYPE)) {
                dshape = dshapeCandidate;
        
                break;
            }
        }
        
        if (dshape == null) {
            String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Node Shape to CytoscapeEditorManager.NODE_TYPE attribute.";
            String title = "Cannot build palette for HyperEdgeEditor";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          expDescript,
                                          title,
                                          JOptionPane.PLAIN_MESSAGE);
        
            return;
        }
        */

        // MLC 11/27/06 END.
        //	EdgeAppearanceCalculator eac = vizStyle.getEdgeAppearanceCalculator();
        //	CyLogger.getLogger().debug("Got edgeAppearanceCalculator: " + eac);
        //        
        //	if (eac == null) {
        //	    String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Edge Target Arrow to an attribute.";
        //	    String title = "Cannot build palette for HyperEdgeEditor: no edge appearance calculator";
        //	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
        //					  title, JOptionPane.PLAIN_MESSAGE);
        //	    
        //	    return;
        //	}
        //        
        //	GenericEdgeArrowCalculator edgeCalc = (GenericEdgeArrowCalculator) eac.getEdgeTargetArrowCalculator();
        //	CyLogger.getLogger().debug("Got edge target arrow calculator: " + edgeCalc);
        //        
        //	if (edgeCalc == null) {
        //	    String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Edge Target Arrow to an attribute.";
        //	    String title = "Cannot build palette for HyperEdgeEditor: no edge arrow calculator";
        //	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
        //					  title, JOptionPane.PLAIN_MESSAGE);
        //	    
        //	    return;
        //	}
        //        
        //	Vector edgeMappings = edgeCalc.getMappings();
        //        
        //	DiscreteMapping dArrow = null;
        //        
        //	for (int i = 0; i < edgeMappings.size(); i++) {
        //	    DiscreteMapping dArrowCandidate = (DiscreteMapping) edgeMappings.get(i);
        //	    String attr = dArrowCandidate.getControllingAttributeName();
        //	    
        //	    //			CyLogger.getLogger().debug("checking attribute: " + attr
        //	    //					+ " against controlling attribute: "
        //	    //					+ controllingEdgeAttribute);
        //	    if (attr.equals(controllingEdgeAttribute)) {
        //		dArrow = dArrowCandidate;
        //		CyLogger.getLogger().debug("Got edge mapping: " + dArrow);
        //		
        //		break;
        //	    }
        //	}
        //        
        //	if (dArrow == null) {
        //	    String expDescript = "Cannot build palette.  You need to set up a Visual Style that maps Edge Target Arrow to an attribute.";
        //	    String title = "Cannot build palette for HyperEdgeEditor";
        //	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
        //					  title, JOptionPane.PLAIN_MESSAGE);
        //	    
        //	    return;
        //	}
        //        
        //	//		CyLogger.getLogger().debug("adding edge arrows to palette");
        //	Arrow edgeTargetArrow;
        //	String[] EdgeTypes = new String[] { ACTIVATION, INHIBITION, CATALYSIS };
        //        
        //	for (int i = 0; i < EdgeTypes.length; i++) {
        //	    //			CyLogger.getLogger().debug("getting map value for edge type: "
        //	    //					+ EdgeTypes[i]);
        //	    if (dArrow.getMapValue(EdgeTypes[i]) != null) {
        //		edgeTargetArrow = (Arrow) dArrow.getMapValue(EdgeTypes[i]);
        //	    } else {
        //		edgeTargetArrow = eac.getDefaultEdgeTargetArrow();
        //	    }
        //	    
        //	    //			CyLogger.getLogger().debug("Addng shape for EdgeType " + EdgeTypes[i]
        //	    //					+ " = " + edgeTargetArrow);
        //	    shapePalette.addShape(EDGE_TYPE, EdgeTypes[i],
        //				  new CytoShapeIcon(edgeTargetArrow), EdgeTypes[i]);
        //	}
        //        byte nodeShape = ((Byte) dshape.getMapValue(EdgeTypeMap.PRODUCT)).byteValue();
        //        Color nodeColor = (Color) dfill.getMapValue(EdgeTypeMap.PRODUCT);
        //        shapePalette.addShape(BioChemicalReactionVisualStyle.CytoscapeEditorManager.NODE_TYPE,
        //            EdgeTypeMap.PRODUCT, new CytoShapeIcon(nodeShape, nodeColor),
        //            EdgeTypeMap.PRODUCT);
        //
        //        nodeShape = ((Byte) dshape.getMapValue(EdgeTypeMap.SUBSTRATE)).byteValue();
        //        nodeColor = (Color) dfill.getMapValue(EdgeTypeMap.SUBSTRATE);
        //        shapePalette.addShape(BioChemicalReactionVisualStyle.CytoscapeEditorManager.NODE_TYPE,
        //            EdgeTypeMap.SUBSTRATE, new CytoShapeIcon(nodeShape, nodeColor),
        //            EdgeTypeMap.SUBSTRATE);
        //
        //        nodeShape = ((Byte) dshape.getMapValue(EdgeTypeMap.MEDIATOR)).byteValue();
        //        nodeColor = (Color) dfill.getMapValue(EdgeTypeMap.MEDIATOR);
        //        shapePalette.addShape(BioChemicalReactionVisualStyle.CytoscapeEditorManager.NODE_TYPE,
        //            EdgeTypeMap.MEDIATOR,
        //            new CytoShapeIcon(nodeShape, nodeColor),
        //            EdgeTypeMap.MEDIATOR);
        //        super.initializeControls(null);
    }

    // MLC 12/18/07 BEGIN:
    //    // This can be removed if/when HyperEdges are added to the Cytoscape core.
    //    // overrides BasicCytoscapeEditor.addNodeContextMenuItems():
    //    public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
    //        removeExistingDeleteMenuItemIfNecessary(menu);
    //        menu.add(new HyperEdgeDeleteAction(nodeView.getNode()));
    //    }
    //
    //    // This can be removed if/when HyperEdges are added to the Cytoscape core.
    //    // overrides BasicCytoscapeEditor.addEdgeContextMenuItems():
    //    public void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu menu) {
    //        removeExistingDeleteMenuItemIfNecessary(menu);
    //        menu.add(new HyperEdgeDeleteAction(edgeView.getEdge()));
    //    }
    // MLC 12/18/07 END.

    /**
     * Ensure that a given node id has no existing node.
     * If it does, return a similar id that doesn't have any existing node.
     */
    public CyNode ensureUniqueNode(String id) {
        if (id == null) {
            HEUtils.throwIllegalArgumentException("HyperEdgeEditor.ensureUniqueNode(): id was null!");
        }

        CyNode node            = Cytoscape.getCyNode(id, false);
        int    iteration_limit = 100;
        String new_id          = id;
        String time_frag       = null;

        while (node != null) {
            // find a unique id:
            // TODO: why not use System.currentTimeMillis()?
            Date d1 = new java.util.Date();
            time_frag = Long.toString(d1.getTime());
            // TODO: This is strange--we are actually continually
            // building on the node name vs trying another simpler
            // node name.
            new_id += ("_" + time_frag.substring(time_frag.length() - 3)); // append last 4 time stamp to node name
            node = Cytoscape.getCyNode(new_id, false);
            iteration_limit--;

            // check for unlikely error condition where we couldn't generate a
            // unique node after a number of tries
            if (iteration_limit <= 0) {
                String expDescript = "Cytoscape Editor cannot generate a unique node for this network.  A serious internal error has occurred.  Please file a bug report at http://www.cytoscape.org.";
                String title = "Cannot generate a unique node";
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                              expDescript,
                                              title,
                                              JOptionPane.PLAIN_MESSAGE);
                HEUtils.throwIllegalStateException("HyperEdgeEditor.ensureUniqueNode(): Couldn't find a unique varient!");
            }
        }

        return Cytoscape.getCyNode(new_id, true);
    }

    public void determineAction(DGraphView view, String attributeValue,
                                Point2D location) {
        if (REACTION.equals(attributeValue)) {
            // create a HyperEdge
            addReaction(view, location);
        } else if (EdgeTypeMap.ACTIVATING_MEDIATOR.equals(attributeValue)) {
            addMediator(view, location, EdgeTypeMap.ACTIVATING_MEDIATOR);
        } else if (EdgeTypeMap.INHIBITING_MEDIATOR.equals(attributeValue)) {
            addMediator(view, location, EdgeTypeMap.INHIBITING_MEDIATOR);
        } else if (EdgeTypeMap.PRODUCT.equals(attributeValue)) {
            addProduct(view, location);
        } else if (EdgeTypeMap.SUBSTRATE.equals(attributeValue)) {
            addSubstrate(view, location);
        }
    }

    // instead of adding a normal edge, we will add an
    // edge to a HyperEdge when node_1 or node_2 are ConnectorNodes.
    // overrides BasicCytoscapeEditor.addEdge()
    public CyEdge addEdge(Node node_1, Node node_2, String attribute,
                          Object attribute_value, boolean create,
                          String edgeType) {
        // MLC 01/15/07:
        // HEUtils.log("addEdge source: " + HEUtils.toString(node_1) +
        //            " target: " + HEUtils.toString(node_2));
        if (!create || !(attribute_value instanceof String)) {
            // we're not creating a new edge, use superclass way of doing things:
            return super.addEdge(node_1, node_2, attribute, attribute_value,
                                 create, edgeType);
        }

        String    attrValue = (String) attribute_value;
        CyNode    node1    = (CyNode) node_1;
        CyNode    node2    = (CyNode) node_2;
        CyNetwork net      = Cytoscape.getCurrentNetwork();
        HyperEdge sourceHE = null;
        HyperEdge targetHE = null;

        // don't just use getHyperEdgeConnectorNode() because we
        // need to check if in the right network:
        if (_manager.isConnectorNode(node1, net)) {
            sourceHE = _manager.getHyperEdgeForConnectorNode(node1);
        }

        // don't just use getHyperEdgeConnectorNode() because we
        // need to check if in the right network:
        if (_manager.isConnectorNode(node2, net)) {
            targetHE = _manager.getHyperEdgeForConnectorNode(node2);
        }

        if ((sourceHE == null) && (targetHE == null)) {
            // make regular edge connection:
            return super.addEdge(node_1, node_2, attribute, attribute_value,
                                 create, edgeType);
        }

        CyEdge newEdge = null;

        if ((sourceHE != null) && (targetHE != null)) {
            // create a shared connection:
            // MLC 01/15/07:
            // HEUtils.log("ADDING SHARED EDGE");
            // MLC 01/09/07:
            newEdge = sourceHE.connectHyperEdges(targetHE, attrValue);
        } else if (sourceHE != null) {
            // MLC 01/15/07:
            // HEUtils.log("ADDING EDGE to HyperEdge");
            newEdge = sourceHE.addEdge(node2, attrValue);
        } else if (targetHE != null) {
            // MLC 01/15/07:
            // HEUtils.log("ADDING EDGE to HyperEdge");
            newEdge = targetHE.addEdge(node1, attrValue);
        }

        // What should we do if the attribute name doesn't match
        // Semantics.INTERACTION?:
        //	if (!Semantics.INTERACTION.equals (attribute)) {
        //	}
        return newEdge;
    }

    private void addReaction(DGraphView netView, Point2D location) {
        // MLC 01/15/07:
        // CyLogger.getLogger().debug("Adding reaction at position " + location);

        // Now create the HyperEdges:
        CyNode s1 = ensureUniqueNode("S");
        CyNode m1 = ensureUniqueNode("M");
        CyNode p1 = ensureUniqueNode("P");

        // MLC 08/07/06 BEGIN:
        //        addAttribute(s1, CytoscapeEditorManager.NODE_TYPE, EdgeTypeMap.SUBSTRATE);
        //        addAttribute(m1, CytoscapeEditorManager.NODE_TYPE,
        //            EdgeTypeMap.ACTIVATING_MEDIATOR);
        //        addAttribute(p1, CytoscapeEditorManager.NODE_TYPE, EdgeTypeMap.PRODUCT);
        // MLC 08/07/06 END.
        // CyNetwork net = Cytoscape.getCurrentNetwork();
        CyNetwork net = (CyNetwork) netView.getGraphPerspective();

        // will add he, p1, m1, and s1 to net:
        HyperEdge he = _factory.createHyperEdge(s1, EdgeTypeMap.SUBSTRATE, m1,
                                                EdgeTypeMap.ACTIVATING_MEDIATOR,
                                                p1, EdgeTypeMap.PRODUCT, net);

        addNewHyperEdgeTooltips(he, netView);
        //        addAttribute(he.getConnectorNode(),
        //                     CytoscapeEditorManager.NODE_TYPE,
        //                     BioChemicalReactionVisualStyle.HE_CONNECTOR_NODE);
        // _manager.addToGraphPerspective(net, he);
        // CyNetworkView cnv = Cytoscape.getCurrentNetworkView();
        net.unselectAllNodes();

        List<CyNode> l = new ArrayList<CyNode>(4);
        l.add(s1);
        l.add(m1);
        l.add(p1);
        l.add(he.getConnectorNode());
        net.setSelectedNodeState(l, true);

        positionHyperEdge(he, location, netView);
        netView.addNodeContextMenuListener(this);
        ((CyNetworkView) netView).redrawGraph(true, true);
        //        // Why are doing this when GraphPerspectiveChangeListener will
        //        // handle node/edge changes?:
        // Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
        //                             CytoscapeEditorManager.CYTOSCAPE_EDITOR,
        //                             net);
    }

    private void addMediator(DGraphView view, Point2D location,
                             String mediatorType) {
        NodeView cNodeView = getConnectorNodeNodeView(location, view);

        if (cNodeView == null) {
            return;
        }

        // everything is set, do the op:
        // MLC 01/15/07:
        // CyLogger.getLogger().debug("addMediator: adding a real mediator");
        HyperEdge he = _manager.getHyperEdgeForConnectorNode((CyNode) cNodeView.getNode());
        //        CyNode m1 = ensureUniqueNode("M");
        //        addAttribute(m1, CytoscapeEditorManager.NODE_TYPE, mediator_type);
        //        he.addEdge(m1, mediator_type);
        //
        //        positionMediators(he, cNodeView, cNodeView.getGraphView());
        //
        //        CyNetwork net = (CyNetwork) (cNodeView.getGraphView()
        //                                              .getGraphPerspective());
        //        net.unselectAllNodes();
        //
        //        List l = new ArrayList(1);
        //        l.add(m1);
        //        net.setSelectedNodeState(l, true);
        //
        //        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
        //            CytoscapeEditorManager.CYTOSCAPE_EDITOR, net);
        addAndPositionMediator(he,
                               cNodeView,
                               cNodeView.getGraphView(),
                               mediatorType);
        ((CyNetworkView) view).redrawGraph(true, true);
    }

    private void addProduct(DGraphView view, Point2D location) {
        NodeView cNodeView = getConnectorNodeNodeView(location, view);

        if (cNodeView == null) {
            return;
        }

        // everything is set, do the op:
        // MLC 01/15/07:
        // CyLogger.getLogger().debug("addProduct: adding a real product");
        HyperEdge he = _manager.getHyperEdgeForConnectorNode((CyNode) cNodeView.getNode());
        //        CyNode p1 = ensureUniqueNode("P");
        //        addAttribute(p1, CytoscapeEditorManager.NODE_TYPE, EdgeTypeMap.PRODUCT);
        //        he.addEdge(p1, EdgeTypeMap.PRODUCT);
        //        positionProducts(he, cNodeView, cNodeView.getGraphView());
        //
        //        CyNetwork net = (CyNetwork) (cNodeView.getGraphView()
        //                                              .getGraphPerspective());
        //        net.unselectAllNodes();
        //
        //        List l = new ArrayList(1);
        //        l.add(p1);
        //        net.setSelectedNodeState(l, true);
        //
        //        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
        //            CytoscapeEditorManager.CYTOSCAPE_EDITOR, net);
        addAndPositionProduct(he,
                              cNodeView,
                              cNodeView.getGraphView());
        ((CyNetworkView) view).redrawGraph(true, true);
    }

    private void addSubstrate(DGraphView view, Point2D location) {
        NodeView cNodeView = getConnectorNodeNodeView(location, view);

        if (cNodeView == null) {
            return;
        }

        // everything is set, do the op:
        // MLC 01/15/07:
        // CyLogger.getLogger().debug("addSubstrate: adding a real substrate");
        HyperEdge he = _manager.getHyperEdgeForConnectorNode((CyNode) cNodeView.getNode());

        // CyNode s1 = ensureUniqueNode("S");
        // addAttribute(s1, CytoscapeEditorManager.NODE_TYPE, EdgeTypeMap.SUBSTRATE);
        // he.addEdge(s1, EdgeTypeMap.SUBSTRATE);

        // positionSubstrates(he, cNodeView, cNodeView.getGraphView());
        addAndPositionSubstrate(he,
                                cNodeView,
                                cNodeView.getGraphView());
        ((CyNetworkView) view).redrawGraph(true, true);
    }

    // There are four cases of dropping an edge onto another target edge.
    // 1)The target edge is not part of a HyperEdge, the target edge's source
    // is a HyperEdge, 3) the target edge's target is a HyperEdge, and 4)
    // the target edge is a shared edge in between two HyperEdges.
    // NOTE: We currently can't handle case 4) (see below).
    //
    // Here's an example of case 1), if we drop an edge palette item onto C (source) and then onto the
    //     edge in between A & B (target):
    //       A--->B
    //       C--->D
    // Then we would get:
    //
    //        A---.-->B
    //            ^
    //            |
    //            C
    //
    // Here's an example of case 3)--drop an edge palette item (e.g.,
    // hyperedge.mediator.activating) onto M2 (source) and then
    // onto the edge in between M1 and the ConnectorNode in:
    //
    //        S--.-->P
    //           ^      M2
    //           |
    //           M1
    //     Then we would get:
    //
    //        S--.-->P
    //           ^
    //           |
    //           .<----M2
    //           ^
    //           |
    //           M1
    // in this case, the edge to M1 and connecting to the S-->P hyperedge
    // ConnectorNode will have the interaction type of the original edge
    // connecting M1. The edge to M2 will have attributeValue as its
    // interaction type.
    // TODO: This need refactoring!
    public void convertEdgeIntoHyperEdge(Point2D location, CyEdge edge,
                                         CyNode addedNode,
                                         String attributeName,
                                         String attributeValue,
                                         CyNetworkView netView) {
        CyNetwork        net          = netView.getNetwork();
        String           oldEdgeIType = HEUtils.getEdgeInteractionType(edge);
        HyperEdgeManager heMan        = HyperEdgeFactory.INSTANCE.getHyperEdgeManager();
        HyperEdge        sourceHe     = heMan.getHyperEdgeForConnectorNode((CyNode) edge.getSource());
        HyperEdge        targetHe     = heMan.getHyperEdgeForConnectorNode((CyNode) edge.getTarget());

        // MLC 01/09/07:
        HyperEdge addedNodeHe = heMan.getHyperEdgeForConnectorNode(addedNode);

        // It's possible edge connects to nodes that are in a
        // hyperedge but the edge is not a member of the hyperedge in
        // net, check for source and target:
        if ((sourceHe != null) &&
            (!sourceHe.hasEdge(edge) || !sourceHe.inNetwork(net))) {
            sourceHe = null;
        }

        if ((targetHe != null) &&
            (!targetHe.hasEdge(edge) || !targetHe.inNetwork(net))) {
            targetHe = null;
        }

        HyperEdge newHe = null;

        if ((sourceHe == null) && (targetHe == null)) {
            // edge is a regular edge:
            // Now check if addedNode is a ConnectorNode. In this case
            // we will be conntecting two hyperedges:
            if (addedNodeHe == null) {
                // addedNode is just a regular node:
                newHe = HyperEdgeFactory.INSTANCE.createHyperEdge((CyNode) edge.getSource(),
                                                                  EdgeTypeMap.SUBSTRATE,
                                                                  addedNode,
                                                                  attributeValue,
                                                                  (CyNode) edge.getTarget(),
                                                                  // MLC 01/09/07:
                // EdgeTypeMap.PRODUCT,
                // MLC 01/09/07:
                oldEdgeIType, net);
            } else {
                // addedNode is a ConnectorNode:
                // MLC 01/09/07 BEGIN:
                // Since we'll end up with two HyperEdges sharing an edge, make sure that
                // addedNodeHe is only in this Network:
                if (!isLegalHyperEdge(addedNodeHe)) {
                    return;
                }

                // MLC 01/09/07 END.
                // first make a two edged hyperedge, then connect to other hyperedge:
                newHe = HyperEdgeFactory.INSTANCE.createHyperEdge((CyNode) edge.getSource(),
                                                                  EdgeTypeMap.SUBSTRATE,
                                                                  (CyNode) edge.getTarget(),
                                                                  oldEdgeIType,
                                                                  net);
                newHe.connectHyperEdges(addedNodeHe, attributeValue);
            }

            // now delete the original edge:
            net.removeEdge(Cytoscape.getRootGraph().getIndex(edge),
                           false);
        } else if ((sourceHe != null) && (targetHe != null)) {
            // we have an edge that is a shared edge in between 2 hyperedges:
            // MLC 01/31/07 BEGIN:
            // we can squeek by if the added Node is not a connectorNode:
            if (addedNodeHe == null) {
                // get the sourceHe and new He connected:
                newHe = createHyperEdgeWithOneRegularNode(addedNode,
                                                          attributeValue,
                                                          sourceHe,
                                                          oldEdgeIType, net);

                // now connect new He and targetHe:
                newHe.connectHyperEdges(targetHe, oldEdgeIType);
                // now delete the original edge (this will remove from
                // targetHe also):
                sourceHe.removeEdge(edge);
            } else {
                // MLC 01/31/07 END.
                // MLC 01/09/07 BEGIN:
                // NOTE: because the HyperEdge API doesn't support
                //       HyperEdges with less then 2 edges and doesn't
                //       allow regular HyperEdge constructors passing in a
                //       ConnectorNode, we can't build this structure:
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                              "Can't drop an edge palette entry with a connectorNode source onto a shared edge target--we can't currently build this structure using the HyperEdge API.");

                return;
                // MLC 01/09/07 END.
                // MLC 01/31/07:
            }
        } else if (sourceHe != null) {
            // source is the hyperedge:
            // MLC 01/31/07 BEGIN:
            // make a new hyperedge with 2 edges and connect the third, shared edge via
            // connectHyperEdges:
            newHe = handleEdgeDropOnHyperEdgeEdge(sourceHe, edge, addedNodeHe,
                                                  addedNode, attributeValue,
                                                  (CyNode) edge.getTarget(),
                                                  oldEdgeIType,
                                                  EdgeTypeMap.SUBSTRATE, net);

            if (newHe == null) {
                return;
            }

            //            // MLC 01/09/07 BEGIN:
            //            // Since we'll end up with two HyperEdges sharing an edge, make sure that
            //            // sourceHe is only in this Network:
            //            if (!isLegalHyperEdge(sourceHe)) {
            //                return;
            //            }
            //
            //            // MLC 01/09/07 END.
            //            // make a new hyperedge with 2 edges and connect the third, shared edge via
            //            // connectHyperEdges:
            //            newHe = HyperEdgeFactory.INSTANCE.createHyperEdge(addedNode,
            //                                                              attributeValue,
            //                                                              (CyNode) edge.getTarget(),
            //                                                              oldEdgeIType,
            //							      net);
            //            sourceHe.connectHyperEdges(newHe, EdgeTypeMap.SUBSTRATE);
            //            // now delete the original edge:
            //            sourceHe.removeEdge(edge);
            // MLC 01/31/07 END.
        } else {
            // target is the hyperedge:
            // MLC 01/31/07 BEGIN:
            // make a new hyperedge with 2 edges and connect the third, shared edge via
            // connectHyperEdges:
            newHe = handleEdgeDropOnHyperEdgeEdge(targetHe, edge, addedNodeHe,
                                                  addedNode, attributeValue,
                                                  (CyNode) edge.getSource(),
                                                  EdgeTypeMap.SUBSTRATE,
                                                  oldEdgeIType, net);

            if (newHe == null) {
                return;
            }

            //            // MLC 01/09/07 BEGIN:
            //            // Since we'll end up with two HyperEdges sharing an edge, make sure that
            //            // targetHe is only in this Network:
            //            if (!isLegalHyperEdge(targetHe)) {
            //                return;
            //            }
            //
            //            // MLC 01/09/07 END.
            //            // make a new hyperedge with 2 edges and connect the third, shared edge via
            //            // connectHyperEdges:
            //            newHe = HyperEdgeFactory.INSTANCE.createHyperEdge(addedNode,
            //                                                              attributeValue,
            //                                                              (CyNode) edge.getSource(),
            //                                                              EdgeTypeMap.SUBSTRATE,
            //                                                              net);
            //            newHe.connectHyperEdges(targetHe, oldEdgeIType);
            //            targetHe.removeEdge(edge);
            // MLC 01/31/07 END.
        }

        addNewHyperEdgeTooltips(newHe, netView);
        positionAndSelectHyperEdge(newHe, netView, location);
    }

    // MLC 01/31/07 BEGIN:
    /*
     * We haved dropped an edge palette item onto the source of 'addedNode' and
     * target 'edgeDroppedOn' and this edge is a HyperEdgeEdge.
     * 'regularNode' is a non-connectorNode node.
     */
    private HyperEdge handleEdgeDropOnHyperEdgeEdge(HyperEdge heDroppedOn,
                                                    CyEdge edgeDroppedOn,
                                                    HyperEdge addedNodeHe,
                                                    CyNode addedNode,
                                                    String addedNodeAttributeValue,
                                                    CyNode regularNode,
                                                    String regularNodeType,
                                                    String sharedEdgeType,
                                                    CyNetwork net) {
        HyperEdge newHe = null;

        // Since we'll end up with two HyperEdges sharing an edge, make sure that
        // sourceHe is only in this Network:
        if (!isLegalHyperEdge(heDroppedOn)) {
            return null;
        }

        if (addedNodeHe != null) {
            // addedNode is a connectorNode--we'll end up with shared
            // edges. make sure that addedNodeHe is only in this
            // Network:
            if (!isLegalHyperEdge(addedNodeHe)) {
                return null;
            }

            newHe = createHyperEdgeWithOneRegularNode(regularNode,
                                                      regularNodeType,
                                                      addedNodeHe,
                                                      addedNodeAttributeValue,
                                                      net);
        } else {
            // addedNode is a regular node.
            newHe = HyperEdgeFactory.INSTANCE.createHyperEdge(addedNode,
                                                              addedNodeAttributeValue,
                                                              regularNode,
                                                              regularNodeType,
                                                              net);
        }

        heDroppedOn.connectHyperEdges(newHe, sharedEdgeType);
        // now delete the original edge:
        heDroppedOn.removeEdge(edgeDroppedOn);

        return newHe;
    }

    /*
     * Now we do some creative construction and connection to avoid
     * API restrictions:
     * First, make a new HyperEdge, but use our one regular node
     * (regularNode) as both endpoints. This is just so we can create
     * the hyperedge, since we need two nodes and we can't use connectorNodes:
     */
    private HyperEdge createHyperEdgeWithOneRegularNode(CyNode regularNode,
                                                        String regularNodeType,
                                                        HyperEdge toConnectTo,
                                                        String toConnectToAttributeValue,
                                                        CyNetwork net) {
        HyperEdge newHe = HyperEdgeFactory.INSTANCE.createHyperEdge(regularNode,
                                                                    regularNodeType,
                                                                    regularNode,
                                                                    "hyperedgeeditor.bogus",
                                                                    net);
        // Now connect up the connectorNode addedNode:
        newHe.connectHyperEdges(toConnectTo, toConnectToAttributeValue);

        // Now newHe has three node, remove the bogus temporary node:
        Iterator<CyEdge> it = newHe.getEdges(regularNode);

        while (it.hasNext()) {
            CyEdge edge = it.next();

            if ("hyperedgeeditor.bogus".equals(HEUtils.getEdgeInteractionType(edge))) {
                newHe.removeEdge(edge);

                break;
            }
        }

        return newHe;
    }

    // MLC 01/31/07 END.
    public void positionAndSelectHyperEdge(HyperEdge he, CyNetworkView netView,
                                           Point2D location) {
        // place the connector node where we clicked on the canvas:
        positionNode(netView,
                     he.getConnectorNode(),
                     location);

        SelectHyperEdgeAction selAct = new SelectHyperEdgeAction(he,
                                                                 netView.getNetwork());
        selAct.actionPerformed(new ActionEvent(this, 1, ""));
    }

    private void addAndPositionSubstrate(HyperEdge he, NodeView cNodeView,
                                         GraphView gv) {
        // GraphPerspective gp = gv.getGraphPerspective();
        CyNode s1 = ensureUniqueNode("S");

        // MLC 08/07/06:
        // addAttribute(s1, CytoscapeEditorManager.NODE_TYPE, EdgeTypeMap.SUBSTRATE);
        Iterator<CyNode> it = he.getNodes(EdgeTypeMap.SUBSTRATE);

        if (!it.hasNext()) {
            // we have no other nodes of this type in the HyperEdge:
            // he.addEdge(s1, EdgeTypeMap.SUBSTRATE);
            addEdgeAndToolTips(gv, he, s1, EdgeTypeMap.SUBSTRATE);
            positionLeftOrRight(he, cNodeView, gv, EdgeTypeMap.SUBSTRATE, "left");
        } else {
            // we have other nodes of this type:
            // adjNode will be the bottom-most substrate to place next to:
            NodeView adjNode = findFurthestNodeViewAlongDimension(it, gv, false);
            // he.addEdge(s1, EdgeTypeMap.SUBSTRATE);
            addEdgeAndToolTips(gv, he, s1, EdgeTypeMap.SUBSTRATE);

            NodeView newNView = gv.getNodeView(s1);
            double   newNodeX = adjNode.getXPosition();

            // ASSUME: Node position is in center of node view. Add half of its
            //         height, vertical separation distance, and half of new nodes
            //         height.
            double newNodeY = adjNode.getYPosition() +
                              (.5 * adjNode.getHeight()) +
                              (.5 * newNView.getHeight()) + NODE_VERTICAL_SEP;
            newNView.setOffset(newNodeX, newNodeY);
        }

        selectNodes(cNodeView, s1);
    }

    private void addAndPositionProduct(HyperEdge he, NodeView cNodeView,
                                       GraphView gv) {
        // GraphPerspective gp = gv.getGraphPerspective();
        CyNode p1 = ensureUniqueNode("P");

        // MLC 08/07/06:
        // addAttribute(p1, CytoscapeEditorManager.NODE_TYPE, EdgeTypeMap.PRODUCT);
        Iterator<CyNode> it = he.getNodes(EdgeTypeMap.PRODUCT);

        if (!it.hasNext()) {
            // we have no other nodes of this type in the HyperEdge:
            // he.addEdge(p1, EdgeTypeMap.PRODUCT);
            addEdgeAndToolTips(gv, he, p1, EdgeTypeMap.PRODUCT);
            positionLeftOrRight(he, cNodeView, gv, EdgeTypeMap.PRODUCT, "right");
        } else {
            // we have other nodes of this type:
            // adjNode will be the bottom-most product to place next to:
            NodeView adjNode = findFurthestNodeViewAlongDimension(it, gv, false);
            // he.addEdge(p1, EdgeTypeMap.PRODUCT);
            addEdgeAndToolTips(gv, he, p1, EdgeTypeMap.PRODUCT);

            NodeView newNView = gv.getNodeView(p1);
            double   newNodeX = adjNode.getXPosition();

            // ASSUME: Node position is in center of node view. Add half of its
            //         height, vertical separation distance, and half of new nodes
            //         height.
            double newNodeY = adjNode.getYPosition() +
                              (.5 * adjNode.getHeight()) +
                              (.5 * newNView.getHeight()) + NODE_VERTICAL_SEP;
            newNView.setOffset(newNodeX, newNodeY);
        }

        selectNodes(cNodeView, p1);
    }

    private void addAndPositionMediator(HyperEdge he, NodeView cNodeView,
                                        GraphView gv, String mediatorType) {
        CyNode             m1        = ensureUniqueNode("M");

        // MLC 08/07/06:
        // addAttribute(m1, CytoscapeEditorManager.NODE_TYPE, mediatorType);

        // Iterator it = he.getNodes(EdgeTypeMap.MEDIATOR);
        Collection<String> mediators = new ArrayList<String>(2);
        mediators.add(EdgeTypeMap.INHIBITING_MEDIATOR);
        mediators.add(EdgeTypeMap.ACTIVATING_MEDIATOR);

        Iterator<CyNode> it = he.getNodesByEdgeTypes(mediators);

        if (!it.hasNext()) {
            // we have no other nodes of this type in the HyperEdge:
            // he.addEdge(m1, mediatorType);
            addEdgeAndToolTips(gv, he, m1, mediatorType);
            positionAboveOrBelow(he, cNodeView, gv, mediators, "above");
        } else {
            // we have other nodes of this type:
            // adjNode will be the bottom-most mediator to place next to:
            NodeView adjNode = findFurthestNodeViewAlongDimension(it, gv, true);
            // he.addEdge(m1, mediatorType);
            addEdgeAndToolTips(gv, he, m1, mediatorType);

            NodeView newNView = gv.getNodeView(m1);
            double   newNodeY = adjNode.getYPosition();

            // ASSUME: Node position is in center of node view. Add half of its
            //         width, horizontal separation distance, and half of new nodes
            //         width.
            double newNodeX = adjNode.getXPosition() +
                              (.5 * adjNode.getWidth()) +
                              (.5 * newNView.getWidth()) + NODE_HORIZONTAL_SEP;
            newNView.setOffset(newNodeX, newNodeY);
        }

        selectNodes(cNodeView, m1);
    }

    // MLC 01/09/07 BEGIN:
    // Is the given HyperEdge in multiple Networks? If so,
    // present a dialog and return false. Othwerwise return true;
    private boolean isLegalHyperEdge(HyperEdge he) {
        Iterator<CyNetwork> heNets = he.getNetworks();

        if (heNets.hasNext()) {
            heNets.next();

            if (heNets.hasNext()) {
                // then targetNet has >= 2 nets:
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                              "Can't directly connect " +
                                              HEUtils.toString(he) +
                                              "to another HyperEdge because it is shared within two or more CyNetworks.");

                return false;
            }
        }

        return true;
    }

    // MLC 01/09/07 END.
    private void addEdgeAndToolTips(GraphView netView, HyperEdge he,
                                    CyNode node, String edgeIType) {
        CyEdge newEdge = he.addEdge(node, edgeIType);
        addNodeToolTip(node, netView);
        addEdgeToolTip(newEdge, netView);
    }

    private void addNewHyperEdgeTooltips(HyperEdge he, GraphView netView) {
        Iterator<CyNode> nodes = he.getNodes(null);
        CyNode           node;

        while (nodes.hasNext()) {
            node = nodes.next();
            addNodeToolTip(node, netView);

            Iterator<CyEdge> edges = he.getEdges(node);

            // even though we will probably only have one edge/node, do full-blown for future:
            while (edges.hasNext()) {
                addEdgeToolTip(edges.next(),
                               netView);
            }
        }

        // now deal with connectorNode specially:
        node = he.getConnectorNode();

        String name = he.getName();

        if (name == null) {
            name = node.getIdentifier();
        }

        netView.getNodeView(node).setToolTip(name);
    }

    private void addNodeToolTip(CyNode cn, GraphView netView) {
        NodeView nv = netView.getNodeView(cn);
        nv.setToolTip(Cytoscape.getNodeAttributes().getStringAttribute(
                                                                       cn.getIdentifier(),
                                                                       HyperEdgeImpl.LABEL_ATTRIBUTE_NAME));
    }

    // God, I wish we didn't have to repeat everything for nodes and edges...:
    private void addEdgeToolTip(CyEdge ce, GraphView netView) {
        EdgeView ev = netView.getEdgeView(ce);
        ev.setToolTip(Cytoscape.getEdgeAttributes().getStringAttribute(
                                                                       ce.getIdentifier(),
                                                                       HyperEdgeImpl.LABEL_ATTRIBUTE_NAME));
    }

    private NodeView findFurthestNodeViewAlongDimension(Iterator<CyNode> nodeIt,
                                                        GraphView netView,
                                                        boolean layoutHorizontally) {
        if (!nodeIt.hasNext()) {
            return null;
        }

        // The coordinate system of the GraphView seems to be centered in the middle
        // of the graphView's used space with X-axis running to the right and Y-axis
        // running down.
        CyNode   node;
        NodeView nv               = null;
        NodeView maxNodeView      = null;
        double   maxNVAlongDim    = 0.0;
        double   nodeViewAlongDim = 0.0;

        do {
            node = nodeIt.next();
            nv   = netView.getNodeView(node);

            // MLC 01/15/07:
            // CyLogger.getLogger().debug("node " + node.getIdentifier() + " offset x = " +
            //               nv.getXPosition() + " y = " + nv.getYPosition());
            // CyLogger.getLogger().debug("node " + node.getIdentifier() + " width = " +
            //               nv.getWidth());
            // CyLogger.getLogger().debug("node " + node.getIdentifier() + " border width = " +
            //               nv.getBorderWidth());
            if (layoutHorizontally) {
                nodeViewAlongDim = nv.getXPosition() + (.5 * nv.getWidth());
            } else {
                // vertical dimension:
                nodeViewAlongDim = nv.getYPosition() + (.5 * nv.getHeight());
            }

            if ((maxNodeView == null) || (nodeViewAlongDim > maxNVAlongDim)) {
                maxNVAlongDim = nodeViewAlongDim;
                maxNodeView   = nv;
            }
        } while (nodeIt.hasNext());

        // MLC 01/15/07:
        // CyLogger.getLogger().debug("max node = " + maxNodeView.getOffset());
        return maxNodeView;
    }

    private void selectNodes(NodeView cNodeView, CyNode newNode) {
        CyNetwork net = (CyNetwork) (cNodeView.getGraphView()
                                              .getGraphPerspective());
        net.unselectAllNodes();

        List<CyNode> l = new ArrayList<CyNode>(1);
        l.add(newNode);
        net.setSelectedNodeState(l, true);

        //        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
        //                                     CytoscapeEditorManager.CYTOSCAPE_EDITOR,
        //                                     net);
    }

    // Return the connector node NodeView associated with the given location.
    // If the given location is not on a connector node, return null.
    private NodeView getConnectorNodeNodeView(Point2D location, DGraphView view) {
        NodeView nv = view.getPickedNodeView(location);

        if (nv == null) {
            return null;
        }

        // must be a HyperEdge connector node:
        // MLC 11/06/06:
        if (!_manager.isConnectorNode((CyNode) nv.getNode(),
                                      (CyNetwork) (nv.getGraphView()
                                                     .getGraphPerspective()))) {
            return null;
        }

        return nv;
    }

    public void positionHyperEdge(HyperEdge he, Point2D heLocation,
                                  GraphView netView) {
        if (heLocation != null) {
            CyNode   connectorNode     = he.getConnectorNode();
            NodeView connectorNodeView = positionNode(netView, connectorNode,
                                                      heLocation);

            //            NodeView nv = Cytoscape.getCurrentNetworkView()
            //                                   .getNodeView(connectorNode);
            //            double[] nextLocn = new double[2];
            //            nextLocn[0] = heLocation.getX();
            //            nextLocn[1] = heLocation.getY();
            //            // This is very implementation dependent on Ding:
            //            ((DGraphView) Cytoscape.getCurrentNetworkView()).xformComponentToNodeCoords(nextLocn);
            //            nv.setOffset(nextLocn[0], nextLocn[1]);
            positionSubstrates(he, connectorNodeView, netView);
            positionProducts(he, connectorNodeView, netView);
            positionMediators(he, connectorNodeView, netView);

            // CyLogger.getLogger().debug("Num nodes = " + he.getNumNodes());

            // ((DGraphView) Cytoscape.getCurrentNetworkView()).fitSelected();
        }
    }

    // Position all substrates of this hyper edge view.  Attempt to
    // stack the substrates (if more than one) on top of each other
    // directly to the left of the connector node. If only one
    // substrate (normal case), place directly to the left of the
    // connector node, separated by EDGE_LENGTH distance.
    private void positionSubstrates(HyperEdge he, NodeView connectorNodeView,
                                    GraphView netView) {
        positionLeftOrRight(he, connectorNodeView, netView,
                            EdgeTypeMap.SUBSTRATE, "left");
    }

    // Position all products of this hyper edge view.  Attempt to
    // stack the products (if more than one) on top of each other
    // directly to the right of the connector node. If only one
    // product (normal case), place directly to the right of the
    // connector node, separated by EDGE_LENGTH distance.
    private void positionProducts(HyperEdge he, NodeView connectorNodeView,
                                  GraphView netView) {
        positionLeftOrRight(he, connectorNodeView, netView,
                            EdgeTypeMap.PRODUCT, "right");
    }

    // Position all meditators of this hyper edge view.  Attempt to
    // place the meditators (if more than one) in a horizontal line
    // directly above the connector node. If only one
    // meditator (normal case), place directly above the
    // connector node, separated by EDGE_LENGTH distance.
    private void positionMediators(HyperEdge he, NodeView connectorNodeView,
                                   GraphView netView) {
        List<String> mediators = new ArrayList<String>(2);
        mediators.add(EdgeTypeMap.INHIBITING_MEDIATOR);
        mediators.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
        positionAboveOrBelow(he, connectorNodeView, netView, mediators, "above");
    }

    private void positionLeftOrRight(HyperEdge he, NodeView connectorNodeView,
                                     GraphView netView, String edge_i_type,
                                     String direction) {
        double  startX;
        boolean right = "right".equals(direction);

        if (right) {
            startX = connectorNodeView.getXPosition() + EDGE_LENGTH;
        } else {
            startX = connectorNodeView.getXPosition() - EDGE_LENGTH;
        }

        // GraphPerspective gp                  = netView.getGraphPerspective();
        double startY              = connectorNodeView.getYPosition();
        double totalHeightAllNodes = computeTotalNodeHeight(he.getNodes(edge_i_type),
                                                            netView);

        // the highest point of all the nodes:
        startY -= (totalHeightAllNodes * .5);

        Iterator<CyNode> it = he.getNodes(edge_i_type);

        CyNode           node;
        double           nodeX;
        double           nodeY      = startY;
        double           nodeWidth;
        double           nodeHeight;

        while (it.hasNext()) {
            node = it.next();

            NodeView nView = netView.getNodeView(node);
            nodeWidth  = nView.getWidth();
            nodeHeight = nView.getHeight();

            // MLC 01/15/07:
            // CyLogger.getLogger().debug("node " + node.getIdentifier() + "with view " +
            //              nView + " width = " + nodeWidth + " height = " +
            //              nodeHeight);
            if (right) {
                nodeX = startX + (.5 * nodeWidth);
            } else {
                nodeX = startX - (.5 * nodeWidth);
            }

            nodeY += (.5 * nodeHeight);
            // MLC 01/15/07:
            // CyLogger.getLogger().debug("final position of node " + node.getIdentifier() +
            //               " = " + nodeX + " " + nodeY);
            nView.setOffset(nodeX, nodeY);
            nodeY += ((.5 * nodeHeight) + NODE_VERTICAL_SEP);
        }

        it = he.getNodes(edge_i_type);
        findFurthestNodeViewAlongDimension(it, netView, true);
    }

    private void positionAboveOrBelow(HyperEdge he, NodeView connectorNodeView,
                                      GraphView netView,
                                      Collection<String> edgeITypes,
                                      String direction) {
        double  startY;
        boolean below = "below".equals(direction);

        if (below) {
            startY = connectorNodeView.getYPosition() + EDGE_LENGTH;
        } else {
            startY = connectorNodeView.getYPosition() - EDGE_LENGTH;
        }

        // GraphPerspective gp     = netView.getGraphPerspective();
        double startX = connectorNodeView.getXPosition();

        // double totalWidthAllNodes = computeTotalNodeWidth(getNodeIterator(he,
        //            edge_i_type), netView);
        double totalWidthAllNodes = computeTotalNodeWidth(he.getNodesByEdgeTypes(edgeITypes),
                                                          netView);

        // the leftmost point of all the nodes:
        startX -= (totalWidthAllNodes * .5);

        Iterator<CyNode> it = he.getNodesByEdgeTypes(edgeITypes);

        CyNode           node;
        double           nodeX      = startX;
        double           nodeY;
        double           nodeWidth;
        double           nodeHeight;

        // ASSUME: node position is the center of the node.
        while (it.hasNext()) {
            node = it.next();

            NodeView nView = netView.getNodeView(node);
            nodeWidth  = nView.getWidth();
            nodeHeight = nView.getHeight();

            // MLC 01/15/07:
            // CyLogger.getLogger().debug("node " + node.getIdentifier() + "with view " +
            //              nView + " width = " + nodeWidth + " height = " +
            //              nodeHeight);
            if (below) {
                nodeY = startY + (.5 * nodeHeight);
            } else {
                nodeY = startY - (.5 * nodeHeight);
            }

            nodeX += (.5 * nodeWidth);
            // MLC 01/15/07:
            // CyLogger.getLogger().debug("final position of node " + node.getIdentifier() +
            //               " = " + nodeX + " " + nodeY);
            nView.setOffset(nodeX, nodeY);
            // MLC 01/15/07:
            // CyLogger.getLogger().debug("node after setOffset() " + node.getIdentifier() +
            //               " width = " + nodeWidth);
            // CyLogger.getLogger().debug("node after setOffset()" + node.getIdentifier() +
            //               " height = " + nodeHeight);
            // get to the left edge of the next node position:
            nodeX += ((.5 * nodeWidth) + NODE_HORIZONTAL_SEP);
        }

        findFurthestNodeViewAlongDimension(he.getNodesByEdgeTypes(edgeITypes),
                                           netView,
                                           false);
    }

    private double computeTotalNodeHeight(Iterator<CyNode> nodeIt,
                                          GraphView netView) {
        if (!nodeIt.hasNext()) {
            return 0.0;
        }

        double nodeHeight;
        double totalHeight = 0.0;
        CyNode node;

        do {
            node       = nodeIt.next();
            nodeHeight = netView.getNodeView(node).getHeight();
            // MLC 01/15/07:
            // CyLogger.getLogger().debug("node " + node.getIdentifier() + " height = " +
            //               nodeHeight);
            totalHeight += nodeHeight;

            if (nodeIt.hasNext()) {
                totalHeight += NODE_VERTICAL_SEP;
            }
        } while (nodeIt.hasNext());

        return totalHeight;
    }

    private double computeTotalNodeWidth(Iterator<CyNode> nodeIt,
                                         GraphView netView) {
        if (!nodeIt.hasNext()) {
            return 0.0;
        }

        double nodeWidth;
        double totalWidth = 0.0;
        CyNode node;

        do {
            node      = nodeIt.next();
            nodeWidth = netView.getNodeView(node).getWidth();
            // MLC 01/15/07:
            // CyLogger.getLogger().debug("node " + node.getIdentifier() + " width = " +
            //              nodeWidth);
            totalWidth += nodeWidth;

            if (nodeIt.hasNext()) {
                totalWidth += NODE_HORIZONTAL_SEP;
            }
        } while (nodeIt.hasNext());

        // MLC 01/15/07:
        // CyLogger.getLogger().debug("computeTotalNodeWidth=" + totalWidth);
        return totalWidth;
    }

    private NodeView positionNode(GraphView gv, CyNode node, Point2D location) {
        NodeView nv       = gv.getNodeView(node);
        double[] nextLocn = new double[2];
        nextLocn[0] = location.getX();
        nextLocn[1] = location.getY();

        // This is very implementation dependent on Ding:
        ((DGraphView) gv).xformComponentToNodeCoords(nextLocn);
        nv.setOffset(nextLocn[0], nextLocn[1]);

        // MLC 01/15/07:
        // CyLogger.getLogger().debug("location of connector node " + node + " = " +
        //              nv.getOffset());
        return nv;
    }

    private class ConnectorNodeIgnorer implements ShapePaletteInfoFilter {
        // Ignore ConnectorNodes:
        public boolean useEntry(ShapePaletteInfo info) {
            return !HyperEdgeImpl.ENTITY_TYPE_CONNECTOR_NODE_VALUE.equals(info.getKey());
        }
    }

    // A CursorSetter that says it's only ok to drop when we are on a ConnectorNode:
    private class HyperEdgePaletteItemDragCursorSetter
        implements DragSourceContextCursorSetter {
        public Cursor computeCursor(CyNetworkView netView, Point netViewLoc,
                                    DragSourceDragEvent dsde) {
            Point2D netViewLoc2D = new Point2D.Float(netViewLoc.x, netViewLoc.y);

            // Now check if we are on a NodeView of a ConnectorNode:
            NodeView nv = getConnectorNodeNodeView(netViewLoc2D,
                                                   (DGraphView) netView);

            if (nv != null) {
                return DragSource.DefaultCopyDrop;
            }

            return DragSource.DefaultCopyNoDrop;
        }
    }
}
