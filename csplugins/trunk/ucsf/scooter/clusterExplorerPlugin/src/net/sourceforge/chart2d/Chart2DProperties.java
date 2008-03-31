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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;


/**
 * A data structure for holding the properties common to all Chart2D charts.
 * Chart2D charts are all capable of having a legend.
 * Pass this to any number of Chart2D objects.
 */
public final class Chart2DProperties extends Properties {


  /**
   * The number is 38.
   */
  public static final int MAX_INTEGER = 38;

  /**
   * The number is -38.
   */
  public static final int MAX_FLOAT = -38;

  /**
   * The default is MAX_INTEGER.
   */
  public final static int CHART_DATA_LABELS_PRECISION_DEFAULT = MAX_INTEGER;

  /**
   * The default is true.
   */
  public final static boolean CHART_BETWEEN_CHART_AND_LEGEND_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 5.
   */
  public final static int CHART_BETWEEN_CHART_AND_LEGEND_GAP_THICKNESS_MODEL_DEFAULT = 5;


  private int chartDataLabelsPrecision;
  private boolean chartBetweenChartAndLegendGapExistence;
  private int chartBetweenChartAndLegendGapThicknessModel;

  private boolean needsUpdate = true;
  private final Vector chart2DVector = new Vector (5, 5);
  private final Vector needsUpdateVector = new Vector (5, 5);


  /**
   * Creates a Chart2DProperties object with the documented default values.
   */
  public Chart2DProperties() {

    needsUpdate = true;
    setChart2DPropertiesToDefaults();
  }


