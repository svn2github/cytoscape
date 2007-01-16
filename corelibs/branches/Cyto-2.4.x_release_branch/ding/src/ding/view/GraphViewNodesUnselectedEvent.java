package ding.view;

import giny.model.Node;

import giny.view.GraphView;


final class GraphViewNodesUnselectedEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_unselectedNodeInx;

    GraphViewNodesUnselectedEvent(GraphView view, int[] unselectedNodeInx) {
        super(view);
        m_view = view;
        m_unselectedNodeInx = unselectedNodeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return NODES_UNSELECTED_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Node[] getUnselectedNodes() {
        final Node[] returnThis = new Node[m_unselectedNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getNode(m_unselectedNodeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getUnselectedNodeIndices() {
        final int[] returnThis = new int[m_unselectedNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_unselectedNodeInx[i];

        return returnThis;
    }
}
