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
package org.cytoscape.ding.impl;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import org.cytoscape.ding.EdgeView;
import org.cytoscape.ding.GraphView;
import org.cytoscape.ding.GraphViewChangeListener;
import org.cytoscape.ding.GraphViewObject;
import org.cytoscape.ding.NodeView;
import org.cytoscape.ding.PrintLOD;
import org.cytoscape.graph.render.immed.GraphGraphics;
import org.cytoscape.graph.render.stateful.GraphLOD;
import org.cytoscape.graph.render.stateful.GraphRenderer;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.spacial.SpacialEntry2DEnumerator;
import org.cytoscape.spacial.SpacialIndex2D;
import org.cytoscape.spacial.SpacialIndex2DFactory;
import org.cytoscape.util.intr.IntBTree;
import org.cytoscape.util.intr.IntEnumerator;
import org.cytoscape.util.intr.IntHash;
import org.cytoscape.util.intr.IntStack;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewChangeListener;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.work.UndoSupport;
import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.work.TaskManager;

import phoebe.PhoebeCanvasDropListener;
import phoebe.PhoebeCanvasDroppable;

/**
 * DING implementation of the GINY view.
 * 
 * Explain relationship to cytoscape.
 * 
 * Throughout this code I am assuming that nodes or edges are never removed from
 * the underlying RootGraph. This assumption was made in the old GraphView
 * implementation. Removal from the RootGraph is the only thing that can affect
 * m_drawPersp and m_structPersp that is beyond our control.
 * 
 * @author Nerius Landys
 */
