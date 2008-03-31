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
 * A data structure for holding the properties common to all GraphChart2D objects.
 * A GraphChart2D objects has axes and one or more overlaid graphs.
 * Pass this to any number of GraphChart2D objects.
 */
 public final class GraphChart2DProperties extends Properties {


  /**
   * Indicates the ticks of an axis should be centered on each label
   * of the axis.  Used by setLabelsAxisTicksAlignment (int).
   */
  public static final int CENTERED = 0;

  /**
   * Indicates the ticks of an axis should be centered between the labels
   * of the axis.  Used by setLabelsAxisTicksAlignment (int).
   */
  public static final int BETWEEN = 1;

  /**
   * The default is false.
   */
  public static final boolean CHART_DATASET_CUSTOMIZE_GREATEST_VALUE_DEFAULT = false;

  /**
   * The default is 0.
   */
  public static final float CHART_DATASET_CUSTOM_GREATEST_VALUE_DEFAULT = 0;

  /**
   * The default is false.
   */
  public static final boolean CHART_DATASET_CUSTOMIZE_LEAST_VALUE_DEFAULT = false;

  /**
   * The default is 0.
   */
  public static final float CHART_DATASET_CUSTOM_LEAST_VALUE_DEFAULT = 0;

  /**
   * The default is .95f.
   */
  public static final float CHART_GRAPHABLE_TO_AVAILABLE_RATIO_DEFAULT = 1f;

  /**
   * The default is 7.
   */
  public static final int NUMBERS_AXIS_NUM_LABELS_DEFAULT = 7;


  /**
   * The default is true.
   */
  public static final boolean LABELS_AXIS_EXISTENCE_DEFAULT = true;


  /**
   * The default is BETWEEN.
   */
  public static final int LABELS_AXIS_TICKS_ALIGNMENT_DEFAULT = BETWEEN;

  /**
   * The default is String[0].
   */
  public static final String[] LABELS_AXIS_LABELS_TEXTS_DEFAULT = new String[0];

  /**
   * The default is true.
   */
  public static final boolean LABELS_AXIS_TITLE_EXISTENCE_DEFAULT = true;

  /**
   * The default is "".
   */
  public static final String LABELS_AXIS_TITLE_TEXT_DEFAULT = "";

  /**
   * The default is 11.
   */
  public static final int LABELS_AXIS_TITLE_FONT_POINT_MODEL_DEFAULT = 11;

  /**
   * The default is "SansSerif".
   */
  public static final String LABELS_AXIS_TITLE_FONT_NAME_DEFAULT = "SansSerif";

  /**
   * The default is Color.black.
   */
  public static final Color LABELS_AXIS_TITLE_FONT_COLOR_DEFAULT = Color.black;

  /**
   * The default is Font.PLAIN.
   */
  public static final int LABELS_AXIS_TITLE_FONT_STYLE_DEFAULT = Font.PLAIN;

  /**
   * The default is true.
   */
  public static final boolean LABELS_AXIS_TITLE_BETWEEN_REST_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public static final int LABELS_AXIS_TITLE_BETWEEN_REST_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is true.
   */
  public static final boolean LABELS_AXIS_TICKS_EXISTENCE_DEFAULT = true;

  /**
   * The default is Dimension (3, 3).
   */
  public static final Dimension LABELS_AXIS_TICKS_SIZE_MODEL_DEFAULT = new Dimension (3, 3);

  /**
   * The default is Color.black.
   */
  public static final Color LABELS_AXIS_TICKS_COLOR_DEFAULT = Color.black;

  /**
   * The default is false.
   */
  public static final boolean LABELS_AXIS_TICKS_OUTLINE_EXISTENCE_DEFAULT = false;

  /**
   * The default is Color.black.
   */
  public static final Color LABELS_AXIS_TICKS_OUTLINE_COLOR_DEFAULT = Color.black;

  /**
   * The default is 10.
   */
  public static final int LABELS_AXIS_LABELS_FONT_POINT_MODEL_DEFAULT = 10;

  /**
   * The default is "SansSerif".
   */
  public static final String LABELS_AXIS_LABELS_FONT_NAME_DEFAULT = "SansSerif";

  /**
   * The default is Color.black.
   */
  public static final Color LABELS_AXIS_LABELS_FONT_COLOR_DEFAULT = Color.black;

  /**
   * The default is Font.PLAIN.
   */
  public static final int LABELS_AXIS_LABELS_FONT_STYLE_DEFAULT = Font.PLAIN;

  /**
   * The default is true.
   */
  public static final boolean LABELS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public static final int LABELS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is true.
   */
  public static final boolean LABELS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public static final int LABELS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is true.
   */
  public static final boolean NUMBERS_AXIS_TITLE_EXISTENCE_DEFAULT = true;

  /**
   * The default is "".
   */
  public static final String NUMBERS_AXIS_TITLE_TEXT_DEFAULT = "";

  /**
   * The default is 11.
   */
  public static final int NUMBERS_AXIS_TITLE_FONT_POINT_MODEL_DEFAULT = 11;

  /**
   * The default is "SansSerif".
   */
  public static final String NUMBERS_AXIS_TITLE_FONT_NAME_DEFAULT = "SansSerif";

  /**
   * The default is Color.black.
   */
  public static final Color NUMBERS_AXIS_TITLE_FONT_COLOR_DEFAULT = Color.black;

  /**
   * The default is Font.PLAIN.
   */
  public static final int NUMBERS_AXIS_TITLE_FONT_STYLE_DEFAULT = Font.PLAIN;

  /**
   * The default is true.
   */
  public static final boolean NUMBERS_AXIS_TITLE_BETWEEN_REST_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public static final int NUMBERS_AXIS_TITLE_BETWEEN_REST_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is true.
   */
  public static final boolean NUMBERS_AXIS_TICKS_EXISTENCE_DEFAULT = true;

  /**
   * The default is Dimension (3, 3).
   */
  public static final Dimension NUMBERS_AXIS_TICKS_SIZE_MODEL_DEFAULT = new Dimension (3, 3);

  /**
   * The default is Color.black.
   */
  public static final Color NUMBERS_AXIS_TICKS_COLOR_DEFAULT = Color.black;

  /**
   * The default is false.
   */
  public static final boolean NUMBERS_AXIS_TICKS_OUTLINE_EXISTENCE_DEFAULT = false;

  /**
   * The default is Color.black.
   */
  public static final Color NUMBERS_AXIS_TICKS_OUTLINE_COLOR_DEFAULT = Color.black;

  /**
   * The default is 10.
   */
  public static final int NUMBERS_AXIS_LABELS_FONT_POINT_MODEL_DEFAULT = 10;

  /**
   * The default is "SansSerif".
   */
  public static final String NUMBERS_AXIS_LABELS_FONT_NAME_DEFAULT = "SansSerif";

  /**
   * The default is Color.black.
   */
  public static final Color NUMBERS_AXIS_LABELS_FONT_COLOR_DEFAULT = Color.black;

  /**
   * The default is Font.PLAIN.
   */
  public static final int NUMBERS_AXIS_LABELS_FONT_STYLE_DEFAULT = Font.PLAIN;

  /**
   * The default is true.
   */
  public static final boolean NUMBERS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public static final int NUMBERS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is true.
   */
  public static final boolean NUMBERS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public static final int NUMBERS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is false.
   */
  public static final boolean GRAPH_COMPONENTS_COLORING_BY_CAT_DEFAULT = false;

  /**
   * The default is null.
   */
  public static final MultiColorsProperties GRAPH_COMPONENTS_COLORS_BY_CAT_DEFAULT = null;


  private boolean chartDatasetCustomizeGreatestValue;
  private float chartDatasetCustomGreatestValue;
  private boolean chartDatasetCustomizeLeastValue;
  private float chartDatasetCustomLeastValue;
  private float chartGraphableToAvailableRatio;

  private boolean labelsAxisExistence;
  private int labelsAxisTicksAlignment;
  private String[] labelsAxisLabelsTexts;
  private boolean labelsAxisTitleExistence;
  private String labelsAxisTitleText;
  private int labelsAxisTitleFontPointModel;
  private String labelsAxisTitleFontName;
  private Color labelsAxisTitleFontColor;
  private int labelsAxisTitleFontStyle;
  private boolean labelsAxisTitleBetweenRestGapExistence;
  private int labelsAxisTitleBetweenRestGapThicknessModel;
  private boolean labelsAxisTicksExistence;
  private Dimension labelsAxisTicksSizeModel;
  private Color labelsAxisTicksColor;
  private boolean labelsAxisTicksOutlineExistence;
  private Color labelsAxisTicksOutlineColor;
  private int labelsAxisLabelsFontPointModel;
  private String labelsAxisLabelsFontName;
  private Color labelsAxisLabelsFontColor;
  private int labelsAxisLabelsFontStyle;
  private boolean labelsAxisBetweenLabelsOrTicksGapExistence;
  private int labelsAxisBetweenLabelsOrTicksGapThicknessModel;
  private boolean labelsAxisBetweenLabelsAndTicksGapExistence;
  private int labelsAxisBetweenLabelsAndTicksGapThicknessModel;

  private int numbersAxisNumLabels;
  private boolean numbersAxisTitleExistence;
  private String numbersAxisTitleText;
  private int numbersAxisTitleFontPointModel;
  private String numbersAxisTitleFontName;
  private Color numbersAxisTitleFontColor;
  private int numbersAxisTitleFontStyle;
  private boolean numbersAxisTitleBetweenRestGapExistence;
  private int numbersAxisTitleBetweenRestGapThicknessModel;
  private boolean numbersAxisTicksExistence;
  private Dimension numbersAxisTicksSizeModel;
  private Color numbersAxisTicksColor;
  private boolean numbersAxisTicksOutlineExistence;
  private Color numbersAxisTicksOutlineColor;
  private int numbersAxisLabelsFontPointModel;
  private String numbersAxisLabelsFontName;
  private Color numbersAxisLabelsFontColor;
  private int numbersAxisLabelsFontStyle;
  private boolean numbersAxisBetweenLabelsOrTicksGapExistence;
  private int numbersAxisBetweenLabelsOrTicksGapThicknessModel;
  private boolean numbersAxisBetweenLabelsAndTicksGapExistence;
  private int numbersAxisBetweenLabelsAndTicksGapThicknessModel;

  private boolean graphComponentsColoringByCat;
  private MultiColorsProperties graphComponentsColorsByCat;

  private boolean needsUpdate = true;
  private final Vector needsUpdateVector = new Vector (5, 5);
  private final Vector graphChart2DVector = new Vector (5, 5);


  /**
   * Creates a GraphChart2DProperties object with the documented default values.
   */
  public GraphChart2DProperties() {

    needsUpdate = true;
    setGraphChart2DPropertiesToDefaults();
  }


  /**
   * Creates a GraphChart2DProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param graphChart2DProps The properties to copy.
   */
  public GraphChart2DProperties (GraphChart2DProperties graphChart2DProps) {

    needsUpdate = true;
    setGraphChart2DProperties (graphChart2DProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setGraphChart2DPropertiesToDefaults() {

    needsUpdate = true;
    setChartDatasetCustomizeGreatestValue (CHART_DATASET_CUSTOMIZE_GREATEST_VALUE_DEFAULT);
    setChartDatasetCustomGreatestValue (CHART_DATASET_CUSTOM_GREATEST_VALUE_DEFAULT);
    setChartDatasetCustomizeLeastValue (CHART_DATASET_CUSTOMIZE_LEAST_VALUE_DEFAULT);
    setChartDatasetCustomLeastValue (CHART_DATASET_CUSTOM_LEAST_VALUE_DEFAULT);
    setChartGraphableToAvailableRatio (CHART_GRAPHABLE_TO_AVAILABLE_RATIO_DEFAULT);
    setNumbersAxisNumLabels (NUMBERS_AXIS_NUM_LABELS_DEFAULT);
    setLabelsAxisExistence (LABELS_AXIS_EXISTENCE_DEFAULT);
    setLabelsAxisTicksAlignment (LABELS_AXIS_TICKS_ALIGNMENT_DEFAULT);
    setLabelsAxisLabelsTexts (LABELS_AXIS_LABELS_TEXTS_DEFAULT);
    setLabelsAxisTitleExistence (LABELS_AXIS_TITLE_EXISTENCE_DEFAULT);
    setLabelsAxisTitleText (LABELS_AXIS_TITLE_TEXT_DEFAULT);
    setLabelsAxisTitleFontPointModel (LABELS_AXIS_TITLE_FONT_POINT_MODEL_DEFAULT);
    setLabelsAxisTitleFontName (LABELS_AXIS_TITLE_FONT_NAME_DEFAULT);
    setLabelsAxisTitleFontColor (LABELS_AXIS_TITLE_FONT_COLOR_DEFAULT);
    setLabelsAxisTitleFontStyle (LABELS_AXIS_TITLE_FONT_STYLE_DEFAULT);
    setLabelsAxisTitleBetweenRestGapExistence (
      LABELS_AXIS_TITLE_BETWEEN_REST_GAP_EXISTENCE_DEFAULT);
    setLabelsAxisTitleBetweenRestGapThicknessModel (
      LABELS_AXIS_TITLE_BETWEEN_REST_GAP_THICKNESS_MODEL_DEFAULT);
    setLabelsAxisTicksExistence (LABELS_AXIS_TICKS_EXISTENCE_DEFAULT);
    setLabelsAxisTicksSizeModel (LABELS_AXIS_TICKS_SIZE_MODEL_DEFAULT);
    setLabelsAxisTicksColor (LABELS_AXIS_TICKS_COLOR_DEFAULT);
    setLabelsAxisTicksOutlineExistence (LABELS_AXIS_TICKS_OUTLINE_EXISTENCE_DEFAULT);
    setLabelsAxisTicksOutlineColor (LABELS_AXIS_TICKS_COLOR_DEFAULT);
    setLabelsAxisLabelsFontPointModel (LABELS_AXIS_LABELS_FONT_POINT_MODEL_DEFAULT);
    setLabelsAxisLabelsFontName (LABELS_AXIS_LABELS_FONT_NAME_DEFAULT);
    setLabelsAxisLabelsFontColor (LABELS_AXIS_LABELS_FONT_COLOR_DEFAULT);
    setLabelsAxisLabelsFontStyle (LABELS_AXIS_LABELS_FONT_STYLE_DEFAULT);
    setLabelsAxisBetweenLabelsOrTicksGapExistence (
      LABELS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_EXISTENCE_DEFAULT);
    setLabelsAxisBetweenLabelsOrTicksGapThicknessModel (
      LABELS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_THICKNESS_MODEL_DEFAULT);
    setLabelsAxisBetweenLabelsAndTicksGapExistence (
      LABELS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_EXISTENCE_DEFAULT);
    setLabelsAxisBetweenLabelsAndTicksGapThicknessModel (
      LABELS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_THICKNESS_MODEL_DEFAULT);
    setNumbersAxisTitleExistence (NUMBERS_AXIS_TITLE_EXISTENCE_DEFAULT);
    setNumbersAxisTitleText (NUMBERS_AXIS_TITLE_TEXT_DEFAULT);
    setNumbersAxisTitleFontPointModel (NUMBERS_AXIS_TITLE_FONT_POINT_MODEL_DEFAULT);
    setNumbersAxisTitleFontName (NUMBERS_AXIS_TITLE_FONT_NAME_DEFAULT);
    setNumbersAxisTitleFontColor (NUMBERS_AXIS_TITLE_FONT_COLOR_DEFAULT);
    setNumbersAxisTitleFontStyle (NUMBERS_AXIS_TITLE_FONT_STYLE_DEFAULT);
    setNumbersAxisTitleBetweenRestGapExistence (
      NUMBERS_AXIS_TITLE_BETWEEN_REST_GAP_EXISTENCE_DEFAULT);
    setNumbersAxisTitleBetweenRestGapThicknessModel (
      NUMBERS_AXIS_TITLE_BETWEEN_REST_GAP_THICKNESS_MODEL_DEFAULT);
    setNumbersAxisTicksExistence (NUMBERS_AXIS_TICKS_EXISTENCE_DEFAULT);
    setNumbersAxisTicksSizeModel (NUMBERS_AXIS_TICKS_SIZE_MODEL_DEFAULT);
    setNumbersAxisTicksColor (NUMBERS_AXIS_TICKS_COLOR_DEFAULT);
    setNumbersAxisTicksOutlineExistence (NUMBERS_AXIS_TICKS_OUTLINE_EXISTENCE_DEFAULT);
    setNumbersAxisTicksOutlineColor (NUMBERS_AXIS_TICKS_COLOR_DEFAULT);
    setNumbersAxisLabelsFontPointModel (NUMBERS_AXIS_LABELS_FONT_POINT_MODEL_DEFAULT);
    setNumbersAxisLabelsFontName (NUMBERS_AXIS_LABELS_FONT_NAME_DEFAULT);
    setNumbersAxisLabelsFontColor (NUMBERS_AXIS_LABELS_FONT_COLOR_DEFAULT);
    setNumbersAxisLabelsFontStyle (NUMBERS_AXIS_LABELS_FONT_STYLE_DEFAULT);
    setNumbersAxisBetweenLabelsOrTicksGapExistence (
      NUMBERS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_EXISTENCE_DEFAULT);
    setNumbersAxisBetweenLabelsOrTicksGapThicknessModel (
      NUMBERS_AXIS_BETWEEN_LABELS_OR_TICKS_GAP_THICKNESS_MODEL_DEFAULT);
    setNumbersAxisBetweenLabelsAndTicksGapExistence (
      NUMBERS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_EXISTENCE_DEFAULT);
    setNumbersAxisBetweenLabelsAndTicksGapThicknessModel (
      NUMBERS_AXIS_BETWEEN_LABELS_AND_TICKS_GAP_THICKNESS_MODEL_DEFAULT);
    setGraphComponentsColoringByCat (GRAPH_COMPONENTS_COLORING_BY_CAT_DEFAULT);
  }


  /**
   * Sets all properties to be the values of another GraphChart2DProperties object.
   * The copying is a deep copy.
   * @param graphChart2DProps The GraphChart2DProperties to copy.
   */
  public final void setGraphChart2DProperties (GraphChart2DProperties graphChart2DProps) {

    needsUpdate = true;
    setChartDatasetCustomizeGreatestValue (
      graphChart2DProps.getChartDatasetCustomizeGreatestValue());
    setChartDatasetCustomGreatestValue (graphChart2DProps.getChartDatasetCustomGreatestValue());
    setChartDatasetCustomizeLeastValue (graphChart2DProps.getChartDatasetCustomizeLeastValue());
    setChartDatasetCustomLeastValue (graphChart2DProps.getChartDatasetCustomLeastValue());
    setChartGraphableToAvailableRatio (graphChart2DProps.getChartGraphableToAvailableRatio());
    setNumbersAxisNumLabels (graphChart2DProps.getNumbersAxisNumLabels());
    setLabelsAxisExistence (graphChart2DProps.getLabelsAxisExistence());
    setLabelsAxisTicksAlignment (graphChart2DProps.getLabelsAxisTicksAlignment());
    String[] labelsTexts = graphChart2DProps.getLabelsAxisLabelsTexts();
    String[] copiedLabelsTexts = new String[labelsTexts.length];
    for (int i = 0; i < labelsTexts.length; ++i) copiedLabelsTexts[i] = labelsTexts[i];
    setLabelsAxisLabelsTexts (copiedLabelsTexts);
    setLabelsAxisTitleExistence (graphChart2DProps.getLabelsAxisTitleExistence());
    setLabelsAxisTitleText (graphChart2DProps.getLabelsAxisTitleText());
    setLabelsAxisTitleFontPointModel (graphChart2DProps.getLabelsAxisTitleFontPointModel());
    setLabelsAxisTitleFontName (graphChart2DProps.getLabelsAxisTitleFontName());
    setLabelsAxisTitleFontColor (graphChart2DProps.getLabelsAxisTitleFontColor());
    setLabelsAxisTitleFontStyle (graphChart2DProps.getLabelsAxisTitleFontStyle());
    setLabelsAxisTitleBetweenRestGapExistence (
      graphChart2DProps.getLabelsAxisTitleBetweenRestGapExistence());
    setLabelsAxisTitleBetweenRestGapThicknessModel (
      graphChart2DProps.getLabelsAxisTitleBetweenRestGapThicknessModel());
    setLabelsAxisTicksExistence (graphChart2DProps.getLabelsAxisTicksExistence());
    setLabelsAxisTicksSizeModel (new Dimension (graphChart2DProps.getLabelsAxisTicksSizeModel()));
    setLabelsAxisTicksColor (graphChart2DProps.getLabelsAxisTicksColor());
    setLabelsAxisTicksOutlineExistence (graphChart2DProps.getLabelsAxisTicksOutlineExistence());
    setLabelsAxisTicksOutlineColor (graphChart2DProps.getLabelsAxisTicksOutlineColor());
    setLabelsAxisLabelsFontPointModel (graphChart2DProps.getLabelsAxisLabelsFontPointModel());
    setLabelsAxisLabelsFontName (graphChart2DProps.getLabelsAxisLabelsFontName());
    setLabelsAxisLabelsFontColor (graphChart2DProps.getLabelsAxisLabelsFontColor());
    setLabelsAxisLabelsFontStyle (graphChart2DProps.getLabelsAxisLabelsFontStyle());
    setLabelsAxisBetweenLabelsOrTicksGapExistence (
      graphChart2DProps.getLabelsAxisBetweenLabelsOrTicksGapExistence());
    setLabelsAxisBetweenLabelsOrTicksGapThicknessModel (
      graphChart2DProps.getLabelsAxisBetweenLabelsOrTicksGapThicknessModel());
    setLabelsAxisBetweenLabelsAndTicksGapExistence (
      graphChart2DProps.getLabelsAxisBetweenLabelsAndTicksGapExistence());
    setLabelsAxisBetweenLabelsAndTicksGapThicknessModel (
      graphChart2DProps.getLabelsAxisBetweenLabelsAndTicksGapThicknessModel());
    setNumbersAxisTitleExistence (graphChart2DProps.getNumbersAxisTitleExistence());
    setNumbersAxisTitleText (graphChart2DProps.getNumbersAxisTitleText());
    setNumbersAxisTitleFontPointModel (graphChart2DProps.getNumbersAxisTitleFontPointModel());
    setNumbersAxisTitleFontName (graphChart2DProps.getNumbersAxisTitleFontName());
    setNumbersAxisTitleFontColor (graphChart2DProps.getNumbersAxisTitleFontColor());
    setNumbersAxisTitleFontStyle (graphChart2DProps.getNumbersAxisTitleFontStyle());
    setNumbersAxisTitleBetweenRestGapExistence (
      graphChart2DProps.getNumbersAxisTitleBetweenRestGapExistence());
    setNumbersAxisTitleBetweenRestGapThicknessModel (
      graphChart2DProps.getNumbersAxisTitleBetweenRestGapThicknessModel());
    setNumbersAxisTicksExistence (graphChart2DProps.getNumbersAxisTicksExistence());
    setNumbersAxisTicksSizeModel (new Dimension (graphChart2DProps.getNumbersAxisTicksSizeModel()));
    setNumbersAxisTicksColor (graphChart2DProps.getNumbersAxisTicksColor());
    setNumbersAxisTicksOutlineExistence (graphChart2DProps.getNumbersAxisTicksOutlineExistence());
    setNumbersAxisTicksOutlineColor (graphChart2DProps.getNumbersAxisTicksOutlineColor());
    setNumbersAxisLabelsFontPointModel (graphChart2DProps.getNumbersAxisLabelsFontPointModel());
    setNumbersAxisLabelsFontName (graphChart2DProps.getNumbersAxisLabelsFontName());
    setNumbersAxisLabelsFontColor (graphChart2DProps.getNumbersAxisLabelsFontColor());
    setNumbersAxisLabelsFontStyle (graphChart2DProps.getNumbersAxisLabelsFontStyle());
    setNumbersAxisBetweenLabelsOrTicksGapExistence (
      graphChart2DProps.getNumbersAxisBetweenLabelsOrTicksGapExistence());
    setNumbersAxisBetweenLabelsOrTicksGapThicknessModel (
      graphChart2DProps.getNumbersAxisBetweenLabelsOrTicksGapThicknessModel());
    setNumbersAxisBetweenLabelsAndTicksGapExistence (
      graphChart2DProps.getNumbersAxisBetweenLabelsAndTicksGapExistence());
    setNumbersAxisBetweenLabelsAndTicksGapThicknessModel (
      graphChart2DProps.getNumbersAxisBetweenLabelsAndTicksGapThicknessModel());
    setGraphComponentsColoringByCat (graphChart2DProps.getGraphComponentsColoringByCat());
  }


  /**
   * Sets whether the true greatest value in the data sets will be
   * substituted by the custom value.
   * This effects the range of the labels of the numbers axis.
   * @param customize If true, the greatest value of the data will be
   * customized.
   */
  public final void setChartDatasetCustomizeGreatestValue (boolean customize) {

    needsUpdate = true;
    chartDatasetCustomizeGreatestValue = customize;
  }


  /**
   * Sets the custom greatest value of the data sets.  This value must be
   * greater than or equal to the true greatest value of the data sets for it
   * to be used.
   * This effects the scale of the labels of the numbers axis.
   * @param value The custom greatest value of the data sets.
   */
  public final void setChartDatasetCustomGreatestValue (float value) {

    needsUpdate = true;
    chartDatasetCustomGreatestValue = value;
  }


  /**
   * Sets whether the true least value in the data sets will be
   * substituted by the custom value.
   * This effects the range of the labels of the numbers axis.
   * @param customize If true, the least value of the data will be
   * customized.
   */
  public final void setChartDatasetCustomizeLeastValue (boolean customize) {

    needsUpdate = true;
    chartDatasetCustomizeLeastValue = customize;
  }


  /**
   * Sets the custom least value of the data sets.  This value must be
   * less than or equal to the true least value of the data sets for it
   * to be used.
   * This effects the scale of the labels of the numbers axis.
   * @param value The custom least value of the data sets.
   */
  public final void setChartDatasetCustomLeastValue (float value) {

    needsUpdate = true;
    chartDatasetCustomLeastValue = value;
  }


  /**
   * Sets how much of the chart's graph is used by the graph's components.
   * This value must be: 0f <= value <= 1f.
   * This effects the scale of the labels of the numbers axis.
   * @param ratio The ratio of usable to available of the graph.
   */
  public final void setChartGraphableToAvailableRatio (float ratio) {

    needsUpdate = true;
    chartGraphableToAvailableRatio = ratio;
  }


  /**
   * Sets the existence of the labels axis.  If existence is false, then the labels axis won't
   * be painted and the axis' texts won't need to be set.
   * @param existence If true, then exists.
   */
  public final void setLabelsAxisExistence (boolean existence) {

    needsUpdate = true;
    labelsAxisExistence = existence;
  }


  /**
   * Sets the placement of the ticks on the labels axis.  The ticks may
   * be either between the labels axis labels (ie for bar charts) or centered
   * on each labels axis label (ie for other charts).
   * The possible values are BETWEEN and CENTERED.
   * @param alignment The alignment of the labels axis ticks.
   */
  public final void setLabelsAxisTicksAlignment (int alignment) {

    needsUpdate = true;
    labelsAxisTicksAlignment = alignment;
  }


  /**
   * Sets the text of each labels axis label.  This determines how many
   * categories the data in the data sets are split into.
   * @param texts The texts of the labels axis labels.
   */
  public final void setLabelsAxisLabelsTexts (String[] texts) {

    needsUpdate = true;
    labelsAxisLabelsTexts = texts;
  }


  /**
   * Sets whether the title of the labels axis exists.
   * @param existence If true, the title exists.
   */
  public final void setLabelsAxisTitleExistence (boolean existence) {

    needsUpdate = true;
    labelsAxisTitleExistence = existence;
  }


  /**
   * Sets the text of the title of the labels axis.
   * @param text The title of the labels axis.
   */
  public final void setLabelsAxisTitleText (String text) {

    needsUpdate = true;
    labelsAxisTitleText = text;
  }


  /**
   * Sets the font point of the labels axis title for the chart's model
   * size.
   * @param point The model font point of the labels axis title.
   */
  public final void setLabelsAxisTitleFontPointModel (int point) {

    needsUpdate = true;
    labelsAxisTitleFontPointModel = point;
  }


  /**
   * Sets the name of the font of the labels axis title.
   * Accepts standard font names.
   * @param name The name of the font of the labels axis title.
   */
  public final void setLabelsAxisTitleFontName (String name) {

    needsUpdate = true;
    labelsAxisTitleFontName = name;
  }


  /**
   * Sets the color of the font of the labels axis title.
   * @param color The color of the font of the labels axis title.
   */
  public final void setLabelsAxisTitleFontColor (Color color) {

    needsUpdate = true;
    labelsAxisTitleFontColor = color;
  }


  /**
   * Sets the style of teh font of the labels axis title.
   * Accepts standard values for font styles.
   * @param style The style of the font of the labels axis title.
   */
  public final void setLabelsAxisTitleFontStyle (int style) {

    needsUpdate = true;
    labelsAxisTitleFontStyle = style;
  }


  /**
   * Sets whether the gap above the labels axis title exists.
   * @param existence If true, the gap exists.
   */
  public final void setLabelsAxisTitleBetweenRestGapExistence (boolean existence) {

    needsUpdate = true;
    labelsAxisTitleBetweenRestGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap above the labels axis title for the
   * chart's model size.
   * @param thickness The model thickness of the gap.
   */
  public final void setLabelsAxisTitleBetweenRestGapThicknessModel (int thickness) {

    needsUpdate = true;
    labelsAxisTitleBetweenRestGapThicknessModel = thickness;
  }


  /**
   * Sets whether there exists ticks along the labels axis.
   * @param existence If true, then they exist.
   */
  public final void setLabelsAxisTicksExistence (boolean existence) {

    needsUpdate = true;
    labelsAxisTicksExistence = existence;
  }


  /**
   * Sets the size of the labels axis ticks for the chart's model size.
   * @param size The model size of the labels axis ticks.
   */
  public final void setLabelsAxisTicksSizeModel (Dimension size) {

    needsUpdate = true;
    labelsAxisTicksSizeModel = size;
  }


  /**
   * Sets the color of the labels axis ticks.
   * @param color The color of the labels axis ticks.
   */
  public final void setLabelsAxisTicksColor (Color color) {

    needsUpdate = true;
    labelsAxisTicksColor = color;
  }


  /**
   * Sets whether a thin line outlines the labels axis ticks.
   * @param existence If true, then a thin outline exists.
   */
  public final void setLabelsAxisTicksOutlineExistence (boolean existence) {

    needsUpdate = true;
    labelsAxisTicksOutlineExistence = existence;
  }


  /**
   * Sets the color of the thin line that outlines the labels axis ticks.
   * @param color The color of the line that outlines the labels axis ticks.
   */
  public final void setLabelsAxisTicksOutlineColor (Color color) {

    needsUpdate = true;
    labelsAxisTicksOutlineColor = color;
  }


  /**
   * Sets the point of the font of the labels axis labels for the chart's
   * model size.
   * @param point The model font point of the labels axis labels.
   */
  public final void setLabelsAxisLabelsFontPointModel (int point) {

    needsUpdate = true;
    labelsAxisLabelsFontPointModel = point;
  }


  /**
   * Sets the name of the font of the labels of the labels axis.
   * Accepts standard font names.
   * @param name The name of the font.
   */
  public final void setLabelsAxisLabelsFontName (String name) {

    needsUpdate = true;
    labelsAxisLabelsFontName = name;
  }


  /**
   * Sets the color of the font of the labels of the labels axis.
   * @param color The color of the font.
   */
  public final void setLabelsAxisLabelsFontColor (Color color) {

    needsUpdate = true;
    labelsAxisLabelsFontColor = color;
  }


  /**
   * Sets the style of the font of the labels axis labels.
   * Accepts standard font styles.
   * @param style The style of the font.
   */
  public final void setLabelsAxisLabelsFontStyle (int style) {

    needsUpdate = true;
    labelsAxisLabelsFontStyle = style;
  }


  /**
   * Sets whether a gap between the labels or ticks exists, across.
   * The gap will be applied to whichever are naturally closer.
   * @param existence If true, the gap exists.
   */
  public final void setLabelsAxisBetweenLabelsOrTicksGapExistence (boolean existence) {

    needsUpdate = true;
    labelsAxisBetweenLabelsOrTicksGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between the labels or ticks,
   * across, for the chart's model size.  The gap will be applied to
   * whichever are naturally closer.
   * @param thicknss The model thickness of the gap.
   */
  public final void setLabelsAxisBetweenLabelsOrTicksGapThicknessModel (int thickness) {

    needsUpdate = true;
    labelsAxisBetweenLabelsOrTicksGapThicknessModel = thickness;
  }


  /**
   * Sets whether a gap between the labels and ticks exists, between.
   * @param existence If true, the gap exists.
   */
  public final void setLabelsAxisBetweenLabelsAndTicksGapExistence (boolean existence) {

    needsUpdate = true;
    labelsAxisBetweenLabelsAndTicksGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between the labels and ticks,
   * between, for the chart's model size.
   * @param thickness The model thickness of the gap.
   */
  public final void setLabelsAxisBetweenLabelsAndTicksGapThicknessModel (int thickness) {

    needsUpdate = true;
    labelsAxisBetweenLabelsAndTicksGapThicknessModel = thickness;
  }


  /**
   * Sets whether the title of the numbers axis exists.
   * @param existence If true, the title exists.
   */
  public final void setNumbersAxisTitleExistence (boolean existence) {

    needsUpdate = true;
    numbersAxisTitleExistence = existence;
  }


  /**
   * Sets the text of the title of the numbers axis.
   * @param text The title of the numbers axis.
   */
  public final void setNumbersAxisTitleText (String text) {

    needsUpdate = true;
    numbersAxisTitleText = text;
  }


  /**
   * Sets the font point of the numbers axis title for the chart's model
   * size.
   * @param point The model font point of the numbers axis title.
   */
  public final void setNumbersAxisTitleFontPointModel (int point) {

    needsUpdate = true;
    numbersAxisTitleFontPointModel = point;
  }


  /**
   * Sets the name of the font of the numbers axis title.
   * Accepts standard font names.
   * @param name The name of the font of the numbers axis title.
   */
  public final void setNumbersAxisTitleFontName (String name) {

    needsUpdate = true;
    numbersAxisTitleFontName = name;
  }


  /**
   * Sets the color of the font of the numbers axis title.
   * @param color The color of the font of the numbers axis title.
   */
  public final void setNumbersAxisTitleFontColor (Color color) {

    needsUpdate = true;
    numbersAxisTitleFontColor = color;
  }


  /**
   * Sets the style of teh font of the numbers axis title.
   * Accepts standard values for font styles.
   * @param style The style of the font of the numbers axis title.
   */
  public final void setNumbersAxisTitleFontStyle (int style) {

    needsUpdate = true;
    numbersAxisTitleFontStyle = style;
  }


  /**
   * Sets whether the gap right of the numbers axis title exists.
   * @param existence If true, the gap exists.
   */
  public final void setNumbersAxisTitleBetweenRestGapExistence (boolean existence) {

    needsUpdate = true;
    numbersAxisTitleBetweenRestGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap right of the numbers axis title for the
   * chart's model size.
   * @param thickness The model thickness of the gap.
   */
  public final void setNumbersAxisTitleBetweenRestGapThicknessModel (int thickness) {

    needsUpdate = true;
    numbersAxisTitleBetweenRestGapThicknessModel = thickness;
  }


  /**
   * Sets whether there exists ticks along the numbers axis.
   * @param existence If true, then they exist.
   */
  public final void setNumbersAxisTicksExistence (boolean existence) {

    needsUpdate = true;
    numbersAxisTicksExistence = existence;
  }


  /**
   * Sets the size of the numbers axis ticks for the chart's model size.
   * @param size The model size of the numbers axis ticks.
   */
  public final void setNumbersAxisTicksSizeModel (Dimension size) {

    needsUpdate = true;
    numbersAxisTicksSizeModel = size;
  }


  /**
   * Sets the color of the numbers axis ticks.
   * @param color The color of the numbers axis ticks.
   */
  public final void setNumbersAxisTicksColor (Color color) {

    needsUpdate = true;
    numbersAxisTicksColor = color;
  }


  /**
   * Sets whether a thin line outlines the numbers axis ticks.
   * @param existence If true, then a thin outline exists.
   */
  public final void setNumbersAxisTicksOutlineExistence (boolean existence) {

    needsUpdate = true;
    numbersAxisTicksOutlineExistence = existence;
  }


  /**
   * Sets the color of the thin line that outlines the numbers axis ticks.
   * @param color The color of the line that outliens the numbers axis ticks.
   */
  public final void setNumbersAxisTicksOutlineColor (Color color) {

    needsUpdate = true;
    numbersAxisTicksOutlineColor = color;
  }


  /**
   * Sets the number of labels in the numbers axis.
   * @param num The number of labels in the numbers axis.
   */
  public final void setNumbersAxisNumLabels (int num) {

    needsUpdate = true;
    numbersAxisNumLabels = num;
  }


  /**
   * Sets the point of the font of the numbers axis labels for the chart's
   * model size.
   * @param point The model font point of the numbers axis labels.
   */
  public final void setNumbersAxisLabelsFontPointModel (int point) {

    needsUpdate = true;
    numbersAxisLabelsFontPointModel = point;
  }


  /**
   * Sets the name of the font of the labels of the numbers axis.
   * Accepts standard font names.
   * @param name The name of the font.
   */
  public final void setNumbersAxisLabelsFontName (String name) {

    needsUpdate = true;
    numbersAxisLabelsFontName = name;
  }


  /**
   * Sets the color of the font of the labels of the numbers axis.
   * @param color The color of the font.
   */
  public final void setNumbersAxisLabelsFontColor (Color color) {

    needsUpdate = true;
    numbersAxisLabelsFontColor = color;
  }


  /**
   * Sets the style of the font of the numbers axis labels.
   * Accepts standard font styles.
   * @param style The style of the font.
   */
  public final void setNumbersAxisLabelsFontStyle (int style) {

    needsUpdate = true;
    numbersAxisLabelsFontStyle = style;
  }


  /**
   * Sets whether a gap between the labels or ticks exists, vertically.
   * The gap will be applied to whichever are naturally closer.
   * @param existence If true, the gap exists.
   */
  public final void setNumbersAxisBetweenLabelsOrTicksGapExistence (boolean existence) {

    needsUpdate = true;
    numbersAxisBetweenLabelsOrTicksGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between the labels or ticks,
   * vertically, for the chart's model size.  The gap will be applied to
   * whichever are naturally closer.
   * @param thicknss The model thickness of the gap.
   */
  public final void setNumbersAxisBetweenLabelsOrTicksGapThicknessModel (int thickness) {

    needsUpdate = true;
    numbersAxisBetweenLabelsOrTicksGapThicknessModel = thickness;
  }


  /**
   * Sets whether a gap between the labels and ticks exists,
   * horizontally.
   * @param existence If true, the gap exists.
   */
  public final void setNumbersAxisBetweenLabelsAndTicksGapExistence (boolean existence) {

    needsUpdate = true;
    numbersAxisBetweenLabelsAndTicksGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between the labels and ticks,
   * horizontally, for the chart's model size.
   * @param thickness The model thickness of the gap.
   */
  public final void setNumbersAxisBetweenLabelsAndTicksGapThicknessModel (int thickness) {

    needsUpdate = true;
    numbersAxisBetweenLabelsAndTicksGapThicknessModel = thickness;
  }


  /**
   * Sets whether the graph components have different colors across sets (or across cats).
   * @param b If true, then colors across sets.
   */
  public final void setGraphComponentsColoringByCat (boolean b) {

    needsUpdate = true;
    graphComponentsColoringByCat = b;
  }


  /**
   * Sets the color properties for the colors by cat coloring.
   * @param props The properties of the colors by cat coloring.
   */
  public final void setGraphComponentsColorsByCat (MultiColorsProperties props) {

    needsUpdate = true;
    for (int i = 0; i < graphChart2DVector.size(); ++i) {

      if (graphComponentsColorsByCat != null) {
        graphComponentsColorsByCat.removeObject2D ((GraphChart2D)graphChart2DVector.get (i));
      }
      if (props != null) props.addObject2D ((GraphChart2D)graphChart2DVector.get (i));
    }
    graphComponentsColorsByCat = props;
  }


  /**
   * Gets whether the true greatest value in the data sets will be
   * substituted by the custom value.
   * This effects the range of the labels of the numbers axis.
   * @return boolean If true, the greatest value of the data will be
   * customized.
   */
  public final boolean getChartDatasetCustomizeGreatestValue() {
    return chartDatasetCustomizeGreatestValue;
  }


  /**
   * Gets the custom greatest value of the data sets.  This value must be
   * greater than or equal to the true greatest value of the data sets for it
   * to be used.
   * This effects the scale of the labels of the numbers axis.
   * @return float The custom greatest value of the data sets.
   */
  public final float getChartDatasetCustomGreatestValue() {
    return chartDatasetCustomGreatestValue;
  }


  /**
   * Gets whether the true least value in the data sets will be
   * substituted by the custom value.
   * This effects the range of the labels of the numbers axis.
   * @return boolean If true, the least value of the data will be
   * customized.
   */
  public final boolean getChartDatasetCustomizeLeastValue() {
    return chartDatasetCustomizeLeastValue;
  }


  /**
   * Gets the custom least value of the data sets.  This value must be
   * less than or equal to the true least value of the data sets for it
   * to be used.
   * This effects the scale of the labels of the numbers axis.
   * @return float The custom least value of the data sets.
   */
  public final float getChartDatasetCustomLeastValue() {
    return chartDatasetCustomLeastValue;
  }


  /**
   * Gets how much of the chart's graph is used by the graph's components.
   * This value must be: 0f <= value <= 1f.
   * This effects the scale of the labels of the numbers axis.
   * @return float The ratio of usable to available of the graph.
   */
  public float getChartGraphableToAvailableRatio() {
    return chartGraphableToAvailableRatio;
  }


  /**
   * Gets the existence of the labels axis.  If existence is false, then the labels axis won't
   * be painted and the axis' texts won't need to be set.
   * @return If true, then exists.
   */
  public final boolean getLabelsAxisExistence() {
    return labelsAxisExistence;
  }


  /**
   * Gets the placement of the ticks on the labels axis.  The ticks may
   * be either between the labels axis labels (ie for bar charts) or centered
   * on each labels axis label (ie for other charts).
   * The possible values are BETWEEN and CENTERED.
   * @return int The alignment of the labels axis ticks.
   */
  public final int getLabelsAxisTicksAlignment() {
    return labelsAxisTicksAlignment;
  }


  /**
   * Gets the text of each labels axis label.  This determines how many
   * categories the data in the data sets are split into.
   * @return String[] The texts of the labels axis labels.
   */
  public final String[] getLabelsAxisLabelsTexts() {
    return labelsAxisLabelsTexts;
  }


  /**
   * Gets whether the title of the labels axis exists.
   * @return boolean If true, the title exists.
   */
  public final boolean getLabelsAxisTitleExistence() {
    return labelsAxisTitleExistence;
  }


  /**
   * Gets the text of the title of the labels axis.
   * @return String The title of the labels axis.
   */
  public final String getLabelsAxisTitleText() {
    return labelsAxisTitleText;
  }


  /**
   * Gets the font point of the labels axis title for the chart's model size.
   * @return int The model font point of the labels axis title.
   */
  public final int getLabelsAxisTitleFontPointModel() {
    return labelsAxisTitleFontPointModel;
  }


  /**
   * Gets the name of the font of the labels axis title.
   * Accepts standard font names.
   * @return String The name of the font of the labels axis title.
   */
  public final String getLabelsAxisTitleFontName() {
    return labelsAxisTitleFontName;
  }


  /**
   * Gets the color of the font of the labels axis title.
   * @return Color The color of the font of the labels axis title.
   */
  public final Color getLabelsAxisTitleFontColor() {
    return labelsAxisTitleFontColor;
  }


  /**
   * Gets the style of teh font of the labels axis title.
   * Accepts standard values for font styles.
   * @return int The style of the font of the labels axis title.
   */
  public final int getLabelsAxisTitleFontStyle() {
    return labelsAxisTitleFontStyle;
  }


  /**
   * Gets whether the gap above the labels axis title exists.
   * @return boolean If true, the gap exists.
   */
  public final boolean getLabelsAxisTitleBetweenRestGapExistence() {
    return labelsAxisTitleBetweenRestGapExistence;
  }


  /**
   * Gets the thickness of the gap above the labels axis title for the
   * chart's model size.
   * @return int The model thickness of the gap.
   */
  public final int getLabelsAxisTitleBetweenRestGapThicknessModel() {
    return labelsAxisTitleBetweenRestGapThicknessModel;
  }


  /**
   * Gets whether there exists ticks along the numbers axis.
   * @return If true, then they exist.
   */
  public final boolean getNumbersAxisTicksExistence() {
    return numbersAxisTicksExistence;
  }


  /**
   * Gets whether there exists ticks along the labels axis.
   * @return If true, then they exist.
   */
  public final boolean getLabelsAxisTicksExistence() {
    return labelsAxisTicksExistence;
  }


  /**
   * Gets the size of the labels axis ticks for the chart's model size.
   * @return Dimension The model size of the labels axis ticks.
   */
  public final Dimension getLabelsAxisTicksSizeModel() {
    return labelsAxisTicksSizeModel;
  }


  /**
   * Gets the color of the labels axis ticks.
   * @return Color The color of the labels axis ticks.
   */
  public final Color getLabelsAxisTicksColor() {
    return labelsAxisTicksColor;
  }


  /**
   * Gets whether a thin line outlines the labels axis ticks.
   * @return boolean If true, then a thin outline exists.
   */
  public final boolean getLabelsAxisTicksOutlineExistence() {
    return labelsAxisTicksOutlineExistence;
  }


  /**
   * Gets the color of the thin line that outlines the labels axis ticks.
   * @return Color The color of the line that outliens the labels axis ticks.
   */
  public final Color getLabelsAxisTicksOutlineColor() {
    return labelsAxisTicksOutlineColor;
  }


  /**
   * Gets the point of the font of the labels axis labels for the chart's
   * model size.
   * @return int The model font point of the labels axis labels.
   */
  public final int getLabelsAxisLabelsFontPointModel() {
    return labelsAxisLabelsFontPointModel;
  }


  /**
   * Gets the name of the font of the labels of the labels axis.
   * Accepts standard font names.
   * @return String The name of the font.
   */
  public final String getLabelsAxisLabelsFontName() {
    return labelsAxisLabelsFontName;
  }


  /**
   * Gets the color of the font of the labels of the labels axis.
   * @return Color The color of the font.
   */
  public final Color getLabelsAxisLabelsFontColor() {
    return labelsAxisLabelsFontColor;
  }


  /**
   * Gets the style of the font of the labels axis labels.
   * Accepts standard font styles.
   * @return int The style of the font.
   */
  public final int getLabelsAxisLabelsFontStyle() {
    return labelsAxisLabelsFontStyle;
  }


  /**
   * Gets whether a gap between the labels or ticks exists, horizontally.
   * The gap will be applied to whichever are naturally closer.
   * @return boolean If true, the gap exists.
   */
  public final boolean getLabelsAxisBetweenLabelsOrTicksGapExistence() {
    return labelsAxisBetweenLabelsOrTicksGapExistence;
  }


  /**
   * Gets the thickness of the gap between the labels or ticks,
   * horizontally, for the chart's model size.  The gap will be applied to
   * whichever are naturally closer.
   * @return int The model thickness of the gap.
   */
  public final int getLabelsAxisBetweenLabelsOrTicksGapThicknessModel() {
    return labelsAxisBetweenLabelsOrTicksGapThicknessModel;
  }


  /**
   * Gets whether a gap between the labels and ticks exists, vertically.
   * @return boolean If true, the gap exists.
   */
  public final boolean getLabelsAxisBetweenLabelsAndTicksGapExistence() {
    return labelsAxisBetweenLabelsAndTicksGapExistence;
  }


  /**
   * Gets the thickness of the gap between the labels and ticks,
   * vertically, for the chart's model size.
   * @return int The model thickness of the gap.
   */
  public final int getLabelsAxisBetweenLabelsAndTicksGapThicknessModel() {
    return labelsAxisBetweenLabelsAndTicksGapThicknessModel;
  }


  /**
   * Gets whether the title of the numbers axis exists.
   * @return boolean If true, the title exists.
   */
  public final boolean getNumbersAxisTitleExistence() {
    return numbersAxisTitleExistence;
  }


  /**
   * Gets the text of the title of the numbers axis.
   * @return String The title of the numbers axis.
   */
  public final String getNumbersAxisTitleText() {
    return numbersAxisTitleText;
  }


  /**
   * Gets the font point of the numbers axis title for the chart's model
   * size.
   * @return int The model font point of the numbers axis title.
   */
  public final int getNumbersAxisTitleFontPointModel() {
    return numbersAxisTitleFontPointModel;
  }


  /**
   * Gets the name of the font of the numbers axis title.
   * Accepts standard font names.
   * @return String The name of the font of the numbers axis title.
   */
  public final String getNumbersAxisTitleFontName() {
    return numbersAxisTitleFontName;
  }


  /**
   * Gets the color of the font of the numbers axis title.
   * @return Color The color of the font of the numbers axis title.
   */
  public final Color getNumbersAxisTitleFontColor() {
    return numbersAxisTitleFontColor;
  }


  /**
   * Gets the style of teh font of the numbers axis title.
   * Accepts standard values for font styles.
   * @return int The style of the font of the numbers axis title.
   */
  public final int getNumbersAxisTitleFontStyle() {
    return numbersAxisTitleFontStyle;
  }


  /**
   * Gets whether the gap right of the numbers axis title exists.
   * @return boolean If true, the gap exists.
   */
  public final boolean getNumbersAxisTitleBetweenRestGapExistence() {
    return numbersAxisTitleBetweenRestGapExistence;
  }


  /**
   * Gets the thickness of the gap right of the numbers axis title for the
   * chart's model size.
   * @return int The model thickness of the gap.
   */
  public final int getNumbersAxisTitleBetweenRestGapThicknessModel() {
    return numbersAxisTitleBetweenRestGapThicknessModel;
  }


  /**
   * Gets the size of the numbers axis ticks for the chart's model size.
   * @return Dimension The model size of the numbers axis ticks.
   */
  public final Dimension getNumbersAxisTicksSizeModel() {
    return numbersAxisTicksSizeModel;
  }


  /**
   * Gets the color of the numbers axis ticks.
   * @return Color The color of the numbers axis ticks.
   */
  public final Color getNumbersAxisTicksColor() {
    return numbersAxisTicksColor;
  }


  /**
   * Gets whether a thin line outlines the numbers axis ticks.
   * @return boolean If true, then a thin outline exists.
   */
  public final boolean getNumbersAxisTicksOutlineExistence() {
    return numbersAxisTicksOutlineExistence;
  }


  /**
   * Gets the color of the thin line that outlines the numbers axis ticks.
   * @return Color The color of the thin line that outliens the numbers axis
   * ticks.
   */
  public final Color getNumbersAxisTicksOutlineColor() {
    return numbersAxisTicksOutlineColor;
  }


  /**
   * Gets the number of labels in the numbers axis.
   * @return int The number of labels in the numbers axis.
   */
  public final int getNumbersAxisNumLabels() {
    return numbersAxisNumLabels;
  }


  /**
   * Gets the point of the font of the numbers axis labels for the chart's
   * model size.
   * @return int The model font point of the numbers axis labels.
   */
  public final int getNumbersAxisLabelsFontPointModel() {
    return numbersAxisLabelsFontPointModel;
  }


  /**
   * Gets the name of the font of the labels of the numbers axis.
   * Accepts standard font names.
   * @return String The name of the font.
   */
  public final String getNumbersAxisLabelsFontName() {
    return numbersAxisLabelsFontName;
  }


  /**
   * Gets the color of the font of the labels of the numbers axis.
   * @return Color The color of the font.
   */
  public final Color getNumbersAxisLabelsFontColor() {
    return numbersAxisLabelsFontColor;
  }


  /**
   * Gets the style of the font of the numbers axis labels.
   * Accepts standard font styles.
   * @return int The style of the font.
   */
  public final int getNumbersAxisLabelsFontStyle() {
    return numbersAxisLabelsFontStyle;
  }


  /**
   * Gets whether a gap between the labels or ticks exists, vertically.
   * The gap will be applied to whichever are naturally closer.
   * @return boolean If true, the gap exists.
   */
  public final boolean getNumbersAxisBetweenLabelsOrTicksGapExistence() {
    return numbersAxisBetweenLabelsOrTicksGapExistence;
  }


  /**
   * Gets the thickness of the gap between the labels or ticks,
   * vertically, for the chart's model size.  The gap will be applied to
   * whichever are naturally closer.
   * @return int The model thickness of the gap.
   */
  public final int getNumbersAxisBetweenLabelsOrTicksGapThicknessModel() {
    return numbersAxisBetweenLabelsOrTicksGapThicknessModel;
  }


  /**
   * Gets whether a gap between the labels and ticks exists,
   * horizontally.
   * @return boolean If true, the gap exists.
   */
  public final boolean getNumbersAxisBetweenLabelsAndTicksGapExistence() {
    return numbersAxisBetweenLabelsAndTicksGapExistence;
  }


  /**
   * Gets the thickness of the gap between the labels and ticks,
   * horizontally, for the chart's model size.
   * @return int The model thickness of the gap.
   */
  public final int getNumbersAxisBetweenLabelsAndTicksGapThicknessModel() {
    return numbersAxisBetweenLabelsAndTicksGapThicknessModel;
  }


  /**
   * Gets whether the graph components have different colors across sets (or across cats).
   * @return If true, then colors across sets.
   */
  public final boolean getGraphComponentsColoringByCat() {
    return graphComponentsColoringByCat;
  }


  /**
   * Sets the color properties for the colors by cat coloring.
   * @return The properties of the colors by cat coloring.
   */
  public final MultiColorsProperties getGraphComponentsColorsByCat() {
    return graphComponentsColorsByCat;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param graphChart2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getGraphChart2DNeedsUpdate (GraphChart2D graphChart2D) {

    if (needsUpdate) return true;

    if (graphComponentsColoringByCat &&
      graphComponentsColorsByCat.getObject2DNeedsUpdate (graphChart2D)) return true;
    int index = -1;

    if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }
    return false;
  }


  /**
   * Adds a GraphChart2D to the set of objects using these properties.
   * @param graphChart2D The object to add.
   */
  final void addGraphChart2D (GraphChart2D graphChart2D) {

    if (!graphChart2DVector.contains (graphChart2D)) {
      if (graphComponentsColorsByCat != null) graphComponentsColorsByCat.addObject2D (graphChart2D);
      graphChart2DVector.add (graphChart2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes a GraphChart2D from the set of objects using these properties.
   * @param graphChart2D The object to remove.
   */
  final void removeGraphChart2D (GraphChart2D graphChart2D) {

    int index = -1;
    if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
      if (graphComponentsColorsByCat != null) {
        graphComponentsColorsByCat.removeObject2D (graphChart2D);
      }
      graphChart2DVector.remove (index);
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

    if (debug) System.out.println ("Validating GraphChart2DProperties");

    boolean valid = true;

    if (chartGraphableToAvailableRatio < 0f || chartGraphableToAvailableRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with ChartGraphableToAvailableRatio");
    }
    if (numbersAxisNumLabels < 2) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisNumLabels < 2");
    }
    if (labelsAxisTicksAlignment != BETWEEN && labelsAxisTicksAlignment != CENTERED) {
      valid = false;
      if (debug) System.out.println ("Problem with LabelsAxisTicksAlignment");
    }
    if (labelsAxisLabelsTexts == null) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisLabelsTexts == null");
    }
    if (labelsAxisTitleText == null) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisTitleText == null");
    }
    if (labelsAxisTitleFontPointModel < 0) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisTitleFontPointModel < 0");
    }
    if (labelsAxisTitleFontName == null || !isFontNameExists (labelsAxisTitleFontName)) {
      valid = false;
      if (debug) System.out.println ("Problem with LabelsAxisTitleFontName");
    }
    if (labelsAxisTitleFontColor == null) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisTitleFontColor == null");
    }
    if (labelsAxisTitleFontStyle != Font.PLAIN &&
      labelsAxisTitleFontStyle != Font.ITALIC &&
      labelsAxisTitleFontStyle != Font.BOLD &&
      labelsAxisTitleFontStyle != (Font.ITALIC|Font.BOLD)) {
      valid = false;
      if (debug) System.out.println ("Problem with LabelsAxisTitleFontStyle");
    }
    if (labelsAxisTitleBetweenRestGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisTitleBetweenRestGapThicknessModel < 0");
    }
    if (labelsAxisTicksSizeModel == null ||
      labelsAxisTicksSizeModel.width < 0 || labelsAxisTicksSizeModel.height < 0) {
      valid = false;
      if (debug) System.out.println ("Problem with LabelsAxisTicksSizeModel");
    }
    if (labelsAxisTicksColor == null) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisTicksColor == null");
    }
    if (labelsAxisTicksOutlineColor == null) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisTicksOutlineColor == null");
    }
    if (labelsAxisLabelsFontPointModel < 0) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisLabelsFontPointModel < 0");
    }
    if (labelsAxisLabelsFontName == null || !isFontNameExists (labelsAxisLabelsFontName)) {
      valid = false;
      if (debug) System.out.println ("Problem with LabelsAxisLabelsFontName");
    }
    if (labelsAxisLabelsFontColor == null) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisLabelsFontColor == null");
    }
    if (labelsAxisLabelsFontStyle != Font.PLAIN &&
      labelsAxisLabelsFontStyle != Font.ITALIC &&
      labelsAxisLabelsFontStyle != Font.BOLD &&
      labelsAxisLabelsFontStyle != (Font.ITALIC|Font.BOLD)) {
      valid = false;
      if (debug) System.out.println ("Problem with LabelsAxisLabelsFontStyle");
    }
    if (labelsAxisBetweenLabelsOrTicksGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisBetweenLabelsOrTicksGapThicknessModel < 0");
    }
    if (labelsAxisBetweenLabelsAndTicksGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("LabelsAxisBetweenLabelsAndTicksGapThicknessModel < 0");
    }
    if (numbersAxisTitleText == null) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisTitleText == null");
    }
    if (numbersAxisTitleFontPointModel < 0) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisTitleFontPointModel < 0");
    }
    if (numbersAxisTitleFontName == null || !isFontNameExists (numbersAxisTitleFontName)) {
      valid = false;
      if (debug) System.out.println ("Problem with NumbersAxisTitleFontName");
    }
    if (numbersAxisTitleFontColor == null) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisTitleFontColor == null");
    }
    if (numbersAxisTitleFontStyle != Font.PLAIN &&
      numbersAxisTitleFontStyle != Font.ITALIC &&
      numbersAxisTitleFontStyle != Font.BOLD &&
      numbersAxisTitleFontStyle != (Font.ITALIC|Font.BOLD)) {
      valid = false;
      if (debug) System.out.println ("Problem with NumbersAxisTitleFontStyle");
    }
    if (numbersAxisTitleBetweenRestGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisTitleBetweenRestGapThicknessModel < 0");
    }
    if (numbersAxisTicksSizeModel == null ||
      numbersAxisTicksSizeModel.width < 0 || numbersAxisTicksSizeModel.height < 0) {
      valid = false;
      if (debug) System.out.println ("Problem with NumbersAxisTicksSizeModel");
    }
    if (numbersAxisTicksColor == null) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisTicksColor == null");
    }
    if (numbersAxisTicksOutlineColor == null) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisTicksOutlineColor == null");
    }
    if (numbersAxisLabelsFontPointModel < 0) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisLabelsFontPointModel < 0");
    }
    if (numbersAxisLabelsFontName == null || !isFontNameExists (numbersAxisLabelsFontName)) {
      valid = false;
      if (debug) System.out.println ("Problem with NumbersAxisLabelsFontName");
    }
    if (numbersAxisLabelsFontColor == null) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisLabelsFontColor == null");
    }
    if (numbersAxisLabelsFontStyle != Font.PLAIN &&
      numbersAxisLabelsFontStyle != Font.ITALIC &&
      numbersAxisLabelsFontStyle != Font.BOLD &&
      numbersAxisLabelsFontStyle != (Font.ITALIC|Font.BOLD)) {
      valid = false;
      if (debug) System.out.println ("Problem with NumbersAxisLabelsFontStyle");
    }
    if (numbersAxisBetweenLabelsOrTicksGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisBetweenLabelsOrTicksGapThicknessModel < 0");
    }
    if (numbersAxisBetweenLabelsAndTicksGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisBetweenLabelsAndTicksGapThicknessModel < 0");
    }
    if (numbersAxisBetweenLabelsAndTicksGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("NumbersAxisBetweenLabelsAndTicksGapThicknessModel < 0");
    }
    if (graphComponentsColoringByCat) {

      if (graphComponentsColorsByCat == null) {
        valid = false;
        if (debug) System.out.println ("GraphComponentsColorsByCat == null");
      }
      else if (!graphComponentsColorsByCat.validate (debug)) valid = false;
    }

    if (debug) {
      if (valid) System.out.println ("GraphChart2DProperties was valid");
      else System.out.println ("GraphChart2DProperties was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this GraphChart2D.
   * @param graphChart2D The GraphChart2D to update.
   */
  final void updateGraphChart2D (GraphChart2D graphChart2D) {

    if (getGraphChart2DNeedsUpdate (graphChart2D)) {

      if (needsUpdate) {
        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }
        needsUpdate = false;
      }

      if (graphComponentsColorsByCat != null) {
        graphComponentsColorsByCat.updateObject2D (graphChart2D);
      }

      int index = -1;
      if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }
}