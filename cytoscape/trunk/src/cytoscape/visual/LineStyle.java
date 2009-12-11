
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.visual;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.Icon;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import cytoscape.visual.ui.icon.*;
import cytoscape.visual.strokes.*;

/**
 * Define line stroke.
 *
 * @author kono
 */
public enum LineStyle {
	// note that "line" and "dash" regexs are legacy, so don't change them!
	SOLID("line", new SolidStroke(1.0f)),
	LONG_DASH( "dash", new LongDashStroke(1.0f)),
	EQUAL_DASH( "equal_dash", new EqualDashStroke(1.0f)),
	DASH_DOT( "dash_dot", new DashDotStroke(1.0f)),
	DOT("dot_dot", new DotStroke(1.0f)),
	ZIGZAG("zigzag", new ZigzagStroke(1.0f)),
	SINEWAVE("sinewave", new SineWaveStroke(1.0f)),
	VERTICAL_SLASH("vertical_slash",new VerticalSlashStroke(1.0f,PipeStroke.Type.VERTICAL)),
	FORWARD_SLASH("forward_slash",new ForwardSlashStroke(1.0f,PipeStroke.Type.FORWARD)),
	BACKWARD_SLASH("backward_slash",new BackwardSlashStroke(1.0f,PipeStroke.Type.BACKWARD)),
	PARALLEL_LINES("parallel_lines", new ParallelStroke(1.0f)),
	CONTIGUOUS_ARROW("contiguous_arrow", new ContiguousArrowStroke(1.0f)),
	SEPARATE_ARROW("separate_arrow", new SeparateArrowStroke(1.0f)),
	;

	private String regex;
	private WidthStroke stroke;

	private LineStyle(String regex, WidthStroke stroke) {
		this.regex = regex;
		this.stroke = stroke;
	}

	private String getRegex() {
		return regex;
	}

	/**
	 * Attempts to parse a LineStyle object from a string.  If the string does
	 * not match any LineStyle.toString() value exactly, then it attempts a regular
	 * expression match on regex pattern defined in this Enum. The regex support
	 * exists primarily to support legacy file formats.  
	 */
	public static LineStyle parse(String val) {
		// First check the style names.
		for ( LineStyle ls : values() ) {
			if ( ls.toString().equals(val) )
				return ls;
		}

		// Then try regex matching. This is for legacy line types and 
		// should really only either match "line" or "dash".
		for ( LineStyle ls : values() ) {
			Pattern p = Pattern.compile(ls.getRegex(),Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(val);
			if ( m.matches() ) {
				return ls;
			}
		}

		// default
		return SOLID;
	}

	/**
	 * Will attempt to find the LineStyle based on the type of stroke.
	 * If it doesn't match a known stroke, it will return SOLID.
	 * @return the LineStyle guessed from the stroke. 
	 */
	public static LineStyle extractLineStyle(Stroke s) {
		if ( s instanceof WidthStroke ) {
			return ((WidthStroke)s).getLineStyle();	
		} 

		return SOLID;
	}

	/**
	 * Creates a new stroke of this LineStyle with the specified width.
	 */
	public Stroke getStroke(float width) {
		return stroke.newInstanceForWidth( width );
	}

	/**
	 * Returns a map of Icons that can be used for user interfaces.
	 */
    public static Map<Object,Icon> getIconSet() {
        Map<Object,Icon> icons = new HashMap<Object,Icon>();

        for (LineStyle def : values()) {
            LineTypeIcon icon = new LineTypeIcon(def.getStroke(5.0f), 
                                                 VisualPropertyIcon.DEFAULT_ICON_SIZE * 4, 
                                                 VisualPropertyIcon.DEFAULT_ICON_SIZE, 
												 def.name());
            icons.put(def, icon);
        }

        return icons;
    }
}
