
package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class DotStroke extends ShapeStroke implements WidthStroke {

	String name;
	float width;

	public DotStroke(float width, String name) {
		super( new Shape[] { new Ellipse2D.Float(0, 0, width, width) }, width * 2f );
		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new DotStroke(w,name);
	}

	public String getName() {
		return name;
	}

	public String toString() { return name + " " + Float.toString(width); }
}


