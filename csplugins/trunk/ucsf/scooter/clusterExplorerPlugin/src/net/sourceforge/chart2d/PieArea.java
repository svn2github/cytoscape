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
import java.util.Date;


/**
 * A container for many variables and components relating to a pie area.
 * A pie area is the area that contains only the pie of a pie chart.  It doesn't
 * have a legend.<br>
 * <b>Features:</b><br>
 * Allows for charting both floats and int values.  Works fine even if all
 * values are zero.  Does not accept negative values.  Outputs center points
 * of sectors so that labels can be drawn into the sectors, touching these
 * points.  Outpouts how many sectors are in each quarter of the pie, so that
 * appropriate placement of labels can be decided.  The first datum in the
 * data set will be the first sector beginning from the angle of 135 degrees and
 * ending somewhere clockwise to it  It will have the first color in the
 * colors set.  The remaining sectors are laid out similarly, clockwise.
 */
final class PieArea extends Area {


  private float[] dataset;
  private Color[] colors;
  private Paint[] paints;

  private Arc2D.Float[] sectors;
  private int numSectors;
  private int[] numSectorsInQuarters;
  private boolean outlineSectors;
  private Color outlineSectorsColor;

  private float sectorPointDepthRatio;
  private Point[] pointsInSectors;
  private float sectorGapPointRatio;
  private Point[] pointsOutSectors;

  private int piePrefSpaceSize;
  private boolean customSizing;
  private Dimension customSpaceSize;

  private int lightSource;

  private boolean needsUpdate;


  /**
   * Creates a pie area with the default values.
   */
  PieArea() {

    setAutoSizes (false, false);
    setAutoJustifys (false, false);
    setBackgroundExistence (false);
    setBorderExistence (false);
    setGapExistence (true);
    setGapThicknessModel (0);
    setOutlineSectors (true);
    setOutlineSectorsColor (Color.black);
    setPiePrefSizeModel (100);
    setSectorPointDepthRatio (.25f); //setSectorPointRatio
    setSectorGapPointRatio (.25f);
    setDataset (new float[0]);
    setColors (new Color[0]);
    setCustomSpaceSize (false, new Dimension());
    setLightSource (TOP);
    resetPieAreaModel (true);
    needsUpdate = true;
    numSectorsInQuarters = new int[4];
  }


  /**
   * Sets from which direction the light is coming for shading of the pie sectors.
   * @param source The direction of the light.
   */
  final void setLightSource (int source) {

    needsUpdate = true;
    lightSource = source;
  }


  /**
   * Indicates whether the sectors should have a thin outline.
   * @param outline If true, then the outline exists.
   */
  final void setOutlineSectors (boolean outline) {

    needsUpdate = true;
    outlineSectors = outline;
  }


  /**
   * Indicates the color of the sectors outline if it exists.
   * @param color The color for the outline.
   */
  final void setOutlineSectorsColor (Color color) {

    needsUpdate = true;
    outlineSectorsColor = color;
  }


  /**
   * Determines how far into the sector the depth of the points from the
   * getPointsInSectors() method are.
   * @param ratio The ratio on the radius of the circle.
   */
  final void setSectorPointDepthRatio (float ratio) {
    needsUpdate = true;
    sectorPointDepthRatio = ratio;
  }


  /**
   * Determines how far out of the sector the points from the
   * getPointsOutSectors() method are.
   * @param ratio The ratio on the radius of the circle.
   */
  final void setSectorGapPointRatio (float ratio) {
    needsUpdate = true;
    sectorGapPointRatio = ratio;
  }


  /**
   * For input of the raw numbers to represent by the pie.  Array element i
   * is sector i, clockwise from degree 135.
   * @param values The raw numbers to represent by the pie.
   */
  final void setDataset (float[] values) {

    needsUpdate = true;
    dataset = values;
  }


  /**
   * For input of the color of each sector that represents a datum of the data
   * set.  Array element i is sector i, clockise from degree 135.
   * @param colors The colors of the sectors.
   */
  final void setColors (Color[] colors) {

    needsUpdate = true;
    this.colors = colors;
  }


  /**
   * Sets the model size of the pie.  When initially drawn, the pie will be
   * at least this size, if this size is less than the max space size of this
   * area.  After that and if magnificying, the pie size will be applied to the
   * size ratios.
   * @param size The model size of the pie.
   */
  final void setPiePrefSizeModel (int size) {

    needsUpdate = true;
    piePrefSpaceSize = size;
  }


  /**
   * Sets the "minimum" size of the pie directly.  If the minimum is too
   * large, then the size will be the pie's maximum size.
   * @param size The number to multiply to get the minimum size.
   */
  final void setCustomSpaceSize (boolean customize, Dimension size) {

    needsUpdate = true;
    customSizing = customize;
    customSpaceSize = size;
  }


