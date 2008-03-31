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
 * A circle capable of automatic gradient paint and warning region coloring.
 */
final class FancyDot extends FancyShape {


  private Rectangle2D.Float bounds, clipBounds;
  private Ellipse2D.Float dot;
  private FancyDot[] warningDots;
  private boolean needsUpdate;


  /**
   * Creates a default fancy dot.
   */
  FancyDot() {
    needsUpdate = true;
  }


  /**
   * Sets the bounds for this bar (specifying the size and location of the bar).
   * @param b The bounds.
   */
  final void setBounds (Rectangle2D.Float b) {
    bounds = b;
    needsUpdate = true;
  }


  /**
   * Sets the clip bounds for this bar (specifying what will show).
   * @param b The bounds.
   */
  final void setClipBounds (Rectangle2D.Float b) {
    clipBounds = b;
    needsUpdate = true;
  }


  /**
   * Gets the bounds for this bar (specifying the size and location of the bar).
   * @return The bounds.
   */
  final Rectangle2D.Float getBounds() {
    return bounds;
  }


  /**
   * Gets the clip bounds for this bar (specifying what will show).
   * @return The bounds.
   */
  final Rectangle2D.Float getClipBounds() {
    return clipBounds;
  }


  /**
   * Paints the dot on the Graphics2D object after calling update.
   * @param g2D The Graphics2D object.
   */
  final void paint (Graphics2D g2D) {

    update();

    Paint oldPaint = g2D.getPaint();
    g2D.setPaint (getPaint());

    Shape oldClip = g2D.getClip();
    java.awt.geom.Area clipArea = new java.awt.geom.Area (clipBounds);
    clipArea.intersect (new java.awt.geom.Area (oldClip));
    g2D.setClip (clipArea);

    g2D.fill (dot);

    if (getOutlineExistence() && bounds.width > 2f && bounds.height > 2f) {
      g2D.setPaint (getOutlinePaint());
      g2D.draw (dot);
    }

    g2D.setPaint (oldPaint);
    g2D.setClip (oldClip);

    if (getWarningRegions() != null) {
      for (int i = 0; i < warningDots.length; ++i) warningDots[i].paint (g2D);
    }
  }


  /**
   * Updates the FancyDot.
   */
  final void update() {

    super.update();

    if (needsUpdate) {

      dot = new Ellipse2D.Float (bounds.x, bounds.y, bounds.width, bounds.height);

      if (getWarningRegions() != null) {

        warningDots = new FancyDot[getWarningRegions().size()];
        for (int i = 0; i < warningDots.length; ++i) {

          FancyDot warningDot = new FancyDot();
          warningDot.setBounds (bounds);
          warningDot.setWarningRegions (null);
          warningDot.setLightBounds (getLightBounds());
          warningDot.setLightSource (getLightSource());
          warningDot.setOutlineExistence (getOutlineExistence());
          warningDot.setOutlineColor (getOutlineColor());
          warningDot.setType (getType());
          warningDot.setGraphBounds (getGraphBounds());
          WarningRegion warningRegion = (WarningRegion)getWarningRegions().get (i);
          Rectangle2D tempRect2D = bounds.createIntersection (warningRegion.getBackgroundBounds());
          Rectangle2D.Float clipBoundsWR = new Rectangle2D.Float();
          clipBoundsWR.setRect (tempRect2D);
          warningDot.setClipBounds (clipBoundsWR);
          warningDot.setColor (warningRegion.getComponentColor());
          warningDots[i] = warningDot;
        }
      }
      needsUpdate = false;
    }
  }
}