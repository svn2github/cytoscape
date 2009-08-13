/*
 * File: CyFrame.java
 * Google Summer of Code
 * Written by Steve Federowicz with help from Scooter Morris
 * 
 * The CyFrame class is essentially a wrapper on a CyNetworkView. It works by having a populate() method which essentially extracts the
 * necessary view data from the current CyNetworkView and stores it in the CyFrame. Each CyFrame also contains a display() method which
 * updates the current network view based upon the visual data stored in that particular CyFrame. It also can hold an image of the network
 * and contains a facility for writing this image to a file. 
 * 
 */


package CyAnimator;   


import cytoscape.*;

import cytoscape.layout.*;

import cytoscape.visual.*;

import ding.view.EdgeContextMenuListener;

import ding.view.NodeContextMenuListener;

import giny.view.*;

import java.util.*;

import giny.model.Node;
import giny.view.NodeView;
import giny.model.Edge;
import giny.view.EdgeView;

import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.*;
import java.awt.Paint;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.ding.DingNetworkView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;
import cytoscape.visual.VisualStyle;

import cytoscape.util.export.BitmapExporter;
import cytoscape.util.export.Exporter;

import ding.view.DGraphView;

public class CyFrame {
	
	private String frameid = "";
	private HashMap<String, double[]> nodePosMap;
	private HashMap<String, Color> nodeColMap;
	private HashMap<String, Integer> nodeOpacityMap;
	private HashMap<String, Double> nodeBorderMap;

	private HashMap<String, Integer> edgeOpacityMap;
	private HashMap<String, Color> edgeColMap;
	
	private Paint backgroundPaint = null;
	private double zoom = 0;
	
	private double xalign;
	private double yalign;
	
	private CyNetworkView networkView = null;
	private CyNetwork currentNetwork = null;
	private BufferedImage networkImage = null;
	private Map<Node, NodeView> nodeMap = null;
	private Map<Edge, EdgeView> edgeMap = null;
	private VisualStyle vizStyle = null;
	private List<Node> nodeList = null;
	private List<Edge> edgeList = null;
	private List<NodeView> nodeViewList = null;
	private List<EdgeView> edgeViewList = null;
	private int intercount = 0;
	private Point2D centerPoint = null;
	private DGraphView dview = null; 
	
	/**
	 * Creates this CyFrame by initializing and populating all of the fields.
	 * 
	 * @param currentNetwork
	 */
	public CyFrame(CyNetwork currentNetwork){
		nodePosMap = new HashMap<String, double[]>();
		nodeColMap = new HashMap<String, Color>();
		edgeMap = new HashMap();
		nodeMap = new HashMap();
		nodeOpacityMap = new HashMap<String, Integer>();
		edgeOpacityMap = new HashMap<String, Integer>();
		edgeColMap = new HashMap<String, Color>();
		this.currentNetwork = currentNetwork;
		networkView = Cytoscape.getCurrentNetworkView();
		this.dview = (DGraphView)networkView;
		this.centerPoint = dview.getCenter();

		nodeViewList = new ArrayList();
		edgeViewList = new ArrayList();
		
		// Initialize our node view maps
		Iterator<EdgeView> eviter = networkView.getEdgeViewsIterator();
		while(eviter.hasNext()) {
			EdgeView ev = eviter.next();
			edgeMap.put(ev.getEdge(), ev);
			edgeViewList.add(ev);
		}

		// Initialize our edge view maps
		Iterator<NodeView> nviter = networkView.getNodeViewsIterator();
		while(nviter.hasNext()) {
			NodeView nv = nviter.next();
			nodeMap.put(nv.getNode(), nv);
			nodeViewList.add(nv);
		}

		// Remember the visual style
		vizStyle = Cytoscape.getVisualMappingManager().getVisualStyle();

		// Get our initial nodeList
		nodeList = currentNetwork.nodesList();

		// Get our initial edgeList
		edgeList = currentNetwork.edgesList();
		
		
	}
	
