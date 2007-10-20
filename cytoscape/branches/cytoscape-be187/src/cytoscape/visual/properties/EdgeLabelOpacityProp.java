package cytoscape.visual.properties;

import giny.view.EdgeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Properties;

import javax.swing.Icon;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.parsers.FloatParser;
import cytoscape.visual.ui.icon.LineTypeIcon;

public class EdgeLabelOpacityProp extends AbstractVisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.EDGE_LABEL_OPACITY;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(final Object value) {
		final LineTypeIcon icon = new LineTypeIcon() {
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.setColor(new Color(10, 10, 10, 0));
				super.paintIcon(c, g, x, y);
				g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawString(value.toString(), c.getX() + LineTypeIcon.DEFAULT_ICON_SIZE*3/2,
				               (int) ((c.getHeight() / 2) + 7));

				g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
			}
		};
		return icon;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ev DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 */
	public void applyToEdgeView(EdgeView ev, Object o) {
		if ((o == null) || (ev == null))
			return;

		Integer tp = ((Color) ev.getLabel().getTextPaint()).getAlpha();
		Integer newTp = ((Number) o).intValue();

		if (tp != newTp) {
			final Color oldPaint = (Color) ev.getLabel().getTextPaint();
			ev.getLabel().setTextPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(),
			                                oldPaint.getBlue(), newTp));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param props DOCUMENT ME!
	 * @param baseKey DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object parseProperty(Properties props, String baseKey) {
		String s = props.getProperty(VisualPropertyType.EDGE_LABEL_OPACITY.getDefaultPropertyKey(baseKey));

		if (s != null)
			return (new FloatParser()).parseFloat(s).intValue();
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return new Integer(255);
	}
}
