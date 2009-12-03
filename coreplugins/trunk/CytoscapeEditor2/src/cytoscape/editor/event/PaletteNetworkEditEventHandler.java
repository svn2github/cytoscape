/* -*-Java-*-
********************************************************************************
*
* File:         PaletteNetworkEditEventHandler.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Fri Jul 31 05:14:41 2005
* Modified:     Wed Feb 04 09:01:03 2009 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Wed Feb 04 08:57:26 2009 (Michael L. Creech) creech@w235krbza760
*  Removed view in favor of deriving from the current network view.
* Wed Jul 09 09:54:56 2008 (Michael L. Creech) creech@w235krbza760
*  Added check that Editor component is active to itemDropped()
*  to avoid handling events when the editor tab isn't the current tab.
* Fri May 11 17:04:38 2007 (Michael L. Creech) creech@w235krbza760
*  Removed uneeded imports.
* Fri Dec 08 05:15:16 2006 (Michael L. Creech) creech@w235krbza760
*  Refactored itemDropped() and handleDroppedEdge() and broke into
*  smaller methods.  Removed findEdgeDropTarget().
********************************************************************************
*/
package cytoscape.editor.event;

import giny.view.NodeView;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import phoebe.PhoebeCanvasDropEvent;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.dialogs.SetNestedNetworkDialog;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import ding.view.InnerCanvas;
import javax.swing.JOptionPane;
import cytoscape.CyNetwork;

