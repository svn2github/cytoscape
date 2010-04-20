package giny.view;

import java.awt.Font;
import java.awt.Paint;

public interface Label {
	
	public static final int SOURCE_BOUND = 9;
	public static final int TARGET_BOUND = 10;

	/**
	 * Give the Label a hint on where to draw itself. <B>NOTE:</B> This should
	 * be thought of as a hint only, not all labels will support all positions
	 */

	/**
	 * Get the paint used to paint this nodes text.
	 * 
	 * @return Paint
	 */
	public Paint getTextPaint();

	/**
	 * Set the paint used to paint this nodes text.
	 * 
	 * @param textPaint
	 */
	public void setTextPaint(Paint textPaint);

	/**
	 * Returns the current greek threshold. When the screen font size will be
	 * below this threshold the text is rendered as 'greek' instead of drawing
	 * the text glyphs.
	 */
	public double getGreekThreshold();

	/**
	 * Sets the current greek threshold. When the screen font size will be below
	 * this threshold the text is rendered as 'greek' instead of drawing the
	 * text glyphs.
	 * 
	 * @param threshold
	 *            minimum screen font size.
	 */
	public void setGreekThreshold(double threshold);

	public String getText();

	/**
	 * Set the text for this node. The text will be broken up into multiple
	 * lines based on the size of the text and the bounds width of this node.
	 */
	public void setText(String aText);

	/**
	 * Returns the font of this PText.
	 * 
	 * @return the font of this PText.
	 */
	public Font getFont();

	/**
	 * Set the font of this PText. Note that in Piccolo if you want to change
	 * the size of a text object it's often a better idea to scale the PText
	 * node instead of changing the font size to get that same effect. Using
	 * very large font sizes can slow performance.
	 */
	public void setFont(Font aFont);
	
	
	public void setPosition(final ObjectPosition p);
	public ObjectPosition getPosition();
	
	
	///////////// deprecated in Cytoscape 2.8. ////////////////

	/**
	 *
	 */
	@Deprecated
	public void setTextAnchor(int position);
	

	/**
	 *
	 */
	@Deprecated
	public int getTextAnchor();

	/**
	 *
	 */
	@Deprecated
	public void setJustify(int justify);

	/**
	 *
	 */
	@Deprecated
	public int getJustify();
	
	@Deprecated
	public static int NORTHWEST = 0;
	@Deprecated
	public static int NORTH = 1;
	@Deprecated
	public static int NORTHEAST = 2;

	@Deprecated
	public static int WEST = 3;
	@Deprecated
	public static int CENTER = 4;
	@Deprecated
	public static int EAST = 5;

	@Deprecated
	public static int SOUTHWEST = 6;
	@Deprecated
	public static int SOUTH = 7;
	@Deprecated
	public static int SOUTHEAST = 8;

	@Deprecated
	public static int JUSTIFY_CENTER = 64;
	@Deprecated
	public static int JUSTIFY_LEFT = 65;
	@Deprecated
	public static int JUSTIFY_RIGHT = 66;

	@Deprecated
	public static int NONE = 127;

}
