

package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;

public class MicroDashStroke extends BasicStroke implements WidthStroke {

	String name;
	float width;

	public MicroDashStroke(float width, String name) {
		super(width * 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
		      10.0f, new float[]{width * 0.2f, width * 0.6f}, 0.0f);
		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new MicroDashStroke(w,name);
	}

	public String getName() {
		return name;
	}

	public String toString() { return name + " " + Float.toString(width); }
}


