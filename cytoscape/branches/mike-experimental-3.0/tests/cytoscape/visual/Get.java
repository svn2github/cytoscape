
package cytoscape.visual;

import java.awt.Color;
import java.awt.Font;

class Get {
	static int integer(Object o) {
		return ((Integer)o).intValue();
	}

	static double ddouble(Object o) {
		return ((Number)o).doubleValue();
	}

	static float ffloat(Object o) {
		return ((Number)o).floatValue();
	}

	static String string(Object o) {
		return (String)o;
	}

	static Color color(Object o) {
		return (Color)o;
	}

	static Font font(Object o) {
		return (Font)o;
	}

	static NodeShape nodeShape(Object o) {
		return (NodeShape)o;
	}
}
