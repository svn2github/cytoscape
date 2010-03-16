
package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class DotStroke extends ShapeStroke {

	public DotStroke(float width, String name) {
		super( new Shape[] { new Ellipse2D.Float(0, 0, width, width) }, width * 2f, name, width );
		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new DotStroke(w,name);
	}
}


