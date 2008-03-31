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
 * An abstract class holding the properties and logic shared by all fancy shapes.
 */
abstract class FancyShape {


  /**
   * Indicates left.
   */
  static final int LEFT = 0;

  /**
   * Indicates right.
   */
  static final int RIGHT = 1;

  /**
   * Indicates top.
   */
  static final int TOP = 2;

  /**
   * Indicates bottom.
   */
  static final int BOTTOM = 3;

  /**
   * Indicates none.
   */
  static final int NONE = 6;

  /**
   * Indicates all.
   */
  static final int ALL = 7;

  /**
   * Indicates labels bottom.
   */
  static final int LABELSBOTTOM = 0;

  /**
   * Indicates labels left.
   */
  static final int LABELSLEFT = 1;


  private Rectangle2D.Float lightBounds;
  private int lightSource;
  private Color color, outlineColor;
  private Paint paint, outlinePaint;
  private boolean outlineExistence;
  private Vector warningRegions;
  private Vector warningPaints;
  private int type;
  private Rectangle2D.Float graphBounds;
  private boolean needsUpdate;


  /**
   * Creates a default FancyShape object.
   */
  FancyShape() {
    needsUpdate = true;
    warningPaints = new Vector (24, 5);
  }


  /**
   * Sets the bounds for the lighting effect.
   * Allows for single lighting effect to be applied to multiple FancyShape objects.
   * @param b The lighting effect bounds.
   */
  final void setLightBounds (Rectangle2D.Float b) {
    lightBounds = b;
    needsUpdate = true;
  }


  /**
   * Sets the source of light for the lighting effect.
   * On the side of the source, the color of the shape is brighter.
   * @param s The light source.
   */
  final void setLightSource (int s) {
    lightSource = s;
    needsUpdate = true;
  }


  /**
   * Sets the color of the shape.
   * @param c The color.
   */
  final void setColor (Color c) {
    color = c;
    needsUpdate = true;
  }


  /**
   * Sets whether the shape is outlined by a one pixel curve.
   * @param e If true, then outlined.
   */
  final void setOutlineExistence (boolean e) {
    outlineExistence = e;
    needsUpdate = true;
  }


  /**
   * Sets the color of the shape's outline if it exists.
   * @param c The outline color.
   */
  final void setOutlineColor (Color c) {
    outlineColor = c;
    needsUpdate = true;
  }


  /**
   * Sets the type of the chart the shape is being used in.
   * Uses fields LABELSLEFT and LABELSBOTTOM.
   * @param t The type.
   */
  final void setType (int t) {
    type = t;
    needsUpdate = true;
  }


  /**
   * Sets the vector of WarningRegion objects that affect this object's painting.
   * @param r The WarningRegion objects vector.
   */
  final void setWarningRegions (Vector r) {
    warningRegions = r;
    needsUpdate = true;
  }


  /**
   * Sets the bounds for of the graph, for clipping edges.
   * @param b The graph bounds.
   */
  final void setGraphBounds (Rectangle2D.Float b) {
    graphBounds = b;
    needsUpdate = true;
  }


  /**
   * Gets the bounds for the lighting effect.
   * Allows for single lighting effect to be applied to multiple FancyShape objects.
   * @return The lighting effect bounds.
   */
  final Rectangle2D.Float getLightBounds() {
    return lightBounds;
  }


  /**
   * Gets the source of light for the lighting effect.
   * On the side of the source, the color of the shape is brighter.
   * @return The light source.
   */
  final int getLightSource() {
    return lightSource;
  }


  /**
   * Gets the color of the shape.
   * @return The color.
   */
  final Color getColor() {
    return color;
  }


  /**
   * Gets the color of the shape's outline if it exists.
   * @return The outline color.
   */
  final Color getOutlineColor() {
    return outlineColor;
  }


  /**
   * Gets whether the shape is outlined by a one pixel curve.
   * @return If true, then outlined.
   */
  final boolean getOutlineExistence() {
    return outlineExistence;
  }


  /**
   * Gets the type of the chart the shape is being used in.
   * Uses fields LABELSLEFT and LABELSBOTTOM.
   * @return The type.
   */
  final int getType() {
    return type;
  }


  /**
   * Gets the bounds for of the graph, for clipping edges.
   * @return The graph bounds.
   */
  final Rectangle2D.Float getGraphBounds() {
    return graphBounds;
  }

  /**
   * Gets the vector of WarningRegion objects that affect this object's painting.
   * @return The WarningRegion objects vector.
   */
  final Vector getWarningRegions() {
    return warningRegions;
  }