	/*
	 * Captures all of the current visual settings for nodes and edges from a 
	 * CyNetworkView and stores them in this frame.
	 */
	public void populate() {
		backgroundPaint = networkView.getBackgroundPaint();
		zoom = networkView.getZoom();
		xalign = networkView.getComponent().getAlignmentX();
		yalign = networkView.getComponent().getAlignmentY();
		
		dview = (DGraphView)networkView;
		
		for(Node node: nodeList){
		
			NodeView nodeView = networkView.getNodeView(node);
			if(nodeView == null){ continue; }
			
			//stores the x and y position of the node
			double[] xy = new double[2];
			xy[0] = nodeView.getXPosition();
			xy[1] = nodeView.getYPosition();
			nodePosMap.put(node.getIdentifier(), xy);
			
			//grab color and opacity
			Color nodeColor = (Color)nodeView.getUnselectedPaint();
			Integer trans = nodeColor.getAlpha();
			//store in respective hashmap
			nodeColMap.put(node.getIdentifier(), (Color)nodeView.getUnselectedPaint());
			nodeOpacityMap.put(node.getIdentifier(), trans);
			
			centerPoint = dview.getCenter();
			
		}

		for(Edge edge: edgeList){
			
			EdgeView edgeView = networkView.getEdgeView(edge);
			if(edgeView == null){  continue; }
			
			//grab color and opacity
			Color p = (Color)edgeView.getUnselectedPaint();
			Integer trans = p.getAlpha();
			//store in respective hashmap
			edgeColMap.put(edge.getIdentifier(), p);
			edgeOpacityMap.put(edge.getIdentifier(), trans);
		
		}
	}
	
	/**
	 * Captures and stores a thumbnail image from the current CyNetworkView for
	 * this frame.
	 */
	public void captureImage() {
		
		double scale = .35;
		double wscale = .25;

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		
		
		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		int width  = (int) (ifc.getWidth() * wscale);
		int height = (int) (ifc.getHeight() * scale);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = (Graphics2D) image.getGraphics();
		g.scale(scale, scale);
		
		//ifc.paint(g);
		ifc.print(g);
		g.dispose();

		networkImage = image;
	
	}
	
	/*
	 * Cycles through the list of nodes and edges and updates the node and edge views 
	 * based upon the visual data stored as part of the CyFrame.  
	 */
	public void display() {

		Cytoscape.getVisualMappingManager().setVisualStyle(vizStyle);

		// We want to use the current view in case we're interpolating
		// across views
		CyNetworkView currentView = Cytoscape.getCurrentNetworkView();


		// First see if we have any views we need to remove
		List<EdgeView> removeEdgeViews = new ArrayList();
		Iterator<EdgeView> eviter = currentView.getEdgeViewsIterator();
		while(eviter.hasNext()) {
			EdgeView ev = eviter.next();
			if (!edgeMap.containsKey(ev.getEdge()))
				removeEdgeViews.add(ev);
		}

		for (EdgeView ev: removeEdgeViews)
			currentView.removeEdgeView(ev);

		// Initialize our edge view maps
		List<NodeView> removeNodeViews = new ArrayList();
		Iterator<NodeView> nviter = currentView.getNodeViewsIterator();
		while(nviter.hasNext()) {
			NodeView nv = nviter.next();
			if (!nodeMap.containsKey(nv.getNode()))
				removeNodeViews.add(nv);
		}

		for (NodeView nv: removeNodeViews)
			currentView.removeNodeView(nv);


		for(Node node: nodeList)
		{
		
			NodeView nodeView = currentView.getNodeView(node);
			if (nodeView == null) {
				addNodeView(currentView, nodeMap.get(node), node);
				nodeView = currentView.getNodeView(node);
				Cytoscape.getVisualMappingManager().vizmapNode(nodeView, currentView);
			}
			
			double[] xy = nodePosMap.get(node.getIdentifier());
			Color p = nodeColMap.get(node.getIdentifier());
			Integer trans = nodeOpacityMap.get(node.getIdentifier());
			// System.out.println("DISPLAY "+node+": "+xy[0]+"  "+xy[1]+", trans = "+trans);
			//if(xy == null || nodeView == null){ continue; }
			
			nodeView.setXPosition(xy[0]);
			nodeView.setYPosition(xy[1]);
			nodeView.setUnselectedPaint(new Color(p.getRed(), p.getGreen(), p.getBlue(), trans));
			
		}
		for(Edge edge: getEdgeList())
		{
			EdgeView edgeView = currentView.getEdgeView(edge);
			if (edgeView == null) {
				addEdgeView(currentView, edgeMap.get(edge), edge);
				edgeView = currentView.getEdgeView(edge);
			}
			Color p = edgeColMap.get(edge.getIdentifier());
			if (p == null || edgeView == null) continue;
			Integer trans = edgeOpacityMap.get(edge.getIdentifier());
			edgeView.setUnselectedPaint(new Color(p.getRed(), p.getGreen(), p.getBlue(), trans));
		}
		currentView.setBackgroundPaint(backgroundPaint);
		currentView.setZoom(zoom);
		//networkView.getComponent().
		dview = (DGraphView)currentView;
		
		//InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(networkView);
		
		dview.setCenter(centerPoint.getX(), centerPoint.getY());
		
		//dview.setBounds(x, y, Math.round(ifc.getWidth()), Math.round(ifc.getHeight()));
		//ifc.setBounds(arg0, arg1, arg2, arg3)
		currentView.updateView();
	}