  /**
   * Gets from which direction the light is coming for shading of the pie sectors.
   * @return The direction of the light.
   */
  final int getLightSource() {
    return lightSource;
  }


  /**
   * Returns whether the sectors should have a thin outline.
   * @return outline If true, then the outline exists.
   */
  final boolean getOutlineSectors() {

    return outlineSectors;
  }


  /**
   * Returns the color of the sectors outline if it exists.
   * @return color The color for the outline.
   */
  final Color getOutlineSectorsColor() {

    return outlineSectorsColor;
  }


  /**
   * Returns the raw numbers to represent by the pie.
   * @return The raw numbers to represent by the pie.
   */
  final float[] getDataset() {

    return dataset;
  }


  /**
   * Returns this property.
   * @return The colors of the lines.
   */
  final Color[] getColors() {

    return colors;
  }


  /**
   * Returns this property.
   * @return The number of sectors in this pie.
   */
  final int getNumSectors () {

    updatePieArea();
    return numSectors;
  }


  /**
   * Returns this property.  This property allows figuring out where to place
   * labels if one wants to label this pie, more than is provided by the legend
   * in PieChartArea.  One can use a
   * HorizontalTextListArea for the TOP and BOTTOM labels; and a
   * VerticalTextListArea for the LEFT and RIGHT labels.
   * This information helps them figure out how many and which labels belong in
   * each text list.
   * @return The number of sectors in the quarters.  Actually, the number of
   * center points of sectors in this quarter.  Quarters are defined as follows:
   * TOP = 135 to 45, LEFT = 45 to 315, BOTTOM = 315 to 225, RIGHT = 225 to 135.
   */
  final int[] getNumSectorsInQuarters() {

    updatePieArea();
    return numSectorsInQuarters;
  }


  /**
   * Returns how far into the sector the depth of the points from the
   * getPointsInSectors() method are.
   * @return The ratio on the radius of the circle.
   */
  final float getSectorPointDepthRatio() {

    return sectorPointDepthRatio;
  }


  /**
   * Returns a point in each sector at the depth specified by
   * setSectorsPointDepthRatio(float).
   * @return Point[] The points inside the sectors.
   */
  final Point[] getPointsInSectors() {

    updatePieArea();
    return pointsInSectors;
  }


