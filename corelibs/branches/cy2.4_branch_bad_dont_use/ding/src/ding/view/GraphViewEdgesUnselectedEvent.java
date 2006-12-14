package ding.view;

import giny.model.Edge;

import giny.view.GraphView;


final class GraphViewEdgesUnselectedEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_unselectedEdgeInx;

    GraphViewEdgesUnselectedEvent(GraphView view, int[] unselectedEdgeInx) {
        super(view);
        m_view = view;
        m_unselectedEdgeInx = unselectedEdgeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return EDGES_UNSELECTED_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Edge[] getUnselectedEdges() {
        final Edge[] returnThis = new Edge[m_unselectedEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getEdge(m_unselectedEdgeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getUnselectedEdgeIndices() {
        final int[] returnThis = new int[m_unselectedEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_unselectedEdgeInx[i];

        return returnThis;
    }
}
