package ding.view;

import giny.model.Edge;

import giny.view.GraphView;


final class GraphViewEdgesHiddenEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_hiddenEdgeInx;

    GraphViewEdgesHiddenEvent(GraphView view, int[] hiddenEdgeInx) {
        super(view);
        m_view = view;
        m_hiddenEdgeInx = hiddenEdgeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return EDGES_HIDDEN_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Edge[] getHiddenEdges() {
        final Edge[] returnThis = new Edge[m_hiddenEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getEdge(m_hiddenEdgeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getHiddenEdgeIndices() {
        final int[] returnThis = new int[m_hiddenEdgeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_hiddenEdgeInx[i];

        return returnThis;
    }
}
