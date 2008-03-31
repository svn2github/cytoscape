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
import java.awt.image.*;


/**
 * A PieChart2D object is an enclosed are with a title, pie sectors, pie labels, and a legend.
 * Changes through its set methods are updated upon next repaint() or getImage() calls.
 */
public final class PieChart2D extends Chart2D {


  private boolean needsUpdate;
  private PieChartArea chart;
  private BufferedImage image;
  private Dimension size;
  private Dimension imageSize;
  private Dimension prefSize;
  private boolean customizePrefSize;
  private Dimension customPrefSize;
  private PieChart2DProperties pieChart2DProps;
  private MultiColorsProperties multiColorsProps;
  private Dataset dataset;


  /**
   * Creates a PieChart2D object with its defaults.
   */
  public PieChart2D() {

    needsUpdate = true;
    chart = new PieChartArea();
    size = new Dimension();
    imageSize = new Dimension();
    prefSize = null;
    customizePrefSize = false;
    customPrefSize = null;
  }


  /**
   * Sets the PieChart2DProperties for this PieChart2D.
   * @param props The PieChart2DProperties.
   */
  public final void setPieChart2DProperties (PieChart2DProperties props) {

    needsUpdate = true;
    props.addPieChart2D (this);
    if (pieChart2DProps != null) pieChart2DProps.removePieChart2D (this);
    pieChart2DProps = props;
  }


  /**
   * Sets the Dataset for this PieChart2D.
   * @param d The Dataset.
   */
  public final void setDataset (Dataset d) {

    needsUpdate = true;
    d.addChart2D (this);
    if (dataset != null) dataset.removeChart2D (this);
    dataset = d;
  }


  /**
   * Sets the MultiColorsProperties for this PieChart2D.
   * @param props The MultiColorsProperties.
   */
  public final void setMultiColorsProperties (MultiColorsProperties props) {

    needsUpdate = true;
    props.addObject2D (this);
    if (multiColorsProps != null) multiColorsProps.removeObject2D (this);
    multiColorsProps = props;
  }


  /**
   * Sets the allocation of space to each component of a pie chart.
   * There are three components:  pieInfo and legend.
   * The pieInfoW needs to be within 0f and 1f.
   * The legendW will be 1f - pieInfoW.
   * Both the pieInfoW and the legendH will be 1f.
   * @param pieInfoW   The ratio of pieInfo width to total.
   */
  public final void setLayoutRatios (float pieInfoW) {

    needsUpdate = true;

    chart.setPieLabelsToWidthRatio (pieInfoW);
    chart.setPieLabelsToHeightRatio (1f);
    chart.setLegendToWidthRatio (1f - pieInfoW);
    chart.setLegendToHeightRatio (1f);

    chart.setAutoSetLayoutRatios (true);
  }


  /**
   * Sets a custom preferred size for the chart.
   * This custom size will override the preferred size calculations that normally occurr.
   * If null is passed, the preferred size calculations will be reinstated.
   * @param size  The custom preferred size for this chart.
   */
  public final void setPreferredSize (Dimension size) {

    needsUpdate = true;
    customizePrefSize = size != null;
    customPrefSize = size;
    prefSize = null;
  }


  /**
   * Gets the PieChart2DProperties for this PieChart2D.
   * @return The PieChart2DProperties.
   */
  public final PieChart2DProperties getPieChart2DProperties() {
    return pieChart2DProps;
  }


  /**
   * Gets the Dataset for this PieChart2D.
   * @return The Dataset.
   */
  public final Dataset getDataset() {
    return dataset;
  }


  /**
   * Gets the MultiColorsProperties for this PieChart2D.
   * @return The MultiColorsProperties.
   */
  public final MultiColorsProperties getMultiColorsProperties() {
    return multiColorsProps;
  }


