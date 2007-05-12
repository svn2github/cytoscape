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

import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;

import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;

import cytoscape.view.CyNetworkView;

import giny.view.NodeView;

import phoebe.PhoebeCanvasDropEvent;

import java.awt.Point;
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
	// MLC 12/07/06:
	// BasicNetworkEditEventHandler editEvent;
	// MLC 12/07/06:
	// public static final String NODE_TYPE = "NODE_TYPE";

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
	public PaletteNetworkEditEventHandler(CytoscapeEditor caller, CyNetworkView view) {
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

	// MLC 12/07/06 BEGIN:
	// implements PhoebeCanvasDropListener interface:
	public void itemDropped(PhoebeCanvasDropEvent e) {
		Point location = e.getLocation();
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

	// // implements PhoebeCanvasDropListener interface:
	// public void itemDropped(PhoebeCanvasDropEvent e) {
	// Object shape = null;
	// String shapeName = null;
	//
	// // AJK: 07/17/06 BEGIN Debugging code
	// // DGraphView view = (DGraphView) Cytoscape.getCurrentNetworkView();
	// // JLabel button = new JLabel ("test 1");
	// // view.getBackgroundCanvas().add(button);
	// // CytoscapeEditorManager.log("added component to background canvas: " +
	// view.getBackgroundCanvas());
	// //
	// // button.setLocation(e.getLocation());
	// // button.setPreferredSize(new Dimension (70, 20));
	// // button.setOpaque(true);
	// // button.setBackground(Color.BLUE);
	// // button.setVisible(true);
	// // view.getCanvas().repaint();
	// // AJK: 07/17/06 END
	//
	// Point location = e.getLocation();
	// Transferable t = e.getTransferable();
	// BasicCytoShapeEntity myShape = null;
	// DataFlavor [] dfl = t.getTransferDataFlavors();
	//	
	// for (int i = 0; i < dfl.length; i++)
	// {
	// DataFlavor d = dfl[i];
	// if (d.isMimeTypeEqual("application/x-java-url"))
	// {
	// handleDroppedURL(t, d, location);
	// }
	// else if (t.isDataFlavorSupported(dfl[i])) // should be d
	// {
	// try
	// {
	// // should be d
	// shape = t.getTransferData(dfl[i]);
	// }
	//			
	// catch (UnsupportedFlavorException exc)
	// {
	// exc.printStackTrace();
	// return;
	// }
	// catch (IOException exc)
	// {
	// exc.printStackTrace();
	// return;
	// }
	// }
	// }
	//        
	// if (shape != null)
	// {
	// shapeName = shape.toString();
	// myShape = ShapePalette.getBasicCytoShapeEntity(shapeName); }
	//	
	// if (myShape != null) {
	// // need to handle nodes and edges differently
	// String attributeName = myShape.getAttributeName();
	// String attributeValue = myShape.getAttributeValue();
	//
	// // CytoscapeEditorManager.log("Item dropped of type: " + attributeName);
	//
	// if (attributeName.equals(PaletteNetworkEditEventHandler.NODE_TYPE)) {
	// this.setNodeAttributeName(attributeName);
	// this.setNodeAttributeValue(attributeValue);
	//
	// _caller.addNode("node" + counter, attributeName,
	// attributeValue, location);
	// counter++;
	// handleDroppedNode(attributeName, attributeValue, location);
	// } else if
	// (attributeName.equals(PaletteNetworkEditEventHandler.EDGE_TYPE)) {
	// this.setEdgeAttributeName(attributeName);
	// this.setEdgeAttributeValue(attributeValue);
	// handleDroppedEdge(attributeValue, e);
	// }
	// }
	// }
	//
	//
	// /**
	// * specialized processing for a dropped shape that represents an edge.
	// * if the edge shape is dropped on a node, then start an edge from the
	// node that is
	// * dropped on. Subsequent movements of the mouse extend the edge, as in
	// the BasicNetworkEditEventHandler.
	// * A mouse click when over the desired target node completes the edge.
	// *
	// * @param attributeValue the type of the edge
	// * @param e the drop event
	// */
	//
	// public void handleDroppedEdge(String attributeValue,
	// PhoebeCanvasDropEvent e) {
	// Point location = e.getLocation();
	//
	// // MLC: Why not just use location?:
	// Point2D locn = (Point2D) location.clone();
	//
	// // locn = canvas.getCamera().localToView(locn);
	//
	// // MLC 12/07/06:
	// // editEvent = this;
	// if (edgeStarted) {
	// // if there is another edit in progress, then don't process a drag/drop
	// return;
	// }
	//
	// // NB: targetNode is *drop* target
	// // MLC: I think findEdgeDropTarget can be replaced with
	// // getView().getPickedNodeView(location) and
	// // this would fix the issues listed in the header for
	// // findEdgeDropTarget.
	// NodeView targetNode = findEdgeDropTarget(locn);
	//
	// // CytoscapeEditorManager.log ("drop target = " + targetNode);
	// if (targetNode == null) {
	// return;
	// }
	//
	// // if we reach this point, then the edge shape has been dropped onto a
	// nod3e
	// // MLC: why do we need nextPoint?:
	// nextPoint = e.getLocation();
	//
	// // MLC: why do we need onNode:
	// boolean onNode = true;
	//
	// // MLC: Why is this 'if' here? It will always be true:
	// if (onNode && !(edgeStarted)) {
	// // Begin Edge creation
	// setHandlingEdgeDrop(true);
	// // MLC: why edgeStarted and setEdgeStarted():
	// // MLC: Also, why not replace all the following
	// // lines of the 'if' with BasicNetworkEditEventHandler.beginEdge()?:
	// edgeStarted = true;
	// setEdgeStarted(true);
	// setNode(targetNode);
	// // edge = new PPath();
	// //
	// // edge.setStroke(new PFixedWidthStroke(3));
	// // edge.setPaint(Color.black);
	// // MLC: why setting startPoint directly and then
	// // with setStartPoint()?:
	// startPoint = nextPoint;
	// updateEdge();
	// setStartPoint(startPoint);
	// }
	// }
	//
	// /**
	// * determine which node the edge has been dropped on, if any
	// * <p>
	// * TODO: findEdgeDropTarget currently iterates through all of the Nodes in
	// the current network and
	// * checks whether the drop event position is contained within the bounds
	// of the node. Is
	// * there a more efficient way to do this?
	// * TODO: 06/22/06: update this to use new renderer routines for finding
	// nodes intersecting point
	// * @param location the location of the drop event
	// * @return the NodeView that is located at the drop location.
	// */
	// public NodeView findEdgeDropTarget(Point2D location) {
	// double[] locn = new double[2];
	// locn[0] = location.getX();
	// locn[1] = location.getY();
	// this.getView().xformComponentToNodeCoords(locn);
	//
	// double locnX = locn[0];
	// double locnY = locn[1];
	//
	// Iterator it = Cytoscape.getCurrentNetworkView().getNodeViewsIterator();
	// NodeView nv;
	//
	// while (it.hasNext()) {
	// nv = (NodeView) it.next();
	//
	// if ((locnX >= (nv.getXPosition() - nv.getWidth())) &&
	// (locnX <= (nv.getXPosition() + nv.getWidth())) &&
	// (locnY >= (nv.getYPosition() - nv.getHeight())) &&
	// (locnY <= (nv.getYPosition() + nv.getHeight()))) {
	// return nv;
	// }
	// }
	//
	// return null;
	// }
	// MLC 12/07/06 END.

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

		// AJK: 12/08/06 oy, what a hack. try to send transferable to
		// transferhandler
		// of cytoscapeDesktopPane
		// AJK: 12/08/06 always dispatch event to next listener
		// TransferHandler th = Cytoscape.getDesktop().getNetworkViewManager().
		// getDesktopPane().getTransferHandler();
		// if (th != null)
		// {
		// th.importData(Cytoscape.getDesktop().getNetworkViewManager().
		// getDesktopPane(), t);
		// }
		// AJK: 12/08/06 END
		try {
			URL = t.getTransferData(d);

			if (URL != null) {
				// CytoscapeEditorManager.log ("Handling dropped URL = " +
				// URLString);
				// MLC 12/07/06:
				// CyNode cn = _caller.addNode("node" + counter, "URL");
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
