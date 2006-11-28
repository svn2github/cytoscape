package ding.view;

/**
 * DOCUMENT ME!
 *
 * @author $author$-
 */
public interface ContentChangeListener {
    /**
     * This gets fired upon graph redraw when at least one of the following
     * things change: node unselected, edge unselected, background paint
     * change, node view added, edge view added, node view removed,
     * edge view removed, node view hidden, edge view hidden, node view
     * restored, edge view restored, graph lod changed, node visual property
     * changed, edge visual property changed.
     */
    public void contentChanged();
}
