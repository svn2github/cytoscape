

package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import static cytoscape.visual.LineStyle.PARALLEL_LINES;

public class ParallelStroke extends ShapeStroke {

	public ParallelStroke(float width) {
		super( new Shape[] { getParallelStroke(width) }, 1f, PARALLEL_LINES, width );
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new ParallelStroke(w);
	}

	private static Shape getParallelStroke(final float width) {
		GeneralPath shape = new GeneralPath();

		shape.moveTo(0f,-0.5f*width);
		shape.lineTo(1f,-0.5f*width);
		shape.lineTo(1f,-1f*width);
		shape.lineTo(0f,-1f*width);
		shape.lineTo(0f,-0.5f*width);

		shape.moveTo(0f,0.5f*width);
		shape.lineTo(1f,0.5f*width);
		shape.lineTo(1f,1f*width);
		shape.lineTo(0f,1f*width);
		shape.lineTo(0f,0.5f*width);

		return shape;
	}
}


