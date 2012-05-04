
package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;
import cytoscape.visual.LineStyle;

/**
 * Rather than handle strokes of width 0 for all implementations of WidthStroke,
 * use this wrapper class that, when the width is less than or equal to 0 a
 * BasicStroke is returned, whereas when the width is greater than 0, return the
 * specified actual WidthStroke.
 */
public class ZeroStroke extends BasicStroke implements WidthStroke {

	final WidthStroke actualStroke;

	/**
	 * @param actualStroke The actual WidthStroke that this ZeroStroke represents. This
	 * object will be used whenever the width for a new instance is greater than 0.
	 */
	public ZeroStroke(WidthStroke actualStroke) {
		super(0);
		this.actualStroke = actualStroke;
	}

	public WidthStroke newInstanceForWidth(float w) {
		if ( w <= 0 )
			return new ZeroStroke(actualStroke);
		else
			return actualStroke.newInstanceForWidth(w);
	}

	public LineStyle getLineStyle() {
		return actualStroke.getLineStyle();
	}

	public String toString() { return getLineStyle().toString() + " 0"; }
}


