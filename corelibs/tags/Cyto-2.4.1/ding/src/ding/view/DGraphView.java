package ding.view;

import cytoscape.geom.rtree.RTree;

import cytoscape.geom.spacial.MutableSpacialIndex2D;
import cytoscape.geom.spacial.SpacialEntry2DEnumerator;

import cytoscape.graph.fixed.FixedGraph;

import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;

import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;

import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntStack;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class DGraphView
    implements GraphView, Printable {
    static final float DEFAULT_ANCHOR_SIZE = 9.0f;
    static final Paint DEFAULT_ANCHOR_SELECTED_PAINT = Color.red;
    static final Paint DEFAULT_ANCHOR_UNSELECTED_PAINT = Color.black;
	public enum Canvas { BACKGROUND_CANVAS, NETWORK_CANVAS, FOREGROUND_CANVAS };
    final Object m_lock = new Object();
    final float[] m_extentsBuff = new float[4];
    final GeneralPath m_path = new GeneralPath();
    GraphPerspective m_perspective;

    // Throughout this code I am assuming that nodes or edges are never
    // removed from the underlying RootGraph.  This assumption was made in the
    // old GraphView implementation.  Removal from the RootGraph is the only
    // thing that can affect m_drawPersp and m_structPersp that is beyond our
    // control.
    GraphPerspective m_drawPersp; // Visible graph.
    GraphPerspective m_structPersp; // Graph of all views (even hidden ones).
    MutableSpacialIndex2D m_spacial;
    MutableSpacialIndex2D m_spacialA;
    DNodeDetails m_nodeDetails;
    DEdgeDetails m_edgeDetails;
    PrintLOD m_printLOD;
    HashMap m_nodeViewMap;
    HashMap m_edgeViewMap;
    String m_identifier;
    final float m_defaultNodeXMin;
    final float m_defaultNodeYMin;
    final float m_defaultNodeXMax;
    final float m_defaultNodeYMax;
    InnerCanvas m_networkCanvas;
	ArbitraryGraphicsCanvas m_backgroundCanvas, m_foregroundCanvas;
    boolean m_nodeSelection = true;
    boolean m_edgeSelection = true;
    final IntBTree m_selectedNodes; // Positive.
    final IntBTree m_selectedEdges; // Positive.
    final IntBTree m_selectedAnchors;
    boolean m_contentChanged = false;
    boolean m_viewportChanged = false;
    final GraphViewChangeListener[] m_lis = new GraphViewChangeListener[1];
    final ContentChangeListener[] m_cLis = new ContentChangeListener[1];
    final ViewportChangeListener[] m_vLis = new ViewportChangeListener[1];

    /**
     * Creates a new DGraphView object.
     *
     * @param perspective DOCUMENT ME!
     */
    public DGraphView(GraphPerspective perspective) {
        m_perspective = perspective;
        m_drawPersp = m_perspective.getRootGraph()
                                   .createGraphPerspective((int[]) null,
                (int[]) null);
        m_structPersp = m_perspective.getRootGraph()
                                     .createGraphPerspective((int[]) null,
                (int[]) null);
        m_spacial = new RTree();
        m_spacialA = new RTree();
        m_nodeDetails = new DNodeDetails(this);
        m_edgeDetails = new DEdgeDetails(this);
        m_nodeViewMap = new HashMap();
        m_edgeViewMap = new HashMap();
        m_printLOD = new PrintLOD(); 
        m_defaultNodeXMin = 0.0f;
        m_defaultNodeYMin = 0.0f;
        m_defaultNodeXMax = m_defaultNodeXMin + DNodeView.DEFAULT_WIDTH;
        m_defaultNodeYMax = m_defaultNodeYMin + DNodeView.DEFAULT_HEIGHT;
        m_networkCanvas = new InnerCanvas(m_lock, this);
		m_backgroundCanvas = new ArbitraryGraphicsCanvas(m_perspective, this, m_networkCanvas, Color.white, true, true);
		m_foregroundCanvas = new ArbitraryGraphicsCanvas(m_perspective, this, m_networkCanvas, Color.white, true, false);
        m_selectedNodes = new IntBTree();
        m_selectedEdges = new IntBTree();
        m_selectedAnchors = new IntBTree();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GraphPerspective getGraphPerspective() {
        return m_perspective;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean nodeSelectionEnabled() {
        return m_nodeSelection;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean edgeSelectionEnabled() {
        return m_edgeSelection;
    }

    /**
     * DOCUMENT ME!
     */
    public void enableNodeSelection() {
        synchronized (m_lock) {
            m_nodeSelection = true;
        }
    }

    /**
     * DOCUMENT ME!
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

            if (listener != null)
                listener.graphViewChanged(
                    new GraphViewNodesUnselectedEvent(this, unselectedNodes));

            // Update the view after listener events are fired because listeners
            // may change something in the graph.
            updateView();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void enableEdgeSelection() {
        synchronized (m_lock) {
            m_edgeSelection = true;
        }
    }

    /**
     * DOCUMENT ME!
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

            if (listener != null)
                listener.graphViewChanged(
                    new GraphViewEdgesUnselectedEvent(this, unselectedEdges));

            // Update the view after listener events are fired because listeners
            // may change something in the graph.
            updateView();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getSelectedNodeIndices() {
        synchronized (m_lock) {
            final IntEnumerator elms = m_selectedNodes.searchRange(Integer.MIN_VALUE,
                    Integer.MAX_VALUE, false);
            final int[] returnThis = new int[elms.numRemaining()];

            for (int i = 0; i < returnThis.length; i++)
                returnThis[i] = ~elms.nextInt();

            return returnThis;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getSelectedNodes() {
        synchronized (m_lock) {
            final IntEnumerator elms = m_selectedNodes.searchRange(Integer.MIN_VALUE,
                    Integer.MAX_VALUE, false);
            final ArrayList returnThis = new ArrayList();

            while (elms.numRemaining() > 0)
                returnThis.add(m_nodeViewMap.get(new Integer(~elms.nextInt())));

            return returnThis;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getSelectedEdges() {
        synchronized (m_lock) {
            final IntEnumerator elms = m_selectedEdges.searchRange(Integer.MIN_VALUE,
                    Integer.MAX_VALUE, false);
            final ArrayList returnThis = new ArrayList();

            while (elms.numRemaining() > 0)
                returnThis.add(m_edgeViewMap.get(new Integer(~elms.nextInt())));

            return returnThis;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void addGraphViewChangeListener(GraphViewChangeListener l) {
        m_lis[0] = GraphViewChangeListenerChain.add(m_lis[0], l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void removeGraphViewChangeListener(GraphViewChangeListener l) {
        m_lis[0] = GraphViewChangeListenerChain.remove(m_lis[0], l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param paint DOCUMENT ME!
     */
    public void setBackgroundPaint(Paint paint) {
        synchronized (m_lock) {
			if (paint instanceof Color) {
				m_networkCanvas.setBackground((Color)paint);
				m_contentChanged = true;
			}
			else {
				System.out.println("DGraphView.setBackgroundPaint(), Color not found!");
			}
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Paint getBackgroundPaint() {
		return m_networkCanvas.getBackground();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Component getComponent() {
        return m_networkCanvas;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeView addNodeView(int nodeInx) {
        NodeView newView = null;

        synchronized (m_lock) {
            newView = addNodeViewInternal(nodeInx);

            if (newView == null)
                return (NodeView) m_nodeViewMap.get(new Integer(nodeInx));

            m_contentChanged = true;
        }

        final GraphViewChangeListener listener = m_lis[0];

        if (listener != null)
            listener.graphViewChanged(
                new GraphViewNodesRestoredEvent(
                    this,
                    new int[] { newView.getRootGraphIndex() }));

        return newView;
    }

    // Should synchronize around m_lock.
    private NodeView addNodeViewInternal(int nodeInx) {
        final NodeView oldView = (NodeView) m_nodeViewMap.get(
                new Integer(nodeInx));

        if (oldView != null)
            return null;

        if (m_drawPersp.restoreNode(nodeInx) == 0) {
            if (m_drawPersp.getNode(nodeInx) != null)
                throw new IllegalStateException(
                    "something weird is going on - node already existed in graph " +
                    "but a view for it did not exist (debug)");

            throw new IllegalArgumentException(
                "node index specified does not exist in underlying RootGraph");
        }

        m_structPersp.restoreNode(nodeInx);

        final NodeView newView;
        newView = new DNodeView(this, nodeInx);
        m_nodeViewMap.put(
            new Integer(nodeInx),
            newView);
        m_spacial.insert(~nodeInx, m_defaultNodeXMin, m_defaultNodeYMin,
            m_defaultNodeXMax, m_defaultNodeYMax);

        return newView;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public EdgeView addEdgeView(int edgeInx) {
        NodeView sourceNode = null;
        NodeView targetNode = null;
        EdgeView edgeView = null;

        synchronized (m_lock) {
            final EdgeView oldView = (EdgeView) m_edgeViewMap.get(
                    new Integer(edgeInx));

            if (oldView != null)
                return oldView;

            final Edge edge = m_drawPersp.getRootGraph()
                                         .getEdge(edgeInx);

            if (edge == null)
                throw new IllegalArgumentException(
                    "edge index specified does not exist in underlying RootGraph");

            sourceNode = addNodeViewInternal(
                    edge.getSource().getRootGraphIndex());
            targetNode = addNodeViewInternal(
                    edge.getTarget().getRootGraphIndex());

            if (m_drawPersp.restoreEdge(edgeInx) == 0) {
                if (m_drawPersp.getEdge(edgeInx) != null)
                    throw new IllegalStateException(
                        "something weird is going on - edge already existed in graph " +
                        "but a view for it did not exist (debug)");

                throw new IllegalArgumentException(
                    "edge index specified does not exist in underlying RootGraph");
            }

            m_structPersp.restoreEdge(edgeInx);
            edgeView = new DEdgeView(this, edgeInx);
            m_edgeViewMap.put(
                new Integer(edgeInx),
                edgeView);
            m_contentChanged = true;
        }

        // Under no circumstances should we be holding m_lock when the listener
        // events are fired.
        final GraphViewChangeListener listener = m_lis[0];

        if (listener != null) {
            if ((sourceNode != null) || (targetNode != null)) {
                int[] nodeInx;

                if (sourceNode == null)
                    nodeInx = new int[] { targetNode.getRootGraphIndex() };
                else if (targetNode == null)
                    nodeInx = new int[] { sourceNode.getRootGraphIndex() };
                else
                    nodeInx = new int[] {
                            sourceNode.getRootGraphIndex(),
                            targetNode.getRootGraphIndex()
                        };

                listener.graphViewChanged(
                    new GraphViewNodesRestoredEvent(this, nodeInx));
            }

            listener.graphViewChanged(
                new GraphViewEdgesRestoredEvent(
                    this,
                    new int[] { edgeView.getRootGraphIndex() }));
        }

        return edgeView;
    }

    /**
     * DOCUMENT ME!
     *
     * @param className DOCUMENT ME!
     * @param edgeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public EdgeView addEdgeView(String className, int edgeInx) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * DOCUMENT ME!
     *
     * @param className DOCUMENT ME!
     * @param nodeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeView addNodeView(String className, int nodeInx) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param replacement DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeView addNodeView(int nodeInx, NodeView replacement) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeView DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeView removeNodeView(NodeView nodeView) {
        return removeNodeView(nodeView.getRootGraphIndex());
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeView removeNodeView(Node node) {
        return removeNodeView(node.getRootGraphIndex());
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeView removeNodeView(int nodeInx) {
        final int[] hiddenEdgeInx;
        final DNodeView returnThis;

        synchronized (m_lock) {
            // We have to query edges in the m_structPersp, not m_drawPersp because
            // what if the node is hidden?
            hiddenEdgeInx = m_structPersp.getAdjacentEdgeIndicesArray(nodeInx,
                    true, true, true);

            if (hiddenEdgeInx == null)
                return null;

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

            //       m_selectedNodes.delete(~nodeInx);
            returnThis.m_view = null;
            m_contentChanged = true;
        }

        final GraphViewChangeListener listener = m_lis[0];

        if (listener != null) {
            if (hiddenEdgeInx.length > 0)
                listener.graphViewChanged(
                    new GraphViewEdgesHiddenEvent(this, hiddenEdgeInx));

            listener.graphViewChanged(
                new GraphViewNodesHiddenEvent(
                    this,
                    new int[] { returnThis.getRootGraphIndex() }));
        }

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeView DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public EdgeView removeEdgeView(EdgeView edgeView) {
        return removeEdgeView(edgeView.getRootGraphIndex());
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public EdgeView removeEdgeView(Edge edge) {
        return removeEdgeView(edge.getRootGraphIndex());
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public EdgeView removeEdgeView(int edgeInx) {
        final DEdgeView returnThis;

        synchronized (m_lock) {
            returnThis = removeEdgeViewInternal(edgeInx);

            if (returnThis != null)
                m_contentChanged = true;
        }

        if (returnThis != null) {
            final GraphViewChangeListener listener = m_lis[0];

            if (listener != null)
                listener.graphViewChanged(
                    new GraphViewEdgesHiddenEvent(
                        this,
                        new int[] { returnThis.getRootGraphIndex() }));
        }

        return returnThis;
    }

    // Should synchronize around m_lock.
    private DEdgeView removeEdgeViewInternal(int edgeInx) {
        final DEdgeView returnThis = (DEdgeView) m_edgeViewMap.remove(
                new Integer(edgeInx));

        if (returnThis == null)
            return returnThis;

        returnThis.unselectInternal();

        // If this edge view was hidden, it won't be in m_drawPersp.
        m_drawPersp.hideEdge(edgeInx);
        m_structPersp.hideEdge(edgeInx);
        m_edgeDetails.unregisterEdge(~edgeInx);

        //     m_selectedEdges.delete(~edgeInx);
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
     * @param id DOCUMENT ME!
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
     * @param zoom DOCUMENT ME!
     */
    public void setZoom(double zoom) {
        synchronized (m_lock) {
            m_networkCanvas.m_scaleFactor = zoom;
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
                             .numRemaining() == 0)
                return;

            m_networkCanvas.m_xCenter = (((double) m_extentsBuff[0]) +
                ((double) m_extentsBuff[2])) / 2.0d;
            m_networkCanvas.m_yCenter = (((double) m_extentsBuff[1]) +
                ((double) m_extentsBuff[3])) / 2.0d;
            m_networkCanvas.m_scaleFactor = Math.min(((double) m_networkCanvas.getWidth()) / (((double) m_extentsBuff[2]) -
                    ((double) m_extentsBuff[0])),
                    ((double) m_networkCanvas.getHeight()) / (((double) m_extentsBuff[3]) -
                    ((double) m_extentsBuff[1])));
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
     * Returns an iterator of all node views, including those that are
     * currently hidden.
     */
    public Iterator getNodeViewsIterator() {
        synchronized (m_lock) {
            return m_nodeViewMap.values()
                                .iterator();
        }
    }

    /*
     * Returns the count of all node views, including those that are currently
     * hidden.
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
    public int getEdgeViewCount() {
        synchronized (m_lock) {
            return m_edgeViewMap.size();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeView getNodeView(Node node) {
        return getNodeView(node.getRootGraphIndex());
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
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
    public List getEdgeViewsList() {
        synchronized (m_lock) {
            final ArrayList returnThis = new ArrayList(m_edgeViewMap.size());
            final Iterator values = m_edgeViewMap.values()
                                                 .iterator();

            while (values.hasNext())
                returnThis.add(values.next());

            return returnThis;
        }
    }

    /*
     * Returns all edge views (including the hidden ones) that are either 1.
     * directed, having oneNode as source and otherNode as target or 2.
     * undirected, having oneNode and otherNode as endpoints.  Note that
     * this behaviour is similar to that of
     * GraphPerspective.edgesList(Node, Node).
     */
    public List getEdgeViewsList(Node oneNode, Node otherNode) {
        synchronized (m_lock) {
            List edges = m_structPersp.edgesList(
                    oneNode.getRootGraphIndex(),
                    otherNode.getRootGraphIndex(),
                    true);

            if (edges == null)
                return null;

            final ArrayList returnThis = new ArrayList();
            Iterator it = edges.iterator();

            while (it.hasNext()) {
                Edge e = (Edge) it.next();
                returnThis.add(getEdgeView(e));
            }

            return returnThis;
        }
    }

    /*
     * Similar to getEdgeViewsList(Node, Node), only that one has control
     * of whether or not to include undirected edges.
     */
    public List getEdgeViewsList(int oneNodeInx, int otherNodeInx,
        boolean includeUndirected) {
        synchronized (m_lock) {
            List edges = m_structPersp.edgesList(oneNodeInx, otherNodeInx,
                    includeUndirected);

            if (edges == null)
                return null;

            final ArrayList returnThis = new ArrayList();
            Iterator it = edges.iterator();

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
    public EdgeView getEdgeView(int edgeInx) {
        synchronized (m_lock) {
            return (EdgeView) m_edgeViewMap.get(new Integer(edgeInx));
        }
    }

    /*
     * Returns an iterator of all edge views, including those that are
     * currently hidden.
     */
    public Iterator getEdgeViewsIterator() {
        synchronized (m_lock) {
            return m_edgeViewMap.values()
                                .iterator();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public EdgeView getEdgeView(Edge edge) {
        return getEdgeView(edge.getRootGraphIndex());
    }

    /*
     * Alias to getEdgeViewCount().
     */
    public int edgeCount() {
        return getEdgeViewCount();
    }

    /*
     * Alias to getNodeViewCount().
     */
    public int nodeCount() {
        return getNodeViewCount();
    }

    /*
     * obj should be either a DEdgeView or a DNodeView.
     */
    public boolean hideGraphObject(Object obj) {
        return hideGraphObjectInternal(obj, true);
    }

    private boolean hideGraphObjectInternal(Object obj,
        boolean fireListenerEvents) {
        if (obj instanceof DEdgeView) {
            int edgeInx;

            synchronized (m_lock) {
                edgeInx = ((DEdgeView) obj).getRootGraphIndex();

                if (m_drawPersp.hideEdge(edgeInx) == 0)
                    return false;

                ((DEdgeView) obj).unselectInternal();
                m_contentChanged = true;
            }

            if (fireListenerEvents) {
                final GraphViewChangeListener listener = m_lis[0];

                if (listener != null)
                    listener.graphViewChanged(
                        new GraphViewEdgesHiddenEvent(
                            this,
                            new int[] { edgeInx }));
            }

            return true;
        } else if (obj instanceof DNodeView) {
            int[] edges;
            int nodeInx;

            synchronized (m_lock) {
                final DNodeView nView = (DNodeView) obj;
                nodeInx = nView.getRootGraphIndex();
                edges = m_drawPersp.getAdjacentEdgeIndicesArray(nodeInx, true,
                        true, true);

                if (edges == null)
                    return false;

                for (int i = 0; i < edges.length; i++)
                    hideGraphObjectInternal(
                        m_edgeViewMap.get(new Integer(edges[i])),
                        false);

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
                    if (edges.length > 0)
                        listener.graphViewChanged(
                            new GraphViewEdgesHiddenEvent(this, edges));

                    listener.graphViewChanged(
                        new GraphViewNodesHiddenEvent(
                            this,
                            new int[] { nodeInx }));
                }
            }

            return true;
        } else
            return false;
    }

    /*
     * obj should be either a DEdgeView or a DNodeView.
     */
    public boolean showGraphObject(Object obj) {
        return showGraphObjectInternal(obj, true);
    }

    private boolean showGraphObjectInternal(Object obj,
        boolean fireListenerEvents) {
        if (obj instanceof DNodeView) {
            int nodeInx;

            synchronized (m_lock) {
                final DNodeView nView = (DNodeView) obj;
                nodeInx = nView.getRootGraphIndex();

                if (m_structPersp.getNode(nodeInx) == null)
                    return false;

                if (m_drawPersp.restoreNode(nodeInx) == 0)
                    return false;

                m_spacial.insert(~nodeInx, nView.m_hiddenXMin,
                    nView.m_hiddenYMin, nView.m_hiddenXMax, nView.m_hiddenYMax);
                m_contentChanged = true;
            }

            if (fireListenerEvents) {
                final GraphViewChangeListener listener = m_lis[0];

                if (listener != null)
                    listener.graphViewChanged(
                        new GraphViewNodesRestoredEvent(
                            this,
                            new int[] { nodeInx }));
            }

            return true;
        } else if (obj instanceof DEdgeView) {
            int sourceNode = 0;
            int targetNode = 0;
            int newEdge = 0;

            synchronized (m_lock) {
                final Edge edge = m_structPersp.getEdge(
                        ((DEdgeView) obj).getRootGraphIndex());

                if (edge == null)
                    return false;

                // The edge exists in m_structPersp, therefore its source and target
                // node views must also exist.
                sourceNode = edge.getSource()
                                 .getRootGraphIndex();

                if (!showGraphObjectInternal(
                            getNodeView(sourceNode),
                            false))
                    sourceNode = 0;

                targetNode = edge.getTarget()
                                 .getRootGraphIndex();

                if (!showGraphObjectInternal(
                            getNodeView(targetNode),
                            false))
                    targetNode = 0;

                newEdge = edge.getRootGraphIndex();

                if (m_drawPersp.restoreEdge(newEdge) == 0)
                    return false;

                m_contentChanged = true;
            }

            if (fireListenerEvents) {
                final GraphViewChangeListener listener = m_lis[0];

                if (listener != null) {
                    if (sourceNode != 0)
                        listener.graphViewChanged(
                            new GraphViewNodesRestoredEvent(
                                this,
                                new int[] { sourceNode }));

                    if (targetNode != 0)
                        listener.graphViewChanged(
                            new GraphViewNodesRestoredEvent(
                                this,
                                new int[] { targetNode }));

                    listener.graphViewChanged(
                        new GraphViewEdgesRestoredEvent(
                            this,
                            new int[] { newEdge }));
                }
            }

            return true;
        } else
            return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param objects DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean hideGraphObjects(List objects) {
        final Iterator it = objects.iterator();

        while (it.hasNext())
            hideGraphObject(it.next());

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param objects DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean showGraphObjects(List objects) {
        final Iterator it = objects.iterator();

        while (it.hasNext())
            showGraphObject(it.next());

        return true;
    }

    // AJK: 04/25/06 BEGIN
    //     for context menu
    public Object[] getContextMethods(String className, boolean plusSuperclass) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param className DOCUMENT ME!
     * @param methods DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] getContextMethods(String className, Object[] methods) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param className DOCUMENT ME!
     * @param methodClassName DOCUMENT ME!
     * @param methodName DOCUMENT ME!
     * @param args DOCUMENT ME!
     * @param loader DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean addContextMethod(String className, String methodClassName,
        String methodName, Object[] args, ClassLoader loader) {
        return false;
    }

    // AJK: 04/25/06 END
    public void setAllNodePropertyData(int nodeInx, Object[] data) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] getAllNodePropertyData(int nodeInx) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param data DOCUMENT ME!
     */
    public void setAllEdgePropertyData(int edgeInx, Object[] data) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] getAllEdgePropertyData(int edgeInx) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getNodeObjectProperty(int nodeInx, int property) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setNodeObjectProperty(int nodeInx, int property, Object value) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getEdgeObjectProperty(int edgeInx, int property) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setEdgeObjectProperty(int edgeInx, int property, Object value) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public double getNodeDoubleProperty(int nodeInx, int property) {
        return 0.0d;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param val DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setNodeDoubleProperty(int nodeInx, int property, double val) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public double getEdgeDoubleProperty(int edgeInx, int property) {
        return 0.0d;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param val DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setEdgeDoubleProperty(int edgeInx, int property, double val) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float getNodeFloatProperty(int nodeInx, int property) {
        return 0.0f;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setNodeFloatProperty(int nodeInx, int property, float value) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float getEdgeFloatProperty(int edgeInx, int property) {
        return 0.0f;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setEdgeFloatProperty(int edgeInx, int property, float value) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getNodeBooleanProperty(int nodeInx, int property) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param val DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setNodeBooleanProperty(int nodeInx, int property, boolean val) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getEdgeBooleanProperty(int edgeInx, int property) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param val DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setEdgeBooleanProperty(int edgeInx, int property, boolean val) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getNodeIntProperty(int nodeInx, int property) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setNodeIntProperty(int nodeInx, int property, int value) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getEdgeIntProperty(int edgeInx, int property) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeInx DOCUMENT ME!
     * @param property DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setEdgeIntProperty(int edgeInx, int property, int value) {
        return false;
    }

    // Auxillary methods specific to this GraphView implementation:
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

            if (selectedElms.numRemaining() == 0)
                return;

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
            m_networkCanvas.m_scaleFactor = Math.min(((double) m_networkCanvas.getWidth()) / (((double) xMax) -
                    ((double) xMin)),
                    ((double) m_networkCanvas.getHeight()) / (((double) yMax) -
                    ((double) yMin)));
            m_viewportChanged = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param lod DOCUMENT ME!
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
     * @param textAsShape DOCUMENT ME!
     */
    public void setPrintingTextAsShape(boolean textAsShape) {
        synchronized (m_lock) {
        	m_printLOD.setPrintingTextAsShape(textAsShape);
        }
    }


    /**
     * Efficiently computes the set of nodes intersecting an axis-aligned
     * query rectangle; the query rectangle is specified in the node coordinate
     * system, not the component coordinate system.<p>
     * NOTE: The order of elements placed on the stack follows the rendering
     * order of nodes; the element waiting to be popped off the stack is the
     * node that is rendered last, and thus is "on top of" other nodes
     * potentially beneath it.<p>
     * HINT: To perform a point query simply set xMin equal to xMax and yMin
     * equal to yMax.
     * @param xMinimum a boundary of the query rectangle: the minimum X
     *   coordinate.
     * @param yMinimum a boundary of the query rectangle: the minimum Y
     *   coordinate.
     * @param xMaximum a boundary of the query rectangle: the maximum X
     *   coordinate.
     * @param yMaximum a boundary of the query rectangle: the maximum Y
     *   coordinate.
     * @param treatNodeShapesAsRectangle if true, nodes are treated as rectangles
     *   for purposes of the query computation; if false, true node shapes are
     *   respected, at the expense of slowing down the query by a constant
     *   factor.
     * @param returnVal RootGraph indices of nodes intersecting the query
     *   rectangle will be placed onto this stack; the stack is not emptied by
     *   this method initially.
     */
    public void getNodesIntersectingRectangle(double xMinimum, double yMinimum,
        double xMaximum, double yMaximum, boolean treatNodeShapesAsRectangle,
        IntStack returnVal) {
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
                    returnVal.push(~under.nextInt());
            } else {
                final double x = xMin;
                final double y = yMin;
                final double w = ((double) xMax) - xMin;
                final double h = ((double) yMax) - yMin;

                for (int i = 0; i < totalHits; i++) {
                    final int node = under.nextExtents(m_extentsBuff, 0);

                    // The only way that the node can miss the intersection query is
                    // if it intersects one of the four query rectangle's corners.
                    if (((m_extentsBuff[0] < xMin) &&
                            (m_extentsBuff[1] < yMin)) ||
                            ((m_extentsBuff[0] < xMin) &&
                            (m_extentsBuff[3] > yMax)) ||
                            ((m_extentsBuff[2] > xMax) &&
                            (m_extentsBuff[3] > yMax)) ||
                            ((m_extentsBuff[2] > xMax) &&
                            (m_extentsBuff[1] < yMin))) {
                        m_networkCanvas.m_grafx.getNodeShape(
                            m_nodeDetails.shape(node),
                            m_extentsBuff[0],
                            m_extentsBuff[1],
                            m_extentsBuff[2],
                            m_extentsBuff[3],
                            m_path);

                        if ((w > 0) && (h > 0)) {
                            if (m_path.intersects(x, y, w, h))
                                returnVal.push(~node);
                        } else {
                            if (m_path.contains(x, y))
                                returnVal.push(~node);
                        }
                    } else
                        returnVal.push(~node);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param xMin DOCUMENT ME!
     * @param yMin DOCUMENT ME!
     * @param xMax DOCUMENT ME!
     * @param yMax DOCUMENT ME!
     * @param returnVal DOCUMENT ME!
     */
    public void queryDrawnEdges(int xMin, int yMin, int xMax, int yMax,
        IntStack returnVal) {
        synchronized (m_lock) {
            m_networkCanvas.computeEdgesIntersecting(xMin, yMin, xMax, yMax, returnVal);
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
                             .numRemaining() == 0)
                return false;

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
     * @param coords DOCUMENT ME!
     */
    public void xformComponentToNodeCoords(double[] coords) {
        synchronized (m_lock) {
            m_networkCanvas.m_grafx.xformImageToNodeCoords(coords);
        }
    }

    private final IntHash m_hash = new IntHash();

    /**
     * DOCUMENT ME!
     *
     * @param img DOCUMENT ME!
     * @param lod DOCUMENT ME!
     * @param bgPaint DOCUMENT ME!
     * @param xCenter DOCUMENT ME!
     * @param yCenter DOCUMENT ME!
     * @param scaleFactor DOCUMENT ME!
     */
    public void drawSnapshot(Image img, GraphLOD lod, Paint bgPaint,
        double xCenter, double yCenter, double scaleFactor) {
        synchronized (m_lock) {
            GraphRenderer.renderGraph(
                (FixedGraph) m_drawPersp,
                m_spacial,
                lod,
                m_nodeDetails,
                m_edgeDetails,
                m_hash,
                new GraphGraphics(img, false),
                bgPaint,
                xCenter,
                yCenter,
                scaleFactor);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void addContentChangeListener(ContentChangeListener l) {
        m_cLis[0] = ContentChangeListenerChain.add(m_cLis[0], l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void removeContentChangeListener(ContentChangeListener l) {
        m_cLis[0] = ContentChangeListenerChain.remove(m_cLis[0], l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void addViewportChangeListener(ViewportChangeListener l) {
        m_vLis[0] = ViewportChangeListenerChain.add(m_vLis[0], l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void removeViewportChangeListener(ViewportChangeListener l) {
        m_vLis[0] = ViewportChangeListenerChain.remove(m_vLis[0], l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param pageFormat DOCUMENT ME!
     * @param page DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int print(Graphics g, PageFormat pageFormat, int page) {
        if (page == 0) {
        	((Graphics2D) g).translate(
                pageFormat.getImageableX(),
                pageFormat.getImageableY());

        	// make sure the whole image on the screen will fit to the printable area of the paper
            double image_scale = Math.min(pageFormat.getImageableWidth()/getComponent().getWidth(),
            		pageFormat.getImageableHeight()/getComponent().getHeight());            
            if (image_scale < 1.0d) {
                ((Graphics2D) g).scale(image_scale, image_scale);
            }

        	g.clipRect(
                0,
                0,
                getComponent().getWidth(),
                getComponent().getHeight());
            
            getComponent()
                .print(g);
            return PAGE_EXISTS;
        } else
            return NO_SUCH_PAGE;
    }

    // AJK: 04/02/06 BEGIN
	public InnerCanvas getCanvas() {
		return m_networkCanvas;
	}
    public DingCanvas getCanvas(Canvas canvasId) {
		if (canvasId == Canvas.BACKGROUND_CANVAS) {
			return m_backgroundCanvas;
		}
		else if (canvasId == Canvas.NETWORK_CANVAS) {
			return m_networkCanvas;
		}
		else if (canvasId == Canvas.FOREGROUND_CANVAS) {
			return m_foregroundCanvas;
		}
		// made it here
		return null;
    }

    /**
     * utility that returns the nodeView that is located at input point
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
        getNodesIntersectingRectangle((float) locn[0], (float) locn[1],
            (float) locn[0], (float) locn[1],
            (m_networkCanvas.getLastRenderDetail() & GraphRenderer.LOD_HIGH_DETAIL) == 0,
            nodeStack);

        chosenNode = (nodeStack.size() > 0) ? nodeStack.peek() : 0;

        if (chosenNode != 0)
            nv = getNodeView(chosenNode);

        return nv;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pt DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public EdgeView getPickedEdgeView(Point2D pt) {
        EdgeView ev = null;
        final IntStack edgeStack = new IntStack();
        queryDrawnEdges((int) pt.getX(), (int) pt.getY(), (int) pt.getX(),
            (int) pt.getY(), edgeStack);

        int chosenEdge = 0;
        chosenEdge = (edgeStack.size() > 0) ? edgeStack.peek() : 0;

        if (chosenEdge != 0)
            ev = getEdgeView(chosenEdge);

        return ev;
    }

    // AJK: 04/25/06 END
    // AJK: 04/27/06 BEGIN
    //   for context menus
    public void addNodeContextMenuListener(NodeContextMenuListener l) {
        //System.out.println("Adding NodeContextListener: " + l);
        getCanvas()
            .addNodeContextMenuListener(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void removeNodeContextMenuListener(NodeContextMenuListener l) {
        getCanvas()
            .removeNodeContextMenuListener(l);
    }

    // AJK: 04/27/06 END
    public void addEdgeContextMenuListener(EdgeContextMenuListener l) {
        //System.out.println("Adding EdgeContextListener: " + l);
        getCanvas()
            .addEdgeContextMenuListener(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void removeEdgeContextMenuListener(EdgeContextMenuListener l) {
        getCanvas()
            .removeEdgeContextMenuListener(l);
    }

    final float[] m_anchorsBuff = new float[2];

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

    // Some things for caching texture paint.  The texture paint is computed
    // from m_lastDash and m_lastPaint.
    int m_lastSize = 0;
    Paint m_lastPaint = null;
    Paint m_lastTexturePaint = null;
}
