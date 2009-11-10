
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
	SOLID("line", new SolidStroke(1.0f,"line")),
	LONG_DASH( "dash", new LongDashStroke(1.0f,"dash")),
	EQUAL( "equal", new EqualDashStroke(1.0f,"equal")),
	UNEVEN( "uneven", new DashDotStroke(1.0f,"uneven")),
	DOT("dot", new DotStroke(1.0f,"dot")),
	ZIGZAG("zigzag", new ZigzagStroke(1.0f,"zigzag")),
	SINEWAVE("sinewave", new SineWaveStroke(1.0f,"sinewave")),
	MICRO("micro", new MicroDashStroke(1.0f,"micro")),
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

	public static LineStyle parse(String val) {
		// first check the style names
		for ( LineStyle ls : values() ) {
			if ( ls.toString().equals(val) )
				return ls;
		}

		// then try regex matching instead 
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

	private static Pattern numPattern = Pattern.compile("(\\d+)");

	/** 
	 * This method attempts to extract a width from a string that has
	 * a number in it like "dashed1" or "line2". This exists to support
	 * old-style line type definitions.
	 * @return The parsed value or if something doesn't match, 1.0
	 */
	public static float parseWidth(String s) {
		Matcher m = numPattern.matcher(s);
		if ( m.matches() ) {
			try {
				return (new Float(m.group(1))).floatValue();
			} catch (Exception e) { }
		}
		return 1.0f;
	}

	/**
	 * Will attempt to find the LineStyle based on the type of stroke.
	 * If it doesn't match a known stroke, it will return SOLID.
	 * @return the LineStyle guessed from the stroke. 
	 */
	public static LineStyle extractLineStyle(Stroke s) {
		if ( s instanceof WidthStroke ) {
			return LineStyle.parse( ((WidthStroke)s).getName() );	
		} 

		return SOLID;
	}

	public Stroke getStroke(float width) {
		return stroke.newInstanceForWidth( width );
	}
	
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
