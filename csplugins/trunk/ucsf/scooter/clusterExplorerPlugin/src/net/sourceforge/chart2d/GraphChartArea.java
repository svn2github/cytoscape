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
import java.util.*;


/**
 * A container containing information and components for cartesian coordinate
 * charts.  That is, charts that are plotted on a parralelogram surface.
 */
class GraphChartArea extends ChartArea {


  private Vector datasetVector;
  private Vector graphVector;

  private XAxisArea xAxis;
  private YAxisArea yAxis;
  private Rectangle maxBounds;
  private Rectangle minBounds;

  private int numPlotAxisLabels;

  private float yAxisToWidthRatio;
  private float yAxisToHeightRatio;

  private float xAxisToWidthRatio;
  private float xAxisToHeightRatio;

  private float graphToWidthRatio;
  private float graphToHeightRatio;
  private float graphableToAvailableRatio;

  private boolean graphLinesThicknessAssociation;
  private boolean graphHorizontalLinesExistence;
  private int graphHorizontalLinesThicknessModel;
  private float[] graphHorizontalLinesStyle;
  private Color graphHorizontalLinesColor;

  private boolean graphVerticalLinesExistence;
  private int graphVerticalLinesThicknessModel;
  private float[] graphVerticalLinesStyle;
  private Color graphVerticalLinesColor;

  private boolean customizeGreatestValue;
  private float customGreatestValue;
  private boolean customizeLeastValue;
  private float customLeastValue;

  private float numbersAxisRangeHigh;
  private float numbersAxisRangeLow;

  private int numbersAxisLabelsType;
  private Vector warningRegions;

  private boolean graphComponentsColoringByCat;
  private Color[] graphComponentsColorsByCat;

  private boolean needsUpdate;


  /**
   * Creates a Graph Chart Area with the default values of a TitledArea,
   * and its own default values.
   */
  GraphChartArea() {

    xAxis = new XAxisArea();
    yAxis = new YAxisArea();
    maxBounds = new Rectangle();
    minBounds = new Rectangle();
    needsUpdate = true;

    setNumPlotAxisLabels (5);
    setCustomGreatestValue (false, 0f);
    setCustomLeastValue (false, 0f);
    setGraphableToAvailableRatio (.95f);

    setYAxisToWidthRatio (.25f);
    setXAxisToWidthRatio (.50f);
    setGraphToWidthRatio (.50f);

    setYAxisToHeightRatio (.75f);
    setGraphToHeightRatio (.75f);
    setXAxisToHeightRatio (.25f);

    setWarningRegions (new Vector (0, 0));

    setGraphComponentsColoringByCat(false);
    setGraphComponentsColorsByCat (new Color[0]);

    resetGraphChartAreaModel (true);
  }


  /**
   * Sets whether the graph components are colored by category or by set.
   * @param b If true, then colored by category.
   */
  final void setGraphComponentsColoringByCat (boolean b) {

    needsUpdate = true;
    graphComponentsColoringByCat = b;
  }


  /**
   * Sets the color array for the graph component coloring.
   * @param colors The color array for the category coloring.
   */
  final void setGraphComponentsColorsByCat (Color[] colors) {

    needsUpdate = true;
    graphComponentsColorsByCat = colors;
  }


  /**
   * Sets the warning regions vector for applying to each graph area.
   * @param v the warning regions vector.
   */
  final void setWarningRegions (Vector v) {

    warningRegions = v;
    needsUpdate = true;
  }



  /**
   * Sets the dataset vector.
   * @param vector A vector of datasets.
   */
  final void setDatasetVector (Vector vector) {

    needsUpdate = true;
    datasetVector = vector;
  }


  /**
   * Sets the number of labels for the plot axis.  For every graph chart, there
   * are two axes.  One axis you specify the labels (ex. June, July, August);
   * the other axis, this library figures out the labels (ex. 250 or 1.95).
   * The axis the library labels is the plot axis.
   * @param num The number of plot axis labels.
   */
  final void setNumPlotAxisLabels (int num) {

    needsUpdate = true;
    numPlotAxisLabels = num;
  }


