/*
 * Created on Jul 31, 2005
 *
 */
package cytoscape.editor.event;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Iterator;

import phoebe.PhoebeCanvasDropEvent;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;

/**
 * 
 * The <b>PaletteNetworkEditEventHandler</b> extends the basic network edit event handler with the
 * capability to drag and drop shapes from a palette onto the canvas, resulting in the addition of 
 * nodes and edges to the current Cytoscape network.  
 * 
 * <p>
 * This functionality is not available in Cytoscape 2.2.
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see SimpleBioMoleculeEditor
 * 
 * 
 */
public class PaletteNetworkEditEventHandler extends
		BasicNetworkEditEventHandler {

	
	BasicNetworkEditEventHandler editEvent;
	
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
	 * The <b>itemDropped()</b> method is at the heart of the palette-based editor.  The method can
	 * respond to a variety of DataFlavors that correspond to the shape being dragged and dropped from
	 * the palette.  These include Cytoscape nodes and edges, as well as URLs that can be dragged and
	 * dropped from other applications onto the palette.
	 * 
	 */
	public void itemDropped (PhoebeCanvasDropEvent e) {
				
		Object shape;
		String shapeName = null;

		Point location = e.getLocation();
		Point2D locn = (Point2D) location.clone();
		locn = canvas.getCamera().localToView(locn);
		Transferable t = e.getTransferable();
		    	
		DataFlavor [] dfl = t.getTransferDataFlavors();		
		
		for (int i = 0; i < dfl.length; i++)
		{
		    		 DataFlavor d = dfl[i];
		    		 if (d.equals("application/x-java-url"))
		    		 {
		    		 	handleDroppedURL(t, d, location);
		    		 }
		}

		try
		{
		    shape = t.getTransferData(DataFlavor.stringFlavor);
		    if (shape != null)
		    {
		    	shapeName = shape.toString();
		    }
		}
		catch (UnsupportedFlavorException exc)
				{
					exc.printStackTrace();
					return;
				}
		catch (IOException exc)
				{
					exc.printStackTrace();
					return;					
				}
				
		BasicCytoShapeEntity myShape = ShapePalette.getBasicCytoShapeEntity(shapeName);

        Object [] args = null;
		if (myShape != null)
		{
			// need to handle nodes and edges differently 
			String attributeName = myShape.getAttributeName();
			String attributeValue = myShape.getAttributeValue();

			args = new Object []{ "LOCATION", location};
			if (attributeName.equals("NodeType") ||
					(attributeName.equals("BIOPAX_NODE_TYPE")))  // TODO: incorporate the processing
				// of BIOPAX_NODE_TYPE into the SimpleBioMoleculeEditor class
			{
				CyNode cn = CytoscapeEditorManager.addNode("node" + counter, 
						true, attributeName, attributeValue);
			    counter++;				
				double zoom = Cytoscape.getCurrentNetworkView().getZoom();
				Cytoscape.getCurrentNetwork().restoreNode(cn);		
				NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
				nv.setOffset(locn.getX(), locn.getY());
			}
			else if (attributeName.equals("EdgeType"))
			{
				handleDroppedEdge (attributeValue, e);
			}
		}	
	}
	
	/**
	 * specialized processing for a dropped shape that represents an edge.
	 * if the edge shape is dropped on a node, then start an edge from the  node that is
	 * dropped on.  Subsequent movements of the mouse extend the edge, as in the BasicNetworkEditEventHandler.
	 * A mouse click when over the desired target node completes the edge.
	 * 
	 * @param attributeValue the type of the edge
	 * @param e the drop event
	 */
	public void handleDroppedEdge (String attributeValue, PhoebeCanvasDropEvent e)
	{
		Point location = e.getLocation();
		Point2D locn = (Point2D) location.clone();
		locn = canvas.getCamera().localToView(locn);
		
		editEvent = this;
		if (getMode() != SELECT_MODE) {
			// if there is another edit in progress, then don't process a drag/drop
			return;
		}
		NodeView targetNode = findEdgeDropTarget (locn);
		if (targetNode == null)
		{
			return;
		}

		// if we reach this point, then the edge shape has been dropped onto a nod3e
		nextPoint = e.getLocation();
		boolean onNode = true;
		locator.setNode((PNode) targetNode);
		nextPoint = locator.locatePoint(nextPoint);
		nextPoint = ((PNode) targetNode).localToGlobal(nextPoint);
		if (onNode && !(edgeStarted)) {
			// Begin Edge creation			
			setHandlingEdgeDrop(true);
			setMode(CONNECT_MODE);
			edgeStarted = true;
			setEdgeStarted(true);
			setNode(targetNode);
			edge = new PPath();			
			getCanvas().getLayer().addChild(edge);

			edge.setStroke(new PFixedWidthStroke(3));
			edge.setPaint(Color.black);
			startPoint = nextPoint;
			updateEdge();
			setEdge(edge);
			setStartPoint(startPoint);
		}
	}

	/**
	 * determine which node the edge has been dropped on, if any
	 * <p>
	 * TODO: findEdgeDropTarget currently iterates through all of the Nodes in the current network and 
	 * checks whether the drop event position is contained within the bounds of the node.    Is 
	 * there a more efficient way to do this?
	 * @param location the location of the drop event
	 * @return the NodeView that is located at the drop location.
	 */
	public NodeView findEdgeDropTarget (Point2D location)
	{
		double locnX = location.getX();
		double locnY = location.getY();
		Iterator it = Cytoscape.getCurrentNetworkView().getNodeViewsIterator();
		NodeView nv;
		while (it.hasNext())
		{
			nv = (NodeView) it.next();
			if ((locnX >= nv.getXPosition() - nv.getWidth()) &&
					(locnX <= nv.getXPosition() + nv.getWidth()) &&
					(locnY >= nv.getYPosition() - nv.getHeight()) &&
					(locnY <= nv.getYPosition() + nv.getHeight()))
			{
				return nv;
			}
		}		
		return null;
	}
	
	
	/**
	 * A stub routine that currently just adds a node at the drop position.
	 * In theory, the URL can be traversed and the retrieved document parsed to
	 * extract information that can be added to the Cytoscape network.  For example,
	 * the user could drag a URL an NCBI Unigene Web page and a node could be added 
	 * to the network for that gene, with certain pieces of information on that Web
	 * extracted and assigned as attributes for that gene. 
	 * @param t the Transferable that is dropped onto the canvas
	 * @param d the DataFlavor that represents the dropped URL.
	 * @param location the location of the drop event.
	 */
	public void handleDroppedURL (Transferable t, DataFlavor d, Point location)
	{
		Object URL;
		try
		{
		    URL = t.getTransferData(d);
		    if (URL != null)
		    {
		    	String URLString = URL.toString();
				CyNode cn = CytoscapeEditorManager.addNode("node" + counter, 
						true, "URL");
			    counter++;				
				double zoom = Cytoscape.getCurrentNetworkView().getZoom();
				Cytoscape.getCurrentNetwork().restoreNode(cn);						
				NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
		    }
		}
		catch (UnsupportedFlavorException exc)
				{
					exc.printStackTrace();
					return;
				}
		catch (IOException exc)
				{
					exc.printStackTrace();
					return;					
				}		
	}	
}

