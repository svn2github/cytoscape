package cytoscape.visual;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * 
 * Define line stroke.
 * 
 * TODO: need to modify rendering engine to fully support dash lines.
 * 
 * @author kono
 *
 */
public enum LineTypeDef {
	SOLID(null), DOT("2.0,2.0"), DASH("5.0,3.0"), DASH_DOT("10.0,2.0,2.0,2.0");

	private float[] strokeDef;

	private LineTypeDef(String def) {
		if (def == null) {
			strokeDef = null;
		} else {
			final String[] parts = def.split(",");
			strokeDef = new float[parts.length];
			for(int i=0; i<strokeDef.length; i++) {
				strokeDef[i] = Float.parseFloat(parts[i]);
			}
		}
	}

	public Stroke getStroke(float width) {
		if(strokeDef != null) {
			return new BasicStroke(width, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 1.0f, strokeDef, 0.0f);
		} else {
			return new BasicStroke(width);
		}
	}
}
