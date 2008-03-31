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


/**
 * A customizable legend for a chart, enabling charting of multiple data sets.
 * <p><b>Features:</b><br>
 * Supports any number of text labels, allows for associating of any color with
 * any text label.  Includes bordering, gapping, auto and manual resizing
 * and locating, and growing and shrinking components.  The only shape of bullet
 * that is available is a rectangle, however.
 */
final class LegendArea extends VerticalTextListArea {


  private boolean needsUpdate;


  /**
   * Creates a legend area using all the defaults of vertical text list area.
   * Defaults:<br>
   * setAutoSizes (false, false);<br>
   * setAutoJustifys (false, false);<br>
   * resetLegendAreaModel (true);<br>
   */
  LegendArea() {

    setAutoSizes (false, false);
    setAutoJustifys (false, false);
    resetLegendAreaModel (true);
    needsUpdate = true;
  }


  /**
   * Specifies which colors to use for the label's bullets.  Each label has
   * a bullet to its left.  This method sets the colors for these bullets.
   * The uppermost label will get the lowest order array member.
   * @param colors The bullet colors.
   */
  final void setColors (Color[] colors) {

    needsUpdate = true;
    setBulletColors (colors);
  }


  /**
   * Resets the model for this class.  The model is used for shrinking and
   * growing of its components based on the maximum size of this class.  If this
   * method is called, then the next time the maximum size is set, this classes
   * model maximum size will be made equal to the new maximum size.  Effectively
   * what this does is ensure that whenever this objects maximum size is equal
   * to the one given, then all of the components will take on their default
   * model sizes.  Note:  This is only useful when auto model max sizing is
   * disabled.
   * @param reset True causes the max model size to be set upon the next max
   * sizing.
   */
  final void resetLegendAreaModel (boolean reset) {

    needsUpdate = true;
    resetVerticalTextListAreaModel (reset);
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getLegendAreaNeedsUpdate() {

    return (needsUpdate || getVerticalTextListAreaNeedsUpdate());
  }


  /**
   * Updates all variables.  First updates the variables of its parent class,
   * then updates its own variables.
   * @param g2D The graphics context used for calculations.
   */
  final void updateLegendArea (Graphics2D g2D) {
    if (getLegendAreaNeedsUpdate()) {
      updateVerticalTextListArea (g2D);
    }
    needsUpdate = false;
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D  The graphics context for calculations and painting.
   */
  void paintComponent (Graphics2D g2D) {

    updateLegendArea (g2D);
    super.paintComponent (g2D);
  }
}