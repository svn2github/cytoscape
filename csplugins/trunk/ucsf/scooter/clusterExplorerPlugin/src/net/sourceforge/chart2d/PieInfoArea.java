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
import java.util.Date;


/**
 * This class manages a set of labels that can be used to encircle a PieArea,
 * labeling its pie sectors.  The way it works is that you must set the size of
 * the max size for the class and also the size that it is supposed to
 * encircle.
 * This provides three different labeling types.
 * The text of the label can be the data number of that sector, the text of
 * the label can be a percent of the total on the data number of that sector,
 * and the text of the label can be both.
 * The font properties can be set using the methods of this' parent class.
 * This class can return a point centered near each label so that lines can
 * be drawn from the labels to their corresponding pie sectors.
 */
final class PieInfoArea extends FontArea {


  /**
   * Static variable for setLabelsType (int) method.
   * RAW indicates that the actual number represented by the sector should be
   * present.
   */
  final static int RAW = 1;


  /**
   * Static variable for setLabelsType (int) method.
   * PERCENT indicates that the percent the number represented by the sector
   * makes of the whole should should be present.
   */
  final static int PERCENT = 2;


  private int labelsType;
  private int rawLabelsPrecision;
  private boolean betweenLabelsGapExistence;
  private int betweenLabelsGapThicknessModel;
  private TextArea[] labels;
  private Point[] pointsNearLabels;
  private boolean labelsPointsGapExistence;
  private int labelsPointsGapThicknessModel;
  private PieArea pieArea;
  private Dimension interiorSize;
  private Rectangle interiorBounds;
  private boolean customSizing;
  private Dimension customSize;
  private float[] dataset;
  private Color[] datasetColors;
  private boolean pieLabelsExistence;

  private boolean needsUpdate;


  /**
   * The general constructor for this class.
   */
  PieInfoArea () {

    pieArea = new PieArea();
    setAutoSizes (false, false);
    setBackgroundExistence (false);
    setBorderExistence (false);
    setGapExistence (false);
    setAutoJustifys (false, false);
    setFontPointModel (10);
    setLabelsType (RAW + PERCENT);
    setRawLabelsPrecision (0);
    setBetweenLabelsGapExistence (true);
    setBetweenLabelsGapThicknessModel (3);
    setLabelsPointsGapExistence (true);
    setLabelsPointsGapThicknessModel (2);
    setCustomSize (false, new Dimension());
    setPieLabelsExistence (true);
    resetPieInfoAreaModel (true);
    needsUpdate = true;
  }


  /**
   * Sets whether the labels exist or not.  If the labels do not exist, their
   * space will be taken up by the pie.
   * @param existence  A boolean indicating whether the labels are painted.
   */
   final void setPieLabelsExistence (boolean existence) {

    needsUpdate = true;
    pieLabelsExistence = existence;
   }


