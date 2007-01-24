/* -*-Java-*-
 ********************************************************************************
 *
 * File:         BasicCytoscapeEditor.java
 * RCS:          $Header: $
 * Description:
 * Author:       Allan Kuchinsky
 * Created:      Thu Jul 27 14:27:11 2006
 * Modified:     Sun Aug 06 05:40:43 2006 (Michael L. Creech) creech@w235krbza760
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
 * Wed Dec 27 06:51:00 2006 (Michael L. Creech) creech@w235krbza760
 *  Cleanup and changed addNodeContextMenuItems(),
 *  addEdgeContextMenuItems(), and
 *  removeExistingDeleteMenuItemIfNecessary() to handle changes to
 *  DeleteAction.
 * Thu Jul 27 14:27:55 2006 (Michael L. Creech) creech@w235krbza760
 *  Removed setting of BIO_PAX attributes from addNode().
 *  Fixed deprecated method usage in addNode().
 ********************************************************************************
 */
package cytoscape.editor.editors;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeModifiedNetworkManager;

import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.data.Semantics;

import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;

import cytoscape.editor.actions.DeleteAction;

import cytoscape.editor.event.BasicNetworkEditEventHandler;
import cytoscape.editor.event.NetworkEditEventAdapter;

import cytoscape.util.CytoscapeAction;
import cytoscape.util.CytoscapeToolBar;

import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;

import ding.view.DGraphView;
import ding.view.EdgeContextMenuListener;
import ding.view.NodeContextMenuListener;

import giny.model.Node;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;


/**
 * The <b>BasicCytoscapeEditor</b> provides base level graph editing
 * functionality for Cytoscape, in particular the base level methods for adding
 * nodes, edges, and context menu items.
 * <p>
 * Provides an "Edit => Connect Selected Nodes" menu item that, when chosen,
 * creates a clique amongst the selected nodes.
 * <p>
 * Provides accelerators for modeless addition of nodes and edges.
 * Control-clicking at a position on the canvas creates a node with default
 * label in that position. The default label appears in an editable text field,
 * so the user can edit its name immediately by just beginning to type. Hit
 * ENTER or click (or control-click) anywhere outside the field, and the edited
 * field is assigned as the label for the node. Control-clicking on a node on
 * the canvas starts an edge with source at that node. Move the cursor and a
 * rubber-banded line follows the cursor. As the cursor passes over another
 * node, that node is highlighted and the rubber-banded line will snap to a
 * connection point on that second node. Control-click the mouse again and the
 * connection is established.
 * <p>
 * Provides functionality for deleting selected nodes and edges and an undo/redo
 * framework for deletion of nodes and edges
 *
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see BasicNetworkEditEventHandler
 *
 */

