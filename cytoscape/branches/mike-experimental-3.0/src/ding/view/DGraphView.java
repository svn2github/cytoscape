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
package ding.view;

import cytoscape.geom.rtree.RTree;

import cytoscape.geom.spacial.MutableSpacialIndex2D;
import cytoscape.geom.spacial.SpacialEntry2DEnumerator;

import cytoscape.graph.fixed.FixedGraph;

import cytoscape.render.immed.GraphGraphics;

import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;

import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntStack;

import cytoscape.Edge;
import cytoscape.GraphPerspective;
import cytoscape.Node;
import cytoscape.RootGraph;

import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.GraphViewChangeListener;
import giny.view.NodeView;
import giny.view.GraphViewObject;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * DING implementation of the GINY view.
 *
 * Explain relationship to cytoscape.
 *
 * Throughout this code I am assuming that nodes or edges are never
 * removed from the underlying RootGraph. This assumption was made in the
 * old GraphView implementation. Removal from the RootGraph is the only
 * thing that can affect m_drawPersp and m_structPersp that is beyond our
 * control.
 *
 * @author Nerius Landys
 */
public class DGraphView implements GraphView, Printable {
	static final float DEFAULT_ANCHOR_SIZE = 9.0f;
	static final Paint DEFAULT_ANCHOR_SELECTED_PAINT = Color.red;
	static final Paint DEFAULT_ANCHOR_UNSELECTED_PAINT = Color.black;

	/**
	 * Enum to identify ding canvases - 
	 * used in getCanvas(Canvas canvasId)
	 */
	public enum Canvas {
		BACKGROUND_CANVAS,
		NETWORK_CANVAS,
		FOREGROUND_CANVAS;
	}

	public enum ShapeType {
		NODE_SHAPE,
		LINE_TYPE,
		ARROW_SHAPE;
	}

	/**
	 * Common object used for synchronization.
	 */
	final Object m_lock = new Object();

	/**
	 * A common buffer object used to pass information about.
	 * X-Y coords of the minimum bounding box?
	 */
	final float[] m_extentsBuff = new float[4];

	/**
	 * A common general path variable used for holding lots of shapes.
	 */
	final GeneralPath m_path = new GeneralPath();

	/**
	 * The graph model that will be viewed.
	 */
	GraphPerspective m_perspective;

	/**
	 * Holds the NodeView data for the nodes that are visible.
	 * This will change as nodes are hidden from the view.
	 */
	GraphPerspective m_drawPersp;

	/**
	 * Holds all of the NodeViews, regardless of whether they're visualized.
	 */
	GraphPerspective m_structPersp;

	/**
	 * RTree used for querying node positions.
	 */
	MutableSpacialIndex2D m_spacial;

	/**
	 * Another RTree, but what for?
	 */
	MutableSpacialIndex2D m_spacialA;

	/**
	 *
	 */
	DNodeDetails m_nodeDetails;

	/**
	 *
	 */
	DEdgeDetails m_edgeDetails;

	/**
	 * Level of detail specific to printing. Not used for rendering.
	 */
	PrintLOD m_printLOD;

	/**
	 *
	 */
	HashMap<Integer,NodeView> m_nodeViewMap;

	/**
	 *
	 */
	HashMap<Integer,EdgeView> m_edgeViewMap;

	/**
	 *
	 */
	String m_identifier;

	/**
	 *
	 */
	final float m_defaultNodeXMin;

	/**
	 *
	 */
	final float m_defaultNodeYMin;

	/**
	 *
	 */
	final float m_defaultNodeXMax;

	/**
	 *
	 */
	final float m_defaultNodeYMax;

	/**
	 * Ref to network canvas object.
	 */
	InnerCanvas m_networkCanvas;

	/**
	 * Ref to background canvas object.
	 */
	ArbitraryGraphicsCanvas m_backgroundCanvas;

	/**
	 * Ref to foreground canvas object.
	 */
	ArbitraryGraphicsCanvas m_foregroundCanvas;

	/**
	 *
	 */
	boolean m_nodeSelection = true;

	/**
	 *
	 */
	boolean m_edgeSelection = true;

	/**
	 * BTree of selected nodes.
	 */
	final IntBTree m_selectedNodes; // Positive.

	/**
	 * BTree of selected edges.
	 */
	final IntBTree m_selectedEdges; // Positive.

	/**
	 * BTree of selected anchors.
	 */
	final IntBTree m_selectedAnchors;

	/**
	 * State variable for when nodes have moved.
	 */
	boolean m_contentChanged = false;

	/**
	 * State variable for when zooming/panning have changed.
	 */
	boolean m_viewportChanged = false;

	/**
	 * List of listeners.
	 */
	final GraphViewChangeListener[] m_lis = new GraphViewChangeListener[1];

	/**
	 * List of listeners.
	 */
	final ContentChangeListener[] m_cLis = new ContentChangeListener[1];

	/**
	 * List of listeners.
	 */
	final ViewportChangeListener[] m_vLis = new ViewportChangeListener[1];

	/**
	 * ???
	 */
	private final IntHash m_hash = new IntHash();

	/**
	 * Used for holding edge anchors.
	 */
	final float[] m_anchorsBuff = new float[2];

	/**
	 *
	 */
	int m_lastSize = 0;

	/**
	 * Used for caching texture paint.
	 */
	Paint m_lastPaint = null;

	/**
	 * Used for caching texture paint.
	 */
	Paint m_lastTexturePaint = null;

	/**
	 * Creates a new DGraphView object.
	 *
	 * @param perspective The graph model that we'll be creating a view for.
	 */
	public DGraphView(GraphPerspective perspective) {
		m_perspective = perspective;

		// creating empty graphs
		m_drawPersp = m_perspective.getRootGraph().createGraphPerspective((int[]) null, (int[]) null);
		m_structPersp = m_perspective.getRootGraph()
		                             .createGraphPerspective((int[]) null, (int[]) null);
		m_spacial = new RTree();
		m_spacialA = new RTree();
		m_nodeDetails = new DNodeDetails(this);
		m_edgeDetails = new DEdgeDetails(this);
		m_nodeViewMap = new HashMap<Integer,NodeView>();
		m_edgeViewMap = new HashMap<Integer,EdgeView>();
		m_printLOD = new PrintLOD();
		m_defaultNodeXMin = 0.0f;
		m_defaultNodeYMin = 0.0f;
		m_defaultNodeXMax = m_defaultNodeXMin + DNodeView.DEFAULT_WIDTH;
		m_defaultNodeYMax = m_defaultNodeYMin + DNodeView.DEFAULT_HEIGHT;
		m_networkCanvas = new InnerCanvas(m_lock, this);
		m_backgroundCanvas = new ArbitraryGraphicsCanvas(m_perspective, this, m_networkCanvas,
		                                                 Color.white, true, true);
		addViewportChangeListener(m_backgroundCanvas);
		m_foregroundCanvas = new ArbitraryGraphicsCanvas(m_perspective, this, m_networkCanvas,
		                                                 Color.white, true, false);
		addViewportChangeListener(m_foregroundCanvas);
		m_selectedNodes = new IntBTree();
		m_selectedEdges = new IntBTree();
		m_selectedAnchors = new IntBTree();
	}