  /**
   * Returns whether the labels exist or not.  If the labels do not exist, their
   * space will be taken up by the pie.
   * @return A boolean indicating whether the labels are painted.
   */
   final boolean getPieLabelsExistence() {

    return pieLabelsExistence;
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
  final void setDatasetColors (Color[] colors) {

    needsUpdate = true;
    datasetColors = colors;
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
  final Color[] getDatasetColors() {

    return datasetColors;
  }


  /**
   * Indicates the final size of the area.
   * Everything is calculated using the non-custom properties.  When laying
   * out the labels, the labels are mereley laid out wider than normal to the
   * point that their ends touch the edges of the custom size...
   * Hence, all extra space is added to the interior size.
   * @param customize If true, then the custom size will be used.
   * @param size The custom size if used.
   */
  final void setCustomSize (boolean customize, Dimension size) {

    needsUpdate = true;
    customSizing = customize;
    customSize = size;
  }


  /**
   * Gets in the PieArea to encircle with labels.
   * @return The PieArea.
   */
  final PieArea getPieArea() {
    return pieArea;
  }


  /**
   * Specifies whether the gaps between the labels and the points near each
   * label exist.
   * @param existence If true, the gaps exist.
   */
  final void setLabelsPointsGapExistence (boolean existence) {

    needsUpdate = true;
    labelsPointsGapExistence = existence;
  }


  /**
   * Indicates how far from the labels, the points on each label should be.
   * The points are returned by the getPointsNearLabels(...) method.
   * @param thickness The model thickness of the gap between the label and the
   * point.
   */
  final void setLabelsPointsGapThicknessModel (int thickness) {

    needsUpdate = true;
    labelsPointsGapThicknessModel = thickness;
  }


  /**
   * Indicates how many significant digits are wanted.
   * For example, if you want two decimal places, then pass -2.
   * If you want no decimal places, then pass 0.
   * For more information see ChartArea.getPrecisionRound(...).
   * @param The desired precision of the raw labels.
   */
  final void setRawLabelsPrecision (int precision) {

    needsUpdate = true;
    rawLabelsPrecision = precision;
  }


  /**
   * Indicates whether a gap should exist between the labels.
   * This method overides setBetweenLabelsGapThicknessModel(...).
   * @param existence If true, the gaps will exist.
   */
  final void setBetweenLabelsGapExistence (boolean existence) {

    needsUpdate = true;
    betweenLabelsGapExistence = existence;
  }


  /**
   * Indicates the model thickness of the gap between each label.
   * This thickness is actually the minimum model thickness.  If there is
   * extra space that the labels don't need it is divided evenly between the
   * labels, but the gap will be at least what is specified here.
   * @param thickness The model minimum thickness of the gap.
   */
  final void setBetweenLabelsGapThicknessModel (int thickness) {

    needsUpdate = true;
    betweenLabelsGapThicknessModel = thickness;
  }


  /**
   * Indirectly sets the text of the labels.  Possible values are RAW, PERCENT,
   * and RAW + PERCENT.  RAW causes the text of the label to be the actual data
   * numbers.  PERCENT causes the text to be the percent of the whole of the
   * data.  RAW + PERCENT causes the text of the label to be both, in the format
   * of "p% - d" were p is the percent and d is the data number.
   * @param type  What text occurrs in each label.
   */
  final void setLabelsType (int type) {

    needsUpdate = true;
    labelsType = type;
  }


  /**
   * Returns how far from the labels, the points on each label should be.
   * The points are returned by the getPointsNearLabels(...) method.
   * @return The model thickness of the gap between the label and the point.
   */
  final int getLabelsPointsGapThicknessModel() {

    return labelsPointsGapThicknessModel;
  }


  /**
   * Returns how many significant digits are wanted.
   * For example, if you want two decimal places, then pass -2.
   * If you want no decimal places, then pass 0.
   * For more information see ChartArea.getPrecisionRound(...).
   * @return The desired precision of the raw labels.
   */
  final int getRawLabelsPrecision() {

    return rawLabelsPrecision;
  }


  /**
   * Indirectly returns the text of the labels.  Possible values are RAW,
   * PERCENT,
   * and RAW + PERCENT.  RAW causes the text of the label to be the actual data
   * numbers.  PERCENT causes the text to be the percent of the whole of the
   * data.  RAW + PERCENT causes the text of the label to be both, in the format
   * of "p% - d" were p is the percent and d is the data number.
   * @return What text occurrs in each label.
   */
  final int getLabelsType() {

    return labelsType;
  }


  /**
   * Returns a bounding rectangle of the area the labels encircle.
   * @return The bounding rectangle of the interior.
   */
  final Rectangle getInteriorBounds (Graphics2D g2D) {

    updatePieInfoArea (g2D);
    return interiorBounds;
  }


  /**
   * Returns whether the gaps between the labels and the points near each
   * label exist.
   * @return boolean If true, the gaps exist.
   */
  final boolean getLabelsPointsGapExistence() {
    return labelsPointsGapExistence;
  }


  /**
   * Returns an array of points, one for each label, that are near the labels.
   * These points can be used for drawing lines from pie sectors to labels.
   * @return Point[] The array of points.
   */
  final Point[] getPointsNearLabels (Graphics2D g2D) {

    updatePieInfoArea (g2D);
    return pointsNearLabels;
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getPieInfoAreaNeedsUpdate() {
    return (needsUpdate || getFontAreaNeedsUpdate() || pieArea.getPieAreaNeedsUpdate());
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
  final void resetPieInfoAreaModel (boolean reset) {

    needsUpdate = true;
    resetFontAreaModel (reset);
    pieArea.resetPieAreaModel (reset);
  }


  /**
   * Updates all this classes variables.  First updates it's parent class, then
   * then updates its own variables.
   */
  final void updatePieInfoArea (Graphics2D g2D) {

    if (getPieInfoAreaNeedsUpdate()) {

      updateFontArea();
      update (g2D);
      pieArea.updatePieArea();
      for (int i = 0; i < labels.length; ++i) {
        labels[i].updateTextArea (g2D);
      }
    }
    needsUpdate = false;
  }


  /**
   * Paints this class.  Updates all variables, then paints its parent, since
   * this class itself doesn't have anything to paint.
   * @param g2D The graphics context for calculations and painting.
   */
  final void paintComponent (Graphics2D g2D) {

    updatePieInfoArea (g2D);
    super.paintComponent (g2D);

    pieArea.paintComponent (g2D);
    for (int i = 0; i < pieArea.getDataset().length; ++i) {
      labels[i].paintComponent (g2D);
    }
  }


  private void update (Graphics2D g2D) {

    float widthRatio = getRatio (WIDTH);
    float heightRatio = getRatio (HEIGHT);
    pieArea.setCustomRatio (WIDTH, true, widthRatio);
    pieArea.setCustomRatio (HEIGHT, true, heightRatio);

    pieArea.setDataset (dataset);
    pieArea.setColors (datasetColors);
    pieArea.setSize (MAX, new Dimension (100, 100));  //fake for accurate calculation
    pieArea.setCustomSpaceSize (true, new Dimension (100, 100)); //fake for accurate calculation
    pieArea.updatePieArea();
    int[] numLabelsInSectors = pieArea.getNumSectorsInQuarters();
    int[] numLabelsInQuarters = new int[4];
    numLabelsInQuarters[TOP] = numLabelsInSectors[TOP];
    numLabelsInQuarters[RIGHT] = numLabelsInSectors[RIGHT];
    numLabelsInQuarters[BOTTOM] = numLabelsInSectors[BOTTOM];
    numLabelsInQuarters[LEFT] = numLabelsInSectors[LEFT];
    pieArea.setCustomSpaceSize (false, null);
    pieArea.updatePieArea();

    int maxLabelsHorizontally =
      numLabelsInQuarters[TOP] > numLabelsInQuarters[BOTTOM] ?
      numLabelsInQuarters[TOP] : numLabelsInQuarters[BOTTOM];
    int maxLabelsVertically =
      numLabelsInQuarters[RIGHT] > numLabelsInQuarters[LEFT] ?
      numLabelsInQuarters[RIGHT] : numLabelsInQuarters[LEFT];

    int betweenLabelsGapThickness = 0;
    if (betweenLabelsGapExistence && pieLabelsExistence) {
      betweenLabelsGapThickness = applyRatio (betweenLabelsGapThicknessModel, getRatio (LESSER));
    }

    Dimension labelsSizeMax;
    if (pieLabelsExistence) {
      int labelsMaxWidth1 = getSpaceSize (MAX).width / 2 - betweenLabelsGapThickness;
      int labelsMaxWidth2 = Integer.MAX_VALUE;
      if (maxLabelsHorizontally > 0) {
        labelsMaxWidth2 =
          (getSpaceSize (MAX).width - (maxLabelsHorizontally - 1) * betweenLabelsGapThickness) /
          maxLabelsHorizontally;
      }
      int labelsMaxWidth = labelsMaxWidth1 < labelsMaxWidth2 ? labelsMaxWidth1 : labelsMaxWidth2;
      labelsMaxWidth = labelsMaxWidth > 0 ? labelsMaxWidth : 0;

      int labelsMaxHeight1 = getSpaceSize (MAX).height / 2 - betweenLabelsGapThickness;
      int labelsMaxHeight2 =
        (getSpaceSize (MAX).height - (maxLabelsVertically + 1) * betweenLabelsGapThickness) /
        (maxLabelsVertically + 2);
      int labelsMaxHeight =
        labelsMaxHeight1 < labelsMaxHeight2 ? labelsMaxHeight1 : labelsMaxHeight2;
      labelsMaxHeight = labelsMaxHeight > 0 ? labelsMaxHeight : 0;
      labelsSizeMax = new Dimension (labelsMaxWidth, labelsMaxHeight);
    }
    else labelsSizeMax = new Dimension();

    labels = new TextArea[dataset.length];
    float datasetTotal = ChartArea.getDatasetTotal (dataset);
    int labelsWidthMin = 0;
    int labelsHeightMin = 0;
    for (int i = 0; i < dataset.length; ++i) {

      TextArea label = new TextArea();
      label.setCustomRatio (WIDTH, true, getRatio (WIDTH));
      label.setCustomRatio (HEIGHT, true, getRatio (HEIGHT));
      label.setAutoJustifys (false, false);
      label.setAutoSizes (true, false);
      label.setSize (MAX, labelsSizeMax);
      label.setBackgroundExistence (false);
      label.setBorderExistence (false);
      label.setGapExistence (false);
      label.setFontColor (getFontColor());
      label.setFontName (getFontName());
      label.setFontPointModel (getFontPointModel());
      label.setFontStyle (getFontStyle());

      String text = "";
      if (labelsType == RAW) {
        text = ChartArea.getFloatToString (
          ChartArea.getPrecisionRound (dataset[i], rawLabelsPrecision), rawLabelsPrecision);
      }
      else if (labelsType == PERCENT) {
        text = ChartArea.getFloatToString (
          ChartArea.getPrecisionRound (100 * dataset[i] / datasetTotal, 0), 0) + "%";
      }
      else {
        String text1 = ChartArea.getFloatToString (
          ChartArea.getPrecisionRound (dataset[i], rawLabelsPrecision), rawLabelsPrecision);
        String text2 = ChartArea.getFloatToString (
          ChartArea.getPrecisionRound (100 * dataset[i] / datasetTotal, 0), 0) + "%";
        text = text1 + " (" + text2 + ")";
      }
      label.setText (text);

      label.updateTextArea (g2D);
      labelsWidthMin =
        label.getSize (MIN).width > labelsWidthMin ? label.getSize (MIN).width : labelsWidthMin;
      labelsHeightMin =
        label.getSize (MIN).height > labelsHeightMin ? label.getSize (MIN).height : labelsHeightMin;
      labels[i] = label;
    }

    Dimension labelsSizeMin = new Dimension (labelsWidthMin, labelsHeightMin);

    int widthMin1 =
      (maxLabelsHorizontally - 1) * betweenLabelsGapThickness +
      maxLabelsHorizontally * labelsWidthMin;
    int widthMin2 = 2 * labelsWidthMin;
    int widthMin = widthMin1 > widthMin2 ? widthMin1 : widthMin2;

    int heightMin1 =
      (maxLabelsVertically + 1) * betweenLabelsGapThickness +
      (maxLabelsVertically + 2) * labelsHeightMin;
    int heightMin2 = 2 * labelsHeightMin;
    int heightMin = heightMin1 > heightMin2 ? heightMin1 : heightMin2;

    int pieSize = 0;
    if (!getAutoSize (MIN) && !customSizing) {

      int availableWidth = getSpaceSize (MAX).width - widthMin;
      int availableHeight = getSpaceSize (MAX).height - heightMin;
      int available = availableWidth < availableHeight ? availableWidth : availableHeight;
      pieArea.setSize (MAX, new Dimension (available, available));
      pieArea.updatePieArea();
      pieSize = pieArea.getSize (MIN).width;

      widthMin += pieSize;
      heightMin += pieSize;

      setSpaceSize (MIN, new Dimension (widthMin, heightMin));
    }
    else if (!getAutoSize (MIN) && customSizing) {

      setSpaceSize (MIN, customSize);
    }

    Dimension sizeMin = getSpaceSize (MIN);

    float midPointX = getSpaceSizeLocation (MIN).x + sizeMin.width / 2f;
    float midPointY = getSpaceSizeLocation (MIN).y + sizeMin.height / 2f;

    pointsNearLabels = new Point[dataset.length];

    int labelsPointsGapThickness =
      labelsPointsGapExistence ? applyRatio (labelsPointsGapThicknessModel, getRatio (LESSER)) : 0;

    int topInteriorY = 0, leftInteriorX = 0;
    Point originTop = new Point (0, getSpaceSizeLocation (MIN).y);
    int betweenGapTop =
      (sizeMin.width - numLabelsInQuarters[TOP] * labelsWidthMin) / (numLabelsInQuarters[TOP] + 1);
    if (numLabelsInQuarters[TOP] % 2 == 0) {
      originTop.setLocation (
        midPointX - (numLabelsInQuarters[TOP] / 2) * labelsWidthMin -
        ((numLabelsInQuarters[TOP] / 2) - 1/2f) * betweenGapTop, originTop.y);
    }
    else {
      originTop.setLocation ((int)(
      midPointX - (numLabelsInQuarters[TOP] / 2f) * labelsWidthMin -
        (int)(numLabelsInQuarters[TOP] / 2) * betweenGapTop),
        originTop.y);
    }

    topInteriorY = originTop.y + labelsHeightMin;
    for (int i = 0; i < numLabelsInQuarters[TOP]; ++i) {
      labels[i].setSpaceSizeLocation (MIN, new Point (
        (int)(originTop.x + i * (labelsWidthMin + betweenGapTop) +
        (labelsWidthMin - labels[i].getSize(MIN).width) / 2f),
        originTop.y));
      pointsNearLabels[i] = new Point (
        (int)(labels[i].getSpaceSizeLocation (MIN).x +
        (labels[i].getSize (MIN).width / 2f)),
        originTop.y + labelsHeightMin + labelsPointsGapThickness);
    }

    Point originRight = new Point (
      getSpaceSizeLocation (MIN).x + sizeMin.width - labelsWidthMin, 0);
    int betweenGapRight =
      (sizeMin.height - (numLabelsInQuarters[RIGHT] + 2) * labelsHeightMin) /
      (numLabelsInQuarters[RIGHT] + 1);
    if (numLabelsInQuarters[RIGHT] % 2 == 0) {
      originRight.setLocation (originRight.x,
        (int)(midPointY - (numLabelsInQuarters[RIGHT] / 2) * labelsHeightMin -
        ((numLabelsInQuarters[RIGHT] / 2) - 1/2f) * betweenGapRight));
    }
    else {
      originRight.setLocation (originRight.x,
        (int)(midPointY - (numLabelsInQuarters[RIGHT] / 2f) * labelsHeightMin -
        (int)(numLabelsInQuarters[RIGHT] / 2) * betweenGapRight));
    }

    int datasetOffsetRight = numLabelsInQuarters[TOP];
    for (int i = 0; i < numLabelsInQuarters[RIGHT]; ++i) {
      labels[i + datasetOffsetRight].setSpaceSizeLocation (MIN, new Point (
        originRight.x +
        (labelsWidthMin - labels[i + datasetOffsetRight].getSize(MIN).width),
        (int)(originRight.y + i * (labelsHeightMin + betweenGapRight) +
        (labelsHeightMin - labels[i + datasetOffsetRight].getSize(MIN).height) /
        2f)));
      pointsNearLabels[i + datasetOffsetRight] = new Point (
        originRight.x - labelsPointsGapThickness,
        (int)(labels[i + datasetOffsetRight].getSpaceSizeLocation (MIN).y +
        (labels[i + datasetOffsetRight].getSize (MIN).height / 2f)));
    }

    Point originBottom = new Point (0,
      getSpaceSizeLocation (MIN).y + getSpaceSize (MIN).height -
      labelsHeightMin);
    int betweenGapBottom =
      (sizeMin.width - numLabelsInQuarters[BOTTOM] * labelsWidthMin) /
      (numLabelsInQuarters[BOTTOM] + 1);
    if (numLabelsInQuarters[BOTTOM] % 2 == 0) {
      originBottom.setLocation (
        midPointX - (numLabelsInQuarters[BOTTOM] / 2) * labelsWidthMin -
        ((numLabelsInQuarters[BOTTOM] / 2) - 1/2f) * betweenGapBottom,
        originBottom.y);
    }
    else {
      originBottom.setLocation ((int)(
      midPointX - (numLabelsInQuarters[BOTTOM] / 2f) * labelsWidthMin -
        (int)(numLabelsInQuarters[BOTTOM] / 2) * betweenGapBottom),
        originBottom.y);
    }

    int datasetOffsetBottom =
      numLabelsInQuarters[TOP] + numLabelsInQuarters[RIGHT];
    int j = numLabelsInQuarters[BOTTOM] - 1;
    for (int i = 0; i < numLabelsInQuarters[BOTTOM]; ++i) {
      labels[i + datasetOffsetBottom].setSpaceSizeLocation (
        MIN, new Point ((int)(originBottom.x +
        j * (labelsWidthMin + betweenGapBottom) +
        (labelsWidthMin - labels[i + datasetOffsetBottom].getSize(MIN).width) /
        2f),
        originBottom.y + (labelsHeightMin -
        labels[i + datasetOffsetBottom].getSize(MIN).height)));
      --j;
      pointsNearLabels[i + datasetOffsetBottom] = new Point (
        (int)(labels[i + datasetOffsetBottom].getSpaceSizeLocation (MIN).x +
        (labels[i + datasetOffsetBottom].getSize (MIN).width / 2f)),
        originBottom.y - labelsPointsGapThickness);
    }

    Point originLeft = new Point (getSpaceSizeLocation (MIN).x, 0);
    int betweenGapLeft =
      (sizeMin.height - (numLabelsInQuarters[LEFT] + 2) * labelsHeightMin) /
      (numLabelsInQuarters[LEFT] + 1);
    if (numLabelsInQuarters[LEFT] % 2 == 0) {
      originLeft.setLocation (originLeft.x,
        (int)(midPointY - (numLabelsInQuarters[LEFT] / 2) * labelsHeightMin -
        ((numLabelsInQuarters[LEFT] / 2) - 1/2f) * betweenGapLeft));
    }
    else {
      originLeft.setLocation (originLeft.x,
        (int)(midPointY - (numLabelsInQuarters[LEFT] / 2f) * labelsHeightMin -
        (int)(numLabelsInQuarters[LEFT] / 2) * betweenGapLeft));
    }
    leftInteriorX = originLeft.x + labelsWidthMin;

    int datasetOffsetLeft = numLabelsInQuarters[TOP] +
      numLabelsInQuarters[RIGHT] + numLabelsInQuarters[BOTTOM];
    j = numLabelsInQuarters[LEFT] - 1;
    for (int i = 0; i < numLabelsInQuarters[LEFT]; ++i) {
      labels[i + datasetOffsetLeft].setSpaceSizeLocation (MIN, new Point (
        originLeft.x + labelsWidthMin -
        labels[i + datasetOffsetLeft].getSize(MIN).width,
        (int)(originLeft.y + j * (labelsHeightMin + betweenGapLeft) +
        (labelsHeightMin -
        labels[i + datasetOffsetLeft].getSize(MIN).height) / 2f)));
      --j;
      pointsNearLabels[i + datasetOffsetLeft] = new Point (
        originLeft.x + labelsWidthMin + labelsPointsGapThickness,
        (int)(labels[i + datasetOffsetLeft].getSpaceSizeLocation (MIN).y +
        (labels[i + datasetOffsetLeft].getSize (MIN).height / 2f)));
    }

    pieArea.setCustomSpaceSize (true, new Dimension (
      originRight.x - originLeft.x - labelsWidthMin - 2 * pieArea.getOffsetThickness(),
      originBottom.y - originTop.y - labelsHeightMin - 2 * pieArea.getOffsetThickness()));
    pieArea.setSpaceSizeLocation (MIN, new Point (
      originLeft.x + labelsWidthMin + pieArea.getOffsetThickness(),
      originTop.y + labelsHeightMin + pieArea.getOffsetThickness()));
  }
}