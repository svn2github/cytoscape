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


/**
 * A structure for recording the region of the graph to be painted in a special way.
 * The background of the graph will be painted with a particular color.
 * All graph components intering the region of the graph will be painted with a particular color.
 */
final class WarningRegion {

  /**
   * Indicates the top of the graph.
   */
  static final float TOP = Float.POSITIVE_INFINITY;

  /**
   * Indicates the bottom of the graph.
   */
  static final float BOTTOM = Float.NEGATIVE_INFINITY;

  /**
   * Indicates the region is for a labels bottom graph.
   */
  static final int LABELS_BOTTOM = 0;

  /**
   * Indicates the region is for a labels left graph.
   */
  static final int LABELS_LEFT = 1;


  private int graphSpaceX, graphSpaceY, graphSpaceWidth, graphSpaceHeight;
  private float high;
  private float highGraph;
  private float low;
  private float lowGraph;
  private int graphType;
  private Color componentColor;
  private boolean backgroundExistence;
  private Color backgroundColor;
  private Rectangle2D.Float background;
  private boolean needsUpdate;


  /**
   * Creates a new WarningRegion object.
   */
  WarningRegion() {
    needsUpdate = true;
  }


  /**
   * Sets the x location of the graph area.
   * @param x The x location.
   */
  final void setGraphSpaceX (int x) {
    graphSpaceX = x;
    needsUpdate = true;
  }


  /**
   * Sets the y location of the graph area.
   * @param y The y location.
   */
  final void setGraphSpaceY (int y) {
    graphSpaceY = y;
    needsUpdate = true;
  }


  /**
   * Sets the width of the graph area.
   * @param width The width of the graph area.
   */
  final void setGraphSpaceWidth (int w) {
    graphSpaceWidth = w;
    needsUpdate = true;
  }


  /**
   * Sets the height of the graph area.
   * @param width The height of the graph area.
   */
  final void setGraphSpaceHeight (int h) {
    graphSpaceHeight = h;
    needsUpdate = true;
  }


  /**
   * Sets the high value from the data for this warning region.
   * @param h The data high value.
   */
  final void setHigh (float h) {
    high = h;
    needsUpdate = true;
  }


  /**
   * Sets the high value of the graph for this warning region.
   * @param h The graph high value.
   */
  final void setHighGraph (float h) {

    highGraph = h;
    needsUpdate = true;
  }


  /**
   * Sets the low value from the data for this warning region.
   * @param h The data low value.
   */
  final void setLow (float l) {
    low = l;
    needsUpdate = true;
  }


  /**
   * Sets the low value of the graph for this warning region.
   * @param h The graph low value.
   */
  final void setLowGraph (float l) {
    lowGraph = l;
    needsUpdate = true;
  }


  /**
   * Sets the graph type for this warning region.
   * Either GraphArea.LABELSLEFT or GraphArea.LABELSBOTTOM.
   * @param t The type of graph.
   */
  final void setGraphType (int t) {
    graphType = t;
    needsUpdate = true;
  }


  /**
   * Sets the color of the portions of components that enter this region.
   * @param c The color of the components.
   */
  final void setComponentColor (Color c) {
    componentColor = c;
    needsUpdate = true;
  }


  /**
   * Sets whether the background warning region is painted or not.
   * @param e If true, the it will be painted.
   */
  final void setBackgroundExistence (boolean e) {
    backgroundExistence = e;
    needsUpdate = true;
  }


  /**
   * Sets the color of the portions of graph background that enter this region.
   * @param c The background color.
   */
  final void setBackgroundColor (Color c) {
    backgroundColor = c;
    needsUpdate = true;
  }


  /**
   * Gets the x location of the graph area.
   * @return The x location.
   */
  final int getGraphSpaceX() {
    return graphSpaceX;
  }


  /**
   * Gets the y location of the graph area.
   * @return The y location.
   */
  final int getGraphSpaceY() {
    return graphSpaceY;
  }


  /**
   * Gets the width of the graph area.
   * @return The width of the graph area.
   */

  final int getGraphSpaceWidth() {
    return graphSpaceWidth;
  }


  /**
   * Gets the height of the graph area.
   * @return The height of the graph area.
   */
  final int getGraphSpaceHeight() {
    return graphSpaceHeight;
  }


  /**
   * Gets the high value from the data for this warning region.
   * @return The data high value.
   */
  final float getHigh() {
    return high;
  }


  /**
   * Gets the high value of the graph for this warning region.
   * @return The graph high value.
   */
  final float getHighGraph() {
    return highGraph;
  }


  /**
   * Gets the low value from the data for this warning region.
   * @return The data low value.
   */
  final float getLow() {
    return low;
  }


  /**
   * Gets the low value of the graph for this warning region.
   * @return The graph low value.
   */
  final float getLowGraph() {
    return lowGraph;
  }


  /**
   * Gets the graph type for this warning region.
   * Either GraphArea.LABELSLEFT or GraphArea.LABELSBOTTOM.
   * @return The type of graph.
   */
  final int getGraphType() {
    return graphType;
  }


  /**
   * Gets the color of the portions of components that enter this region.
   * @return The color of the components.
   */
  final Color getComponentColor() {
    return componentColor;
  }


  /**
   * Gets whether the background warning region is painted or not.
   * @return If true, the it will be painted.
   */
  final boolean getBackgroundExistence() {
    return backgroundExistence;
  }


  /**
   * Gets the color of the portions of graph background that enter this region.
   * @return The background color.
   */
  final Color getBackgroundColor() {
    return backgroundColor;
  }


  /**
   * Gets the calculated background bounds of this warning region.
   * @return The warning region's background bounds.
   */
  final Rectangle2D.Float getBackgroundBounds() {
    updateWarningRegion();
    return background;
  }



  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getWarningRegionNeedsUpdate() {
    return needsUpdate;
  }


  /**
   * Paints this region.
   * Updates everything before painting.
   * @param g2D The graphics context for calculations and painting.
   */
  final void paintComponent (Graphics2D g2D) {

    updateWarningRegion();
    if (backgroundExistence) {
      g2D.setColor (backgroundColor);
      g2D.fill (background);
    }
  }


  /**
   * Updates this warning region.
   * Calling this methods assures they are all updates with respect to eachother.
   */
  final void updateWarningRegion() {

    if (getWarningRegionNeedsUpdate()) {

      needsUpdate = false;

      float x, y, width, height;
      if (graphType == LABELS_BOTTOM) {

        x = graphSpaceX;
        width = graphSpaceWidth;

        y = graphSpaceY + graphSpaceHeight - highGraph;
        height = highGraph - lowGraph;
      }
      else {

        y = graphSpaceY;
        height = graphSpaceHeight;

        x = graphSpaceX + lowGraph;
        width = highGraph - lowGraph;
      }

      background = new Rectangle2D.Float (x, y, width, height);
    }
  }
}