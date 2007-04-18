/*
 File: IconSupport.java

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
package cytoscape.visual.ui;

import cytoscape.visual.Arrow;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.Line;
import cytoscape.visual.LineType;
import cytoscape.visual.LineTypeDef;
import cytoscape.visual.NodeShape;
import static cytoscape.visual.NodeShape.DIAMOND;
import static cytoscape.visual.NodeShape.ELLIPSE;
import static cytoscape.visual.NodeShape.HEXAGON;
import static cytoscape.visual.NodeShape.OCTAGON;
import static cytoscape.visual.NodeShape.PARALLELOGRAM;
import static cytoscape.visual.NodeShape.RECT;
import static cytoscape.visual.NodeShape.ROUND_RECT;
import static cytoscape.visual.NodeShape.TRIANGLE;
import static cytoscape.visual.ui.ValueDisplayer.ARROW;
import static cytoscape.visual.ui.ValueDisplayer.LINETYPE;
import static cytoscape.visual.ui.ValueDisplayer.NODESHAPE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.net.URL;

import java.util.HashMap;

import javax.swing.ImageIcon;


/**
 * This used to be the MiscDialog class, but that was so poorly named, I had to
 * change it.  This class also contains support for getting an icon based on an
 * object or type (as defined in ValueDisplayer).
 */
public class IconSupport {
    private ImageIcon currentIcon;
    private ImageIcon[] icons;
    private HashMap sToI;
    private static Font defaultFont = new Font("SansSerif", Font.PLAIN, 8);

    /**
     * Creates a new IconSupport object.
     */
    public IconSupport() {
    }

    /**
     * Creates a new IconSupport object.
     *
     * @param o  DOCUMENT ME!
     */
    public IconSupport(Object o) {
        if (o instanceof Arrow)
            init(o, ARROW);
        else if (o instanceof NodeShape)
            init(o, NODESHAPE);
        else if (o instanceof LineType)
            init(o, LINETYPE);
    }

    /**
     * Creates a new IconSupport object.
     *
     * @param startObject  DOCUMENT ME!
     * @param type  DOCUMENT ME!
     */
    public IconSupport(Object startObject, byte type) {
        init(startObject, type);
    }

