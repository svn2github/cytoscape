package org.cytoscape.groups.results;

// System imports
import giny.view.NodeView;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import metaNodePlugin2.model.MetaNode;

//import metaNodePlugin2.model.MetaNode;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;

//Cytoscape group system imports
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupViewer;
import cytoscape.groups.CyGroupViewer.ChangeType;

import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupChangeListener;

import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import org.cytoscape.groups.results.LayoutRegion;
import ding.view.DGraphView;

/**
 * The GroupPanel is the implementation for the Cytopanel that presents the
 * group list mechanism to the user.
 */
public class GroupHandler implements 
		CyGroupChangeListener, CyGroupViewer {

	public String viewerName = "moduleFinderViewer";
	private static boolean registeredWithGroupPanel = false;

	// Controlling variables
	public static boolean multipleEdges = false;
	public static boolean recursive = true;
	
	private Method updateMethod = null;
	private CyGroupViewer namedSelectionViewer = null;
	
	// State values
	public static final int EXPANDED = 1;
	public static final int COLLAPSED = 2;


	public GroupHandler() {
		super();

		CyGroupManager.addGroupChangeListener(this);
		
		//register groupViewer
		CyGroupManager.registerGroupViewer(this);
		
	}

	
	// Determine the region (rectangle:x,y,w,h) for all nodes in a given group 
	private double[] getRectValues(CyGroup pGroup){

		Iterator it = pGroup.getNodeIterator();
		
		CyNode node = (CyNode) it.next();
		NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(node);
		
		double minX = Double.POSITIVE_INFINITY; 
		double maxX = Double.NEGATIVE_INFINITY; 
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		
		double scaleFactor = Cytoscape.getCurrentNetworkView().getZoom();	

		DGraphView theView = (DGraphView) Cytoscape.getCurrentNetworkView();

		double w = theView.getComponent().getBounds().getWidth();
		double h = theView.getComponent().getBounds().getHeight();
		
		double xCenter = theView.getCenter().getX();
		double yCenter = theView.getCenter().getY();
		
		while (it.hasNext()){
			node = (CyNode) it.next();
			nv = Cytoscape.getCurrentNetworkView().getNodeView(node);
			
			
			if (nv.getXPosition() < minX){
				minX = nv.getXPosition();
			}
			if (nv.getXPosition() > maxX){
				maxX = nv.getXPosition();
			}
			
			if (nv.getYPosition() < minY){
				minY = nv.getYPosition();
			}
			if (nv.getYPosition() > maxY){
				maxY = nv.getYPosition();
			}				
		}
				
		double[] rectValues = new double[4];
		
		rectValues[0] = w/2 - (xCenter - minX)*scaleFactor;
		rectValues[1] = h/2 - (yCenter - minY)*scaleFactor;
		rectValues[2] = (maxX - minX)*scaleFactor;
		rectValues[3] = (maxY - minY)*scaleFactor;

		return rectValues;
	}

	
	
	//required by CyGroupChangeListener
	public void groupChanged(CyGroup group, CyGroupChangeListener.ChangeType change) { 
		if ( change == CyGroupChangeListener.ChangeType.GROUP_CREATED ) {

			System.out.println("\nGroupHandler: GROUP_CREATE");
			 
			//determine the rectangle region (x,y,w,h)
			double[] region = getRectValues(group);
			
			//Create LayoutRegion object
			LayoutRegion layoutRegion = new LayoutRegion(region[0],region[1],region[2],region[3], null, null);
			
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_DELETED ) {
			System.out.println("GroupHandler: GROUP_DELETED");

			//resultPanel.removeGroup(group);
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_MODIFIED ) {
			System.out.println("GroupHandler: GROUP_MODIFIED");
		} else {
			System.err.println("unsupported change Group ChangeType");
		}
	}
	
	//
	// These are required by the CyGroupViewer interface
	/**
	 * Return the name of our viewer
	 *
	 * @return viewer name
	 */
	public String getViewerName() { return viewerName; }
	

	/**
	 * This is called when a new group has been created that
	 * we care about.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that was just created
	 */
	public void groupCreated(CyGroup group) { 

		System.out.println("groupCreated()");
	}

	/**
	 * This is called when a new group has been created that
	 * we care about.  This version of the groupCreated
	 * method is called by XGMML and provides the CyNetworkView
	 * that is in the process of being created.
	 *
	 * @param group the CyGroup that was just created
	 * @param view the CyNetworkView that is being created
	 */
	public void groupCreated(CyGroup group, CyNetworkView myview) { 
		System.out.println("groupCreated() Apple");

	}

	/**
	 * This is called when a group we care about is about to 
	 * be deleted.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) {
		System.out.println("groupWillBeRemoved()");
	}


	
	/**
	 * This is called when a group we care about has been
	 * changed (usually node added or deleted).
	 *
	 * @param group the CyGroup that has changed
	 * @param node the CyNode that caused the change
	 * @param change the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, CyGroupViewer.ChangeType change) { 
		System.out.println("groupChanged()");
	}


	
	
}
