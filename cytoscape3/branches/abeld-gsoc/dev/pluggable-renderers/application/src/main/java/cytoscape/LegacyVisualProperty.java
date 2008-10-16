package cytoscape;

import java.util.Map;
import java.util.Properties;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.cytoscape.vizmap.LabelPosition;
import org.cytoscape.vizmap.ValueParser;
import org.cytoscape.vizmap.icon.LineTypeIcon;
import org.cytoscape.vizmap.icon.NodeIcon;
import org.cytoscape.vizmap.icon.ArrowIcon;
import org.cytoscape.vizmap.parsers.ColorParser;
import org.cytoscape.vizmap.parsers.DoubleParser;
import org.cytoscape.vizmap.parsers.FontParser;
import org.cytoscape.vizmap.LabelPlacerGraphic;

import org.cytoscape.view.DependentVisualPropertyCallback;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.NodeView;
import org.cytoscape.view.Label;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.VisualPropertyCatalog;

import cytoscape.render.immed.GraphGraphics;

import javax.swing.Icon;

/**
 * Helper class to define legacy (pre-3.0 cytoscape) VisualProperties
 */
public class LegacyVisualProperty implements VisualProperty {
	private String name;
	private Class<?> dataType; // Data type
	private boolean isNodeProp; // indicates whether or not property is for a node or edge
	
	public LegacyVisualProperty(final String name, final Class dataType, final boolean isNodeProp) {
		this.dataType = dataType;
		this.isNodeProp = isNodeProp;
		this.name = name;
	}
	
