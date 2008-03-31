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
import java.util.*;


/**
 * The area that the x axis and y axis attach to, and in which bars, lines, or
 * dots are painted to represent the data set.  The "LB" in the name of this
 * class stands for Labels Bottom.  This graph should always be used when the
 * data descriptors for the the graph are located on the bottom.  Data
 * descriptors have values such as September and November, and not like 0 or 50.
 * <b>Features:</b><br>
 * Provides bordering functionality.  Allows lines to be painted that match
 * up with y axis and x axis tick marks in order to know where the bars, dots,
 * or plot lines are in relation to the y axis values.  Supports vertical
 * bar graphs, line charts, and scatter charts (dots), and any combintation
 * thereof.  If a bars are present in any graph, then it is highly recommended
 * that the GraphArea method, allowComponentAlignment be passed the value of
 * false.  This will ensure that all the bars are visible on the graph.  For
 * graphs that do not have bars present, the opposite is advised.  The GraphArea
 * method should be passed the value of true.  This provides a more intuitive
 * representation of the data.
 */
final class LBGraphArea extends GraphArea {

  private FancyBar[][] bars;
  private FancyDot[][] dots;
  private FancyLine[] lines;
  private boolean needsUpdate;


  /**
   * Creates a graph area with GraphArea's default values. Default values:
   * resetLBGraphAreaModel (true);
   */
  LBGraphArea() {

    bars = new FancyBar[0][0];
    dots = new FancyDot[0][0];
    lines = new FancyLine[0];

    setBarRoundingRatio (.25f);

    resetLBGraphAreaModel (true);
    needsUpdate = true;
  }


