


package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;

public class DashDotStroke extends BasicStroke implements WidthStroke {

	String name;
	float width;

	public DashDotStroke(float width, String name) {
		super(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 
		      10.0f, new float[]{12.0f,3.0f,3.0f,3.0f}, 0.0f);

		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new DashDotStroke(w,name);
	}

	public String getName() {
		return name;
	}

	public String toString() { return name + " " + Float.toString(width); }
}


