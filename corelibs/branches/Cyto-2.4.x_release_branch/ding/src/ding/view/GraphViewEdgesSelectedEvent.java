package ding.view;

import giny.model.Edge;

import giny.view.GraphView;


final class GraphViewEdgesSelectedEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_selectedEdgeInx;

    GraphViewEdgesSelectedEvent(GraphView view, int[] selectedEdgeInx) {
        super(view);
        m_view = view;
        m_selectedEdgeInx = selectedEdgeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return EDGES_SELECTED_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Edge[] getSelectedEdges() {
        final Edge[] returnThis = new Edge[m_selectedEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getEdge(m_selectedEdgeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getSelectedEdgeIndices() {
        final int[] returnThis = new int[m_selectedEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_selectedEdgeInx[i];

        return returnThis;
    }
}
