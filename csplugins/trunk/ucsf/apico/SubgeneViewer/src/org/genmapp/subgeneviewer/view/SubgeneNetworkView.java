package org.genmapp.subgeneviewer.view;

import java.awt.Rectangle;

import javax.swing.JPanel;

import cytoscape.CyNode;

public class SubgeneNetworkView extends JPanel{

//	private CyNetworkView cynetworkView;
//	
//	public CyNetworkView getCynetworkView() {
//		return cynetworkView;
//	}
//
//	public void setCynetworkView(CyNetworkView cynetworkView) {
//		this.cynetworkView = cynetworkView;
//	}

	private CyNode parentNode = null;
	public CyNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(CyNode parentNode) {
		this.parentNode = parentNode;
	}

	public SubgeneNetworkView()
	{
	
	}

	/**
	 * get bounding box of a Block, Region, or Feature
	 * 
	 * @param obj
	 * @return
	 */
	public Rectangle getBounds (SubgeneNodeView obj)
	{
		Rectangle bounds = obj.getBounds();
		if (bounds == null)
		{
			bounds = obj.computeBounds();
		}
		if (bounds == null)
		{
			System.err.println("Internal error computing bounding box for "
					+ obj + " with ID " + obj.getId());
		}		
		return bounds;
	}
	
	
}
