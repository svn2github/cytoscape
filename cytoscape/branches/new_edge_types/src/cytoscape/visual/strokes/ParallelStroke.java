

package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class ParallelStroke extends ShapeStroke implements WidthStroke {

	String name;
	float width;

	public ParallelStroke(float width, String name) {
		super( new Shape[] { getParallelStroke(width) }, 1f );
		this.name = name;
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new ParallelStroke(w,name);
	}

	public String getName() {
		return name;
	}

	public String toString() { return name + " " + Float.toString(width); }

	static Shape getParallelStroke(final float width) {
		GeneralPath shape = new GeneralPath();

		shape.moveTo(0f,-0.5f*width);
		shape.lineTo(1f,-0.5*width);
		shape.lineTo(1f,-1f*width);
		shape.lineTo(0f,-1f*width);
		shape.lineTo(0f,-0.5f*width);

		shape.moveTo(0f,0.5f*width);
		shape.lineTo(1f,0.5*width);
		shape.lineTo(1f,1f*width);
		shape.lineTo(0f,1f*width);
		shape.lineTo(0f,0.5f*width);

		return shape;
	}
}