/**
 *
 * The <b>PaletteNetworkEditEventHandler</b> extends the basic network edit event handler with the
 * capability to drag and drop shapes from a palette onto
 * the canvas, resulting in the addition of nodes and edges to the current
 * Cytoscape network.
 *
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class PaletteNetworkEditEventHandler extends BasicNetworkEditEventHandler {
	CyLogger logger = CyLogger.getLogger(PaletteNetworkEditEventHandler.class);

	/**
	 *
	 */
	public PaletteNetworkEditEventHandler() {
		super();
	}

	/**
	 * @param caller
	 */
	public PaletteNetworkEditEventHandler(CytoscapeEditor caller) {
		super(caller);
	}

	/**
	 * @deprecated call single argument constructor
	 * @param caller
	 * @param view
	 */
	public PaletteNetworkEditEventHandler(CytoscapeEditor caller, CyNetworkView view) {
	    // MLC 02/03/09 BEGIN:
	    // super(caller, view);
	    super(caller);
	    // MLC 02/03/09 END.
	}

	protected BasicCytoShapeEntity getShapeEntityForLocation(Point location, Transferable t) {
		Object shape = null;
		BasicCytoShapeEntity myShape = null;
		Class shapeClass = null;

		try {
			shapeClass = Class.forName("cytoscape.editor.impl.BasicCytoShapeEntity");
		} catch (Exception except) {
			logger.warn("Can't get class for BasicCytoShapeEntity", except);
			return null;
		}

		DataFlavor[] dfl = t.getTransferDataFlavors();

		for (DataFlavor d : dfl) {
	
			CytoscapeEditorManager.log("Item dropped of Mime Type: " + d.getMimeType());
			CytoscapeEditorManager.log("Mime subtype is:  " + d.getSubType());
			CytoscapeEditorManager.log("Mime class is: " + d.getRepresentationClass());

			Class mimeClass = d.getRepresentationClass();

			if (mimeClass == shapeClass) {
				CytoscapeEditorManager.log("got shape: " + d.getRepresentationClass());

				try {
					shape = t.getTransferData(d);
				} catch (UnsupportedFlavorException exc) {
					logger.warn("Unsupported shape flavor", exc);
					return null;
				} catch (IOException exc) {
					logger.warn("I/O exception getting shape", exc);

					return null;
				}

				break;
			} else if (d.isMimeTypeEqual("application/x-java-url")) {
				handleDroppedURL(t, d, location);

				break;
			}
		}

		if (shape != null) {
			myShape = ShapePalette.getBasicCytoShapeEntity(shape.toString());
		}

		return myShape;
	}

	/**
	 * The <b>itemDropped()</b> method is at the heart of the palette-based
	 * editor. The method can respond to a variety of DataFlavors that
	 * correspond to the shape being dragged and dropped from the palette. These
	 * include Cytoscape nodes and edges, as well as URLs that can be dragged
	 * and dropped from other applications onto the palette.
	 *
	 */

	// implements PhoebeCanvasDropListener interface:
	public void itemDropped(PhoebeCanvasDropEvent e) {
	    // TODO: This check should really be avoided by having the editor remove the PhoebeCanvasDropListener
	    //       when the editor looses focus (another tab is clicked on).
	    //       Since this is somewhat involved and so is left for when the editor is refactored.
	    if (!CytoscapeEditorManager.isEditorInOperation()) {
		return;
	    }
		Point location = e.getLocation();
		CytoscapeEditorManager.log("Item dropped at: " + e.getLocation());
		CytoscapeEditorManager.log("on object: " + e.getSource());
		
		// AJK: 07/03/07 BEGIN
		//    do nothing if we are not dropping on canvas for current network view
		InnerCanvas dropCanvas = (InnerCanvas) e.getSource();
		// MLC 02/03/09 BEGIN:
		// InnerCanvas currentCanvas = ((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas();
		InnerCanvas currentCanvas = getCurrentDGraphView().getCanvas();
		// MLC 02/03/09 END.

		if (dropCanvas != currentCanvas)
		{
			return;
		}
		// AJK: 07/03/07 END
		
		BasicCytoShapeEntity myShape = getShapeEntityForLocation(location, e.getTransferable());

		if (myShape != null) {
			// need to handle nodes and edges differently
			String attributeName = myShape.getAttributeName();
			String attributeValue = myShape.getAttributeValue();

			// CytoscapeEditorManager.log("Item dropped of type: " +
			// attributeName);
			if (attributeName.equals(get_caller().getControllingNodeAttribute())) {
				setNodeAttributeName(attributeName);
				setNodeAttributeValue(attributeValue);
				handleDroppedNode(attributeName, attributeValue, location);
			} else if (attributeName.equals(get_caller().getControllingEdgeAttribute())) {
				setEdgeAttributeName(attributeName);
				setEdgeAttributeValue(attributeValue);
				handleDroppedEdge(attributeName, attributeValue, location);
			} else if (attributeName.equals(get_caller().getControllingNetworkAttribute())){
				setNetworkAttributeName(attributeName);
				setNetworkAttributeValue(attributeValue);
				handleDroppedNetwork(getNodeAttributeName(), getNodeAttributeValue(), location);			}
		}
	}

	// overwridden by subclasses:
	protected void handleDroppedNode(String attributeName, String attributeValue, Point location) {
		String nodeID = "node" + counter;

		if (ShapePalette.specifyIdentifier){
			nodeID = getNodeID(nodeID);
			if (nodeID == null){
				return;
			}
		}

		get_caller().addNode(nodeID, attributeName, attributeValue, location);
		counter++;
	}

	private String getNodeID(String nodeID){
		String newNodeID = null;

		while (true){
			newNodeID = JOptionPane.showInputDialog(Cytoscape.getDesktop(),"Please Specify Node identifier", nodeID);
			if (newNodeID == null){
				return null;
			}

			// Check if the nodeID already exists
			CyNode aNode = Cytoscape.getCyNode(newNodeID);
			if (aNode == null){
				break;
			}
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), newNodeID + " already exists!", "Duplicated Identifier", 
					JOptionPane.WARNING_MESSAGE);
		}

		return newNodeID;
	}
	
	// overwridden by subclasses:
	protected void handleDroppedEdge(String attributeName, String attributeValue, Point location) {
		if (isEdgeStarted()) {
			// if there is another edit in progress, then don't
			// process a drag/drop
			return;
		}

		// NB: targetNode is *drop* target
		// MLC 02/03/09 BEGIN:
		// NodeView targetNode = getView().getPickedNodeView(location);
		// Things that add new network views, like HyperEdgeEditor sample networks, can cause the
		// cached view to not be the correct view where we are clicking, so we use the current network view:
		// CytoscapeEditorManager.log ("In handleDroppedEdge");
		NodeView targetNode = getCurrentDGraphView().getPickedNodeView(location);
		// MLC 02/03/09 END.
		if (targetNode == null) {
			return;
		}

		// if we reach this point, then the edge shape has been
		// dropped onto a node Begin Edge creation
		setHandlingEdgeDrop(true);
		beginEdge(location, targetNode);
	}

	// Support Nested Network
	protected void handleDroppedNetwork(String attributeName, String attributeValue, Point location) {
		//get_caller().addNetwork("network" + counter, attributeName, attributeValue, location);
		//counter++;

		NodeView targetNode = getCurrentDGraphView().getPickedNodeView(location);
		if (targetNode == null) {

			// Select the nested network
			SetNestedNetworkDialog dlg = new SetNestedNetworkDialog(Cytoscape.getDesktop(), true);
			dlg.setLocationRelativeTo(Cytoscape.getDesktop());	
			dlg.setVisible(true);
			
			CyNetwork selectedNetwork = dlg.getSelectedNetwork();

			// Create a new Node
			String nodeID = selectedNetwork.getIdentifier();//"node" + counter;
			
			if (ShapePalette.specifyIdentifier){
				nodeID = getNodeID(nodeID);
				if (nodeID == null){
					return;
				}
			}
						
			CyNode newNode = get_caller().addNode(nodeID, attributeName, attributeValue, location);				
			counter++;
			
			// Set a nested network for this newly created node
			NodeView newNodeView = Cytoscape.getCurrentNetworkView().getNodeView(newNode);
			
			if (newNodeView == null){
				return;
			}
			newNode.setNestedNetwork(selectedNetwork);
			
		}
		else {
			SetNestedNetworkDialog dlg = new SetNestedNetworkDialog(Cytoscape.getDesktop(), true, targetNode);
			dlg.setLocationRelativeTo(Cytoscape.getDesktop());	
			dlg.setVisible(true);
		}
	}

	/**
	 * A stub routine that currently just adds a node at the drop position. In
	 * theory, the URL can be traversed and the retrieved document parsed to
	 * extract information that can be added to the Cytoscape network. For
	 * example, the user could drag a URL an NCBI Unigene Web page and a node
	 * could be added to the network for that gene, with certain pieces of
	 * information on that Web extracted and assigned as attributes for that
	 * gene.
	 *
	 * @param t
	 *            the Transferable that is dropped onto the canvas
	 * @param d
	 *            the DataFlavor that represents the dropped URL.
	 * @param location
	 *            the location of the drop event.
	 */
	public void handleDroppedURL(Transferable t, DataFlavor d, Point location) {
		Object URL;

		try {
			URL = t.getTransferData(d);

			if (URL != null) {
				// CytoscapeEditorManager.log ("Handling dropped URL = " +
				// URLString);
				CyNode cn = get_caller().addNode("node" + counter, "URL");
				counter++;
				Cytoscape.getCurrentNetwork().restoreNode(cn);
			}
		} catch (UnsupportedFlavorException exc) {
			logger.warn("Unsupported shape flavor", exc);

			return;
		} catch (IOException exc) {
			logger.warn("I/O exception getting shape", exc);

			return;
		}
	}
}
