
package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;

public class SolidStroke extends BasicStroke implements WidthStroke {

	String name;
	float width;

	public SolidStroke(float width, String name) {
		super(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new SolidStroke(w,name);
	}

	public String getName() {
		return name;
	}

	public String toString() { return name + " " + Float.toString(width); }
}