	public void applyToEdgeView(EdgeView ev, Object o) {
		// FIXME FIXME FIXME: this will be replaced with a simple "ev.setVisualAttribute(o)" -- infact, such a method isn't even needed in VisualProperty
		// the following is only needed until we refactor the ViewModel layer & rendering
		if ((o == null) || (ev == null))
			return;
		if (name.equals("EDGE_COLOR")){
			if (!((Color) o).equals(ev.getUnselectedPaint()))
				ev.setUnselectedPaint((Color) o);
		} else if (name.equals("EDGE_LABEL")){
			Label label = ev.getLabel();

			if (!((String) o).equals(label.getText()))
				label.setText((String) o);
		} else if (name.equals("EDGE_FONT_FACE")){
			Label nodelabel = ev.getLabel();
			if (!((Font) o).equals(nodelabel.getFont()))
				nodelabel.setFont((Font) o);
		} else if (name.equals("EDGE_FONT_SIZE")){
			Label edgelabel = ev.getLabel();
			Font f = edgelabel.getFont();
			float newFontSize = ((Number) o).floatValue();

			if (newFontSize != f.getSize2D())
				edgelabel.setFont(f.deriveFont(newFontSize));
		} else if (name.equals("EDGE_LABEL_COLOR")){
			Label edgelabel = ev.getLabel();

			if (!((Color) o).equals(edgelabel.getTextPaint()))
				edgelabel.setTextPaint((Color) o);
		} else if (name.equals("EDGE_TOOLTIP")){
	        if(((String)o).startsWith("<html>")) {
	            ev.setToolTip((String) o);
	            return;
	        }

	        // Setting the tooltip to null is preferred because otherwise a small icon
	        // indicating the empty tooltip appears.
	        if (((String)o).equals("")) {
	            ev.setToolTip(null);
	            return;
	        }

	        StringBuilder buf = new StringBuilder();
	        buf.append("<html><body bgcolor=\"white\"><Div Align=\"center\"><Font Size=\"4\">");
	        buf.append(((String)o).replaceAll("\\n", "<br>"));
	        buf.append("</Font></div></body></html>");
	        ev.setToolTip(buf.toString());
		} else if (name.equals("EDGE_LINE_WIDTH")){
			if (ev.getStrokeWidth() != ((Number)o).floatValue()) {
				final BasicStroke oldValue = (BasicStroke) ev.getStroke();
				final Stroke newLine = new BasicStroke(((Number)o).floatValue(), oldValue.getEndCap(), oldValue.getLineJoin(),
						oldValue.getMiterLimit(), oldValue.getDashArray(), oldValue.getDashPhase() );
			
				//System.out.println("*** o = " + o + ", new w = " + ev.getStrokeWidth());
				
				ev.setStroke(newLine);
				//System.out.println("Changed w = " + ev.getStrokeWidth());
			}
		} else if (name.equals("EDGE_SRCARROW_COLOR")){
			final Paint newSourceArrowColor = ((Color) o);

			if (newSourceArrowColor != ev.getSourceEdgeEndPaint())
				ev.setSourceEdgeEndPaint(newSourceArrowColor);
		} else if (name.equals("EDGE_TGTARROW_COLOR")){

			final Paint newTargetArrowColor = ((Color) o);

			if (newTargetArrowColor != ev.getTargetEdgeEndPaint())
				ev.setTargetEdgeEndPaint(newTargetArrowColor);
		} else if (name.equals("EDGE_OPACITY")){
			Integer tp = ((Color) ev.getUnselectedPaint()).getAlpha();
			Integer newTp = ((Number) o).intValue();

			if (tp != newTp) {
				final Color oldPaint = (Color) ev.getUnselectedPaint();
				ev.setUnselectedPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(),
				                                oldPaint.getBlue(), newTp));
			}
		} else if (name.equals("EDGE_LABEL_OPACITY")){
			Integer tp = ((Color) ev.getLabel().getTextPaint()).getAlpha();
			Integer newTp = ((Number) o).intValue();

			if (tp != newTp) {
				final Color oldPaint = (Color) ev.getLabel().getTextPaint();
				ev.getLabel().setTextPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(),
				                                oldPaint.getBlue(), newTp));
			}
		} else if (name.equals("EDGE_SRCARROW_OPACITY")){
			final Color oldPaint = (Color) ev.getSourceEdgeEndPaint();
			Integer tp = oldPaint.getAlpha();
			Integer newTp = ((Number) o).intValue();

			if (tp != newTp) {
				ev.setSourceEdgeEndPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(), 
				                                   oldPaint.getBlue(), newTp));
			}
		} else if (name.equals("EDGE_TGTARROW_OPACITY")){
			final Color oldPaint = (Color) ev.getTargetEdgeEndPaint();
			Integer tp = oldPaint.getAlpha();
			Integer newTp = ((Number) o).intValue();

			if (tp != newTp) {
				ev.setTargetEdgeEndPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(), 
				                                   oldPaint.getBlue(), newTp));
			}
		} else if (name.equals("EDGE_LABEL_POSITION")){
			/* this was commented out in EdgeLabelPositionProp.java (??) */
		} else {
			System.out.println("unhandled VisualProperty! (apply to EdgeView): "+name);
		}
	}

	public void applyToNodeView(NodeView nv, Object o) {
		if ((o == null) || (nv == null))
			return;
		
		if (name.equals("NODE_FILL_COLOR")){
			nv.setUnselectedPaint((Paint)o);
		} else if (name.equals("NODE_BORDER_COLOR")){
			nv.setBorderPaint((Paint)o);
		} else if (name.equals("NODE_OPACITY")){ // FIXME
			// already moved to NodeRenderer-style
		} else if (name.equals("NODE_BORDER_OPACITY")){ // FIXME
			// already moved to NodeRenderer-style
		} else if (name.equals("NODE_LABEL_OPACITY")){
			Integer tp = ((Color) nv.getLabel().getTextPaint()).getAlpha();
			Integer newTp = ((Number) o).intValue();

			if (tp != newTp) {
				final Color oldPaint = (Color) nv.getLabel().getTextPaint();
				nv.getLabel().setTextPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(),
				                                oldPaint.getBlue(), newTp));
			}
		} else if (name.equals("NODE_SIZE")){
			double size = ((Number) o).doubleValue();
			double difference = size - nv.getHeight();

			if (Math.abs(difference) > 0.1)
				nv.setHeight(size);

			difference = size - nv.getWidth();

			if (Math.abs(difference) > 0.1)
				nv.setWidth(size);
		} else if (name.equals("NODE_WIDTH")){
			double width = ((Number) o).doubleValue();
			double difference = width - nv.getWidth();

			if (Math.abs(difference) > 0.1)
				nv.setWidth(width);
		} else if (name.equals("NODE_HEIGHT")){

			double height = ((Number) o).doubleValue();
			double difference = height - nv.getHeight();

			if (Math.abs(difference) > 0.1)
				nv.setHeight(height);
		} else if (name.equals("NODE_LABEL")){
			Label nodelabel = nv.getLabel();

			if (!((String) o).equals(nodelabel.getText()))
				nodelabel.setText((String) o);
		} else if (name.equals("NODE_FONT_FACE")){
			Label nodelabel = nv.getLabel();

			if (!((Font) o).equals(nodelabel.getFont()))
				nodelabel.setFont((Font) o);
		} else if (name.equals("NODE_BORDER_COLOR")){
			
		} else if (name.equals("NODE_FONT_SIZE")){
			Label nodelabel = nv.getLabel();
			Font f = nodelabel.getFont();
			float newFontSize = ((Number) o).floatValue();

			if (newFontSize != f.getSize2D())
				nodelabel.setFont(f.deriveFont(newFontSize));
		} else if (name.equals("NODE_LABEL_COLOR")){
			Label nodelabel = nv.getLabel();

			if (!((Color) o).equals(nodelabel.getTextPaint()))
				nodelabel.setTextPaint((Color) o);
		} else if (name.equals("NODE_TOOLTIP")){
			if(((String)o).startsWith("<html>")) {
				nv.setToolTip((String) o);
				return;
			}

			// Setting the tooltip to null is preferred because otherwise a small icon
			// indicating the empty tooltip appears.
			if (((String)o).equals("")) {
				nv.setToolTip(null);
				return;
			}
			
			StringBuilder buf = new StringBuilder();
			buf.append("<html><body bgcolor=\"white\"><Div Align=\"center\"><Font Size=\"4\">");
			buf.append(((String)o).replaceAll("\\n", "<br>"));
			buf.append("</Font></div></body></html>");
			nv.setToolTip(buf.toString());
		} else if (name.equals("NODE_LABEL_POSITION")){
			Label nodelabel = nv.getLabel();
			LabelPosition labelPosition = (LabelPosition) o;

			int newTextAnchor = labelPosition.getLabelAnchor();

			if (nodelabel.getTextAnchor() != newTextAnchor)
				nodelabel.setTextAnchor(newTextAnchor);

			int newJustify = labelPosition.getJustify();

			if (nodelabel.getJustify() != newJustify)
				nodelabel.setJustify(newJustify);

			int newNodeAnchor = labelPosition.getTargetAnchor();

			if (nv.getNodeLabelAnchor() != newNodeAnchor)
				nv.setNodeLabelAnchor(newNodeAnchor);

			double newOffsetX = labelPosition.getOffsetX();

			if (nv.getLabelOffsetX() != newOffsetX)
				nv.setLabelOffsetX(newOffsetX);

			double newOffsetY = labelPosition.getOffsetY();

			if (nv.getLabelOffsetY() != newOffsetY)
				nv.setLabelOffsetY(newOffsetY);
		} else {
			System.out.println("unhandled VisualProperty! (apply to NodeView): "+name);
		}

	}

	public Object getDefaultAppearanceObject() {
		// FIXME FIXME: refactor
		if (name.equals("NODE_FILL_COLOR")){
			return Color.orange;
		} else if (name.equals("NODE_BORDER_COLOR")){
			return Color.black;
		} else if (name.equals("NODE_OPACITY")){
			return Integer.valueOf(255);
		} else if (name.equals("NODE_BORDER_OPACITY")){
			return Integer.valueOf(255);
		} else if (name.equals("NODE_LABEL_OPACITY")){
			return Integer.valueOf(255);
		} else if (name.equals("NODE_SIZE")){
			return new Double(35.0);
		} else if (name.equals("NODE_WIDTH")){
			return new Double(70.0);
		} else if (name.equals("NODE_HEIGHT")){
			return new Double(30.0);
		} else if (name.equals("NODE_LABEL")){
			return "";
		} else if (name.equals("NODE_FONT_FACE")){
			return new Font(null, Font.PLAIN, 12);
		} else if (name.equals("NODE_FONT_SIZE")){
			return new Float(12.0f);
		} else if (name.equals("NODE_LABEL_COLOR")){
			return Color.black;
		} else if (name.equals("NODE_TOOLTIP")){
			return "";
		} else if (name.equals("NODE_LABEL_POSITION")){
			return new LabelPosition();
		} else if (name.equals("EDGE_COLOR")){
			return Color.black;
		} else if (name.equals("EDGE_LABEL")){
			return "";
		} else if (name.equals("EDGE_FONT_FACE")){
			return new Font("SanSerif", Font.PLAIN, 10);
		} else if (name.equals("EDGE_FONT_SIZE")){
			return new Float(10.0f);
		} else if (name.equals("EDGE_LABEL_COLOR")){
			return Color.black;
		} else if (name.equals("EDGE_TOOLTIP")){
			return ""; 
		} else if (name.equals("EDGE_LINE_WIDTH")){
			return new Float(1.0f);
		} else if (name.equals("EDGE_SRCARROW_COLOR")){
			return Color.black;
		} else if (name.equals("EDGE_TGTARROW_COLOR")){
			return Color.black;
		} else if (name.equals("EDGE_OPACITY")){
			return Integer.valueOf(255);
		} else if (name.equals("EDGE_LABEL_OPACITY")){
			return Integer.valueOf(255);
		} else if (name.equals("EDGE_SRCARROW_OPACITY")){
			return Integer.valueOf(255); 
		} else if (name.equals("EDGE_TGTARROW_OPACITY")){
			return Integer.valueOf(255); 
		} else if (name.equals("EDGE_LABEL_POSITION")){
			return new LabelPosition();
		} else {
			System.out.println("unhandled VisualProperty! ((getDefaultAppearanceObject)): "+name);
			return null;
		}
	}

	public Icon getDefaultIcon() {
		// TODO Auto-generated method stub
		return getIcon(getDefaultAppearanceObject());
	}

	public Icon getIcon(final Object value) {
		// FIXME FIXME refactor
		
		if (name.equals("NODE_FILL_COLOR")){
			// already moved to NodeRenderer-style
		} else if (name.equals("NODE_BORDER_COLOR")){
			// already moved to NodeRenderer-style
		} else if (name.equals("NODE_OPACITY")){
			// already moved to NodeRenderer-style
		} else if (name.equals("NODE_BORDER_OPACITY")){
			// already moved to NodeRenderer-style
		} else if (name.equals("NODE_LABEL_OPACITY")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876356662L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 0));
							super.paintIcon(c, g, x, y);
							g2d.translate(0, -2);

							final Color color = ((Color) getDefaultAppearanceObject()); 
							g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
							                       ((Number) value).intValue()));
							g2d.setStroke(new BasicStroke(2f));
							g2d.draw(super.newShape);
							g2d.translate(0, 2);

							g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawString(value.toString(), c.getX() + 7,
							               (int) ((c.getHeight() / 2) + 7));

							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					return icon;
		} else if (name.equals("NODE_RENDERER")){
			  return new NodeIcon();
		} else if (name.equals("NODE_SIZE")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876414681L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 25));
							super.paintIcon(c, g, x, y);
							g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawString(value.toString(), c.getX() + 7,
							               (int) ((c.getHeight() / 2) + 7));
							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					icon.setBottomPadding(-2);

					return icon;
		} else if (name.equals("NODE_WIDTH")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876432876L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 25));
							super.paintIcon(c, g, x, y);
							g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawString(value.toString(), c.getX() + 7,
							               (int) ((c.getHeight() / 2) + 7));
							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					icon.setBottomPadding(-2);

					return icon;
		} else if (name.equals("NODE_HEIGHT")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876333773L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 25));
							super.paintIcon(c, g, x, y);
							g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawString(value.toString(), c.getX() + 7,
							               (int) ((c.getHeight() / 2) + 7));
							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					icon.setBottomPadding(-2);

					return icon;
		} else if (name.equals("NODE_LABEL")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876377140L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 25));
							super.paintIcon(c, g, x, y);
							g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
							g2d.setColor(Color.DARK_GRAY);
							
							String defLabel = value.toString();
							if(defLabel.length()>15) {
								defLabel = defLabel.substring(0, 14) + "...";
							}
							g2d.drawString(defLabel, c.getX() + 7,
							               (int) ((c.getHeight() / 2)));
							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					icon.setBottomPadding(-2);

					return icon;
		} else if (name.equals("NODE_FONT_FACE")){
			return new LineTypeIcon() {
				private final static long serialVersionUID = 1202339875930797L;
							public void paintIcon(Component c, Graphics g, int x, int y) {
								super.setColor(new Color(10, 10, 10, 0));
								super.paintIcon(c, g, x, y);
								g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
								g2d.setColor(Color.DARK_GRAY);

								final Font font = (Font) value;
								g2d.setFont(new Font(font.getFontName(), font.getStyle(), 40));
								g2d.setColor(new Color(10, 10, 10, 40));
								g2d.drawString("Font", c.getX() + 15, c.getY() - 10);
								g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
							}
						};
		} else if (name.equals("NODE_BORDER_COLOR")){
			// already moved to NodeRenderer-style
		} else if (name.equals("NODE_FONT_SIZE")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876324715L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 25));
							super.paintIcon(c, g, x, y);

							g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawString(value.toString(), c.getX() + 7,
							               (int) ((c.getHeight() / 2) + 7));

							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					return icon;
		} else if (name.equals("NODE_LABEL_COLOR")){
			final LineTypeIcon icon = new LineTypeIcon();
			icon.setColor(new Color(10, 10, 10, 0));
			icon.setText("Font");

			final Color fontColor = (Color) value;
			final Font defFont = (Font)  VisualPropertyCatalog.getVisualProperty("NODE_FONT_FACE").getDefaultAppearanceObject();
			icon.setTextFont(new Font(defFont.getFontName(), defFont.getStyle(), 24));
			icon.setBottomPadding(-7);
			icon.setTextColor(fontColor);

			return icon;
		} else if (name.equals("NODE_TOOLTIP")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876422546L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 25));
							super.paintIcon(c, g, x, y);
							g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
							g2d.setColor(Color.DARK_GRAY);

							String defLabel = value.toString();

							if (defLabel.length() > 15) {
								defLabel = defLabel.substring(0, 14) + "...";
							}

							g2d.drawString(defLabel, c.getX() + 7, (int) ((c.getHeight() / 2)));
							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					icon.setBottomPadding(-2);

					return icon;
		} else if (name.equals("NODE_LABEL_POSITION")){
			int size = 55;

			final BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = bi.createGraphics();

			LabelPlacerGraphic lp = new LabelPlacerGraphic((LabelPosition) value, size, false);
			lp.paint(g2);

			NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876366750L;
				public void paintIcon(Component c, Graphics g, int x, int y) {
					super.setColor(new Color(10, 10, 10, 0));
					super.paintIcon(c, g, x, y);
					g2d.drawImage(bi, 10, -5, null);
				}
			};

			return icon;
		} else if (name.equals("EDGE_COLOR")){
			final LineTypeIcon icon = new LineTypeIcon();
			icon.setColor((Color) value);
			icon.setBottomPadding(-7);

			return icon;
		} else if (name.equals("EDGE_LABEL")){
			return null;
		} else if (name.equals("EDGE_FONT_FACE")){
			return new LineTypeIcon() {
				private final static long serialVersionUID = 1202339875930797L;
							public void paintIcon(Component c, Graphics g, int x, int y) {
								super.setColor(new Color(10, 10, 10, 0));
								super.paintIcon(c, g, x, y);
								g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
								g2d.setColor(Color.DARK_GRAY);

								final Font font = (Font) value;
								g2d.setFont(new Font(font.getFontName(), font.getStyle(), 40));
								g2d.setColor(new Color(10, 10, 10, 40));
								g2d.drawString("Font", c.getX() + 15, c.getY() - 10);
								g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
							}
						};
		} else if (name.equals("EDGE_FONT_SIZE")){
			final LineTypeIcon icon = new LineTypeIcon();
			icon.setColor(new Color(10, 10, 10, 20));
			icon.setText(value.toString());
			icon.setBottomPadding(-7);

			return icon;
		} else if (name.equals("EDGE_LABEL_COLOR")){
			final NodeIcon icon = new NodeIcon() {
				private final static long serialVersionUID = 1202339876345568L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 0));
							super.paintIcon(c, g, x, y);

							g2d.setColor((Color) value);

							final Font font = (Font) VisualPropertyCatalog.getVisualProperty("NODE_FONT_FACE").getDefaultAppearanceObject(); 
							g2d.setFont(new Font(font.getFontName(), font.getStyle(), 28));
							g2d.drawString("Label", 8, (c.getHeight() / 2) + 10);
							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					return icon;
		} else if (name.equals("EDGE_TOOLTIP")){
			return null;
		} else if (name.equals("EDGE_LINE_WIDTH")){
			final LineTypeIcon icon = new LineTypeIcon();
			icon.setColor(new Color(10, 10, 10, 20));
			icon.setText(value.toString());
			icon.setBottomPadding(-7);

			return icon;
		} else if (name.equals("EDGE_SRCARROW_COLOR")){
			final Integer arrow = (Integer) VisualPropertyCatalog.getVisualProperty("EDGE_SRCARROW_SHAPE").getDefaultAppearanceObject();
			final ArrowIcon icon = new ArrowIcon(GraphGraphics.getArrowShapes().get(new Byte(arrow.byteValue())));
			icon.setColor((Color) value);
			icon.setLeftPadding(20);
			icon.setBottomPadding(-6);
			
			return icon;
		} else if (name.equals("EDGE_TGTARROW_COLOR")){
			final Integer arrowShape = (Integer) VisualPropertyCatalog.getVisualProperty("EDGE_TGTARROW_SHAPE").getDefaultAppearanceObject();
			final ArrowIcon icon = new ArrowIcon(GraphGraphics.getArrowShapes().get(new Byte(arrowShape.byteValue())));
			icon.setColor((Color) value);
			icon.setLeftPadding(20);
			icon.setBottomPadding(-6);

			return icon;
		} else if (name.equals("EDGE_OPACITY")){
			final LineTypeIcon icon = new LineTypeIcon() {
				private final static long serialVersionUID = 1202339875954385L;
						public void paintIcon(Component c, Graphics g, int x, int y) {
							super.setColor(new Color(10, 10, 10, 0));
							super.paintIcon(c, g, x, y);

							g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
							g2d.setColor(Color.DARK_GRAY);
							g2d.drawString(value.toString(),
							               c.getX() + ((LineTypeIcon.DEFAULT_ICON_SIZE * 3) / 2),
							               (int) ((c.getHeight() / 2) + 7));

							g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
						}
					};

					return icon;
		} else if (name.equals("EDGE_LABEL_OPACITY")){
			final LineTypeIcon icon = new LineTypeIcon() {
				private final static long serialVersionUID = 1202339875942467L;
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
		} else if (name.equals("EDGE_SRCARROW_OPACITY")){
			 final LineTypeIcon icon = new LineTypeIcon() {
					private final static long serialVersionUID = 1202339875970881L;
				            public void paintIcon(Component c, Graphics g, int x, int y) {
				                super.setColor(new Color(10, 10, 10, 0));
				                super.paintIcon(c, g, x, y);

				                g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
				                g2d.setColor(Color.DARK_GRAY);
				                g2d.drawString(value.toString(),
				                               c.getX() + ((LineTypeIcon.DEFAULT_ICON_SIZE * 3) / 2),
				                               (int) ((c.getHeight() / 2) + 7));

				                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
				            }
				        };

				        return icon;
		} else if (name.equals("EDGE_TGTARROW_OPACITY")){
			 final LineTypeIcon icon = new LineTypeIcon() {
					private final static long serialVersionUID = 1202339876271830L;
				            public void paintIcon(Component c, Graphics g, int x, int y) {
				                super.setColor(new Color(10, 10, 10, 0));
				                super.paintIcon(c, g, x, y);

				                g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
				                g2d.setColor(Color.DARK_GRAY);
				                g2d.drawString(value.toString(),
				                               c.getX() + ((LineTypeIcon.DEFAULT_ICON_SIZE * 3) / 2),
				                               (int) ((c.getHeight() / 2) + 7));

				                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
				            }
				        };

				        return icon;
		} else if (name.equals("EDGE_LABEL_POSITION")){
			return null;
		} else {
			System.out.println("unhandled VisualProperty! (getIcon): "+name);
			return null;
		}
		return null;
	}

	public Map<Object, Icon> getIconSet() {
		// this method is not applicable to any VisualProperty handled in this class
		// (it is only used for DiscreteVisualProperty)
		return null; // default value
	}

	public Object parseProperty(Properties props, String baseKey) {
		// FIXME FIXME -- do IO later
		return null;
	}
	public Object parseStringValue(String string){
		System.out.println("parsing string value: "+string);
		ValueParser parser=null;
		if (dataType.isAssignableFrom(String.class)){
			return string;
		} else if (dataType.isAssignableFrom(Number.class)){
			parser = new DoubleParser(); 
		} else if  (dataType.isAssignableFrom(Color.class)){
			parser = new ColorParser(); 
		} else if (dataType.isAssignableFrom(Font.class)){
			parser = new FontParser(); 
		}
		Object o = parser.parseStringValue(string);
		System.out.println("parsed value: "+o);
		return o;
	}
	
	public String getName() {
		return name;
	}
	/**
	 * Returns the data type of this VisualProperty.
	 * @return 
	 */
	public Class getDataType() {
		return dataType;
	}

	/**
	 * Check this visual property is for node or not.
	 *
	 * @return true if vp is for node. If false, this is a vp for edge.
	 */
	public boolean isNodeProp() {
		return isNodeProp;
	}
	public DependentVisualPropertyCallback dependentVisualPropertyCallback(){
		return null;
	}
}