  /**
   * Gets the gradient paint for this shape.
   * @return The gradient paint.
   */
  final Paint getPaint() {
    update();
    return paint;
  }


  /**
   * Gets the outline paint for this shape.
   * The outline paint is just a solid color.
   * @return The outline paint.
   */
  final Paint getOutlinePaint() {
    update();
    return outlinePaint;
  }


  /**
   * Gets the paints for the warning regions of this shape.
   * @return The warning region paints.
   */
  final Vector getWarningPaints() {
    update();
    return warningPaints;
  }


  /**
   * Gets whether a property has changed such that an update is needed.
   * @return If true, then needs update.
   */
  final boolean getNeedsUpdate() {
    return needsUpdate;
  }


  /**
   * Updates the FancyShape.
   */
  void update() { //don't update warning regions here, do it in GraphArea...

    if (getNeedsUpdate()) {

      if (lightSource == TOP) {
        paint = new GradientPaint (lightBounds.x, lightBounds.y, color.brighter(),
          lightBounds.x, lightBounds.y + lightBounds.height, color);
        if (outlineExistence) {
          outlinePaint = new GradientPaint (lightBounds.x, lightBounds.y, outlineColor.brighter(),
            lightBounds.x, lightBounds.y + lightBounds.height, outlineColor);
        }
        warningPaints.removeAllElements();
        if (warningRegions != null) {
          for (int i = 0; i < warningRegions.size(); ++i) {
            Color warningColor = ((WarningRegion)warningRegions.get(i)).getComponentColor();
            warningPaints.add (
              new GradientPaint (lightBounds.x, lightBounds.y, warningColor.brighter(),
              lightBounds.x, lightBounds.y + lightBounds.height, warningColor));
          }
        }
      }
      else if (lightSource == BOTTOM) {
        paint = new GradientPaint (lightBounds.x, lightBounds.y, color,
          lightBounds.x, lightBounds.y + lightBounds.height, color.brighter());
        if (outlineExistence) {
          outlinePaint = new GradientPaint (lightBounds.x, lightBounds.y, outlineColor,
            lightBounds.x, lightBounds.y + lightBounds.height, outlineColor.brighter());
        }
        warningPaints.removeAllElements();
        for (int i = 0; i < warningRegions.size(); ++i) {
          Color warningColor = ((WarningRegion)warningRegions.get(i)).getComponentColor();
          warningPaints.add (
            new GradientPaint (lightBounds.x, lightBounds.y, warningColor,
            lightBounds.x, lightBounds.y + lightBounds.height, warningColor.brighter()));
        }
      }
      else if (lightSource == LEFT) {

        paint = new GradientPaint (lightBounds.x, lightBounds.y, color.brighter(),
          lightBounds.x + lightBounds.width, lightBounds.y, color);
        if (outlineExistence) {
          outlinePaint = new GradientPaint (lightBounds.x, lightBounds.y, outlineColor.brighter(),
            lightBounds.x + lightBounds.width, lightBounds.y, outlineColor);
        }
        warningPaints.removeAllElements();
        if (warningRegions != null) {
          for (int i = 0; i < warningRegions.size(); ++i) {
            Color warningColor = ((WarningRegion)warningRegions.get(i)).getComponentColor();
            warningPaints.add (
              new GradientPaint (lightBounds.x, lightBounds.y, warningColor.brighter(),
              lightBounds.x + lightBounds.width, lightBounds.y, warningColor));
          }
        }
      }
      else if (lightSource == RIGHT) {
        paint = new GradientPaint (lightBounds.x, lightBounds.y, color,
          lightBounds.x + lightBounds.width, lightBounds.y, color.brighter());
        if (outlineExistence) {
          outlinePaint = new GradientPaint (lightBounds.x, lightBounds.y, outlineColor,
            lightBounds.x + lightBounds.width, lightBounds.y, outlineColor.brighter());
        }
        warningPaints.removeAllElements();
        if (warningRegions != null) {
          for (int i = 0; i < warningRegions.size(); ++i) {
            Color warningColor = ((WarningRegion)warningRegions.get(i)).getComponentColor();
            warningPaints.add (
              new GradientPaint (lightBounds.x, lightBounds.y, warningColor,
              lightBounds.x + lightBounds.width, lightBounds.y, warningColor.brighter()));
          }
        }
      }
      else {
        paint = color;
        outlinePaint = outlineColor;
        warningPaints.removeAllElements();
        if (warningRegions != null) {
          for (int i = 0; i < warningRegions.size(); ++i) {
            Color warningColor = ((WarningRegion)warningRegions.get(i)).getComponentColor();
            warningPaints.add (warningColor);
          }
        }
      }
      needsUpdate = false;
    }
  }
}