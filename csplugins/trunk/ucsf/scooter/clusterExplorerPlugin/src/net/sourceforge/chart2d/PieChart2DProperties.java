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
import java.util.Vector;
import java.awt.Font;


/**
 * A data structure for holding the properties common to all PieChart2D charts.
 * A PieChart2D object is an enclosed are with a title, pie sectors, pie labels, and a legend.
 * Pass this to any number of PieChart2D objects.
 */
final public class PieChart2DProperties extends Properties {


  /**
   * Indicates the pie numbers labels should have raw numbers.
   */
  public static final int RAW = 1;

  /**
   * Indicates the pie numbers labels should have percent ratios.
   */
  public static final int PERCENT = 2;

  /**
   * Signifies left.
   */
  public static final int LEFT = 0;

  /**
   * Signifies right.
   */
  public static final int RIGHT = 1;

  /**
   * Signifies top.
   */
  public static final int TOP = 2;

  /**
   * Signifies bottom.
   */
  public static final int BOTTOM = 3;

  /**
   * Signifies none.
   */
  public static final int NONE = 6;

  /**
   * The default is true.
   */
  public static final boolean CHART_BETWEEN_PIE_LABELS_AND_PIE_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 6.
   */
  public static final int CHART_BETWEEN_PIE_LABELS_AND_PIE_GAP_THICKNESS_MODEL_DEFAULT = 6;

  /**
   * The default is 30.
   */
  public static final int PIE_PREFERRED_SIZE_DEFAULT = 30;

  /**
   * The default is false.
   */
  public static final boolean PIE_SECTORS_OUTLINE_EXISTENCE_DEFAULT = false;

  /**
   * The default is Color.black.
   */
  public static final Color PIE_SECTORS_OUTLINE_COLOR_DEFAULT = Color.black;

  /**
   * The default is true.
   */
  public static final boolean PIE_LABELS_EXISTENCE_DEFAULT = true;

  /**
   * The default is RAW + PERCENT.
   */
  public static final int PIE_LABELS_TYPE_DEFAULT = RAW + PERCENT;

  /**
   * The default is true.
   */
  public static final boolean PIE_LABELS_BETWEEN_LABELS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public static final int PIE_LABELS_BETWEEN_LABELS_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is true.
   */
  public static final boolean PIE_LABELS_POINTS_GAP_OFFSET_EXISTENCE_DEFAULT = true;

  /**
   * The default is .50f.
   */
  public static final float PIE_LABELS_POINTS_GAP_OFFSET_MODEL_RATIO_DEFAULT = .50f;

  /**
   * The default is .125f.
   */
  public static final float PIE_LABELS_POINTS_PIE_SECTORS_DEPTH_RATIO_DEFAULT = .125f;

  /**
   * The default is .25f.
   */
  public static final float PIE_LABELS_POINTS_BETWEEN_PIE_AND_LABEL_GAPS_DEPTH_RATIO_DEFAULT = .25f;

  /**
   * The default is true.
   */
  public static final boolean PIE_LABELS_LINES_EXISTENCE_DEFAULT = true;

  /**
   * The default is 1.
   */
  public static final int PIE_LABELS_LINES_THICKNESS_MODEL_DEFAULT = 1;

  /**
   * The default is Color.black.
   */
  public static final Color PIE_LABELS_LINES_COLOR_DEFAULT = Color.black;

  /**
   * The default is false.
   */
  public static final boolean PIE_LABELS_LINES_DOTS_EXISTENCE_DEFAULT = false;

  /**
   * The default is 2.
   */
  public static final int PIE_LABELS_LINES_DOTS_THICKNESS_MODEL_DEFAULT = 2;

  /**
   * The default is Color.black.
   */
  public static final Color PIE_LABELS_LINES_DOTS_COLOR_DEFAULT = Color.black;

  /**
   * The default is TOP.
   */
  public static final int PIE_SECTOR_LIGHT_SOURCE_DEFAULT = TOP;

  /**
   * The default is 10.
   */
  public final static int PIE_LABELS_FONT_POINT_MODEL_DEFAULT = 10;

