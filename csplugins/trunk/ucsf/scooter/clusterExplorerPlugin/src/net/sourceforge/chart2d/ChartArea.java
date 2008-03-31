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
import java.text.*;


/**
 * A container containing information and components for all charts.
 */
class ChartArea extends TitledArea {


  /**
   * Used by setLabelsPrecisionNum (int num).
   * The number is 8.
   * This makes the label with the largest absolute value, have
   * as many zeroes as possible -- up to 8.
   */
  static final int MAX_INTEGER = 38;


  /**
   * Used by setLabelsPrecisionNum (int num).
   * The number is -8.
   * This makes the float labels have as many significant
   * digits as possible -- 8.
   */
  static final int MAX_FLOAT = -38;


  /**
   * Used by setLabelsPrecisionNum (int num).
   * The number is -2.
   * This makes the float labels have two significant labels.
   */
  static final int DEFAULT_FLOAT = -2;

  private Color[] datasetColors;
  private boolean betweenChartAndLegendGapExistence;
  private float betweenChartAndLegendGapToWidthRatio;
  private int betweenChartAndLegendGapThicknessModel;
  private LegendArea legend;
  private boolean legendExistence;
  private float legendToWidthRatio;
  private float legendToHeightRatio;
  private int labelsPrecisionNum;

  private boolean autoSetLayoutRatios;
  private boolean needsUpdate;


  /**
   * Creates a Chart Area with the default values.
   */
  ChartArea() {

    needsUpdate = true;
    legend = new LegendArea();

    setAutoSizes (false, true);
    setFontPointModel (16);
    setLabelsPrecisionNum (MAX_INTEGER);
    setTitleSqueeze (true);
    setBetweenChartAndLegendGapExistence (true);
    setBetweenChartAndLegendGapThicknessModel (5);
    setLegendExistence (true);
    setLegendToWidthRatio (.25f);
    setLegendToHeightRatio (1f);
    setAutoSetLayoutRatios (true);

    setDatasetColors (new Color[0]);

    resetChartAreaModel (true);
  }


  /**
   * Specifies whether the gap between the chart and the legend exists.  If the
   * legend doesn't exist, this gap will automatically not exist.
   * @param existence If true, the gap exists.
   */
  final void setBetweenChartAndLegendGapExistence (boolean existence) {
    needsUpdate = true;
    betweenChartAndLegendGapExistence = existence;
  }


  /**
   * Specifies the thickness of the gap between the chart and the legend for the
   * chart's model size.
   * @param thickness The model thickness of the gap.
   */
  final void setBetweenChartAndLegendGapThicknessModel (int thickness) {
    needsUpdate = true;
    betweenChartAndLegendGapThicknessModel = thickness;
  }


  /**
   * Indiates whether upon the next painting, the layout ratios distributing
   * the max size of the space between the components, should be set to their
   * ideal values based on particulars of the chart.
   * @param auto If true, then the ratios will be adjusted.
   */
  final void setAutoSetLayoutRatios (boolean auto) {

    needsUpdate = true;
    autoSetLayoutRatios = auto;
  }


  /**
   * Changes whether the chart title should be squeezed near to and on top of
   * the chart graph and legend.  The between title and chart gap will be
   * respected.
   * @param squeeze True if the title should be squeezed.
   */
  final void setTitleSqueeze (boolean squeeze) {

    needsUpdate = true;
    setTitleAutoLocate (!squeeze);
  }


  /**
   * Passes in a chart colors object to specify the colors for this chart.
   * @param colors The chart colors object.
   */
   final void setDatasetColors (Color[] colors) {

    needsUpdate = true;
    datasetColors = colors;
  }


  /**
   * Enables or disables the legend from calculations and painting.
   * @param existence The existence of the legend.
   */
  final void setLegendExistence (boolean existence) {

    needsUpdate = true;
    legendExistence = existence;
  }


  /**
   * Specifies how much of the maximum width less borders and gaps to make
   * availalble to the legend maximum size.
   * @param ratio The ratio to the width, to make available to the legend.
   */
  final void setLegendToWidthRatio (float ratio) {

    needsUpdate = true;
    legendToWidthRatio = ratio;
  }


  /**
   * Specifies how much of the maximum height less borders, gaps, and title to
   * make availalble to the legend maximum size.
   * @param ratio The ratio to the height, to make available to the legend.
   */
  final void setLegendToHeightRatio (float ratio) {

    needsUpdate = true;
    legendToHeightRatio = ratio;
  }