  /**
   * Gets a buffered image of the chart.
   * @return An image of this chart
   */
  public final BufferedImage getImage() {

    if (getSize().width <= 0 || getSize().height <= 0) pack();
    else updateImage (getSize());

    if (!chart.getBackgroundExistence()) {

      Graphics2D imageG2D = image.createGraphics();
      imageG2D.setRenderingHint (
        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.paintComponent (imageG2D);
      Point location = chart.getSizeLocation (chart.MIN);
      imageSize = chart.getSize (chart.MIN);
      image = image.getSubimage (location.x, location.y, imageSize.width, imageSize.height);
    }

    return image;
  }


  /**
   * Gets the preferred size of the chart.
   * The preferred size is within the maximum and minimum sizes of the chart.
   * Much calculation is performed when calling this method.
   * @return The preferred minimum size of the chart.
   */
  public final Dimension getPreferredSize() {

    updateChart2D();
    updatePieChartArea();

    if (!customizePrefSize) {

      boolean autoModel = chart.getAutoSize (chart.MAXMODEL);
      boolean autoMin = chart.getAutoSize (chart.MIN);
      chart.setAutoSizes (true, false);
      chart.resetPieChartAreaModel (true);
      chart.setAutoSetLayoutRatios (true);
      chart.setSize (chart.MAX, getMaximumSize());
      BufferedImage image = new BufferedImage (
        getMaximumSize().width, getMaximumSize().height, BufferedImage.TYPE_INT_BGR);
      Graphics2D imageG2D = image.createGraphics();
      prefSize = chart.getPrefSize (imageG2D);
      chart.setAutoSizes (autoModel, autoMin);
    }
    else prefSize = customPrefSize;

    int prefWidth =
      prefSize.width < getMinimumSize().width ? getMinimumSize().width : prefSize.width;
    int prefHeight =
      prefSize.height < getMinimumSize().height ? getMinimumSize().height : prefSize.height;
    prefSize.setSize (prefWidth, prefHeight);

    this.size = prefSize;
    chart.resetPieChartAreaModel (true);
    chart.setAutoSetLayoutRatios (true);
    chart.setSize (chart.MAX, size);
    if (!chart.getBackgroundExistence()) {

      image = new BufferedImage (
          getMaximumSize().width, getMaximumSize().height, BufferedImage.TYPE_INT_BGR);
      Graphics2D imageG2D = image.createGraphics();
      imageG2D.setRenderingHint (
        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.updatePieChartArea (imageG2D);
    }
    else {

      image = new BufferedImage (size.width, size.height, BufferedImage.TYPE_INT_BGR);
      Graphics2D imageG2D = image.createGraphics();
      imageG2D.setRenderingHint (
        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.paintComponent (imageG2D);
      Point location = chart.getSizeLocation (chart.MIN);
      imageSize = chart.getSize (chart.MIN);
      image = image.getSubimage (location.x, location.y, imageSize.width, imageSize.height);
    }

    needsUpdate = false;

    return prefSize;
  }


  /**
   * Causes the object to reinintialize to it's preferred size.
   */
  public final void pack() {

    needsUpdate = true;
    setSize (getPreferredSize());
  }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  public final boolean validate (boolean debug) {

    if (debug) System.out.println ("Validating PieChart2D");

    boolean valid = true;

    if (!validateChart2D (debug)) valid = false;

    if (pieChart2DProps == null) {
      valid = false;
      if (debug) System.out.println ("PieChart2DProperties is null");
    }
    else if (!pieChart2DProps.validate (debug)) valid = false;

    if (dataset == null) {
      valid = false;
      if (debug) System.out.println ("Dataset is null");
    }
    else if (!dataset.validate (debug)) valid = false;

    if (multiColorsProps == null) {
      valid = false;
      if (debug) System.out.println ("MultiColorsProperties is null");
    }
    else if (!multiColorsProps.validate (debug)) valid = false;

    //Restriction:  If multi colors customize and num sets > 0, then custom colors > 0
    if (valid) {
      if (dataset.getNumSets() > 0 &&
        multiColorsProps.getColorsCustomize() &&
        multiColorsProps.getColorsCustom().length < 1) {
        valid = false;
        if (debug) System.out.println ("Not enough custom colors for MultiColorsProperties");
      }
    }

    if (debug) {
      if (valid) System.out.println ("PieChart2D was valid");
      else {
        System.out.println (
          "Possibly unable to check for all invalidities because of early invalidity");
        System.out.println ("PieChart2D was invalid");
      }
    }

    return valid;
  }


  /**
   * Paints the chart.
   * This is provided for the layout manager to call.
   * @param g The graphics context for calculations and painting.
   */
  public final void paintComponent (Graphics g) {

    super.paintComponent (g);
    Graphics2D g2D = (Graphics2D)g;

    updateImage (getSize());

    if (!chart.getBackgroundExistence()) {

      g2D.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.paintComponent (g2D);
    }
    else g2D.drawImage (image, 0, 0, imageSize.width, imageSize.height, this);
  }


  /**
   * Gets the PieChartArea of this PieChart2D.
   * @return The PieChartArea of this PieChart2D.
   */
  final TitledArea getObjectArea() {
    return chart;
  }


  private boolean getNeedsUpdate() {

    return (needsUpdate || size.width != getSize().width || size.height != getSize().height ||
      getNeedsUpdateChart2D() || pieChart2DProps.getPieChart2DNeedsUpdate (this) ||
      dataset.getChart2DNeedsUpdate (this) || multiColorsProps.getObject2DNeedsUpdate (this));
  }


  private void updateImage (Dimension size) {

    if (prefSize == null) getPreferredSize();

    if (getNeedsUpdate()) {

      updateChart2D();
      updatePieChartArea();

      this.size = size;
      chart.setSize (chart.MAX, size);

      if (!chart.getBackgroundExistence()) {

        image = new BufferedImage (
          getMaximumSize().width, getMaximumSize().height, BufferedImage.TYPE_INT_BGR);
        Graphics2D imageG2D = image.createGraphics();
        imageG2D.setRenderingHint (
          RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.updatePieChartArea (imageG2D);
      }
      else {

        image = new BufferedImage (
          size.width, size.height, BufferedImage.TYPE_INT_BGR);
        Graphics2D imageG2D = image.createGraphics();
        imageG2D.setRenderingHint (
          RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.paintComponent (imageG2D);
        Point location = chart.getSizeLocation (chart.MIN);
        imageSize = chart.getSize (chart.MIN);
        image = image.getSubimage (location.x, location.y, imageSize.width, imageSize.height);
      }

      needsUpdate = false;
    }
  }


  private void updatePieChartArea() {

    if (pieChart2DProps.getPieChart2DNeedsUpdate (this) ||
      dataset.getChart2DNeedsUpdate (this) || multiColorsProps.getObject2DNeedsUpdate (this)) {

      pieChart2DProps.updatePieChart2D (this);
      dataset.updateChart2D (this);
      multiColorsProps.updateObject2D (this);

      chart.setDataset (dataset.getOldPieStruct());
      chart.setDatasetColors (multiColorsProps.getColorsArray (dataset.getNumSets()));
      chart.setLabelsLinesExistence (pieChart2DProps.getPieLabelsLinesExistence());
      chart.setLabelsLinesThicknessModel (pieChart2DProps.getPieLabelsLinesThicknessModel());
      chart.setLabelsLinesColor (pieChart2DProps.getPieLabelsLinesColor());
      chart.setLabelsLineDotsExistence (pieChart2DProps.getPieLabelsLinesDotsExistence());
      chart.setLabelsLineDotsThicknessModel (pieChart2DProps.getPieLabelsLinesDotsThicknessModel());
      chart.setLabelsLineDotsColor (pieChart2DProps.getPieLabelsLinesDotsColor());

      PieInfoArea pieInfo = chart.getPieInfoArea();
      pieInfo.setPieLabelsExistence(pieChart2DProps.getPieLabelsExistence());
      pieInfo.setLabelsType (pieChart2DProps.getPieLabelsType());
      pieInfo.setBetweenLabelsGapExistence (
        pieChart2DProps.getPieLabelsBetweenLabelsGapExistence());
      pieInfo.setBetweenLabelsGapThicknessModel (
        pieChart2DProps.getPieLabelsBetweenLabelsGapThicknessModel());
      pieInfo.setLabelsPointsGapExistence (
        pieChart2DProps.getPieLabelsPointsGapOffsetExistence());
      pieInfo.setLabelsPointsGapThicknessModel (
        (int)(pieChart2DProps.getPieLabelsPointsGapOffsetModelRatio() *
        pieChart2DProps.getChartBetweenPieLabelsAndPieGapThicknessModel()));
      pieInfo.setFontPointModel (pieChart2DProps.getPieLabelsFontPointModel());
      pieInfo.setFontName (pieChart2DProps.getPieLabelsFontName());
      pieInfo.setFontColor (pieChart2DProps.getPieLabelsFontColor());
      pieInfo.setFontStyle (pieChart2DProps.getPieLabelsFontStyle());

      PieArea pie = pieInfo.getPieArea();
      pie.setGapExistence (pieChart2DProps.getChartBetweenPieLabelsAndPieGapExistence());
      pie.setGapThicknessModel (pieChart2DProps.getChartBetweenPieLabelsAndPieGapThicknessModel());
      pie.setPiePrefSizeModel (pieChart2DProps.getPiePreferredSize());
      pie.setOutlineSectors(pieChart2DProps.getPieSectorsOutlineExistence());
      pie.setOutlineSectorsColor(pieChart2DProps.getPieSectorsOutlineColor());
      pie.setSectorPointDepthRatio(pieChart2DProps.getPieLabelsPointsPieSectorsDepthRatio());
      pie.setSectorGapPointRatio(
        pieChart2DProps.getPieLabelsPointsBetweenPieAndLabelGapsDepthRatio());
      pie.setLightSource (pieChart2DProps.getPieSectorLightSource());
    }
  }
}