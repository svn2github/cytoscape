

package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;
import cytoscape.visual.LineStyle;
import static cytoscape.visual.LineStyle.LONG_DASH;

public class LongDashStroke extends BasicStroke implements WidthStroke {

	float width;

	public LongDashStroke(float width) {
		super(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
		      10.0f, new float[]{width * 4f, width * 2f}, 0.0f);
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new LongDashStroke(w);
	}

	public LineStyle getLineStyle() {
		return LONG_DASH;
	}

	public String toString() { return LONG_DASH.toString() + " " + Float.toString(width); }
}