  /**
   * Changes the number of decimal places to be displayed on the data values
   * axis.  A positive int specifies how many places to the right of the decimal
   * place should be kept (Float Labels).  A non positive int specifies how many
   * zeros to the left of the decimal should occur (Integer Labels).
   * Use MAX_ZEROES if you want integer labels with only one non-zero number.
   * @param num The number of decimal places.
   */
  final void setLabelsPrecisionNum (int num) {

    needsUpdate = true;
    labelsPrecisionNum = num;
  }


  /**
   * Returns whether upon the next painting, the layout ratios distributing
   * the max size of the space between the components, should be set to their
   * ideal values based on particulars of the chart.
   * @return boolean If true, then the ratios should be adjusted.
   */
  final boolean getAutoSetLayoutRatios() {

    return autoSetLayoutRatios;
  }


  /**
   * Returns whether the gap between the chart and the legend exists.  If the
   * legend doesn't exist, this gap will automatically not exist.
   * @return boolean If true, the gap exists.
   */
  final boolean getBetweenChartAndLegendGapExistence() {
    return betweenChartAndLegendGapExistence;
  }


  /**
   * Returns the thickness of the gap between the chart and the legend for the
   * chart's model size.
   * @return int The model thickness of the gap.
   */
  final int getBetweenChartAndLegendGapThicknessModel() {
    return betweenChartAndLegendGapThicknessModel;
  }


  /**
   *  Indicates whether the title should be squeezed on top of, and near to the
   *  chart.  The title gap spacing will be respected.
   *  @return True if the title will be squeezed.
   */
  final boolean getTitleSqueeze () {

    return !getTitleAutoLocate();
  }


  /**
   * Returns the total amount of data in this set.
   * @param data set.  The data set to sum.
   * @return float The sum.
   */
  final static float getDatasetTotal (float[] dataset) {

    float total = 0f;
    for (int i = 0; i < dataset.length; ++i) total += dataset[i];
    return total;
  }


  /**
   * Returns the total amount of data in for each data category.  Found by
   * summing the data in each category, from each data set.
   * @return float[] An array of the totals of each data category.
   */
  final static float[] getDataCategoryTotals (float[][] datasets) {

    if (datasets.length > 0 && datasets[0].length > 0) {
      float[] totals = new float[datasets[0].length];
      for (int i = 0; i < datasets[0].length; ++i) {
        totals[i] = 0;
        for (int j = 0; j < datasets.length; ++j) {
          totals[i] += datasets[j][i];
        }
      }
      return totals;
    }
    else return new float[0];
  }


  /**
   * Returns the colors the auto charting algorithm chose or your custom colors.
   * @return The colors for the chart.
   */
  final Color[] getDatasetColors() {

    return datasetColors;
  }


  /**
   * Returns the colors the auto charting algorithm chose or your custom colors,
   * from the beginning index (inclusive) and to the ending index (exclusive).
   * @return The colors for the chart.
   */
  final Color[] getDatasetColors (int begin, int end) {

    Color[] subColors = new Color[end - begin];
    int j = 0;
    for (int i = begin; i < end && i < datasetColors.length; ++i) {
      subColors[j] = datasetColors[i];
      ++j;
    }
    return subColors;
  }


