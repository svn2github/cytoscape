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

import phoebe.PhoebeCanvasDropEvent;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import edu.umd.cs.piccolo.event.PInputEvent;
import giny.model.Node;
import giny.view.NodeView;

/**
 * Event handler for SimpleBioPAX_Editor.  Pretty much equivalent yp
 * SimpleBioMoleculeEditor, except that BIOPAX_NODE_TYPE and BIOPAX_EDGE_TYPE are
 * ued as the controlling variable.
 * @author ajk
 *
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
	}

	/**
	 * @param caller
	 */
	public BioPAXNetworkEditEventHandler(CytoscapeEditor caller) {
		super(caller);
	}

	/**
	 * 
	 * @param caller
	 * @param view
	 */
	public BioPAXNetworkEditEventHandler(CytoscapeEditor caller, CyNetworkView view) {
		super(caller, view);
	}
		
	
	/**
	 * create node at point of location
	 */
//	public CyNode createNode (PInputEvent e)
	public CyNode createNode (Point2D location)
	{
		CyNode cn = super.createNode(location);
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
		edgeStarted = false;
		updateEdge();

		NodeView target = (NodeView) e.getPickedNode();
		// From Earlier
		NodeView source = node;

		Node source_node = source.getNode();
		Node target_node = target.getNode();

		CyEdge myEdge = _caller.addEdge(source_node,
				target_node, cytoscape.data.Semantics.INTERACTION,
				this.getEdgeAttributeValue(), true, this.getEdgeAttributeValue());   // set to BIOPAX_EDGE_TYPE

		edgeAttribs.setAttribute(myEdge.getIdentifier(), "BIOPAX_EDGE_TYPE",
				this.getEdgeAttributeValue());		//				Cytoscape.getCurrentNetwork().restoreEdge(myEdge);

		edge = null;
		node = null;
		if (isHandlingEdgeDrop()) {
			this.setHandlingEdgeDrop(false);
		}
		
		// AJK: 11/19/05 invert selection of target, which will have had its selection inverted upon mouse entry
		target.setSelected(!target.isSelected());
		
		// redraw graph so that the correct arrow is shown (but only if network is small enough to see the edge...
		if (Cytoscape.getCurrentNetwork().getNodeCount() <= 100)
		{
			Cytoscape.getCurrentNetworkView ().redrawGraph(true, true);
			
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
//		PGraphView thisView = this.getView();
		DGraphView thisView = this.getView();

		if (thisView != ((DGraphView) Cytoscape.getCurrentNetworkView()))
		{
			return;
		}
		
		Object shape = null;
		String shapeName = null;

		Point location = e.getLocation();
				
		Point2D locn = (Point2D) location.clone();
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


		    if (shape != null)
		    {
		    	shapeName = shape.toString();
		    	myShape = ShapePalette.getBasicCytoShapeEntity(shapeName);		    }
				

        if (myShape != null)
		{
			// need to handle nodes and edges differently 
			// AJK: 09/17/05 make attribute name and value global, so that BioPAX attributes can be set
			String attributeName = myShape.getAttributeName();
			String attributeValue = myShape.getAttributeValue();
				
			if (attributeName.equals(NODE_TYPE)
					)
			{
				// TODO: move node creation into super.createNode();
			
				this.setNodeAttributeName(attributeName);
				this.setNodeAttributeValue(attributeValue);
				CyNode cn = _caller.addNode("node" + counter, 
						attributeName, attributeValue);
			    counter++;				
				Cytoscape.getCurrentNetwork().restoreNode(cn);		
				NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
				nv.setOffset(locn.getX(), locn.getY());
				// hack for biopax, fix later
				nodeAttribs.setAttribute(cn.getIdentifier(), "BIOPAX_NAME", cn.getIdentifier());
			}
			else if ( (attributeName.equals(BioPAXNetworkEditEventHandler.EDGE_TYPE)) ||
					(attributeName.equals("BIOPAX_EDGE_TYPE")))
			{
				this.setEdgeAttributeName(attributeName);
				this.setEdgeAttributeValue(attributeValue);
				handleDroppedEdge (attributeValue, e);
			}
		}	
	}		
}