  /**
   * Creates a Chart2DProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param chart2DProps The properties to copy.
   */
  public Chart2DProperties (Chart2DProperties chart2DProps) {

    needsUpdate = true;
    setChart2DProperties (chart2DProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setChart2DPropertiesToDefaults() {

    needsUpdate = true;
    setChartDataLabelsPrecision (CHART_DATA_LABELS_PRECISION_DEFAULT);
    setChartBetweenChartAndLegendGapExistence (
      CHART_BETWEEN_CHART_AND_LEGEND_GAP_EXISTENCE_DEFAULT);
    setChartBetweenChartAndLegendGapThicknessModel (
      CHART_BETWEEN_CHART_AND_LEGEND_GAP_THICKNESS_MODEL_DEFAULT);
  }


  /**
   * Sets all properties to be the values of another Chart2DProperties object.
   * The copying is a deep copy.
   * @param chart2DProps The properties to copy.
   */
  public final void setChart2DProperties (Chart2DProperties chart2DProps) {

    needsUpdate = true;
    setChartDataLabelsPrecision (chart2DProps.getChartDataLabelsPrecision());
    setChartBetweenChartAndLegendGapExistence (
      chart2DProps.getChartBetweenChartAndLegendGapExistence());
    setChartBetweenChartAndLegendGapThicknessModel (
      chart2DProps.getChartBetweenChartAndLegendGapThicknessModel());
  }


  /**
   * Sets the look of labels that are supposed to describe the value of the
   * data.  For example, for pie charts this method will influence
   * the labels that surround the chart, and for charts with axes this method
   * will influence the labels on the numbers axis.  The specific labels are
   * first determined from the greatest and least value in the data set and
   * other factors.  This method then lets you adjust whether you want integer
   * or floating point labels and how exact you want those labels to be.  You
   * do this using the precision parameter, and passing values between 38 and -38.
   * Nonnegative ints indicate the labels will not contain
   * a decimal (ie will be integer labels), and the specific nonnegative
   * number indicates how many zeroes will be to the left of the decimal if
   * it were there (ex. for the number 1976, a precision of 0 would return 1976,
   * a precision of 1 would return 1980, a precision of 3 would return 2000, and
   * a precision greater than 3 would also return 2000).  If the number of
   * desired zeroes is greater than the label, then the number of zeroes is
   * automatically scaled back to be 1 less than the number of digits.  Negative
   * ints for precision indicate the labels will contain a decimal, and the
   * specific negative number indicates how many digits to the right of the
   * decimal are wanted (ex. for the number 1.935, a precision of -1 would
   * return 1.9, a precision of -2 would return 1.94, a precision of -4 would
   * return 1.9350).  If the number of desired decimal places is larger than the
   * number of places present in the number, then zeroes are added to form as
   * many places as are desired up to -38.  MAX_INTEGER or MAX_FLOAT will always
   * indicate the maximum number of zeroes and the maximum number of decimal
   * places possible by Chart2D's algorithm for adjusting these labels.
   * @param precision The adjustement of the precision of numbers labels.
   */
  public final void setChartDataLabelsPrecision (int precision) {

    needsUpdate = true;
    chartDataLabelsPrecision = precision;
  }


  /**
   * Sets whether the gap between the chart and the legend exists.
   * If the legend doesn't exist, this gap will automatically not exist.
   * @param existence If true, the gap exists.
   */
  public final void setChartBetweenChartAndLegendGapExistence (boolean existence) {

    needsUpdate = true;
    chartBetweenChartAndLegendGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between the chart and the legend for the chart's model size.
   * @param thickness The model thickness of the gap.
   */
  public final void setChartBetweenChartAndLegendGapThicknessModel (int thickness) {

    needsUpdate = true;
    chartBetweenChartAndLegendGapThicknessModel = thickness;
  }


  /**
   * Gets the look of labels that are supposed to describe the value of the
   * data.  For example, for pie charts this method will influence
   * the labels that surround the chart, and for charts with axes this method
   * will influence the labels on the numbers axis.  The specific labels are
   * first determined from the greatest and least value in the data set and
   * other factors.  This method then lets you adjust whether you want integer
   * or floating point labels and how exact you want those labels to be.  You
   * do this using the precision parameter, and passing values between 8 and -8.
   * Nonnegative ints indicate the labels will not contain
   * a decimal (ie will be integer labels), and the specific nonnegative
   * number indicates how many zeroes will be to the left of the decimal if
   * it were there (ex. for the number 1976, a precision of 0 would return 1976,
   * a precision of 1 would return 1980, a precision of 3 would return 2000, and
   * a precision greater than 3 would also return 2000).  If the number of
   * desired zeroes is greater than the label, then the number of zeroes is
   * automatically scaled back to be 1 less than the number of digits.  Negative
   * ints for precision indicate the labels will contain a decimal, and the
   * specific negative number indicates how many digits to the right of the
   * decimal are wanted (ex. for the number 1.935, a precision of -1 would
   * return 1.9, a precision of -2 would return 1.94, a precision of -4 would
   * return 1.9350).  If the number of desired decimal places is larger than the
   * number of places present in the number, then zeroes are added to form as
   * many places as are desired up to -8.  MAX_INTEGER or MAX_FLOAT will always
   * indicate the maximum number of zeroes and the maximum number of decimal
   * places possible by Chart2D's algorithm for adjusting these labels.
   * @return The adjustement of the precision of numbers labels.
   */
  public final int getChartDataLabelsPrecision() {
    return chartDataLabelsPrecision;
  }


  /**
   * Gets whether the gap between the chart and the legend exists.
   * If the legend doesn't exist, this gap will automatically not exist.
   * @return If true, the gap exists.
   */
  public final boolean getChartBetweenChartAndLegendGapExistence() {
    return chartBetweenChartAndLegendGapExistence;
  }


  /**
   * Gets the thickness of the gap between the chart and the legend for the chart's model size.
   * @return The model thickness of the gap.
   */
  public final int getChartBetweenChartAndLegendGapThicknessModel() {
    return chartBetweenChartAndLegendGapThicknessModel;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param chart2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getChart2DNeedsUpdate (Chart2D chart2D) {

    if (needsUpdate) return true;
    int index = -1;
    if ((index = chart2DVector.indexOf (chart2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }
    return false;
  }


  /**
   * Adds a Chart2D to the set of objects using these properties.
   * @param chart2D The object to add.
   */
  final void addChart2D (Chart2D chart2D) {
    if (!chart2DVector.contains (chart2D)) {
      chart2DVector.add (chart2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes a Chart2D from the set of objects using these properties.
   * @param chart2D The object to remove.
   */
  final void removeChart2D (Chart2D chart2D) {
    int index = -1;
    if ((index = chart2DVector.indexOf (chart2D)) != -1) {
      chart2DVector.remove (index);
      needsUpdateVector.remove (index);
    }
  }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  final boolean validate (boolean debug) {

    if (debug) System.out.println ("Validating Chart2DProperties");

    boolean valid = true;

    if (chartDataLabelsPrecision > MAX_INTEGER || chartDataLabelsPrecision < MAX_FLOAT) {
      valid = false;
      if (debug) System.out.println ("Problem with ChartDataLabelsPrecision");
    }
    if (chartBetweenChartAndLegendGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("ChartBetweenChartAndLegendGapThicknessModel < 0");
    }

    if (debug) {
      if (valid) System.out.println ("Chart2DProperties was valid");
      else System.out.println ("Chart2DProperties was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this Chart2D.
   * @param chart2D The Chart2D to update.
   */
  final void updateChart2D (Chart2D chart2D) {

    if (getChart2DNeedsUpdate (chart2D)) {

      if (needsUpdate) {
        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }
        needsUpdate = false;
      }

      int index = -1;
      if ((index = chart2DVector.indexOf (chart2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }
}