  /**
   * The default is "SansSerif".
   */
  public final static String PIE_LABELS_FONT_NAME_DEFAULT = "SansSerif";

  /**
   * The default is Color.black.
   */
  public final static Color PIE_LABELS_FONT_COLOR_DEFAULT = Color.black;

  /**
   * The default is Font.PLAIN.
   */
  public final static int PIE_LABELS_FONT_STYLE_DEFAULT = Font.PLAIN;


  private boolean chartBetweenPieLabelsAndPieGapExistence;
  private int chartBetweenPieLabelsAndPieGapThicknessModel;
  private int piePreferredSize;

  private boolean pieLabelsExistence;
  private int pieLabelsType;
  private boolean pieLabelsBetweenLabelsGapExistence;
  private int pieLabelsBetweenLabelsGapThicknessModel;
  private boolean pieLabelsPointsGapOffsetExistence;
  private float pieLabelsPointsGapOffsetModelRatio;
  private float pieLabelsPointsPieSectorsDepthRatio;
  private float pieLabelsPointsBetweenPieAndLabelGapsDepthRatio;

  private boolean pieLabelsLinesExistence;
  private int pieLabelsLinesThicknessModel;
  private Color pieLabelsLinesColor;
  private boolean pieLabelsLinesDotsExistence;
  private int pieLabelsLinesDotsThicknessModel;
  private Color pieLabelsLinesDotsColor;

  private boolean pieSectorsOutlineExistence;
  private Color pieSectorsOutlineColor;
  private int pieSectorLightSource;

  private int pieLabelsFontPointModel;
  private String pieLabelsFontName;
  private Color pieLabelsFontColor;
  private int pieLabelsFontStyle;

  private boolean needsUpdate = true;
  private final Vector needsUpdateVector = new Vector (5, 5);
  private final Vector pieChart2DVector = new Vector (5, 5);


  /**
   * Creates a PieChart2DProperties object with the documented default values.
   */
  public PieChart2DProperties() {

    needsUpdate = true;
    setPieChart2DPropertiesToDefaults();
  }


