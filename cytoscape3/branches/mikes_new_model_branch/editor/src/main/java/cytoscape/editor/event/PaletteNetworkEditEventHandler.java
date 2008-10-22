/* -*-Java-*-
********************************************************************************
*
* File:         PaletteNetworkEditEventHandler.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Fri Jul 31 05:14:41 2005
* Modified:     Fri May 11 17:04:45 2007 (Michael L. Creech) creech@w235krbza760
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
* Fri May 11 17:04:38 2007 (Michael L. Creech) creech@w235krbza760
*  Removed uneeded imports.
* Fri Dec 08 05:15:16 2006 (Michael L. Creech) creech@w235krbza760
*  Refactored itemDropped() and handleDroppedEdge() and broke into
*  smaller methods.  Removed findEdgeDropTarget().
********************************************************************************
*/
package cytoscape.editor.event;

import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;
import phoebe.PhoebeCanvasDropEvent;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


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
	 *
	 * @param caller
	 * @param view
	 */
	public PaletteNetworkEditEventHandler(CytoscapeEditor caller, GraphView view) {
		super(caller, view);
	}

	protected BasicCytoShapeEntity getShapeEntityForLocation(Point location, Transferable t) {
		Object shape = null;
		BasicCytoShapeEntity myShape = null;
		Class shapeClass = null;

		try {
			shapeClass = Class.forName("cytoscape.editor.impl.BasicCytoShapeEntity");
		} catch (Exception except) {
			except.printStackTrace();

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
					exc.printStackTrace();

					return null;
				} catch (IOException exc) {
					exc.printStackTrace();

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
		Point location = e.getLocation();
		CytoscapeEditorManager.log("Item dropped at: " + e.getLocation());
		CytoscapeEditorManager.log("on object: " + e.getSource());
		
		// do nothing if we are not dropping on the current network view
		GraphView dropView = (GraphView) e.getSource();
		GraphView currentView = Cytoscape.getCurrentNetworkView();
		if (dropView != currentView)
		{
			return;
		}
		
		BasicCytoShapeEntity myShape = getShapeEntityForLocation(location, e.getTransferable());

		if (myShape != null) {
			// need to handle nodes and edges differently
			String attributeName = myShape.getAttributeName();
			String attributeValue = myShape.getAttributeValue();

			if (attributeName.equals(get_caller().getControllingNodeAttribute())) {
				setNodeAttributeName(attributeName);
				setNodeAttributeValue(attributeValue);
				handleDroppedNode(attributeName, attributeValue, location);
			} else if (attributeName.equals(get_caller().getControllingEdgeAttribute())) {
				setEdgeAttributeName(attributeName);
				setEdgeAttributeValue(attributeValue);
				handleDroppedEdge(attributeName, attributeValue, location);
			}
		}
	}

	// overwridden by subclasses:
	protected void handleDroppedNode(String attributeName, String attributeValue, Point location) {
		get_caller().addNode("node" + counter, attributeName, attributeValue, location);
		counter++;
	}

	// overwridden by subclasses:
	protected void handleDroppedEdge(String attributeName, String attributeValue, Point location) {
		if (isEdgeStarted()) {
			// if there is another edit in progress, then don't
			// process a drag/drop
			return;
		}

		// NB: targetNode is *drop* target
		NodeView targetNode = getView().getPickedNodeView(location);

		if (targetNode == null) {
			return;
		}

		// if we reach this point, then the edge shape has been
		// dropped onto a nod3e Begin Edge creation
		setHandlingEdgeDrop(true);
		beginEdge(location, targetNode);
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
				// MLC 12/07/06:
				// Node cn = _caller.addNode("node" + counter, "URL");
				// MLC 12/07/06:
				CyNode cn = get_caller().addNode("node" + counter, "URL");
				counter++;
				Cytoscape.getCurrentNetwork().restoreNode(cn);
			}
		} catch (UnsupportedFlavorException exc) {
			exc.printStackTrace();

			return;
		} catch (IOException exc) {
			exc.printStackTrace();

			return;
		}
	}
}
