/**
 * Chart2D, a java library for drawing two dimensional charts.
 * Copyright (C) 2001 Jason J. Simas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author of this library may be contacted at:
 * E-mail:  jjsimas@users.sourceforge.net
 * Street Address:  J J Simas, 887 Tico Road, Ojai, CA 93023-3555 USA
 */


package net.sourceforge.chart2d;


import java.awt.*;
import java.awt.geom.*;
import java.util.*;


/**
 * A line capable of gradient paint and warning region coloring.
 */
final class FancyLine extends FancyShape {


  private GeneralPath line;
  private BasicStroke stroke;
  private BasicStroke outlineStroke;
  private float thickness;
  private boolean fillArea;
  private Rectangle2D.Float clipBounds;
  private FancyLine[] warningLines;
  private boolean needsUpdate;


  /**
   * Creates a default FancyLine object.
   */
  FancyLine() {
    needsUpdate = true;
    line = new GeneralPath (GeneralPath.WIND_EVEN_ODD);
  }


  /**
   * Sets the thickness of this line.
   * @param t The line thickness.
   */
  final void setThickness (float t) {
    thickness = t;
    needsUpdate = true;
  }


  /**
   * Sets whether the bounds of this line intersected with the graph bounds will be filled.
   * @param f If true, then filled.
   */
  final void setFillArea (boolean f) {
    fillArea = f;
  }


  /**
   * Sets the general path of this line.
   * @param l The general path.
   */
  final void setLine (GeneralPath l) {
    needsUpdate = true;
    line = l;
  }


  /**
   * Sets the clipping bounds (specifying which parts of the line to paint).
   * @param b The clipping bounds.
   */
  final void setClipBounds (Rectangle2D.Float b) {
    needsUpdate = true;
    clipBounds = b;
  }


  /**
   * Gets the thickness of this line.
   * return The line thickness.
   */
  final float getThickness() {
    return thickness;
  }


  /**
   * Gets whether the bounds of this line intersected with the graph bounds will be filled.
   * @return If true, then filled.
   */
  final boolean getFillArea() {
    return fillArea;
  }


  /**
   * Gets the general path of this line.
   * @return The general path.
   */
  final GeneralPath getLine() {
    return line;
  }


  /**
   * Gets the clipping bounds (specifying which parts of the line to paint).
   * @return The clipping bounds.
   */
  final Rectangle2D.Float getClipBounds() {
    return clipBounds;
  }


  /**
   * Paints the line on the Graphics2D object after calling update.
   * @param g2D The Graphics2D object.
   */
  final void paint (Graphics2D g2D) {

    update();

    Stroke oldStroke = g2D.getStroke();
    Paint oldPaint = g2D.getPaint();
    Shape oldClip = g2D.getClip();

    java.awt.geom.Area clipArea = new java.awt.geom.Area (clipBounds);
    clipArea.intersect (new java.awt.geom.Area (oldClip));
    g2D.setClip (clipArea);

    if (getOutlineExistence() && thickness > 2f) {
      g2D.setStroke (outlineStroke);
      g2D.setPaint (getOutlinePaint());
      g2D.draw (line);
    }

    g2D.setPaint (getPaint());
    g2D.setStroke (stroke);
    if (fillArea) g2D.fill (line);
    else g2D.draw (line);

    g2D.setPaint (oldPaint);
    g2D.setClip (oldClip);
    g2D.setStroke(oldStroke);

    if (warningLines != null) {
      for (int i = 0; i < warningLines.length; ++i) warningLines[i].paint (g2D);
    }
  }


  /**
   * Updates the FancyLine.
   */
  final void update() {

    super.update();

    if (needsUpdate) {

      outlineStroke = new BasicStroke ((float)thickness,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, Area.CONTINUOUS, 0.0f);
      float tempThickness = thickness > 2f  && getOutlineExistence() ? thickness - 2f : thickness;
      stroke = new BasicStroke ((float)tempThickness,
          BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, Area.CONTINUOUS, 0.0f);

      if (getWarningRegions() != null) {

        warningLines = new FancyLine[getWarningRegions().size()];
        for (int i = 0; i < warningLines.length; ++i) {

          FancyLine warningLine = new FancyLine();

          WarningRegion warningRegion = (WarningRegion)getWarningRegions().get(i);
          java.awt.geom.Area clipArea = new java.awt.geom.Area (clipBounds);
          java.awt.geom.Area wrArea =
            new java.awt.geom.Area (warningRegion.getBackgroundBounds());
          clipArea.intersect (wrArea);
          Rectangle2D.Float wrClipBounds = new Rectangle2D.Float();
          wrClipBounds.setRect (clipArea.getBounds2D());
          warningLine.setClipBounds (wrClipBounds);
          warningLine.setFillArea (getFillArea());
          warningLine.setThickness (getThickness());
          warningLine.setColor (warningRegion.getComponentColor());
          warningLine.setLightBounds (getLightBounds());
          warningLine.setLightSource (getLightSource());
          warningLine.setOutlineColor (getOutlineColor());
          warningLine.setOutlineExistence (getOutlineExistence());
          warningLine.setType (getType());
          warningLine.setWarningRegions (null);
          warningLine.setLine (line);
          warningLines[i] = warningLine;
        }
      }
      needsUpdate = false;
    }
  }
}