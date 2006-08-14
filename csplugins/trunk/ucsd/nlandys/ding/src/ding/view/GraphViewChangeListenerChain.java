package ding.view;

import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;


class GraphViewChangeListenerChain
    implements GraphViewChangeListener {
    private final GraphViewChangeListener a;
    private final GraphViewChangeListener b;

    private GraphViewChangeListenerChain(GraphViewChangeListener a,
        GraphViewChangeListener b) {
        this.a = a;
        this.b = b;
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    public void graphViewChanged(GraphViewChangeEvent evt) {
        a.graphViewChanged(evt);
        b.graphViewChanged(evt);
    }

    static GraphViewChangeListener add(GraphViewChangeListener a,
        GraphViewChangeListener b) {
        if (a == null)
            return b;

        if (b == null)
            return a;

        return new GraphViewChangeListenerChain(a, b);
    }

    static GraphViewChangeListener remove(GraphViewChangeListener l,
        GraphViewChangeListener oldl) {
        if ((l == oldl) || (l == null))
            return null;
        else if (l instanceof GraphViewChangeListenerChain)
            return ((GraphViewChangeListenerChain) l).remove(oldl);
        else

            return l;
    }

    private GraphViewChangeListener remove(GraphViewChangeListener oldl) {
        if (oldl == a)
            return b;

        if (oldl == b)
            return a;

        GraphViewChangeListener a2 = remove(a, oldl);
        GraphViewChangeListener b2 = remove(b, oldl);

        if ((a2 == a) && (b2 == b))
            return this;

        return add(a2, b2);
    }
}
