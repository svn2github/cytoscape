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
import java.util.Vector;
import java.awt.AlphaComposite;


/**
 * A data structure for holding the properties common to all graph area objects.
 * A graph area is the rectangular region surrounded on two sides by axes and containing either
 * lines, dots, or bars as graph components.
 * Pass this to any number of GraphChart2D objects.
 */
 public final class GraphProperties {


  /**
   * Indicates the lines will be continuous.
   * Used by setGraphNumbersLinesStyle(int) and setGraphLabelsLinesStyle(int).
   */
  public static float[] CONTINUOUS = {10.0f, 0.0f};

  /**
   * Indicates the lines will be dashed.
   * Used by setGraphNumbersLinesStyle(int) and setGraphLabelsLinesStyle(int).
   */
  public static float[] DASHED = {7.0f, 3.0f};

  /**
   * Indicates the lines will be dotted.
   * Used by setGraphNumbersLinesStyle(int) and setGraphLabelsLinesStyle(int).
   */
  public static float[] DOTTED = {3.0f, 3.0f};

  /**
   * Indicates the left of something.  Used by setGraphComponentsLightSource(int).
   */
  public static int LEFT = 0;


  /**
   * Indicates the right of something.  Used by setGraphComponentsLightSource(int).
   */
  public static int RIGHT = 1;


  /**
   * Indicates the top of something.  Used by setGraphComponentsLightSource(int).
   */
  public static int TOP = 2;


  /**
   * Indicates the bottom of something.  Used by setGraphComponentsLightSource(int).
   */
  public static int BOTTOM = 3;


  /**
   * Indicates none.  Used by setGraphComponentsLightSource(int).
   */
  public static int NONE = 6;


  /**
   * Indicates only the component.  Used by setGraphComponentsLightType(int).
   */
  public static int COMPONENT = 0;


  /**
   * Indicates only the graph.  Used by setGraphComponentsLightType(int).
   */
  public static int GRAPH = 1;


  /**
   * An opaque (no blending) alpha composite.
   */
  public static AlphaComposite ALPHA_COMPOSITE_NONE =
    AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 1f);

  /**
   * A mildly transparent (some blending) alpha composite.
   */
  public static AlphaComposite ALPHA_COMPOSITE_MILD =
    AlphaComposite.getInstance (AlphaComposite.SRC_OVER, .9f);

  /**
   * A medium transparent (some blending) alpha composite.
   */
  public static AlphaComposite ALPHA_COMPOSITE_MEDIUM =
    AlphaComposite.getInstance (AlphaComposite.SRC_OVER, .75f);

  /**
   * The default is false.
   */
  public static final boolean GRAPH_BACKGROUND_EXISTENCE_DEFAULT = false;

  /**
   * The default is Color.white.
   */
  public static final Color GRAPH_BACKGROUND_COLOR_DEFAULT = Color.white;

  /**
   * The default is true.
   */
  public static final boolean GRAPH_BORDER_EXISTENCE_DEFAULT = true;

  /**
   * The default is 2.
   */
  public static final int GRAPH_BORDER_THICKNESS_MODEL_DEFAULT = 2;

  /**
   * The default is Color.black.
   */
  public static final Color GRAPH_BORDER_LEFT_BOTTOM_COLOR_DEFAULT = Color.black;

  /**
   * The default is Color.gray.
   */
  public static final Color GRAPH_BORDER_RIGHT_TOP_COLOR_DEFAULT = Color.gray;

  /**
   * The default is false.
   */
  public static final boolean GRAPH_ALLOW_COMPONENT_ALIGNMENT_DEFAULT = false;

  /**
   * The default is false.
   */
  public static final boolean GRAPH_OUTLINE_COMPONENTS_EXISTENCE_DEFAULT = false;

  /**
   * The default is Color.black.
   */
  public static final Color GRAPH_OUTLINE_COMPONENTS_COLOR_DEFAULT = Color.black;

  /**
   * The default is true.
   */
  public static final boolean GRAPH_BETWEEN_COMPONENTS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 2.
   */
  public static final int GRAPH_BETWEEN_COMPONENTS_GAP_THICKNESS_MODEL_DEFAULT = 2;

  /**
   * The default is true.
   */
  public static final boolean GRAPH_BARS_EXISTENCE_DEFAULT = true;

  /**
   * The default is 10.
   */
  public static final int GRAPH_BARS_THICKNESS_MODEL_DEFAULT = 10;

  /**
   * The default is 1f.
   */
  public static final float GRAPH_BARS_EXCESS_SPACE_FEEDBACK_RATIO_DEFAULT = 1f;

  /**
   * The default is .535f.
   */
  public static final float GRAPH_BARS_WITHIN_CATEGORY_OVERLAP_RATIO_DEFAULT = .535f;

  /**
   * The default is false.
   */
  public static final boolean GRAPH_LINES_EXISTENCE_DEFAULT = false;

  /**
   * The default is 5.
   */
  public static final int GRAPH_LINES_THICKNESS_MODEL_DEFAULT = 5;

  /**
   * The default is false.
   */
  public static final boolean GRAPH_LINES_FILL_INTERIOR_DEFAULT = false;

  /**
   * The default is 0f.
   */
  public static final float GRAPH_LINES_EXCESS_SPACE_FEEDBACK_RATIO_DEFAULT = 0f;

  /**
   * The default is 0f.
   */
  public static final float GRAPH_LINES_WITHIN_CATEGORY_OVERLAP_RATIO_DEFAULT = 0f;

  /**
   * The default is false.
   */
  public static final boolean GRAPH_DOTS_EXISTENCE_DEFAULT = false;

  /**
   * The default is 8.
   */
  public static final int GRAPH_DOTS_THICKNESS_MODEL_DEFAULT = 8;

  /**
   * The default is 0f.
   */
  public static final float GRAPH_DOTS_EXCESS_SPACE_FEEDBACK_RATIO_DEFAULT = 0f;

  /**
   * The default is .40f.
   */
  public static final float GRAPH_DOTS_WITHIN_CATEGORY_OVERLAP_RATIO_DEFAULT = .40f;

  /**
   * The default is true.
   */
  public static final boolean GRAPH_NUMBERS_LINES_EXISTENCE_DEFAULT = true;

  /**
   * The default is 2.
   */
  public static final int GRAPH_NUMBERS_LINES_THICKNESS_MODEL_DEFAULT = 2;

  /**
   * The default is CONTINUOUS.
   */
  public static final float[] GRAPH_NUMBERS_LINES_STYLE_DEFAULT = CONTINUOUS;

  /**
   * The default is Color.gray.
   */
  public static final Color GRAPH_NUMBERS_LINES_COLOR_DEFAULT = Color.gray;

  /**
   * The default is false.
   */
  public static final boolean GRAPH_LABELS_LINES_EXISTENCE_DEFAULT = false;

  /**
   * The default is 2.
   */
  public static final int GRAPH_LABELS_LINES_THICKNESS_MODEL_DEFAULT = 2;

  /**
   * The default is CONTINUOUS.
   */
  public static final float[] GRAPH_LABELS_LINES_STYLE_DEFAULT = CONTINUOUS;

  /**
   * The default is Color.gray.
   */
  public static final Color GRAPH_LABELS_LINES_COLOR_DEFAULT = Color.gray;

  /**
   * The default is true.
   */
  public static final boolean GRAPH_LINES_THICKNESS_ASSOCIATION_DEFAULT = true;

  /**
   * The default is TOP.
   */
  public static int GRAPH_COMPONENTS_LIGHT_SOURCE_DEFAULT = TOP;

  /**
   * The default is COMPONENT.
   */
  public static int GRAPH_COMPONENTS_LIGHT_TYPE_DEFAULT = COMPONENT;

  /**
   * The default is .75f.
   */
  public static float GRAPH_BARS_ROUNDING_RATIO_DEFAULT = .75f;

  /**
   * The default is true.
   */
  public static final boolean GRAPH_COMPONENTS_OVERFLOW_CLIP_DEFAULT = true;

  /**
   * The default is ALPHA_COMPOSITE_NONE.
   */
  public static AlphaComposite GRAPH_COMPONENTS_ALPHA_COMPOSITE_DEFAULT = ALPHA_COMPOSITE_NONE;


  private boolean graphBackgroundExistence;
  private Color graphBackgroundColor;
  private boolean graphBorderExistence;
  private int graphBorderThicknessModel;
  private Color graphBorderLeftBottomColor;
  private Color graphBorderRightTopColor;
  private boolean graphAllowComponentAlignment;
  private boolean graphOutlineComponentsExistence;
  private Color graphOutlineComponentsColor;
  private boolean graphBetweenComponentsGapExistence;
  private int graphBetweenComponentsGapThicknessModel;
  private boolean graphBarsExistence;
  private int graphBarsThicknessModel;
  private float graphBarsExcessSpaceFeedbackRatio;
  private float graphBarsWithinCategoryOverlapRatio;
  private boolean graphLinesExistence;
  private int graphLinesThicknessModel;
  private boolean graphLinesFillInterior;
  private float graphLinesExcessSpaceFeedbackRatio;
  private float graphLinesWithinCategoryOverlapRatio;
  private boolean graphDotsExistence;
  private int graphDotsThicknessModel;
  private float graphDotsWithinCategoryOverlapRatio;
  private float graphDotsExcessSpaceFeedbackRatio;
  private boolean graphNumbersLinesExistence;
  private int graphNumbersLinesThicknessModel;
  private float[] graphNumbersLinesStyle;
  private Color graphNumbersLinesColor;
  private boolean graphLabelsLinesExistence;
  private int graphLabelsLinesThicknessModel;
  private float[] graphLabelsLinesStyle;
  private Color graphLabelsLinesColor;
  private boolean graphLinesThicknessAssociation;
  private int graphComponentsLightSource;
  private int graphComponentsLightType;
  private float graphBarsRoundingRatio;
  private boolean graphComponentsOverflowClip;
  private AlphaComposite graphComponentsAlphaComposite;

  private boolean needsUpdate = true;
  private final Vector needsUpdateVector = new Vector (5, 5);
  private final Vector graphChart2DVector = new Vector (5, 5);


  /**
   * Creates a GraphProperties object with the documented default values.
   */
  public GraphProperties() {

    needsUpdate = true;
    setGraphPropertiesToDefaults();
  }


  /**
   * Creates a GraphProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param graphProps The properties to copy.
   */
  public GraphProperties (GraphProperties graphProps) {

    needsUpdate = true;
    setGraphProperties (graphProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setGraphPropertiesToDefaults() {

    needsUpdate = true;
    setGraphBackgroundExistence (GRAPH_BACKGROUND_EXISTENCE_DEFAULT);
    setGraphBackgroundColor (GRAPH_BACKGROUND_COLOR_DEFAULT);
    setGraphBorderExistence (GRAPH_BORDER_EXISTENCE_DEFAULT);
    setGraphBorderThicknessModel (GRAPH_BORDER_THICKNESS_MODEL_DEFAULT);
    setGraphBorderLeftBottomColor (GRAPH_BORDER_LEFT_BOTTOM_COLOR_DEFAULT);
    setGraphBorderRightTopColor (GRAPH_BORDER_RIGHT_TOP_COLOR_DEFAULT);
    setGraphAllowComponentAlignment (GRAPH_ALLOW_COMPONENT_ALIGNMENT_DEFAULT);
    setGraphOutlineComponentsExistence (GRAPH_OUTLINE_COMPONENTS_EXISTENCE_DEFAULT);
    setGraphOutlineComponentsColor (GRAPH_OUTLINE_COMPONENTS_COLOR_DEFAULT);
    setGraphBetweenComponentsGapExistence (GRAPH_BETWEEN_COMPONENTS_GAP_EXISTENCE_DEFAULT);
    setGraphBetweenComponentsGapThicknessModel (
      GRAPH_BETWEEN_COMPONENTS_GAP_THICKNESS_MODEL_DEFAULT);
    setGraphBarsExistence (GRAPH_BARS_EXISTENCE_DEFAULT);
    setGraphBarsThicknessModel (GRAPH_BARS_THICKNESS_MODEL_DEFAULT);
    setGraphBarsExcessSpaceFeedbackRatio (GRAPH_BARS_EXCESS_SPACE_FEEDBACK_RATIO_DEFAULT);
    setGraphBarsWithinCategoryOverlapRatio (GRAPH_BARS_WITHIN_CATEGORY_OVERLAP_RATIO_DEFAULT);
    setGraphLinesExistence (GRAPH_LINES_EXISTENCE_DEFAULT);
    setGraphLinesThicknessModel (GRAPH_LINES_THICKNESS_MODEL_DEFAULT);
    setGraphLinesFillInterior (GRAPH_LINES_FILL_INTERIOR_DEFAULT);
    setGraphLinesExcessSpaceFeedbackRatio (GRAPH_LINES_EXCESS_SPACE_FEEDBACK_RATIO_DEFAULT);
    setGraphLinesWithinCategoryOverlapRatio (GRAPH_LINES_WITHIN_CATEGORY_OVERLAP_RATIO_DEFAULT);
    setGraphDotsExistence (GRAPH_DOTS_EXISTENCE_DEFAULT);
    setGraphDotsThicknessModel (GRAPH_DOTS_THICKNESS_MODEL_DEFAULT);
    setGraphDotsExcessSpaceFeedbackRatio (GRAPH_DOTS_EXCESS_SPACE_FEEDBACK_RATIO_DEFAULT);
    setGraphDotsWithinCategoryOverlapRatio (GRAPH_DOTS_WITHIN_CATEGORY_OVERLAP_RATIO_DEFAULT);
    setGraphNumbersLinesExistence (GRAPH_NUMBERS_LINES_EXISTENCE_DEFAULT);
    setGraphNumbersLinesThicknessModel (GRAPH_NUMBERS_LINES_THICKNESS_MODEL_DEFAULT);
    setGraphNumbersLinesStyle (GRAPH_NUMBERS_LINES_STYLE_DEFAULT);
    setGraphNumbersLinesColor (GRAPH_NUMBERS_LINES_COLOR_DEFAULT);
    setGraphLabelsLinesExistence (GRAPH_LABELS_LINES_EXISTENCE_DEFAULT);
    setGraphLabelsLinesThicknessModel (GRAPH_LABELS_LINES_THICKNESS_MODEL_DEFAULT);
    setGraphLabelsLinesStyle (GRAPH_LABELS_LINES_STYLE_DEFAULT);
    setGraphLabelsLinesColor (GRAPH_LABELS_LINES_COLOR_DEFAULT);
    setGraphLinesThicknessAssociation (GRAPH_LINES_THICKNESS_ASSOCIATION_DEFAULT);
    setGraphComponentsLightSource (GRAPH_COMPONENTS_LIGHT_SOURCE_DEFAULT);
    setGraphComponentsLightType (GRAPH_COMPONENTS_LIGHT_TYPE_DEFAULT);
    setGraphBarsRoundingRatio (GRAPH_BARS_ROUNDING_RATIO_DEFAULT);
    setGraphComponentsOverflowClip (GRAPH_COMPONENTS_OVERFLOW_CLIP_DEFAULT);
    setGraphComponentsAlphaComposite (GRAPH_COMPONENTS_ALPHA_COMPOSITE_DEFAULT);
  }


  /**
   * Sets all properties to be the values of another GraphProperties object.
   * The copying is a deep copy.
   * @param graphProps The properties to copy.
   */
  public final void setGraphProperties (GraphProperties graphProps) {

    needsUpdate = true;
    setGraphBackgroundExistence (graphProps.getGraphBackgroundExistence());
    setGraphBackgroundColor (graphProps.getGraphBackgroundColor());
    setGraphBorderExistence (graphProps.getGraphBorderExistence());
    setGraphBorderThicknessModel (graphProps.getGraphBorderThicknessModel());
    setGraphBorderLeftBottomColor (graphProps.getGraphBorderLeftBottomColor());
    setGraphBorderRightTopColor (graphProps.getGraphBorderRightTopColor());
    setGraphAllowComponentAlignment (graphProps.getGraphAllowComponentAlignment());
    setGraphOutlineComponentsExistence (graphProps.getGraphOutlineComponentsExistence());
    setGraphOutlineComponentsColor (graphProps.getGraphOutlineComponentsColor());
    setGraphBetweenComponentsGapExistence (graphProps.getGraphBetweenComponentsGapExistence());
    setGraphBetweenComponentsGapThicknessModel (graphProps.getGraphBetweenComponentsGapThicknessModel());
    setGraphBarsExistence (graphProps.getGraphBarsExistence());
    setGraphBarsThicknessModel (graphProps.getGraphBarsThicknessModel());
    setGraphBarsExcessSpaceFeedbackRatio (graphProps.getGraphBarsExcessSpaceFeedbackRatio());
    setGraphBarsWithinCategoryOverlapRatio (graphProps.getGraphBarsWithinCategoryOverlapRatio());
    setGraphLinesExistence (graphProps.getGraphLinesExistence());
    setGraphLinesThicknessModel (graphProps.getGraphLinesThicknessModel());
    setGraphLinesFillInterior (graphProps.getGraphLinesFillInterior());
    setGraphLinesExcessSpaceFeedbackRatio (graphProps.getGraphLinesExcessSpaceFeedbackRatio());
    setGraphLinesWithinCategoryOverlapRatio (graphProps.getGraphLinesWithinCategoryOverlapRatio());
    setGraphDotsExistence (graphProps.getGraphDotsExistence());
    setGraphDotsThicknessModel (graphProps.getGraphDotsThicknessModel());
    setGraphDotsExcessSpaceFeedbackRatio (graphProps.getGraphDotsExcessSpaceFeedbackRatio());
    setGraphDotsWithinCategoryOverlapRatio (graphProps.getGraphDotsWithinCategoryOverlapRatio());
    setGraphNumbersLinesExistence (graphProps.getGraphNumbersLinesExistence());
    setGraphNumbersLinesThicknessModel (graphProps.getGraphNumbersLinesThicknessModel());
    setGraphNumbersLinesStyle (graphProps.getGraphNumbersLinesStyle());
    setGraphNumbersLinesColor (graphProps.getGraphNumbersLinesColor());
    setGraphLabelsLinesExistence (graphProps.getGraphLabelsLinesExistence());
    setGraphLabelsLinesThicknessModel (graphProps.getGraphLabelsLinesThicknessModel());
    setGraphLabelsLinesStyle (graphProps.getGraphLabelsLinesStyle());
    setGraphLabelsLinesColor (graphProps.getGraphLabelsLinesColor());
    setGraphLinesThicknessAssociation (graphProps.getGraphLinesThicknessAssociation());
    setGraphComponentsLightSource (graphProps.getGraphComponentsLightSource());
    setGraphComponentsLightType (graphProps.getGraphComponentsLightType());
    setGraphBarsRoundingRatio (graphProps.getGraphBarsRoundingRatio());
    setGraphComponentsOverflowClip (graphProps.getGraphComponentsOverflowClip());
    setGraphComponentsAlphaComposite (graphProps.getGraphComponentsAlphaComposite());
  }


  /**
   * Sets whether the background of this graph exists.  For each chart this
   * graph is added to, this property will only be respected if its the first
   * graph added that the chart; otherwise, graphs added after any chart would
   * totally paint over the previous graph.
   * @param existence If true, the background of this graph will exist.
   */
  public final void setGraphBackgroundExistence (boolean existence) {

    needsUpdate = true;
    graphBackgroundExistence = existence;
  }


  /**
   * Sets the color of the background of this graph.  For each chart this
   * graph is added to, this property will only be respected if its the first
   * graph added that the chart; otherwise, graphs added after any chart would
   * totally paint over the previous graph.
   * @param color The color of the background of this graph.
   */
  public final void setGraphBackgroundColor (Color color) {

    needsUpdate = true;
    graphBackgroundColor = color;
  }


  /**
   * Sets whether the graph's left and bottom border exists.
   * @param existence If true, then the graph's left and bottom border exists.
   */
  public final void setGraphBorderExistence (boolean existence) {

    needsUpdate = true;
    graphBorderExistence = existence;
  }


  /**
   * Sets the thickness of the graph's left and bottom border for the
   * chart's model size.
   * @param thickness The model thickness of the graph's left and bottom border.
   */
  public final void setGraphBorderThicknessModel (int thickness) {

    needsUpdate = true;
    graphBorderThicknessModel = thickness;
  }


  /**
   * Sets the color of the graph's left and bottom border.
   * @param color The color of the graph's left and bottom border.
   */
  public final void setGraphBorderLeftBottomColor (Color color) {

    needsUpdate = true;
    graphBorderLeftBottomColor = color;
  }


  /**
   * Sets the color of the graph's right and top border.
   * @param color The color of the graph's right and top border.
   */
  public final void setGraphBorderRightTopColor (Color color) {

    needsUpdate = true;
    graphBorderRightTopColor = color;
  }


  /**
   * Sets whether the graph's components (ie bars, dots, or lines) are
   * allowed to overlap/align or are offset for each set and within each
   * category.  For non-stacked bars charts, don't align; for all other chart
   * types alignment is generally preferrable.
   * @param alignment If true, the components will not be offset within the
   * category.
   */
  public final void setGraphAllowComponentAlignment (boolean alignment) {

    needsUpdate = true;
    graphAllowComponentAlignment = alignment;
  }


  /**
   * Sets whether there exists a thin outline around each component
   * (ie bars, lines, or dots).
   * @param existence If true, the components will have an outline.
   */
  public final void setGraphOutlineComponentsExistence (boolean existence) {

    needsUpdate = true;
    graphOutlineComponentsExistence = existence;
  }


  /**
   * Sets the color of the thin outline around components
   * (ie bars, lines, or dots).
   * @param color The color of each component's outline.
   */
  public final void setGraphOutlineComponentsColor (Color color) {

    needsUpdate = true;
    graphOutlineComponentsColor = color;
  }


  /**
   * Sets whether a gap between each category of components exists (ie
   * not the gap between each each component with each category).
   * @param existence If true, then the gap between components exists.
   */
  public final void setGraphBetweenComponentsGapExistence (boolean existence) {

    needsUpdate = true;
    graphBetweenComponentsGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between each category of components for
   * the chart's model size.
   * @param thickness The model thickness of teh gap between components.
   */
  public final void setGraphBetweenComponentsGapThicknessModel (int thickness) {

    needsUpdate = true;
    graphBetweenComponentsGapThicknessModel = thickness;
  }


  /**
   * Sets whether the graph contains bar components.
   * @param existence If true, then the graph contains bars.
   */
  public final void setGraphBarsExistence (boolean existence) {

    needsUpdate = true;
    graphBarsExistence = existence;
  }


  /**
   * Sets the thickness of the bar components for the chart's model size.
   * @param thickness The model thickness of the bars.
   */
  public final void setGraphBarsThicknessModel (int thickness) {

    needsUpdate = true;
    graphBarsThicknessModel = thickness;
  }


  /**
   * Sets the amount of the excess space to feed back to bars thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the bars, making them larger.  The ratio is the amount of
   * space to feed back to the bars, to the total amount of space.
   * @param ratio The ratio on the total amount of space to feedback.
   */
  public final void setGraphBarsExcessSpaceFeedbackRatio (float ratio) {

    needsUpdate = true;
    graphBarsExcessSpaceFeedbackRatio = ratio;
  }


  /**
   * Sets how much the bars can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @param ratio The ratio on the thickness of the bar for overlap.
   */
   public final void setGraphBarsWithinCategoryOverlapRatio (float ratio) {

    needsUpdate = true;
    graphBarsWithinCategoryOverlapRatio = ratio;
  }


  /**
   * Sets whether the graph contains line components.
   * @param existence If true, then the graph contains lines.
   */
  public final void setGraphLinesExistence (boolean existence) {

    needsUpdate = true;
    graphLinesExistence = existence;
  }


  /**
   * Sets the thickness of the line components for the chart's model size.
   * @param thickness The model thickness of the lines.
   */
  public final void setGraphLinesThicknessModel (int thickness) {

    needsUpdate = true;
    graphLinesThicknessModel = thickness;
  }


  /**
   * Sets whether the graph lines will made to form a shap (ie like a
   * mountain range).
   * @param fill If true, then the lines will be filled.
   */
  public final void setGraphLinesFillInterior (boolean fill) {

    needsUpdate = true;
    graphLinesFillInterior = fill;
  }


  /**
   * Sets the amount of the excess space to feed back to lines thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the lines, making them larger.  The ratio is the amount of
   * space to feed back to the lines, to the total amount of space.
   * @param ratio The ratio on the total amount of space to feedback.
   */
  public final void setGraphLinesExcessSpaceFeedbackRatio (float ratio) {

    needsUpdate = true;
    graphLinesExcessSpaceFeedbackRatio = ratio;
  }


  /**
   * Sets how much the lines can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @param ratio The ratio on the thickness of the line for overlap.
   */
   public final void setGraphLinesWithinCategoryOverlapRatio (float ratio) {

    needsUpdate = true;
    graphLinesWithinCategoryOverlapRatio = ratio;
  }


  /**
   * Sets whether the graph contains dot components.
   * @param existence If true, then the graph contains dots.
   */
  public final void setGraphDotsExistence (boolean existence) {

    needsUpdate = true;
    graphDotsExistence = existence;
  }


  /**
   * Sets the thickness of the dot components for the chart's model size.
   * @param thickness The model thickness of the dots.
   */
  public final void setGraphDotsThicknessModel (int thickness) {

    needsUpdate = true;
    graphDotsThicknessModel = thickness;
  }


  /**
   * Sets the amount of the excess space to feed back to dots thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the dots, making them larger.  The ratio is the amount of
   * space to feed back to the dots, to the total amount of space.
   * @param ratio The ratio on the total amount of space to feedback.
   */
  public final void setGraphDotsExcessSpaceFeedbackRatio (float ratio) {

    needsUpdate = true;
    graphDotsExcessSpaceFeedbackRatio = ratio;
  }


  /**
   * Sets how much the dots can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @param ratio The ratio on the thickness of the dot for overlap.
   */
   public final void setGraphDotsWithinCategoryOverlapRatio (float ratio) {

    needsUpdate = true;
    graphDotsWithinCategoryOverlapRatio = ratio;
  }


  /**
   * Sets whether the horizontal lines of this graph exist.  These lines
   * are aligned with the axis' ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @param existence If true, the horizontal lines exist.
   */
  public final void setGraphNumbersLinesExistence (boolean existence) {

    needsUpdate = true;
    graphNumbersLinesExistence = existence;
  }


  /**
   * Sets the thickness of the horizontal lines of this graph for the
   * chart's model size.  These lines are aligned with the axis's ticks.  For
   * each chart this graph is added to, this property will only be respected if
   * its the first graph added that the chart; otherwise, graphs added after any
   * chart would paint over the previous graph's components.
   * @param thickness The model thickness of the horizontal lines.
   */
  public final void setGraphNumbersLinesThicknessModel (int thickness) {

    needsUpdate = true;
    graphNumbersLinesThicknessModel = thickness;
  }


  /**
   * Sets the style of the horizontal lines of this graph.  These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.  Possible values for style are:
   * CONTINUOUS, DASHED, and DOTTED.
   * @param style The style of the horizontal lines.
   */
  public final void setGraphNumbersLinesStyle (float[] style) {

    needsUpdate = true;
    graphNumbersLinesStyle = style;
  }


  /**
   * Sets the color of the horizontal lines of this graph. These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @param color The color of the horizontal lines.
   */
  public final void setGraphNumbersLinesColor (Color color) {

    needsUpdate = true;
    graphNumbersLinesColor = color;
  }


  /**
   * Sets whether the vertical lines of this graph exist.  These lines
   * are aligned with the axis' ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @param existence If true, the vertical lines exist.
   */
  public final void setGraphLabelsLinesExistence (boolean existence) {

    needsUpdate = true;
    graphLabelsLinesExistence = existence;
  }


  /**
   * Sets the thickness of the vertical lines of this graph for the
   * chart's model size.  These lines are aligned with the axis's ticks.  For
   * each chart this graph is added to, this property will only be respected if
   * its the first graph added that the chart; otherwise, graphs added after any
   * chart would paint over the previous graph's components.
   * @param thickness The model thickness of the vertical lines.
   */
  public final void setGraphLabelsLinesThicknessModel (int thickness) {

    needsUpdate = true;
    graphLabelsLinesThicknessModel = thickness;
  }


  /**
   * Sets the style of the vertical lines of this graph.  These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.  Possible values for style are:
   * CONTINUOUS, DASHED, and DOTTED.
   * @param style The style of the vertical lines.
   */
  public final void setGraphLabelsLinesStyle (float[] style) {

    needsUpdate = true;
    graphLabelsLinesStyle = style;
  }


  /**
   * Sets the color of the vertical lines of this graph. These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @param color The color of the vertical lines.
   */
  public final void setGraphLabelsLinesColor (Color color) {

    needsUpdate = true;
    graphLabelsLinesColor = color;
  }


  /**
   * Sets whether the horizontal and vertical lines (if they both exist)
   * should both be the same thickness at all times.  Uses the smaller thickness
   * if they are not already equal.
   * @param association If true, then these lines will have equal thickness.
   */
  public final void setGraphLinesThicknessAssociation (boolean association) {

    needsUpdate = true;
    graphLinesThicknessAssociation = association;
  }


  /**
   * Sets the direction of the source of the light if any.
   * Possible values are:  TOP, BOTTOM, LEFT, RIGHT, TOPLEFT, BOTTOMRIGHT, and NONE.
   * @param s The direction of the light source.
   */
  public final void setGraphComponentsLightSource (int s) {

    needsUpdate = true;
    graphComponentsLightSource = s;
  }


  /**
   * Sets the type of the lighting affect.
   * Possible values are:  COMPONENT and GRAPH.
   * COMPONENT implies that the light source is positioned directly on the components (for example
   * leaving a complete shading affect for each component).
   * GRAPH implies that the light source is positioned directly on the graph (for example leaving
   * the components on one side of the graph lighter than the others).
   * @param t The lighting affect type.
   */
  public final void setGraphComponentsLightType (int t) {

    needsUpdate = true;
    graphComponentsLightType = t;
  }


  /**
   * Sets the degree of rounding for the bars.  Uses the RoundRectangle in its implemenation.
   * See the arcw and arch properties for guidance of RoundRectangle.
   * The ratio is the diameter of the half-ellipse making the arc over the dimension in one
   * direction of a bar.  For the "Labels" ratio, the direction is the same direction in which the
   * labels axis runs.  Possible values are between zero and 1.  Zero means less round, one means
   * more round.
   * @param r The rounding ratio.
   */
  public final void setGraphBarsRoundingRatio (float r) {

    needsUpdate = true;
    graphBarsRoundingRatio = r;
  }


  /**
   * Sets whether the graph's components will be clipped if they pass over the graph's inner space
   * or border.  The only time the graph's components should not be clipped is if the graph's
   * inner space and border are set to not exist.  Not clipping may cause components to be painted
   * over other chart components such as the legend or axis.
   * @param c If true, then the components will be clipped.
   */
  public final void setGraphComponentsOverflowClip (boolean c) {

    needsUpdate = true;
    graphComponentsOverflowClip = c;
  }


  /**
   * Sets the actual AlphaComposite object to use on the Graphics2D object context for painting the
   * graph components managed by this GraphProperties object.  By passing different AlphaComposite
   * objects, the graph components can take on a blending or transparency effect.  Underneath
   * components can be seen through components painted over them.  This is especially useful for
   * "line area" or "filled line" charts because top lines can paint over underneath lines if not
   * using a "stacked" dataset object.
   * @param a The AlphaComposite object to use.
   */
  public final void setGraphComponentsAlphaComposite (AlphaComposite a) {

    graphComponentsAlphaComposite = a;
    needsUpdate = true;
  }


  /**
   * Gets whether the background of this graph exists.  For each chart this
   * graph is added to, this property will only be respected if its the first
   * graph added that the chart; otherwise, graphs added after any chart would
   * totally paint over the previous graph.
   * @return boolean If true, the background of this graph will exist.
   */
  public final boolean getGraphBackgroundExistence() {
    return graphBackgroundExistence;
  }


  /**
   * Gets the color of the background of this graph.  For each chart this
   * graph is added to, this property will only be respected if its the first
   * graph added that the chart; otherwise, graphs added after any chart would
   * totally paint over the previous graph.
   * @return Color The color of the background of this graph.
   */
  public final Color getGraphBackgroundColor() {
    return graphBackgroundColor;
  }


  /**
   * Gets whether the graph's left and bottom border exists.
   * @return boolean If true, then the graph's left and bottom border exists.
   */
  public final boolean getGraphBorderExistence() {
    return graphBorderExistence;
  }


  /**
   * Gets the thickness of the graph's left and bottom border for the
   * chart's model size.
   * @return int The model thickness of the graph's left and bottom border.
   */
  public final int getGraphBorderThicknessModel() {
    return graphBorderThicknessModel;
  }


  /**
   * Gets the color of the graph's left and bottom border.
   * @return Color The color of the graph's left and bottom border.
   */
  public final Color getGraphBorderLeftBottomColor() {
    return graphBorderLeftBottomColor;
  }


  /**
   * Gets the color of the graph's right and top border.
   * @return Color The color of the graph's right and top border.
   */
  public final Color getGraphBorderRightTopColor() {
    return graphBorderRightTopColor;
  }


  /**
   * Gets whether the graph's components (ie bars, dots, or lines) are
   * allowed to overlap/align or are offset for each set and within each
   * category.  For non-stacked bars charts, don't align; for all other chart
   * types alignment is generally preferrable.
   * @return boolean If true, the components will not be offset within the
   * category.
   */
  public final boolean getGraphAllowComponentAlignment() {
    return graphAllowComponentAlignment;
  }


  /**
   * Gets whether there exists a thin outline around each component
   * (ie bars, lines, or dots).
   * @return boolean If true, the components will have an outline.
   */
  public final boolean getGraphOutlineComponentsExistence() {
    return graphOutlineComponentsExistence;
  }


  /**
   * Gets the color of the thin outline around components
   * (ie bars, lines, or dots).
   * @return Color The color of each component's outline.
   */
  public final Color getGraphOutlineComponentsColor() {
    return graphOutlineComponentsColor;
  }


  /**
   * Gets whether a gap between each category of components exists (ie
   * not the gap between each each component with each category).
   * @return boolean If true, then the gap between components exists.
   */
  public final boolean getGraphBetweenComponentsGapExistence() {
    return graphBetweenComponentsGapExistence;
  }


  /**
   * Gets the thickness of the gap between each category of components for
   * the chart's model size.
   * @return int The model thickness of teh gap between components.
   */
  public final int getGraphBetweenComponentsGapThicknessModel() {
    return graphBetweenComponentsGapThicknessModel;
  }


  /**
   * Gets whether the graph contains bar components.
   * @return boolean If true, then the graph contains bars.
   */
  public final boolean getGraphBarsExistence() {
    return graphBarsExistence;
  }


  /**
   * Gets the thickness of the bar components for the chart's model size.
   * @return int The model thickness of the bars.
   */
  public final int getGraphBarsThicknessModel() {
    return graphBarsThicknessModel;
  }


  /**
   * Gets the amount of the excess space to feed back to bars thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the bars, making them larger.  The ratio is the amount of
   * space to feed back to the bars, to the total amount of space.
   * @return float The ratio on the total amount of space to feedback.
   */
  public final float getGraphBarsExcessSpaceFeedbackRatio() {
    return graphBarsExcessSpaceFeedbackRatio;
  }


  /**
   * Gets how much the bars can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @return ratio The ratio on the thickness of the bar for overlap.
   */
   public final float getGraphBarsWithinCategoryOverlapRatio() {
    return graphBarsWithinCategoryOverlapRatio;
  }


  /**
   * Gets whether the graph contains line components.
   * @return boolean If true, then the graph contains lines.
   */
  public final boolean getGraphLinesExistence() {
    return graphLinesExistence;
  }


  /**
   * Gets the thickness of the line components for the chart's model size.
   * @return int The model thickness of the lines.
   */
  public final int getGraphLinesThicknessModel() {
    return graphLinesThicknessModel;
  }


  /**
   * Gets whether the graph lines will made to form a shap (ie like a
   * mountain range).
   * @return boolean If true, then the lines will be filled.
   */
  public final boolean getGraphLinesFillInterior() {
    return graphLinesFillInterior;
  }


  /**
   * Gets the amount of the excess space to feed back to lines thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the lines, making them larger.  The ratio is the amount of
   * space to feed back to the lines, to the total amount of space.
   * @return float The ratio on the total amount of space to feedback.
   */
  public final float getGraphLinesExcessSpaceFeedbackRatio() {
    return graphLinesExcessSpaceFeedbackRatio;
  }


  /**
   * Gets how much the lines can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @return ratio The ratio on the thickness of the line for overlap.
   */
   public final float getGraphLinesWithinCategoryOverlapRatio() {
    return graphLinesWithinCategoryOverlapRatio;
  }


  /**
   * Gets whether the graph contains dot components.
   * @return boolean If true, then the graph contains dots.
   */
  public final boolean getGraphDotsExistence() {
    return graphDotsExistence;
  }


  /**
   * Gets the thickness of the dot components for the chart's model size.
   * @return int The model thickness of the dots.
   */
  public final int getGraphDotsThicknessModel() {
    return graphDotsThicknessModel;
  }


  /**
   * Gets the amount of the excess space to feed back to dots thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the dots, making them larger.  The ratio is the amount of
   * space to feed back to the dots, to the total amount of space.
   * @return float The ratio on the total amount of space to feedback.
   */
  public final float getGraphDotsExcessSpaceFeedbackRatio() {
    return graphDotsExcessSpaceFeedbackRatio;
  }


  /**
   * Gets how much the dots can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @return ratio The ratio on the thickness of the dot for overlap.
   */
   public final float getGraphDotsWithinCategoryOverlapRatio() {
    return graphDotsWithinCategoryOverlapRatio;
  }


  /**
   * Gets whether the horizontal lines of this graph exist.  These lines
   * are aligned with the axis' ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @return boolean If true, the horizontal lines exist.
   */
  public final boolean getGraphNumbersLinesExistence() {
    return graphNumbersLinesExistence;
  }


  /**
   * Gets the thickness of the horizontal lines of this graph for the
   * chart's model size.  These lines are aligned with the axis's ticks.  For
   * each chart this graph is added to, this property will only be respected if
   * its the first graph added that the chart; otherwise, graphs added after any
   * chart would paint over the previous graph's components.
   * @return int The model thickness of the horizontal lines.
   */
  public final int getGraphNumbersLinesThicknessModel() {
    return graphNumbersLinesThicknessModel;
  }


  /**
   * Gets the style of the horizontal lines of this graph.  These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.  Possible values for style are:
   * CONTINUOUS, DASHED, and DOTTED.
   * @return float[] The style of the horizontal lines.
   */
  public final float[] getGraphNumbersLinesStyle() {
    return graphNumbersLinesStyle;
  }


  /**
   * Gets the color of the horizontal lines of this graph. These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @return Color The color of the horizontal lines.
   */
  public final Color getGraphNumbersLinesColor() {
    return graphNumbersLinesColor;
  }


  /**
   * Gets whether the vertical lines of this graph exist.  These lines
   * are aligned with the axis' ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @return boolean If true, the vertical lines exist.
   */
  public final boolean getGraphLabelsLinesExistence() {
    return graphLabelsLinesExistence;
  }


  /**
   * Gets the thickness of the vertical lines of this graph for the
   * chart's model size.  These lines are aligned with the axis's ticks.  For
   * each chart this graph is added to, this property will only be respected if
   * its the first graph added that the chart; otherwise, graphs added after any
   * chart would paint over the previous graph's components.
   * @return int The model thickness of the vertical lines.
   */
  public final int getGraphLabelsLinesThicknessModel() {
    return graphLabelsLinesThicknessModel;
  }


  /**
   * Gets the style of the vertical lines of this graph.  These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.  Possible values for style are:
   * CONTINUOUS, DASHED, and DOTTED.
   * @return float[] The style of the vertical lines.
   */
  public final float[] getGraphLabelsLinesStyle() {
    return graphLabelsLinesStyle;
  }


  /**
   * Gets the color of the vertical lines of this graph. These lines
   * are aligned with the axis's ticks.  For each chart this graph is added to,
   * this property will only be respected if its the first graph added that the
   * chart; otherwise, graphs added after any chart would paint over the
   * previous graph's components.
   * @return Color The color of the vertical lines.
   */
  public final Color getGraphLabelsLinesColor() {
    return graphLabelsLinesColor;
  }


  /**
   * Gets whether the horizontal and vertical lines (if they both exist)
   * should both be the same thickness at all times.  Uses the smaller thickness
   * if they are not already equal.
   * @return boolean If true, then these lines will have equal thickness.
   */
  public final boolean getGraphLinesThicknessAssociation() {
    return graphLinesThicknessAssociation;
  }


  /**
   * Gets the direction of the source of the light if any.
   * Possible values are:  TOP, BOTTOM, LEFT, RIGHT, and NONE.
   * @return The direction of the light source.
   */
  public final int getGraphComponentsLightSource() {
    return graphComponentsLightSource;
  }


  /**
   * Gets the type of the lighting affect.
   * Possible values are:  COMPONENT and GRAPH.
   * COMPONENT implies that the light source is positioned directly on the components (for example
   * leaving a complete shading affect for each component).
   * GRAPH implies that the light source is positioned directly on the graph (for example leaving
   * the components on one side of the graph lighter than the others).
   * @return The lighting affect type.
   */
  public final int getGraphComponentsLightType() {
    return graphComponentsLightType;
  }


  /**
   * Gets the degree of rounding for the bars.  Uses the RoundRectangle in its implemenation.
   * See the arcw and arch properties for guidance of RoundRectangle.
   * The ratio is the diameter of the half-ellipse making the arc over the dimension in one
   * direction of a bar.  For the "Labels" ratio, the direction is the same direction in which the
   * labels axis runs.  Possible values are between zero and 1.  Zero means less round, one means
   * more round.
   * @return The rounding ratio.
   */
  public final float getGraphBarsRoundingRatio() {
    return graphBarsRoundingRatio;
  }


  /**
   * Gets whether the graph's components will be clipped if they pass over the graph's inner
   * space or border.  The only time the graph's components should not be clipped is if the graph's
   * inner space and border are set to not exist.  Not clipping may cause components to be painted
   * over other chart components such as the legend or axis.
   * @return If true, then the components will be clipped.
   */
  public final boolean getGraphComponentsOverflowClip() {
    return graphComponentsOverflowClip;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param graphChart2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getGraphChart2DNeedsUpdate (GraphChart2D graphChart2D) {

    if (needsUpdate) return true;

    int index = -1;
    if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }

    return false;
  }


  /**
   * Gets the actual AlphaComposite object to use on the Graphics2D object context for painting the
   * graph components managed by this GraphProperties object.  By passing different AlphaComposite
   * objects, the graph components can take on a blending or transparency effect.  Underneath
   * components can be seen through components painted over them.  This is especially useful for
   * "line area" or "filled line" charts because top lines can paint over underneath lines if not
   * using a "stacked" dataset object.
   * @return The AlphaComposite object to use.
   */
  public final AlphaComposite getGraphComponentsAlphaComposite() {
    return graphComponentsAlphaComposite;
  }


  /**
   * Adds a GraphChart2D to the set of objects using these properties.
   * @param graphChart2D The Object2D to add.
   */
  final void addGraphChart2D (GraphChart2D graphChart2D) {

    if (!graphChart2DVector.contains (graphChart2D)) {
      graphChart2DVector.add (graphChart2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes a GraphChart2D from the set of objects using these properties.
   * @param graphChart2D The Object2D to remove.
   */
  final void removeGraphChart2D (GraphChart2D graphChart2D) {

    int index = -1;
    if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
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

    if (debug) System.out.println ("Validating GraphProperties");

    boolean valid = true;

    if (graphBackgroundColor == null) {
      valid = false;
      if (debug) System.out.println ("GraphBackgroundColor == null");
    }
    if (graphBorderThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("GraphBorderThicknessModel < 0");
    }
    if (graphBorderLeftBottomColor == null) {
      valid = false;
      if (debug) System.out.println ("GraphBorderLeftBottomColor == null");
    }
    if (graphBorderRightTopColor == null) {
      valid = false;
      if (debug) System.out.println ("GraphBorderRightTopColor == null");
    }
    if (graphOutlineComponentsColor == null) {
      valid = false;
      if (debug) System.out.println ("GraphOutlineComponentsColor == null");
    }
    if (graphBetweenComponentsGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("GraphBetweenComponentsGapThicknessModel < 0");
    }
    if (graphBarsThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("GraphBarsThicknessModel < 0");
    }
    if (graphBarsExcessSpaceFeedbackRatio < 0f || graphBarsExcessSpaceFeedbackRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with graphBarsExcessSpaceFeedbackRatio");
    }
    if (graphBarsWithinCategoryOverlapRatio < 0f || graphBarsWithinCategoryOverlapRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with graphBarsWithinCategoryOverlapRatio");
    }
    if (graphLinesThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("GraphLinesThicknessModel < 0");
    }
    if (graphLinesExcessSpaceFeedbackRatio < 0f || graphLinesExcessSpaceFeedbackRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with graphLinesExcessSpaceFeedbackRatio");
    }
    if (graphLinesWithinCategoryOverlapRatio < 0f || graphLinesWithinCategoryOverlapRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with graphLinesWithinCategoryOverlapRatio");
    }
    if (graphDotsThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("GraphDotsThicknessModel < 0");
    }
    if (graphDotsExcessSpaceFeedbackRatio < 0f || graphDotsExcessSpaceFeedbackRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with graphDotsExcessSpaceFeedbackRatio");
    }
    if (graphDotsWithinCategoryOverlapRatio < 0f || graphDotsWithinCategoryOverlapRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with graphDotsWithinCategoryOverlapRatio");
    }
    if (graphNumbersLinesThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("GraphNumbersLinesThicknessModel < 0");
    }
    if (graphNumbersLinesStyle != CONTINUOUS &&
      graphNumbersLinesStyle != DASHED &&
      graphNumbersLinesStyle != DOTTED) {
      valid = false;
      if (debug) System.out.println ("Problem with graphNumbersLinesStyle");
    }
    if (graphNumbersLinesColor ==  null) {
      valid = false;
      if (debug) System.out.println ("GraphNumbersLinesColor ==  null");
    }
    if (graphLabelsLinesThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("GraphLabelsLinesThicknessModel < 0");
    }
    if (graphLabelsLinesStyle != CONTINUOUS &&
      graphLabelsLinesStyle != DASHED &&
      graphLabelsLinesStyle != DOTTED) {
      valid = false;
      if (debug) System.out.println ("problem with graphLabelsLinesStyle");
    }
    if (graphLabelsLinesColor == null) {
      valid = false;
      if (debug) System.out.println ("GraphLabelsLinesColor == null");
    }
    if (graphComponentsLightSource != NONE &&
      graphComponentsLightSource != TOP &&
      graphComponentsLightSource != BOTTOM &&
      graphComponentsLightSource != LEFT &&
      graphComponentsLightSource != RIGHT) {
      valid = false;
      if (debug) System.out.println ("Problem with GraphComponentsLightSource");
    }
    if (graphComponentsLightType != COMPONENT && graphComponentsLightType != GRAPH) {
      valid = false;
      if (debug) System.out.println ("Problem with GraphComponentsLightType");
    }
    if (graphBarsRoundingRatio < 0f || graphBarsRoundingRatio > 1f) {
      valid = false;
      if (debug) System.out.println ("Problem with GraphBarsRoundingRatio");
    }
    if (graphComponentsAlphaComposite == null) {
      valid = false;
      if (debug) System.out.println ("graphComponentsAlphaComposite == null");
    }

    if (debug) {
      if (valid) System.out.println ("GraphProperties was valid");
      else System.out.println ("GraphProperties was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this GraphChart2D.
   * @param graphChart2D The object to update.
   */
  final void updateGraphChart2D (GraphChart2D graphChart2D) {

    if (getGraphChart2DNeedsUpdate (graphChart2D)) {

      if (needsUpdate) {
        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }
        needsUpdate = false;
      }

      int index = -1;
      if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }


  /**
   * Accepts a graph area and configures it with current properties.
   * @param type Whether the graph is labels bottom or labels left using GraphChart2D fields.
   * @param graph The graph area to configure.
   */
  final void configureGraphArea (int type, GraphArea graph) {

    graph.setBackgroundExistence (getGraphBackgroundExistence());
    graph.setBackgroundColor (getGraphBackgroundColor());
    graph.setBorderExistence (getGraphBorderExistence());
    graph.setBorderThicknessModel (getGraphBorderThicknessModel());
    graph.setBorderColors (
      getGraphBorderLeftBottomColor(), getGraphBorderRightTopColor(),
      getGraphBorderRightTopColor(),   getGraphBorderLeftBottomColor());
    graph.setAllowComponentAlignment (getGraphAllowComponentAlignment());
    graph.setOutlineComponents (getGraphOutlineComponentsExistence());
    graph.setOutlineComponentsColor (getGraphOutlineComponentsColor());
    graph.setBetweenComponentsGapExistence (getGraphBetweenComponentsGapExistence());
    graph.setBetweenComponentsGapThicknessModel (getGraphBetweenComponentsGapThicknessModel());
    graph.setBarsExistence (getGraphBarsExistence());
    graph.setBarsThicknessModel (getGraphBarsThicknessModel());
    graph.setBarsExcessSpaceFeedbackRatio (getGraphBarsExcessSpaceFeedbackRatio());
    graph.setBarsWithinCategoryOverlapRatio (getGraphBarsWithinCategoryOverlapRatio());
    graph.setLinesExistence (getGraphLinesExistence());
    graph.setLinesThicknessModel (getGraphLinesThicknessModel());
    graph.setLinesFillInterior (getGraphLinesFillInterior());
    graph.setLinesExcessSpaceFeedbackRatio (getGraphLinesExcessSpaceFeedbackRatio());
    graph.setLinesWithinCategoryOverlapRatio (getGraphLinesWithinCategoryOverlapRatio());
    graph.setDotsExistence (getGraphDotsExistence());
    graph.setDotsThicknessModel (getGraphDotsThicknessModel());
    graph.setDotsExcessSpaceFeedbackRatio (getGraphDotsExcessSpaceFeedbackRatio());
    graph.setDotsWithinCategoryOverlapRatio (getGraphDotsWithinCategoryOverlapRatio());
    graph.setBarRoundingRatio (getGraphBarsRoundingRatio());

    if (type == GraphChart2D.LABELS_BOTTOM) {

      graph.setHorizontalLinesExistence (getGraphNumbersLinesExistence());
      graph.setHorizontalLinesThicknessModel (getGraphNumbersLinesThicknessModel());
      graph.setHorizontalLinesStyle (getGraphNumbersLinesStyle());
      graph.setHorizontalLinesColor (getGraphNumbersLinesColor());
      graph.setVerticalLinesExistence (getGraphLabelsLinesExistence());
      graph.setVerticalLinesThicknessModel (getGraphLabelsLinesThicknessModel());
      graph.setVerticalLinesStyle (getGraphLabelsLinesStyle());
      graph.setVerticalLinesColor (getGraphLabelsLinesColor());
    }
    else {

      graph.setVerticalLinesExistence (getGraphNumbersLinesExistence());
      graph.setVerticalLinesThicknessModel (getGraphNumbersLinesThicknessModel());
      graph.setVerticalLinesStyle (getGraphNumbersLinesStyle());
      graph.setVerticalLinesColor (getGraphNumbersLinesColor());
      graph.setHorizontalLinesExistence (getGraphLabelsLinesExistence());
      graph.setHorizontalLinesThicknessModel (getGraphLabelsLinesThicknessModel());
      graph.setHorizontalLinesStyle (getGraphLabelsLinesStyle());
      graph.setHorizontalLinesColor (getGraphLabelsLinesColor());
    }

    graph.setLinesThicknessAssociation (getGraphLinesThicknessAssociation());
    graph.setComponentsLightSource (getGraphComponentsLightSource());
    graph.setComponentsLightType (getGraphComponentsLightType());
    graph.setClip (getGraphComponentsOverflowClip());
    graph.setComponentsAlphaComposite (getGraphComponentsAlphaComposite());
  }


  /**
   * Accepts a graph area and configures it with current properties.
   * Uses the properties of the background graph in order to overlay correctly.
   * @param backgroundGraphProps The properties of the background graph.
   * @param type Whether the graph is labels bottom or labels left using GraphChart2D fields.
   * @param graph The graph area to configure.
   */
  final void configureGraphArea (GraphProperties backgroundGraphProps, int type, GraphArea graph) {

    graph.setBackgroundExistence (false);
    graph.setBorderExistence (backgroundGraphProps.getGraphBorderExistence());
    graph.setBorderThicknessModel (backgroundGraphProps.getGraphBorderThicknessModel());
    graph.setBorderColors (
      backgroundGraphProps.getGraphBorderLeftBottomColor(),
      backgroundGraphProps.getGraphBorderRightTopColor(),
      backgroundGraphProps.getGraphBorderRightTopColor(),
      backgroundGraphProps.getGraphBorderLeftBottomColor());
    graph.setAllowComponentAlignment (getGraphAllowComponentAlignment());
    graph.setOutlineComponents (getGraphOutlineComponentsExistence());
    graph.setOutlineComponentsColor (getGraphOutlineComponentsColor());
    graph.setBetweenComponentsGapExistence (getGraphBetweenComponentsGapExistence());
    graph.setBetweenComponentsGapThicknessModel (getGraphBetweenComponentsGapThicknessModel());
    graph.setBarsExistence (getGraphBarsExistence());
    graph.setBarsThicknessModel (getGraphBarsThicknessModel());
    graph.setBarsExcessSpaceFeedbackRatio (getGraphBarsExcessSpaceFeedbackRatio());
    graph.setBarsWithinCategoryOverlapRatio (getGraphBarsWithinCategoryOverlapRatio());
    graph.setLinesExistence (getGraphLinesExistence());
    graph.setLinesThicknessModel (getGraphLinesThicknessModel());
    graph.setLinesFillInterior (getGraphLinesFillInterior());
    graph.setLinesExcessSpaceFeedbackRatio (getGraphLinesExcessSpaceFeedbackRatio());
    graph.setLinesWithinCategoryOverlapRatio (getGraphLinesWithinCategoryOverlapRatio());
    graph.setDotsExistence (getGraphDotsExistence());
    graph.setDotsThicknessModel (getGraphDotsThicknessModel());
    graph.setDotsExcessSpaceFeedbackRatio (getGraphDotsExcessSpaceFeedbackRatio());
    graph.setDotsWithinCategoryOverlapRatio (getGraphDotsWithinCategoryOverlapRatio());

    graph.setHorizontalLinesExistence (false);
    graph.setVerticalLinesExistence (false);
    graph.setBarRoundingRatio (getGraphBarsRoundingRatio());

    graph.setComponentsLightSource (getGraphComponentsLightSource());
    graph.setComponentsLightType (getGraphComponentsLightType());
    graph.setClip (getGraphComponentsOverflowClip());
    graph.setComponentsAlphaComposite (getGraphComponentsAlphaComposite());
  }
}