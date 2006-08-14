package ding.view;

import giny.model.Node;

import giny.view.GraphView;


final class GraphViewNodesRestoredEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_restoredNodeInx;

    GraphViewNodesRestoredEvent(GraphView view, int[] restoredNodeInx) {
        super(view);
        m_view = view;
        m_restoredNodeInx = restoredNodeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return NODES_RESTORED_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Node[] getRestoredNodes() {
        final Node[] returnThis = new Node[m_restoredNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getNode(m_restoredNodeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getRestoredNodeIndices() {
        final int[] returnThis = new int[m_restoredNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_restoredNodeInx[i];

        return returnThis;
    }
}
