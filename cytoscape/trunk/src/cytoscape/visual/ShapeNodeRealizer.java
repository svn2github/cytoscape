/*
  File: ShapeNodeRealizer.java

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


/**
 * This class is a replacement for the yFiles ShapeNodeRealizer class.
 * It defines byte constants specifying shape types.<br>
 *
 * @deprecated Will be removed 5/2008. Use enum NodeShape instead.
 *
 */
@Deprecated
public class ShapeNodeRealizer {
    /**
     *
     */
    public static final byte RECT = (byte) 0;

    /**
     *
     */
    public static final byte ROUND_RECT = (byte) 1;

    /**
     *
     */
    public static final byte RECT_3D = (byte) 2;

    /**
     *
     */
    public static final byte TRAPEZOID = (byte) 3;

    /**
     *
     */
    public static final byte TRAPEZOID_2 = (byte) 4;

    /**
     *
     */
    public static final byte TRIANGLE = (byte) 5;

    /**
     *
     */
    public static final byte PARALLELOGRAM = (byte) 6;

    /**
     *
     */
    public static final byte DIAMOND = (byte) 7;

    /**
     *
     */
    public static final byte ELLIPSE = (byte) 8;

    /**
     *
     */
    public static final byte HEXAGON = (byte) 9;

    /**
     *
     */
    public static final byte OCTAGON = (byte) 10;

    /**
     *  DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Byte parseNodeShapeTextIntoByte(String text) {
        return new Byte(parseNodeShapeText(text));
    }

    /**
     *  DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static byte parseNodeShapeText(String text) {
        final NodeShape shape = NodeShape.parseNodeShapeText(text);

        if (shape == null)
            return ShapeNodeRealizer.RECT;

        return (byte) shape.ordinal();
    }

    /**
     *  DOCUMENT ME!
     *
     * @param shape DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getNodeShapeText(byte shape) {
        if (shape == RECT)
            return "rect";

        if (shape == ROUND_RECT)
            return "roundrect";

        if (shape == RECT_3D)
            return "rect3d";

        if (shape == TRAPEZOID)
            return "trapezoid";

        if (shape == TRAPEZOID_2)
            return "trapezoid2";

        if (shape == TRIANGLE)
            return "triangle";

        if (shape == PARALLELOGRAM)
            return "parallelogram";

        if (shape == DIAMOND)
            return "diamond";

        if (shape == ELLIPSE)
            return "ellipse";

        if (shape == HEXAGON)
            return "hexagon";

        if (shape == OCTAGON)
            return "octagon";

        return "rect";
    }

    /**
     *  DOCUMENT ME!
     *
     * @param byteShape DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static int getGinyShape(byte byteShape) {
        final NodeShape shape = getNodeShape(byteShape);

        return shape.getGinyShape();
    }

    /**
     *  DOCUMENT ME!
     *
     * @param shape DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isValidShape(byte shape) {
        return NodeShape.isValidShape(getNodeShape(shape));
    }

    /**
     * This method is for backward compatibility.
     *
     * @param oldShape
     * @return
     */
    public static NodeShape getNodeShape(byte oldShape) {
        String shapeString = getNodeShapeText(oldShape);

        return NodeShape.parseNodeShapeText(shapeString);
    }
}
