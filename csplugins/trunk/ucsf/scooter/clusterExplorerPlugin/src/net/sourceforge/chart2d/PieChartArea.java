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
 * A titled pie chart with legend exactly like PieChart2D with one difference.
 * This chart does not keep the title near to it, when auto min sizing is
 * disabled.  In this scenario, the title would be near the top of the max size
 * and the chart would be centered within the left over space.  This is not
 * desireable.  PieChart2D forces the title as near as possible to the chart
 * under unless explicitly set not to.  PieChart2D uses this class.  It
 * creates one of these charts with min sizing enabled.  That produces a
 * chart that is where the title is forced near the chart, and the chart
 * is centered in the middle of the area.  But really, if any settings of the
 * chart need to be changed they are generally changed here, or in one of its
 * components available through a get method.
 */
final class PieChartArea extends ChartArea {

  private static final int LABEL = 0;
  private static final int PIE = 1;

  private PieInfoArea pieLabels;
  private float pieToWidthRatio;
  private float pieToHeightRatio;
  private float pieLabelsToWidthRatio;
  private float pieLabelsToHeightRatio;
  private float[] dataset;
  private Dimension prefSize;
  private boolean needsUpdate;

  private boolean linesExistence;
  private GeneralPath[] lines;
  private int linesThicknessModel;
  private Color linesColor;
  private BasicStroke linesStroke;

  private boolean lineDotsExistence;
  private Ellipse2D.Float[][] lineDots;
  private int lineDotsThicknessModel;
  private Color lineDotsColor;

  private int tempX, tempY;


  /**
   * Creates a Pie Chart Area with default values.
   */
  PieChartArea() {

    pieLabels = new PieInfoArea();
    prefSize = new Dimension();
    needsUpdate = true;

    setPieLabelsToWidthRatio (.50f);
    setPieLabelsToHeightRatio (.25f);

    setLabelsLinesExistence (true);
    setLabelsLinesThicknessModel (1);
    setLabelsLinesColor (Color.black);
    setLabelsLineDotsExistence (true);
    setLabelsLineDotsThicknessModel (4);
    setLabelsLineDotsColor (Color.black);
    resetPieChartAreaModel (true);
  }


  /**
   * The dataset to plot.
   * @param set The set of data.
   */
  final void setDataset (float[] d) {

    needsUpdate = true;
    dataset = d;
  }


  /**
   * Sets the existence of the dots on the lines between the pie sector labels
   * and the pie sectors.
   * @param existence If true, the dots will be painted.
   */
  final void setLabelsLineDotsExistence (boolean existence) {

    lineDotsExistence = existence;
  }


  /**
   * Returns the existence of the dots on the lines between the pie sector
   * labels and the pie sectors.
   * @return If true, the dots will be painted.
   */
  final boolean getLabelsLineDotsExistence() {

    return lineDotsExistence;
  }


  /**
   * Sets the model thickness of the dots on the lines between the pie sector
   * labels and the pie sectors.
   * @param thickness The model thickness of the dots.
   */
  final void setLabelsLineDotsThicknessModel (int thickness) {

    needsUpdate = true;
    lineDotsThicknessModel = thickness;
  }


  /**
   * Returns the model thickness of the dots on the lines between the pie sector
   * labels and the pie sectors.
   * @return The model thickness of the dots.
   */
  final int getLabelsLineDotsThicknessModel() {

    return lineDotsThicknessModel;
  }


  /**
   * Sets the color of the dots on the lines between the pie sector labels and
   * the pie sectors.
   * @param color The color of the dots.
   */
  final void setLabelsLineDotsColor (Color color) {

    needsUpdate = true;
    lineDotsColor = color;
  }


  /**
   * Returns the color of the dots on the lines between the pie sector labels
   * and the pie sectors.
   * @return The color of the dots.
   */
  final Color getLabelsLineDotsColor() {

    return lineDotsColor;
  }


  /**
   * Sets the existence of the lines between the pie sector labels and the pie
   * sectors.
   * @param existence If true, the lines will be painted.
   */
  final void setLabelsLinesExistence (boolean existence) {

    linesExistence = existence;
  }


  /**
   * Sets the model thickness of the lines between the pie sector labels and the
   * pie sectors.
   * @param thickness The model thickness of the lines.
   */
  final void setLabelsLinesThicknessModel (int thickness) {

    needsUpdate = true;
    linesThicknessModel = thickness;
  }


  /**
   * Return the model thickness of the lines between the pie sector labels and
   * the pie sectors.
   * @return The model thickness of the lines.
   */
  final int getLabelsLinesThicknessModel() {

    return linesThicknessModel;
  }


