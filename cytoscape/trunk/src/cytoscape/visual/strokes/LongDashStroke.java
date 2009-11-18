

package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;

public class LongDashStroke extends BasicStroke implements WidthStroke {

	String name;
	float width;

	public LongDashStroke(float width, String name) {
		super(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
		      10.0f, new float[]{width * 4f, width * 2f}, 0.0f);
		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new LongDashStroke(w,name);
	}

	public String getName() {
		return name;
	}

	public String toString() { return name + " " + Float.toString(width); }
}


