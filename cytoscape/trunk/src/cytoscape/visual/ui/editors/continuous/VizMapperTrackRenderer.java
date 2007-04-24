package cytoscape.visual.ui.editors.continuous;

import org.jdesktop.swingx.multislider.TrackRenderer;


/**
 * DOCUMENT ME!
 *
 * @author $author$-
  */
public interface VizMapperTrackRenderer extends TrackRenderer {
    /**
     * DOCUMENT ME!
     *
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getToolTipForCurrentLocation(int x, int y);

    /**
     * DOCUMENT ME!
     *
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getObjectInRange(int x, int y);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Double getSelectedThumbValue();
}