	/**
	 * Returns the graph model that this view was created for.
	 *
	 * @return The GraphPerspective that the view was created for.
	 */
	public GraphPerspective getGraphPerspective() {
		return m_perspective;
	}

	/**
	 * Whether node selection is enabled.
	 *
	 * @return Whether node selection is enabled.
	 */
	public boolean nodeSelectionEnabled() {
		return m_nodeSelection;
	}

	/**
	 * Whether edge selection is enabled.
	 *
	 * @return Whether edge selection is enabled.
	 */
	public boolean edgeSelectionEnabled() {
		return m_edgeSelection;
	}

	/**
	 * Enabling the ability to select nodes.
	 */
	public void enableNodeSelection() {
		synchronized (m_lock) {
			m_nodeSelection = true;
		}
	}

	/**
	 * Disables the ability to select nodes.
	 */
	public void disableNodeSelection() {
		final int[] unselectedNodes;

		synchronized (m_lock) {
			m_nodeSelection = false;
			unselectedNodes = getSelectedNodeIndices();

			if (unselectedNodes.length > 0) {
				// Adding this line to speed things up from O(n*log(n)) to O(n).
				m_selectedNodes.empty();

				for (int i = 0; i < unselectedNodes.length; i++)
					((DNodeView) getNodeView(unselectedNodes[i])).unselectInternal();

				m_contentChanged = true;
			}
		}

		if (unselectedNodes.length > 0) {
			final GraphViewChangeListener listener = m_lis[0];

			if (listener != null) {
				listener.graphViewChanged(new GraphViewNodesUnselectedEvent(this, unselectedNodes));
			}

			// Update the view after listener events are fired because listeners
			// may change something in the graph.
			updateView();
		}
	}

	/**
	 * Enables the ability to select edges.
	 */
	public void enableEdgeSelection() {
		synchronized (m_lock) {
			m_edgeSelection = true;
		}
	}

	/**
	 * Disables the ability to select edges.
	 */
	public void disableEdgeSelection() {
		final int[] unselectedEdges;

		synchronized (m_lock) {
			m_edgeSelection = false;
			unselectedEdges = getSelectedEdgeIndices();

			if (unselectedEdges.length > 0) {
				// Adding this line to speed things up from O(n*log(n)) to O(n).
				m_selectedEdges.empty();

				for (int i = 0; i < unselectedEdges.length; i++)
					((DEdgeView) getEdgeView(unselectedEdges[i])).unselectInternal();

				m_contentChanged = true;
			}
		}

		if (unselectedEdges.length > 0) {
			final GraphViewChangeListener listener = m_lis[0];

			if (listener != null) {
				listener.graphViewChanged(new GraphViewEdgesUnselectedEvent(this, unselectedEdges));
			}

			// Update the view after listener events are fired because listeners
			// may change something in the graph.
			updateView();
		}
	}

	/**
	 * Returns an array of selected node indices.
	 *
	 * @return An array of selected node indices.
	 */
	public int[] getSelectedNodeIndices() {
		synchronized (m_lock) {
			// all nodes from the btree
			final IntEnumerator elms = m_selectedNodes.searchRange(Integer.MIN_VALUE,
			                                                       Integer.MAX_VALUE, false);
			final int[] returnThis = new int[elms.numRemaining()];

			for (int i = 0; i < returnThis.length; i++)
				// GINY requires all node indices to be negative (why?), 
				// hence the bitwise complement here.
				returnThis[i] = ~elms.nextInt();

			return returnThis;
		}
	}

	/**
	 * Returns a list of selected node objects.
	 *
	 * @return A list of selected node objects.
	 */
	public List<Node> getSelectedNodes() {
		synchronized (m_lock) {
			// all nodes from the btree
			final IntEnumerator elms = m_selectedNodes.searchRange(Integer.MIN_VALUE,
			                                                       Integer.MAX_VALUE, false);
			final ArrayList<Node> returnThis = new ArrayList<Node>();

			while (elms.numRemaining() > 0)
				// GINY requires all node indices to be negative (why?), 
				// hence the bitwise complement here.
				returnThis.add(m_nodeViewMap.get(new Integer(~elms.nextInt())).getNode());

			return returnThis;
		}
	}

	/**
	 * Returns an array of selected edge indices.
	 *
	 * @return An array of selected edge indices.
	 */
	public int[] getSelectedEdgeIndices() {
		synchronized (m_lock) {
			final IntEnumerator elms = m_selectedEdges.searchRange(Integer.MIN_VALUE,
			                                                       Integer.MAX_VALUE, false);
			final int[] returnThis = new int[elms.numRemaining()];

			for (int i = 0; i < returnThis.length; i++)
				returnThis[i] = ~elms.nextInt();

			return returnThis;
		}
	}

	/**
	 * Returns a list of selected edge objects.
	 *
	 * @return A list of selected edge objects.
	 */
	public List<Edge> getSelectedEdges() {
		synchronized (m_lock) {
			final IntEnumerator elms = m_selectedEdges.searchRange(Integer.MIN_VALUE,
			                                                       Integer.MAX_VALUE, false);
			final ArrayList<Edge> returnThis = new ArrayList<Edge>();

			while (elms.numRemaining() > 0)
				returnThis.add(m_edgeViewMap.get(new Integer(~elms.nextInt())).getEdge());

			return returnThis;
		}
	}

	/**
	 * Add GraphViewChangeListener to linked list of GraphViewChangeListeners.
	 * AAAAAARRRGGGGHHHHHH!!!!
	 *
	 * @param l GraphViewChangeListener to be added to the list.
	 */
	public void addGraphViewChangeListener(GraphViewChangeListener l) {
		m_lis[0] = GraphViewChangeListenerChain.add(m_lis[0], l);
	}

	/**
	 * Remove GraphViewChangeListener from linked list of GraphViewChangeListeners.
	 * AAAAAARRRGGGGHHHHHH!!!!
	 *
	 * @param l GraphViewChangeListener to be removed from the list.
	 */
	public void removeGraphViewChangeListener(GraphViewChangeListener l) {
		m_lis[0] = GraphViewChangeListenerChain.remove(m_lis[0], l);
	}

