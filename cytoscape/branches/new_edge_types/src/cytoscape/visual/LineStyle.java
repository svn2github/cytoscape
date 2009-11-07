
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

/**
 *
 * Define line stroke.
 *
 * TODO: need to modify rendering engine to fully support dash lines.
 *
 * @author kono
 *
 */
public enum LineStyle {
	SOLID("line"),
	LONG_DASH( "dash"),
	DOUBLE("double"),
	SHAPE("shape"),
	ZIGZAG("zigzag"),
	;

	// DASH("4.0f,4.0f"),
	// DASH_DOT("12.0f,3.0f,3.0f,3.0f"),

	private String regex;

	private LineStyle(String regex) {
		this.regex = regex;
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
	 * A method that attempts to figure out if a stroke is dashed
	 * or not.  If the Stroke object is not a BasicStroke, it will
	 * return SOLID by default.
	 * @return the LineStyle guessed based on the BasicStroke dash array.
	 */
	public static LineStyle extractLineStyle(Stroke stroke) {
		if ( stroke instanceof BasicStroke ) {
        	final float[] dash = ((BasicStroke)stroke).getDashArray();
			if ( dash == null )
				return SOLID;
			else
				return LONG_DASH;
		} 

		return SOLID;
	}

	public Stroke getStroke(float width) {
		// OH God!
		if ( regex.equals("dash") )
			return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f,
			                       new float[]{10.0f,4.0f}, 0.0f);
		else if ( regex.equals("double") )
			return new DoubleStroke(width,width);
		else if ( regex.equals("shape") )
			return new ShapeStroke( new Shape[] { new Ellipse2D.Float(0, 0, 4, 4) }, 15.0f );
			///return new ShapStroke( new Shape[] { new Star( 5, 0, 0, 0, 0.5f, 6.0f), new Ellipse2D.Float(0, 0, 4, 4) }, 15.0f );
		else if ( regex.equals("zigzag") )
			return new ZigzagStroke(new BasicStroke(2.0f),5.0f,10.0f);

		else
			return new BasicStroke(width);
	}
	
    public static Map<Object,Icon> getIconSet() {
		System.out.println("in getIconSet");
        Map<Object,Icon> icons = new HashMap<Object,Icon>();

        for (LineStyle def : values()) {
            LineTypeIcon icon = new LineTypeIcon(def.getStroke(5.0f), 
                                                 VisualPropertyIcon.DEFAULT_ICON_SIZE * 4, 
                                                 VisualPropertyIcon.DEFAULT_ICON_SIZE, 
												 def.name());
			System.out.println("created icon " + def);
            icons.put(def, icon);
        }

		System.out.println("about to return icons");
        return icons;
    }
}
