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
 * A graph chart with the data category lables located along the left of the
 * graph chart.  A graph chart is a bar chart, a line chart, a scatter plot
 * chart or any combination (i.e. any chart where data is represented in a
 * cartesian coordinate system).  This class is particulary suited for a
 * horizontal bar chart. This class manages the xAxis, yAxis, legend,
 * and graph areas of the chart.  This class cannot be added to a content pane;
 * if that is desired, use LLChart2D.  This class is for custom painting, such
 * as custom painting a JComponent (i.e. overriding the
 * paintComponent (Graphics g) method).  For customizing the LLChart2D class
 * to use with your data set and needs, you'll have to get this class
 * LLChartArea.  You'll have to pass it your data sets in an array at least.
 * You'll also want to set the title of this class, and also get its
 * yAxis and other parts for setting their titles, labels, and so on.
 */
final class LLChartArea extends GraphChartArea {


  private Rectangle maxBounds;
  private Rectangle minBounds;
  private Dimension prefSize;
  private XAxisArea xAxis;
  private YAxisArea yAxis;
  private LegendArea legend;
  private boolean needsUpdate;


  /**
   * Creates LLChartArea with GraphChartArea's defaults, and its defaults.
   */
  LLChartArea() {

    xAxis = getXAxis();
    yAxis = getYAxis();
    legend = getLegend();
    minBounds = new Rectangle();
    prefSize = new Dimension();

    xAxis.setType (LABELSLEFT);
    yAxis.setType (LABELSLEFT);
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

    updateLLChartArea (g2D);
    return prefSize;
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getLLChartAreaNeedsUpdate() {

    if (needsUpdate || getGraphChartAreaNeedsUpdate()) return true;
    Vector graphVector = getGraphVector();
    for (int i = 0; i < graphVector.size(); ++i) {
      if (((LLGraphArea)graphVector.get (i)).getLLGraphAreaNeedsUpdate())
        return true;
    }
    return false;
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
  final void resetLLChartAreaModel (boolean reset) {

    needsUpdate = true;
    resetGraphChartAreaModel (reset);
    Vector graphVector = this.getGraphVector();
    for (int i = 0; i < graphVector.size(); ++i) {
      ((LLGraphArea)graphVector.get (i)).resetLLGraphAreaModel (reset);
    }
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  final void updateLLChartArea (Graphics2D g2D) {

    if (getLLChartAreaNeedsUpdate()) {
      updateGraphChartArea (g2D);
      update (g2D);
      Vector graphVector = getGraphVector();
      for (int i = 0; i < graphVector.size(); ++i) {
        ((LLGraphArea)graphVector.get (i)).updateLLGraphArea();
      }
      Vector warningRegions = getWarningRegions();
      for (int i = 0; i < warningRegions.size(); ++i) {
        ((WarningRegion)warningRegions.get (i)).updateWarningRegion();
      }
      legend.updateLegendArea (g2D);
      xAxis.updateXAxisArea (g2D);
      yAxis.updateYAxisArea (g2D);
    }
    needsUpdate = false;
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D The graphics context for calculations and painting.
   */
  final void paintComponent (Graphics2D g2D) {

    updateLLChartArea (g2D);
    super.paintComponent (g2D);

    Vector graphVector = getGraphVector();
    for (int i = graphVector.size() - 1; i >= 0; --i) {
      ((LLGraphArea)graphVector.get (i)).paintComponent (g2D);
    }
  }


  private void update (Graphics2D g2D) {

    Vector graphVector = getGraphVector();
    Vector datasetVector = getDatasetVector();

    int colorOffset = 0;
    for (int i = 0; i < graphVector.size(); ++i) {

      int datasetsLength = ((Dataset)datasetVector.get (i)).getNumSets();
      Color[] graphColors = getDatasetColors (colorOffset, colorOffset + datasetsLength);

      LLGraphArea thisGraph = (LLGraphArea)graphVector.get(i);
      thisGraph.setBarColors (graphColors);
      thisGraph.setDotColors (graphColors);
      thisGraph.setLineColors (graphColors);
      thisGraph.setWarningRegions (getWarningRegions());
      thisGraph.setComponentsColoringByCat (getGraphComponentsColoringByCat());
      thisGraph.setComponentsColorsByCat (getGraphComponentsColorsByCat());
      colorOffset += datasetsLength;
    }

    float widthRatio = getRatio (WIDTH);
    float heightRatio = getRatio (HEIGHT);
    xAxis.setCustomRatio (WIDTH, true, widthRatio);
    xAxis.setCustomRatio (HEIGHT, true, heightRatio);
    yAxis.setCustomRatio (WIDTH, true, widthRatio);
    yAxis.setCustomRatio (HEIGHT, true, heightRatio);
    legend.setCustomRatio (WIDTH, true, widthRatio);
    legend.setCustomRatio (HEIGHT, true, heightRatio);
    for (int i = 0; i < graphVector.size(); ++i) {
      ((LLGraphArea)graphVector.get(i)).setCustomRatio (WIDTH, true, widthRatio);
      ((LLGraphArea)graphVector.get(i)).setCustomRatio (HEIGHT, true, heightRatio);
      ((LLGraphArea)graphVector.get(i)).setLabelsAxisTicksAlignment (yAxis.getTicksAlignment());
    }

    maxBounds = getMaxEntitledSpaceBounds (g2D);

    float xAxisToHeightRatio = getXAxisToHeightRatio();
    float yAxisToHeightRatio = getYAxisToHeightRatio();
    float graphToHeightRatio = getGraphToHeightRatio();
    float legendToHeightRatio = getLegendToHeightRatio();

    float xAxisToWidthRatio = getXAxisToWidthRatio();
    float yAxisToWidthRatio = getYAxisToWidthRatio();
    float graphToWidthRatio = getGraphToWidthRatio();
    float legendToWidthRatio = getLegendToWidthRatio();

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

    int width = 0, height = 0;
    if (getLegendExistence()) {
      height = (int)(legendToHeightRatio * maxBounds.height);
      width = (int)(legendToWidthRatio * availableWidth);
    }
    legend.setSize (MAX, new Dimension (width, height));

    VerticalTextListArea yTextList = yAxis.getTextList();
    yTextList.setCustomSpaceMinHeight (false, 0);
    height = (int)(yAxisToHeightRatio * maxBounds.height);
    width = (int)(yAxisToWidthRatio * availableWidth);
    yAxis.setSize (MAX, new Dimension (width, height));

    HorizontalTextListArea xTextList = xAxis.getTextList();
    xTextList.setCustomSpaceMinWidth (false, 0);
    height = (int)(xAxisToHeightRatio * maxBounds.height);
    width = (int)(xAxisToWidthRatio * availableWidth);
    xAxis.setSize (MAX, new Dimension (width, height));

    height = (int)(graphToHeightRatio * maxBounds.height);
    width = (int)(graphToWidthRatio * availableWidth);
    for (int i = 0; i < graphVector.size(); ++i) {
      ((LLGraphArea)graphVector.get(i)).setSize (MAX, new Dimension (width, height));
    }

    xAxis.setNumTicks (getNumPlotAxisLabels());  //sets here to because of below case
    yAxis.setNumTicks (yTextList.getNumBullets());  //sets here to hold intermediate calculation

    float greatestValue = -9999999999999999f;
    float leastValue = 9999999999999999f;
    for (int i = 0; i < datasetVector.size(); ++i) {
      greatestValue =
        ((Dataset)datasetVector.get (i)).getGreatest() > greatestValue ?
        ((Dataset)datasetVector.get (i)).getGreatest() : greatestValue;
      leastValue =
        ((Dataset)datasetVector.get (i)).getLeast() < leastValue ?
        ((Dataset)datasetVector.get (i)).getLeast() : leastValue;
    }

    greatestValue =
      getCustomizeGreatestValue() && (getCustomGreatestValue() > greatestValue)
      ? getCustomGreatestValue() : greatestValue;
    leastValue =
      getCustomizeLeastValue() && (getCustomLeastValue() < leastValue)
      ? getCustomLeastValue() : leastValue;

    float maxValue = getGraphableToAvailableRatio() > 0 ?
      (greatestValue - leastValue) / getGraphableToAvailableRatio() : 0f;
    float emptyValue = maxValue - (greatestValue - leastValue);

    int dataSign = GraphArea.NEG;
    if (greatestValue > 0f && leastValue < 0f) {
      greatestValue = greatestValue + (emptyValue / 2f);
      leastValue = leastValue - (emptyValue / 2f);
      float nomValue = Math.abs (greatestValue) > Math.abs (leastValue) ?
        Math.abs (greatestValue) : Math.abs (leastValue);
      greatestValue = nomValue;
      leastValue = -nomValue;
      dataSign = GraphArea.MIX;
    }
    else if (greatestValue >= 0f && leastValue >= 0f) {
      greatestValue = greatestValue + emptyValue;
      if (!getCustomizeLeastValue()) leastValue = 0f;
      dataSign = GraphArea.POS;
    }
    else {
      leastValue = leastValue - emptyValue;
      if (!getCustomizeGreatestValue()) greatestValue = 0f;
    }

    int precisionNum = getLabelsPrecisionNum();
    greatestValue = getPrecisionCeil (greatestValue, precisionNum);
    leastValue = getPrecisionFloor (leastValue, precisionNum);
    maxValue = greatestValue - leastValue;

    float difference = 0;
    float label = 0;
    if (getNumPlotAxisLabels() > 1) {
      difference = maxValue / (getNumPlotAxisLabels() - 1);
      label = leastValue;
    }
    else {
      label = maxValue / 2f;
    }

    String[] labels = new String[getNumPlotAxisLabels()];
    String lastLabel = null;
    for (int i = 0; i < getNumPlotAxisLabels(); ++i) {
      float sign = label > 0 ? 1f : -1f;
      if (i == getNumPlotAxisLabels() - 1 || i == 0) {
        labels[i] = getFloatToString (
          sign * getPrecisionRound (sign * label, precisionNum), precisionNum);
      }
      else {
        labels[i] = getFloatToString (label, precisionNum);
      }
      label += difference;
      if (labels[i].equals (lastLabel)) labels[i] = "^";
      else lastLabel = labels[i];
    }
    xAxis.getTextList().setLabels (labels);

    int graphPrefHeight = 0;
    for (int i = 0; i < graphVector.size(); ++i) {
      int numSets = ((Dataset)datasetVector.get (i)).getNumSets();
      int numCats = ((Dataset)datasetVector.get (i)).getNumCats();
      int numCompsPerCat = ((Dataset)datasetVector.get (i)).getNumItems();
      int tempHeight =
        ((LLGraphArea)graphVector.get (i)).getPrefSpaceHeight (numSets, numCats, numCompsPerCat);
      graphPrefHeight = tempHeight > graphPrefHeight ? tempHeight : graphPrefHeight;
      ((LLGraphArea)graphVector.get(i)).setDataSign (dataSign);
    }

    yTextList.setCustomSpaceMinHeight (true, graphPrefHeight);
    yAxis.updateYAxisArea (g2D);
    int minSpaceHeight = yAxis.getSpaceSize (MIN).height;

    xAxis.updateXAxisArea (g2D);
    int minSpaceWidth = 0;
    Rectangle[] xAxisTicks = xAxis.getTicks (g2D);
    if (xAxisTicks.length > 0) {
      minSpaceWidth = xAxisTicks[xAxisTicks.length - 1].x - xAxisTicks[0].x + xAxisTicks[0].width;
    }

    Dimension minGraphSpaceSize = new Dimension (minSpaceWidth, minSpaceHeight);
    for (int i = 0; i < graphVector.size(); ++i) {
      ((LLGraphArea)graphVector.get(i)).setSpaceSize (MIN, minGraphSpaceSize);
    }

    LLGraphArea graphFirst = (LLGraphArea)graphVector.get (graphVector.size() - 1);
    int graphMinSizeWidth = 0;
    int graphMinSizeHeight = 0;
    if (graphVector.size() > 0) {
      graphMinSizeWidth = graphFirst.getSize(MIN).width;
      graphMinSizeHeight = graphFirst.getSize(MIN).height;
    }

    legend.updateLegendArea(g2D);

    int height1 =  xAxis.getSize(MIN).height + graphMinSizeHeight;
    int height2 =  xAxis.getSize(MIN).height + yAxis.getSize(MIN).height;
    height = height1 > height2 ? height1 : height2;
    int heightForDeficient = height;

    int leftTickX = xAxis.getTicks(g2D)[0].x;
    int titleLeftX = xAxis.getSizeLocation(MIN).x +
      (int)((xAxis.getSize(MIN).width -
      xAxis.getTitle().getSize(MIN).width) / 2f);
    int labelLeftX = xTextList.getLabels(g2D)[0].getSizeLocation(MIN).x;
    int yAxisLeftX = leftTickX - graphFirst.getBorderThickness (LEFT) -
      yAxis.getSize(MIN).width;
    int leftX = titleLeftX < labelLeftX ? titleLeftX : labelLeftX;
    leftX = leftX < yAxisLeftX ? leftX : yAxisLeftX;

    int rightTickX = xAxis.getTicks(g2D)[xAxis.getTicks(g2D).length-1].x;
    int tickWidth = xAxis.getTicks(g2D)[0].width;
    int graphRightX = rightTickX + tickWidth +
      graphFirst.getBorderThickness (RIGHT);
    int titleRightX = titleLeftX + xAxis.getTitle().getSize(MIN).width;
    TextArea xAxisLabelRight =
      xTextList.getLabels(g2D)[xTextList.getLabels(g2D).length-1];
    int labelRightX = xAxisLabelRight.getSizeLocation(MIN).x +
      xAxisLabelRight.getSize(MIN).width;
    int legendRightX = graphRightX + betweenChartAndLegendGapThickness +
      legend.getSize(MIN).width ;
    int rightX = titleRightX > labelRightX ? titleRightX : labelRightX;
    rightX = rightX > legendRightX ? rightX : legendRightX;

    width = (rightX - leftX);

    if (getAutoSetLayoutRatios()) {

      yAxisToWidthRatio = width > 0 ? yAxis.getSize (MIN).width / (float)width : 0f;
      yAxisToWidthRatio = yAxisToWidthRatio < 1f ? yAxisToWidthRatio : 1f;
      xAxisToWidthRatio = width > 0 ? xAxis.getSize (MIN).width / (float)width : 0f;
      xAxisToWidthRatio = xAxisToWidthRatio < 1f ? xAxisToWidthRatio : 1f;
      graphToWidthRatio = width > 0 ? graphMinSizeWidth / (float)width : 0f;
      graphToWidthRatio = graphToWidthRatio < 1f ? graphToWidthRatio : 1f;
      if (xAxisToWidthRatio > graphToWidthRatio) graphToWidthRatio = xAxisToWidthRatio;
      else xAxisToWidthRatio = graphToWidthRatio;

      yAxisToHeightRatio = height > 0 ? yAxis.getSize (MIN).height / (float)height : 0f;
      yAxisToHeightRatio = yAxisToHeightRatio < 1f ? yAxisToHeightRatio : 1f;
      xAxisToHeightRatio = height > 0 ? xAxis.getSize (MIN).height / (float)height : 0f;
      xAxisToHeightRatio = xAxisToHeightRatio < 1f ? xAxisToHeightRatio : 1f;
      graphToHeightRatio = height > 0 ? graphMinSizeHeight / (float)height : 0f;
      graphToHeightRatio = graphToHeightRatio < 1f ? graphToHeightRatio : 1f;
      if (yAxisToHeightRatio > graphToHeightRatio) graphToHeightRatio = yAxisToHeightRatio;
      else yAxisToHeightRatio = graphToHeightRatio;

      if (yAxisToWidthRatio <= 0f || yAxisToHeightRatio <= 0f) {
        yAxisToWidthRatio = yAxisToHeightRatio = 0f;
      }
      if (xAxisToWidthRatio <= 0f || xAxisToHeightRatio <= 0f) {
        xAxisToWidthRatio = xAxisToHeightRatio = 0f;
      }
      if (graphToWidthRatio <= 0f || graphToHeightRatio <= 0f) {
        graphToWidthRatio = graphToHeightRatio = 0f;
      }
      legendToWidthRatio = 1f - yAxisToWidthRatio - graphToWidthRatio;
      legendToHeightRatio = 1f;

      if (legendToWidthRatio <= 0f || legendToHeightRatio <= 0f) {
        legendToWidthRatio = legendToHeightRatio = 0f;
      }

      setYAxisToWidthRatio (yAxisToWidthRatio);
      setXAxisToWidthRatio (xAxisToWidthRatio);
      setGraphToWidthRatio (graphToWidthRatio);
      setLegendToWidthRatio (legendToWidthRatio);

      setYAxisToHeightRatio (yAxisToHeightRatio);
      setXAxisToHeightRatio (xAxisToHeightRatio);
      setGraphToHeightRatio (graphToHeightRatio);
      setLegendToHeightRatio (legendToHeightRatio);

      setAutoSetLayoutRatios (false);
    }

    Dimension titleSize = getTitleSize (MIN, g2D);
    int widthForDeficient = width;
    width = titleSize.width > width ? titleSize.width : width;
    int prefWidth = width + (getSize (MIN).width - getSpaceSize (MIN).width);
    int prefHeight =
      height + (getSize (MIN).height - getSpaceSize (MIN).height) +
      titleSize.height + getBetweenTitleAndSpaceGapThickness (g2D);
    prefSize = new Dimension ((int)(1.4f * prefWidth), (int)(1.3f * prefHeight));

    int deficientWidth = 0;
    int deficientHeight = 0;
    if (getAutoSize (MIN)) {

      deficientHeight = maxBounds.height - heightForDeficient;
      deficientWidth = maxBounds.width - widthForDeficient;
    }
    else {
      deficientWidth = width - widthForDeficient;
      deficientHeight = height - heightForDeficient;
    }

    graphPrefHeight = minSpaceHeight + deficientHeight;
    yTextList.setCustomSpaceMinHeight (true, graphPrefHeight);
    yAxis.updateYAxisArea (g2D);
    minSpaceHeight = yAxis.getSize (MIN).height;

    deficientWidth += (deficientWidth / getNumPlotAxisLabels());
    int xAxisPrefWidth = xAxis.getSize(MIN).width + deficientWidth;
    xTextList.setCustomSpaceMinWidth (true, xAxisPrefWidth);

    xAxis.updateXAxisArea (g2D);
    minSpaceWidth = 0;
    xAxisTicks = xAxis.getTicks (g2D);
    if (xAxisTicks.length > 0) {
      minSpaceWidth = xAxisTicks[xAxisTicks.length - 1].x -
        xAxisTicks[0].x + xAxisTicks[0].width;
    }

    minGraphSpaceSize = new Dimension (minSpaceWidth, minSpaceHeight);
    for (int i = 0; i < graphVector.size(); ++i) {
      ((LLGraphArea)graphVector.get(i)).setSpaceSize (MIN, minGraphSpaceSize);
    }

    graphMinSizeWidth = 0;
    graphMinSizeHeight = 0;
    if (graphVector.size() > 0) {
      graphMinSizeWidth = graphFirst.getSize(MIN).width;
      graphMinSizeHeight = graphFirst.getSize(MIN).height;
    }

    legend.updateLegendArea (g2D);

    leftTickX = xAxis.getTicks(g2D)[0].x;
    titleLeftX = xAxis.getSizeLocation(MIN).x +
      (int)((xAxis.getSize(MIN).width -
      xAxis.getTitle().getSize(MIN).width) / 2f);
    labelLeftX = xTextList.getLabels(g2D)[0].getSizeLocation(MIN).x;
    yAxisLeftX = leftTickX - graphFirst.getBorderThickness (LEFT) -
      yAxis.getSize(MIN).width;
    leftX = titleLeftX < labelLeftX ? titleLeftX : labelLeftX;
    leftX = leftX < yAxisLeftX ? leftX : yAxisLeftX;

    rightTickX = xAxis.getTicks(g2D)[xAxis.getTicks(g2D).length-1].x;
    tickWidth = xAxis.getTicks(g2D)[0].width;
    graphRightX = rightTickX + tickWidth +
      graphFirst.getBorderThickness (RIGHT);
    titleRightX = titleLeftX + xAxis.getTitle().getSize(MIN).width;
    xAxisLabelRight =
      xTextList.getLabels(g2D)[xTextList.getLabels(g2D).length-1];
    labelRightX = xAxisLabelRight.getSizeLocation(MIN).x +
      xAxisLabelRight.getSize(MIN).width;
    legendRightX = graphRightX + betweenChartAndLegendGapThickness +
      legend.getSize(MIN).width ;
    rightX = titleRightX > labelRightX ? titleRightX : labelRightX;
    rightX = rightX > legendRightX ? rightX : legendRightX;

    width = (rightX - leftX);
    width = titleSize.width > width ? titleSize.width : width;

    height1 =  xAxis.getSize(MIN).height + graphMinSizeHeight;
    height2 =  xAxis.getSize(MIN).height + yAxis.getSize(MIN).height;
    height = height1 > height2 ? height1 : height2;

    float widthMultiplier = maxValue != 0 ?
      (float)(minSpaceWidth) / maxValue : 0;
    int graphLinesFillInteriorBaseValue = 0;
    if (greatestValue > 0 & leastValue < 0)
      graphLinesFillInteriorBaseValue =
        (int)Math.ceil (maxValue / 2f * widthMultiplier);
    else if (greatestValue > 0)  graphLinesFillInteriorBaseValue = 0;
    else graphLinesFillInteriorBaseValue =
      (int)Math.ceil (maxValue * widthMultiplier);
    for (int k = 0; k < graphVector.size(); ++k) {
      float[][] thisDataSet = ((Dataset)datasetVector.get (k)).getOldGraphStruct();
      int numSets = thisDataSet.length;
      int numWidths = numSets > 0 ? thisDataSet[0].length : 0;
      int[][] widths = new int[numSets][numWidths];
      int[][] barLowWidths = new int[numSets][numWidths];
      for (int i = 0; i < numWidths; ++i) {
        for (int j = 0; j < numSets; ++j) {
          if (greatestValue > 0 & leastValue < 0) {
            widths[j][i] =
              (int)((thisDataSet[j][i] + maxValue / 2f) * widthMultiplier);
            barLowWidths[j][i] =
              (int)Math.ceil (maxValue / 2f * widthMultiplier);
          }
          else if (greatestValue > 0) {
            widths[j][i] =
              (int)((thisDataSet[j][i] - leastValue) * widthMultiplier);
            barLowWidths[j][i] = 0;
          }
          else {
            widths[j][i] = (int)((thisDataSet[j][i] +
              maxValue - greatestValue) * widthMultiplier);
            barLowWidths[j][i] = (int)Math.ceil (maxValue * widthMultiplier);
          }
        }
      }
      ((LLGraphArea)graphVector.get(k)).setGraphValues (widths);
      ((LLGraphArea)graphVector.get(k)).setBarLowValues (barLowWidths);
      ((LLGraphArea)graphVector.get(k)).setLinesFillInteriorBaseValue (
        graphLinesFillInteriorBaseValue);
      ((LLGraphArea)graphVector.get(k)).setXTicks (xAxis.getTicks (g2D));
      ((LLGraphArea)graphVector.get(k)).setYTicks (yAxis.getTicks (g2D));
      ((LLGraphArea)graphVector.get(k)).updateLLGraphArea();
    }

    Vector warningRegions = getWarningRegions();
    for (int i = 0; i < warningRegions.size(); ++i) {
      WarningRegion warningRegion = (WarningRegion)warningRegions.get(i);
      if (greatestValue > 0 & leastValue < 0) {
        warningRegion.setHighGraph (
          warningRegion.getHigh() == Float.POSITIVE_INFINITY ? minSpaceWidth :
          (int)((warningRegion.getHigh() + maxValue / 2f) * widthMultiplier));
        warningRegion.setLowGraph (
          warningRegion.getLow() == Float.NEGATIVE_INFINITY ? 0f :
          (int)((warningRegion.getLow() + maxValue / 2f) * widthMultiplier));
      }
      else if (greatestValue >= 0) {
        warningRegion.setHighGraph (
          warningRegion.getHigh() == Float.POSITIVE_INFINITY ? minSpaceWidth :
          (int)((warningRegion.getHigh() - leastValue) * widthMultiplier));
        warningRegion.setLowGraph (
          warningRegion.getLow() == Float.NEGATIVE_INFINITY ? 0f :
          (int)((warningRegion.getLow() - leastValue) * widthMultiplier));
      }
      else {
        warningRegion.setHighGraph (
          warningRegion.getHigh() == Float.POSITIVE_INFINITY ? minSpaceWidth :
          (int)((warningRegion.getHigh() + maxValue - greatestValue) * widthMultiplier));
        warningRegion.setLowGraph (
          warningRegion.getLow() == Float.NEGATIVE_INFINITY ? 0f :
          (int)((warningRegion.getLow() + maxValue - greatestValue) * widthMultiplier));
      }
      if (warningRegion.getHighGraph() > minSpaceWidth)
        warningRegion.setHighGraph (minSpaceWidth);
      if (warningRegion.getLowGraph() < 0) warningRegion.setLowGraph (0);
      if (warningRegion.getHighGraph() < warningRegion.getLowGraph())
        warningRegion.setHighGraph (warningRegion.getLowGraph());
      if (warningRegion.getLowGraph() > warningRegion.getHighGraph())
        warningRegion.setLowGraph (warningRegion.getHighGraph());
      if (widthMultiplier <= 0f) {
        warningRegion.setHighGraph (0);
        warningRegion.setLowGraph (0);
      }
    }
    //no need to set warning regions for graph areas because this has already been done

    minBounds.setSize (width, height);
    if (!getAutoSize(MIN)) {
      int minWidth = titleSize.width > minBounds.width ?
        titleSize.width : minBounds.width;
      int minHeight;
      if (titleSize.height > 0 && minBounds.height > 0) {
        minHeight = titleSize.height +
          getBetweenTitleAndSpaceGapThickness (g2D) + minBounds.height;
      }
      else minHeight = titleSize.height + minBounds.height;
      setSpaceSize (MIN, new Dimension (minWidth, minHeight));
    }

    int x = maxBounds.x + (maxBounds.width - minBounds.width) / 2;
    int y = maxBounds.y + (maxBounds.height - minBounds.height) / 2;
    minBounds.setLocation (x, y);

    int graphBetweenWidth = graphFirst.getSpaceSizeLocation(MIN).x -
      graphFirst.getSizeLocation(MIN).x;
    int graphBetweenHeight = graphFirst.getSpaceSizeLocation(MIN).y -
      graphFirst.getSizeLocation(MIN).y;
    int legendBetweenWidth =
      legend.getSpaceSizeLocation(MIN).x - legend.getSizeLocation(MIN).x;

    int yAxisX, yAxisY, xAxisX, xAxisY, graphX, graphY, legendX, legendY;

    int yAxisOffsetX = yAxisLeftX - leftX;
    yAxisX = minBounds.x + yAxisOffsetX;
    int xAxisLeftX = titleLeftX < labelLeftX ? titleLeftX : labelLeftX;
    int xAxisOffsetX = xAxisLeftX - leftX;
    xAxisX = minBounds.x + xAxisOffsetX -
      (xAxisLeftX - xAxis.getSpaceSizeLocation(MIN).x);
    graphX = yAxisX + yAxis.getSize(MIN).width + graphBetweenWidth;
    legendX = minBounds.x + minBounds.width -
      legend.getSize(MIN).width + legendBetweenWidth;

    graphY = minBounds.y + graphBetweenHeight;
    yAxisY = graphY;
    xAxisY = minBounds.y + graphFirst.getSize(MIN).height;
    legendY = graphY + (graphFirst.getSpaceSize(MIN).height -
      legend.getSpaceSize(MIN).height) / 2;

    yAxis.setSpaceSizeLocation (MIN, new Point (yAxisX, yAxisY));
    yAxis.updateYAxisArea (g2D);
    xAxis.setSpaceSizeLocation (MIN, new Point (xAxisX, xAxisY));
    xAxis.updateXAxisArea(g2D);
    legend.setSpaceSizeLocation (MIN, new Point (legendX, legendY));
    legend.updateLegendArea(g2D);

    for (int i = 0; i < graphVector.size(); ++i) {
      ((LLGraphArea)graphVector.get(i)).setSpaceSizeLocation (
        MIN, new Point (graphX, graphY));
      ((LLGraphArea)graphVector.get(i)).setXTicks (xAxis.getTicks (g2D));
      ((LLGraphArea)graphVector.get(i)).setYTicks (yAxis.getTicks (g2D));
      ((LLGraphArea)graphVector.get(i)).updateLLGraphArea();
    }

    for (int i = 0; i < warningRegions.size(); ++i) {
      WarningRegion warningRegion = (WarningRegion)warningRegions.get (i);
      warningRegion.setGraphSpaceX (graphFirst.getSpaceSizeLocation (MIN).x);
      warningRegion.setGraphSpaceY (graphFirst.getSpaceSizeLocation (MIN).y);
      warningRegion.setGraphSpaceWidth (graphFirst.getSpaceSize (MIN).width);
      warningRegion.setGraphSpaceHeight (graphFirst.getSpaceSize (MIN).height);
    }

    if (getTitleSqueeze()) {
      int titleX = minBounds.x + (minBounds.width - titleSize.width) / 2;
      int titleY = minBounds.y - getBetweenTitleAndSpaceGapThickness (g2D) -
        getTitle().getSize(MIN).height;
      setTitleLocation (new Point (titleX, titleY));
    }
  }
}