	/**
	 * Sets the background color on the canvas.
	 *
	 * @param paint The Paint (color) to apply to the background.
	 */
	public void setBackgroundPaint(Paint paint) {
		synchronized (m_lock) {
			if (paint instanceof Color) {
				m_backgroundCanvas.setBackground((Color) paint);
				m_contentChanged = true;
			} else {
				System.out.println("DGraphView.setBackgroundPaint(), Color not found!");
			}
		}
	}

	/**
	 * Returns the background color on the canvas.
	 *
	 * @return The background color on the canvas.
	 */
	public Paint getBackgroundPaint() {
		return m_backgroundCanvas.getBackground();
	}

	/**
	 * Returns the InnerCanvas object.  The InnerCanvas object is the actual component
	 * that the network is rendered on.
	 *
	 * @return The InnerCanvas object.
	 */
	public Component getComponent() {
		return m_networkCanvas;
	}

	/**
	 * Adds a NodeView object to the GraphView. Creates NodeView if one doesn't already exist.
	 *
	 * @param nodeInx The index of the NodeView object to be added.
	 *
	 * @return The NodeView object that is added to the GraphView.
	 */
	public NodeView addNodeView(int nodeInx) {
		NodeView newView = null;

		synchronized (m_lock) {
			newView = addNodeViewInternal(nodeInx);

			if (newView == null) {
				return (NodeView) m_nodeViewMap.get(new Integer(nodeInx));
			}

			m_contentChanged = true;
		}

		final GraphViewChangeListener listener = m_lis[0];

		if (listener != null) {
			listener.graphViewChanged(new GraphViewNodesRestoredEvent(this,
			                                                          new int[] {
			                                                              newView.getRootGraphIndex()
			                                                          }));
		}

		return newView;
	}

	/**
	 * Should synchronize around m_lock.
	 */
	private NodeView addNodeViewInternal(int nodeInx) {
		final NodeView oldView = (NodeView) m_nodeViewMap.get(new Integer(nodeInx));

		if (oldView != null) {
			return null;
		}

		if (m_drawPersp.restoreNode(nodeInx) == 0) {
			if (m_drawPersp.getNode(nodeInx) != null) {
				throw new IllegalStateException("something weird is going on - node already existed in graph "
				                                + "but a view for it did not exist (debug)");
			}

			throw new IllegalArgumentException("node index specified does not exist in underlying RootGraph");
		}

		m_structPersp.restoreNode(nodeInx);

		final NodeView newView;
		newView = new DNodeView(this, nodeInx);
		m_nodeViewMap.put(new Integer(nodeInx), newView);
		m_spacial.insert(~nodeInx, m_defaultNodeXMin, m_defaultNodeYMin, m_defaultNodeXMax,
		                 m_defaultNodeYMax);

		return newView;
	}

	/**
	 * Adds EdgeView to the GraphView.
	 *
	 * @param edgeInx The index of EdgeView to be added.
	 *
	 * @return The EdgeView that was added.
	 */
	public EdgeView addEdgeView(int edgeInx) {
		NodeView sourceNode = null;
		NodeView targetNode = null;
		EdgeView edgeView = null;

		synchronized (m_lock) {
			final EdgeView oldView = (EdgeView) m_edgeViewMap.get(new Integer(edgeInx));

			if (oldView != null) {
				return oldView;
			}

			final Edge edge = m_drawPersp.getRootGraph().getEdge(edgeInx);

			if (edge == null) {
				throw new IllegalArgumentException("edge index specified does not exist in underlying RootGraph");
			}

			sourceNode = addNodeViewInternal(edge.getSource().getRootGraphIndex());
			targetNode = addNodeViewInternal(edge.getTarget().getRootGraphIndex());

			if (m_drawPersp.restoreEdge(edgeInx) == 0) {
				if (m_drawPersp.getEdge(edgeInx) != null) {
					throw new IllegalStateException("something weird is going on - edge already existed in graph "
					                                + "but a view for it did not exist (debug)");
				}

				throw new IllegalArgumentException("edge index specified does not exist in underlying RootGraph");
			}

			m_structPersp.restoreEdge(edgeInx);
			edgeView = new DEdgeView(this, edgeInx);
			m_edgeViewMap.put(new Integer(edgeInx), edgeView);
			m_contentChanged = true;
		}

		// Under no circumstances should we be holding m_lock when the listener
		// events are fired.
		final GraphViewChangeListener listener = m_lis[0];

		if (listener != null) {
			// Only fire this event if either of the nodes is new.  The node
			// will be null if it already existed.
			if ((sourceNode != null) || (targetNode != null)) {
				int[] nodeInx;

				if (sourceNode == null) {
					nodeInx = new int[] { targetNode.getRootGraphIndex() };
				} else if (targetNode == null) {
					nodeInx = new int[] { sourceNode.getRootGraphIndex() };
				} else {
					nodeInx = new int[] {
					              sourceNode.getRootGraphIndex(), targetNode.getRootGraphIndex()
					          };
				}

				listener.graphViewChanged(new GraphViewNodesRestoredEvent(this, nodeInx));
			}

			listener.graphViewChanged(new GraphViewEdgesRestoredEvent(this,
			                                                          new int[] {
			                                                              edgeView.getRootGraphIndex()
			                                                          }));
		}

		return edgeView;
	}

	/**
	 * Will thrown an UnsupportedOperationException. Don't use this.
	 *
	 * @param className ???
	 * @param edgeInx ???
	 *
	 * @return Nothing, an exception will be thrown.
	 */
	public EdgeView addEdgeView(String className, int edgeInx) {
		throw new UnsupportedOperationException("not implemented");
	}

	/**
	 * Will thrown an UnsupportedOperationException. Don't use this.
	 *
	 * @param className ???
	 * @param nodeInx ???
	 *
	 * @return Nothing, an exception will be thrown.
	 */
	public NodeView addNodeView(String className, int nodeInx) {
		throw new UnsupportedOperationException("not implemented");
	}

	/**
	 * Will thrown an UnsupportedOperationException. Don't use this.
	 *
	 * @param nodeInx ??
	 * @param replacement ??
	 *
	 * @return Nothing, an exception will be thrown.
	 */
	public NodeView addNodeView(int nodeInx, NodeView replacement) {
		throw new UnsupportedOperationException("not implemented");
	}

	/**
	 * Removes a NodeView based on specified NodeView.
	 *
	 * @param nodeView The NodeView object to be removed.
	 *
	 * @return The NodeView object that was removed.
	 */
	public NodeView removeNodeView(NodeView nodeView) {
		return removeNodeView(nodeView.getRootGraphIndex());
	}

	/**
	 * Removes a NodeView based on specified Node.
	 *
	 * @param node The Node object connected to the NodeView to be removed.
	 *
	 * @return The NodeView object that was removed.
	 */
	public NodeView removeNodeView(Node node) {
		return removeNodeView(node.getRootGraphIndex());
	}

