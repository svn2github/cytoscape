


package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;
import cytoscape.visual.LineStyle;
import static cytoscape.visual.LineStyle.DASH_DOT;

public class DashDotStroke extends BasicStroke implements WidthStroke {

	private float width;

	public DashDotStroke(float width) {
		super(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 
		      10.0f, new float[]{width * 4f,width*2f,width,width*2f}, 0.0f);

		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new DashDotStroke(w);
	}

	public LineStyle getLineStyle() {
		return DASH_DOT;
	}

	public String toString() { return DASH_DOT + " " + Float.toString(width); }
}


