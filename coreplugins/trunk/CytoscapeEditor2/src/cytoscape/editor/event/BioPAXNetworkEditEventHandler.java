/*
 * Created on Sep 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cytoscape.editor.event;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.io.IOException;

import phoebe.PGraphView;
import phoebe.PhoebeCanvasDropEvent;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.view.CyNetworkView;
import edu.umd.cs.piccolo.event.PInputEvent;
import giny.model.Node;
import giny.view.NodeView;

/**
 * @author ajk
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BioPAXNetworkEditEventHandler extends
		PaletteNetworkEditEventHandler {
	
	
	/**
	 * main data structures for all node and edge attributes
	 */
	public static cytoscape.data.CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();	
	public static cytoscape.data.CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();	

	
	/**
	 * 
	 */
	public BioPAXNetworkEditEventHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param caller
	 */
	public BioPAXNetworkEditEventHandler(CytoscapeEditor caller) {
		super(caller);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param caller
	 * @param view
	 */
	public BioPAXNetworkEditEventHandler(CytoscapeEditor caller, CyNetworkView view) {
		super(caller, view);
	}
		
		
	public CyNode createNode (PInputEvent e)
	{
		CyNode cn = super.createNode(e);
		CyNetwork net = Cytoscape.getCurrentNetwork();
		nodeAttribs.setAttribute(cn.getIdentifier(), "BIOPAX_NAME",
				cn.getIdentifier());
		return cn;
	}
	
	
	/**
	 * finish edge on node containing input point
	 * @param e input event for mouse press
	 */
	public CyEdge finishEdge (PInputEvent e)
	{
		System.out.println ("Finishing edge in BioPAX network event handler");
		edgeStarted = false;
		updateEdge();

		// From the Pick Path
		NodeView target = (NodeView) e.getPickedNode();
		// From Earlier
		NodeView source = node;

		Node source_node = source.getNode();
		Node target_node = target.getNode();

		CyEdge myEdge = _caller.addEdge(source_node,
				target_node, cytoscape.data.Semantics.INTERACTION,
				"default", true, this.getEdgeAttributeValue());   // set to BIOPAX_EDGE_TYPE

		System.out.println("setting BIOPAX_EDGE_TYPE for " + myEdge.getIdentifier() + " to " + this.getEdgeAttributeValue());
		edgeAttribs.setAttribute(myEdge.getIdentifier(), "BIOPAX_EDGE_TYPE",
				this.getEdgeAttributeValue());		//				Cytoscape.getCurrentNetwork().restoreEdge(myEdge);

		getCanvas().getLayer().removeChild(edge);
		edge = null;
		node = null;
		if (isHandlingEdgeDrop()) {
//			setMode(SELECT_MODE);
			this.setHandlingEdgeDrop(false);
		}
		
		// AJK: 11/19/05 invert selection of target, which will have had its selection inverted upon mouse entry
		target.setSelected(!target.isSelected());
		
		// redraw graph so that the correct arrow is shown (but only if network is small enough to see the edge...
		if (Cytoscape.getCurrentNetwork().getNodeCount() <= 100)
		{
			Cytoscape.getDesktop().redrawGraph(true, true);
			
		}
				
		return myEdge;
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
		PGraphView thisView = this.getView();
		if (thisView != ((PGraphView) Cytoscape.getCurrentNetworkView()))
		{
			return;
		}
		
		Object shape = null;
		String shapeName = null;

		Point location = e.getLocation();
		
		System.out.println ("Item dropped at: " + e.getLocation());
		System.out.println ("Bounds of current view are: " +
				Cytoscape.getCurrentNetworkView().getComponent().getBounds());
		
		Point2D locn = (Point2D) location.clone();
		locn = canvas.getCamera().localToView(locn);
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
			// AJK: 09/17/05 make attribute name and value global, so that BioPAX attributes can be set
			String attributeName = myShape.getAttributeName();
			String attributeValue = myShape.getAttributeValue();
//			 System.out.println ("Item dropped: AttributeName = " + attributeName + ": " + attributeValue);
				
			args = new Object []{ "LOCATION", location};
			if (attributeName.equals(NODE_TYPE)
//					||
//					(attributeName.equals("BIOPAX_NODE_TYPE")))  // TODO: incorporate the processing
				// of BIOPAX_NODE_TYPE into the SimpleBioMoleculeEditor class
					)
			{
				// TODO: move node creation into super.createNode();
			
				this.setNodeAttributeName(attributeName);
				this.setNodeAttributeValue(attributeValue);
				CyNode cn = _caller.addNode("node" + counter, 
						attributeName, attributeValue);
			    counter++;				
				double zoom = Cytoscape.getCurrentNetworkView().getZoom();
				Cytoscape.getCurrentNetwork().restoreNode(cn);		
				NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
				nv.setOffset(locn.getX(), locn.getY());
				// hack for biopax, fix later
				nodeAttribs.setAttribute(cn.getIdentifier(), "BIOPAX_NAME", cn.getIdentifier());
			}
			else if ( (attributeName.equals(this.EDGE_TYPE)) ||
					(attributeName.equals("BIOPAX_EDGE_TYPE")))
			{
				this.setEdgeAttributeName(attributeName);
				this.setEdgeAttributeValue(attributeValue);
				handleDroppedEdge (attributeValue, e);
			}
		}	
	}	
	
}
