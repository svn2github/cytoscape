package cytoscape.visual;

import static cytoscape.visual.Position.CENTER;
import static cytoscape.visual.Position.JUSTIFY_CENTER;

import java.text.DecimalFormat;

/**
*
*/
public class ObjectPositionImpl implements ObjectPosition {

	private Position objectAnchor;
	private Position targetAnchor;
	private Position justify;

	private double xOffset;
	private double yOffset;

	/**
	 * Creates a new ObjectPosition object.
	 */
	public ObjectPositionImpl() {
		this(CENTER, CENTER, JUSTIFY_CENTER, 0.0, 0.0);
	}

	/**
	 * Copy constructor
	 * 
	 * @param lp
	 *            DOCUMENT ME!
	 */
	public ObjectPositionImpl(final ObjectPosition p) {
		targetAnchor = p.getTargetAnchor();
		objectAnchor = p.getAnchor();
		xOffset = p.getOffsetX();
		yOffset = p.getOffsetY();
		justify = p.getJustify();
	}

	/**
	 * Creates a new ObjectPosition object.
	 * 
	 * @param targ
	 *            DOCUMENT ME!
	 * @param lab
	 *            DOCUMENT ME!
	 * @param just
	 *            DOCUMENT ME!
	 * @param x
	 *            DOCUMENT ME!
	 * @param y
	 *            DOCUMENT ME!
	 */
	public ObjectPositionImpl(final Position targ, final Position lab,
			final Position just, final double x, final double y) {
		targetAnchor = targ;
		objectAnchor = lab;
		justify = just;
		xOffset = x;
		yOffset = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#getLabelAnchor()
	 */
	public Position getAnchor() {
		return objectAnchor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#getTargetAnchor()
	 */
	public Position getTargetAnchor() {
		return targetAnchor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#getJustify()
	 */
	public Position getJustify() {
		return justify;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#getOffsetX()
	 */
	public double getOffsetX() {
		return xOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#getOffsetY()
	 */
	public double getOffsetY() {
		return yOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#setLabelAnchor(int)
	 */
	public void setAnchor(Position p) {
		objectAnchor = p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#setTargetAnchor(int)
	 */
	public void setTargetAnchor(Position p) {
		targetAnchor = p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#setJustify(int)
	 */
	public void setJustify(Position p) {
		justify = p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#setOffsetX(double)
	 */
	public void setOffsetX(double d) {
		xOffset = d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#setOffsetY(double)
	 */
	public void setOffsetY(double d) {
		yOffset = d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#equals(java.lang.Object)
	 */
	public boolean equals(Object lp) {
		// Accepts non-null ObjectPosition only.
		if (lp == null || lp instanceof ObjectPosition == false)
			return false;

		final ObjectPosition p = (ObjectPosition) lp;

		if (Math.abs(p.getOffsetX() - xOffset) > 0.0000001)
			return false;

		if (Math.abs(p.getOffsetY() - yOffset) > 0.0000001)
			return false;

		if (p.getAnchor() != objectAnchor)
			return false;

		if (p.getTargetAnchor() != targetAnchor)
			return false;

		if (p.getJustify() != justify)
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#toString()
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("target: ").append(targetAnchor.getName());
		sb.append("  label: ").append(objectAnchor.getName());
		sb.append("  justify: ").append(justify.getName());
		sb.append("  X offset: ").append(Double.toString(xOffset));
		sb.append("  Y offset: ").append(Double.toString(yOffset));

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.visual.ObjectPosition#shortString()
	 */
	public String shortString() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		final StringBuilder sb = new StringBuilder();
		sb.append(targetAnchor.getShortName());
		sb.append(",");
		sb.append(objectAnchor.getShortName());
		sb.append(",");
		sb.append(justify.getShortName());
		sb.append(",");
		sb.append(df.format(xOffset));
		sb.append(",");
		sb.append(df.format(yOffset));

		return sb.toString();
	}

}
