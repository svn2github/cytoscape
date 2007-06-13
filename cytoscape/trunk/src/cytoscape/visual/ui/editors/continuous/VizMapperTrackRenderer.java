package cytoscape.visual.ui.editors.continuous;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jdesktop.swingx.multislider.TrackRenderer;


/**
 * DOCUMENT ME!
 *
 * @author $author$-
  */
public interface VizMapperTrackRenderer extends TrackRenderer {
	
	/*
	 * Static variables used by the implemeted classes.
	 */
	static final Font ICON_FONT = new Font("SansSerif", Font.BOLD, 8);
	static final Font SMALL_FONT = new Font("SansSerif", Font.BOLD, 10);
	static final Font LARGE_FONT = new Font("SansSerif", Font.BOLD, 18);
	
	static final Color BORDER_COLOR = Color.DARK_GRAY;
	
	static final BasicStroke STROKE1 = new BasicStroke(1.0f);
	static final BasicStroke STROKE2 = new BasicStroke(2.0f);
	
	
}