	/**
	 * Removes a NodeView based on a specified index.
	 *
	 * @param nodeInx The index of the NodeView to be removed.
	 *
	 * @return The NodeView object that was removed.
	 */
	public NodeView removeNodeView(int nodeInx) {
		final int[] hiddenEdgeInx;
		final DNodeView returnThis;

		synchronized (m_lock) {
			// We have to query edges in the m_structPersp, not m_drawPersp
			// because what if the node is hidden?
			hiddenEdgeInx = m_structPersp.getAdjacentEdgeIndicesArray(nodeInx, true, true, true);

			// This isn't an error. Only if the nodeInx is invalid will getAdjacentEdgeIndicesArray 
			// return null. If there are no adjacent edges, then it will return an array of length 0.
			if (hiddenEdgeInx == null) {
				return null;
			}

			for (int i = 0; i < hiddenEdgeInx.length; i++)
				removeEdgeViewInternal(hiddenEdgeInx[i]);

			returnThis = (DNodeView) m_nodeViewMap.remove(new Integer(nodeInx));
			returnThis.unselectInternal();

			// If this node was hidden, it won't be in m_drawPersp.
			m_drawPersp.hideNode(nodeInx);
			m_structPersp.hideNode(nodeInx);
			m_nodeDetails.unregisterNode(~nodeInx);

			// If this node was hidden, it won't be in m_spacial.
			m_spacial.delete(~nodeInx);

			// m_selectedNodes.delete(~nodeInx);
			returnThis.m_view = null;
			m_contentChanged = true;
		}

		final GraphViewChangeListener listener = m_lis[0];

		if (listener != null) {
			if (hiddenEdgeInx.length > 0) {
				listener.graphViewChanged(new GraphViewEdgesHiddenEvent(this, hiddenEdgeInx));
			}

			listener.graphViewChanged(new GraphViewNodesHiddenEvent(this,
			                                                        new int[] {
			                                                            returnThis.getRootGraphIndex()
			                                                        }));
		}

		return returnThis;
	}

	/**
	 * Removes an EdgeView based on an EdgeView.
	 *
	 * @param edgeView The EdgeView to be removed.
	 *
	 * @return The EdgeView that was removed.
	 */
	public EdgeView removeEdgeView(EdgeView edgeView) {
		return removeEdgeView(edgeView.getRootGraphIndex());
	}

	/**
	 * Removes an EdgeView based on an Edge.
	 *
	 * @param edge The Edge of the EdgeView to be removed.
	 *
	 * @return The EdgeView that was removed.
	 */
	public EdgeView removeEdgeView(Edge edge) {
		return removeEdgeView(edge.getRootGraphIndex());
	}

	/**
	 * Removes an EdgeView based on an EdgeIndex.
	 *
	 * @param edgeInx The edge index of the EdgeView to be removed.
	 *
	 * @return The EdgeView that was removed.
	 */
	public EdgeView removeEdgeView(int edgeInx) {
		final DEdgeView returnThis;

		synchronized (m_lock) {
			returnThis = removeEdgeViewInternal(edgeInx);

			if (returnThis != null) {
				m_contentChanged = true;
			}
		}

		if (returnThis != null) {
			final GraphViewChangeListener listener = m_lis[0];

			if (listener != null) {
				listener.graphViewChanged(new GraphViewEdgesHiddenEvent(this,
				                                                        new int[] {
				                                                            returnThis.getRootGraphIndex() 
																			}));
			}
		}

		return returnThis;
	}