  /**
   * Creates a PieChart2DProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param pieChart2DProps The properties to copy.
   */
  public PieChart2DProperties (PieChart2DProperties pieChart2DProps) {

    needsUpdate = true;
    setPieChart2DProperties (pieChart2DProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setPieChart2DPropertiesToDefaults() {

    needsUpdate = true;
    setChartBetweenPieLabelsAndPieGapExistence (
      CHART_BETWEEN_PIE_LABELS_AND_PIE_GAP_EXISTENCE_DEFAULT);
    setChartBetweenPieLabelsAndPieGapThicknessModel (
      CHART_BETWEEN_PIE_LABELS_AND_PIE_GAP_THICKNESS_MODEL_DEFAULT);
    setPiePreferredSize (PIE_PREFERRED_SIZE_DEFAULT);
    setPieSectorsOutlineExistence (PIE_SECTORS_OUTLINE_EXISTENCE_DEFAULT);
    setPieSectorsOutlineColor (PIE_SECTORS_OUTLINE_COLOR_DEFAULT);
    setPieLabelsExistence (PIE_LABELS_EXISTENCE_DEFAULT);
    setPieLabelsType (PIE_LABELS_TYPE_DEFAULT);
    setPieLabelsBetweenLabelsGapExistence (PIE_LABELS_BETWEEN_LABELS_GAP_EXISTENCE_DEFAULT);
    setPieLabelsBetweenLabelsGapThicknessModel (
      PIE_LABELS_BETWEEN_LABELS_GAP_THICKNESS_MODEL_DEFAULT);
    setPieLabelsPointsGapOffsetExistence (
      PIE_LABELS_POINTS_GAP_OFFSET_EXISTENCE_DEFAULT);
    setPieLabelsPointsGapOffsetModelRatio (
      PIE_LABELS_POINTS_GAP_OFFSET_MODEL_RATIO_DEFAULT);
    setPieLabelsPointsPieSectorsDepthRatio (PIE_LABELS_POINTS_PIE_SECTORS_DEPTH_RATIO_DEFAULT);
    setPieLabelsPointsBetweenPieAndLabelGapsDepthRatio (
      PIE_LABELS_POINTS_BETWEEN_PIE_AND_LABEL_GAPS_DEPTH_RATIO_DEFAULT);
    setPieLabelsLinesExistence (PIE_LABELS_LINES_EXISTENCE_DEFAULT);
    setPieLabelsLinesThicknessModel (PIE_LABELS_LINES_THICKNESS_MODEL_DEFAULT);
    setPieLabelsLinesColor (PIE_LABELS_LINES_COLOR_DEFAULT);
    setPieLabelsLinesDotsExistence (PIE_LABELS_LINES_DOTS_EXISTENCE_DEFAULT);
    setPieLabelsLinesDotsThicknessModel (PIE_LABELS_LINES_DOTS_THICKNESS_MODEL_DEFAULT);
    setPieLabelsLinesDotsColor (PIE_LABELS_LINES_DOTS_COLOR_DEFAULT);
    setPieSectorLightSource (PIE_SECTOR_LIGHT_SOURCE_DEFAULT);
    setPieLabelsFontPointModel (PIE_LABELS_FONT_POINT_MODEL_DEFAULT);
    setPieLabelsFontName (PIE_LABELS_FONT_NAME_DEFAULT);
    setPieLabelsFontColor (PIE_LABELS_FONT_COLOR_DEFAULT);
    setPieLabelsFontStyle (PIE_LABELS_FONT_STYLE_DEFAULT);
  }


  /**
   * Sets all the properties to be the values of another PieChart2DProperties object.
   * The copying is a deep copy.
   * @param pieChart2DProps The properties to copy.
   */
  public final void setPieChart2DProperties (PieChart2DProperties pieChart2DProps) {

    needsUpdate = true;
    setChartBetweenPieLabelsAndPieGapExistence (
      pieChart2DProps.getChartBetweenPieLabelsAndPieGapExistence());
    setChartBetweenPieLabelsAndPieGapThicknessModel (
      pieChart2DProps.getChartBetweenPieLabelsAndPieGapThicknessModel());
    setPiePreferredSize (pieChart2DProps.getPiePreferredSize());
    setPieSectorsOutlineExistence (pieChart2DProps.getPieSectorsOutlineExistence());
    setPieSectorsOutlineColor (pieChart2DProps.getPieSectorsOutlineColor());
    setPieLabelsExistence (pieChart2DProps.getPieLabelsExistence());
    setPieLabelsType (pieChart2DProps.getPieLabelsType());
    setPieLabelsBetweenLabelsGapExistence (pieChart2DProps.getPieLabelsBetweenLabelsGapExistence());
    setPieLabelsBetweenLabelsGapThicknessModel (
      pieChart2DProps.getPieLabelsBetweenLabelsGapThicknessModel());
    setPieLabelsPointsGapOffsetExistence (
      pieChart2DProps.getPieLabelsPointsGapOffsetExistence());
    setPieLabelsPointsGapOffsetModelRatio (
      pieChart2DProps.getPieLabelsPointsGapOffsetModelRatio());
    setPieLabelsPointsPieSectorsDepthRatio (
      pieChart2DProps.getPieLabelsPointsPieSectorsDepthRatio());
    setPieLabelsPointsBetweenPieAndLabelGapsDepthRatio (
      pieChart2DProps.getPieLabelsPointsBetweenPieAndLabelGapsDepthRatio());
    setPieLabelsLinesExistence (pieChart2DProps.getPieLabelsLinesExistence());
    setPieLabelsLinesThicknessModel (pieChart2DProps.getPieLabelsLinesThicknessModel());
    setPieLabelsLinesColor (pieChart2DProps.getPieLabelsLinesColor());
    setPieLabelsLinesDotsExistence (pieChart2DProps.getPieLabelsLinesDotsExistence());
    setPieLabelsLinesDotsThicknessModel (pieChart2DProps.getPieLabelsLinesDotsThicknessModel());
    setPieLabelsLinesDotsColor (pieChart2DProps.getPieLabelsLinesDotsColor());
    setPieSectorLightSource (pieSectorLightSource);
    setPieLabelsFontPointModel (pieChart2DProps.getPieLabelsFontPointModel());
    setPieLabelsFontName (pieChart2DProps.getPieLabelsFontName());
    setPieLabelsFontColor (pieChart2DProps.getPieLabelsFontColor());
    setPieLabelsFontStyle (pieChart2DProps.getPieLabelsFontStyle());
  }


  /**
   * Sets from which direction the light is coming for shading of the pie sectors.
   * Uses fields TOP, BOTTOM, LEFT, RIGHT, and NONE.
   * @param source The direction of the light.
   */
  final void setPieSectorLightSource (int source) {

    needsUpdate = true;
    pieSectorLightSource = source;
  }


  /**
   * Specifies whether the gap between each pie label and the pie exists.
   * @param existence If true, the gap exists.
   */
  public final void setChartBetweenPieLabelsAndPieGapExistence (boolean existence) {

    needsUpdate = true;
    chartBetweenPieLabelsAndPieGapExistence = existence;
  }


  /**
   * Specifies the thickness of the gap between each pie label and the pie for the chart's model
   * size.
   * @param int The model thickness of the gap.
   */
  public final void setChartBetweenPieLabelsAndPieGapThicknessModel (int thickness) {

    needsUpdate = true;
    chartBetweenPieLabelsAndPieGapThicknessModel = thickness;
  }


  /**
   * Specifies the preffered size of the pie in the pie chart.  A pie does not
   * have a calculable preferred size so one must be explicitly provided in this
   * method.  The size indicates the length of the diameter of the pie.
   * This length must be >= 10.
   * @param size The preferred size of the pie.
   */
  public final void setPiePreferredSize (int size) {

    needsUpdate = true;
    piePreferredSize = size;
  }


  /**
   * Specifies whether the sectors of the pie should have a thin outline.
   * @param existence If true, then the pie will have a thin outline.
   */
  public final void setPieSectorsOutlineExistence (boolean existence) {

    needsUpdate = true;
    pieSectorsOutlineExistence = existence;
  }


  /**
   * Specifies the color of the outline of the pie sectors.
   * @param color The color of the outline of the pie sectors.
   */
  public final void setPieSectorsOutlineColor (Color color) {

    needsUpdate = true;
    pieSectorsOutlineColor = color;
  }


  /**
   * Specifies whether the pie sectors of the pie chart will be labeled.
   * The labels can be raw numbers or percents.  They encircle the pie.
   * Methods that specify properties of these labels are setPieLabelsType (int)
   * and Chart2DProperties.setChartDataLabelsPrecision (int).
   * @param existence If true, the pie sectors will be labeled.
   */
  public final void setPieLabelsExistence (boolean existence) {

    needsUpdate = true;
    pieLabelsExistence = existence;
  }


  /**
   * Specifies the type of pie sector labels of the pie chart.  The possible
   * types are RAW, PERCENT, and RAW+PERCENT.  The format for RAW+PERCENT is
   * v (p%) where v is a data value and p is a percent value.
   * @param type The type of the pie labels.
   */
  public final void setPieLabelsType (int type) {

    needsUpdate = true;
    pieLabelsType = type;
  }


  /**
   * Specifies whether a gap between each pie sector label exists.
   * The gap is applied both vertically and horizontally.
   * @param existence If true, then the gap exists.
   */
  public final void setPieLabelsBetweenLabelsGapExistence (boolean existence) {

    needsUpdate = true;
    pieLabelsBetweenLabelsGapExistence = existence;
  }


  /**
   * Specifies the thickness of the gap between each pie label for the chart's
   * model size.
   * @param thickness The thickness of the gap.
   */
  public final void setPieLabelsBetweenLabelsGapThicknessModel (int thickness) {

    needsUpdate = true;
    pieLabelsBetweenLabelsGapThicknessModel = thickness;
  }


  /**
   * Specifies whether the gap between each pie label and a point from which a
   * line may be drawn and/or on which a dot may be placed to relate each label
   * with a particular pie sector exists.
   * @param existence If true, the gap exists.
   */
  public final void setPieLabelsPointsGapOffsetExistence (boolean existence) {

    needsUpdate = true;
    pieLabelsPointsGapOffsetExistence = existence;
  }


  /**
   * Specifies the ratio on the gap between the pie and the labels, for the gap between the
   * labels and the labels point.
   * @param ratio The ratio.
   */
  public final void setPieLabelsPointsGapOffsetModelRatio (float ratio) {

    needsUpdate = true;
    pieLabelsPointsGapOffsetModelRatio = ratio;
  }


  /**
   * Specifies the depth within each pie sector a point is placed from which a
   * line may be drawn and/or on which a dot may be placed to relate each label
   * with a particular pie sector for the chart's model size.
   * @param ratio The depth into the pie sector to the pie sector depth for
   * the point's placement.
   */
  public final void setPieLabelsPointsPieSectorsDepthRatio (float ratio) {

    needsUpdate = true;
    pieLabelsPointsPieSectorsDepthRatio = ratio;
  }


  /**
   * Specifies the depth within each gap between each pie sector and label,
   * from each pie, of the point at which a line coming from a label and a line
   * coming from a pie, meet.
   * @param ratio The depth into the gap where the lines labeling lines meet.
   */
  public final void setPieLabelsPointsBetweenPieAndLabelGapsDepthRatio (float ratio) {

    needsUpdate = true;
    pieLabelsPointsBetweenPieAndLabelGapsDepthRatio = ratio;
  }


  /**
   * Specifies whether lines relating each pie sector with its particular label exists.
   * @param existence If true, then the pie labels lines exist.
   */
  public final void setPieLabelsLinesExistence (boolean existence) {

    needsUpdate = true;
    pieLabelsLinesExistence = existence;
  }


  /**
   * Specifies the thickness of the lines relating each pie sector with its
   * particular label for the model size of the chart.
   * @param thickness The thickness of the line.
   */
  public final void setPieLabelsLinesThicknessModel (int thickness) {

    needsUpdate = true;
    pieLabelsLinesThicknessModel = thickness;
  }


  /**
   * Specifies the color of the lines relating each pie sector with its particular label.
   * @param color The color of the lines.
   */
  public final void setPieLabelsLinesColor (Color color) {

    needsUpdate = true;
    pieLabelsLinesColor = color;
  }


  /**
   * Specifies whether dots exist at the beginning and ending of the lines
   * relating each pie sector with its particular label.
   * @param existence If true, then two dots per line will exist.
   */
  public final void setPieLabelsLinesDotsExistence (boolean existence) {

    needsUpdate = true;
    pieLabelsLinesDotsExistence = existence;
  }


  /**
   * Specifies the thickness (ie diameter) of the dots that exist at the
   * beginning and ending of the lines releating each pie sector with its
   * particular label for the chart's model size.
   * @param thickness The thickness of the dots.
   */
  public final void setPieLabelsLinesDotsThicknessModel (int thickness) {

    needsUpdate = true;
    pieLabelsLinesDotsThicknessModel = thickness;
  }


  /**
   * Specifies the color of the dots that exist at the beginning and ending of
   * the lines relating each pie sector with its particular label.
   * @param color The color of the dots.
   */
  public final void setPieLabelsLinesDotsColor (Color color) {

    needsUpdate = true;
    pieLabelsLinesDotsColor = color;
  }


  /**
   * Sets the point of the font of the pie labels for the chart's model size.
   * @param point The model font point of the pie labels.
   */
  public final void setPieLabelsFontPointModel (int point) {

    needsUpdate = true;
    pieLabelsFontPointModel = point;
  }

  /**
   * Sets name of the font of the pie labels.
   * Accepts all values accepted by java.awt.Font.
   * @param name The name of the font for the pie labels.
   */
  public final void setPieLabelsFontName (String name) {

    needsUpdate = true;
    pieLabelsFontName = name;
  }


  /**
   *  Sets the color of the font of the pie labels.
   *  @param color The color of the font of the pie labels.
   */
  public final void setPieLabelsFontColor (Color color) {

    needsUpdate = true;
    pieLabelsFontColor = color;
  }


  /**
   * Sets the style of the font of the pie labels.
   * Accepts all values that java.awt.Font accepts.
   * @param style The style of the font of the pie labels.
   */
  public final void setPieLabelsFontStyle (int style) {

    needsUpdate = true;
    pieLabelsFontStyle = style;
  }


  /**
   * Returns whether the gap between each pie label and the pie exists.
   * @return If true, the gap exists.
   */
  public final boolean getChartBetweenPieLabelsAndPieGapExistence() {
    return chartBetweenPieLabelsAndPieGapExistence;
  }


  /**
   * Returns the thickness of the gap between each pie label and the pie
   * for the chart's model size.
   * @return The model thickness of the gap.
   */
  public final int getChartBetweenPieLabelsAndPieGapThicknessModel() {
    return chartBetweenPieLabelsAndPieGapThicknessModel;
  }


  /**
   * Returns the preffered size of the pie in the pie chart.  A pie does not
   * have a calculable preferred size so one must be explicitly provided in this
   * method.  The size indicates the length of the diameter of the pie.
   * This length must be >= 10.
   * @return The preferred size of the pie.
   */
  public final int getPiePreferredSize() {
    return piePreferredSize;
  }


  /**
   * Returns whether the sectors of the pie should have a thin outline.
   * @return If true, then the pie will have a thin outline.
   */
  public final boolean getPieSectorsOutlineExistence() {
    return pieSectorsOutlineExistence;
  }


  /**
   * Returns the color of the outline of the pie sectors.
   * @return The color of the outline of the pie sectors.
   */
  public final Color getPieSectorsOutlineColor() {
    return pieSectorsOutlineColor;
  }


  /**
   * Returns whether the pie sectors of the pie chart will be labeled.
   * The labels can be raw numbers or percents.  They encircle the pie.
   * Methods that specify properties of these labels are setPieLabelsType (int)
   * and Chart2DProperties.setChartDataLabelsPrecision (int).
   * @return If true, the pie sectors will be labeled.
   */
  public final boolean getPieLabelsExistence() {
    return pieLabelsExistence;
  }


  /**
   * Returns the type of pie sector labels of the pie chart.  The possible
   * types are RAW, PERCENT, and RAW+PERCENT.  The format for RAW+PERCENT is
   * v (p%) where v is a data value and p is a percent value.
   * @return The type of the pie labels.
   */
  public final int getPieLabelsType() {
    return pieLabelsType;
  }


  /**
   * Returns whether a gap between each pie sector label exists.
   * The gap is applied both vertically and horizontally.
   * @return If true, then the gap exists.
   */
  public final boolean getPieLabelsBetweenLabelsGapExistence() {
    return pieLabelsBetweenLabelsGapExistence;
  }


  /**
   * Returns the thickness of the gap between each pie label for the chart's model size.
   * @return The thickness of the gap.
   */
  public final int getPieLabelsBetweenLabelsGapThicknessModel() {
    return pieLabelsBetweenLabelsGapThicknessModel;
  }


  /**
   * Returns whether the gap between each pie label and a point from which a
   * line may be drawn and/or on which a dot may be placed to relate each label
   * with a particular pie sector exists.
   * @return If true, the gap exists.
   */
  public final boolean getPieLabelsPointsGapOffsetExistence() {
    return pieLabelsPointsGapOffsetExistence;
  }


  /**
   * Gets the ratio on the gap between the pie and the labels, for the gap between the
   * labels and the labels point.
   * @return The ratio.
   */
  public final float getPieLabelsPointsGapOffsetModelRatio() {
    return pieLabelsPointsGapOffsetModelRatio;
  }


  /**
   * Returns the depth within each pie sector a point is placed from which a
   * line may be drawn and/or on which a dot may be placed to relate each label
   * with a particular pie sector for the chart's model size.
   * @return The depth into the pie sector to the pie sector depth for the point's placement.
   */
  public final float getPieLabelsPointsPieSectorsDepthRatio() {
    return pieLabelsPointsPieSectorsDepthRatio;
  }


  /**
   * Returns the depth within each gap between each pie sector and label,
   * from each pie, of the point at which a line coming from a label and a line
   * coming from a pie, meet.
   * @return The depth into the gap where the lines labeling lines meet.
   */
  public final float getPieLabelsPointsBetweenPieAndLabelGapsDepthRatio() {
    return pieLabelsPointsBetweenPieAndLabelGapsDepthRatio;
  }


  /**
   * Returns whether lines relating each pie sector with its particular label exists.
   * @return If true, then the pie labels lines exist.
   */
  public final boolean getPieLabelsLinesExistence() {
    return pieLabelsLinesExistence;
  }


  /**
   * Returns the thickness of the lines relating each pie sector with its
   * particular label for the model size of the chart.
   * @return The thickness of the line.
   */
  public final int getPieLabelsLinesThicknessModel() {
    return pieLabelsLinesThicknessModel;
  }


  /**
   * Returns the color of the lines relating each pie sector with its particular label.
   * @return The color of the lines.
   */
  public final Color getPieLabelsLinesColor() {
    return pieLabelsLinesColor;
  }


  /**
   * Returns whether dots exist at the beginning and ending of the lines
   * relating each pie sector with its particular label.
   * @return If true, then two dots per line will exist.
   */
  public final boolean getPieLabelsLinesDotsExistence() {
    return pieLabelsLinesDotsExistence;
  }


  /**
   * Returns the thickness (ie diameter) of the dots that exist at the
   * beginning and ending of the lines releating each pie sector with its
   * particular label for the chart's model size.
   * @return The thickness of the dots.
   */
  public final int getPieLabelsLinesDotsThicknessModel() {
    return pieLabelsLinesDotsThicknessModel;
  }


  /**
   * Returns the color of the dots that exist at the beginning and ending of
   * the lines relating each pie sector with its particular label.
   * @return The color of the dots.
   */
  public final Color getPieLabelsLinesDotsColor() {
    return pieLabelsLinesDotsColor;
  }


  /**
   * Gets from which direction the light is coming for shading of the pie sectors.
   * Uses fields TOP, BOTTOM, LEFT, RIGHT, and NONE.
   * @return The direction of the light.
   */
  public final int getPieSectorLightSource() {
    return pieSectorLightSource;
  }


  /**
   * Gets the point of the font of the pie labels for the chart's model size.
   * @return int The model font point of the pie labels.
   */
  public final int getPieLabelsFontPointModel() {
    return pieLabelsFontPointModel;
  }

  /**
   * Gets name of the font of the pie labels.
   * Accepts all values accepted by java.awt.Font.
   * @return String The name of the font for the pie labels.
   */
  public final String getPieLabelsFontName() {
    return pieLabelsFontName;
  }


  /**
   *  Gets the color of the font of the pie labels.
   *  @return Color The color of the font of the pie labels.
   */
  public final Color getPieLabelsFontColor() {
    return pieLabelsFontColor;
  }


  /**
   * Gets the style of the font of the pie labels.
   * Accepts all values that java.awt.Font accepts.
   * @return int The style of the font of the pie labels.
   */
  public final int getPieLabelsFontStyle() {
    return pieLabelsFontStyle;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param pieChart2D  The object that might need to be updated.
   * @return If true the object needs to be udpated.
   */
  final boolean getPieChart2DNeedsUpdate (PieChart2D pieChart2D) {

    if (needsUpdate) return true;
    int index = -1;
    if ((index = pieChart2DVector.indexOf (pieChart2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }
    return false;
  }


  /**
   * Adds a PieChart2D to the set of objects using these properties.
   * @param pieChart2D The object to add.
   */
  final void addPieChart2D (PieChart2D pieChart2D) {

    if (!pieChart2DVector.contains (pieChart2D)) {
      pieChart2DVector.add (pieChart2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes a PieChart2D from the set of objects using these properties.
   * @param pieChart2D The object to remove.
   */
  final void removePieChart2D (PieChart2D pieChart2D) {

    int index = -1;
    if ((index = pieChart2DVector.indexOf (pieChart2D)) != -1) {
      pieChart2DVector.remove (index);
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

    if (debug) System.out.println ("Validating PieChart2DProperties");

    boolean valid = true;

    if (chartBetweenPieLabelsAndPieGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("ChartBetweenPieLabelsAndPieGapThicknessModel < 0");
    }
    if (piePreferredSize < 0) {
      valid = false;
      if (debug) System.out.println ("Problem with PiePreferredSize");
    }
    if (pieLabelsType != RAW && pieLabelsType != PERCENT &&
      pieLabelsType != (RAW + PERCENT)) {
      valid = false;
      if (debug) System.out.println ("Problem with PieLabelsType");
    }
    if (pieLabelsBetweenLabelsGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("PieLabelsBetweenLabelsGapThicknessModel < 0");
    }
    if (pieLabelsPointsGapOffsetModelRatio < 0f ||
      pieLabelsPointsGapOffsetModelRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with PieLabelsPointsGapOffsetModelRatio");
    }
    if (pieLabelsPointsPieSectorsDepthRatio < 0f ||
      pieLabelsPointsPieSectorsDepthRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with pieLabelsPointsPieSectorsDepthRatio");
    }
    if (pieLabelsPointsBetweenPieAndLabelGapsDepthRatio < 0f ||
      pieLabelsPointsBetweenPieAndLabelGapsDepthRatio > 1f) {
      valid = false;
      if (debug) System.out.println (
        "Problem with pieLabelsPointsBetweenPieAndLabelGapsDepthRatio");
    }
    if (pieLabelsLinesThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("PieLabelsLinesThicknessModel < 0");
    }
    if (pieLabelsLinesColor == null) {
      valid = false;
      if (debug) System.out.println ("PieLabelsLinesColor == null");
    }
    if (pieLabelsLinesDotsThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("PieLabelsLinesDotsThicknessModel < 0");
    }
    if (pieLabelsLinesDotsColor == null) {
      valid = false;
      if (debug) System.out.println ("PieLabelsLinesDotsColor == null");
    }
    if (pieSectorsOutlineColor == null) {
      valid = false;
      if (debug) System.out.println ("PieSectorsOutlineColor == null");
    }
    if (pieSectorLightSource != TOP && pieSectorLightSource != BOTTOM &&
      pieSectorLightSource != LEFT && pieSectorLightSource != RIGHT &&
      pieSectorLightSource != NONE) {
      valid = false;
      if (debug) System.out.println ("Problem with PieSectorLightSource");
    }
    if (pieLabelsFontPointModel < 0) {
      valid = false;
      if (debug) System.out.println ("PieLabelsFontPointModel < 0");
    }
    if (pieLabelsFontName == null || !isFontNameExists (pieLabelsFontName)) {
      valid = false;
      if (debug) System.out.println ("Problem with PieLabelsFontName");
    }
    if (pieLabelsFontColor == null) {
      valid = false;
      if (debug) System.out.println ("PieLabelsFontColor == null");
    }
    if (pieLabelsFontStyle != Font.PLAIN && pieLabelsFontStyle != Font.ITALIC &&
      pieLabelsFontStyle != Font.BOLD && pieLabelsFontStyle != (Font.ITALIC|Font.BOLD)) {
      valid = false;
      if (debug) System.out.println ("Problem with PieLabelsFontStyle");
    }

    if (debug) {
      if (valid) System.out.println ("PieChart2DProperties was valid");
      else System.out.println ("PieChart2DProperties was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this PieChart2D.
   * @param pieChart2D The object to update.
   */
  final void updatePieChart2D (PieChart2D pieChart2D) {

    if (getPieChart2DNeedsUpdate (pieChart2D)) {

      if (needsUpdate) {
        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }
        needsUpdate = false;
      }

      int index = -1;
      if ((index = pieChart2DVector.indexOf (pieChart2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }
}