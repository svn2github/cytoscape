package ding.view;

/**
 * DOCUMENT ME!
 *
 * @author $author$-
 */
public interface ViewportChangeListener {
    /**
     * This gets fired upon graph redraw when zoom is changed or the graph
     * center is changed or the view component is resized.
     */
    public void viewportChanged(int viewportWidth, int viewportHeight,
        double newXCenter, double newYCenter, double newScaleFactor);
}
