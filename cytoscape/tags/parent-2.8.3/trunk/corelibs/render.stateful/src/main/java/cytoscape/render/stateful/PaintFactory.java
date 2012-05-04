package cytoscape.render.stateful;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;


/**
 * Factory for creating paint object for a rectangulaer bounds.
 * 
 * @author kono
 *
 */
public interface PaintFactory {
	/**
	 * Create a new Paint object bounded by the given rectangular region.
	 * 
	 * @param bound
	 * @return
	 */
	public Paint getPaint(final Rectangle2D bound);
}
