
package org.cytoscape.ding.impl.strokes;

import java.awt.BasicStroke;

import org.cytoscape.ding.LineStyle;

public class SolidStroke extends BasicStroke implements WidthStroke {

	private float width;

	public SolidStroke(float width) {
		super(width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new SolidStroke(w);
	}

	public LineStyle getLineStyle() {
		return LineStyle.SOLID;
	}

	@Override public String toString() { return LineStyle.SOLID.toString() + " " + Float.toString(width); }
}


