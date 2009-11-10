

package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class PipeStroke extends ShapeStroke implements WidthStroke {

	String name;
	float width;

	public enum Type {
		VERTICAL(0f),
		FORWARD(-1f),
		BACKWARD(1f),
		;
		
		private float adjust;
		private Type(float adjust) {
			this.adjust = adjust;
		}

		public float adjust(float input) {
			return adjust * input;	
		}
	}

	private Type offsetType;

	public PipeStroke(float width, Type offsetType, String name) {
		super( new Shape[] { getShape(width, offsetType) },  width );
		this.name = name;
		this.width = width;
		this.offsetType = offsetType;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new PipeStroke(w,offsetType,name);
	}

	public String getName() {
		return name;
	}

	public String toString() { return name + " " + Float.toString(width); }

	static Shape getShape(final float input, final Type offsetType) {
		GeneralPath shape = new GeneralPath();
		float height = input;
		float width = input/5f;
		float offset = offsetType.adjust(input); 

		shape.moveTo(0f,-height);
		shape.lineTo(width,-height);
		shape.lineTo(width+offset,height);
		shape.lineTo(0f+offset,height);
		shape.lineTo(0f,-height);

		return shape;
	}
}


