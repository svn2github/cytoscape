package cytoscape.visual.ui.icon;

import java.awt.Color;
import java.awt.Shape;

import javax.swing.ImageIcon;


/**
 *
 * Icon created from Shape object passed from rendering engine.<br>
 *
 * This icon is scalable (vector image).
 *
 * Actual paint method is defined in child classes.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public abstract class VisualPropertyIcon extends ImageIcon {
    // Default icon color.
    protected static final Color DEF_COLOR = Color.DARK_GRAY;
    protected int height;
    protected int width;
    protected Color color;
    protected Shape shape;
    protected String name;
    protected int pad = 0;

    /**
     * Constructor without Color parameter.
     *
     * @param shape
     * @param width
     * @param height
     * @param name
     */
    public VisualPropertyIcon(Shape shape, int width, int height, String name) {
        this(shape, width, height, name, DEF_COLOR);
    }

    /**
     * Constructor with full parameter set.
     *
     * @param shape
     * @param width
     * @param height
     * @param name
     * @param color
     */
    public VisualPropertyIcon(Shape shape, int width, int height, String name,
        Color color) {
        this.shape = shape;
        this.width = width;
        this.height = height;
        this.name = name;

        if (color != null)
            this.color = color;
        else
            this.color = DEF_COLOR;
    }

    /**
     * Get height of icon. This implements Icon interface.
     */
    public int getIconHeight() {
        return height;
    }

    /**
     * Get width of icon. This implements Icon interface.
     */
    public int getIconWidth() {
        return width;
    }

    /**
     * Set width.
     *
     * @param width
     *            Width of icon
     */
    public void setIconWidth(int width) {
        this.width = width;
    }

    /**
     * Set height.
     *
     * @param height
     *            Height of icon
     */
    public void setIconHeight(int height) {
        this.height = height;
    }

    /**
     * Get human-readable name of this icon.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set human-readable name of this icon.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get color of icon
     *
     * @return Icon color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set icon color.
     *
     * @param color
     *            Icon color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
    * Insert space on the left.
    *
    * @param pad DOCUMENT ME!
    */
    public void setLeftPadding(int pad) {
        this.pad = pad;
    }
}
