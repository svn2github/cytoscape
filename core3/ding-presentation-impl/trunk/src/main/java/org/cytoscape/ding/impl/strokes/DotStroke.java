
package org.cytoscape.ding.impl.strokes;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

//import static org.cytoscape.ding.LineStyle.DOT;

public class DotStroke extends ShapeStroke {


	
	//FIXME
	public DotStroke(float width) {
		super( new Shape[] { new Ellipse2D.Float(0, 0, width, width) }, width * 2f, width );
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new DotStroke(w);
	}
}


