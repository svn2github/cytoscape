
package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;
import cytoscape.visual.LineStyle;
import static cytoscape.visual.LineStyle.SOLID;

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
		return SOLID;
	}

	public String toString() { return SOLID.toString() + " " + Float.toString(width); }
}