	/**
	 * Should synchronize around m_lock.
	 */
	private DEdgeView removeEdgeViewInternal(int edgeInx) {
		final DEdgeView returnThis = (DEdgeView) m_edgeViewMap.remove(new Integer(edgeInx));

		if (returnThis == null) {
			return returnThis;
		}

		returnThis.unselectInternal();

		// If this edge view was hidden, it won't be in m_drawPersp.
		m_drawPersp.hideEdge(edgeInx);
		m_structPersp.hideEdge(edgeInx);
		m_edgeDetails.unregisterEdge(~edgeInx);

		// m_selectedEdges.delete(~edgeInx);
		returnThis.m_view = null;

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getIdentifier() {
		return m_identifier;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param id
	 *            DOCUMENT ME!
	 */
	public void setIdentifier(String id) {
		m_identifier = id;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getZoom() {
		return m_networkCanvas.m_scaleFactor;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param zoom
	 *            DOCUMENT ME!
	 */
	public void setZoom(double zoom) {
		synchronized (m_lock) {
			m_networkCanvas.m_scaleFactor = checkZoom(zoom,m_networkCanvas.m_scaleFactor);
			m_viewportChanged = true;
		}

		updateView();
	}

	/**
	 * DOCUMENT ME!
	 */
	public void fitContent() {
		synchronized (m_lock) {
			if (m_spacial.queryOverlap(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
			                           Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
			                           m_extentsBuff, 0, false).numRemaining() == 0) {
				return;
			}

			m_networkCanvas.m_xCenter = (((double) m_extentsBuff[0]) + ((double) m_extentsBuff[2])) / 2.0d;
			m_networkCanvas.m_yCenter = (((double) m_extentsBuff[1]) + ((double) m_extentsBuff[3])) / 2.0d;
			final double zoom = Math.min(((double) m_networkCanvas.getWidth()) / 
			                             (((double) m_extentsBuff[2]) - 
			                              ((double) m_extentsBuff[0])), 
			                              ((double) m_networkCanvas.getHeight()) / 
			                             (((double) m_extentsBuff[3]) - 
			                              ((double) m_extentsBuff[1])));
			m_networkCanvas.m_scaleFactor = checkZoom(zoom,m_networkCanvas.m_scaleFactor);
			m_viewportChanged = true;
		}

		updateView();
	}

	/**
	 * DOCUMENT ME!
	 */
	public void updateView() {
		m_networkCanvas.repaint();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public RootGraph getRootGraph() {
		return m_perspective.getRootGraph();
	}

	/*
	 * Returns an iterator of all node views, including those that are currently
	 * hidden.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator<NodeView> getNodeViewsIterator() {
		synchronized (m_lock) {
			return m_nodeViewMap.values().iterator();
		}
	}

	/*
	 * Returns the count of all node views, including those that are currently
	 * hidden.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getNodeViewCount() {
		synchronized (m_lock) {
			return m_nodeViewMap.size();
		}
	}

	/*
	 * Returns the count of all edge views, including those that are currently
	 * hidden.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEdgeViewCount() {
		synchronized (m_lock) {
			return m_edgeViewMap.size();
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public NodeView getNodeView(Node node) {
		return getNodeView(node.getRootGraphIndex());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public NodeView getNodeView(int nodeInx) {
		synchronized (m_lock) {
			return (NodeView) m_nodeViewMap.get(new Integer(nodeInx));
		}
	}

	/*
	 * Returns a list of all edge views, including those that are currently
	 * hidden.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<EdgeView> getEdgeViewsList() {
		synchronized (m_lock) {
			final ArrayList<EdgeView> returnThis = new ArrayList<EdgeView>(m_edgeViewMap.size());
			final Iterator<EdgeView> values = m_edgeViewMap.values().iterator();

			while (values.hasNext())
				returnThis.add(values.next());

			return returnThis;
		}
	}

	/*
	 * Returns all edge views (including the hidden ones) that are either 1.
	 * directed, having oneNode as source and otherNode as target or 2.
	 * undirected, having oneNode and otherNode as endpoints. Note that this
	 * behaviour is similar to that of GraphPerspective.edgesList(Node, Node).
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param oneNode DOCUMENT ME!
	 * @param otherNode DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<EdgeView> getEdgeViewsList(Node oneNode, Node otherNode) {
		synchronized (m_lock) {
			List<Edge> edges = m_structPersp.edgesList(oneNode.getRootGraphIndex(),
			                                     otherNode.getRootGraphIndex(), true);

			if (edges == null) {
				return null;
			}

			final ArrayList<EdgeView> returnThis = new ArrayList<EdgeView>();
			Iterator<Edge> it = edges.iterator();

			while (it.hasNext()) {
				Edge e = (Edge) it.next();
				returnThis.add(getEdgeView(e));
			}

			return returnThis;
		}
	}

	/*
	 * Similar to getEdgeViewsList(Node, Node), only that one has control of
	 * whether or not to include undirected edges.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param oneNodeInx DOCUMENT ME!
	 * @param otherNodeInx DOCUMENT ME!
	 * @param includeUndirected DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<EdgeView> getEdgeViewsList(int oneNodeInx, int otherNodeInx, boolean includeUndirected) {
		synchronized (m_lock) {
			List<Edge> edges = m_structPersp.edgesList(oneNodeInx, otherNodeInx, includeUndirected);

			if (edges == null) {
				return null;
			}

			final ArrayList<EdgeView> returnThis = new ArrayList<EdgeView>();
			Iterator<Edge> it = edges.iterator();

			while (it.hasNext()) {
				Edge e = (Edge) it.next();
				returnThis.add(getEdgeView(e));
			}

			return returnThis;
		}
	}

	/*
	 * Returns an edge view with specified edge index whether or not the edge
	 * view is hidden; null is returned if view does not exist.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public EdgeView getEdgeView(int edgeInx) {
		synchronized (m_lock) {
			return (EdgeView) m_edgeViewMap.get(new Integer(edgeInx));
		}
	}

	/*
	 * Returns an iterator of all edge views, including those that are currently
	 * hidden.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator<EdgeView> getEdgeViewsIterator() {
		synchronized (m_lock) {
			return m_edgeViewMap.values().iterator();
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public EdgeView getEdgeView(Edge edge) {
		return getEdgeView(edge.getRootGraphIndex());
	}

	/*
	 * Alias to getEdgeViewCount().
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int edgeCount() {
		return getEdgeViewCount();
	}

	/*
	 * Alias to getNodeViewCount().
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int nodeCount() {
		return getNodeViewCount();
	}

	/*
	 * obj should be either a DEdgeView or a DNodeView.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param obj DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean hideGraphObject(Object obj) {
		return hideGraphObjectInternal(obj, true);
	}

	private boolean hideGraphObjectInternal(Object obj, boolean fireListenerEvents) {
		if (obj instanceof DEdgeView) {
			int edgeInx;

			synchronized (m_lock) {
				edgeInx = ((DEdgeView) obj).getRootGraphIndex();

				if (m_drawPersp.hideEdge(edgeInx) == 0) {
					return false;
				}

				((DEdgeView) obj).unselectInternal();
				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					listener.graphViewChanged(new GraphViewEdgesHiddenEvent(this,
					                                                        new int[] { edgeInx }));
				}
			}

			return true;
		} else if (obj instanceof DNodeView) {
			int[] edges;
			int nodeInx;

			synchronized (m_lock) {
				final DNodeView nView = (DNodeView) obj;
				nodeInx = nView.getRootGraphIndex();
				edges = m_drawPersp.getAdjacentEdgeIndicesArray(nodeInx, true, true, true);

				if (edges == null) {
					return false;
				}

				for (int i = 0; i < edges.length; i++)
					hideGraphObjectInternal(m_edgeViewMap.get(new Integer(edges[i])), false);

				nView.unselectInternal();
				m_spacial.exists(~nodeInx, m_extentsBuff, 0);
				nView.m_hiddenXMin = m_extentsBuff[0];
				nView.m_hiddenYMin = m_extentsBuff[1];
				nView.m_hiddenXMax = m_extentsBuff[2];
				nView.m_hiddenYMax = m_extentsBuff[3];
				m_drawPersp.hideNode(nodeInx);
				m_spacial.delete(~nodeInx);
				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					if (edges.length > 0) {
						listener.graphViewChanged(new GraphViewEdgesHiddenEvent(this, edges));
					}

					listener.graphViewChanged(new GraphViewNodesHiddenEvent(this,
					                                                        new int[] { nodeInx }));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	/*
	 * obj should be either a DEdgeView or a DNodeView.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param obj DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean showGraphObject(Object obj) {
		return showGraphObjectInternal(obj, true);
	}

	private boolean showGraphObjectInternal(Object obj, boolean fireListenerEvents) {
		if (obj instanceof DNodeView) {
			int nodeInx;

			synchronized (m_lock) {
				final DNodeView nView = (DNodeView) obj;
				nodeInx = nView.getRootGraphIndex();

				if (m_structPersp.getNode(nodeInx) == null) {
					return false;
				}

				if (m_drawPersp.restoreNode(nodeInx) == 0) {
					return false;
				}

				m_spacial.insert(~nodeInx, nView.m_hiddenXMin, nView.m_hiddenYMin,
				                 nView.m_hiddenXMax, nView.m_hiddenYMax);
				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					listener.graphViewChanged(new GraphViewNodesRestoredEvent(this,
					                                                          new int[] { nodeInx }));
				}
			}

			return true;
		} else if (obj instanceof DEdgeView) {
			int sourceNode = 0;
			int targetNode = 0;
			int newEdge = 0;

			synchronized (m_lock) {
				final Edge edge = m_structPersp.getEdge(((DEdgeView) obj).getRootGraphIndex());

				if (edge == null) {
					return false;
				}

				// The edge exists in m_structPersp, therefore its source and
				// target
				// node views must also exist.
				sourceNode = edge.getSource().getRootGraphIndex();

				if (!showGraphObjectInternal(getNodeView(sourceNode), false)) {
					sourceNode = 0;
				}

				targetNode = edge.getTarget().getRootGraphIndex();

				if (!showGraphObjectInternal(getNodeView(targetNode), false)) {
					targetNode = 0;
				}

				newEdge = edge.getRootGraphIndex();

				if (m_drawPersp.restoreEdge(newEdge) == 0) {
					return false;
				}

				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					if (sourceNode != 0) {
						listener.graphViewChanged(new GraphViewNodesRestoredEvent(this,
						                                                          new int[] {
						                                                              sourceNode
						                                                          }));
					}

					if (targetNode != 0) {
						listener.graphViewChanged(new GraphViewNodesRestoredEvent(this,
						                                                          new int[] {
						                                                              targetNode
						                                                          }));
					}

					listener.graphViewChanged(new GraphViewEdgesRestoredEvent(this,
					                                                          new int[] { newEdge }));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param objects
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean hideGraphObjects(List<? extends GraphViewObject> objects) {
		final Iterator<? extends GraphViewObject> it = objects.iterator();

		while (it.hasNext())
			hideGraphObject(it.next());

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param objects
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean showGraphObjects(List<? extends GraphViewObject> objects) {
		final Iterator<? extends GraphViewObject> it = objects.iterator();

		while (it.hasNext())
			showGraphObject(it.next());

		return true;
	}

	// AJK: 04/25/06 BEGIN
	/**
	 *  DOCUMENT ME!
	 *
	 * @param className DOCUMENT ME!
	 * @param plusSuperclass DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object[] getContextMethods(String className, boolean plusSuperclass) {
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param className
	 *            DOCUMENT ME!
	 * @param methods
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object[] getContextMethods(String className, Object[] methods) {
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param className
	 *            DOCUMENT ME!
	 * @param methodClassName
	 *            DOCUMENT ME!
	 * @param methodName
	 *            DOCUMENT ME!
	 * @param args
	 *            DOCUMENT ME!
	 * @param loader
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean addContextMethod(String className, String methodClassName, String methodName,
	                                Object[] args, ClassLoader loader) {
		return false;
	}

	// AJK: 04/25/06 END
	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param data DOCUMENT ME!
	 */
	public void setAllNodePropertyData(int nodeInx, Object[] data) {
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object[] getAllNodePropertyData(int nodeInx) {
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param data
	 *            DOCUMENT ME!
	 */
	public void setAllEdgePropertyData(int edgeInx, Object[] data) {
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object[] getAllEdgePropertyData(int edgeInx) {
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object getNodeObjectProperty(int nodeInx, int property) {
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setNodeObjectProperty(int nodeInx, int property, Object value) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object getEdgeObjectProperty(int edgeInx, int property) {
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setEdgeObjectProperty(int edgeInx, int property, Object value) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getNodeDoubleProperty(int nodeInx, int property) {
		return 0.0d;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param val
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setNodeDoubleProperty(int nodeInx, int property, double val) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getEdgeDoubleProperty(int edgeInx, int property) {
		return 0.0d;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param val
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setEdgeDoubleProperty(int edgeInx, int property, double val) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float getNodeFloatProperty(int nodeInx, int property) {
		return 0.0f;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setNodeFloatProperty(int nodeInx, int property, float value) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float getEdgeFloatProperty(int edgeInx, int property) {
		return 0.0f;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setEdgeFloatProperty(int edgeInx, int property, float value) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean getNodeBooleanProperty(int nodeInx, int property) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param val
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setNodeBooleanProperty(int nodeInx, int property, boolean val) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean getEdgeBooleanProperty(int edgeInx, int property) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param val
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setEdgeBooleanProperty(int edgeInx, int property, boolean val) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getNodeIntProperty(int nodeInx, int property) {
		return 0;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setNodeIntProperty(int nodeInx, int property, int value) {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getEdgeIntProperty(int edgeInx, int property) {
		return 0;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * @param property
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setEdgeIntProperty(int edgeInx, int property, int value) {
		return false;
	}

	// Auxillary methods specific to this GraphView implementation:
	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public void setCenter(double x, double y) {
		synchronized (m_lock) {
			m_networkCanvas.m_xCenter = x;
			m_networkCanvas.m_yCenter = y;
			m_viewportChanged = true;
		}

		updateView();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Point2D getCenter() {
		synchronized (m_lock) {
			return new Point2D.Double(m_networkCanvas.m_xCenter, m_networkCanvas.m_yCenter);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void fitSelected() {
		synchronized (m_lock) {
			final IntEnumerator selectedElms = m_selectedNodes.searchRange(Integer.MIN_VALUE,
			                                                               Integer.MAX_VALUE, false);

			if (selectedElms.numRemaining() == 0) {
				return;
			}

			float xMin = Float.POSITIVE_INFINITY;
			float yMin = Float.POSITIVE_INFINITY;
			float xMax = Float.NEGATIVE_INFINITY;
			float yMax = Float.NEGATIVE_INFINITY;

			while (selectedElms.numRemaining() > 0) {
				final int node = selectedElms.nextInt();
				m_spacial.exists(node, m_extentsBuff, 0);
				xMin = Math.min(xMin, m_extentsBuff[0]);
				yMin = Math.min(yMin, m_extentsBuff[1]);
				xMax = Math.max(xMax, m_extentsBuff[2]);
				yMax = Math.max(yMax, m_extentsBuff[3]);
			}

			m_networkCanvas.m_xCenter = (((double) xMin) + ((double) xMax)) / 2.0d;
			m_networkCanvas.m_yCenter = (((double) yMin) + ((double) yMax)) / 2.0d;
			final double zoom = Math.min(((double) m_networkCanvas.getWidth()) / (((double) xMax)
			                             - ((double) xMin)),
			                             ((double) m_networkCanvas.getHeight()) / (((double) yMax)
			                             - ((double) yMin)));
			m_networkCanvas.m_scaleFactor = checkZoom(zoom,m_networkCanvas.m_scaleFactor);
			m_viewportChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param lod
	 *            DOCUMENT ME!
	 */
	public void setGraphLOD(GraphLOD lod) {
		synchronized (m_lock) {
			m_networkCanvas.m_lod[0] = lod;
			m_contentChanged = true;
		}

		updateView();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphLOD getGraphLOD() {
		return m_networkCanvas.m_lod[0];
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param textAsShape
	 *            DOCUMENT ME!
	 */
	public void setPrintingTextAsShape(boolean textAsShape) {
		synchronized (m_lock) {
			m_printLOD.setPrintingTextAsShape(textAsShape);
		}
	}

	/**
	 * Efficiently computes the set of nodes intersecting an axis-aligned query
	 * rectangle; the query rectangle is specified in the node coordinate
	 * system, not the component coordinate system.
	 * <p>
	 * NOTE: The order of elements placed on the stack follows the rendering
	 * order of nodes; the element waiting to be popped off the stack is the
	 * node that is rendered last, and thus is "on top of" other nodes
	 * potentially beneath it.
	 * <p>
	 * HINT: To perform a point query simply set xMin equal to xMax and yMin
	 * equal to yMax.
	 *
	 * @param xMinimum
	 *            a boundary of the query rectangle: the minimum X coordinate.
	 * @param yMinimum
	 *            a boundary of the query rectangle: the minimum Y coordinate.
	 * @param xMaximum
	 *            a boundary of the query rectangle: the maximum X coordinate.
	 * @param yMaximum
	 *            a boundary of the query rectangle: the maximum Y coordinate.
	 * @param treatNodeShapesAsRectangle
	 *            if true, nodes are treated as rectangles for purposes of the
	 *            query computation; if false, true node shapes are respected,
	 *            at the expense of slowing down the query by a constant factor.
	 * @param returnVal
	 *            RootGraph indices of nodes intersecting the query rectangle
	 *            will be placed onto this stack; the stack is not emptied by
	 *            this method initially.
	 */
	public void getNodesIntersectingRectangle(double xMinimum, double yMinimum, double xMaximum,
	                                          double yMaximum, boolean treatNodeShapesAsRectangle,
	                                          IntStack returnVal) {
		synchronized (m_lock) {
			final float xMin = (float) xMinimum;
			final float yMin = (float) yMinimum;
			final float xMax = (float) xMaximum;
			final float yMax = (float) yMaximum;
			final SpacialEntry2DEnumerator under = m_spacial.queryOverlap(xMin, yMin, xMax, yMax,
			                                                              null, 0, false);
			final int totalHits = under.numRemaining();

			if (treatNodeShapesAsRectangle) {
				for (int i = 0; i < totalHits; i++)
					returnVal.push(~under.nextInt());
			} else {
				final double x = xMin;
				final double y = yMin;
				final double w = ((double) xMax) - xMin;
				final double h = ((double) yMax) - yMin;

				for (int i = 0; i < totalHits; i++) {
					final int node = under.nextExtents(m_extentsBuff, 0);

					// The only way that the node can miss the intersection
					// query is
					// if it intersects one of the four query rectangle's
					// corners.
					if (((m_extentsBuff[0] < xMin) && (m_extentsBuff[1] < yMin))
					    || ((m_extentsBuff[0] < xMin) && (m_extentsBuff[3] > yMax))
					    || ((m_extentsBuff[2] > xMax) && (m_extentsBuff[3] > yMax))
					    || ((m_extentsBuff[2] > xMax) && (m_extentsBuff[1] < yMin))) {
						m_networkCanvas.m_grafx.getNodeShape(m_nodeDetails.shape(node),
						                                     m_extentsBuff[0], m_extentsBuff[1],
						                                     m_extentsBuff[2], m_extentsBuff[3],
						                                     m_path);

						if ((w > 0) && (h > 0)) {
							if (m_path.intersects(x, y, w, h)) {
								returnVal.push(~node);
							}
						} else {
							if (m_path.contains(x, y)) {
								returnVal.push(~node);
							}
						}
					} else {
						returnVal.push(~node);
					}
				}
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param xMin
	 *            DOCUMENT ME!
	 * @param yMin
	 *            DOCUMENT ME!
	 * @param xMax
	 *            DOCUMENT ME!
	 * @param yMax
	 *            DOCUMENT ME!
	 * @param returnVal
	 *            DOCUMENT ME!
	 */
	public void queryDrawnEdges(int xMin, int yMin, int xMax, int yMax, IntStack returnVal) {
		synchronized (m_lock) {
			m_networkCanvas.computeEdgesIntersecting(xMin, yMin, xMax, yMax, returnVal);
		}
	}

	/**
	 * Extents of the nodes.
	 */
	public boolean getExtents(double[] extentsBuff) {
		synchronized (m_lock) {
			if (m_spacial.queryOverlap(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
			                           Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
			                           m_extentsBuff, 0, false).numRemaining() == 0) {
				return false;
			}

			extentsBuff[0] = m_extentsBuff[0];
			extentsBuff[1] = m_extentsBuff[1];
			extentsBuff[2] = m_extentsBuff[2];
			extentsBuff[3] = m_extentsBuff[3];

			return true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param coords
	 *            DOCUMENT ME!
	 */
	public void xformComponentToNodeCoords(double[] coords) {
		synchronized (m_lock) {
			m_networkCanvas.m_grafx.xformImageToNodeCoords(coords);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param img
	 *            DOCUMENT ME!
	 * @param lod
	 *            DOCUMENT ME!
	 * @param bgPaint
	 *            DOCUMENT ME!
	 * @param xCenter
	 *            DOCUMENT ME!
	 * @param yCenter
	 *            DOCUMENT ME!
	 * @param scaleFactor
	 *            DOCUMENT ME!
	 */
	public void drawSnapshot(Image img, GraphLOD lod, Paint bgPaint, double xCenter,
	                         double yCenter, double scaleFactor) {
		synchronized (m_lock) {
			GraphRenderer.renderGraph((FixedGraph) m_drawPersp, m_spacial, lod, m_nodeDetails,
			                          m_edgeDetails, m_hash, new GraphGraphics(img, false),
			                          bgPaint, xCenter, yCenter, scaleFactor);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param l
	 *            DOCUMENT ME!
	 */
	public void addContentChangeListener(ContentChangeListener l) {
		m_cLis[0] = ContentChangeListenerChain.add(m_cLis[0], l);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param l
	 *            DOCUMENT ME!
	 */
	public void removeContentChangeListener(ContentChangeListener l) {
		m_cLis[0] = ContentChangeListenerChain.remove(m_cLis[0], l);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param l
	 *            DOCUMENT ME!
	 */
	public void addViewportChangeListener(ViewportChangeListener l) {
		m_vLis[0] = ViewportChangeListenerChain.add(m_vLis[0], l);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param l
	 *            DOCUMENT ME!
	 */
	public void removeViewportChangeListener(ViewportChangeListener l) {
		m_vLis[0] = ViewportChangeListenerChain.remove(m_vLis[0], l);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g
	 *            DOCUMENT ME!
	 * @param pageFormat
	 *            DOCUMENT ME!
	 * @param page
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int print(Graphics g, PageFormat pageFormat, int page) {
		if (page == 0) {
			((Graphics2D) g).translate(pageFormat.getImageableX(), pageFormat.getImageableY());

			// make sure the whole image on the screen will fit to the printable
			// area of the paper
			double image_scale = Math.min(pageFormat.getImageableWidth() / getComponent().getWidth(),
			                              pageFormat.getImageableHeight() / getComponent()
			                                                                    .getHeight());

			if (image_scale < 1.0d) {
				((Graphics2D) g).scale(image_scale, image_scale);
			}

			g.clipRect(0, 0, getComponent().getWidth(), getComponent().getHeight());

			getComponent().print(g);

			return PAGE_EXISTS;
		} else {
			return NO_SUCH_PAGE;
		}
	}

	// AJK: 04/02/06 BEGIN
	/**
	 * Method to return a reference to the network canvas.
	 * This method existed before the addition of background
	 * and foreground canvases, and it remains for backward compatibility.
	 *
	 * @return InnerCanvas
	 */
	public InnerCanvas getCanvas() {
		return m_networkCanvas;
	}

	/**
	 * Method to return a reference to a DingCanvas object,
	 * given a canvas id.
	 *
	 * @param canvasId Canvas
	 * @return  DingCanvas
	 */
	public DingCanvas getCanvas(Canvas canvasId) {
		if (canvasId == Canvas.BACKGROUND_CANVAS) {
			return m_backgroundCanvas;
		} else if (canvasId == Canvas.NETWORK_CANVAS) {
			return m_networkCanvas;
		} else if (canvasId == Canvas.FOREGROUND_CANVAS) {
			return m_foregroundCanvas;
		}

		// made it here
		return null;
	}

	/**
	 * Method to return a reference to an Image object,
	 * which represents the current network view.
	 *
	 * @param width Width of desired image.
	 * @param height Height of desired image.
	 * @param shrink Percent to shrink the network shown in the image. 
	 * This doesn't shrink the image, just the network shown, as if the user zoomed out.
	 * Can be between 0 and 1, if not it will default to 1.  
	 * @return Image
	 * @throws IllegalArgumentException
	 */
	public Image createImage(int width, int height, double shrink) {

		// check args
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("DGraphView.createImage(int width, int height): " +
											   "width and height arguments must be greater than zero");
		}

		if ( shrink < 0 || shrink > 1.0 ) {
			System.out.println("DGraphView.createImage(width,height,shrink) shrink is invalid: "
			                   + shrink + "  using default of 1.0");
			shrink = 1.0;
		}

		// create image to return
        Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
        Graphics g = image.getGraphics();

		// paint background canvas into image
		Dimension dim = m_backgroundCanvas.getSize();
		m_backgroundCanvas.setSize(width, height);
		m_backgroundCanvas.paint(g);
		m_backgroundCanvas.setSize(dim);

		// paint inner canvas (network)
		dim = m_networkCanvas.getSize();
		m_networkCanvas.setSize(width, height);
        fitContent();
		setZoom( getZoom() * shrink );
		m_networkCanvas.paint(g);
		m_networkCanvas.setSize(dim);

		// paint foreground canvas
		dim = m_foregroundCanvas.getSize();
		m_foregroundCanvas.setSize(width, height);
		m_foregroundCanvas.paint(g);
		m_foregroundCanvas.setSize(dim);

		// outta here
		return image;
	}

	/**
	 * utility that returns the nodeView that is located at input point
	 *
	 * @param pt
	 */
	public NodeView getPickedNodeView(Point2D pt) {
		NodeView nv = null;
		double[] locn = new double[2];
		locn[0] = pt.getX();
		locn[1] = pt.getY();

		int chosenNode = 0;
		xformComponentToNodeCoords(locn);

		final IntStack nodeStack = new IntStack();
		getNodesIntersectingRectangle((float) locn[0], (float) locn[1], (float) locn[0],
		                              (float) locn[1],
		                              (m_networkCanvas.getLastRenderDetail()
		                              & GraphRenderer.LOD_HIGH_DETAIL) == 0, nodeStack);

		chosenNode = (nodeStack.size() > 0) ? nodeStack.peek() : 0;

		if (chosenNode != 0) {
			nv = getNodeView(chosenNode);
		}

		return nv;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param pt
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public EdgeView getPickedEdgeView(Point2D pt) {
		EdgeView ev = null;
		final IntStack edgeStack = new IntStack();
		queryDrawnEdges((int) pt.getX(), (int) pt.getY(), (int) pt.getX(), (int) pt.getY(),
		                edgeStack);

		int chosenEdge = 0;
		chosenEdge = (edgeStack.size() > 0) ? edgeStack.peek() : 0;

		if (chosenEdge != 0) {
			ev = getEdgeView(chosenEdge);
		}

		return ev;
	}

	// AJK: 04/25/06 END
	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void addNodeContextMenuListener(NodeContextMenuListener l) {
		// System.out.println("Adding NodeContextListener: " + l);
		getCanvas().addNodeContextMenuListener(l);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param l
	 *            DOCUMENT ME!
	 */
	public void removeNodeContextMenuListener(NodeContextMenuListener l) {
		getCanvas().removeNodeContextMenuListener(l);
	}

	// AJK: 04/27/06 END
	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void addEdgeContextMenuListener(EdgeContextMenuListener l) {
		// System.out.println("Adding EdgeContextListener: " + l);
		getCanvas().addEdgeContextMenuListener(l);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param l
	 *            DOCUMENT ME!
	 */
	public void removeEdgeContextMenuListener(EdgeContextMenuListener l) {
		getCanvas().removeEdgeContextMenuListener(l);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final float getAnchorSize() {
		return DEFAULT_ANCHOR_SIZE;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final Paint getAnchorSelectedPaint() {
		return DEFAULT_ANCHOR_SELECTED_PAINT;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final Paint getAnchorUnselectedPaint() {
		return DEFAULT_ANCHOR_UNSELECTED_PAINT;
	}

	/**
	 * This is inefficient, but there is no way to sync. giny type and renderer
	 * type.
	 *
	 * @return map of shape ids to shapes
	 */
	public static Map<Integer, Shape> getNodeShapes() {
		final Map<Byte, Shape> nodeShapes = GraphGraphics.getNodeShapes();
		final Map<Integer, Shape> ginyKeyShapes = new HashMap<Integer, Shape>();

		Shape shape;

		for (Byte key : nodeShapes.keySet()) {
			shape = nodeShapes.get(key);
			ginyKeyShapes.put(GinyUtil.getGinyNodeType(key), shape);
		}

		return ginyKeyShapes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Map<Integer, Shape> getArrowShapes() {
		final Map<Byte, Shape> arrowShapes = GraphGraphics.getArrowShapes();
		final Map<Integer, Shape> ginyKeyShapes = new HashMap<Integer, Shape>();

		Shape shape;

		for (Byte key : arrowShapes.keySet()) {
			shape = arrowShapes.get(key);
			ginyKeyShapes.put(GinyUtil.getGinyArrowType(key), shape);
		}

		return ginyKeyShapes;
	}

	private double checkZoom(double zoom, double orig) {
		if ( zoom > 0 ) 
			return zoom;
	
		System.out.println("invalid zoom: " + zoom + "   using orig: " + orig);
		return orig;
	}
}
