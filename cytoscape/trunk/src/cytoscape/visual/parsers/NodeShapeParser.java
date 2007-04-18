/*
  File: NodeShapeParser.java

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
package cytoscape.visual.parsers;


//----------------------------------------------------------------------------
import cytoscape.visual.NodeShape;
import cytoscape.visual.ShapeNodeRealizer;


//----------------------------------------------------------------------------
/**
 * Parses a String into a yFiles shape, which is represented by a byte
 * identifier. The return value here is a Byte object wrapping the
 * primitive byte identifier.
 */
public class NodeShapeParser
    implements ValueParser {
    /**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object parseStringValue(String value) {
        return parseNodeShapeEnum(value);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public Byte parseNodeShape(String value) {
        return ShapeNodeRealizer.parseNodeShapeTextIntoByte(value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeShape parseNodeShapeEnum(String value) {
        return NodeShape.parseNodeShapeText(value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param shape DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean isValidShape(NodeShape shape) {
        return NodeShape.isValidShape(shape);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param shape DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public static boolean isValidShape(byte shape) {
        final NodeShape nShape = ShapeNodeRealizer.getNodeShape(shape);

        return NodeShape.isValidShape(nShape);
    }
}
