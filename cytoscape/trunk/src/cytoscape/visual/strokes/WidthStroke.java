
package cytoscape.visual.strokes;

import java.awt.Stroke;

public interface WidthStroke extends Stroke {
	WidthStroke newInstanceForWidth(float width);
	String getName();
}