public class DGraphView implements RenderingEngine, GraphView, Printable,
		PhoebeCanvasDroppable, ViewChangeListener {

	private static enum ZOrder {
		BACKGROUND_PANE, NETWORK_PANE, FOREGROUND_PANE;
		int layer() {
			if (this == BACKGROUND_PANE)
				return -30000;

			if (this == NETWORK_PANE)
				return 0;

			if (this == FOREGROUND_PANE)
				return 301;

			return 0;
		}
	}

	static final float DEFAULT_ANCHOR_SIZE = 9.0f;
	static final Paint DEFAULT_ANCHOR_SELECTED_PAINT = Color.red;
	static final Paint DEFAULT_ANCHOR_UNSELECTED_PAINT = Color.black;

	/**
	 * Enum to identify ding canvases - used in getCanvas(Canvas canvasId)
	 */
	public enum Canvas {
		BACKGROUND_CANVAS, NETWORK_CANVAS, FOREGROUND_CANVAS;
	}

	public enum ShapeType {
		NODE_SHAPE, LINE_TYPE, ARROW_SHAPE;
	}

	/**
	 * Common object used for synchronization.
	 */
	final Object m_lock = new Object();

	/**
	 * A common buffer object used to pass information about. X-Y coords of the
	 * minimum bounding box?
	 */
	final float[] m_extentsBuff = new float[4];

	/**
	 * A common general path variable used for holding lots of shapes.
	 */
	final GeneralPath m_path = new GeneralPath();

	/**
	 * The graph model that will be viewed.
	 */
	CyNetwork m_perspective;

	/**
	 * Holds the NodeView data for the nodes that are visible. This will change
	 * as nodes are hidden from the view.
	 */
	CySubNetwork m_drawPersp;

	/**
	 * Holds all of the NodeViews, regardless of whether they're visualized.
	 */
	// CyNetwork m_structPersp;
	/**
	 * RTree used for querying node positions.
	 */
	SpacialIndex2D m_spacial;

	/**
	 * RTree used for querying Edge Handle positions. Used by DNodeView,
	 * DEdgeView, and InnerCanvas.
	 */
	SpacialIndex2D m_spacialA;

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
	HashMap<Integer, NodeView> m_nodeViewMap;

	/**
	 *
	 */
	HashMap<Integer, EdgeView> m_edgeViewMap;

	/**
	 *
	 */
	Long m_identifier;

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

	CyNetworkView cyNetworkView;

	RootVisualLexicon rootLexicon;

	Map<NodeViewTaskFactory, Map> nodeViewTFs;
	Map<EdgeViewTaskFactory, Map> edgeViewTFs;
	Map<NetworkViewTaskFactory, Map> emptySpaceTFs;

	TunableInterceptor interceptor;
	TaskManager manager;

	// Will be injected.
	private VisualLexicon dingLexicon;

	/**
	 * Creates a new DGraphView object.
	 * 
	 * @param perspective
	 *            The graph model that we'll be creating a view for.
	 */
	public DGraphView(CyNetworkView view, CyDataTableFactory dataFactory,
			CyRootNetworkFactory cyRoot, UndoSupport undo,
			SpacialIndex2DFactory spacialFactory, RootVisualLexicon vpc,
			VisualLexicon dingLexicon,
			Map<NodeViewTaskFactory, Map> nodeViewTFs,
			Map<EdgeViewTaskFactory, Map> edgeViewTFs,
			Map<NetworkViewTaskFactory, Map> emptySpaceTFs,
			TunableInterceptor interceptor, TaskManager manager) {
		m_perspective = view.getSource();
		cyNetworkView = view;
		rootLexicon = vpc;
		this.dingLexicon = dingLexicon;

		this.nodeViewTFs = nodeViewTFs;
		this.edgeViewTFs = edgeViewTFs;
		this.emptySpaceTFs = emptySpaceTFs;

		this.interceptor = interceptor;
		this.manager = manager;

		CyDataTable nodeCAM = dataFactory.createTable("node view", false);
		nodeCAM.createColumn("hidden", Boolean.class, false);
		m_perspective.getNodeCyDataTables().put("VIEW", nodeCAM);

		CyDataTable edgeCAM = dataFactory.createTable("edge view", false);
		edgeCAM.createColumn("hidden", Boolean.class, false);
		m_perspective.getEdgeCyDataTables().put("VIEW", edgeCAM);

		// creating empty subnetworks
		m_drawPersp = cyRoot.convert(m_perspective).addMetaNode().getSubNetwork();

		m_spacial = spacialFactory.createSpacialIndex2D();
		m_spacialA = spacialFactory.createSpacialIndex2D();
		m_nodeDetails = new DNodeDetails(this);
		m_edgeDetails = new DEdgeDetails(this);
		m_nodeViewMap = new HashMap<Integer, NodeView>();
		m_edgeViewMap = new HashMap<Integer, EdgeView>();
		m_printLOD = new PrintLOD();
		m_defaultNodeXMin = 0.0f;
		m_defaultNodeYMin = 0.0f;
		m_defaultNodeXMax = m_defaultNodeXMin + DNodeView.DEFAULT_WIDTH;
		m_defaultNodeYMax = m_defaultNodeYMin + DNodeView.DEFAULT_HEIGHT;
		m_networkCanvas = new InnerCanvas(m_lock, this, undo);
		m_backgroundCanvas = new ArbitraryGraphicsCanvas(m_perspective, this,
				m_networkCanvas, Color.white, true, true);
		addViewportChangeListener(m_backgroundCanvas);
		m_foregroundCanvas = new ArbitraryGraphicsCanvas(m_perspective, this,
				m_networkCanvas, Color.white, true, false);
		addViewportChangeListener(m_foregroundCanvas);
		m_selectedNodes = new IntBTree();
		m_selectedEdges = new IntBTree();
		m_selectedAnchors = new IntBTree();

		// from DingNetworkView
		this.title = m_perspective.attrs().get("name", String.class);

		for (CyNode nn : m_perspective.getNodeList())
			addNodeView(nn);

		for (CyEdge ee : m_perspective.getEdgeList())
			addEdgeView(ee);

		// read in visual properties from view obj
		Collection<VisualProperty<?>> netVPs = rootLexicon
				.getVisualProperties(NETWORK);
		for (VisualProperty<?> vp : netVPs)
			visualPropertySet(vp, cyNetworkView.getVisualProperty(vp));

		new FlagAndSelectionHandler(this);
		new AddDeleteHandler(this);
	}

	/**
	 * Returns the graph model that this view was created for.
	 * 
	 * @return The GraphPerspective that the view was created for.
	 */
	public CyNetwork getGraphPerspective() {
		return m_perspective;
	}

	public CyNetwork getNetwork() {
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
					((DNodeView) getNodeView(unselectedNodes[i]))
							.unselectInternal();

				m_contentChanged = true;
			}
		}

		if (unselectedNodes.length > 0) {
			final GraphViewChangeListener listener = m_lis[0];

			if (listener != null) {
				listener.graphViewChanged(new GraphViewNodesUnselectedEvent(
						this, makeNodeList(unselectedNodes, this)));
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
					((DEdgeView) getEdgeView(unselectedEdges[i]))
							.unselectInternal();

				m_contentChanged = true;
			}
		}

		if (unselectedEdges.length > 0) {
			final GraphViewChangeListener listener = m_lis[0];

			if (listener != null) {
				listener.graphViewChanged(new GraphViewEdgesUnselectedEvent(
						this, makeEdgeList(unselectedEdges, this)));
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
			final IntEnumerator elms = m_selectedNodes.searchRange(
					Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			final int[] returnThis = new int[elms.numRemaining()];

			for (int i = 0; i < returnThis.length; i++)
				// GINY requires all node indices to be negative (why?),
				// hence the bitwise complement here.
				returnThis[i] = elms.nextInt();

			return returnThis;
		}
	}

	/**
	 * Returns a list of selected node objects.
	 * 
	 * @return A list of selected node objects.
	 */
	public List<CyNode> getSelectedNodes() {
		synchronized (m_lock) {
			// all nodes from the btree
			final IntEnumerator elms = m_selectedNodes.searchRange(
					Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			final ArrayList<CyNode> returnThis = new ArrayList<CyNode>();

			while (elms.numRemaining() > 0)
				// GINY requires all node indices to be negative (why?),
				// hence the bitwise complement here.
				returnThis.add(m_nodeViewMap.get(
						Integer.valueOf(elms.nextInt())).getNode());

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
			final IntEnumerator elms = m_selectedEdges.searchRange(
					Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			final int[] returnThis = new int[elms.numRemaining()];

			for (int i = 0; i < returnThis.length; i++)
				returnThis[i] = elms.nextInt();

			return returnThis;
		}
	}

	/**
	 * Returns a list of selected edge objects.
	 * 
	 * @return A list of selected edge objects.
	 */
	public List<CyEdge> getSelectedEdges() {
		synchronized (m_lock) {
			final IntEnumerator elms = m_selectedEdges.searchRange(
					Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			final ArrayList<CyEdge> returnThis = new ArrayList<CyEdge>();

			while (elms.numRemaining() > 0)
				returnThis.add(m_edgeViewMap.get(
						Integer.valueOf(elms.nextInt())).getEdge());

			return returnThis;
		}
	}

	/**
	 * Add GraphViewChangeListener to linked list of GraphViewChangeListeners.
	 * AAAAAARRRGGGGHHHHHH!!!!
	 * 
	 * @param l
	 *            GraphViewChangeListener to be added to the list.
	 */
	public void addGraphViewChangeListener(GraphViewChangeListener l) {
		m_lis[0] = GraphViewChangeListenerChain.add(m_lis[0], l);
	}

	/**
	 * Remove GraphViewChangeListener from linked list of
	 * GraphViewChangeListeners. AAAAAARRRGGGGHHHHHH!!!!
	 * 
	 * @param l
	 *            GraphViewChangeListener to be removed from the list.
	 */
	public void removeGraphViewChangeListener(GraphViewChangeListener l) {
		m_lis[0] = GraphViewChangeListenerChain.remove(m_lis[0], l);
	}

	/**
	 * Sets the background color on the canvas.
	 * 
	 * @param paint
	 *            The Paint (color) to apply to the background.
	 */
	public void setBackgroundPaint(Paint paint) {
		synchronized (m_lock) {
			if (paint instanceof Color) {
				m_backgroundCanvas.setBackground((Color) paint);
				m_contentChanged = true;
			} else {
				System.out
						.println("DGraphView.setBackgroundPaint(), Color not found!");
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
	 * Returns the InnerCanvas object. The InnerCanvas object is the actual
	 * component that the network is rendered on.
	 * 
	 * @return The InnerCanvas object.
	 */
	public Component getComponent() {
		return m_networkCanvas;
	}

	/**
	 * Adds a NodeView object to the GraphView. Creates NodeView if one doesn't
	 * already exist.
	 * 
	 * @param nodeInx
	 *            The index of the NodeView object to be added.
	 * 
	 * @return The NodeView object that is added to the GraphView.
	 */
	public NodeView addNodeView(CyNode node) {
		NodeView newView = null;

		synchronized (m_lock) {
			newView = addNodeViewInternal(node);

			if (newView == null) {
				return m_nodeViewMap.get(node.getIndex());
			}

			m_contentChanged = true;
		}

		final GraphViewChangeListener listener = m_lis[0];

		if (listener != null) {
			listener.graphViewChanged(new GraphViewNodesRestoredEvent(this,
					makeList(newView.getNode())));
		}

		return newView;
	}

	/**
	 * Should synchronize around m_lock.
	 */
	private NodeView addNodeViewInternal(CyNode node) {
		final int nodeInx = node.getIndex();
		final NodeView oldView = m_nodeViewMap.get(nodeInx);

		if (oldView != null) {
			return null;
		}

		m_drawPersp.addNode(node);

		// m_structPersp.restoreNode(nodeInx);

		final View<CyNode> nv = cyNetworkView.getNodeView(node);
		final NodeView newView = new DNodeView(this, nodeInx, nv);
		nv.addViewChangeListener(newView);

		m_nodeViewMap.put(nodeInx, newView);
		m_spacial.insert(nodeInx, m_defaultNodeXMin, m_defaultNodeYMin,
				m_defaultNodeXMax, m_defaultNodeYMax);

		// read in visual properties from view obj
		Collection<VisualProperty<?>> nodeVPs = rootLexicon
				.getVisualProperties(NODE);
		for (VisualProperty<?> vp : nodeVPs) {
			newView.visualPropertySet(vp, nv.getVisualProperty(vp));
		}
		return newView;
	}

	/**
	 * Adds EdgeView to the GraphView.
	 * 
	 * @param edgeInx
	 *            The index of EdgeView to be added.
	 * 
	 * @return The EdgeView that was added.
	 */
	public EdgeView addEdgeView(final CyEdge edge) {
		NodeView sourceNode = null;
		NodeView targetNode = null;
		EdgeView edgeView = null;
		if (edge == null)
			throw new NullPointerException("edge is null");

		synchronized (m_lock) {
			final int edgeInx = edge.getIndex();
			final EdgeView oldView = m_edgeViewMap.get(edgeInx);

			if (oldView != null) {
				return oldView;
			}

			sourceNode = addNodeViewInternal(edge.getSource());
			targetNode = addNodeViewInternal(edge.getTarget());

			m_drawPersp.addEdge(edge);

			// m_structPersp.restoreEdge(edgeInx);
			View<CyEdge> ev = cyNetworkView.getEdgeView(edge);
			edgeView = new DEdgeView(this, edgeInx, ev);
			cyNetworkView.getEdgeView(edge).addViewChangeListener(edgeView);

			m_edgeViewMap.put(Integer.valueOf(edgeInx), edgeView);
			m_contentChanged = true;

			// read in visual properties from view obj
			Collection<VisualProperty<?>> edgeVPs = rootLexicon
					.getVisualProperties(EDGE);
			for (VisualProperty<?> vp : edgeVPs)
				edgeView.visualPropertySet(vp, ev.getVisualProperty(vp));

		}

		// Under no circumstances should we be holding m_lock when the listener
		// events are fired.
		final GraphViewChangeListener listener = m_lis[0];

		if (listener != null) {
			// Only fire this event if either of the nodes is new. The node
			// will be null if it already existed.
			if ((sourceNode != null) || (targetNode != null)) {
				int[] nodeInx;

				if (sourceNode == null) {
					nodeInx = new int[] { targetNode.getRootGraphIndex() };
				} else if (targetNode == null) {
					nodeInx = new int[] { sourceNode.getRootGraphIndex() };
				} else {
					nodeInx = new int[] { sourceNode.getRootGraphIndex(),
							targetNode.getRootGraphIndex() };
				}

				listener.graphViewChanged(new GraphViewNodesRestoredEvent(this,
						makeNodeList(nodeInx, this)));
			}

			listener.graphViewChanged(new GraphViewEdgesRestoredEvent(this,
					makeList(edgeView.getEdge())));
		}

		return edgeView;
	}

	/**
	 * Removes a NodeView based on specified NodeView.
	 * 
	 * @param nodeView
	 *            The NodeView object to be removed.
	 * 
	 * @return The NodeView object that was removed.
	 */
	public NodeView removeNodeView(NodeView nodeView) {
		return removeNodeView(nodeView.getRootGraphIndex());
	}

	/**
	 * Removes a NodeView based on specified Node.
	 * 
	 * @param node
	 *            The Node object connected to the NodeView to be removed.
	 * 
	 * @return The NodeView object that was removed.
	 */
	public NodeView removeNodeView(CyNode node) {
		return removeNodeView(node.getIndex());
	}

	/**
	 * Removes a NodeView based on a specified index.
	 * 
	 * @param nodeInx
	 *            The index of the NodeView to be removed.
	 * 
	 * @return The NodeView object that was removed.
	 */
	public NodeView removeNodeView(int nodeInx) {
		final List<CyEdge> hiddenEdgeInx;
		final DNodeView returnThis;
		final CyNode nnode;

		synchronized (m_lock) {
			nnode = m_perspective.getNode(nodeInx);

			// We have to query edges in the m_structPersp, not m_drawPersp
			// because what if the node is hidden?
			hiddenEdgeInx = m_perspective.getAdjacentEdgeList(nnode,
					CyEdge.Type.ANY);

			// This isn't an error. Only if the nodeInx is invalid will
			// getAdjacentEdgeIndicesArray
			// return null. If there are no adjacent edges, then it will return
			// an array of length 0.
			if (hiddenEdgeInx == null)
				return null;

			for (CyEdge ee : hiddenEdgeInx)
				removeEdgeViewInternal(ee.getIndex());

			returnThis = (DNodeView) m_nodeViewMap.remove(Integer
					.valueOf(nodeInx));
			returnThis.unselectInternal();

			// If this node was hidden, it won't be in m_drawPersp.
			m_drawPersp.removeNode(nnode);
			// m_structPersp.removeNode(nodeInx);
			m_nodeDetails.unregisterNode(nodeInx);

			// If this node was hidden, it won't be in m_spacial.
			m_spacial.delete(nodeInx);

			// m_selectedNodes.delete(nodeInx);
			returnThis.m_view = null;
			m_contentChanged = true;
		}

		final GraphViewChangeListener listener = m_lis[0];

		if (listener != null) {
			if (hiddenEdgeInx.size() > 0) {
				listener.graphViewChanged(new GraphViewEdgesHiddenEvent(this,
						hiddenEdgeInx));
			}

			listener.graphViewChanged(new GraphViewNodesHiddenEvent(this,
					makeList(returnThis.getNode())));
		}

		return returnThis;
	}

	/**
	 * Removes an EdgeView based on an EdgeView.
	 * 
	 * @param edgeView
	 *            The EdgeView to be removed.
	 * 
	 * @return The EdgeView that was removed.
	 */
	public EdgeView removeEdgeView(EdgeView edgeView) {
		return removeEdgeView(edgeView.getRootGraphIndex());
	}

	/**
	 * Removes an EdgeView based on an Edge.
	 * 
	 * @param edge
	 *            The Edge of the EdgeView to be removed.
	 * 
	 * @return The EdgeView that was removed.
	 */
	public EdgeView removeEdgeView(CyEdge edge) {
		return removeEdgeView(edge.getIndex());
	}

	/**
	 * Removes an EdgeView based on an EdgeIndex.
	 * 
	 * @param edgeInx
	 *            The edge index of the EdgeView to be removed.
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
						makeList(returnThis.getEdge())));
			}
		}

		return returnThis;
	}

	/**
	 * Should synchronize around m_lock.
	 */
	private DEdgeView removeEdgeViewInternal(int edgeInx) {
		final DEdgeView returnThis = (DEdgeView) m_edgeViewMap.remove(Integer
				.valueOf(edgeInx));

		CyEdge eedge = m_perspective.getEdge(edgeInx);

		if (returnThis == null) {
			return returnThis;
		}

		returnThis.unselectInternal();

		// If this edge view was hidden, it won't be in m_drawPersp.
		m_drawPersp.removeEdge(eedge);
		// m_structPersp.hideEdge(edgeInx);
		m_edgeDetails.unregisterEdge(edgeInx);

		// m_selectedEdges.delete(edgeInx);
		returnThis.m_view = null;

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Long getIdentifier() {
		return m_identifier;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param id
	 *            DOCUMENT ME!
	 */
	public void setIdentifier(Long id) {
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
			m_networkCanvas.m_scaleFactor = checkZoom(zoom,
					m_networkCanvas.m_scaleFactor);
			m_viewportChanged = true;
		}

		updateView();
	}

	/**
	 * DOCUMENT ME!
	 */
	public void fitContent() {
		synchronized (m_lock) {
			if (m_spacial.queryOverlap(Float.NEGATIVE_INFINITY,
					Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,
					Float.POSITIVE_INFINITY, m_extentsBuff, 0, false)
					.numRemaining() == 0) {
				return;
			}

			m_networkCanvas.m_xCenter = (((double) m_extentsBuff[0]) + ((double) m_extentsBuff[2])) / 2.0d;
			m_networkCanvas.m_yCenter = (((double) m_extentsBuff[1]) + ((double) m_extentsBuff[3])) / 2.0d;
			final double zoom = Math
					.min(
							((double) m_networkCanvas.getWidth())
									/ (((double) m_extentsBuff[2]) - ((double) m_extentsBuff[0])),
							((double) m_networkCanvas.getHeight())
									/ (((double) m_extentsBuff[3]) - ((double) m_extentsBuff[1])));
			m_networkCanvas.m_scaleFactor = checkZoom(zoom,
					m_networkCanvas.m_scaleFactor);
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
	 * Returns an iterator of all node views, including those that are currently
	 * hidden.
	 * 
	 * @return DOCUMENT ME!
	 */
	public Iterator<NodeView> getNodeViewsIterator() {
		synchronized (m_lock) {
			return m_nodeViewMap.values().iterator();
		}
	}

	/**
	 * Returns the count of all node views, including those that are currently
	 * hidden.
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getNodeViewCount() {
		synchronized (m_lock) {
			return m_nodeViewMap.size();
		}
	}

	/**
	 * Returns the count of all edge views, including those that are currently
	 * hidden.
	 * 
	 * @return DOCUMENT ME!
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
	 * @return DOCUMENT ME!
	 */
	public NodeView getNodeView(CyNode node) {
		return getNodeView(node.getIndex());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 */
	public NodeView getNodeView(int nodeInx) {
		synchronized (m_lock) {
			return (NodeView) m_nodeViewMap.get(Integer.valueOf(nodeInx));
		}
	}

	/**
	 * Returns a list of all edge views, including those that are currently
	 * hidden.
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<EdgeView> getEdgeViewsList() {
		synchronized (m_lock) {
			final ArrayList<EdgeView> returnThis = new ArrayList<EdgeView>(
					m_edgeViewMap.size());
			final Iterator<EdgeView> values = m_edgeViewMap.values().iterator();

			while (values.hasNext())
				returnThis.add(values.next());

			return returnThis;
		}
	}

	/**
	 * Returns all edge views (including the hidden ones) that are either 1.
	 * directed, having oneNode as source and otherNode as target or 2.
	 * undirected, having oneNode and otherNode as endpoints. Note that this
	 * behaviour is similar to that of CyNetwork.edgesList(Node, Node).
	 * 
	 * @param oneNode
	 *            DOCUMENT ME!
	 * @param otherNode
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<EdgeView> getEdgeViewsList(CyNode oneNode, CyNode otherNode) {
		synchronized (m_lock) {
			List<CyEdge> edges = m_perspective.getConnectingEdgeList(oneNode,
					otherNode, CyEdge.Type.ANY);

			if (edges == null) {
				return null;
			}

			final ArrayList<EdgeView> returnThis = new ArrayList<EdgeView>();
			Iterator<CyEdge> it = edges.iterator();

			while (it.hasNext()) {
				CyEdge e = (CyEdge) it.next();
				EdgeView ev = getEdgeView(e);
				if (ev != null)
					returnThis.add(ev);
			}

			return returnThis;
		}
	}

	/**
	 * Similar to getEdgeViewsList(Node, Node), only that one has control of
	 * whether or not to include undirected edges.
	 * 
	 * @param oneNodeInx
	 *            DOCUMENT ME!
	 * @param otherNodeInx
	 *            DOCUMENT ME!
	 * @param includeUndirected
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<EdgeView> getEdgeViewsList(int oneNodeInx, int otherNodeInx,
			boolean includeUndirected) {
		CyNode n1;
		CyNode n2;
		synchronized (m_lock) {
			n1 = m_perspective.getNode(oneNodeInx);
			n2 = m_perspective.getNode(otherNodeInx);
		}
		return getEdgeViewsList(n1, n2);
	}

	/**
	 * Returns an edge view with specified edge index whether or not the edge
	 * view is hidden; null is returned if view does not exist.
	 * 
	 * @param edgeInx
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public EdgeView getEdgeView(int edgeInx) {
		synchronized (m_lock) {
			return (EdgeView) m_edgeViewMap.get(Integer.valueOf(edgeInx));
		}
	}

	/**
	 * Returns an iterator of all edge views, including those that are currently
	 * hidden.
	 * 
	 * @return DOCUMENT ME!
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
	public EdgeView getEdgeView(CyEdge edge) {
		return getEdgeView(edge.getIndex());
	}

	/**
	 * Alias to getEdgeViewCount().
	 * 
	 * @return DOCUMENT ME!
	 */
	public int edgeCount() {
		return getEdgeViewCount();
	}

	/**
	 * Alias to getNodeViewCount().
	 * 
	 * @return DOCUMENT ME!
	 */
	public int nodeCount() {
		return getNodeViewCount();
	}

	/**
	 * @param obj
	 *            should be either a DEdgeView or a DNodeView.
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean hideGraphObject(Object obj) {
		return hideGraphObjectInternal(obj, true);
	}

	private boolean hideGraphObjectInternal(Object obj,
			boolean fireListenerEvents) {
		if (obj instanceof DEdgeView) {
			int edgeInx;
			CyEdge edge;

			synchronized (m_lock) {
				edgeInx = ((DEdgeView) obj).getRootGraphIndex();
				edge = ((DEdgeView) obj).getEdge();

				edge.getCyRow("VIEW").set("hidden", true);
				if (!m_drawPersp.removeEdge(edge))
					return false;

				((DEdgeView) obj).unselectInternal();
				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					listener.graphViewChanged(new GraphViewEdgesHiddenEvent(
							this, makeList(((DEdgeView) obj).getEdge())));
				}
			}

			return true;
		} else if (obj instanceof DNodeView) {
			List<CyEdge> edges;
			int nodeInx;
			CyNode nnode;

			synchronized (m_lock) {
				final DNodeView nView = (DNodeView) obj;
				nodeInx = nView.getRootGraphIndex();
				nnode = m_perspective.getNode(nodeInx);
				edges = m_drawPersp.getAdjacentEdgeList(nnode, CyEdge.Type.ANY);

				if (edges == null || edges.size() <= 0) {
					return false;
				}

				for (CyEdge ee : edges)
					hideGraphObjectInternal(m_edgeViewMap.get(ee.getIndex()),
							false);

				nView.unselectInternal();
				m_spacial.exists(nodeInx, m_extentsBuff, 0);
				nView.m_hiddenXMin = m_extentsBuff[0];
				nView.m_hiddenYMin = m_extentsBuff[1];
				nView.m_hiddenXMax = m_extentsBuff[2];
				nView.m_hiddenYMax = m_extentsBuff[3];
				m_drawPersp.removeNode(nnode);
				nnode.getCyRow("VIEW").set("hidden", true);
				m_spacial.delete(nodeInx);
				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					if (edges.size() > 0) {
						listener
								.graphViewChanged(new GraphViewEdgesHiddenEvent(
										this, edges));
					}

					listener.graphViewChanged(new GraphViewNodesHiddenEvent(
							this, makeList(nnode)));
				}
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param obj
	 *            should be either a DEdgeView or a DNodeView.
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean showGraphObject(Object obj) {
		return showGraphObjectInternal(obj, true);
	}

	private boolean showGraphObjectInternal(Object obj,
			boolean fireListenerEvents) {
		if (obj instanceof DNodeView) {
			int nodeInx;
			final DNodeView nView = (DNodeView) obj;

			synchronized (m_lock) {
				nodeInx = nView.getRootGraphIndex();
				CyNode nnode = m_perspective.getNode(nodeInx);

				if (nnode == null) {
					return false;
				}

				nnode.getCyRow("VIEW").set("hidden", false);
				if (!m_drawPersp.addNode(nnode))
					return false;

				m_spacial.insert(nodeInx, nView.m_hiddenXMin,
						nView.m_hiddenYMin, nView.m_hiddenXMax,
						nView.m_hiddenYMax);
				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					listener.graphViewChanged(new GraphViewNodesRestoredEvent(
							this, makeList(nView.getNode())));
				}
			}

			return true;
		} else if (obj instanceof DEdgeView) {
			CyNode sourceNode;
			CyNode targetNode;
			CyEdge newEdge;

			synchronized (m_lock) {
				final CyEdge edge = m_perspective.getEdge(((DEdgeView) obj)
						.getRootGraphIndex());

				if (edge == null) {
					return false;
				}

				// The edge exists in m_structPersp, therefore its source and
				// target
				// node views must also exist.
				sourceNode = edge.getSource();

				if (!showGraphObjectInternal(getNodeView(sourceNode), false)) {
					sourceNode = null;
				}

				targetNode = edge.getTarget();

				if (!showGraphObjectInternal(getNodeView(targetNode), false)) {
					targetNode = null;
				}

				newEdge = edge;

				newEdge.getCyRow("VIEW").set("hidden", false);
				if (!m_drawPersp.addEdge(newEdge))
					return false;

				m_contentChanged = true;
			}

			if (fireListenerEvents) {
				final GraphViewChangeListener listener = m_lis[0];

				if (listener != null) {
					if (sourceNode != null) {
						listener
								.graphViewChanged(new GraphViewNodesRestoredEvent(
										this, makeList(sourceNode)));
					}

					if (targetNode != null) {
						listener
								.graphViewChanged(new GraphViewNodesRestoredEvent(
										this, makeList(targetNode)));
					}

					listener.graphViewChanged(new GraphViewEdgesRestoredEvent(
							this, makeList(newEdge)));
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

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nodeInx
	 *            DOCUMENT ME!
	 * @param data
	 *            DOCUMENT ME!
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
	 * DOCUMENT ME!
	 * 
	 * @param x
	 *            DOCUMENT ME!
	 * @param y
	 *            DOCUMENT ME!
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
			return new Point2D.Double(m_networkCanvas.m_xCenter,
					m_networkCanvas.m_yCenter);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void fitSelected() {
		synchronized (m_lock) {
			IntEnumerator selectedElms = m_selectedNodes.searchRange(
					Integer.MIN_VALUE, Integer.MAX_VALUE, false);

			// Only check for selected edges if we don't have selected nodes.
			if (selectedElms.numRemaining() == 0 && edgeSelectionEnabled()) {
				selectedElms = getSelectedEdgeNodes();
				if (selectedElms.numRemaining() == 0)
					return;
			}

			float xMin = Float.POSITIVE_INFINITY;
			float yMin = Float.POSITIVE_INFINITY;
			float xMax = Float.NEGATIVE_INFINITY;
			float yMax = Float.NEGATIVE_INFINITY;

			int leftMost = 0;
			int rightMost = 0;

			while (selectedElms.numRemaining() > 0) {
				final int node = selectedElms.nextInt();
				m_spacial.exists(node, m_extentsBuff, 0);
				if (m_extentsBuff[0] < xMin) {
					xMin = m_extentsBuff[0];
					leftMost = node;
				}

				if (m_extentsBuff[2] > xMax) {
					xMax = m_extentsBuff[2];
					rightMost = node;
				}

				yMin = Math.min(yMin, m_extentsBuff[1]);
				yMax = Math.max(yMax, m_extentsBuff[3]);
			}

			xMin = xMin - (getLabelWidth(leftMost) / 2);
			xMax = xMax + (getLabelWidth(rightMost) / 2);

			m_networkCanvas.m_xCenter = (((double) xMin) + ((double) xMax)) / 2.0d;
			m_networkCanvas.m_yCenter = (((double) yMin) + ((double) yMax)) / 2.0d;
			final double zoom = Math.min(((double) m_networkCanvas.getWidth())
					/ (((double) xMax) - ((double) xMin)),
					((double) m_networkCanvas.getHeight())
							/ (((double) yMax) - ((double) yMin)));
			m_networkCanvas.m_scaleFactor = checkZoom(zoom,
					m_networkCanvas.m_scaleFactor);
			m_viewportChanged = true;
		}
	}

	/**
	 * @return An IntEnumerator listing the nodes that are endpoints of the
	 *         currently selected edges.
	 */
	private IntEnumerator getSelectedEdgeNodes() {
		synchronized (m_lock) {
			final IntEnumerator selectedEdges = m_selectedEdges.searchRange(
					Integer.MIN_VALUE, Integer.MAX_VALUE, false);

			final IntHash nodeIds = new IntHash();

			while (selectedEdges.numRemaining() > 0) {
				final int edge = selectedEdges.nextInt();
				CyEdge currEdge = getEdgeView(edge).getEdge();

				CyNode source = currEdge.getSource();
				int sourceId = source.getIndex();
				nodeIds.put(sourceId);

				CyNode target = currEdge.getTarget();
				int targetId = target.getIndex();
				nodeIds.put(targetId);
			}

			return nodeIds.elements();
		}
	}

	private int getLabelWidth(int node) {
		DNodeView x = ((DNodeView) getNodeView(node));
		if (x == null)
			return 0;

		String s = x.getText();
		if (s == null)
			return 0;

		char[] lab = s.toCharArray();
		if (lab == null)
			return 0;

		if (m_networkCanvas.m_fontMetrics == null)
			return 0;

		return m_networkCanvas.m_fontMetrics.charsWidth(lab, 0, lab.length);
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
	public void getNodesIntersectingRectangle(double xMinimum, double yMinimum,
			double xMaximum, double yMaximum,
			boolean treatNodeShapesAsRectangle, IntStack returnVal) {
		synchronized (m_lock) {
			final float xMin = (float) xMinimum;
			final float yMin = (float) yMinimum;
			final float xMax = (float) xMaximum;
			final float yMax = (float) yMaximum;
			final SpacialEntry2DEnumerator under = m_spacial.queryOverlap(xMin,
					yMin, xMax, yMax, null, 0, false);
			final int totalHits = under.numRemaining();

			if (treatNodeShapesAsRectangle) {
				for (int i = 0; i < totalHits; i++)
					returnVal.push(under.nextInt());
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
						m_networkCanvas.m_grafx.getNodeShape(m_nodeDetails
								.shape(node), m_extentsBuff[0],
								m_extentsBuff[1], m_extentsBuff[2],
								m_extentsBuff[3], m_path);

						if ((w > 0) && (h > 0)) {
							if (m_path.intersects(x, y, w, h)) {
								returnVal.push(node);
							}
						} else {
							if (m_path.contains(x, y)) {
								returnVal.push(node);
							}
						}
					} else {
						returnVal.push(node);
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
	public void queryDrawnEdges(int xMin, int yMin, int xMax, int yMax,
			IntStack returnVal) {
		synchronized (m_lock) {
			m_networkCanvas.computeEdgesIntersecting(xMin, yMin, xMax, yMax,
					returnVal);
		}
	}

	/**
	 * Extents of the nodes.
	 */
	public boolean getExtents(double[] extentsBuff) {
		synchronized (m_lock) {
			if (m_spacial.queryOverlap(Float.NEGATIVE_INFINITY,
					Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,
					Float.POSITIVE_INFINITY, m_extentsBuff, 0, false)
					.numRemaining() == 0) {
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
	public void drawSnapshot(Image img, GraphLOD lod, Paint bgPaint,
			double xCenter, double yCenter, double scaleFactor) {
		synchronized (m_lock) {
			GraphRenderer
					.renderGraph(m_drawPersp, m_spacial, lod, m_nodeDetails,
							m_edgeDetails, m_hash,
							new GraphGraphics(img, false), bgPaint, xCenter,
							yCenter, scaleFactor);
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
			((Graphics2D) g).translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());

			// make sure the whole image on the screen will fit to the printable
			// area of the paper
			double image_scale = Math.min(pageFormat.getImageableWidth()
					/ m_networkCanvas.getWidth(), pageFormat
					.getImageableHeight()
					/ m_networkCanvas.getHeight());

			if (image_scale < 1.0d) {
				((Graphics2D) g).scale(image_scale, image_scale);
			}

			// old school
			// g.clipRect(0, 0, getComponent().getWidth(),
			// getComponent().getHeight());
			// getComponent().print(g);

			// from InternalFrameComponent
			g.clipRect(0, 0, m_backgroundCanvas.getWidth(), m_backgroundCanvas
					.getHeight());
			m_backgroundCanvas.print(g);
			m_networkCanvas.print(g);
			m_foregroundCanvas.print(g);

			return PAGE_EXISTS;
		} else {
			return NO_SUCH_PAGE;
		}
	}

	/**
	 * Method to return a reference to the network canvas. This method existed
	 * before the addition of background and foreground canvases, and it remains
	 * for backward compatibility.
	 * 
	 * @return InnerCanvas
	 */
	public InnerCanvas getCanvas() {
		return m_networkCanvas;
	}

	/**
	 * Method to return a reference to a DingCanvas object, given a canvas id.
	 * 
	 * @param canvasId
	 *            Canvas
	 * @return DingCanvas
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
	 * Method to return a reference to an Image object, which represents the
	 * current network view.
	 * 
	 * @param width
	 *            Width of desired image.
	 * @param height
	 *            Height of desired image.
	 * @param shrink
	 *            Percent to shrink the network shown in the image. This doesn't
	 *            shrink the image, just the network shown, as if the user
	 *            zoomed out. Can be between 0 and 1, if not it will default to
	 *            1.
	 * @return Image
	 * @throws IllegalArgumentException
	 */
	public Image createImage(int width, int height, double shrink) {

		// check args
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException(
					"DGraphView.createImage(int width, int height): "
							+ "width and height arguments must be greater than zero");
		}

		if (shrink < 0 || shrink > 1.0) {
			System.out
					.println("DGraphView.createImage(width,height,shrink) shrink is invalid: "
							+ shrink + "  using default of 1.0");
			shrink = 1.0;
		}

		// create image to return
		Image image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
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
		setZoom(getZoom() * shrink);
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
		getNodesIntersectingRectangle(
				(float) locn[0],
				(float) locn[1],
				(float) locn[0],
				(float) locn[1],
				(m_networkCanvas.getLastRenderDetail() & GraphRenderer.LOD_HIGH_DETAIL) == 0,
				nodeStack);

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
	 * @return DOCUMENT ME!
	 */
	public EdgeView getPickedEdgeView(Point2D pt) {
		EdgeView ev = null;
		final IntStack edgeStack = new IntStack();
		queryDrawnEdges((int) pt.getX(), (int) pt.getY(), (int) pt.getX(),
				(int) pt.getY(), edgeStack);

		int chosenEdge = 0;
		chosenEdge = (edgeStack.size() > 0) ? edgeStack.peek() : 0;

		if (chosenEdge != 0) {
			ev = getEdgeView(chosenEdge);
		}

		return ev;
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

	private double checkZoom(double zoom, double orig) {
		if (zoom > 0)
			return zoom;

		System.out.println("invalid zoom: " + zoom + "   using orig: " + orig);
		return orig;
	}

	private String title;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param title
	 *            DOCUMENT ME!
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nodes
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean setSelected(CyNode[] nodes) {
		return setSelected(convertToViews(nodes));
	}

	private NodeView[] convertToViews(CyNode[] nodes) {
		NodeView[] views = new NodeView[nodes.length];

		for (int i = 0; i < nodes.length; i++) {
			views[i] = getNodeView(nodes[i]);
		}

		return views;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param node_views
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean setSelected(NodeView[] node_views) {
		for (int i = 0; i < node_views.length; i++) {
			node_views[i].select();
		}

		return true;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param edges
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean setSelected(CyEdge[] edges) {
		return setSelected(convertToViews(edges));
	}

	private EdgeView[] convertToViews(CyEdge[] edges) {
		EdgeView[] views = new EdgeView[edges.length];

		for (int i = 0; i < edges.length; i++) {
			views[i] = getEdgeView(edges[i]);
		}

		return views;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param edge_views
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean setSelected(EdgeView[] edge_views) {
		for (int i = 0; i < edge_views.length; i++) {
			edge_views[i].select();
		}

		return true;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<NodeView> getNodeViewsList() {
		ArrayList<NodeView> list = new ArrayList<NodeView>(getNodeViewCount());
		for (CyNode nn : getGraphPerspective().getNodeList())
			list.add(getNodeView(nn.getIndex()));

		return list;
	}

	public void addTransferComponent(JComponent comp) {
		m_networkCanvas.addTransferComponent(comp);
	}

	/**
	 * This method is used by freehep lib to export network as graphics.
	 */
	public void print(Graphics g) {
		m_backgroundCanvas.print(g);
		m_networkCanvas.print(g);
		m_foregroundCanvas.print(g);
	}

	/**
	 * This method is used by BitmapExporter to export network as graphics (png,
	 * jpg, bmp)
	 */
	public void printNoImposter(Graphics g) {
		m_backgroundCanvas.print(g);
		m_networkCanvas.printNoImposter(g);
		m_foregroundCanvas.print(g);
	}

	/**
	 * Our implementation of Component setBounds(). If we don't do this, the
	 * individual canvas do not get rendered.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param width
	 *            int
	 * @param height
	 *            int
	 */
	public void setBounds(int x, int y, int width, int height) {
		// call reshape on each canvas
		m_backgroundCanvas.setBounds(x, y, width, height);
		m_networkCanvas.setBounds(x, y, width, height);
		m_foregroundCanvas.setBounds(x, y, width, height);
	}

	public void setSize(Dimension d) {
		m_networkCanvas.setSize(d);
	}

	public Container getContainer(JLayeredPane jlp) {
		return new InternalFrameComponent(jlp, this);
	}

	public void addMouseListener(MouseListener m) {
		m_networkCanvas.addMouseListener(m);
	}

	public void addMouseMotionListener(MouseMotionListener m) {
		m_networkCanvas.addMouseMotionListener(m);
	}

	public void addKeyListener(KeyListener k) {
		m_networkCanvas.addKeyListener(k);
	}

	public void removeMouseListener(MouseListener m) {
		m_networkCanvas.removeMouseListener(m);
	}

	public void removeMouseMotionListener(MouseMotionListener m) {
		m_networkCanvas.removeMouseMotionListener(m);
	}

	public void removeKeyListener(KeyListener k) {
		m_networkCanvas.removeKeyListener(k);
	}

	public void addPhoebeCanvasDropListener(PhoebeCanvasDropListener l) {
		m_networkCanvas.addPhoebeCanvasDropListener(l);
	}

	public void removePhoebeCanvasDropListener(PhoebeCanvasDropListener l) {
		m_networkCanvas.removePhoebeCanvasDropListener(l);
	}

	static <X> List<X> makeList(X nodeOrEdge) {
		List<X> nl = new ArrayList<X>(1);
		nl.add(nodeOrEdge);
		return nl;
	}

	static List<CyNode> makeNodeList(int[] nodeids, GraphView view) {
		List<CyNode> l = new ArrayList<CyNode>(nodeids.length);
		for (int nid : nodeids)
			l.add(view.getNodeView(nid).getNode());

		return l;
	}

	static List<CyEdge> makeEdgeList(int[] edgeids, GraphView view) {
		List<CyEdge> l = new ArrayList<CyEdge>(edgeids.length);
		for (int nid : edgeids)
			l.add(view.getEdgeView(nid).getEdge());

		return l;
	}

	public void visualPropertySet(VisualProperty<?> vp, Object o) {
		if (o == null)
			return;

		if (vp == DVisualLexicon.NETWORK_NODE_SELECTION) {
			boolean b = ((Boolean) o).booleanValue();
			if (b)
				enableNodeSelection();
			else
				disableNodeSelection();
		} else if (vp == DVisualLexicon.NETWORK_EDGE_SELECTION) {
			boolean b = ((Boolean) o).booleanValue();
			if (b)
				enableEdgeSelection();
			else
				disableEdgeSelection();
		} else if (vp == TwoDVisualLexicon.NETWORK_BACKGROUND_COLOR) {
			setBackgroundPaint((Paint) o);
		} else if (vp == TwoDVisualLexicon.NETWORK_CENTER_X_LOCATION) {
			setCenter(((Double) o).doubleValue(), m_networkCanvas.m_yCenter);
		} else if (vp == TwoDVisualLexicon.NETWORK_CENTER_Y_LOCATION) {
			setCenter(m_networkCanvas.m_xCenter, ((Double) o).doubleValue());
		} else if (vp == TwoDVisualLexicon.NETWORK_SCALE_FACTOR) {
			setZoom(((Double) o).doubleValue());
		}
	}

	// ////// The following implements Presentation API ////////////

	public Printable getPrintable() {
		return this;
	}

	public Image getImage() {
		return null;
	}

	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProperties(Properties props) {
		// TODO Auto-generated method stub

	}

	public Icon getDefaultIcon(VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getImage(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	public VisualLexicon getVisualLexicon() {
		return this.dingLexicon;
	}

	public CyNetworkView getViewModel() {
		return cyNetworkView;
	}
}
