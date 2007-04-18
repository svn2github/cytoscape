package cytoscape.visual.ui;

import cytoscape.visual.VisualPropertyType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class IconPanel extends JPanel {
    private VisualPropertyType type;

    /**
     * Creates a new IconPanel object.
     *
     * @param type DOCUMENT ME!
     */
    public IconPanel(VisualPropertyType type) {
        this.type = type;
        this.setPreferredSize(new Dimension());
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void paintComponent(Graphics g) {
        clear(g);

        Graphics2D g2d = (Graphics2D) g;

        int strW = SwingUtilities.computeStringWidth(
                g2d.getFontMetrics(),
                type.getName());

        //this.setPreferredSize(new Dimension(strW + 6, 1));
        int panelHeight = this.getHeight() - 80;

        Polygon poly = new Polygon();
        int top = 10;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(1.0f));

        int center = this.getWidth() / 2;

        poly.addPoint(center, top);
        poly.addPoint(center - 6, top + 15);
        poly.addPoint(center, top + 15);
        g.fillPolygon(poly);

        g2d.drawLine(center, top, center, (panelHeight / 2) - 20);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));

        g2d.drawString(
            type.getName(),
            center - (strW / 2),
            (panelHeight / 2) + 5);
        //g2d.drawString("Width", center-15, 130);
        //g2d.drawImage(icon, center - 20, 110, this);
        g2d.setColor(Color.black);
        g2d.drawLine(center, (panelHeight / 2) + 20, center, panelHeight);
    }

    // super.paintComponent clears offscreen pixmap,
    // since we're using double buffering by default.
    protected void clear(Graphics g) {
        super.paintComponent(g);
    }
}
