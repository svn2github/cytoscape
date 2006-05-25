/*
 * Created on Jul 31, 2005
 *
 */
package cytoscape.editor.event;

import giny.view.NodeView;

import java.awt.Color;
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
import cytoscape.editor.editors.SimpleBioMoleculeEditor;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

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
	
	public static final String NODE_TYPE = "NODE_TYPE";
	
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
	
	/**
	 * The <b>itemDropped()</b> method is at the heart of the palette-based editor.  The method can
	 * respond to a variety of DataFlavors that correspond to the shape being dragged and dropped from
	 * the palette.  These include Cytoscape nodes and edges, as well as URLs that can be dragged and
	 * dropped from other applications onto the palette.
	 * 
	 */
	public void itemDropped (PhoebeCanvasDropEvent e) {
		
		// AJK: 11/20/05 return if we're not dropping into the currently active view
		// AJK: 04/02/06 go from PGraphView to DGraphView
		
//		PGraphView thisView = this.getView();
//		if (thisView != ((PGraphView) Cytoscape.getCurrentNetworkView()))
//		{
//			return;
//		}
		
		

		
//		System.out.println ("Item dropped at: " + e.getLocation());
//		System.out.println ("Bounds of current view are: " +
//				Cytoscape.getCurrentNetworkView().getComponent().getBounds());
//		
		
			
		
				
		Object shape = null;
		String shapeName = null;
		
	
		Point location = e.getLocation();
		
		// AJK: 04/02/06 BEGIN
/*		Point2D locn = (Point2D) location.clone();
		locn = canvas.getCamera().localToView(locn);
*/		// AJK: 04/02/06 END
		
		Transferable t = e.getTransferable();
		 
		BasicCytoShapeEntity myShape = null;
		
		DataFlavor [] dfl = t.getTransferDataFlavors();		
		
		for (int i = 0; i < dfl.length; i++)
		{
		    		 DataFlavor d = dfl[i];
		    		 if (d.isMimeTypeEqual("application/x-java-url"))
		    		 {
		    		 	handleDroppedURL(t, d, location);
		    		 }
		    		 else if (t.isDataFlavorSupported(dfl[i]))
		    		 {
		    			 try
		    			 {
		    				 shape =  t.getTransferData(dfl[i]);
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

//		try
//		{
//		    shape = t.getTransferData(DataFlavor.stringFlavor);

		    if (shape != null)
		    {
		    	shapeName = shape.toString();
		    	myShape = ShapePalette.getBasicCytoShapeEntity(shapeName);		    }
//		}
//		catch (UnsupportedFlavorException exc)
//				{
//					exc.printStackTrace();
//					return;
//				}
//		catch (IOException exc)
//				{
//					exc.printStackTrace();
//					return;					
//				}
				


        Object [] args = null;
		if (myShape != null)
		{
			// need to handle nodes and edges differently 
			String attributeName = myShape.getAttributeName();
			String attributeValue = myShape.getAttributeValue();
			
			System.out.println("Item dropped of type: " + attributeName);

			args = new Object []{ "LOCATION", location};
			if (attributeName.equals(this.NODE_TYPE)
//					||
//					(attributeName.equals("BIOPAX_NODE_TYPE")))  // TODO: incorporate the processing
				// of BIOPAX_NODE_TYPE into the SimpleBioMoleculeEditor class
					)
			{
				this.setNodeAttributeName(attributeName);
				this.setNodeAttributeValue(attributeValue);
				CyNode cn = _caller.addNode("node" + counter, 
						attributeName, attributeValue);
			    counter++;				
				double zoom = Cytoscape.getCurrentNetworkView().getZoom();
	
				Cytoscape.getCurrentNetwork().restoreNode(cn);		
				NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
				// AJK: 04/02/06
//				nv.setOffset(locn.getX(), locn.getY());
				nv.setOffset(location.x, location.y);
			    DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
//			    double zoomFactor = dview.getZoom();
//			    Point2D dCenter = dview.getCenter();
//			    Rectangle2D dBounds = (Rectangle2D) dview.getCanvas().getBounds();
//			    System.out.println("item dropped at local coordinate: " + location.x + "," + location.y);
//			    System.out.println("bounds are at: " + dBounds.getMinX() + "," + 
//			    		dBounds.getMinY() + " " + dBounds.getMaxX() + "," + dBounds.getMaxY());
//                System.out.println("zoom factor is: " + zoomFactor);			    
			    
			    double [] locn = new double[2];
                locn [0] = location.getX();
                locn [1] = location.getY();
                dview.xformComponentToNodeCoords(locn);
                nv.setOffset(locn[0], locn[1]);
			    
//			    nv.setOffset(xlocn, ylocn);
			    System.out.println("Offset set to: " + locn[0] + "," + locn[1]);
				
				// AJK: 04/02/06 END
				
			}
			else if (attributeName.equals(this.EDGE_TYPE))
			{
				this.setEdgeAttributeName(attributeName);
				this.setEdgeAttributeValue(attributeValue);
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
//		locn = canvas.getCamera().localToView(locn);
		
		editEvent = this;
//		if (getMode() != SELECT_MODE) {
		if (edgeStarted)
		{
			// if there is another edit in progress, then don't process a drag/drop
			return;
		}
		NodeView targetNode = findEdgeDropTarget (locn);
		System.out.println ("drop target = " + targetNode);
		if (targetNode == null)
		{
			return;
		}

		// if we reach this point, then the edge shape has been dropped onto a nod3e
		nextPoint = e.getLocation();
		boolean onNode = true;
//		locator.setNode((PNode) targetNode);
//		nextPoint = locator.locatePoint(nextPoint);
//		nextPoint = ((PNode) targetNode).localToGlobal(nextPoint);
		if (onNode && !(edgeStarted)) {
			// Begin Edge creation			
			setHandlingEdgeDrop(true);
//			setMode(CONNECT_MODE);
			edgeStarted = true;
			setEdgeStarted(true);
			setNode(targetNode);
			edge = new PPath();			
//			getCanvas().getLayer().addChild(edge);

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
//		double locnX = location.getX();
//		double locnY = location.getY();
		
		double[] locn = new double[2];
		locn[0] = location.getX();
		locn[1] = location.getY();
		int chosenNode = 0;
		this.getView().xformComponentToNodeCoords(locn);
		double locnX = locn[0];
    	double locnY = locn[1];
	
		
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
			    System.out.println ("Handling dropped URL = " + URLString);
		    	CyNode cn = _caller.addNode("node" + counter, 
						"URL");
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