  /**
   * Sets the color of the lines between the pie sector labels and the pie
   * sectors.
   * @param color The color of the lines.
   */
  final void setLabelsLinesColor (Color color) {

    needsUpdate = true;
    linesColor = color;
  }


  /**
   * Returns the color of the lines between the pie sector labels and the pie
   * sectors.
   * @return The color of the lines.
   */
  final Color getLabelsLinesColor() {

    return linesColor;
  }


  /**
   * Sets the width to be shared by all the labels, beyond the width of the
   * pie.  For instance, if there are labels on the left and the right of the
   * pie, then their max widths will be equal and will each be half of the width
   * indicated by applying the ratio to the max width of the chart.
   * @param ratio The ratio for indicating the max widths of the labels.
   */
  final void setPieLabelsToWidthRatio (float ratio) {

    needsUpdate = true;
    pieLabelsToWidthRatio = ratio;
  }


  /**
   * Sets the height to be shared by all the labels, beyond the height of the
   * pie.  For instance, if there are lables on the top and the bottom of the
   * pie, then their max heights will be equal and will each be half of the
   * height indicated by applying the ratio to the max width of the chart.
   * @param ratio The ratio for indicating the max heights of the labels.
   */
  final void setPieLabelsToHeightRatio (float ratio) {

    needsUpdate = true;
    pieLabelsToHeightRatio = ratio;
  }


  /**
   * Returns the minimum size that the chart would need if it was to be redrawn,
   * the "preferred" size.  The preferred size is the minimum size which would
   * need to be set as the maxmodel size of the chart, if the chart was to be
   * redrawn (assuming magnification is disabled).
   * @param g2D The graphics context for calculations and painting.
   * @return The size of the minimum maxmodel for a redraw.
   */
  final Dimension getPrefSize (Graphics2D g2D) {

    if (getPieChartAreaNeedsUpdate()) updatePieChartArea (g2D);
    return prefSize;
  }