	/**
	 * Return the frame ID for this frame
	 * 
	 * @return the frame ID
	 * 
	 */
	public String getID() {
		return frameid;
	}

	public void setID(String ID) {
		frameid = ID;
	}

	/**
	 * Return the CyNetwork for this frame
	 *
	 * @return the CyNetwork
	 */
	public CyNetwork getCurrentNetwork() {
		return currentNetwork;
	}

	/**
	 * Return the number of frames to be interpolated between this frame and the next.
	 *
	 * @return the frame number
	 */
	public int getInterCount() {
		return intercount;
	}

	/**
	 * Set the number of frames to be interpolated between this frame and the next.
	 *
	 * @param interCount the number of frames to interpret
	 */
	public void setInterCount(int intercount) {
		this.intercount = intercount;
	}

	/**
	 * Return the zoom value for this frame.
	 *
	 * @return zoom
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Set the zoom value for this frame.
	 *
	 * @param zoom set the zoom value
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	/**
	 * Return the background color value for this frame.
	 *
	 * @return the background color
	 */
	public Paint getBackgroundPaint() {
		return backgroundPaint;
	}

	/**
	 * Set the background color value for this frame.
	 *
	 * @param bg set the background color
	 */
	public void setBackgroundPaint(Paint bg) {
		backgroundPaint = bg;
	}

	/**
	 * Get the node position for a node in this frame
	 *
	 * @param nodeID the ID of the node whose position to retrieve
	 * @return the node position as a double array with two values
	 */
	public double[] getNodePosition(String nodeID) {
		return nodePosMap.get(nodeID);
	}

	/**
	 * Set the node position for a node in this frame
	 *
	 * @param nodeID the ID of the node whose position to retrieve
	 * @param pos a 2 element double array with the x,y values for this node
	 */
	public void setNodePosition(String nodeID, double[] pos) {
		nodePosMap.put(nodeID, pos);
	}

	/**
	 * Get the node color for a node in this frame
	 *
	 * @param nodeID the ID of the node whose color to retrieve
	 * @return the color 
	 */
	public Color getNodeColor(String nodeID) {
		return nodeColMap.get(nodeID);
	}

	/**
	 * Set the node color for a node in this frame
	 *
	 * @param nodeID the ID of the node whose color to retrieve
	 * @param color the color for this node
	 */
	public void setNodeColor(String nodeID, Color color) {
		nodeColMap.put(nodeID, color);
	}

	/**
	 * Get the edge color for an edge in this frame
	 *
	 * @param edgeID the ID of the edge whose color to retrieve
	 * @return the color 
	 */
	public Color getEdgeColor(String edgeID) {
		return edgeColMap.get(edgeID);
	}

	/**
	 * Set the edge color for a edge in this frame
	 *
	 * @param edge the ID of the edge whose color to retrieve
	 * @param color the color for this edge
	 */
	public void setEdgeColor(String edgeID, Color color) {
		edgeColMap.put(edgeID, color);
	}

	/**
	 * Get the edge opacity for an edge in this frame
	 *
	 * @param edgeID the ID of the edge whose opacity to retrieve
	 * @return the opacity 
	 */
	public Integer getEdgeOpacity(String edgeID) {
		Integer opacity = edgeOpacityMap.get(edgeID);
		return opacity;
	}

	/**
	 * Set the edge opacity for an edge in this frame
	 *
	 * @param edge the ID of the edge whose opacity to retrieve
	 * @param opacity the opacity for this edge
	 */
	public void setEdgeOpacity(String edgeID, Integer opacity) {
		edgeOpacityMap.put(edgeID, opacity);
	}