  /**
   * Returns a point in out of each sector at the location indicated by
   * setSectorsPointExternalRatio(float).
   * @return Point[] The points outside the sectors.
   */
  final Point[] getPointsOutSectors() {
    updatePieArea();
    return pointsOutSectors;
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
  final void resetPieAreaModel (boolean reset) {

    needsUpdate = true;
    resetAreaModel (reset);
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getPieAreaNeedsUpdate() {

    return (needsUpdate || getAreaNeedsUpdate());
  }


  /**
   * Updates this parent's variables, and this' variables.
   */
  final void updatePieArea () {

    if (getPieAreaNeedsUpdate()) {
      updateArea();
      update();
    }
    needsUpdate = false;
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D  The graphics context for calculations and painting.
   */
  final void paintComponent (Graphics2D g2D) {

    updatePieArea();
    super.paintComponent (g2D);

    for (int i = 0; i < numSectors; ++i) {
      g2D.setPaint (paints[i]);
      g2D.fill (sectors[i]);
    }

    if (outlineSectors) {
      for (int i = 0; i < numSectors; ++i) {
          g2D.setColor (outlineSectorsColor);
          g2D.draw (sectors[i]);
      }
    }
  }


  private void update() {

    if (!getAutoSize (MIN) && !customSizing) {
      piePrefSpaceSize = applyRatio (piePrefSpaceSize, getRatio (LESSER));
      int tempPrefSize = piePrefSpaceSize < getSpaceSize (MAX).width ?
        piePrefSpaceSize : getSpaceSize (MAX).width;
      tempPrefSize = piePrefSpaceSize < getSpaceSize (MAX).height ?
        piePrefSpaceSize : getSpaceSize (MAX).height;
      setSpaceSize (MIN, new Dimension (tempPrefSize, tempPrefSize));
    }
    else if (!getAutoSize (MIN) && customSizing) {
      setSpaceSize (MIN, customSpaceSize);
    }

    int width = getSpaceSize (MIN).width;
    int height = getSpaceSize (MIN).height;
    int minSpaceSizeSide = width < height ? width : height;

    Point location = getSpaceSizeLocation (MIN);
    int locationX = location.x + (int)((width - minSpaceSizeSide) / 2f);
    int locationY = location.y + (int)((height - minSpaceSizeSide) / 2f);

    numSectors = dataset.length;
    sectors = new Arc2D.Float[numSectors];

    float total = 0f;
    for (int i = 0; i < numSectors; ++i) {
      total = total + dataset[i];
    }

    if (total != 0f) {
      float end = 135f;
      float begin = 135f;
      float extent = 0f;
      for (int i = 0; i < (numSectors - 1); ++i) {
        extent = (dataset[i] / total) * 360f;
        begin = end - extent;
        sectors[i] = new Arc2D.Float (locationX, locationY,
          minSpaceSizeSide, minSpaceSizeSide, begin, extent, Arc2D.PIE);
        end = begin;
      }
      extent = (dataset[numSectors - 1] / total) * 360f;
      begin = 135f;
      sectors[numSectors - 1] = new Arc2D.Float (locationX, locationY,
        minSpaceSizeSide, minSpaceSizeSide, begin, extent, Arc2D.PIE);
    }
    else if (numSectors != 0) {
      float end = 135f;
      float begin = 135f;
      float extent = (1f / numSectors) * 360f;
      for (int i = 0; i < (numSectors - 1); ++i) {
        begin = end - extent;
        sectors[i] = new Arc2D.Float (locationX, locationY,
          minSpaceSizeSide, minSpaceSizeSide, begin, extent, Arc2D.PIE);
        end = begin;
      }
      begin = 135f;
      sectors[numSectors - 1] = new Arc2D.Float (locationX, locationY,
        minSpaceSizeSide, minSpaceSizeSide, begin, extent, Arc2D.PIE);
    }

    pointsInSectors = new Point[numSectors];
    float radius = minSpaceSizeSide / 2f;
    float centerX = locationX + radius;
    float centerY = locationY + radius;
    for (int i = 0; i < numSectors; ++i) {
      float begin = sectors[i].start;
      float extent = sectors[i].extent;
      float theta = begin + extent / 2f;
      float offsetX = (float)((1f - sectorPointDepthRatio) * radius *
        Math.cos (Math.toRadians (theta)));
      float offsetY = (float)((1f - sectorPointDepthRatio) * radius *
        Math.sin (Math.toRadians (theta)));
      float x = centerX + offsetX;
      float y = centerY - offsetY;
      pointsInSectors[i] = new Point (Math.round (x), Math.round (y));
    }

    int gapThickness = getGapThickness();
    pointsOutSectors = new Point[numSectors];
    float outOffset = sectorGapPointRatio * gapThickness;
    for (int i = 0; i < numSectors; ++i) {

      float begin = sectors[i].start;
      float extent = sectors[i].extent;
      float theta = begin + extent / 2f;
      float offsetX = (float)((radius + outOffset) *
        Math.cos (Math.toRadians(theta)));
      float offsetY = (float)((radius + outOffset) *
        Math.sin (Math.toRadians(theta)));
      float x = centerX + offsetX;
      float y = centerY - offsetY;
      pointsOutSectors[i] = new Point (Math.round (x), Math.round (y));
    }

    numSectorsInQuarters[TOP] = 0;
    numSectorsInQuarters[RIGHT] = 0;
    numSectorsInQuarters[BOTTOM] = 0;
    numSectorsInQuarters[LEFT] = 0;

    boolean topDone = false;

    for (int i = 0; i < numSectors; ++i) {

      float begin = sectors[i].start;
      float extent = sectors[i].extent;
      begin = (begin + 720) % 360;
      extent = (extent + 720) % 360;
      float angle = begin + (extent / 2);

      if (angle <= 135 && angle > 45){
        if (!topDone) ++numSectorsInQuarters[TOP];
        else ++numSectorsInQuarters[LEFT];
      }
      else if (angle > 135 && angle <= 225){
        ++numSectorsInQuarters[LEFT];
        topDone = true;
      }
      else if (angle > 225 && angle <= 315){
        ++numSectorsInQuarters[BOTTOM];
        topDone = true;
      }
      else {
        ++numSectorsInQuarters[RIGHT];
        topDone = true;
      }
    }

    paints = new Paint[colors.length];
    for (int i = 0; i < paints.length; ++i) {
      if (lightSource == TOP) {
        paints[i] = new GradientPaint (locationX, locationY, colors[i].brighter(),
          locationX, locationY + height, colors[i]);
      }
      else if (lightSource == BOTTOM) {
        paints[i] = new GradientPaint (locationX, locationY, colors[i],
          locationX, locationY + height, colors[i].brighter());
      }
      else if (lightSource == LEFT) {
        paints[i] = new GradientPaint (locationX, locationY, colors[i].brighter(),
          locationX + width, locationY, colors[i]);
      }
      else if (lightSource == RIGHT) {
        paints[i] = new GradientPaint (locationX, locationY, colors[i],
          locationX + width, locationY, colors[i].brighter());
      }
      else {
        paints[i] = colors[i];
      }
    }
  }
}