// MLC 07/27/06:
public class BasicCytoscapeEditor implements CytoscapeEditor,
                                             SelectEventListener,
                                             NodeContextMenuListener,
                                             EdgeContextMenuListener {
    /**
     * title of menu item for connecting selected Nodes
     */
    private static final String _connectedTitle = "Connect Selected Nodes";

    /**
     * name and type of the editor
     */
    protected String editorName;

    /**
     * pointer to Cytoscape menus
     */
    CyMenus _cyMenus;

    /**
     * pointer to Cytoscape toolbar
     */
    CytoscapeToolBar _toolBar;

    // MLC 12/13/06 BEGIN:
    //	/**
    //	 * current network view being edited
    //	 */
    // CyNetworkView view;
    //	 MLC 12/13/06 END.
    boolean DEBUG = false;

    /**
     * 32x32 image for edge cursor
     */
    Image _connectionCursorImage;

    /**
     * default cursor used by Cytoscape
     */
    Cursor _originalCursor;

    /**
     *
     * the name of the attribute used to determine Node shapes on palette this
     * is the same as the controllingNodeAttribute for mapping of visual style
     * to Node shape and color
     */
    private String _controllingNodeAttribute;

    /**
     * the name of the attribute used to determine edge shapes on palette this
     * is the same as the controllingEdgeAttribute for mapping of visual style
     * to edge line type, target arrow
     */
    private String _controllingEdgeAttribute;

    /**
     *
     * the network event handler that is associated with this editor
     */
    NetworkEditEventAdapter _networkEditEventAdapter;

    // MLC 07/27/06 BEGIN:
    // /**
    // * Cytoscape Attribute: BioPAX Name.
    // */
    // public static final String BIOPAX_NAME_ATTRIBUTE = "BIOPAX_NAME";
    // MLC 07/27/06 END.

    /**
     *
     */
    public BasicCytoscapeEditor() {
        super();

        // AJK: 04/27/06 for context menus
    }

    /**
     * wrapper for adding a node in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor.
     *
     * this method will ensure that the node added is unique. If it finds that
     * there is an existing node for <em>nodeName</em>, it will attempt to
     * generate a new, unique, <em>nodeName</em> by extending the
     * <em>nodeName</em> argument with a randomly generated extension.
     *
     * @param nodeName
     *            the name of the node to be created. This will be used as a
     *            unique identifier for the node.
     * @param attribute
     *            a defining property for the node, that can be used in
     *            conjunction with the Visual Mapper to assign visual
     *            characteristics to different types of nodes. Also can be used,
     *            by the canvas when handling a dropped item, to distinguish
     *            between nodes and edges, so should be set to something like
     *            "NodeType".
     * @param value
     *            the value of the attribute for this node. This can be used in
     *            conjunction with the Visual Mapper to assign visual
     *            characteristics to different types of nodes, for example to
     *            assign a violet diamond shape to a 'smallMolecule' node type.
     * @param location
     *            the position at which to add the node
     * @return the CyNode that has been either reused or created.
     *
     */
    public CyNode addNode(String nodeName, String attribute, String value,
                          Point2D location) {
        CytoscapeEditorManager.log("Adding node " + nodeName + " at position " +
                                   location);

        CyNode cn              = Cytoscape.getCyNode(nodeName, false); // first see if
                                                                       // there is an
                                                                       // existing node

        int    iteration_limit = 100;

        while ((cn != null) && (iteration_limit > 0)) {
            java.util.Date d1 = new java.util.Date();
            long           t1 = d1.getTime();
            String         s1 = Long.toString(t1);
            nodeName += ("_" + s1.substring(s1.length() - 3)); // append last 4
                                                               // digits of
                                                               // time stamp to node name

            cn = Cytoscape.getCyNode(nodeName, false);
            iteration_limit--;
        }

        // check for unlikely error condition where we couldn't generate a
        // unique node after a number of tries
        if (iteration_limit <= 0) {
            String expDescript = "Cytoscape Editor cannot generate a unique node for this network.  A serious internal error has occurred.  Please file a bug report at http://www.cytoscape.org.";
            String title = "Cannot generate a unique node";
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                          expDescript,
                                          title,
                                          JOptionPane.PLAIN_MESSAGE);

            return null;
        }

        // now create a unique node
        cn = Cytoscape.getCyNode(nodeName, true);

        CyNetwork net = Cytoscape.getCurrentNetwork();

        if (attribute != null) {
            // MLC 08/06/06:
            // CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(),
            // MLC 08/06/06:
            Cytoscape.getNodeAttributes().setAttribute(cn.getIdentifier(),
                                                       attribute,
                                                       value);

            if (attribute != CytoscapeEditorManager.NODE_TYPE) {
                // MLC 08/06/06:
                // CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(),
                // MLC 08/06/06:
                Cytoscape.getNodeAttributes().setAttribute(cn.getIdentifier(),
                                                           CytoscapeEditorManager.NODE_TYPE,
                                                           value);
            }

            // String canonicalName =
            // nodeAttribs.getStringAttribute(cn.getIdentifier(),
            // Semantics.CANONICAL_NAME);
            // CytoscapeEditorManager.log ("Got canonical name: " +
            // canonicalName);
            // MLC 07/27/06 BEGIN:
            // hack for BioPAX visual style
            // CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(),
            // CytoscapeEditorManager.BIOPAX_NODE_TYPE, value);
            // CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(),
            // BIOPAX_NAME_ATTRIBUTE,
            // // canonicalName);
            // cn.getIdentifier());
            // MLC 07/27/06 END.
            net.restoreNode(cn);
        }

        // hack for BioPAX
        // TODO: move this hack for BIOPAX into the
        // PaletteNetworkEditEventHandler code
        /*
         * if (attribute.equals("BIOPAX_NODE_TYPE")) {
         * net.setNodeAttributeValue(cn, "BIOPAX_NAME", nodeName); }
         */

        // AJK: 01/24/06 select the added node
        // MLC 07/27/06 BEGIN:
        // net.unFlagAllNodes();
        // net.setFlagged(cn, true);
        net.unselectAllNodes();

        List<CyNode> l = new ArrayList<CyNode>(1);
        l.add(cn);
        net.setSelectedNodeState(l, true);

        // MLC 07/27/06 END.
        NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
        nv.setToolTip(cn.getIdentifier());

        if (location != null) {
            double[] nextLocn = new double[2];
            nextLocn[0] = location.getX();
            nextLocn[1] = location.getY();
            ((DGraphView) Cytoscape.getCurrentNetworkView()).xformComponentToNodeCoords(nextLocn);
            nv.setOffset(nextLocn[0], nextLocn[1]);
            CytoscapeEditorManager.log("Offset for node " + cn + "set to " +
                                       nv.getOffset());
        }

        CytoscapeEditorManager.manager.setupUndoableAdditionEdit(net, cn, null);

        // AJK: 05/15/06 BEGIN
        // set tooltip on the node's view
        Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(this);
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
                                     CytoscapeEditorManager.CYTOSCAPE_EDITOR,
                                     net);

        // AJK: 05/15/06 END
        return cn;
    }

    // MLC 07/27/06 BEGIN:
    // /**
    // * respond to flagging of a Node. Does nothing right now.
    // */
    // public void onFlagEvent(FlagEvent e) {
    // Object obj = e.getTarget();
    //
    // if (obj instanceof NodeView) {
    // NodeView nv = (NodeView) obj;
    // CyNode myNode = (CyNode) nv.getNode();
    // CyAttributes attribs = Cytoscape.getNodeAttributes();
    //
    // // String canonicalName =
    // // attribs.getStringAttribute(myNode.getIdentifier(),
    // // Semantics.CANONICAL_NAME);
    // // CytoscapeEditorManager.log("Got target: " + canonicalName);
    // }
    // }

    /**
     * respond to selection of a Node. Does nothing right now. Implements
     * SelectionEventListener interface:
     */
    public void onSelectEvent(SelectEvent e) {
    }

    // MLC 07/27/06 END.
    // MLC 12/27/06 BEGIN:
    // implements NodeContextMenuListener
    public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
            removeExistingDeleteMenuItemIfNecessary(menu);
            menu.add(new DeleteAction(nodeView.getNode()));
    }
    //    public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
    //        if (nodeView instanceof NodeView) {
    //            // AJK: 12/09/06 fix for multiple entries
    //            // if (!menuItemExists(menu, "Delete Selected Nodes and Edges")) {
    //            removeExistingDeleteMenuItemIfNecessary(menu);
    //            menu.add(new DeleteAction(nodeView.getNode(),
    //                                      "Delete Selected Nodes and Edges"));
    //        }
    //    }
    // MLC 12/27/06 END.

    // MLC 12/27/06 BEGIN:
    // public void removeExistingDeleteMenuItemIfNecessary(JPopupMenu menu) {
    protected void removeExistingDeleteMenuItemIfNecessary(JPopupMenu menu) {
        // String        label    = "Delete Selected Nodes and Edges";
	// MLC 12/27/06 END.

        MenuElement[] elements = menu.getSubElements();

        for (int i = 0; i < elements.length; i++) {
            MenuElement elem = elements[i];

            if (elem instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) elem;

		// MLC 12/27/06 BEGIN:
                // if (item.getText().equals(label)) {
                if (item.getText().equals(DeleteAction.ACTION_TITLE)) {
		// MLC 12/27/06 END.
                    menu.remove(item);
                }
            }
        }
    }

    public boolean menuItemExists(JPopupMenu menu, String label) {
        boolean itemExists = false;

        MenuElement[] elements = menu.getSubElements();

        for (int i = 0; i < elements.length; i++) {
            MenuElement elem = elements[i];

            if (elem instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) elem;

                if (item.getText().equals(label)) {
                    itemExists = true;

                    break;
                }
            }
        }

        return itemExists;
    }

    // MLC 12/27/06 BEGIN:
    // implements EdgeContextMenuListener
    public void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu menu) {
            removeExistingDeleteMenuItemIfNecessary(menu);
            menu.add(new DeleteAction(edgeView.getEdge()));
    }

