package ding.view;

class ContentChangeListenerChain
    implements ContentChangeListener {
    private final ContentChangeListener a;
    private final ContentChangeListener b;

    private ContentChangeListenerChain(ContentChangeListener a,
        ContentChangeListener b) {
        this.a = a;
        this.b = b;
    }

    /**
     * DOCUMENT ME!
     */
    public void contentChanged() {
        a.contentChanged();
        b.contentChanged();
    }

    static ContentChangeListener add(ContentChangeListener a,
        ContentChangeListener b) {
        if (a == null)
            return b;

        if (b == null)
            return a;

        return new ContentChangeListenerChain(a, b);
    }

    static ContentChangeListener remove(ContentChangeListener l,
        ContentChangeListener oldl) {
        if ((l == oldl) || (l == null))
            return null;
        else if (l instanceof ContentChangeListenerChain)
            return ((ContentChangeListenerChain) l).remove(oldl);
        else

            return l;
    }

    private ContentChangeListener remove(ContentChangeListener oldl) {
        if (oldl == a)
            return b;

        if (oldl == b)
            return a;

        ContentChangeListener a2 = remove(a, oldl);
        ContentChangeListener b2 = remove(b, oldl);

        if ((a2 == a) && (b2 == b))
            return this;

        return add(a2, b2);
    }
}
