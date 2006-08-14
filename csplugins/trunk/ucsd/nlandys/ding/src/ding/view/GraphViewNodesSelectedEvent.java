package ding.view;

import giny.model.Node;

import giny.view.GraphView;


final class GraphViewNodesSelectedEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_selectedNodeInx;

    GraphViewNodesSelectedEvent(GraphView view, int[] selectedNodeInx) {
        super(view);
        m_view = view;
        m_selectedNodeInx = selectedNodeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return NODES_SELECTED_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Node[] getSelectedNodes() {
        final Node[] returnThis = new Node[m_selectedNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getNode(m_selectedNodeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getSelectedNodeIndices() {
        final int[] returnThis = new int[m_selectedNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_selectedNodeInx[i];

        return returnThis;
    }
}
