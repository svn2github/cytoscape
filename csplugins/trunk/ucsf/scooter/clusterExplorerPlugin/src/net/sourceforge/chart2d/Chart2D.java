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


/**
 * An abstract class for the common methods of GraphChart2D and PieChart2D.
 * These are methods for handling the Chart2DProperties object and the LegendProperties object.
 * Changes through its set methods are updated upon next repaint() or getImage() calls.
 */
public abstract class Chart2D extends Object2D {


  private Chart2DProperties chart2DProps;
  private LegendProperties legendProps;
  private boolean needsUpdate;


  /**
   * Creates a Chart2D object with its defaults.
   * A Chart2DProperties object must be set for this object before it is used.
   * An LegendProperties object must be set for this object before it is used.
   */
  public Chart2D() {
    needsUpdate = true;
  }


  /**
   * Sets the Chart2DProperties for this Chart2D.
   * @param props The Chart2DProperties.
   */
  public final void setChart2DProperties (Chart2DProperties props) {

    needsUpdate = true;
    props.addChart2D (this);
    if (chart2DProps != null) chart2DProps.removeChart2D (this);
    chart2DProps = props;
  }


  /**
   * Sets the LegendProperties for this Chart2D.
   * @param props The LegendProperties.
   */
  public final void setLegendProperties (LegendProperties props) {

    needsUpdate = true;
    props.addChart2D (this);
    if (legendProps != null) legendProps.removeChart2D (this);
    legendProps = props;
  }


  /**
   * Gets the Chart2DProperties for this Chart2D.
   * @return The Chart2DProperties.
   */
  public final Chart2DProperties getChart2DProperties() {
    return chart2DProps;
  }


  /**
   * Gets the LegendProperties for this Chart2D.
   * @return The LegendProperties.
   */
  public final LegendProperties getLegendProperties() {
    return legendProps;
  }


  /**
   * Gets whether this object needs to be updated.
   * @return If true then needs update.
   */
  final boolean getNeedsUpdateChart2D() {
    return (needsUpdate || getNeedsUpdateObject2D() ||
    chart2DProps.getChart2DNeedsUpdate (this) || legendProps.getChart2DNeedsUpdate (this));
  }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  final boolean validateChart2D (boolean debug) {

    if (debug) System.out.println ("Validating Chart2D");

    boolean valid = true;

    if (!validateObject2D (debug)) valid = false;

    if (chart2DProps == null) {
      valid = false;
      if (debug) System.out.println ("Chart2DProperties is null");
    }
    else if (!chart2DProps.validate (debug)) valid = false;

    if (legendProps == null) {
      valid = false;
      if (debug) System.out.println ("LegendProperties is null");
    }
    else if (!legendProps.validate (debug)) valid = false;

    if (debug) {
      if (valid) System.out.println ("Chart2D was valid");
      else System.out.println ("Chart2D was invalid");
    }

    return valid;
  }


  /**
   * Updates this object with the settings from the properties objects.
   */
  final void updateChart2D() {

    if (getNeedsUpdateChart2D()) {

      needsUpdate = false;

      updateObject2D();

      chart2DProps.updateChart2D (this);
      legendProps.updateChart2D (this);

      ChartArea chart = (ChartArea)getObjectArea();
      LegendArea legend = chart.getLegend();

      chart.setLabelsPrecisionNum (chart2DProps.getChartDataLabelsPrecision());
      chart.setBetweenChartAndLegendGapExistence (
        chart2DProps.getChartBetweenChartAndLegendGapExistence());
      chart.setBetweenChartAndLegendGapThicknessModel (
        chart2DProps.getChartBetweenChartAndLegendGapThicknessModel());

      chart.setLegendExistence (legendProps.getLegendExistence());
      legend.setBorderExistence (legendProps.getLegendBorderExistence());
      legend.setBorderThicknessModel (legendProps.getLegendBorderThicknessModel());
      legend.setBorderColor (legendProps.getLegendBorderColor());
      legend.setGapExistence (legendProps.getLegendGapExistence());
      legend.setGapThicknessModel (legendProps.getLegendGapThicknessModel());
      legend.setBackgroundExistence (legendProps.getLegendBackgroundExistence());
      legend.setBackgroundColor (legendProps.getLegendBackgroundColor());
      legend.setLabels (legendProps.getLegendLabelsTexts());
      legend.setFontPointModel (legendProps.getLegendLabelsFontPointModel());
      legend.setFontName (legendProps.getLegendLabelsFontName());
      legend.setFontColor (legendProps.getLegendLabelsFontColor());
      legend.setFontStyle (legendProps.getLegendLabelsFontStyle());
      legend.setBetweenBulletsGapExistence (
        legendProps.getLegendBetweenLabelsOrBulletsGapExistence());
      legend.setBetweenBulletsGapThicknessModel (
        legendProps.getLegendBetweenLabelsOrBulletsGapThicknessModel());
      legend.setBetweenLabelsGapExistence (
        legendProps.getLegendBetweenLabelsOrBulletsGapExistence());
      legend.setBetweenLabelsGapThicknessModel (
        legendProps.getLegendBetweenLabelsOrBulletsGapThicknessModel());
      legend.setBetweenBulletsAndLabelsGapExistence (
        legendProps.getLegendBetweenLabelsAndBulletsGapExistence());
      legend.setBetweenBulletsAndLabelsGapThicknessModel (
        legendProps.getLegendBetweenLabelsAndBulletsGapThicknessModel());
      legend.setBulletsOutline (legendProps.getLegendBulletsOutlineExistence());
      legend.setBulletsOutlineColor (legendProps.getLegendBulletsOutlineColor());
      legend.setBulletsSizeModel (legendProps.getLegendBulletsSizeModel());
    }
  }
}