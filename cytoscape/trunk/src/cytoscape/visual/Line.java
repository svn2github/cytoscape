package cytoscape.visual;

import java.awt.Stroke;

/**
 * Define a line.  This will be used as edge or node border.
 * 
 * @since Cytoscape 2.5
 * @author kono
 * 
 */
public class Line {

	// Define line type (stroke).
	private LineTypeDef type;

	// Width of this line.
	private float width;

	public Line(LineTypeDef type, float width) {
		this.type = type;
		this.width = width;
	}

	public void setWidth(final float width) {
		this.width = width;
	}

	public void setType(final LineTypeDef type) {
		this.type = type;
	}

	public float getWidth() {
		return width;
	}

	public LineTypeDef getType() {
		return type;
	}

	public Stroke getStroke() {
		return type.getStroke(width);
	}
}