	/**
	 * Get the node opacity for a node in this frame
	 *
	 * @param nodeID the ID of the node whose opacity to retrieve
	 * @return the opacity 
	 */
	public Integer getNodeOpacity(String nodeID) {
		Integer opacity = nodeOpacityMap.get(nodeID);
		return opacity;
	}

	/**
	 * Set the node opacity for an node in this frame
	 *
	 * @param node the ID of the node whose opacity to retrieve
	 * @param opacity the opacity for this node
	 */
	public void setNodeOpacity(String nodeID, Integer opacity) {
		nodeOpacityMap.put(nodeID, opacity);
	}

	/**
	 * Get the list of nodes in this frame
	 *
	 * @return the list of nodes
	 */
	public List<Node> getNodeList() {
		return nodeList;
	}

	/**
	 * Get the list of edges in this frame
	 *
	 * @return the list of edges
	 */
	public List<Edge> getEdgeList() {
		return edgeList;
	}

	/**
	 * Set the list of nodes in this frame
	 *
	 * @param nodeList the list of nodes
	 */
	public void setNodeList(List<Node>nodeList) {
		this.nodeList = nodeList;
	}

	/**
	 * Set the list of edges in this frame
	 *
	 * @param edgeList the list of edges
	 */
	public void setEdgeList(List<Edge>edgeList) {
		this.edgeList = edgeList;
	}

	/**
	 * Get the list of node views in this frame
	 *
	 * @return the list of node views
	 */
	public List<NodeView> getNodeViewList() {
		return nodeViewList;
	}

	/**
	 * Get the list of edge views in this frame
	 *
	 * @return the list of edge views
	 */
	public List<EdgeView> getEdgeViewList() {
		return edgeViewList;
	}

	/**
	 * Set the list of node views in this frame
	 *
	 * @param nodeViewList the list of node views
	 */
	public void setNodeViewList(List<NodeView>nodeViewList) {
		this.nodeViewList = nodeViewList;
	}

	/**
	 * Set the list of edge views in this frame
	 *
	 * @param edgeViewList the list of edges
	 */
	public void setEdgeViewList(List<EdgeView>edgeViewList) {
		this.edgeViewList = edgeViewList;
	}

	/**
	 * Get the Image for this frame
	 *
	 * @return the image for this frame
	 */
	public BufferedImage getFrameImage() {
		return this.networkImage;
	}

	/**
 	 * Export a graphic image for this frame
 	 *
 	 * @param fileName the file to write the image to
 	 */
	public void writeImage(String fileName) {
		display();
		CyNetworkView curView = Cytoscape.getCurrentNetworkView();
		// Get the component to export
		InternalFrameComponent ifc =
		         Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(curView);
		
	
		// Handle the exportTextAsShape property
		DGraphView theViewToPrint = (DingNetworkView) curView;
		boolean exportTextAsShape =
		     new Boolean(CytoscapeInit.getProperties().getProperty("exportTextAsShape")).booleanValue();

		theViewToPrint.setPrintingTextAsShape(exportTextAsShape);
		Exporter pngExporter = new BitmapExporter("png", 5.0f);
		Exporter jpegExporter = new BitmapExporter("jpeg", 4.0f);
		
		try {
			FileOutputStream outputFile = new FileOutputStream(fileName);
			//pngExporter.export(curView, outputFile);
			jpegExporter.export(curView, outputFile);
			outputFile.close();
		} catch (IOException e) {
			//
		}
		
		System.out.println("written?");
	}

	/**
	 * Get the center point for the frame
	 * 
	 * @return the center for this frame
	 */
	public Point2D getCenterPoint() {
		return this.centerPoint;
	}

	/**
	 * Set the center point of the frame
	 * 
	 * @param center point for a frame
	 */
	public void setCenterPoint(Point2D pnt) {
		this.centerPoint = pnt;
	}
	
	// At some point, need to pull the information from nv
	// and map it to the new nv.
	private void addNodeView(CyNetworkView view, NodeView nv, Node node) {
		view.addNodeView(node.getRootGraphIndex());
	}

	private void addEdgeView(CyNetworkView view, EdgeView ev, Edge edge) {
		view.addEdgeView(edge.getRootGraphIndex());
	}

}
