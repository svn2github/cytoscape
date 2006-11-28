package ding.view;

import giny.model.Edge;
import giny.model.Node;

import giny.view.GraphView;
import giny.view.GraphViewChangeEvent;


abstract class GraphViewChangeEventAdapter extends GraphViewChangeEvent {
    GraphViewChangeEventAdapter(GraphView source) {
        super(source);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int getType();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isNodesRestoredType() {
        return (getType() & NODES_RESTORED_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isEdgesRestoredType() {
        return (getType() & EDGES_RESTORED_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isNodesHiddenType() {
        return (getType() & NODES_HIDDEN_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isEdgesHiddenType() {
        return (getType() & EDGES_HIDDEN_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isNodesSelectedType() {
        return (getType() & NODES_SELECTED_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isNodesUnselectedType() {
        return (getType() & NODES_UNSELECTED_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isEdgesSelectedType() {
        return (getType() & EDGES_SELECTED_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final boolean isEdgesUnselectedType() {
        return (getType() & EDGES_UNSELECTED_TYPE) != 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node[] getRestoredNodes() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Edge[] getRestoredEdges() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node[] getHiddenNodes() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Edge[] getHiddenEdges() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node[] getSelectedNodes() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node[] getUnselectedNodes() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Edge[] getSelectedEdges() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Edge[] getUnselectedEdges() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getRestoredNodeIndices() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getRestoredEdgeIndices() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getHiddenNodeIndices() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getHiddenEdgeIndices() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getSelectedNodeIndices() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getUnselectedNodeIndices() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getSelectedEdgeIndices() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getUnselectedEdgeIndices() {
        return null;
    }
}
