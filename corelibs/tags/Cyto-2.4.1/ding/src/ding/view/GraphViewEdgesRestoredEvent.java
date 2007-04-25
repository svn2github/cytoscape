package ding.view;

import giny.model.Edge;

import giny.view.GraphView;


final class GraphViewEdgesRestoredEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_restoredEdgeInx;

    GraphViewEdgesRestoredEvent(GraphView view, int[] restoredEdgeInx) {
        super(view);
        m_view = view;
        m_restoredEdgeInx = restoredEdgeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return EDGES_RESTORED_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Edge[] getRestoredEdges() {
        final Edge[] returnThis = new Edge[m_restoredEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getEdge(m_restoredEdgeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getRestoredEdgeIndices() {
        final int[] returnThis = new int[m_restoredEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_restoredEdgeInx[i];

        return returnThis;
    }
}