    private void init(Object startObject, byte type) {
        // get icons - cannot be done from a static context
        icons = null;
        sToI = null;

        HashMap iToS = null;

        switch (type) {
        case ARROW:
            icons = getArrowIcons();
            iToS = IconSupport.getArrowToStringHashMap(25);
            sToI = IconSupport.getStringToArrowHashMap(25);

            break;

        case NODESHAPE:
            icons = IconSupport.getShapeIcons();
            iToS = IconSupport.getShapeByteToStringHashMap();
            sToI = IconSupport.getStringToShapeByteHashMap();

            break;

        case LINETYPE:
            icons = IconSupport.getLineTypeIcons();
            iToS = IconSupport.getLineTypeToStringHashMap();
            sToI = IconSupport.getStringToLineTypeHashMap();

            break;
        }

        currentIcon = null;

        if (startObject != null) {
            // find the right icon
            String ltName = (String) iToS.get(startObject);
            int iconIndex = 0;

            for (; iconIndex < icons.length; iconIndex++) {
                if (icons[iconIndex].getDescription()
                                        .equals(ltName))
                    break;
            }

            if (iconIndex == icons.length) { // not found
                System.err.println("Icon for object " + startObject +
                    " not found!");
                iconIndex = 0;
            }

            currentIcon = icons[iconIndex];
        }
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ImageIcon[] getIcons() {
        return icons;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ImageIcon getCurrentIcon() {
        return currentIcon;
    }

    /**
     *  DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getIconType(ImageIcon i) {
        if (i != null)
            return sToI.get(i.getDescription());
        else

            return null;
    }

    /**
     *  arrow dialog functions:
     *
     *   getStringToArrowHashMap(int nodeSize)
     *   getArrowToStringHashMap(int nodeSize)
     *   getArrowIcons()
     *
     * Note: with the move to Giny, I'm dropping support for the scalable
     * arrow type; we can add this back later if desired. -AM 2003-10-28
     */
    public static HashMap getStringToArrowHashMap(int nodeSize) {
        HashMap<String, Object> h = new HashMap<String, Object>();

        h.put("NONE", Arrow.NONE);

        h.put("COLOR_DIAMOND", Arrow.DIAMOND);
        h.put("COLOR_DELTA", Arrow.DELTA);
        h.put("COLOR_T", Arrow.T);
        h.put("COLOR_CIRCLE", Arrow.CIRCLE);
        h.put("COLOR_ARROW", Arrow.ARROW);

        return h;
    }

    /**
     *  DOCUMENT ME!
     *
     * @param nodeSize DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HashMap getArrowToStringHashMap(int nodeSize) {
        HashMap<Object, String> h = new HashMap<Object, String>();
        h.put(Arrow.NONE, "NONE");
        h.put(Arrow.DIAMOND, "COLOR_DIAMOND");
        h.put(Arrow.DELTA, "COLOR_DELTA");
        h.put(Arrow.CIRCLE, "COLOR_CIRCLE");
        h.put(Arrow.T, "COLOR_T");
        h.put(Arrow.ARROW, "COLOR_ARROW");

        return h;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon[] getArrowIcons() {
        final ImageIcon[] arrowIcons = new ImageIcon[6];

        arrowIcons[0] = new ImageIcon(
                locateImage("/cytoscape/images/edgeEnds/arrow_none.jpg"),
                "NONE");

        arrowIcons[1] = new ImageIcon(
                locateImage("/cytoscape/images/edgeEnds/COLOR_DIAMOND.jpg"),
                "COLOR_DIAMOND");

        arrowIcons[2] = new ImageIcon(
                locateImage("/cytoscape/images/edgeEnds/COLOR_DELTA.jpg"),
                "COLOR_DELTA");

        arrowIcons[3] = new ImageIcon(
                locateImage("/cytoscape/images/edgeEnds/COLOR_CIRCLE.jpg"),
                "COLOR_CIRCLE");

        arrowIcons[4] = new ImageIcon(
                locateImage("/cytoscape/images/edgeEnds/COLOR_ARROW.jpg"),
                "COLOR_ARROW");

        arrowIcons[5] = new ImageIcon(
                locateImage("/cytoscape/images/edgeEnds/COLOR_T.jpg"),
                "COLOR_T");

        return arrowIcons;
    }

    /**
     *  shape dialog functions:
     *
     *   getStringToShapeByteHashMap()
     *   getShapeByteToStringHashMap()
     *   getShapeIcons()
     *
     */
    public static HashMap getStringToShapeByteHashMap() {
        final HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("ELLIPSE", ELLIPSE);
        h.put("ROUND_RECT", ROUND_RECT);
        h.put("RECTANGLE", RECT);
        h.put("DIAMOND", DIAMOND);
        h.put("HEXAGON", HEXAGON);
        h.put("OCTAGON", OCTAGON);
        h.put("PARALLELOGRAM", PARALLELOGRAM);
        h.put("TRIANGLE", TRIANGLE);

        return h;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HashMap getShapeByteToStringHashMap() {
        HashMap<Object, String> h = new HashMap<Object, String>();
        h.put(ELLIPSE, "ELLIPSE");
        h.put(ROUND_RECT, "ROUND_RECT");
        h.put(RECT, "RECTANGLE");
        h.put(DIAMOND, "DIAMOND");
        h.put(HEXAGON, "HEXAGON");
        h.put(OCTAGON, "OCTAGON");
        h.put(PARALLELOGRAM, "PARALLELOGRAM");
        h.put(TRIANGLE, "TRIANGLE");

        return h;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon[] getShapeIcons() {
        ImageIcon[] shapeIcons = new ImageIcon[8]; // Array of icons for the list
        shapeIcons[0] = new ImageIcon(
                locateImage("images/ellipse.jpg"),
                "ELLIPSE");
        shapeIcons[1] = new ImageIcon(
                locateImage("images/round_rect.jpg"),
                "ROUND_RECT");
        shapeIcons[2] = new ImageIcon(
                locateImage("images/rect.jpg"),
                "RECTANGLE");
        shapeIcons[3] = new ImageIcon(
                locateImage("images/diamond.jpg"),
                "DIAMOND");
        shapeIcons[4] = new ImageIcon(
                locateImage("images/hexagon.jpg"),
                "HEXAGON");
        shapeIcons[5] = new ImageIcon(
                locateImage("images/octagon.jpg"),
                "OCTAGON");
        shapeIcons[6] = new ImageIcon(
                locateImage("images/parallelogram.jpg"),
                "PARALLELOGRAM");
        shapeIcons[7] = new ImageIcon(
                locateImage("images/triangle.jpg"),
                "TRIANGLE");

        return shapeIcons;
    }

    /**
     *  line type dialog functions:
     *
     *   getStringToLineTypeHashMap()
     *   getLineTypeToStringHashMap()
     *   getLineTypeIcons()
     *
     */
    public static HashMap getStringToLineTypeHashMap() {
        HashMap h = new HashMap();
        h.put(
            "DASHED_1",
            new Line(LineTypeDef.LONG_DASH, 1.0f));
        h.put(
            "DASHED_2",
            new Line(LineTypeDef.LONG_DASH, 2.0f));
        h.put(
            "DASHED_3",
            new Line(LineTypeDef.LONG_DASH, 3.0f));
        h.put(
            "DASHED_4",
            new Line(LineTypeDef.LONG_DASH, 4.0f));
        h.put(
            "DASHED_5",
            new Line(LineTypeDef.LONG_DASH, 5.0f));
        h.put(
            "LINE_1",
            new Line(LineTypeDef.SOLID, 1.0f));
        h.put(
            "LINE_2",
            new Line(LineTypeDef.SOLID, 2.0f));
        h.put(
            "LINE_3",
            new Line(LineTypeDef.SOLID, 3.0f));
        h.put(
            "LINE_4",
            new Line(LineTypeDef.SOLID, 4.0f));
        h.put(
            "LINE_5",
            new Line(LineTypeDef.SOLID, 5.0f));
        h.put(
            "LINE_6",
            new Line(LineTypeDef.SOLID, 6.0f));
        h.put(
            "LINE_7",
            new Line(LineTypeDef.SOLID, 7.0f));

        return h;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HashMap getLineTypeToStringHashMap() {
        HashMap h = new HashMap();
        h.put(
            new Line(LineTypeDef.LONG_DASH, 1.0f),
            "DASHED_1");
        h.put(
            new Line(LineTypeDef.LONG_DASH, 2.0f),
            "DASHED_2");
        h.put(
            new Line(LineTypeDef.LONG_DASH, 3.0f),
            "DASHED_3");
        h.put(
            new Line(LineTypeDef.LONG_DASH, 4.0f),
            "DASHED_4");
        h.put(
            new Line(LineTypeDef.LONG_DASH, 5.0f),
            "DASHED_5");
        h.put(
            new Line(LineTypeDef.SOLID, 1.0f),
            "LINE_1");
        h.put(
            new Line(LineTypeDef.SOLID, 2.0f),
            "LINE_2");
        h.put(
            new Line(LineTypeDef.SOLID, 3.0f),
            "LINE_3");
        h.put(
            new Line(LineTypeDef.SOLID, 4.0f),
            "LINE_4");
        h.put(
            new Line(LineTypeDef.SOLID, 5.0f),
            "LINE_5");
        h.put(
            new Line(LineTypeDef.SOLID, 6.0f),
            "LINE_6");
        h.put(
            new Line(LineTypeDef.SOLID, 7.0f),
            "LINE_7");

        return h;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon[] getLineTypeIcons() {
        ImageIcon[] lineTypeIcons = new ImageIcon[12]; // Array of icons for the list
        lineTypeIcons[0] = new ImageIcon(
                locateImage("images/line_1.jpg"),
                "LINE_1");
        lineTypeIcons[1] = new ImageIcon(
                locateImage("images/line_2.jpg"),
                "LINE_2");
        lineTypeIcons[2] = new ImageIcon(
                locateImage("images/line_3.jpg"),
                "LINE_3");
        lineTypeIcons[3] = new ImageIcon(
                locateImage("images/line_4.jpg"),
                "LINE_4");
        lineTypeIcons[4] = new ImageIcon(
                locateImage("images/line_5.jpg"),
                "LINE_5");
        lineTypeIcons[5] = new ImageIcon(
                locateImage("images/line_6.jpg"),
                "LINE_6");
        lineTypeIcons[6] = new ImageIcon(
                locateImage("images/line_7.jpg"),
                "LINE_7");
        lineTypeIcons[7] = new ImageIcon(
                locateImage("images/dashed_1.jpg"),
                "DASHED_1");
        lineTypeIcons[8] = new ImageIcon(
                locateImage("images/dashed_2.jpg"),
                "DASHED_2");
        lineTypeIcons[9] = new ImageIcon(
                locateImage("images/dashed_3.jpg"),
                "DASHED_3");
        lineTypeIcons[10] = new ImageIcon(
                locateImage("images/dashed_4.jpg"),
                "DASHED_4");
        lineTypeIcons[11] = new ImageIcon(
                locateImage("images/dashed_5.jpg"),
                "DASHED_5");

        return lineTypeIcons;
    }

    /**
     * Get the image from the .jar file
     */
    private static URL locateImage(String imageFilename) {
        // have to construct a dialog to get class from,
        // because otherwise the getClass method is not
        // static, and thus this function can't be static.
        IconSupport is = new IconSupport();

        return is.getClass()
                 .getResource(imageFilename);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon getColorIcon(Color c) {
        int size = 30;
        BufferedImage bi = new BufferedImage(size, size,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setPaint(c);
        g2.fillRect(0, 0, size, size);

        return new ImageIcon(bi);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon getNodeWidthIcon(Double d) {
        int w = d.intValue();
        int bound = 4;
        int edgeHeight = 4;
        int height = 30;

        String s = Integer.toString(w);

        BufferedImage bi = new BufferedImage(w + bound + bound, height,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(defaultFont);

        int stringWidth = g2.getFontMetrics()
                            .stringWidth(s);

        g2.setBackground(Color.white);
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, w + bound + bound, height);
        g2.setPaint(Color.black);

        g2.drawLine(bound, (height / 2) + edgeHeight, bound,
            (height / 2) - edgeHeight);
        g2.drawLine(w + bound, (height / 2) + edgeHeight, w + bound,
            (height / 2) - edgeHeight);
        g2.drawLine(bound, height / 2, w + bound, height / 2);
        g2.setPaint(Color.black);
        g2.drawString(s, ((w / 2) + bound) - (stringWidth / 2),
            (height / 2) - edgeHeight);

        return new ImageIcon(bi);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon getNodeHeightIcon(Double d) {
        int h = d.intValue();
        int bound = 4;
        int edgeWidth = 4;
        int width = 40;

        String s = Integer.toString(h);

        BufferedImage bi = new BufferedImage(width, h + bound + bound,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(defaultFont);

        int stringHeight = g2.getFontMetrics()
                             .getMaxAscent();

        g2.setBackground(Color.white);
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, width, h + bound + bound);
        g2.setPaint(Color.black);

        g2.drawLine(bound, bound, bound + edgeWidth + edgeWidth, bound);
        g2.drawLine(bound, h + bound, bound + edgeWidth + edgeWidth, h + bound);
        g2.drawLine(bound + edgeWidth, bound, bound + edgeWidth, h + bound);
        g2.setPaint(Color.black);
        g2.drawString(s, bound + edgeWidth + edgeWidth,
            ((h + bound + bound) / 2) + (stringHeight / 2));

        return new ImageIcon(bi);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon getNodeSizeIcon(Double d) {
        int size = d.intValue();

        int bound = 4;

        String s = Integer.toString(size);

        BufferedImage bi = new BufferedImage(size + bound + bound,
                size + bound + bound, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(defaultFont);

        g2.setBackground(Color.white);
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, size + bound + bound, size + bound + bound);
        g2.setPaint(Color.black);

        g2.drawLine(bound, bound, bound, size + bound);
        g2.drawLine(bound, size + bound, size + bound, size + bound);
        g2.setPaint(Color.black);
        g2.drawString(s, bound + bound, size);

        return new ImageIcon(bi);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param pos DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon getLabelPositionIcon(LabelPosition pos) {
        int size = 60;

        BufferedImage bi = new BufferedImage(size, size,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();

        LabelPlacerGraphic lp = new LabelPlacerGraphic(pos, size, false);
        lp.paint(g2);

        return new ImageIcon(bi);
    }
}