//    public void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu menu) {
//        if (edgeView instanceof EdgeView) {
//            // MLC 07/27/06:
//            // final EdgeView ev = (EdgeView) edgeView;
//            // AJK: 12/09/06 fix for multiple entries
//            // if (!menuItemExists(menu, "Delete Selected Nodes and Edges")) {
//            removeExistingDeleteMenuItemIfNecessary(menu);
//            menu.add(new DeleteAction(edgeView.getEdge(),
//                                      "Delete Selected Nodes and Edges"));
//        }
//    }
    // MLC 12/27/06 END.

    // AJK: 12/09/06 END

    // AJK: 04/27/08 END

    /**
     * wrapper for adding a node in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor.
     *
     * this method will ensure that the node added is unique. If it finds that
     * there is an existing node for <em>nodeName</em>, it will attempt to
     * generate a new, unique, <em>nodeName</em> by extending the
     * <em>nodeName</em> argument with a randomly generated extension.
     *
     * @param nodeName
     *            the name of the node to be created. This will be used as a
     *            unique identifier for the node.
     * @param attribute
     *            a defining property for the node, that can be used in
     *            conjunction with the Visual Mapper to assign visual
     *            characteristics to different types of nodes. Also can be used,
     *            by the canvas when handling a dropped item, to distinguish
     *            between nodes and edges, so should be set to something like
     *            "NodeType".
     * @param value
     *            the value of the attribute for this node. This can be used in
     *            conjunction with the Visual Mapper to assign visual
     *            characteristics to different types of nodes, for example to
     *            assign a violet diamond shape to a 'smallMolecule' node type.
     * @return the CyNode that has been either reused or created.
     *
     */
    public CyNode addNode(String nodeName, String attribute, String value) {
        return addNode(nodeName, attribute, value, null);
    }

    /**
     * wrapper for adding a node in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor.
     *
     * @param nodeName
     *            the name of the node to be created. This will be used as a
     *            unique identifier for the node.
     * @param nodeType
     *            the value of the 'NodeType' attribute for this node. This can
     *            be used in conjunction with the Visual Mapper to assign visual
     *            characteristics to different types of nodes. Also can be used,
     *            by the canvas when handling, a dropped item, to distinguish
     *            between nodes and edges.
     * @return the CyNode that has been either reused or created.
     */
    public CyNode addNode(String nodeName, String nodeType) {
        return addNode(nodeName, CytoscapeEditorManager.NODE_TYPE, nodeType);
    }

    /**
     * wrapper for adding a node in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor. This form of addNode()
     * will create a node in all cases, whether it previously exists or not.
     *
     * @param nodeName
     *            the name of the node to be created. This will be used as a
     *            unique identifier for the node.
     * @return the CyNode that has been either reused or created.
     */
    public CyNode addNode(String nodeName) {
        return addNode(nodeName, null);
    }

    /**
     *
     * wrapper for adding an edge in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor.
     *
     * @param node_1
     *            Node at one end of the edge
     * @param node_2
     *            Node at the other end of the edge
     * @param attribute
     *            the attribute of the edge to be searched, a common one is
     *            Semantics.INTERACTION
     * @param attribute_value
     *            a value for the attribute, like "pp" or "default"
     * @param create
     *            if true, then create an edge if one does not already exist.
     *            Otherwise, return the edge if it already exists.
     * @param edgeType
     *            a value for the "EdgeType" attribute assigned to the edge.
     *            This can be used in conjunction with the Visual Mapper.
     * @return the CyEdge that has either been reused or created
     *
     */
    public CyEdge addEdge(Node node_1, Node node_2, String attribute,
                          Object attribute_value, boolean create,
                          String edgeType) {
        // first see if edge already exists. If it does, then
        // there is no need to set up undoable edit or fire event
        CyEdge  edge;
        boolean uniqueEdge = true;
        edge = Cytoscape.getCyEdge(node_1, node_2, attribute, attribute_value,
                                   false, true); // edge is directed

        if (edge != null) {
            uniqueEdge = false;
        } else {
            edge = Cytoscape.getCyEdge(node_1, node_2, attribute,
                                       attribute_value, create, true); // edge is directed
        }

        if (edge != null) {
            CyNetwork net = Cytoscape.getCurrentNetwork();
            net.restoreEdge(edge);

            if (uniqueEdge) {
                if (edgeType != null) {
                    // MLC 08/06/06:
                    // CytoscapeEditorManager.edgeAttribs.setAttribute(edge.getIdentifier(),
                    // MLC 08/06/06:
                    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),
                                                               CytoscapeEditorManager.EDGE_TYPE,
                                                               edgeType);
                }

                CytoscapeEditorManager.manager.setupUndoableAdditionEdit(net,
                                                                         null,
                                                                         edge);
                Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
                                             CytoscapeEditorManager.CYTOSCAPE_EDITOR,
                                             net);

                // AJK: 05/16/06
                // ((DGraphView)
                // Cytoscape.getCurrentNetworkView()).addEdgeContextMenuListener(this);
                Cytoscape.getCurrentNetworkView()
                         .addEdgeContextMenuListener(this);
            }
        }

        // AJK: 12/09/06 BEGIN
        // set tooltip
        if (edge != null) {
            EdgeView ev = Cytoscape.getCurrentNetworkView().getEdgeView(edge);
            ev.setToolTip(edge.getIdentifier());
        }

        // AJK: 12/09/06 END
        return edge;
    }

    /**
     *
     * wrapper for adding an edge in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor. This version always
     * creates an edge, whether or not one already exists.
     *
     * @param node_1
     *            Node at one end of the edge
     * @param node_2
     *            Node at the other end of the edge
     * @param attribute
     *            the attribute of the edge to be searched, a common one is
     *            Semantics.INTERACTION
     * @param attribute_value
     *            a value for the attribute, like "pp" or "default"
     * @return the CyEdge that has been created
     *
     */
    public CyEdge addEdge(Node node_1, Node node_2, String attribute,
                          Object attribute_value) {
        return addEdge(node_1, node_2, attribute, attribute_value, true, null);
    }

    /**
     *
     * wrapper for adding an edge in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor. This version always
     * creates an edge, whether or not one already exists.
     *
     * @param node_1
     *            Node at one end of the edge
     * @param node_2
     *            Node at the other end of the edge
     * @param attribute
     *            the attribute of the edge to be searched, a common one is
     *            Semantics.INTERACTION
     * @param attribute_value
     *            a value for the attribute, like "pp" or "default"
     * @param edgeType
     *            a value for the "EdgeType" attribute assigned to the edge.
     *            This can be used in conjunction with the Visual Mapper.
     * @return the CyEdge that has been created
     *
     */
    public CyEdge addEdge(Node node_1, Node node_2, String attribute,
                          Object attribute_value, String edgeType) {
        return addEdge(node_1, node_2, attribute, attribute_value, true,
                       edgeType);
    }

    /**
     *
     * wrapper for adding an edge in Cytoscape. This is intended to be called by
     * the CytoscapeEditor in lieu of making direct modifications to the
     * Cytoscape model. Thus, it provides an insulating level of abstraction
     * between the CytoscapeEditor and the Cytoscape implementation, allowing
     * for portability and extensibility of the editor.
     *
     * @param node_1
     *            Node at one end of the edge
     * @param node_2
     *            Node at the other end of the edge
     * @param attribute
     *            the attribute of the edge to be searched, a common one is
     *            Semantics.INTERACTION
     * @param attribute_value
     *            a value for the attribute, like "pp" or "default"
     * @param create
     *            if true, then create an edge if one does not already exist.
     *            Otherwise, return the edge if it already exists.
     * @return the CyEdge that has either been reused or created
     *
     */
    public CyEdge addEdge(Node node_1, Node node_2, String attribute,
                          Object attribute_value, boolean create) {
        return addEdge(node_1, node_2, attribute, attribute_value, create, null);
    }

    /**
     * Deletes (hides) a node from the current network
     *
     * @param node
     *            the node to be deleted
     */
    public void deleteNode(Node node) {
        CyNetwork net = Cytoscape.getCurrentNetwork();
        net.hideNode(node);
        CytoscapeModifiedNetworkManager.setModified(net,
                                                    CytoscapeModifiedNetworkManager.MODIFIED);
    }

    /**
     * Deletes (hides) an edge from the current network
     *
     * @param edge
     *            the edge to be deleted
     */
    public void deleteEdge(CyEdge edge) {
        CyNetwork net = Cytoscape.getCurrentNetwork();
        net.hideEdge(edge);
        CytoscapeModifiedNetworkManager.setModified(net,
                                                    CytoscapeModifiedNetworkManager.MODIFIED);
    }

    /**
     * build the visualStyle for this editor this code should be overidden by
     * more specialized editors that programmatically create a visual style
     *
     */
    public void buildVisualStyle() {
    }

    /**
     * specialized initialization code for editor, called by
     * CytoscapeEditorManager when a new editor is built, should be overridden
     *
     * @param args
     *            an arbitrary list of arguments passed to initialization
     *            routine. Not used in this editor
     */
    public void initializeControls(List args) {
        // first, check to see if there already is a menu item to "Connect
        // Selected Nodes"
        // if not, then add an item
        // TODO: look for routines to find a menu item given a string; there
        // should be such a utility
        JMenu   editMenu             = Cytoscape.getDesktop().getCyMenus()
                                                .getEditMenu();
        boolean foundConnectSelected = false;
        // CytoscapeEditorManager.log("checking against edit menu: " +
        // editMenu);
        CytoscapeEditorManager.log("item count = " + editMenu.getItemCount());

        for (int i = 0; i < editMenu.getItemCount(); i++) {
            JMenuItem jIt = editMenu.getItem(i);

            if (jIt != null) {
                String name = jIt.getText();

                // System.out
                // .println("Checking for get selected against: " + name);
                if (name.equals(_connectedTitle)) {
                    foundConnectSelected = true;

                    break;
                }
            }
        }

        if (!foundConnectSelected) {
            ConnectSelectedNodesAction connectAction = new ConnectSelectedNodesAction();
            connectAction.setPreferredMenu("Edit");
            Cytoscape.getDesktop().getCyMenus().addAction(connectAction);
        }

        _cyMenus = Cytoscape.getDesktop().getCyMenus();

        _originalCursor = Cytoscape.getDesktop().getCursor();
    }

    /**
     * sets controls invisible when editor type is switched
     *
     * @param args
     *            args an arbitrary list of arguments (not used in this editor)
     */
    public void disableControls(List args) {
    }

    /**
     * sets controls visible when editor type is switched back to this editor
     *
     * @param args
     *            args an arbitrary list of arguments (not used in this editor) *
     */
    public void enableControls(List args) {
    }

    /**
     * gets the name (type) of this editor
     *
     * @return the editorName.
     */
    public String getEditorName() {
        return editorName;
    }

    /**
     * sets the name (type) for this editor
     *
     * @param editorName
     *            the editorName to set.
     */
    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }

    /**
     *
     * @return the name of the attribute used to determine edge shapes on
     *         palette this is the same as the controllingEdgeAttribute for
     *         mapping of visual style to edge line type, target arrow
     */
    public String getControllingEdgeAttribute() {
        return _controllingEdgeAttribute;
    }

    /**
     *
     * @param controllingEdgeAttribute
     *            the name of the attribute used to determine edge shapes on
     *            palette this is the same as the controllingEdgeAttribute for
     *            mapping of visual style to edge line type, target arrow
     *
     */
    public void setControllingEdgeAttribute(String controllingEdgeAttribute) {
        _controllingEdgeAttribute = controllingEdgeAttribute;
    }

    /**
     *
     * @return the name of the attribute used to determine Node shapes on
     *         palette this is the same as the controllingNodeAttribute for
     *         mapping of visual style to Node shape and color
     */
    public String getControllingNodeAttribute() {
        return _controllingNodeAttribute;
    }

    /**
     *
     * @param controllingNodeAttribute
     *            the name of the attribute used to determine Node shapes on
     *            palette this is the same as the controllingNodeAttribute for
     *            mapping of visual style to Node shape and color
     *
     */
    public void setControllingNodeAttribute(String controllingNodeAttribute) {
        _controllingNodeAttribute = controllingNodeAttribute;
    }

    /**
     *
     * @return the network event handler that is associated with this editor
     */
    public NetworkEditEventAdapter getNetworkEditEventAdapter() {
        return _networkEditEventAdapter;
    }

    /**
     *
     * @param adapter
     *            the network event handler that is associated with this editor
     */
    public void setNetworkEditEventAdapter(NetworkEditEventAdapter adapter) {
        _networkEditEventAdapter = adapter;
    }

    /**
     * action performed with the Edit->Connect Selected Nodes menu item is
     * clicked on creates a clique from the set of selected nodes
     *
     * @author ajk
     *
     */
    class ConnectSelectedNodesAction extends CytoscapeAction {
        // MLC 07/27/06:
        private static final long serialVersionUID = 3131183861434497429L;

        public ConnectSelectedNodesAction() {
            super(_connectedTitle);
            setPreferredMenu("Edit");
            setPreferredIndex(Cytoscape.getDesktop().getCyMenus().getEditMenu()
                                       .getItemCount() - 2);
        }

        public ConnectSelectedNodesAction(boolean label) {
            this();
        }

        public String getName() {
            return _connectedTitle;
        }

        public void actionPerformed(ActionEvent e) {
            CyNetworkView  view  = Cytoscape.getCurrentNetworkView();
            java.util.List nodes = view.getSelectedNodes();

            // AJK: 06/16/06 BEGIN
            // set EDGE_TYPE
            String edgeTypeValue = "default";
            String edgeTypeName  = "DefaultEdge";
            List   edgeTypesList = CytoscapeEditorManager.getEdgeTypesForVisualStyle(view.getVisualStyle());

            if (edgeTypesList != null) {
                if (edgeTypesList.size() == 1) // just use only edge type
                 {
                    edgeTypeValue = edgeTypesList.get(0).toString();
                    edgeTypeName  = edgeTypesList.get(0).toString();
                } else {
                    String[] possibilities = new String[edgeTypesList.size()];

                    for (int i = 0; i < edgeTypesList.size(); i++) {
                        possibilities[i] = edgeTypesList.get(i).toString();
                    }

                    String s = (String) JOptionPane.showInputDialog(Cytoscape.getDesktop(),
                                                                    "Please choose an EDGE_TYPE",
                                                                    "Connect Selected Nodes",
                                                                    JOptionPane.PLAIN_MESSAGE,
                                                                    null,
                                                                    possibilities,
                                                                    possibilities[0]);

                    // If a string was returned, say so.
                    if ((s != null) && (s.length() > 0)) {
                        edgeTypeValue = s;
                        edgeTypeName  = s;
                    }
                }
            }

            // AJK: 06/16/06 END
            for (int i = 0; i < (nodes.size() - 1); i++) {
                NodeView nv          = (NodeView) nodes.get(i);
                CyNode   firstCyNode = (CyNode) nv.getNode();

                for (int j = i + 1; j < nodes.size(); j++) {
                    NodeView nv2          = (NodeView) nodes.get(j);
                    CyNode   secondCyNode = (CyNode) nv2.getNode();
                    addEdge(firstCyNode, secondCyNode, Semantics.INTERACTION,
                            edgeTypeValue, true, edgeTypeName);
                }
            }
        }
    }
}