  /**
   * Specifies how much of the maximum width less borders and gaps to make
   * availalble to the y axis maximum size.
   * @param ratio The ratio to the width, to make available to the y axis.
   */
  final void setYAxisToWidthRatio (float ratio) {

    needsUpdate = true;
    yAxisToWidthRatio = ratio;
  }


  /**
   * Specifies how much of the maximum height less borders, gaps, and title to
   * make availalble to the y axis maximum size.
   * @param ratio The ratio to the height, to make available to the y axis.
   */
  final void setYAxisToHeightRatio (float ratio) {

    needsUpdate = true;
    yAxisToHeightRatio = ratio;
  }


  /**
   * Specifies how much of the maximum width less borders and gaps to make
   * availalble to the x axis maximum size.
   * @param ratio The ratio to the width, to make available to the x axis.
   */
  final void setXAxisToWidthRatio (float ratio) {

    needsUpdate = true;
    xAxisToWidthRatio = ratio;
  }


  /**
   * Specifies how much of the maximum height less borders, gaps, and title to
   * make availalble to the x axis maximum size.
   * @param ratio The ratio to the height, to make available to the x axis.
   */
  final void setXAxisToHeightRatio (float ratio) {

    needsUpdate = true;
    xAxisToHeightRatio = ratio;
  }


  /**
   * Specifies how much of the maximum width less borders and gaps to make
   * availalble to the graph maximum size.
   * @param ratio The ratio to the width, to make available to the graph.
   */
  final void setGraphToWidthRatio (float ratio) {

    needsUpdate = true;
    graphToWidthRatio = ratio;
  }


  /**
   * Specifies how much of the maximum height less borders, gaps, and title to
   * make availalble to the graph maximum size.
   * @param ratio The ratio to the height, to make available to the graph.
   */
  final void setGraphToHeightRatio (float ratio) {

    needsUpdate = true;
    graphToHeightRatio = ratio;
  }


  /**
   * Influences the plot axis' label with the highest value.
   * Does this by tricking chart2d into thinking that the passed value is the
   * largest value in the dataset.
   * Then if setGraphableToAvailableRatio (1f), the plot axis' label with the
   * the highest value will be the custom greatest value.
   * So, in order to control what the highest value plot axis label is, then
   * use this method, the setGraphableToAvailableRatio method, and the
   * setLabelsPrecisionNum method.
   * Note:  If there are both positive and negative numbers in the dataset
   * then you may need to set both custom greatest and custom least methods
   * since labels are always equally far from zero.
   * @param customize Whether this value is used.
   * @param value The value to use.
   */
  final void setCustomGreatestValue (boolean customize, float value) {

    needsUpdate = true;
    customizeGreatestValue = customize;
    customGreatestValue = value;
  }


  /**
   * Influences the plot axis' label with the lowest value.
   * Does this by tricking chart2d into thinking that the passed value is the
   * largest value in the dataset.
   * Then if setGraphableToAvailableRatio (1f), the plot axis' label with the
   * the highest value will be the custom greatest value.
   * So, in order to control what the highest value plot axis label is, then
   * use this method, the setGraphableToAvailableRatio method, and the
   * setLabelsPrecisionNum method.
   * Note:  If there are both positive and negative numbers in the dataset
   * then you may need to set both custom greatest and custom least methods
   * since labels are always equally far from zero.
   * @param customize Whether this value is used.
   * @param value The value to use.
   */
  final void setCustomLeastValue (boolean customize, float value) {

    needsUpdate = true;
    customizeLeastValue = customize;
    customLeastValue = value;
  }


  /**
   * Specifies how much of he height <b>of the graph</b> to make available to
   * the components plot area.  If this ratio is set to one, then the highest
   * value of all the data sets will touch the top of the graph area.
   * @param ratio The ratio of the graph height to the greatest value in the
   * data set.  [Must be between 0.0 and 1.0]
   */
  final void setGraphableToAvailableRatio (float ratio) {

    needsUpdate = true;
    graphableToAvailableRatio = ratio;
  }


  /**
   * Gets whether the graph components are colored by category or by set.
   * @return If true, then colored by category.
   */
  final boolean getGraphComponentsColoringByCat() {
    return graphComponentsColoringByCat;
  }


  /**
   * Gets the color array for the graph component coloring.
   * @return The color array for the category coloring.
   */
  final Color[] getGraphComponentsColorsByCat() {
    return graphComponentsColorsByCat;
  }


