

package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class ParallelStroke extends ShapeStroke {

	public ParallelStroke(float width, String name) {
		super( new Shape[] { getParallelStroke(width) }, 0.75f, name, width );
		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new ParallelStroke(w,name);
	}

	static Shape getParallelStroke(final float width) {
		GeneralPath shape = new GeneralPath();

		// Instead of drawing a simple "=" sign with two parallel
		// rectanges, I'm drawing rectangles with rounded short ends.
		// This coupled the overlap specified in the advance above,
		// means that when these lines are curved, they appear to 
		// bend slightly more gracefully.

		shape.moveTo(0f,-0.5f*width);
		shape.lineTo(1f,-0.5*width);
		shape.quadTo(1.5f, -0.75f*width, 1f,-1f*width);
		shape.lineTo(0f,-1f*width);
		shape.quadTo(-0.5f,-0.75*width,0f,-0.5f*width);

		shape.moveTo(0f,0.5f*width);
		shape.lineTo(1f,0.5*width);
		shape.quadTo(1.5f, 0.75f, 1f,1f*width);
		shape.lineTo(0f,1f*width);
		shape.quadTo(-0.5f, 0.75f*width, 0f,0.5f*width);

		return shape;
	}
}


