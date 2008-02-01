/*
  File: LineType.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import java.awt.BasicStroke;
import java.awt.Stroke;

//----------------------------------------------------------------------------
import java.io.Serializable;


//----------------------------------------------------------------------------
/**
 * This class is a replacement for the yFiles LineType class.
 */
@Deprecated
public class LineType
    implements Serializable {
    /**
     *
     */
    public static final LineType LINE_1 = new LineType("LINE_1");

    /**
     *
     */
    public static final LineType LINE_2 = new LineType("LINE_2");

    /**
     *
     */
    public static final LineType LINE_3 = new LineType("LINE_3");

    /**
     *
     */
    public static final LineType LINE_4 = new LineType("LINE_4");

    /**
     *
     */
    public static final LineType LINE_5 = new LineType("LINE_5");

    /**
     *
     */
    public static final LineType LINE_6 = new LineType("LINE_6");

    /**
     *
     */
    public static final LineType LINE_7 = new LineType("LINE_7");

    /**
     *
     */
    public static final LineType DASHED_1 = new LineType("DASHED_1");

    /**
     *
     */
    public static final LineType DASHED_2 = new LineType("DASHED_2");

    /**
     *
     */
    public static final LineType DASHED_3 = new LineType("DASHED_3");

    /**
     *
     */
    public static final LineType DASHED_4 = new LineType("DASHED_4");

    /**
     *
     */
    public static final LineType DASHED_5 = new LineType("DASHED_5");
    String name;
    Stroke stroke;

    // Define line type (stroke).
    private LineStyle type;

    // Width of this line.
    private Float width;

    /**
     * Creates a new LineType object.
     *
     * @param name  DOCUMENT ME!
     */
    public LineType(String name) {
        this.name = name;
		this.type = LineStyle.SOLID;

        if (name.equals("LINE_2"))
            width = 2.0f;
        else if (name.equals("LINE_3"))
            width = 3.0f;
        else if (name.equals("LINE_4"))
            width = 4.0f;
        else if (name.equals("LINE_5"))
            width = 5.0f;
        else if (name.equals("LINE_6"))
            width = 6.0f;
        else if (name.equals("LINE_7"))
            width = 7.0f;
        else if (name.equals("DASHED_1")) {
            width = 1.0f;
			type = LineStyle.LONG_DASH;
        } else if (name.equals("DASHED_2")) {
            width = 2.0f;
			type = LineStyle.LONG_DASH;
        } else if (name.equals("DASHED_3")) {
            width = 3.0f;
			type = LineStyle.LONG_DASH;
        } else if (name.equals("DASHED_4")) {
            width = 4.0f;
			type = LineStyle.LONG_DASH;
        } else if (name.equals("DASHED_5")) {
            width = 5.0f;
			type = LineStyle.LONG_DASH;
        } else
            width = 1.0f;

		stroke = makeStroke();
    }

    /**
     * This method converts the names of linetypes, such as from a
     * visual mappings properties file, to a LineType object.
     */
    public static LineType parseLineTypeText(String text) {
        String lttext = text.trim();
        lttext = lttext.replaceAll("_", ""); // ditch all underscores

        if (lttext.equalsIgnoreCase("dashed1"))
            return LineType.DASHED_1;
        else if (lttext.equalsIgnoreCase("dashed2"))
            return LineType.DASHED_2;
        else if (lttext.equalsIgnoreCase("dashed3"))
            return LineType.DASHED_3;
        else if (lttext.equalsIgnoreCase("dashed4"))
            return LineType.DASHED_4;
        else if (lttext.equalsIgnoreCase("dashed5"))
            return LineType.DASHED_5;
        else if (lttext.equalsIgnoreCase("line1"))
            return LineType.LINE_1;
        else if (lttext.equalsIgnoreCase("line2"))
            return LineType.LINE_2;
        else if (lttext.equalsIgnoreCase("line3"))
            return LineType.LINE_3;
        else if (lttext.equalsIgnoreCase("line4"))
            return LineType.LINE_4;
        else if (lttext.equalsIgnoreCase("line5"))
            return LineType.LINE_5;
        else if (lttext.equalsIgnoreCase("line6"))
            return LineType.LINE_6;
        else if (lttext.equalsIgnoreCase("line7"))
            return LineType.LINE_7;
        else
            return LineType.LINE_1;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return name;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String toString() {
        return getName();
    }

    /**
     *  DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean equals(Object o) {
        if (o instanceof LineType) {
            LineType other = (LineType) o;

            if (this.name.equals(other.getName()))
                return true;
            else
                return false;
        } else
            return false;
    }

    private Stroke makeStroke() {
		if ( type == LineStyle.LONG_DASH ) { 
        	float[] dash = { 5.0f, 3.0f };
        	return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f);
		} else {
			return new BasicStroke(width);
		}
    }


    public LineType(LineStyle type, Float width) {
        this.type = type;
        this.width = width;
		this.name = type.toString();
		this.stroke = type.getStroke(width);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float getWidth() {
        return width;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public LineStyle getType() {
        return type;
    }

}
