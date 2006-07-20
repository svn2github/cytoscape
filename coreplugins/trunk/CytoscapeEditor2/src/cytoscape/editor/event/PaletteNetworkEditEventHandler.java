/*
 * Created on Jul 31, 2005
 *
 */
package cytoscape.editor.event;

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

import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import phoebe.PhoebeCanvasDropEvent;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.editors.SimpleBioMoleculeEditor;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;

/**
 * 
 * The <b>PaletteNetworkEditEventHandler</b> extends the basic network edit event handler with the
 * capability to drag and drop shapes from a palette onto the canvas, resulting in the addition of 
 * nodes and edges to the current Cytoscape network.  
 * 
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
		
		Object shape = null;
		String shapeName = null;
		
		// AJK: 07/17/06 BEGIN Debugging code
		DGraphView view = (DGraphView) Cytoscape.getCurrentNetworkView();
		JLabel button = new JLabel ("test 1");
//		view.getCanvas().add(button);
		view.getBackgroundCanvas().add(button);
		System.out.println("added component to background canvas: " + view.getBackgroundCanvas());
		
		button.setLocation(e.getLocation());
		button.setPreferredSize(new Dimension (70, 20));
		button.setOpaque(true);
		button.setBackground(Color.BLUE);
		button.setVisible(true);
		view.getCanvas().repaint();
		// AJK: 07/17/06 END
	
		Point location = e.getLocation();
	
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
		    		 else if (t.isDataFlavorSupported(dfl[i])) // should be d
		    		 {
		    			 try
		    			 {
		    				 // should be d
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

		    if (shape != null)
		    {
		    	shapeName = shape.toString();
		    	myShape = ShapePalette.getBasicCytoShapeEntity(shapeName);		    }

        if (myShape != null)
		{
			// need to handle nodes and edges differently 
			String attributeName = myShape.getAttributeName();
			String attributeValue = myShape.getAttributeValue();
			
//			System.out.println("Item dropped of type: " + attributeName);

			if (attributeName.equals(PaletteNetworkEditEventHandler.NODE_TYPE))
			{
				this.setNodeAttributeName(attributeName);
				this.setNodeAttributeValue(attributeValue);
				_caller.addNode("node" + counter, 
						attributeName, attributeValue, location);
			    counter++;				
			}
			else if (attributeName.equals(PaletteNetworkEditEventHandler.EDGE_TYPE))
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
		if (edgeStarted)
		{
			// if there is another edit in progress, then don't process a drag/drop
			return;
		}
		// NB: targetNode is *drop* target
		NodeView targetNode = findEdgeDropTarget (locn);
//		System.out.println ("drop target = " + targetNode);
		if (targetNode == null)
		{
			return;
		}

		// if we reach this point, then the edge shape has been dropped onto a nod3e
		nextPoint = e.getLocation();
		boolean onNode = true;
		if (onNode && !(edgeStarted)) {
			// Begin Edge creation			
			setHandlingEdgeDrop(true);
			edgeStarted = true;
			setEdgeStarted(true);
			setNode(targetNode);
//			edge = new PPath();			
//
//			edge.setStroke(new PFixedWidthStroke(3));
//			edge.setPaint(Color.black);
			startPoint = nextPoint;
			updateEdge();
			setStartPoint(startPoint);
		}
	}

	/**
	 * determine which node the edge has been dropped on, if any
	 * <p>
	 * TODO: findEdgeDropTarget currently iterates through all of the Nodes in the current network and 
	 * checks whether the drop event position is contained within the bounds of the node.    Is 
	 * there a more efficient way to do this?
	 * TODO: 06/22/06: update this to use new renderer routines for finding nodes intersecting point
	 * @param location the location of the drop event
	 * @return the NodeView that is located at the drop location.
	 */
	public NodeView findEdgeDropTarget (Point2D location)
	{
		
		double[] locn = new double[2];
		locn[0] = location.getX();
		locn[1] = location.getY();
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
		    	//			    System.out.println ("Handling dropped URL = " + URLString);
		    	CyNode cn = _caller.addNode("node" + counter, 
						"URL");
			    counter++;				
				Cytoscape.getCurrentNetwork().restoreNode(cn);

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

