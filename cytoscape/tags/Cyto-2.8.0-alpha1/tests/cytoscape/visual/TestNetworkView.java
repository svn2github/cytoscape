
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.visual;

import cytoscape.view.CyNetworkView; 
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.view.CyEdgeView;
import cytoscape.view.CyNodeView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

import java.awt.Component;
import java.util.*;
import javax.swing.JPanel;

import giny.model.*;
import giny.view.*;
import ding.view.*;



/**
 * A dummy TestNetworkView that can be used to unit test things.
 */
public class TestNetworkView implements CyNetworkView {
	CyNetwork net;
	public TestNetworkView(CyNetwork net) {
		this.net = net;
	}
	public CyNetwork getNetwork() {
		return net;
	}
	String title;
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void redrawGraph(boolean layout, boolean vizmap) {}
	public CyNetworkView getView() { return this; }
	public VisualMappingManager getVizMapManager() {
		return Cytoscape.getVisualMappingManager();
	}
	boolean vizmapEnabled = false;
	public void toggleVisualMapperEnabled() {
		vizmapEnabled = (vizmapEnabled ? false : true);
	}
	public void setVisualMapperEnabled(boolean state) {
		vizmapEnabled = state;
	}
	public boolean getVisualMapperEnabled() {
		return vizmapEnabled;
	}
	public void putClientData(String data_name, Object data) {}
	public Collection getClientDataNames() { return null; }
	public Object getClientData(String data_name) { return null; }
	CyNode[] selectedNodes;
	public boolean setSelected(CyNode[] nodes) {
		selectedNodes = nodes;
		return true;
	}
	NodeView[] selectedNodeViews;
	public boolean setSelected(NodeView[] node_views) {
		selectedNodeViews = node_views;
		return true;
	}
	public boolean applyVizMap(CyEdge edge) {return true;}
	public boolean applyVizMap(EdgeView edge_view) {return true;}
	public boolean applyVizMap(CyNode node) {return true;}
	public boolean applyVizMap(NodeView node_view) {return true;}
	public boolean applyVizMap(CyEdge edge, VisualStyle style) {return true;}
	public boolean applyVizMap(EdgeView edge_view, VisualStyle style) {return true;}
	public boolean applyVizMap(CyNode node, VisualStyle style) {return true;}
	public boolean applyVizMap(NodeView node_view, VisualStyle style) {return true;}
	CyEdge[] selectedEdges;
	public boolean setSelected(CyEdge[] edges) {
		selectedEdges = edges;
		return true;
	}
	EdgeView[] selectedEdgeViews;
	public boolean setSelected(EdgeView[] edge_views) {
		selectedEdgeViews = edge_views;
		return true;
	}
	public void applyVizmapper(VisualStyle style) {}
	public void applyLayout(CyLayoutAlgorithm layout) {}
	public void applyLockedLayout(CyLayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges) {}
	public void applyLayout(CyLayoutAlgorithm layout, CyNode[] nodes, CyEdge[] edges) {}
	public void applyLockedLayout(CyLayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges) {}
	public void applyLayout(CyLayoutAlgorithm layout, CyNodeView[] nodes, CyEdgeView[] edges) {}
	public void applyLockedLayout(CyLayoutAlgorithm layout, int[] nodes, int[] edges) {}
	public void applyLayout(CyLayoutAlgorithm layout, int[] nodes, int[] edges) {}
	String style;
	public void setVisualStyle(String VSName) {
		style = VSName;
	}
	public VisualStyle getVisualStyle() { return null; }
	public void addNodeContextMenuListener(NodeContextMenuListener l) {}
	public void removeNodeContextMenuListener(NodeContextMenuListener l) {}
	public void addEdgeContextMenuListener(EdgeContextMenuListener l) {}
	public void removeEdgeContextMenuListener(EdgeContextMenuListener l) {}
	public  GraphPerspective getGraphPerspective() {
  		return net;
	}
	public boolean nodeSelectionEnabled() { return true; }
	public boolean edgeSelectionEnabled() { return true; }
	public void enableNodeSelection () {}
	public void disableNodeSelection () {}
	public void enableEdgeSelection () {}
	public void disableEdgeSelection () {}
	public int[] getSelectedNodeIndices() { return null; }
	public List getSelectedNodes() { return null; }
	public int[] getSelectedEdgeIndices() { return null; }
	public List getSelectedEdges() { return null; }
	public void addGraphViewChangeListener(GraphViewChangeListener listener) {} 
	public void removeGraphViewChangeListener(GraphViewChangeListener listener) {}
	Paint bgp;
	public void setBackgroundPaint(Paint paint) {
		bgp = paint;
	}
	public Paint getBackgroundPaint() {
		return bgp;
	}
	public Component getComponent() {
		return new JPanel();
	}
	List<NodeView> nodeViews = new ArrayList<NodeView>();
	public  NodeView addNodeView(int node_index) { 
		NodeView nv = new TestNodeView(node_index);
		nodeViews.add(nv);
		return nv;
	}
	List<EdgeView> edgeViews = new ArrayList<EdgeView>();
	public  EdgeView addEdgeView(int edge_index) {  
		EdgeView ev = new TestEdgeView(edge_index);
		edgeViews.add(ev);
		return ev;
	}
	public EdgeView addEdgeView(String class_name, int edge_index) { return null; }
	public NodeView addNodeView(String class_name, int node_index) { return null; }
	public NodeView addNodeView( int node_index, NodeView node_view_replacement) {return null;}
	public NodeView removeNodeView ( NodeView node_view ) { 
		nodeViews.remove(node_view);
		return node_view;
	}
	public NodeView removeNodeView ( Node node ) {return null;}
	public NodeView removeNodeView ( int node ) {return null;}
	public EdgeView removeEdgeView ( EdgeView edge_view ) {
		edgeViews.remove(edge_view);
		return edge_view;
	}
	public EdgeView removeEdgeView ( Edge edge ) {return null;}
	public EdgeView removeEdgeView ( int edge ) {return null;}
	String identifier = "";
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String new_identifier) {
		identifier = new_identifier;
	}
	double zoom = 1.0;
	public double getZoom() {
		return zoom;
	}
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
	public void fitContent() {}
	public void updateView() {}
	public RootGraph getRootGraph() { return net.getRootGraph(); }
	public Iterator getNodeViewsIterator() { return nodeViews.iterator(); }
	public int getNodeViewCount() { return nodeViews.size(); }
	public int getEdgeViewCount() { return edgeViews.size(); }
	public NodeView getNodeView(Node node) { return null; }
	public NodeView getNodeView(int index) { return null; }
	public java.util.List getEdgeViewsList() { return edgeViews; }
	public  java.util.List getEdgeViewsList( Node oneNode, Node otherNode) { return null; }
	public  java.util.List getEdgeViewsList( int from_node_index, int to_node_index, boolean include_undirected_edges) { return null; }
	public  EdgeView getEdgeView(int edge_index) { return null; }
	public Iterator getEdgeViewsIterator() { return edgeViews.iterator(); }
	public EdgeView getEdgeView(Edge edge) { return null; }
	public int edgeCount() { return edgeViews.size(); }
	public int nodeCount() { return nodeViews.size(); }
	public boolean hideGraphObject(Object object) {  return true;}
	public boolean showGraphObject(Object object) { return true; }
	public boolean hideGraphObjects(List objects) {  return true;}
	public boolean showGraphObjects(List objects) { return true; }
	public Object[] getContextMethods( String class_name, boolean plus_superclass) { return null; }
	public Object[] getContextMethods( String class_name, Object[] methods) { return null; }
	public boolean addContextMethod( String class_name, String method_class_name, String method_name, Object[] args, ClassLoader loader ) { return true; }
	public  void setAllNodePropertyData(int node_index, Object[] data) {}
	public  Object[] getAllNodePropertyData(int node_index) { return null; }
	public  void setAllEdgePropertyData(int edge_index, Object[] data) {}
	public  Object[] getAllEdgePropertyData(int edge_index) { return null; }
	public  Object getNodeObjectProperty(int node_index, int property) { return null; }
	public  boolean setNodeObjectProperty( int node_index, int property, Object value) {return true;}
	public  Object getEdgeObjectProperty(int edge_index, int property) { return null; }
	public  boolean setEdgeObjectProperty( int edge_index, int property, Object value) {return true;} 
	public  double getNodeDoubleProperty(int node_index, int property) { return 0d; }
	public  boolean setNodeDoubleProperty( int node_index, int property, double value) {return true;}
	public  double getEdgeDoubleProperty(int edge_index, int property) { return 0d; }
	public  boolean setEdgeDoubleProperty( int edge_index, int property, double value) {return true;}
	public  float getNodeFloatProperty(int node_index, int property) { return 0f; }
	public  boolean setNodeFloatProperty( int node_index, int property, float value) {return true;}
	public  float getEdgeFloatProperty(int edge_index, int property) { return 0f; }
	public  boolean setEdgeFloatProperty( int edge_index, int property, float value) {return true;}
	public  boolean getNodeBooleanProperty(int node_index, int property) { return true; }
	public  boolean setNodeBooleanProperty( int node_index, int property, boolean value) {return true;}
	public  boolean getEdgeBooleanProperty(int edge_index, int property) { return true; }
	public  boolean setEdgeBooleanProperty( int edge_index, int property, boolean value) {return true;}
	public  int getNodeIntProperty(int node_index, int property) { return 0; }
	public  boolean setNodeIntProperty( int node_index, int property, int value) {return true;}
	public  int getEdgeIntProperty(int edge_index, int property) { return 0; }
	public  boolean setEdgeIntProperty( int edge_index, int property, int value) {return true;}
}
