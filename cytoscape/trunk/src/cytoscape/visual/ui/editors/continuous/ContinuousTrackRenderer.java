
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.visual.ui.editors.continuous;

import cytoscape.Cytoscape;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.Thumb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class ContinuousTrackRenderer extends JComponent implements VizMapperTrackRenderer {
	/*
	 * Constants for diagram.
	 */
	private int TRACK_HEIGHT = 120;
	private static final int THUMB_WIDTH = 12;
	private static final Font ICON_FONT = new Font("SansSerif", Font.BOLD, 8);
	private final Font smallFont = new Font("SansSerif", Font.BOLD, 10);
	private final Font defFont = new Font("SansSerif", Font.BOLD, 12);
	private final Font largeFont = new Font("SansSerif", Font.BOLD, 14);
	private static final Color VALUE_AREA_COLOR = new Color(0, 180, 255, 40);
	private static final int V_PADDING = 20;
	private int ARROW_BAR_Y_POSITION = TRACK_HEIGHT + 50;
	private static final String TITLE1 = "Mapping: ";
	private Map<Integer, Double> valueMap;

	/*
	 * Define Colors used in this diagram.
	 */
	private static final Color BORDER_COLOR = Color.black;
	private double valueRange;
	private double minValue;
	private double maxValue;
	private float min = 0;
	private float max = 0;
	private boolean clickFlag = false;
	private Point curPoint;
	private JXMultiThumbSlider slider;
	private CMouseListener listener = null;
	private Map<Integer, Point> verticesList;
	private int selectedIdx;
	private Point dragOrigin;
	private VisualPropertyType type;
	private ContinuousMapping cMapping;
	private String title;
	private Number below;
	private Number above;
	private List<Float> values = new ArrayList<Float>();
	private Polygon valueArea = new Polygon();

	/**
	 * Creates a new ContinuousTrackRenderer object.
	 *
	 * @param minValue DOCUMENT ME!
	 * @param maxValue DOCUMENT ME!
	 */
	public ContinuousTrackRenderer(VisualPropertyType type, double minValue, double maxValue,
	                               Number below, Number above) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.below = below;
		this.above = above;

		this.type = type;

		if (type.isNodeProp())
			cMapping = (ContinuousMapping) Cytoscape.getVisualMappingManager().getVisualStyle()
			                                        .getNodeAppearanceCalculator()
			                                        .getCalculator(type).getMapping(0);
		else
			cMapping = (ContinuousMapping) Cytoscape.getVisualMappingManager().getVisualStyle()
			                                        .getEdgeAppearanceCalculator()
			                                        .getCalculator(type).getMapping(0);

		title = cMapping.getControllingAttributeName();
		valueRange = Math.abs(maxValue - minValue);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 */
	public void paint(Graphics g) {
		super.paint(g);
		paintComponent(g);
	}

	protected void paintComponent(Graphics gfx) {
		TRACK_HEIGHT = slider.getHeight() - 100;
		ARROW_BAR_Y_POSITION = TRACK_HEIGHT + 50;

		// AA on
		Graphics2D g = (Graphics2D) gfx;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int thumb_width = 12;
		int track_width = slider.getWidth() - thumb_width;
		g.translate(thumb_width / 2, 12);

		// get the list of tumbs
		List<Thumb> stops = slider.getModel().getSortedThumbs();

		int numPoints = stops.size();

		// set up the data for the gradient
		float[] fractions = new float[numPoints];
		Float[] floatProperty = new Float[numPoints];
		int i = 0;

		values.clear();
		values.add(below.floatValue());
		values.add(above.floatValue());

		for (Thumb thumb : stops) {
			floatProperty[i] = (Float) thumb.getObject();
			fractions[i] = thumb.getPosition();
			values.add((Float) thumb.getObject());
			i++;
		}

		for (Float val : values) {
			if (min >= val)
				min = val;

			if (max <= val)
				max = val;
		}

		// Draw arrow bar
		g.setStroke(new BasicStroke(1.0f));
		g.setColor(Color.black);
		g.drawLine(0, ARROW_BAR_Y_POSITION, track_width, ARROW_BAR_Y_POSITION);

		Polygon arrow = new Polygon();
		arrow.addPoint(track_width, ARROW_BAR_Y_POSITION);
		arrow.addPoint(track_width - 20, ARROW_BAR_Y_POSITION - 8);
		arrow.addPoint(track_width - 20, ARROW_BAR_Y_POSITION);
		g.fill(arrow);

		g.setColor(Color.gray);
		g.drawLine(0, ARROW_BAR_Y_POSITION, 15, ARROW_BAR_Y_POSITION - 30);
		g.drawLine(15, ARROW_BAR_Y_POSITION - 30, 25, ARROW_BAR_Y_POSITION - 30);

		g.setFont(smallFont);
		g.drawString("Min=" + minValue, 28, ARROW_BAR_Y_POSITION - 25);

		g.drawLine(track_width, ARROW_BAR_Y_POSITION, track_width - 15, ARROW_BAR_Y_POSITION + 30);
		g.drawLine(track_width - 15, ARROW_BAR_Y_POSITION + 30, track_width - 25,
		           ARROW_BAR_Y_POSITION + 30);

		final String maxStr = "Max=" + maxValue;
		int strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), maxStr);
		g.drawString(maxStr, track_width - strWidth - 26, ARROW_BAR_Y_POSITION + 35);

		g.setFont(defFont);
		g.setColor(Color.black);
		strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), title);
		g.drawString(title, (track_width / 2) - (strWidth / 2), ARROW_BAR_Y_POSITION + 35);

		/*
		 * If no points, just draw empty box.
		 */
		if (numPoints == 0) {
			g.setColor(BORDER_COLOR);
			g.setStroke(new BasicStroke(1.5f));
			g.drawRect(0, 5, track_width, TRACK_HEIGHT);

			return;
		}

		g.setStroke(new BasicStroke(1.0f));

		/*
		 * Fill background
		 */
		g.setColor(Color.white);
		g.fillRect(0, 5, track_width, TRACK_HEIGHT);

		int newX = 0;
		int lastY = 0;

		Point2D p1 = new Point2D.Float(0, 5);
		Point2D p2 = new Point2D.Float(0, 5);

		for (i = 0; i < floatProperty.length; i++) {
			newX = (int) (track_width * (fractions[i] / 100));

			p2.setLocation(newX, 5);

			int newY = (5 + TRACK_HEIGHT) - (int) ((floatProperty[i] / max) * TRACK_HEIGHT);

			valueArea.reset();

			g.setColor(VALUE_AREA_COLOR);

			if (i == 0) {
				int h = (5 + TRACK_HEIGHT) - (int) ((below.floatValue() / max) * TRACK_HEIGHT);
				g.fillRect(0, h, newX, (int) ((below.floatValue() / max) * TRACK_HEIGHT));
				g.setColor(Color.red);
				g.fillRect(-5, h - 5, 10, 10);
			} else {
				valueArea.addPoint((int) p1.getX(), lastY);
				valueArea.addPoint(newX, newY);
				valueArea.addPoint(newX, TRACK_HEIGHT + 5);
				valueArea.addPoint((int) p1.getX(), TRACK_HEIGHT + 5);
				g.fill(valueArea);
			}

			for (int j = 0; j < stops.size(); j++) {
				if (slider.getModel().getThumbAt(j).getObject() == floatProperty[i]) {
					Point newPoint = new Point(newX, newY);

					if (verticesList.containsValue(newPoint) == false)
						verticesList.put(j, new Point(newX, newY));

					break;
				}
			}

			lastY = newY;

			g.setColor(Color.black);
			g.setStroke(new BasicStroke(1.5f));
			g.setFont(smallFont);

			int numberWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(),
			                                                    floatProperty[i].toString());

			g.setColor(Color.DARK_GRAY);

			if (fractions[i] < 10) {
				g.drawLine(newX, newY, newX + 15, newY - 35);
				g.drawString(floatProperty[i].toString(), newX + numberWidth, newY - 48);
			} else {
				g.drawLine(newX, newY, newX - 15, newY + 35);
				g.drawString(floatProperty[i].toString(), newX - (numberWidth + 5), newY + 48);
			}

			g.setColor(Color.DARK_GRAY);
			g.setFont(new Font("SansSerif", Font.BOLD, 10));

			Float curPositionValue = ((Double) (((fractions[i] / 100) * valueRange)
			                         - Math.abs(minValue))).floatValue();
			String valueString = String.format("%.4f", curPositionValue);

			int flipLimit = 90;
			int borderVal = track_width - newX;

			if (((i % 2) == 0) && (flipLimit < borderVal)) {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX + 20, ARROW_BAR_Y_POSITION - 15);
				g.drawLine(newX + 20, ARROW_BAR_Y_POSITION - 15, newX + 30,
				           ARROW_BAR_Y_POSITION - 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX + 33, ARROW_BAR_Y_POSITION - 11);
			} else if (((i % 2) == 1) && (flipLimit < borderVal)) {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX + 20, ARROW_BAR_Y_POSITION + 15);
				g.drawLine(newX + 20, ARROW_BAR_Y_POSITION + 15, newX + 30,
				           ARROW_BAR_Y_POSITION + 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX + 33, ARROW_BAR_Y_POSITION + 19);
			} else if (((i % 2) == 0) && (flipLimit >= borderVal)) {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX - 20, ARROW_BAR_Y_POSITION - 15);
				g.drawLine(newX - 20, ARROW_BAR_Y_POSITION - 15, newX - 30,
				           ARROW_BAR_Y_POSITION - 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX - 90, ARROW_BAR_Y_POSITION - 11);
			} else {
				g.drawLine(newX, ARROW_BAR_Y_POSITION, newX - 20, ARROW_BAR_Y_POSITION + 15);
				g.drawLine(newX - 20, ARROW_BAR_Y_POSITION + 15, newX - 30,
				           ARROW_BAR_Y_POSITION + 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX - 90, ARROW_BAR_Y_POSITION + 19);
			}

			g.setColor(Color.black);
			g.fillOval(newX - 3, ARROW_BAR_Y_POSITION - 3, 6, 6);

			p1.setLocation(p2);
		}

		p2.setLocation(track_width, 5);

		g.setColor(VALUE_AREA_COLOR);

		int h = (5 + TRACK_HEIGHT) - (int) ((above.floatValue() / max) * TRACK_HEIGHT);
		g.fillRect((int) p1.getX(), h, track_width - (int) p1.getX(),
		           (int) ((above.floatValue() / max) * TRACK_HEIGHT));
		g.setColor(Color.red);
		g.fillRect(track_width - 5, h - 5, 10, 10);

		/*
		 * Finally, draw border line (rectangle)
		 */
		g.setColor(BORDER_COLOR);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRect(0, 5, track_width, TRACK_HEIGHT);

		g.setColor(Color.red);
		g.setStroke(new BasicStroke(1.5f));

		for (Integer key : verticesList.keySet()) {
			Point p = verticesList.get(key);

			if (clickFlag) {
				int diffX = Math.abs(p.x - (curPoint.x - 6));
				int diffY = Math.abs(p.y - (curPoint.y - 12));

				if (((diffX < 6) && (diffY < 6)) || (key == selectedIdx)) {
					g.setColor(Color.green);
					g.setStroke(new BasicStroke(2.5f));
				} else {
					g.setColor(Color.red);
					g.setStroke(new BasicStroke(1.5f));
				}
			}

			g.drawRect(p.x - 5, p.y - 5, 10, 10);
		}

		/*
		 * Draw below & above
		 */
		g.translate(-THUMB_WIDTH / 2, -12);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param i DOCUMENT ME!
	 */
	public void removePoint(int i) {
		verticesList.remove(i);
		System.out.println("---List len = " + verticesList.size());
		slider.repaint();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Double getSelectedThumbValue() {
		final float position = slider.getModel().getThumbAt(slider.getSelectedIndex()).getPosition();
		final double thumbVal = (((position / 100) * valueRange) - Math.abs(minValue));

		return thumbVal;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param slider DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public JComponent getRendererComponent(JXMultiThumbSlider slider) {
		this.slider = slider;

		// this.slider.addMouseWheelListener(new MouseWheelListener() {
		//
		// public void mouseWheelMoved(MouseWheelEvent e) {
		// // TODO Auto-generated method stub
		//        System.out.println("rc called!: " + slider.getModel().getThumbCount());

		//			
		// });
		if (listener == null) {
			listener = new CMouseListener();
			this.slider.addMouseListener(listener);
			this.slider.addMouseMotionListener(new CMouseMotionListener());
		}

		if (verticesList == null)
			verticesList = new HashMap<Integer, Point>();

		if (valueMap == null)
			valueMap = new HashMap<Integer, Double>();

		return this;
	}

	protected List getRanges() {
		List range = new ArrayList();

		return range;
	}

	protected String getToolTipForCurrentPosition() {
		return "AAAAAAAAA";
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getToolTipForCurrentLocation(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object getObjectInRange(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	private void fractionlocation() {
	}

	class CMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			curPoint = e.getPoint();

			/*
			 * If beyond the bottom lin
			 */
			if (clickFlag == true) {
				Thumb selectedThumb = slider.getModel().getThumbAt(selectedIdx);

				if (curPoint.getY() >= (TRACK_HEIGHT + 5)) {
					selectedThumb.setObject(0f);

					return;
				}

				Float oldVal = (Float) selectedThumb.getObject();
				double curY = curPoint.getY();
				float fraction = (float) (curY / (TRACK_HEIGHT + THUMB_WIDTH));
				float newY = (float) ((((TRACK_HEIGHT + 5) - curY) * max) / (TRACK_HEIGHT + 5));

				selectedThumb.setObject(newY);
			}

			dragOrigin = e.getPoint();
			slider.repaint();
		}

		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}
	}

	class CMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			/*
			 * Show popup dialog to enter new numerical value.
			 */
			if (isPointerInSquare(e) && (e.getClickCount() == 2)) {
				final String val = JOptionPane.showInputDialog(slider,
				                                               "Please type new value for this pivot.");
				final Float newVal = Float.valueOf(val);
				slider.getModel().getThumbAt(selectedIdx).setObject(newVal);

				updateMax();

				cMapping.getPoint(selectedIdx).getRange().equalValue = newVal;

				final BoundaryRangeValues brv = new BoundaryRangeValues(cMapping.getPoint(selectedIdx)
				                                                                .getRange().lesserValue,
				                                                        newVal,
				                                                        cMapping.getPoint(selectedIdx)
				                                                                .getRange().greaterValue);

				cMapping.getPoint(selectedIdx).setRange(brv);

				int numPoints = cMapping.getAllPoints().size();

				// Update Values which are not accessible from
				// UI
				if (numPoints > 1) {
					if (selectedIdx == 0)
						brv.greaterValue = newVal;
					else if (selectedIdx == (numPoints - 1))
						brv.lesserValue = newVal;
					else {
						brv.lesserValue = newVal;
						brv.greaterValue = newVal;
					}

					cMapping.fireStateChanged();

					Cytoscape.getVisualMappingManager().getNetworkView().redrawGraph(false, true);
					slider.repaint();
				}

				repaint();
				slider.repaint();
				repaint();
			}
		}

		public void mousePressed(MouseEvent e) {
			curPoint = e.getPoint();
			dragOrigin = e.getPoint();

			for (Integer key : verticesList.keySet()) {
				Point p = verticesList.get(key);
				int diffY = Math.abs((p.y + 12) - curPoint.y);
				int diffX = Math.abs((p.x + (THUMB_WIDTH / 2)) - curPoint.x);

				if ((diffX < 6) && (diffY < 6)) {
					selectedIdx = key;
					clickFlag = true;
				}
			}
		}

		public void mouseReleased(MouseEvent arg0) {
			clickFlag = false;
			updateMax();

			if (slider.getSelectedThumb() == null)
				slider.repaint();

			//			
			repaint();
		}

		private boolean isPointerInSquare(MouseEvent e) {
			curPoint = e.getPoint();
			dragOrigin = e.getPoint();

			for (Integer key : verticesList.keySet()) {
				Point p = verticesList.get(key);
				int diffY = Math.abs((p.y + 12) - curPoint.y);
				int diffX = Math.abs((p.x + (THUMB_WIDTH / 2)) - curPoint.x);

				if ((diffX < 6) && (diffY < 6)) {
					System.out.println("\n" + "\nHIT!!!!!!!!!!!!" + curPoint + ", " + p);
					selectedIdx = key;

					return true;
				}
			}

			return false;
		}

		private void updateMax() {
			Float val;
			Float curMax = 0f;

			for (Object thumb : slider.getModel().getSortedThumbs()) {
				val = (Float) ((Thumb) thumb).getObject();

				if (val > curMax)
					curMax = val;
			}

			max = curMax;

			//            System.out.println("New Max = " + max);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param iconWidth DOCUMENT ME!
	 * @param iconHeight DOCUMENT ME!
	 * @param mapping DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public ImageIcon getTrackGraphicIcon(int iconWidth, int iconHeight) {
		if (slider == null) {
			return null;
		}

		final BufferedImage bi = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = bi.createGraphics();

		// Turn Anti-alias on
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		/*
		 * Fill background
		 */
		g.setColor(Color.white);
		g.fillRect(0, 0, iconWidth, iconHeight);

		int leftSpace = 10;
		int trackHeight = iconHeight - 9;
		int track_width = iconWidth - leftSpace;

		/*
		 * Compute fractions from mapping
		 */
		List<Thumb> stops = slider.getModel().getSortedThumbs();

		int numPoints = stops.size();

		float[] fractions = new float[numPoints];
		Float[] floatProperty = new Float[numPoints];
		int i = 0;

		values.clear();
		values.add(below.floatValue());
		values.add(above.floatValue());

		for (Thumb thumb : stops) {
			floatProperty[i] = (Float) thumb.getObject();
			fractions[i] = thumb.getPosition();
			values.add((Float) thumb.getObject());
			i++;
		}

		for (Float val : values) {
			if (min >= val)
				min = val;

			if (max <= val)
				max = val;
		}

		// Draw min/max
		g.setColor(Color.DARK_GRAY);
		g.setFont(ICON_FONT);

		int minWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(),
		                                                 String.format("%.1f", min));
		int maxWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(),
		                                                 String.format("%.1f", max));

		g.drawString(String.format("%.1f", min), 0, trackHeight);
		g.drawString(String.format("%.1f", max), 0, 8);

		leftSpace = Math.max(minWidth, maxWidth) + 1;
		track_width = iconWidth - leftSpace;

		g.drawString(String.format("%.2f", minValue), 0, iconHeight);

		final String maxStr = String.format("%.2f", maxValue);
		int strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), maxStr);
		g.drawString(maxStr, iconWidth - strWidth - 2, iconHeight);

		/*
		 * If no points, just return empty rectangle.
		 */
		if (numPoints == 0) {
			g.setStroke(new BasicStroke(1.0f));
			g.setColor(Color.DARK_GRAY);
			g.drawRect(leftSpace, 0, track_width - 3, trackHeight);

			return new ImageIcon(bi);
		}

		g.translate(leftSpace, 0);
		g.setStroke(new BasicStroke(1.0f));

		int newX = 0;
		int lastY = 0;

		Point2D p1 = new Point2D.Float(0, 0);
		Point2D p2 = new Point2D.Float(0, 0);

		for (i = 0; i < floatProperty.length; i++) {
			newX = (int) (track_width * (fractions[i] / 100));

			p2.setLocation(newX, 0);

			int newY = trackHeight - (int) ((floatProperty[i] / max) * trackHeight);

			valueArea.reset();

			g.setColor(VALUE_AREA_COLOR);

			if (i == 0) {
				int h = trackHeight - (int) ((below.floatValue() / max) * trackHeight);
				g.fillRect(0, h, newX, (int) ((below.floatValue() / max) * trackHeight));
			} else {
				valueArea.addPoint((int) p1.getX(), lastY);
				valueArea.addPoint(newX, newY);
				valueArea.addPoint(newX, trackHeight);
				valueArea.addPoint((int) p1.getX(), trackHeight);
				g.fill(valueArea);
			}

			for (int j = 0; j < stops.size(); j++) {
				if (slider.getModel().getThumbAt(j).getObject() == floatProperty[i]) {
					Point newPoint = new Point(newX, newY);

					if (verticesList.containsValue(newPoint) == false)
						verticesList.put(j, new Point(newX, newY));

					break;
				}
			}

			lastY = newY;
			p1.setLocation(p2);
		}

		p2.setLocation(track_width, 0);

		g.setColor(VALUE_AREA_COLOR);

		int h = trackHeight - (int) ((above.floatValue() / max) * trackHeight);
		g.fillRect((int) p1.getX(), h, track_width - (int) p1.getX(),
		           (int) ((above.floatValue() / max) * trackHeight));

		g.translate(-leftSpace, 0);

		/*
		 * Finally, draw border line (rectangle)
		 */
		g.setColor(BORDER_COLOR);
		g.setStroke(new BasicStroke(1.0f));
		g.drawRect(leftSpace, 0, track_width - 3, trackHeight);

		return new ImageIcon(bi);
	}
}