  /**
   * Does a quick calculation of the preferred width of the graph.
   * @param numSets The number of data series.
   * @param numComps The number of data categories per series.
   * @param numCompsPerCat  The number of data per series, per category.
   * @return The preferred width.
   */
  final int getPrefSpaceWidth (int numSets, int numCats, int numCompsPerCat) {

    float ratio = getRatio (WIDTH);
    int barsWidth = 0, dotsWidth = 0, linesWidth = 0, prefWidth = 0;
    if (getAllowComponentAlignment()) {
      barsWidth = getBarsExistence() ?
        (int)(numCats * (1 + (1 - getBarsWithinCategoryOverlapRatio()) *
        (numCompsPerCat - 1)) *
        applyRatio (getBarsThicknessModel(), ratio)) : 0;
      dotsWidth = getDotsExistence() ?
        (int)(numCats * (1 + (1 - getDotsWithinCategoryOverlapRatio()) *
        (numCompsPerCat - 1)) *
        applyRatio (getDotsThicknessModel(), ratio)) : 0;
      linesWidth = getLinesExistence() ?
        (int)(numCats * (1 + (1 - getLinesWithinCategoryOverlapRatio()) *
        (numCompsPerCat - 1)) *
        applyRatio (getLinesThicknessModel(), ratio)) : 0;
    }
    else {
      barsWidth = getBarsExistence() ?
        (int)(numSets * numCats *
        (1 + (1 - getBarsWithinCategoryOverlapRatio()) *
        (numCompsPerCat - 1)) *
        applyRatio (getBarsThicknessModel(), ratio)) : 0;
      dotsWidth = getDotsExistence() ?
        (int)(numSets * numCats * (1 +
        (1 - getDotsWithinCategoryOverlapRatio()) *
        (numCompsPerCat - 1)) *
        applyRatio (getDotsThicknessModel(), ratio)) : 0;
      linesWidth = getLinesExistence() ?
        (int)(numSets * numCats * (1 +
        (1 - getLinesWithinCategoryOverlapRatio()) *
        (numCompsPerCat - 1)) *
        applyRatio (getLinesThicknessModel(), ratio)) : 0;

    }
    prefWidth = barsWidth > dotsWidth ? barsWidth : dotsWidth;
    prefWidth = prefWidth > linesWidth ? prefWidth : linesWidth;
    int gapsWidth = getBetweenComponentsGapExistence() ?
      numCats * applyRatio (getBetweenComponentsGapThicknessModel(), ratio) : 0;
    prefWidth = prefWidth + gapsWidth;
    prefWidth = prefWidth < getSpaceSize(MAX).width ? prefWidth : getSpaceSize(MAX).width;
    return (prefWidth);
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getLBGraphAreaNeedsUpdate() {

    return (needsUpdate || getGraphAreaNeedsUpdate());
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
  final void resetLBGraphAreaModel (boolean reset) {

    needsUpdate = true;
    resetGraphAreaModel (reset);
  }


  /**
   * Updates this parent's variables, and this' variables.
   */
  final void updateLBGraphArea() {

    if (getLBGraphAreaNeedsUpdate()) {
      updateGraphArea();
      update();
    }
    needsUpdate = false;
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D The graphics context for calculations and painting.
   */
  final void paintComponent (Graphics2D g2D) {

    updateLBGraphArea();
    super.paintComponent (g2D);

    Shape oldClip = g2D.getClip();
    if (getClip()) g2D.setClip (new Rectangle (getSpaceSizeLocation (MIN), getSpaceSize (MIN)));

    Composite oldComposite = g2D.getComposite();
    g2D.setComposite (getComponentsAlphaComposite());

    int numBars = bars.length > 0 ? bars[0].length : 0;
    int numDots = dots.length > 0 ? dots[0].length : 0;
    int numLines = lines.length;
    int numSets = getGraphValues().length;
    if (!getAllowComponentAlignment() && numBars > 0) {
      for (int i = 0; i < numSets; ++i) {
        for (int j = 0; j < numBars; ++j) {
          bars[i][j].paint (g2D);
        }
      }
    }
    else if (numBars > 0) {
      int[][] graphValues = getGraphValues();
      graphValues = stackedBarConvert (graphValues, getBarLowValues());
      for (int j = 0; j < numBars; ++j) {
        int[] sorted = stackedBarSort (graphValues, j);
        for (int i = 0; i < numSets; ++i) {
          bars[sorted[i]][j].paint (g2D);
        }
      }
    }

    for (int i = numLines - 1; i >= 0 ; --i) lines[i].paint (g2D);

    for (int i = numSets - 1; i >= 0; --i) {
      for (int j = numDots - 1; j >= 0 ; --j) {
        dots[i][j].paint (g2D);
      }
    }

    g2D.setClip (oldClip);
    g2D.setComposite (oldComposite);
  }


  private void update() {

    if (getAllowComponentAlignment()) updateAllowAlignment();
    else updateDisallowAlignment();
  }


  private void updateDisallowAlignment() {

    int[][] graphLengths = getGraphValues();
    int numBars = 0, numDots = 0, numLinePoints = 0, numGaps = 0;
    int numSets = graphLengths.length;
    Rectangle[] xTicks = getXTicks();
    int numCompsPerCat = 0;
    int numCats = 0;
    if (numSets > 0) {
      numCats = getLabelsAxisTicksAlignment() == BETWEEN ?
        xTicks.length + 1 : xTicks.length;
      numBars = getBarsExistence() ? numCats : 0;
      numDots = getDotsExistence() ? numCats : 0;
      numLinePoints = getLinesExistence() ? numCats : 0;
      numGaps = getBetweenComponentsGapExistence() ? numCats : 0;
      numCompsPerCat =
        numCats > 0 ? (int)(graphLengths[0].length / numCats) : 0;
    }
    if (numSets > 0 && numCats > 0 && numCompsPerCat > 0) {

      float ratio = getRatio (WIDTH);
      int availableThickness = getSpaceSize(MIN).width;

      int betweenComponentsGapThickness = 0;
      if (numGaps > 0) {
        betweenComponentsGapThickness =
          applyRatio (getBetweenComponentsGapThicknessModel(), ratio);
        betweenComponentsGapThickness =
          numGaps * betweenComponentsGapThickness <= availableThickness ?
          betweenComponentsGapThickness : availableThickness / numGaps;
        availableThickness -= numGaps * betweenComponentsGapThickness;
      }

      int barsThickness = 0;
      if (numBars > 0) {
        barsThickness = applyRatio (getBarsThicknessModel(), ratio);
        if ((numSets * numBars * numCompsPerCat) * barsThickness <=
          availableThickness) {
          float leftover =
            (availableThickness - barsThickness *
            (numSets * numBars * numCompsPerCat)) /
            (numSets * numBars * numCompsPerCat);
          barsThickness =
            barsThickness + (int)(getBarsExcessSpaceFeedbackRatio() * leftover);
        }
        else {
          float numOverlapBarsPerCat =
            (1 + (1 - getBarsWithinCategoryOverlapRatio()) *
            (numCompsPerCat - 1));
          if (numSets * numBars * numOverlapBarsPerCat * barsThickness >
            availableThickness) {
            barsThickness = (int)(availableThickness /
              (numSets * numBars * numOverlapBarsPerCat));
          }
        }
      }
      int dotsThickness = 0;
      if (numDots > 0) {
        dotsThickness = applyRatio (getDotsThicknessModel(), getRatio (LESSER));
        if ((numSets * numDots * numCompsPerCat) * dotsThickness <=
          availableThickness) {
          float leftover =
            (availableThickness - dotsThickness *
            (numSets * numDots * numCompsPerCat)) /
            (numSets * numDots * numCompsPerCat);
          dotsThickness =
            dotsThickness + (int)(getDotsExcessSpaceFeedbackRatio() * leftover);
        }
        else {
          float numOverlapDotsPerCat =
            (1 + (1 - getDotsWithinCategoryOverlapRatio()) *
            (numCompsPerCat - 1));
          if (numSets * numDots * numOverlapDotsPerCat * dotsThickness >
            availableThickness) {
            dotsThickness = (int)(availableThickness /
              (numSets * numDots * numOverlapDotsPerCat));
          }
        }
      }
      int linesThickness = 0;
      if (numLinePoints > 0) {
        linesThickness = applyRatio (getLinesThicknessModel(), getRatio (LESSER));
        if ((numSets * numLinePoints * numCompsPerCat) * linesThickness <=
          availableThickness) {
          float leftover =
            (availableThickness - linesThickness *
            (numSets * numLinePoints * numCompsPerCat)) /
            (numSets * numLinePoints * numCompsPerCat);
          linesThickness =
            linesThickness + (int)(getLinesExcessSpaceFeedbackRatio() * leftover);
        }
        else {
          float numOverlapLinesPerCat =
            (1 + (1 - getLinesWithinCategoryOverlapRatio()) *
            (numCompsPerCat - 1));
          if (numSets * numLinePoints * numOverlapLinesPerCat * linesThickness >
            availableThickness) {
            linesThickness = (int)(availableThickness /
              (numSets * numLinePoints * numOverlapLinesPerCat));
          }
        }
      }

      int componentsThickness = barsThickness;
      componentsThickness = dotsThickness > componentsThickness ?
        dotsThickness : componentsThickness;
      componentsThickness = linesThickness > componentsThickness ?
        linesThickness : componentsThickness;
      int setsThickness = componentsThickness * numSets;
      setsThickness =
        setsThickness > availableThickness / (numCats * numCompsPerCat) ?
        availableThickness / (numCats * numCompsPerCat) : setsThickness;
      componentsThickness = setsThickness / numSets;

      Rectangle2D.Float boundsGraph = new Rectangle2D.Float (
        getSpaceSizeLocation (MIN).x, getSpaceSizeLocation (MIN).y,
        getSpaceSize (MIN).width, getSpaceSize (MIN).height);

      bars = new FancyBar[numSets][numBars * numCompsPerCat];
      int[][] barLowValues = getBarLowValues();
      if (numBars > 0) {
        int y = getSpaceSizeLocation (MIN).y + getSpaceSize(MIN).height, x = 0;
        float catDelta =
          getSpaceSize (MIN).width / (float)(numBars * numCompsPerCat);
        if (numBars == 1) {
          x = getSpaceSizeLocation (MIN).x +
            (getSpaceSize(MIN).width - setsThickness) / 2;
          x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
          for (int k = 0; k < numCompsPerCat; ++k) {
            for (int j = 0; j < numSets; ++j) {
              int compX = x + j * componentsThickness +
                (componentsThickness - barsThickness) / 2;
              bars[j][k] = new FancyBar();
              bars[j][k].setDataSign (getDataSign());
              bars[j][k].setBaseValue (y - getLinesFillInteriorBaseValue());
              if (graphLengths[j][k] > barLowValues[j][k]) {
                bars[j][k].setBounds (new Rectangle2D.Float (
                  compX + k * catDelta, y - graphLengths[j][k],
                  barsThickness, graphLengths[j][k] - barLowValues[j][k]));
              }
              else {
                bars[j][k].setBounds (new Rectangle2D.Float (
                  compX + k * catDelta, y - barLowValues[j][k],
                  barsThickness,  barLowValues[j][k] - graphLengths[j][k]));
              }
              Rectangle2D.Float clipBounds = new Rectangle2D.Float (
                boundsGraph.x, bars[j][k].getBounds().y,
                boundsGraph.width, bars[j][k].getBounds().height);
              bars[j][k].setClipBounds (clipBounds);
              bars[j][k].setLightBounds (
                getComponentsLightType() == COMPONENT ? bars[j][k].getBounds() : boundsGraph);
              bars[j][k].setLightSource (getComponentsLightSource());
              bars[j][k].setColor (!getComponentsColoringByCat() ?
                getBarColors()[j] : getComponentsColorsByCat()[0]);
              bars[j][k].setOutlineExistence (getOutlineComponents());
              bars[j][k].setOutlineColor (getOutlineComponentsColor());
              bars[j][k].setArcw (getBarRoundingRatio() * barsThickness);
              bars[j][k].setArch (getBarRoundingRatio() * barsThickness);
              bars[j][k].setWarningRegions (getWarningRegions());
              bars[j][k].setType (FancyShape.LABELSBOTTOM);
              bars[j][k].setGraphBounds (boundsGraph);
            }
          }
        }
        else {
          float minX, maxX, tickHalfWidth;
          minX = getSpaceSizeLocation (MIN).x;
          tickHalfWidth = xTicks[0].width / 2f;
          maxX = xTicks[0].x + tickHalfWidth;
          int i = 0;
          do {
            if (getLabelsAxisTicksAlignment() == this.BETWEEN) {
              x = (int)(minX + Math.floor((maxX - minX - setsThickness) / 2f));
            }
            else x = (int)Math.floor (maxX - setsThickness / 2f);
            x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
            for (int k = 0; k < numCompsPerCat; ++k) {
              for (int j = 0; j < numSets; ++j) {
                int compX = x + j * componentsThickness +
                  (componentsThickness - barsThickness) / 2;
                bars[j][i * numCompsPerCat + k] = new FancyBar();
                bars[j][i * numCompsPerCat + k].setDataSign (getDataSign());
                bars[j][i * numCompsPerCat + k].setBaseValue (y - getLinesFillInteriorBaseValue());
                if (graphLengths[j][i * numCompsPerCat + k] >
                  barLowValues[j][i * numCompsPerCat + k]) {
                  bars[j][i * numCompsPerCat + k].setBounds (new Rectangle2D.Float (
                    compX + k * catDelta, y - graphLengths[j][i * numCompsPerCat + k],
                    barsThickness, graphLengths[j][i * numCompsPerCat + k] -
                    barLowValues[j][i * numCompsPerCat + k]));
                }
                else {
                  bars[j][i * numCompsPerCat + k].setBounds (new Rectangle2D.Float (
                    compX + k * catDelta, y - barLowValues[j][i * numCompsPerCat + k],
                    barsThickness,  barLowValues[j][i * numCompsPerCat + k] -
                    graphLengths[j][i * numCompsPerCat + k]));
                }
                Rectangle2D.Float clipBounds = new Rectangle2D.Float (
                  boundsGraph.x, bars[j][i * numCompsPerCat + k].getBounds().y,
                  boundsGraph.width, bars[j][i * numCompsPerCat + k].getBounds().height);
                bars[j][i * numCompsPerCat + k].setClipBounds (clipBounds);
                bars[j][i * numCompsPerCat + k].setLightBounds (
                  getComponentsLightType() == COMPONENT ?
                  bars[j][i * numCompsPerCat + k].getBounds() : boundsGraph);
                bars[j][i * numCompsPerCat + k].setLightSource (getComponentsLightSource());
                bars[j][i * numCompsPerCat + k].setColor (!getComponentsColoringByCat() ?
                  getBarColors()[j] : getComponentsColorsByCat()[i]);
                bars[j][i * numCompsPerCat + k].setOutlineExistence (getOutlineComponents());
                bars[j][i * numCompsPerCat + k].setOutlineColor (getOutlineComponentsColor());
                bars[j][i * numCompsPerCat + k].setArcw (getBarRoundingRatio() * barsThickness);
                bars[j][i * numCompsPerCat + k].setArch (getBarRoundingRatio() * barsThickness);
                bars[j][i * numCompsPerCat + k].setWarningRegions (getWarningRegions());
                bars[j][i * numCompsPerCat + k].setType (FancyShape.LABELSBOTTOM);
                bars[j][i * numCompsPerCat + k].setGraphBounds (boundsGraph);
              }
            }
            if (i + 1 == numBars) break;
            minX = maxX;
            if (i + 2 == numBars &&
              getLabelsAxisTicksAlignment() == this.BETWEEN) {
              maxX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width;
            }
            else maxX = xTicks[i + 1].x + tickHalfWidth;
            ++i;
          } while (true);
        }
      }

      dots = new FancyDot[numSets][numDots * numCompsPerCat];
      if (numDots > 0) {
        int y = getSpaceSizeLocation (MIN).y + getSpaceSize(MIN).height -
          dotsThickness / 2, x;
        float catDelta =
          getSpaceSize (MIN).width / (float)(numDots * numCompsPerCat);
        if (numDots == 1) {
          x = getSpaceSizeLocation (MIN).x +
            (getSpaceSize(MIN).width - setsThickness) / 2;
          x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
          for (int k = 0; k < numCompsPerCat; ++k) {
            for (int j = 0; j < numSets; ++j) {
              int compX = x + j * componentsThickness +
                (componentsThickness - dotsThickness) / 2;
              dots[j][k] = new FancyDot();
              dots[j][k].setBounds (new Rectangle2D.Float (
                compX + k * catDelta, y - graphLengths[j][k], dotsThickness, dotsThickness));
              dots[j][k].setClipBounds (dots[j][k].getBounds());
              dots[j][k].setColor (!getComponentsColoringByCat() ?
                getDotColors()[j] : getComponentsColorsByCat()[0]);
              dots[j][k].setOutlineExistence (getOutlineComponents());
              dots[j][k].setOutlineColor (getOutlineComponentsColor());
              dots[j][k].setLightBounds (
                getComponentsLightType() == COMPONENT ? dots[j][k].getBounds() : boundsGraph);
              dots[j][k].setLightSource (getComponentsLightSource());
              dots[j][k].setWarningRegions (getWarningRegions());
              dots[j][k].setType (FancyShape.LABELSBOTTOM);
            }
          }
        }
        else {
          float minX, maxX, tickHalfWidth;
          minX = getSpaceSizeLocation (MIN).x;
          tickHalfWidth = xTicks[0].width / 2f;
          maxX = xTicks[0].x + tickHalfWidth;
          int i = 0;
          do {
            if (getLabelsAxisTicksAlignment() == this.BETWEEN) {
              x = (int)(minX + Math.floor((maxX - minX - setsThickness) / 2f));
            }
            else x = (int)Math.floor (maxX - setsThickness / 2f);
            x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
            for (int k = 0; k < numCompsPerCat; ++k) {
              for (int j = 0; j < numSets; ++j) {
                int compX = x + j * componentsThickness +
                  (componentsThickness - dotsThickness) / 2;
                dots[j][i * numCompsPerCat + k] = new FancyDot();
                dots[j][i * numCompsPerCat + k].setBounds (new Rectangle2D.Float (
                  compX + k * catDelta, y - graphLengths[j][i * numCompsPerCat + k],
                  dotsThickness, dotsThickness));
                dots[j][i * numCompsPerCat + k].setClipBounds (
                  dots[j][i * numCompsPerCat + k].getBounds());
                dots[j][i * numCompsPerCat + k].setColor (!getComponentsColoringByCat() ?
                  getDotColors()[j] : getComponentsColorsByCat()[i]);
                dots[j][i * numCompsPerCat + k].setOutlineExistence (getOutlineComponents());
                dots[j][i * numCompsPerCat + k].setOutlineColor (getOutlineComponentsColor());
                dots[j][i * numCompsPerCat + k].setLightBounds (
                  getComponentsLightType() == COMPONENT ?
                  dots[j][i * numCompsPerCat + k].getBounds() : boundsGraph);
                dots[j][i * numCompsPerCat + k].setLightSource (getComponentsLightSource());
                dots[j][i * numCompsPerCat + k].setWarningRegions (getWarningRegions());
                dots[j][i * numCompsPerCat + k].setType (FancyShape.LABELSBOTTOM);
              }
            }
            if (i + 1 == numDots) break;
            minX = maxX;
            if (i + 2 == numDots &&
              getLabelsAxisTicksAlignment() == this.BETWEEN) {
              maxX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width;
            }
            else maxX = xTicks[i + 1].x + tickHalfWidth;
            ++i;
          } while (true);
        }
      }

      if ((numCats * numCompsPerCat) < 2 || linesThickness < 1) lines = new FancyLine[0];
      else {
        lines = new FancyLine[numSets];
        int y = getSpaceSizeLocation (MIN).y + getSpaceSize(MIN).height, x;
        for (int i = 0; i < numSets; ++i) {
          lines[i] = new FancyLine();
          lines[i].setThickness (linesThickness);
          lines[i].setFillArea (getLinesFillInterior());
          lines[i].setColor (getLineColors()[i]);
          lines[i].setOutlineExistence (getOutlineComponents());
          lines[i].setOutlineColor (getOutlineComponentsColor());
          lines[i].setLightSource (getComponentsLightSource());
          lines[i].setWarningRegions (getWarningRegions());
          lines[i].setType (FancyShape.LABELSBOTTOM);
        }
        Point firstPoints[] = new Point[numSets];
        float minX, maxX, tickHalfWidth;
        minX = getSpaceSizeLocation (MIN).x;
        tickHalfWidth = xTicks[0].width / 2f;
        maxX = xTicks[0].x + tickHalfWidth;
        float catDelta =
          getSpaceSize (MIN).width / (float)(numLinePoints * numCompsPerCat);
        int i = 0;
        do {
          if (getLabelsAxisTicksAlignment() == BETWEEN) {
            x = (int)(minX + Math.floor((maxX - minX - setsThickness) / 2f));
          }
          else x = (int)Math.floor (maxX - setsThickness / 2f);
          x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
          for (int k = 0; k < numCompsPerCat; ++k) {
            for (int j = 0; j < numSets; ++j) {
              int compX = x + j * componentsThickness + componentsThickness / 2;
              Point thisPoint = new Point (
                (int)(compX + k * catDelta),
                (int)(y - graphLengths[j][i * numCompsPerCat + k]));
              if (k == 0 && i == 0) {
                if (getLinesFillInterior()) {
                  firstPoints[j] = new Point (
                    (int)(compX + k * catDelta),
                    y - getLinesFillInteriorBaseValue());
                  lines[j].getLine().moveTo (firstPoints[j].x, firstPoints[j].y);
                  lines[j].getLine().lineTo (thisPoint.x, thisPoint.y);
                }
                else {
                  lines[j].getLine().moveTo (thisPoint.x, thisPoint.y);
                }
              }
              else {
                lines[j].getLine().lineTo (thisPoint.x, thisPoint.y);
                if (getLinesFillInterior() &&
                  k == numCompsPerCat - 1 && i == numLinePoints - 1) {
                    lines[j].getLine().lineTo (thisPoint.x, firstPoints[j].y);
                    lines[j].getLine().lineTo (firstPoints[j].x, firstPoints[j].y);
                }
              }
            }
          }
          if (i + 1 == numLinePoints) break;
          minX = maxX;
          if (i + 2 == numLinePoints &&
              getLabelsAxisTicksAlignment() == this.BETWEEN) {
            maxX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width;
          }
          else maxX = xTicks[i + 1].x + tickHalfWidth;
          ++i;
        } while (true);
        for (int g = 0; g < numSets; ++g) {
          lines[g].setClipBounds (boundsGraph);
          Rectangle2D.Float bounds = (Rectangle2D.Float)lines[g].getLine().getBounds2D();
          lines[g].setLightBounds (
            getComponentsLightType() == COMPONENT ? bounds : boundsGraph);
        }
      }
    }
    else {
      bars = new FancyBar[0][0];
      dots = new FancyDot[0][0];
      lines = new FancyLine[0];
    }
  }


  private void updateAllowAlignment() {

    int[][] graphLengths = getGraphValues();
    int numBars = 0, numDots = 0, numLinePoints = 0, numGaps = 0;
    int numSets = graphLengths.length;
    Rectangle[] xTicks = getXTicks();
    int numCompsPerCat = 0;
    int numCats = 0;
    if (numSets > 0) {
      numCats = getLabelsAxisTicksAlignment() == BETWEEN ?
        xTicks.length + 1 : xTicks.length;
      numBars = getBarsExistence() ? numCats : 0;
      numDots = getDotsExistence() ? numCats : 0;
      numLinePoints = getLinesExistence() ? numCats : 0;
      numGaps = getBetweenComponentsGapExistence() ? numCats : 0;
      numCompsPerCat = numCats > 0 ? (int)(graphLengths[0].length / numCats) : 0;
    }

    if (numSets > 0 && numCats > 0 && numCompsPerCat > 0) {
      float ratio = getRatio (WIDTH);
      int availableThickness = getSpaceSize(MIN).width;

      int betweenComponentsGapThickness = 0;
      if (numGaps > 0) {
        betweenComponentsGapThickness =
          applyRatio (getBetweenComponentsGapThicknessModel(), ratio);
        betweenComponentsGapThickness =
          numGaps * betweenComponentsGapThickness <= availableThickness ?
          betweenComponentsGapThickness : availableThickness / numGaps;
        availableThickness -= numGaps * betweenComponentsGapThickness;
      }

      int barsThickness = 0;
      if (numBars > 0) {
        barsThickness = applyRatio (getBarsThicknessModel(), ratio);
        if (numBars * numCompsPerCat * barsThickness <= availableThickness) {
          float leftover =
            (availableThickness - barsThickness *
            (numBars * numCompsPerCat)) /
            (numBars * numCompsPerCat);
          barsThickness =
            barsThickness + (int)(getBarsExcessSpaceFeedbackRatio() * leftover);
        }
        else {
          float numOverlapBarsPerCat =
            (1 + (1 - getBarsWithinCategoryOverlapRatio()) *
            (numCompsPerCat - 1));
          if (numBars * numOverlapBarsPerCat * barsThickness >
            availableThickness) {
            barsThickness =
              (int)(availableThickness / (numBars * numOverlapBarsPerCat));
          }
        }
      }
      int dotsThickness = 0;
      if (numDots > 0) {
        dotsThickness = applyRatio (getDotsThicknessModel(), getRatio (LESSER));
        if (numDots * numCompsPerCat * dotsThickness <=
          availableThickness) {
          float leftover =
            (availableThickness - dotsThickness *
            (numDots * numCompsPerCat)) /
            (numDots * numCompsPerCat);
          dotsThickness =
            dotsThickness + (int)(getDotsExcessSpaceFeedbackRatio() * leftover);
        }
        else {
          float numOverlapDotsPerCat =
            (1 + (1 - getDotsWithinCategoryOverlapRatio()) *
            (numCompsPerCat - 1));
          if (numDots * numOverlapDotsPerCat * dotsThickness >
            availableThickness) {
            dotsThickness =
              (int)(availableThickness / (numDots * numOverlapDotsPerCat));
          }
        }
      }
      int linesThickness = 0;
      if (numLinePoints > 0) {
        linesThickness =
          applyRatio (getLinesThicknessModel(), getRatio (LESSER));
        if (numLinePoints * numCompsPerCat * linesThickness <=
          availableThickness) {
          float leftover =
            (availableThickness - linesThickness *
            (numLinePoints * numCompsPerCat)) /
            (numLinePoints * numCompsPerCat);
          linesThickness =
            linesThickness + (int)(getLinesExcessSpaceFeedbackRatio() * leftover);
        }
        else {
          float numOverlapLinesPerCat =
            (1 + (1 - getLinesWithinCategoryOverlapRatio()) *
            (numCompsPerCat - 1));
          if (numLinePoints * numOverlapLinesPerCat * linesThickness >
            availableThickness) {
            linesThickness =
              (int)(availableThickness / (numLinePoints * numOverlapLinesPerCat));
          }
        }
      }

      Rectangle2D.Float boundsGraph = new Rectangle2D.Float (
        getSpaceSizeLocation (MIN).x, getSpaceSizeLocation (MIN).y,
        getSpaceSize (MIN).width, getSpaceSize (MIN).height);

      bars = new FancyBar[numSets][numBars * numCompsPerCat];
      int[][] barLowValues = getBarLowValues();
      if (numBars > 0) {
        int y = getSpaceSizeLocation (MIN).y + getSpaceSize(MIN).height, x;
        float catDelta =
          getSpaceSize (MIN).width / (float)(numBars * numCompsPerCat);
        if (numBars == 1) {
          x = getSpaceSizeLocation (MIN).x +
            (getSpaceSize(MIN).width - barsThickness) / 2;
          x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
          for (int k = 0; k < numCompsPerCat; ++k) {
            for (int j = 0; j < numSets; ++j) {
              bars[j][k] = new FancyBar();
              bars[j][k].setDataSign (getDataSign());
              bars[j][k].setBaseValue (y - getLinesFillInteriorBaseValue());
              if (graphLengths[j][k] > barLowValues[j][k]) {
                bars[j][k].setBounds (new Rectangle2D.Float (
                  x + k * catDelta, y - graphLengths[j][k],
                  barsThickness, graphLengths[j][k] - barLowValues[j][k]));
              }
              else {
                bars[j][k].setBounds (new Rectangle2D.Float (
                  x + k * catDelta, y - barLowValues[j][k],
                  barsThickness,  barLowValues[j][k] - graphLengths[j][k]));
              }
              Rectangle2D.Float clipBounds = new Rectangle2D.Float (
                boundsGraph.x, bars[j][k].getBounds().y,
                boundsGraph.width, bars[j][k].getBounds().height);
              bars[j][k].setClipBounds (clipBounds);
              bars[j][k].setLightBounds (
                getComponentsLightType() == COMPONENT ? bars[j][k].getBounds() : boundsGraph);
              bars[j][k].setLightSource (getComponentsLightSource());
              bars[j][k].setColor (!getComponentsColoringByCat() ?
                getBarColors()[j] : getComponentsColorsByCat()[0]);
              bars[j][k].setOutlineExistence (getOutlineComponents());
              bars[j][k].setOutlineColor (getOutlineComponentsColor());
              bars[j][k].setArcw (getBarRoundingRatio() * barsThickness);
              bars[j][k].setArch (getBarRoundingRatio() * barsThickness);
              bars[j][k].setWarningRegions (getWarningRegions());
              bars[j][k].setType (FancyShape.LABELSBOTTOM);
              bars[j][k].setGraphBounds (boundsGraph);
            }
          }
        }
        else {
          float minX, maxX, tickHalfWidth;
          minX = getSpaceSizeLocation (MIN).x;
          tickHalfWidth = xTicks[0].width / 2f;
          maxX = xTicks[0].x + tickHalfWidth;
          int i = 0;
          do {
            if (getLabelsAxisTicksAlignment() == BETWEEN) {
              x = (int)(minX + Math.floor((maxX - minX - barsThickness) / 2f));
            }
            else x = (int)Math.floor (maxX - barsThickness / 2f);
            x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
            for (int k = 0; k < numCompsPerCat; ++k) {
              for (int j = 0; j < numSets; ++j) {
                bars[j][i * numCompsPerCat + k] = new FancyBar();
                bars[j][i * numCompsPerCat + k].setDataSign (getDataSign());
                bars[j][i * numCompsPerCat + k].setBaseValue (y - getLinesFillInteriorBaseValue());
                if (graphLengths[j][i * numCompsPerCat + k] >
                  barLowValues[j][i * numCompsPerCat + k]) {
                  bars[j][i * numCompsPerCat + k].setBounds (new Rectangle2D.Float (
                    x + k * catDelta, y - graphLengths[j][i * numCompsPerCat + k],
                    barsThickness, graphLengths[j][i * numCompsPerCat + k] -
                    barLowValues[j][i * numCompsPerCat + k]));
                }
                else {
                  bars[j][i * numCompsPerCat + k].setBounds (new Rectangle2D.Float (
                    (int)(x + k * catDelta),
                    y - barLowValues[j][i * numCompsPerCat + k],
                    barsThickness,  barLowValues[j][i * numCompsPerCat + k] -
                    graphLengths[j][i * numCompsPerCat + k]));
                }
                Rectangle2D.Float clipBounds = new Rectangle2D.Float (
                  boundsGraph.x, bars[j][i * numCompsPerCat + k].getBounds().y,
                  boundsGraph.width, bars[j][i * numCompsPerCat + k].getBounds().height);
                bars[j][i * numCompsPerCat + k].setClipBounds (clipBounds);
                bars[j][i * numCompsPerCat + k].setLightBounds (
                  getComponentsLightType() == COMPONENT ?
                  bars[j][i * numCompsPerCat + k].getBounds() : boundsGraph);
                bars[j][i * numCompsPerCat + k].setLightSource (getComponentsLightSource());
                bars[j][i * numCompsPerCat + k].setColor (!getComponentsColoringByCat() ?
                  getBarColors()[j] : getComponentsColorsByCat()[i]);
                bars[j][i * numCompsPerCat + k].setOutlineExistence (getOutlineComponents());
                bars[j][i * numCompsPerCat + k].setOutlineColor (getOutlineComponentsColor());
                bars[j][i * numCompsPerCat + k].setArcw (getBarRoundingRatio() * barsThickness);
                bars[j][i * numCompsPerCat + k].setArch (getBarRoundingRatio() * barsThickness);
                bars[j][i * numCompsPerCat + k].setWarningRegions (getWarningRegions());
                bars[j][i * numCompsPerCat + k].setType (FancyShape.LABELSBOTTOM);
                bars[j][i * numCompsPerCat + k].setGraphBounds (boundsGraph);
              }
            }
            if (i + 1 == numBars) break;
            minX = maxX;
            if (i + 2 == numBars &&
              getLabelsAxisTicksAlignment() == BETWEEN) {
              maxX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width;
            }
            else maxX = xTicks[i + 1].x + tickHalfWidth;
            ++i;
          } while (true);
        }
      }

      dots = new FancyDot[numSets][numDots * numCompsPerCat];
      if (numDots > 0) {
        int y = getSpaceSizeLocation (MIN).y + getSpaceSize(MIN).height -
          dotsThickness / 2, x;
        float catDelta =
          getSpaceSize (MIN).width / (float)(numDots * numCompsPerCat);
        if (numDots == 1) {
          x = getSpaceSizeLocation (MIN).x +
            (getSpaceSize(MIN).width - dotsThickness) / 2;
          x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
          for (int k = 0; k < numCompsPerCat; ++k) {
            for (int j = 0; j < numSets; ++j) {
              dots[j][k] = new FancyDot();
              dots[j][k].setBounds (new Rectangle2D.Float (
                x + k * catDelta, y - graphLengths[j][k], dotsThickness, dotsThickness));
              dots[j][k].setClipBounds (dots[j][k].getBounds());
              dots[j][k].setColor (!getComponentsColoringByCat() ?
                getDotColors()[j] : getComponentsColorsByCat()[0]);
              dots[j][k].setOutlineExistence (getOutlineComponents());
              dots[j][k].setOutlineColor (getOutlineComponentsColor());
              dots[j][k].setLightBounds (
                getComponentsLightType() == COMPONENT ? dots[j][k].getBounds() : boundsGraph);
              dots[j][k].setLightSource (getComponentsLightSource());
              dots[j][k].setWarningRegions (getWarningRegions());
              dots[j][k].setType (FancyShape.LABELSBOTTOM);
            }
          }
        }
        else {
          float minX, maxX, tickHalfWidth;
          minX = getSpaceSizeLocation (MIN).x;
          tickHalfWidth = xTicks[0].width / 2f;
          maxX = xTicks[0].x + tickHalfWidth;
          int i = 0;
          do {
            if (getLabelsAxisTicksAlignment() == this.BETWEEN) {
              x = (int)(minX + Math.floor((maxX - minX - dotsThickness) / 2f));
            }
            else x = (int)Math.floor (maxX - dotsThickness / 2f);
            x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
            for (int k = 0; k < numCompsPerCat; ++k) {
              for (int j = 0; j < numSets; ++j) {
                dots[j][i * numCompsPerCat + k] = new FancyDot();
                dots[j][i * numCompsPerCat + k].setBounds (new Rectangle2D.Float (
                  x + k * catDelta, y - graphLengths[j][i * numCompsPerCat + k],
                  dotsThickness, dotsThickness));
                dots[j][i * numCompsPerCat + k].setClipBounds (
                  dots[j][i * numCompsPerCat + k].getBounds());
                dots[j][i * numCompsPerCat + k].setColor (!getComponentsColoringByCat() ?
                  getDotColors()[j] : getComponentsColorsByCat()[i]);
                dots[j][i * numCompsPerCat + k].setOutlineExistence (getOutlineComponents());
                dots[j][i * numCompsPerCat + k].setOutlineColor (getOutlineComponentsColor());
                dots[j][i * numCompsPerCat + k].setLightBounds (
                  getComponentsLightType() == COMPONENT ?
                  dots[j][i * numCompsPerCat + k].getBounds() : boundsGraph);
                dots[j][i * numCompsPerCat + k].setLightSource (getComponentsLightSource());
                dots[j][i * numCompsPerCat + k].setWarningRegions (getWarningRegions());
                dots[j][i * numCompsPerCat + k].setType (FancyShape.LABELSBOTTOM);
              }
            }
            if (i + 1 == numDots) break;
            minX = maxX;
            if (i + 2 == numDots &&
              getLabelsAxisTicksAlignment() == this.BETWEEN) {
              maxX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width;
            }
            else maxX = xTicks[i + 1].x + tickHalfWidth;
            ++i;
          } while (true);
        }
      }


      if ((numCats * numCompsPerCat) < 2 || linesThickness < 1) lines = new FancyLine[0];
      else {
        lines = new FancyLine[numSets];
        int y = getSpaceSizeLocation (MIN).y + getSpaceSize(MIN).height, x;
        for (int i = 0; i < numSets; ++i) {
          lines[i] = new FancyLine();
          lines[i].setThickness (linesThickness);
          lines[i].setFillArea (getLinesFillInterior());
          lines[i].setColor (getLineColors()[i]);
          lines[i].setOutlineExistence (getOutlineComponents());
          lines[i].setOutlineColor (getOutlineComponentsColor());
          lines[i].setLightSource (getComponentsLightSource());
          lines[i].setWarningRegions (getWarningRegions());
          lines[i].setType (FancyShape.LABELSBOTTOM);
        }
        Point firstPoints[] = new Point[numSets];
        float minX, maxX, tickHalfWidth;
        minX = getSpaceSizeLocation (MIN).x;
        tickHalfWidth = xTicks[0].width / 2f;
        maxX = xTicks[0].x + tickHalfWidth;
        float catDelta =
          getSpaceSize (MIN).width / (float)(numLinePoints * numCompsPerCat);
        int i = 0;
        do {
          if (getLabelsAxisTicksAlignment() == Area.BETWEEN) {
            x = (int)(minX + Math.floor((maxX - minX) / 2f));
          }
          else {
            x = (int)maxX;
          }
          x = (int)(x - ((numCompsPerCat - 1) / 2f) * catDelta);
          for (int k = 0; k < numCompsPerCat; ++k) {
            for (int j = 0; j < numSets; ++j) {
              Point thisPoint = new Point (
                (int)(x + k * catDelta),
                (int)(y - graphLengths[j][i * numCompsPerCat + k]));
              if (k == 0 && i == 0) {
                if (getLinesFillInterior()) {
                  firstPoints[j] = new Point (
                    (int)(x + k * catDelta),
                    y - getLinesFillInteriorBaseValue());
                  lines[j].getLine().moveTo (firstPoints[j].x, firstPoints[j].y);
                  lines[j].getLine().lineTo (thisPoint.x, thisPoint.y);
                }
                else {
                  lines[j].getLine().moveTo (thisPoint.x, thisPoint.y);
                }
              }
              else {
                lines[j].getLine().lineTo (thisPoint.x, thisPoint.y);
                if (getLinesFillInterior() &&
                  k == numCompsPerCat - 1 && i == numLinePoints - 1) {
                    lines[j].getLine().lineTo (thisPoint.x, firstPoints[j].y);
                    lines[j].getLine().lineTo (firstPoints[j].x, firstPoints[j].y);
                }
              }
            }
          }
          if (i + 1 == numLinePoints) break;
          minX = maxX;
          if (i + 2 == numLinePoints &&
            getLabelsAxisTicksAlignment() == this.BETWEEN) {
            maxX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width;
          }
          else maxX = xTicks[i + 1].x + tickHalfWidth;
          ++i;
        } while (true);
        for (int g = 0; g < numSets; ++g) {
          lines[g].setClipBounds (boundsGraph);
          Rectangle2D.Float bounds = (Rectangle2D.Float)lines[g].getLine().getBounds2D();
          lines[g].setLightBounds (getComponentsLightType() == COMPONENT ? bounds : boundsGraph);
        }
      }
    }
    else {
      bars = new FancyBar[0][0];
      dots = new FancyDot[0][0];
      lines = new FancyLine[0];
    }
  }
}