  /**
   * Returns the pie labels component of this chart in order to allow
   * customization of it.
   * @return The pie labels area.
   */
  final PieInfoArea getPieInfoArea() {
    return pieLabels;
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
   * @param reset True causes the max model to be reset upon next max sizing.
   */
  final void resetPieChartAreaModel (boolean reset) {

    resetChartAreaModel (reset);
    pieLabels.resetPieInfoAreaModel (reset);
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getPieChartAreaNeedsUpdate() {

    if (needsUpdate || getChartAreaNeedsUpdate() || pieLabels.getPieInfoAreaNeedsUpdate())
      return true;
    return false;
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  final void updatePieChartArea (Graphics2D g2D) {

    if (getPieChartAreaNeedsUpdate()) {
      updateChartArea (g2D);
      update (g2D);
      pieLabels.updatePieInfoArea (g2D);
    }
    needsUpdate = false;
  }


  /**
   * Paints pie chart area components.
   * @param g2D The graphics context to use for calculations.
   */
  final void paintComponent (Graphics2D g2D) {

    updatePieChartArea (g2D);
    super.paintComponent (g2D);
    pieLabels.paintComponent (g2D);

    if (pieLabels.getPieLabelsExistence() && linesExistence) {
      g2D.setColor (linesColor);
      g2D.setStroke (linesStroke);
      for (int i = 0; i < dataset.length; ++i) {
        g2D.draw (lines[i]);
      }
    }

    if (pieLabels.getPieLabelsExistence() && lineDotsExistence) {
      g2D.setColor (lineDotsColor);
      for (int i = 0; i < dataset.length; ++i) {
        g2D.fill (lineDots[i][LABEL]);
        g2D.fill (lineDots[i][PIE]);
      }
    }
  }


  private void update (Graphics2D g2D) {

    LegendArea legend = getLegend();

    float widthRatio = getRatio (WIDTH);
    float heightRatio = getRatio (HEIGHT);
    pieLabels.setCustomRatio (WIDTH, true, widthRatio);
    pieLabels.setCustomRatio (HEIGHT, true, heightRatio);
    legend.setCustomRatio (WIDTH, true, widthRatio);
    legend.setCustomRatio (HEIGHT, true, heightRatio);

    pieLabels.setRawLabelsPrecision (getLabelsPrecisionNum());
    pieLabels.setDatasetColors (getDatasetColors());
    pieLabels.setDataset (dataset);

    Rectangle maxBounds = getMaxEntitledSpaceBounds (g2D);

    int betweenChartAndLegendGapThickness = 0;
    int availableWidth = maxBounds.width;
    if (getBetweenChartAndLegendGapExistence() && getLegendExistence()) {

      betweenChartAndLegendGapThickness =
        applyRatio (getBetweenChartAndLegendGapThicknessModel(), getRatio (WIDTH));
      betweenChartAndLegendGapThickness =
        betweenChartAndLegendGapThickness <= availableWidth ?
        betweenChartAndLegendGapThickness : availableWidth;
      availableWidth -= betweenChartAndLegendGapThickness;
    }

    int legendWidth = 0, legendHeight = 0;
    float legendToWidthRatio = getLegendToWidthRatio();
    float legendToHeightRatio = getLegendToHeightRatio();
    if (getLegendExistence()) {
      legendWidth = (int)(legendToWidthRatio * availableWidth);
      legendHeight = (int)(legendToHeightRatio * maxBounds.height);
    }
    legend.setSize (MAX, new Dimension (legendWidth, legendHeight));
    legend.updateLegendArea (g2D);
    legendWidth = legend.getSize (MIN).width;
    legendHeight = legend.getSize (MIN).height;

    int pieLabelsWidth = 0, pieLabelsHeight = 0;
    pieLabelsWidth = (int)(pieLabelsToWidthRatio * availableWidth);
    pieLabelsHeight = (int)(pieLabelsToHeightRatio * maxBounds.height);
    pieLabels.setSize (MAX, new Dimension (pieLabelsWidth, pieLabelsHeight));
    pieLabels.setCustomSize (false, new Dimension());
    pieLabels.updatePieInfoArea (g2D);
    pieLabelsWidth = pieLabels.getSize (MIN).width;
    pieLabelsHeight = pieLabels.getSize (MIN).height;

    int width = 0, height = 0;
    width =  pieLabelsWidth + betweenChartAndLegendGapThickness + legendWidth;
    height = pieLabelsHeight > legendHeight ? pieLabelsHeight : legendHeight;

    if (getAutoSetLayoutRatios()) {

      width -= betweenChartAndLegendGapThickness;

      pieLabelsToWidthRatio = width > 0 ? pieLabelsWidth / (float)width : 0f;
      pieLabelsToWidthRatio = pieLabelsToWidthRatio < 1f ? pieLabelsToWidthRatio : 1f;
      pieLabelsToHeightRatio = height > 0 ? pieLabelsHeight / (float)height : 0f;
      pieLabelsToHeightRatio = pieLabelsToHeightRatio < 1f ? pieLabelsToHeightRatio : 1f;

      legendToWidthRatio = legendToWidthRatio != 0f ? 1f - pieLabelsToWidthRatio : 0f;
      legendToHeightRatio = legendToHeightRatio != 0f ? 1f : 0f;

      if (pieLabelsToWidthRatio <= 0f || pieLabelsToHeightRatio <=  0f) {
        pieLabelsToWidthRatio = pieLabelsToHeightRatio = 0f;
      }

      if (legendToWidthRatio <= 0f || legendToHeightRatio <=  0f) {
        legendToWidthRatio = legendToHeightRatio = 0f;
      }

      setPieLabelsToWidthRatio (pieLabelsToWidthRatio);
      setPieLabelsToHeightRatio (pieLabelsToHeightRatio);

      setLegendToWidthRatio (legendToWidthRatio);
      setLegendToHeightRatio (legendToHeightRatio);

      setAutoSetLayoutRatios (false);

      width += betweenChartAndLegendGapThickness;
    }

    Dimension titleSize = getTitleSize (MIN, g2D);
    int titleGap = getBetweenTitleAndSpaceGapThickness (g2D);
    int prefWidth1 = width + 2 * getOffsetThickness();
    int prefWidth2 = titleSize.width + 2 * getOffsetThickness();
    int prefWidth = prefWidth1 > prefWidth2 ? prefWidth1 : prefWidth2;
    int prefHeight = height + 2 * getOffsetThickness() + titleSize.height + titleGap;
    prefSize = new Dimension ((int)(1.3f * prefWidth), (int)(1.3f * prefHeight));

    if (getAutoSize (MIN)) {

      int deficientWidth = maxBounds.width - width;
      int deficientHeight = maxBounds.height - pieLabelsHeight;
      int deficient = deficientWidth < deficientHeight ? deficientWidth : deficientHeight;

      pieLabels.setCustomSize (
        true, new Dimension (pieLabelsWidth + deficientWidth, pieLabelsHeight + deficientHeight));

      pieLabels.updatePieInfoArea (g2D);

      pieLabelsWidth = pieLabels.getSize (MIN).width;
      pieLabelsHeight = pieLabels.getSize (MIN).height;

      width =  pieLabelsWidth + betweenChartAndLegendGapThickness + legendWidth;
      height = pieLabelsHeight > legendHeight ? pieLabelsHeight : legendHeight;
    }

    Rectangle minBounds = new Rectangle();
    minBounds.setSize (width, height);

    if (!getAutoSize (MIN)) {

      int minWidth = titleSize.width > minBounds.width ? titleSize.width : minBounds.width;
      int minHeight;
      if (titleSize.height > 0 && minBounds.height > 0) {
        minHeight = titleSize.height + titleGap + minBounds.height;
      }
      else minHeight = titleSize.height + minBounds.height;
      setSpaceSize (MIN, new Dimension (minWidth, minHeight));
    }

    int x = maxBounds.x + (maxBounds.width - minBounds.width) / 2;
    int y = maxBounds.y + (maxBounds.height - minBounds.height) / 2;
    minBounds.setLocation (x, y);

    if (getTitleSqueeze()) {
      int titleX = maxBounds.x + (maxBounds.width - titleSize.width) / 2;
      int titleY = minBounds.y - titleSize.height - titleGap;
      setTitleLocation (new Point (titleX, titleY));
    }

    int legendX, legendY, pieLabelsX, pieLabelsY;
    pieLabelsX = minBounds.x;
    legendX = minBounds.x + minBounds.width - legendWidth + legend.getOffsetThickness();

    if (pieLabelsHeight > legendHeight) {
      pieLabelsY = minBounds.y;
      legendY = pieLabelsY + (pieLabelsHeight - legend.getSpaceSize (MIN).height) / 2;
    }
    else {
      legendY = minBounds.y + legend.getOffsetThickness();
      pieLabelsY = legendY + (legendHeight - pieLabels.getSpaceSize (MIN).height) / 2;
    }

    pieLabels.setSpaceSizeLocation (MIN, new Point (pieLabelsX, pieLabelsY));
    pieLabels.updatePieInfoArea (g2D);

    legend.setSpaceSizeLocation (MIN, new Point (legendX, legendY));
    legend.updateLegendArea (g2D);

    PieArea pie = pieLabels.getPieArea();

    Point[] linesLabel = pieLabels.getPointsNearLabels (g2D);
    Point[] linesGap = pie.getPointsOutSectors();
    Point[] linesSector = pie.getPointsInSectors();
    lines = new GeneralPath[dataset.length];
    for (int i = 0; i < dataset.length; ++i) {

      lines[i] = new GeneralPath (GeneralPath.WIND_EVEN_ODD, 3);
      lines[i].moveTo (linesSector[i].x, linesSector[i].y);
      lines[i].lineTo (linesGap[i].x, linesGap[i].y);
      lines[i].lineTo (linesLabel[i].x, linesLabel[i].y);
    }
    int tempLinesThicknessModel =
      linesThicknessModel > 2 * pieLabels.getLabelsPointsGapThicknessModel() ?
      2 * pieLabels.getLabelsPointsGapThicknessModel() : linesThicknessModel;
    tempLinesThicknessModel =
      tempLinesThicknessModel > 2 * pie.getGapThicknessModel() ?
      2 * pie.getGapThicknessModel() : tempLinesThicknessModel;
    int linesThickness =
      applyRatio (tempLinesThicknessModel, getRatio (LESSER));
    linesThickness = linesThickness > pie.getGapThickness() ?
      pie.getGapThickness() : linesThickness;
    float[] style = {10.0f, 0.0f};
    linesStroke = new BasicStroke (
      (float)linesThickness,
      BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, style, 0.0f);

    lineDots = new Ellipse2D.Float[dataset.length][2];
    int tempLineDotsThicknessModel =
      lineDotsThicknessModel >
      2 * pieLabels.getLabelsPointsGapThicknessModel() ?
      2 * pieLabels.getLabelsPointsGapThicknessModel() : lineDotsThicknessModel;
    tempLineDotsThicknessModel =
      tempLineDotsThicknessModel > 2 * pie.getGapThicknessModel() ?
      2 * pie.getGapThicknessModel() : tempLineDotsThicknessModel;
    int lineDotsThickness =
      applyRatio (tempLineDotsThicknessModel, getRatio (LESSER));
    for (int i = 0; i < dataset.length; ++i) {

      lineDots[i][PIE] = new Ellipse2D.Float (
          linesSector[i].x - lineDotsThickness / 2f,
          linesSector[i].y - lineDotsThickness / 2f,
          lineDotsThickness, lineDotsThickness);
      lineDots[i][LABEL] = new Ellipse2D.Float (
          linesLabel[i].x - lineDotsThickness / 2f,
          linesLabel[i].y - lineDotsThickness / 2f,
          lineDotsThickness, lineDotsThickness);
    }
  }
}