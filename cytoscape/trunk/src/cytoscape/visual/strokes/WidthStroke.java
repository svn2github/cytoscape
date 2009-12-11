
package cytoscape.visual.strokes;

import java.awt.Stroke;
import cytoscape.visual.LineStyle;

/**
 * A simple extension of Stroke that allows Stroke objects to be
 * coupled with LineStyle enum values and allows the width of the
 * stroke to be adjusted.
 */
public interface WidthStroke extends Stroke {
	
	/**
	 * @return A new instance of this WidthStroke with the specified width.
	 */
	WidthStroke newInstanceForWidth(float width);

	/**
	 * @return the LineStyle associated with this particular WidthStroke.
	 */
	LineStyle getLineStyle();
}
