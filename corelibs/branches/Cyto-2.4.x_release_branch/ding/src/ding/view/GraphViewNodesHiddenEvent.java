package ding.view;

import giny.model.Node;

import giny.view.GraphView;


final class GraphViewNodesHiddenEvent extends GraphViewChangeEventAdapter {
    private final GraphView m_view;
    private final int[] m_hiddenNodeInx;

    GraphViewNodesHiddenEvent(GraphView view, int[] hiddenNodeInx) {
        super(view);
        m_view = view;
        m_hiddenNodeInx = hiddenNodeInx;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int getType() {
        return NODES_HIDDEN_TYPE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Node[] getHiddenNodes() {
        final Node[] returnThis = new Node[m_hiddenNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_view.getRootGraph()
                                  .getNode(m_hiddenNodeInx[i]);

        return returnThis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final int[] getHiddenNodeIndices() {
        final int[] returnThis = new int[m_hiddenNodeInx.length];

        for (int i = 0; i < returnThis.length; i++)
            returnThis[i] = m_hiddenNodeInx[i];

        return returnThis;
    }
}
