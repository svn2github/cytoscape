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
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.editor.impl.ShapePalette;
import edu.umd.cs.piccolo.event.PInputEvent;
import giny.view.NodeView;

/**
 * @author ajk
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BioPAXNetworkEditEventHandler extends
		PaletteNetworkEditEventHandler {
	
	private String attributeName;
	private String attributeValue;

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
	
	public CyNode createNode (PInputEvent e)
	{
		CyNode cn = super.createNode(e);
		CyNetwork net = Cytoscape.getCurrentNetwork();
		Cytoscape.setNodeAttributeValue(cn, "BIOPAX_NAME",
				cn.getIdentifier());
		return cn;
	}

	public CyEdge finishEdge (PInputEvent e)
	{
		CyEdge edge = super.finishEdge(e);
		CyNetwork net = Cytoscape.getCurrentNetwork();
		Cytoscape.setEdgeAttributeValue(edge, "BIOPAX_EDGE_TYPE",
				attributeValue);
		
		return edge;
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
			// AJK: 09/17/05 make attribute name and value global, so that BioPAX attributes can be set
			 attributeName = myShape.getAttributeName();
			 System.out.println ("Item dropped: AttributeName = " + attributeName);
			attributeValue = myShape.getAttributeValue();

			args = new Object []{ "LOCATION", location};
			if (attributeName.equals(NODE_TYPE)
//					||
//					(attributeName.equals("BIOPAX_NODE_TYPE")))  // TODO: incorporate the processing
				// of BIOPAX_NODE_TYPE into the SimpleBioMoleculeEditor class
					)
			{
				// TODO: move node creation into super.createNode();
				CyNode cn = CytoscapeEditorManager.addNode("node" + counter, 
						true, attributeName, attributeValue);
			    counter++;				
				double zoom = Cytoscape.getCurrentNetworkView().getZoom();
				Cytoscape.getCurrentNetwork().restoreNode(cn);		
				NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
				nv.setOffset(locn.getX(), locn.getY());
				// hack for biopax, fix later
				Cytoscape.setNodeAttributeValue(cn, "BIOPAX_NAME", cn.getIdentifier());
			}
			else if ( (attributeName.equals("EdgeType")) ||
					(attributeName.equals("BIOPAX_EDGE_TYPE")))
			{
				handleDroppedEdge (attributeValue, e);
			}
		}	
	}	
	
}
