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
 * A rectangle capable of automatic gradient paint, rounded corners on any single side,
 * and warning region coloring.
 */
final class FancyBar extends FancyShape {


  private Rectangle2D.Float bounds, clipBounds;
  private float arcw, arch;
  private RoundRectangle2D.Float bar;
  private FancyBar[] warningBars;
  private int dataSign, baseValue;
  private boolean needsUpdate;


  /**
   * Values for x, y, width, height, and roundSide needs to be set before use.
   * Default rounding is squuare.
   */
  FancyBar() {
    needsUpdate = true;
  }


  /**
   * Sets the value at which the bar should not begin for that end should not be rounded.
   * @param value The base value.
   */
  final void setBaseValue (int value) {
    baseValue = value;
    needsUpdate = true;
  }


  /**
   * Gets the value at which the bar should not begin for that end should not be rounded.
   * @return The base value.
   */
  final int getBaseValue() {
    return baseValue;
  }


  /**
   * Sets which kinds of the data is graphed.
   * Uses fields of GraphArea.
   * Use POS for non-negative data.
   * Use NEG for non-positive data.
   * Use MIX for both positive and negative data.
   * @param sign The sign of the data.
   */
  final void setDataSign (int sign) {

    dataSign = sign;
    needsUpdate = true;
  }


  /**
   * Gets which kinds of the data is graphed.
   * Uses fields of GraphArea.
   * Use POS for non-negative data.
   * Use NEG for non-positive data.
   * Use MIX for both positive and negative data.
   * @return The sign of the data.
   */
  final int getDataSign() {
    return dataSign;
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
   * Sets the radius of the (width) arc for the bar rounding.
   * @param w The width arc radius.
   */
  final void setArcw (float w) {
    arcw = w;
    needsUpdate = true;
  }


  /*
   * Sets the radius of the (height) arc for the bar rounding.
   * @param h The height arc radius.
   */
  final void setArch (float h) {
    arch = h;
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
   * Gets the radius of the (width) arc for the bar rounding.
   * @return The width arc radius.
   */
  final float getArcw() {
    return arcw;
  }


  /*
   * Gets the radius of the (height) arc for the bar rounding.
   * @return The height arc radius.
   */
  final float getArch() {
    return arch;
  }


  /**
   * Paints the bar on the Graphics2D object after calling update.
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

    g2D.fill (bar);

    if (getOutlineExistence() && bounds.width > 2f && bounds.height > 2f) {

      g2D.setPaint (getOutlinePaint());
      g2D.draw (bar);
    }

    g2D.setPaint (oldPaint);
    g2D.setClip (oldClip);

    if (getWarningRegions() != null) {
      for (int i = 0; i < warningBars.length; ++i) warningBars[i].paint (g2D);
    }
  }


  /**
   * Updates the FancyBar.
   */
  final void update() {

    super.update();

    if (needsUpdate) {

      if (getType() == LABELSLEFT) {

        if (dataSign == GraphArea.POS) {
          bar = new RoundRectangle2D.Float (
            bounds.x - (arcw / 2f), bounds.y,
            bounds.width  + (arcw / 2f), bounds.height, arcw, arch);
        }
        else if (dataSign == GraphArea.NEG) {
          bar = new RoundRectangle2D.Float (
            bounds.x, bounds.y,
            bounds.width  + (arcw / 2f), bounds.height, arcw, arch);
        }
        else {

          if (bounds.x >= baseValue) {
            bar = new RoundRectangle2D.Float (
              bounds.x - (arcw / 2f), bounds.y,
              bounds.width  + (arcw / 2f), bounds.height, arcw, arch);
          }
          else {
            bar = new RoundRectangle2D.Float (
              bounds.x, bounds.y,
              bounds.width  + (arcw / 2f), bounds.height, arcw, arch);
          }
        }
      }
      else { //LABELSBOTTOM

        if (dataSign == GraphArea.POS) {
          bar = new RoundRectangle2D.Float (
            bounds.x, bounds.y,
            bounds.width, bounds.height + (arch / 2f), arcw, arch);
        }
        else if (dataSign == GraphArea.NEG) {
          bar = new RoundRectangle2D.Float (
            bounds.x, bounds.y - (arch / 2f),
            bounds.width, bounds.height + (arch / 2f), arcw, arch);
        }
        else {

          if (baseValue <= bounds.y) {
            bar = new RoundRectangle2D.Float (
              bounds.x, bounds.y - (arch / 2f),
              bounds.width, bounds.height + (arch / 2f), arcw, arch);
          }
          else {
            bar = new RoundRectangle2D.Float (
              bounds.x, bounds.y, bounds.width, bounds.height + (arch / 2f), arcw, arch);
          }
        }
      }

      if (getWarningRegions() != null) {

        warningBars = new FancyBar[getWarningRegions().size()];
        for (int i = 0; i < warningBars.length; ++i) {

          FancyBar warningBar = new FancyBar();
          warningBar.setBounds (bounds);
          warningBar.setArcw (arcw);
          warningBar.setArch (arch);
          warningBar.setDataSign (dataSign);
          warningBar.setBaseValue (baseValue);
          warningBar.setWarningRegions (null);
          warningBar.setLightBounds (getLightBounds());
          warningBar.setLightSource (getLightSource());
          warningBar.setOutlineExistence (getOutlineExistence());
          warningBar.setOutlineColor (getOutlineColor());
          warningBar.setType (getType());
          warningBar.setGraphBounds (getGraphBounds());
          WarningRegion warningRegion = (WarningRegion)getWarningRegions().get (i);
          Rectangle2D tempRect2D = bounds.createIntersection (warningRegion.getBackgroundBounds());
          Rectangle2D.Float clipBoundsWR = new Rectangle2D.Float();
          clipBoundsWR.setRect (tempRect2D);
          warningBar.setClipBounds (clipBoundsWR);
          warningBar.setColor (warningRegion.getComponentColor());
          warningBars[i] = warningBar;
        }
      }
      needsUpdate = false;
    }
  }
}