  /**
   * Gets the warning regions vector for applying to each graph area.
   * @return the warning regions vector.
   */
  final Vector getWarningRegions() {
    return warningRegions;
  }


  /**
   * Returns all the Dataset objects added to this object.
   * @return Vector The Dataset objects.
   */
  final Vector getDatasetVector() {
    return datasetVector;
  }


  /**
   * Returns the x Axis in order to allow customizations of it.
   * @return The x axis of this chart.
   */
  final XAxisArea getXAxis() {

    return xAxis;
  }


  /**
   * Returns the y axis in order to allow customization of it.
   * @return The y axis of this chart.
   */
  final YAxisArea getYAxis() {

    return yAxis;
  }


  /**
   * Sets the vector of graphs for this chart.
   * @param vector The vector of graphs.
   */
  final void setGraphVector (Vector vector) {

    needsUpdate = true;
    graphVector = vector;
  }


  /**
   * Returns a vector with all the graphs that were added by the addGraph method.
   * @return Vector A vector of graphs.
   */
  final Vector getGraphVector() {

    return graphVector;
  }


  /**
   * Gets the number of labels for the plot axis.  For every graph chart, there
   * are two axes.  One axis you specify the labels (ex. June, July, August);
   * the other axis, this library figures out the labels (ex. 250 or 1.95).
   * The axis the library labels is the plot axis.
   * @return The number of plot axis labels.
   */
  final int getNumPlotAxisLabels() {

    return numPlotAxisLabels;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getYAxisToWidthRatio() {

    return yAxisToWidthRatio;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getYAxisToHeightRatio() {

    return yAxisToHeightRatio;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getXAxisToWidthRatio() {

    return xAxisToWidthRatio;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getXAxisToHeightRatio() {

    return xAxisToHeightRatio;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  float getGraphToWidthRatio() {

    return graphToWidthRatio;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getGraphToHeightRatio() {

    return graphToHeightRatio;
  }


  /**
   * Gets the boolean value passed to setCustomGreatestValue (boolean, float).
   * @return True if the max value is being customized.
   */
  final boolean getCustomizeGreatestValue() {

    return customizeGreatestValue;
  }


  /**
   * Gets the float value passed to setCustomGreatestValue (boolean, float).
   * @return The max value.
   */
  final float getCustomGreatestValue() {

    return customGreatestValue;
  }


  /**
   * Gets the boolean value passed to setCustomLeastValue (boolean, float).
   * @return True if the min value is being customized.
   */
  final boolean getCustomizeLeastValue() {

    return customizeLeastValue;
  }


  /**
   * Gets the float value passed to setCustomLeastValue (boolean, float).
   * @return The min value.
   */
  final float getCustomLeastValue() {

    return customLeastValue;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getGraphableToAvailableRatio() {

    return graphableToAvailableRatio;
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return boolean true if some property has changed.
   */
  final boolean getGraphChartAreaNeedsUpdate() {

    for (int i = 0; i < warningRegions.size(); ++i) {
      needsUpdate = needsUpdate ||
        ((WarningRegion)warningRegions.get (i)).getWarningRegionNeedsUpdate();
    }
    return (needsUpdate || getChartAreaNeedsUpdate() ||
    xAxis.getXAxisAreaNeedsUpdate() || yAxis.getYAxisAreaNeedsUpdate());
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
   * @param reset True resets the max model upon the next max sizing.
   */
  final void resetGraphChartAreaModel (boolean reset) {

    needsUpdate = true;
    resetChartAreaModel (reset);
    xAxis.resetXAxisModel (reset);
    yAxis.resetYAxisModel (reset);
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  final void updateGraphChartArea (Graphics2D g2D) {

    if (getGraphChartAreaNeedsUpdate()) {
      updateChartArea (g2D);
      //update warning regions after graphs are updated
    }
    needsUpdate = false;
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  void paintComponent (Graphics2D g2D) {

    updateGraphChartArea (g2D);
    super.paintComponent (g2D);
    for (int i = 0; i < warningRegions.size(); ++i) {
      ((WarningRegion)warningRegions.get (i)).paintComponent (g2D);
    }
    xAxis.paintComponent (g2D);
    yAxis.paintComponent (g2D);
  }
}