  /**
   * Returns the legend in order to allow customization of it.
   * @return The legend of this chart.
   */
  final LegendArea getLegend() {

    return legend;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final boolean getLegendExistence() {

    return legendExistence;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getLegendToWidthRatio() {

    return legendToWidthRatio;
  }


  /**
   * Returns this property in order for subclasses to have access to it.
   * @return The specified property.
   */
  final float getLegendToHeightRatio() {

    return legendToHeightRatio;
  }


  /**
   * Indicates how many decimal places should be in the labels of the value
   * axis.  For integer values, choose 0.  For floats, a good choice is
   * generally 2.
   * @return The number of decimal places in the labels.
   */
  final int getLabelsPrecisionNum () {

    return labelsPrecisionNum;
  }


  /**
   * Provides a more sophisticated use of Math.ceil(double).
   * For precision = 0, the return value is Math.ceil (value).
   * For precision > 1, the return value is the value obtained by the following
   * three operations:  shift decimal left precision number of places, apply
   * the resultant value in Math.ceil, shift decimal right precision number of
   * places.
   * For precision < 1, the return value is the value obtained by the following
   * three operations:  shift decimal right precision number of places, apply
   * the resultant value in Math.ceil, shift decimal left precision number of
   * places.
   * @param value The value to ceil.
   * @param precision The basically an indicator of what digit to apply ceil to.
   * @return float The resultant value.
   */
  static final float getPrecisionCeil (float value, int precision) {

    float sign = value < 0f ? -1f : 1f;
    value = value == -0f ? 0f : sign * value;
    if (precision > 0) {
      DecimalFormat df = new DecimalFormat ("#0.0#");
      String valueS = df.format (value);
      int decIndex = valueS.indexOf (df.getDecimalFormatSymbols().getDecimalSeparator());
      precision = precision < valueS.length() - (valueS.length() - decIndex) ?
        precision : valueS.length() - (valueS.length() - decIndex) - 1;
      String prefix = valueS.substring (0, decIndex - precision);
      String postfix;
      postfix = valueS.substring (decIndex - precision, decIndex) +
          valueS.substring (decIndex + 1, valueS.length());
      String toCeil = prefix + "." + postfix;
      int ceiled = (int)Math.ceil (sign * Float.parseFloat(toCeil));
      if (ceiled == 0f) return getPrecisionCeil (sign * value, --precision);
      else {
        String ceiledS = String.valueOf (ceiled);
        int i = precision;
        for (i = 0; i < precision; ++i) ceiledS += "0";
        return Float.parseFloat (ceiledS);
      }
    }
    else if (precision < 0) {
      DecimalFormat df = new DecimalFormat ("#.00#");
      String valueS = df.format (value);
      precision = -precision;
      int decIndex = valueS.indexOf (df.getDecimalFormatSymbols().getDecimalSeparator());
      precision =
        precision < valueS.length() - decIndex ? precision : valueS.length() - decIndex - 1;
      String prefix =
        valueS.substring (0, decIndex) + valueS.substring (decIndex + 1, decIndex + precision);
      String postfix = valueS.substring (decIndex + precision, valueS.length());
      String toCeil =
        prefix.substring(0, prefix.length()) +
        postfix.substring (0, 1)+ "." + postfix.substring (1, postfix.length());
      int ceiled = (int)Math.ceil (sign * Float.parseFloat (toCeil));
      String ceiledS = String.valueOf (ceiled);
      decIndex = sign < 0f ? ++decIndex : decIndex;
      prefix = ceiledS.substring (0, decIndex);
      postfix = ceiledS.substring (decIndex, ceiledS.length());
      valueS = prefix + "." + postfix;
      return Float.parseFloat (valueS);
    }
    else return (float)Math.ceil (sign * value);
  }


  /**
   * Provides a more sophisticated use of Math.floor(double).
   * For precision = 0, the return value is Math.floor (value).
   * For precision > 1, the return value is the value obtained by the following
   * three operations:  shift decimal left precision number of places, apply
   * the resultant value in Math.floor, shift decimal right precision number of
   * places.
   * For precision < 1, the return value is the value obtained by the following
   * three operations:  shift decimal right precision number of places, apply
   * the resultant value in Math.floor, shift decimal left precision number of
   * places.
   * @param value The value to floor.
   * @param precision The basically an indicator of what digit to apply floor to.
   * @return float The resultant value.
   */
  static final float getPrecisionFloor (float value, int precision) {

    float sign = value < 0f ? -1f : 1f;
    value = value == -0f ? 0f : sign * value;
    if (precision > 0) {
      DecimalFormat df =
        new DecimalFormat ("#0.0#");
      String valueS = df.format (value);
      int decIndex =
          valueS.indexOf (df.getDecimalFormatSymbols().getDecimalSeparator());
      precision = precision < valueS.length() - (valueS.length() - decIndex) ?
        precision : valueS.length() - (valueS.length() - decIndex) - 1;
      String prefix = valueS.substring (0, decIndex - precision);
      String postfix;
      postfix = valueS.substring (decIndex - precision, decIndex) +
          valueS.substring (decIndex + 1, valueS.length());
      String toFloor = prefix + "." + postfix;
      int floored = (int)Math.floor (sign * Float.parseFloat(toFloor));
      if (floored == 0f) return getPrecisionFloor (sign * value, --precision);
      else {
        String flooredS = String.valueOf (floored);
        int i = precision;
        for (i = 0; i < precision; ++i) flooredS += "0";
        return Float.parseFloat (flooredS);
      }
    }
    else if (precision < 0) {
      DecimalFormat df =
        new DecimalFormat ("#.00#");
      String valueS = df.format (value);
      precision = -precision;
      int decIndex =
          valueS.indexOf (df.getDecimalFormatSymbols().getDecimalSeparator());
      precision =
        precision < valueS.length() - decIndex?
        precision : valueS.length() - decIndex - 1;
      String prefix = valueS.substring (0, decIndex) +
        valueS.substring (decIndex + 1, decIndex + precision);
      String postfix = valueS.substring (decIndex + precision, valueS.length());
      String toFloor = prefix.substring(0, prefix.length()) +
        postfix.substring (0, 1)+ "." + postfix.substring (1, postfix.length());
      int floored = (int)Math.floor (sign * Float.parseFloat (toFloor));
      String flooredS = String.valueOf (floored);
      decIndex = sign < 0f ? ++decIndex : decIndex;
      prefix = flooredS.substring (0, decIndex);
      postfix = flooredS.substring (decIndex, flooredS.length());
      valueS = prefix + "." + postfix;
      return Float.parseFloat (valueS);
    }
    else return (float)Math.floor (sign * value);

  }


  /**
   * Provides a more sophisticated use of Math.round(double).
   * For precision = 0, the return value is Math.round (value).
   * For precision > 1, the return value is the value obtained by the following
   * three operations:  shift decimal left precision number of places, apply
   * the resultant value in Math.round, shift decimal right precision number of
   * places.
   * For precision < 1, the return value is the value obtained by the following
   * three operations:  shift decimal right precision number of places, apply
   * the resultant value in Math.round, shift decimal left precision number of
   * places.
   * @param value The value to round.
   * @param precision The basically an indicator of what digit to apply round to.
   * @return float The resultant value.
   */
  static final float getPrecisionRound (float value, int precision) {

    float sign = value < 0f ? -1f : 1f;
    value = value == -0f ? 0f : sign * value;
    if (precision > 0) {
      DecimalFormat df =
        new DecimalFormat ("#0.0#");
      String valueS = df.format (value);
      int decIndex =
          valueS.indexOf (df.getDecimalFormatSymbols().getDecimalSeparator());
      precision = precision < valueS.length() - (valueS.length() - decIndex) ?
        precision : valueS.length() - (valueS.length() - decIndex) - 1;
      String prefix = valueS.substring (0, decIndex - precision);
      String postfix;
      postfix = valueS.substring (decIndex - precision, decIndex) +
          valueS.substring (decIndex + 1, valueS.length());
      String toRound = prefix + "." + postfix;
      int rounded = (int)Math.round (sign * Float.parseFloat (toRound));
      if (rounded == 0f) return getPrecisionRound (sign * value, --precision);
      else {
        String roundedS = String.valueOf (rounded);
        int i = precision;
        for (i = 0; i < precision; ++i) roundedS += "0";
        return Float.parseFloat (roundedS);
      }
    }
    else if (precision < 0) {
      DecimalFormat df =
        new DecimalFormat ("#.00#");
      String valueS = df.format (value);
      precision = -precision;
      int decIndex =
          valueS.indexOf (df.getDecimalFormatSymbols().getDecimalSeparator());
      precision =
        precision < valueS.length() - decIndex?
        precision : valueS.length() - decIndex - 1;
      String prefix = valueS.substring (0, decIndex) +
        valueS.substring (decIndex + 1, decIndex + precision);
      String postfix = valueS.substring (decIndex + precision, valueS.length());
      String toRound = prefix.substring (0, prefix.length()) +
        postfix.substring (0, 1)+ "." + postfix.substring (1, postfix.length());
      int rounded = (int)Math.round (sign * Float.parseFloat (toRound));
      String roundedS = String.valueOf (rounded);
      decIndex = sign < 0f ? ++decIndex : decIndex;
      prefix = roundedS.substring (0, decIndex);
      postfix = roundedS.substring (decIndex, roundedS.length());
      valueS = prefix + "." + postfix;
      return Float.parseFloat (valueS);
    }
    else return (float)Math.round (sign * value);
  }


  /**
   * Returns a simple string representation of the float.
   * If places >=0, then floats will have respresentation as integers.
   * If places < 0, then floats will have floating point representation with
   * the number of places to the right of the decimal equal to |places|.
   * @param value The value to represent.
   * @param places Roughly, the number of decimal places in the representation.
   * @return The representation of the value.
   */
  static final String getFloatToString (float value, int places) {

    String format;
    value = value == -0f ? 0f : value;
    if (places < 0) {
      format = "0.0";
      for (int i = 1; i < -places; ++i) format += "0";
    }
    else format = "#";
    DecimalFormat df = new DecimalFormat (format);
    return df.format (value);
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getChartAreaNeedsUpdate() {

    return (needsUpdate || getTitledAreaNeedsUpdate() || legend.getLegendAreaNeedsUpdate());
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
  final void resetChartAreaModel (boolean reset) {

    needsUpdate = true;
    resetTitledAreaModel (reset);
    legend.resetLegendAreaModel (reset);
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  final void updateChartArea (Graphics2D g2D) {

    if (getChartAreaNeedsUpdate()) {
      updateTitledArea (g2D);
      update();
      legend.updateLegendArea (g2D);
    }
    needsUpdate = false;
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  void paintComponent (Graphics2D g2D) {

    updateChartArea (g2D);
    super.paintComponent (g2D);
    legend.paintComponent (g2D);
  }


  private void update() {
    legend.setColors (datasetColors);
  }
}