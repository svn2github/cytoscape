/*
 File: Arrow.java

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

import giny.view.EdgeView;

import java.awt.Color;

import java.io.Serializable;


/**
 * This class is a replacement for the yFiles Arrow class.
 */
public class Arrow
    implements Serializable {
    private static final Color DEFAULT_ARROW_COLOR = Color.BLACK;

    /**
     * DOCUMENT ME!
     */
    public static final Arrow NONE = new Arrow(ArrowShape.NONE,
            DEFAULT_ARROW_COLOR);

    /**
     * DOCUMENT ME!
     */
    public static final Arrow DIAMOND = new Arrow(ArrowShape.DIAMOND,
            DEFAULT_ARROW_COLOR);

    /**
     * DOCUMENT ME!
     */
    public static final Arrow DELTA = new Arrow(ArrowShape.DELTA,
            DEFAULT_ARROW_COLOR);

    /**
     * DOCUMENT ME!
     */
    public static final Arrow ARROW = new Arrow(ArrowShape.ARROW,
            DEFAULT_ARROW_COLOR);

    /**
     * DOCUMENT ME!
     */
    public static final Arrow T = new Arrow(ArrowShape.T, DEFAULT_ARROW_COLOR);

    /**
     * DOCUMENT ME!
     */
    public static final Arrow CIRCLE = new Arrow(ArrowShape.CIRCLE,
            DEFAULT_ARROW_COLOR);

    /*
     * Following arrow types will not be used after 2.5.
     */
    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow COLOR_DIAMOND = DIAMOND;

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow COLOR_DELTA = DELTA;

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow COLOR_ARROW = ARROW;

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow COLOR_T = T;

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow COLOR_CIRCLE = CIRCLE;

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow BLACK_DIAMOND = new Arrow(ArrowShape.DIAMOND,
            Color.BLACK);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow WHITE_DIAMOND = new Arrow(ArrowShape.DIAMOND,
            Color.WHITE);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow BLACK_DELTA = new Arrow(ArrowShape.DELTA,
            Color.BLACK);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow WHITE_DELTA = new Arrow(ArrowShape.DELTA,
            Color.WHITE);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow BLACK_ARROW = new Arrow(ArrowShape.ARROW,
            Color.BLACK);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow WHITE_ARROW = new Arrow(ArrowShape.ARROW,
            Color.WHITE);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow BLACK_T = new Arrow(ArrowShape.T, Color.BLACK);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow WHITE_T = new Arrow(ArrowShape.T, Color.WHITE);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow BLACK_CIRCLE = new Arrow(ArrowShape.CIRCLE,
            Color.BLACK);

    /**
     * DOCUMENT ME!
     */
    @Deprecated
    public static final Arrow WHITE_CIRCLE = new Arrow(ArrowShape.CIRCLE,
            Color.WHITE);

    /*
     * New in 2.5: Arrow can have arbitrary color. This can be different from
     */
    private Color arrowColor;
    private ArrowShape shape;

    // Maybe supported
    private int size;
    @Deprecated
    private String name;

    /**
     * New constructor for 2.5 and later:<br>
     *
     * @param shape
     * @param arrowColor
     */
    public Arrow(ArrowShape shape, Color arrowColor) {
        this.shape = shape;
        this.arrowColor = arrowColor;
    }

    /**
     * Creates a new Arrow object.
     *
     * @param name DOCUMENT ME!
     */
    @Deprecated
    public Arrow(String name) {
        this.name = name;
        this.arrowColor = DEFAULT_ARROW_COLOR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Deprecated
    public int getGinyArrow() {
        if (name.equals("WHITE_DIAMOND"))
            return EdgeView.WHITE_DIAMOND;
        else if (name.equals("BLACK_DIAMOND"))
            return EdgeView.BLACK_DIAMOND;
        else if (name.equals("COLOR_DIAMOND"))
            return EdgeView.EDGE_COLOR_DIAMOND;

        else if (name.equals("WHITE_DELTA"))
            return EdgeView.WHITE_DELTA;
        else if (name.equals("BLACK_DELTA"))
            return EdgeView.BLACK_DELTA;
        else if (name.equals("COLOR_DELTA"))
            return EdgeView.EDGE_COLOR_DELTA;

        else if (name.equals("WHITE_ARROW"))
            return EdgeView.WHITE_ARROW;
        else if (name.equals("BLACK_ARROW"))
            return EdgeView.BLACK_ARROW;
        else if (name.equals("COLOR_ARROW"))
            return EdgeView.EDGE_COLOR_ARROW;

        else if (name.equals("WHITE_T"))
            return EdgeView.WHITE_T;
        else if (name.equals("BLACK_T"))
            return EdgeView.BLACK_T;
        else if (name.equals("COLOR_T"))
            return EdgeView.EDGE_COLOR_T;

        else if (name.equals("WHITE_CIRCLE"))
            return EdgeView.WHITE_CIRCLE;
        else if (name.equals("BLACK_CIRCLE"))
            return EdgeView.BLACK_CIRCLE;
        else if (name.equals("COLOR_CIRCLE"))
            return EdgeView.EDGE_COLOR_CIRCLE;

        else
            return EdgeView.NO_END;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrowShape getShape() {
        return shape;
    }

    /**
     * DOCUMENT ME!
     *
     * @param newShape DOCUMENT ME!
     */
    public void setShape(ArrowShape newShape) {
        this.shape = newShape;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getColor() {
        return arrowColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param newColor DOCUMENT ME!
     */
    public void setColor(Color newColor) {
        this.arrowColor = newColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Deprecated
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return shape.getGinyName();
    }

    /**
     * DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Arrow parseArrowText(String text) {
        String arrowtext = text.trim();

        if (arrowtext.equals("WHITE_DIAMOND"))
            return new Arrow(ArrowShape.DIAMOND, Color.white);
        else if (arrowtext.equals("BLACK_DIAMOND"))
            return DIAMOND;
        else if (arrowtext.equals("COLOR_DIAMOND"))
            return DIAMOND;

        else if (arrowtext.equals("WHITE_DELTA"))
            return new Arrow(ArrowShape.DELTA, Color.white);
        else if (arrowtext.equals("BLACK_DELTA"))
            return DELTA;
        else if (arrowtext.equals("COLOR_DELTA"))
            return DELTA;

        else if (arrowtext.equals("WHITE_ARROW"))
            return new Arrow(ArrowShape.ARROW, Color.white);
        else if (arrowtext.equals("BLACK_ARROW"))
            return ARROW;
        else if (arrowtext.equals("COLOR_ARROW"))
            return ARROW;

        else if (arrowtext.equals("WHITE_T"))
            return new Arrow(ArrowShape.T, Color.white);
        else if (arrowtext.equals("BLACK_T"))
            return T;
        else if (arrowtext.equals("COLOR_T"))
            return T;

        else if (arrowtext.equals("WHITE_CIRCLE"))
            return new Arrow(ArrowShape.CIRCLE, Color.white);
        else if (arrowtext.equals("BLACK_CIRCLE"))
            return CIRCLE;
        else if (arrowtext.equals("COLOR_CIRCLE"))
            return CIRCLE;

        else
            return Arrow.NONE;
    } // parseArrowText
}
