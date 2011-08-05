package cytoscape.visual.ui.editors.continuous;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.ThumbRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JComponent;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class TriangleThumbRenderer extends JComponent
    implements ThumbRenderer {
    private static final Color SELECTED_COLOR = Color.red;
    private static final Color DEFAULT_COLOR = Color.DARK_GRAY;
    private static final Color BACKGROUND_COLOR = Color.white;
    private JXMultiThumbSlider slider;
    private boolean selected;
    private boolean grabbed = false;
		private int index = -1;
		private int selectedIndex = -1;
		private JComponent selectedComponent = null;

    /**
     * Creates a new TriangleThumbRenderer object.
     *
     * @param slider DOCUMENT ME!
     */
    public TriangleThumbRenderer(JXMultiThumbSlider slider) {
        super();

        this.slider = slider;
        setBackground(BACKGROUND_COLOR);
    }

    protected void paintComponent(Graphics g) {
        /*
         * Enable anti-aliasing
         */
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        /*
         * Draw small triangle
         */
        if (selectedIndex == index) {
            final Polygon outline = new Polygon();
            outline.addPoint(0, 0);
            outline.addPoint(0, 5);
            outline.addPoint(5, 10);
            outline.addPoint(9, 5);
            outline.addPoint(9, 0);
            g.fillPolygon(outline);
            g.setColor(Color.blue);
            ((Graphics2D) g).setStroke(new BasicStroke(2.0f));
            g.drawPolygon(outline);
        } else {
            final Polygon thumb = new Polygon();

            thumb.addPoint(0, 0);
            thumb.addPoint(10, 0);
            thumb.addPoint(5, 10);
            g.fillPolygon(thumb);

            final Polygon outline = new Polygon();
            outline.addPoint(0, 0);
            outline.addPoint(9, 0);
            outline.addPoint(5, 9);
            g.setColor(Color.DARK_GRAY);
            ((Graphics2D) g).setStroke(new BasicStroke(1.0f));
            g.drawPolygon(outline);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param slider DOCUMENT ME!
     * @param index DOCUMENT ME!
     * @param selected DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JComponent getThumbRendererComponent(JXMultiThumbSlider slider, int index, boolean selected) {
        this.grabbed = selected;
				this.index = index;
				if (selected) {
					selectedIndex = index;
					selectedComponent = this;
				}

        final Object obj = slider.getModel()
                                 .getThumbAt(index)
                                 .getObject();

        if (obj.getClass() == Color.class)
            this.setForeground((Color) obj);
        else {
            if (grabbed)
                this.setForeground(SELECTED_COLOR);
            else
                this.setForeground(DEFAULT_COLOR);
        }

        return this;
    }

		/**
		 * Return the value for the currently selected index
		 *
		 * @return index of the currently selected thumb
		 */
		public int getSelectedIndex() { return this.selectedIndex; }
		public JComponent getSelectedThumb() { return this.selectedComponent; }

		/**
		 * Set the the currently selected index
		 *
		 * @param index the currently selected thumb
		 */
		public void setSelectedIndex(int index) { 
			this.selectedIndex = index; 
			this.selectedComponent = this;
		}
    
}
