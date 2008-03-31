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
 * A container for many variables and components relating to a graph area.
 * A graph area is the area that both the y axis and x axis touch, and in which
 * bars, lines, or dots are plotted to represent the data set.
 */
class GraphArea extends Area {


  /**
   * Indicates the continuous.
   */
  static float[] CONTINUOUS = {10.0f, 0.0f};
  /**
   * Indicates the dashed.
   */
  static float[] DASHED = {7.0f, 3.0f};
  /**
   * Indicates the dotted.
   */
  static float[] DOTTED = {3.0f, 3.0f};
  /**
   * Indicates positive.
   */
  static int POS = 1;
  /**
   * Indicates both positive and negative.
   */
  static int MIX = 0;
  /**
   * Indicates negative.
   */
  static int NEG = -1;


  static final int COMPONENT = 0;
  static final int GRAPH = 1;


  private int type;
  private boolean allowComponentAlignment;

  private Rectangle[] xTicks;
  private Rectangle[] yTicks;
  private int ticksAlignment;
  private int[][] graphValues;
  private int[][] barLowValues;

  private boolean linesThicknessAssociation;
  private boolean verticalLinesExistence;
  private Line2D.Double[] verticalLines;
  private int verticalLinesThicknessModel;
  private int verticalLinesThickness;
  private Color verticalLinesColor;
  private float[] verticalLinesStyle;
  private BasicStroke verticalLinesStroke;

  private boolean horizontalLinesExistence;
  private Line2D.Double[] horizontalLines;
  private int horizontalLinesThicknessModel;
  private int horizontalLinesThickness;
  private Color horizontalLinesColor;
  private float[] horizontalLinesStyle;
  private BasicStroke horizontalLinesStroke;

  private boolean barsExistence;
  private int barsThicknessModel;
  private Color[] barColors;
  private float barsExcessSpaceFeedbackRatio;
  private float barsWithinCategoryOverlapRatio;

  private boolean dotsExistence;
  private int dotsThicknessModel;
  private Color[] dotColors;
  private float dotsExcessSpaceFeedbackRatio;
  private float dotsWithinCategoryOverlapRatio;

  private boolean linesExistence;
  private int linesThicknessModel;
  private Color[] lineColors;
  private boolean linesFillInterior;
  private int linesFillInteriorBaseValue;
  private float linesExcessSpaceFeedbackRatio;
  private float linesWithinCategoryOverlapRatio;

  private boolean outlineComponents;
  private Color outlineComponentsColor;

  private boolean betweenComponentsGapExistence;
  private int betweenComponentsGapThicknessModel;

  private float barRoundingRatio;
  private int roundSide;
  private int lightSource;
  private int lightType;
  private Vector warningRegions;
  private boolean clip;
  private boolean componentsColoringByCat;
  private Color[] componentsColorsByCat;

  private int dataSign;

  private AlphaComposite componentsAlphaComposite;

  private boolean needsUpdate;


  /**
   * Creates a graph area with the default values of the Area class
   * (except where overridden here), and its own default values.
   */
  GraphArea() {

    setType (LABELSBOTTOM);
    setGraphValues (new int[0][0]);
    setBarLowValues (new int[0][0]);
    setAllowComponentAlignment (false);

    setAutoSizes (false, false);
    setAutoJustifys (false, false);
    setBackgroundColor (Color.lightGray);
    setBorderAssociations (false, false, true, true, false, false);
    setBorderCornerAssociations (LEFT, LEFT, RIGHT, RIGHT);
    setBorderColors (Color.black, Color.gray, Color.gray, Color.black);

    setXTicks (new Rectangle[0]);
    setYTicks (new Rectangle[0]);

    setLinesThicknessAssociation (true);
    setHorizontalLinesExistence (true);
    setHorizontalLinesThicknessModel (2);
    setHorizontalLinesStyle (CONTINUOUS);
    setHorizontalLinesColor (Color.gray);
    setVerticalLinesExistence (false);
    setVerticalLinesThicknessModel (2);
    setVerticalLinesStyle (CONTINUOUS);
    setVerticalLinesColor (Color.gray);

    setBarsExistence (true);
    setBarsThicknessModel (10);
    setBarColors (new Color[0]);
    setBarsExcessSpaceFeedbackRatio (.75f);
    setBarsWithinCategoryOverlapRatio (.5f);

    setDotsExistence (false);
    setDotsThicknessModel (8);
    setDotColors (new Color[0]);
    setDotsExcessSpaceFeedbackRatio (0f);
    setDotsWithinCategoryOverlapRatio (.5f);

    setLinesExistence (false);
    setLinesThicknessModel (4);
    setLineColors (new Color[0]);
    setLinesFillInterior (false);
    setLinesFillInteriorBaseValue (0);
    setLinesExcessSpaceFeedbackRatio (0f);
    setLinesWithinCategoryOverlapRatio (.5f);

    setBetweenComponentsGapExistence (true);
    setBetweenComponentsGapThicknessModel (2);
    setLabelsAxisTicksAlignment (BETWEEN);

    setGapExistence (false);
    setOutlineComponents (true);
    setOutlineComponentsColor (Color.black);

    setBarRoundingRatio (.25f);
    setComponentsLightSource (FancyShape.LEFT);
    setComponentsLightType (COMPONENT);

    setClip (true);
    setComponentsColoringByCat (false);
    setComponentsColorsByCat (new Color[0]);

    resetGraphAreaModel (true);
    needsUpdate = true;
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
  public final void setComponentsAlphaComposite (AlphaComposite a) {

    componentsAlphaComposite = a;
    needsUpdate = true;
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
  public final AlphaComposite getComponentsAlphaComposite() {
    return componentsAlphaComposite;
  }


  /**
   * Sets which kinds of the data is graphed.
   * Use POS for non-negative data.
   * Use NEG for non-positive data.
   * Use MIX for both positive and negative data.
   * @param sign The sign of the data.
   */
  final void setDataSign (int sign) {

    dataSign = sign;
    needsUpdate = true;
  }


  /**
   * Gets which kinds of the data is graphed.
   * Use POS for non-negative data.
   * Use NEG for non-positive data.
   * Use MIX for both positive and negative data.
   * @return The sign of the data.
   */
  final int getDataSign() {

    return dataSign;
  }


  /**
   * Sets whether the graph components are colored by category or by set.
   * @param b If true, then colored by category.
   */
  final void setComponentsColoringByCat (boolean b) {
    needsUpdate = true;
    componentsColoringByCat = b;
  }


  /**
   * Sets the color array for the category coloring.
   * @param c The color array.
   */
  final void setComponentsColorsByCat (Color[] c) {
    needsUpdate = true;
    componentsColorsByCat = c;
  }


  /**
   * Sets the whether the overpaint of the graph components are clipped or not.
   * @param c If true then the components are clipped.
   */
  final void setClip (boolean c) {
    needsUpdate = true;
    clip = c;
  }


  /**
   * Sets the warning regions vector for applying to the graph background and the graph components.
   * @param v The warning regions vector.
   */
  final void setWarningRegions (Vector v) {
    warningRegions = v;
    needsUpdate = true;
  }


  /**
   * Sets the ratio of the rounding arc to the thickness of the bar.
   * Values need to be between zero and one.  Zero is square.  One is perfectly round.
   * @param r The ratio.
   */
  final void setBarRoundingRatio (float r) {
    barRoundingRatio = r;
    needsUpdate = true;
  }


  /**
   * Sets the side from which the lighting affect originates.
   * all components for each set).
   * For specifying the side, use the fields of FancyShape:
   *  TOP, BOTTOM, LEFT, RIGHT, TOPLEFT, BOTTOMRIGHT, and NONE.
   * Added for Siperian.
   * @param s The side.
   */
  final void setComponentsLightSource (int s) {
    lightSource = s;
    needsUpdate = true;
  }


  /**
   * Sets the type of lighting affect.
   * Type refers to whether the lighting should begin and end by component or by graph (meaning
   * all components for each set).
   * For specifying the type use the fields COMPONENT and GRAPH.
   * Added for Siperian.
   * @param t The type.
   */
  final void setComponentsLightType (int t) {
    lightType = t;
    needsUpdate = true;
  }


  /**
   * Indicates whether bars, lines, and/or dots should have a thin black
   * outline around them.
   * @param outline If true, then there will be an outline.
   */
   final void setOutlineComponents (boolean outline) {

    needsUpdate = true;
    outlineComponents = outline;
   }


  /**
   * Indicates the color of the components outline if it exists.
   * @param color The color for the outline.
   */
   final void setOutlineComponentsColor (Color color) {

    needsUpdate = true;
    outlineComponentsColor = color;
   }


  /**
   * Placement of the graph components (bars, dots, or line points) depends
   * to some degree on placement of the ticks.  (It shouldn't, but it does in
   * order to have things lined up within 1 pixel error).  Pass the alignment
   * setting of the ticks of the axis that has the data description "labels".
   * @param alignment A value of either Area.BETWEEN or Area.CENTERED
   */
   final void setLabelsAxisTicksAlignment (int alignment) {
    needsUpdate = true;
    ticksAlignment = alignment;
   }


  /**
   * The type of the graph area.  There exist two types of graph areas.  One
   * has the data descriptors on the bottom (i.e. vertical bar chart); the other
   * has them on the left (i.e. horizontal bar chart).
   * @param type The type of the graph area.  [LABELSBOTTOM or LABELSLEFT]
   */
  final void setType (int type) {

    needsUpdate = true;
    this.type = type;
  }


  /**
   * Specifies whether graph components, bars, lines, or dots are offset from
   * eachother.  Generally, var charts are not aligned, and line and dot charts
   * are.
   * @param allow If true, then aligns the components.
   */
  final void setAllowComponentAlignment (boolean allow) {

    needsUpdate = true;
    allowComponentAlignment = allow;
  }


  /**
   * The heights/widths of the components.  This is respective to the bottom of
   * the graph area, but above the border/to the left of the graph area.  This
   * is an array of arrays of values.  The first array contains data sets.  The
   * inner arrays contains the heights/widths for each set.  Heights are used
   * for when the type of graph area is LABELSBOTTOM.  Otherwise, widths.
   * @param values The offsets of the components from the data descriptor axis.
   */
  final void setGraphValues (int[][] values) {

    needsUpdate = true;
    graphValues = values;
  }


  /**
   * Determines where bars of bar charts begin.  Generally bars begin at zero,
   * but with stackable charts, bars sometimes need to start at the tops of
   * other bars.  Also, bars can be used to signify uncertainty (i.e. 3+-2) can
   * be signified by a bar that starts at 1 and goes to 5.
   * @param values The low values normalized the graph area (i.e. not the actual
   * data set values).
   */
   final void setBarLowValues (int[][] values) {

    needsUpdate = true;
    barLowValues = values;
   }


  /**
   * Sets the ticks of the x axis.  This is necessary in order to make sure the
   * components area exactly where they should be, either between the ticks or
   * aligned exactly respective to the middle of them.
   * @param ticks  The bounds of the ticks, location and size.
   */
  final void setXTicks (Rectangle[] ticks) {

    needsUpdate = true;
    this.xTicks = ticks;
  }


  /**
   * Sets the ticks of the x axis.  This is necessary in order to make sure the
   * components area exactly where they should be, either between the ticks or
   * aligned exactly respective to the middle of them.
   * @param ticks  The bounds of the ticks, location and size.
   */
  final void setYTicks (Rectangle[] ticks) {

    needsUpdate = true;
    this.yTicks = ticks;
  }


  /**
   * Specifies whether the vertical and/or horizontal lines should maintain
   * the same size.  The vertical/horizontal lines are do not represent the
   * data sets.  These lines are either perfectly horizontal or perfectly
   * vertical and mark out the graph area.
   * @param association  If true, then the lines thicknesses will be equal.
   */
  final void setLinesThicknessAssociation (boolean association) {

    needsUpdate = true;
    linesThicknessAssociation = association;
  }


  /**
   * Whether the horizontal lines exist.
   * @param existence If true, then they do.
   */
  final void setHorizontalLinesExistence (boolean existence) {

    needsUpdate = true;
    horizontalLinesExistence = existence;
  }


  /**
   * The model thickness for the horizontal lines.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.
   * @param thickness The model thickness for the horizontal lines.
   */
  final void setHorizontalLinesThicknessModel (int thickness) {

    needsUpdate = true;
    horizontalLinesThicknessModel = thickness;
  }


  /**
   * The style of the horizontal lines.  Lines may be continuous, dashed and
   * dotted.  Or something other.  See BasicStroke for more information.
   * @param style The style of the lines [CONTINOUS, DASHED, DOTTED]
   */
  final void setHorizontalLinesStyle (float[] style) {

    needsUpdate = true;
    horizontalLinesStyle = style;
  }


  /**
   * The color of the horizontal lines.
   * @param color Some Color.
   */
  final void setHorizontalLinesColor (Color color) {

    needsUpdate = true;
    horizontalLinesColor = color;
  }


  /**
   * Whether the vertical lines exist.
   * @param existence If true, then they do.
   */
  final void setVerticalLinesExistence (boolean existence) {

    needsUpdate = true;
    verticalLinesExistence = existence;
  }


  /**
   * The model thickness for the vertical lines.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.
   * @param thickness The model thickness for the vertical lines.
   */
  final void setVerticalLinesThicknessModel (int thickness) {

    needsUpdate = true;
    verticalLinesThicknessModel = thickness;
  }


  /**
   * The style of the vertical lines.  Lines may be continuous, dashed and
   * dotted.  Or something other.  See BasicStroke for more information.
   * @param style The style of the lines [CONTINOUS, DASHED, DOTTED]
   */
  final void setVerticalLinesStyle (float[] style) {

    needsUpdate = true;
    verticalLinesStyle = style;
  }


  /**
   * The color of the vertical lines.
   * @param color Some Color.
   */
  final void setVerticalLinesColor (Color color) {

    needsUpdate = true;
    verticalLinesColor = color;
  }


  /**
   * Whether to paint bars representing the graph values.  If they do not exist
   * then they will not be included in calculations or in painting.
   * @param existence If true, then they exist.
   */
  final void setBarsExistence (boolean existence) {

    needsUpdate = true;
    barsExistence = existence;
  }


  /**
   * The model thickness for the bars.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.
   * @param thickness The model thickness for the bars.
   */
  final void setBarsThicknessModel (int thickness) {

    needsUpdate = true;
    barsThicknessModel = thickness;
  }


  /**
   * The colors of the bar sets.  Each data set has its own color.  In every
   * set of bars, the left most bar (or first bar painted when components are
   * aligned) will be the color in the lowest order array position.
   * @param colors The colors for the data sets.
   */
  final void setBarColors (Color[] colors) {

    needsUpdate = true;
    barColors = colors;
  }


  /**
   * Specifies the amount of the excess space to feed back to bars thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the bars, making them larger.  The ratio is the amount of
   * space to feed back to the bars, to the total amount of space.
   * @param ratio The ratio on the total amount of space to feedback.
   */
  final void setBarsExcessSpaceFeedbackRatio (float ratio) {

    needsUpdate = true;
    barsExcessSpaceFeedbackRatio = ratio;
  }


  /**
   * Specifies how much the bars can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @param ratio The ratio on the thickness of the bar for overlap.
   */
   final void setBarsWithinCategoryOverlapRatio (float ratio) {
    needsUpdate = true;
    barsWithinCategoryOverlapRatio = ratio;
  }


  /**
   * Whether to paint dots representing the graph values.  If they do not exist
   * then they will not be included in calculations or in painting.
   * @param existence If true, then they exist.
   */
  final void setDotsExistence (boolean existence) {

    needsUpdate = true;
    dotsExistence = existence;
  }


  /**
   * The model thickness for the dots.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.
   * @param thickness The model thickness for the dots.
   */
  final void setDotsThicknessModel (int thickness) {

    needsUpdate = true;
    dotsThicknessModel = thickness;
  }


  /**
   * Specifies how much the dots can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @param ratio The ratio on the thickness of the dot for overlap.
   */
   final void setDotsWithinCategoryOverlapRatio (float ratio) {
    needsUpdate = true;
    dotsWithinCategoryOverlapRatio = ratio;
  }


  /**
   * The colors of the dot sets.  Each data set has its own color.  In every
   * set of dots, the left most dot (or first dot painted when components are
   * aligned) will be the color in the lowest order array position.
   * @param colors The colors for the data sets.
   */
  final void setDotColors (Color[] colors) {

    needsUpdate = true;
    dotColors = colors;
  }


  /**
   * Specifies the amount of the excess space to feed back to dots thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the dots, making them larger.  The ratio is the amount of
   * space to feed back to the dots, to the total amount of space.
   * @param ratio The ratio on the total amount of space to feedback.
   */
  final void setDotsExcessSpaceFeedbackRatio (float ratio) {

    needsUpdate = true;
    dotsExcessSpaceFeedbackRatio = ratio;
  }


  /**
   * Whether to paint lines representing the graph values.  If they do not exist
   * then they will not be included in calculations or in painting.
   * @param existence If true, then they exist.
   */
  final void setLinesExistence (boolean existence) {

    needsUpdate = true;
    linesExistence = existence;
  }


  /**
   * The model thickness for the lines.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.
   * @param thickness The model thickness for the lines.
   */
  final void setLinesThicknessModel (int thickness) {

    needsUpdate = true;
    linesThicknessModel = thickness;
  }


  /**
   * Indicates whether the region between the lines and between its baseline
   * should be filled in.
   * @param fill If true, then the region under/above the lines will be filled.
   */
  final void setLinesFillInterior (boolean fill) {

    needsUpdate = true;
    linesFillInterior = fill;
  }


  /**
   * Indicates the length across the graph length, in the same direction of the
   * meaning of the graph values, that the forms the base for the filled region.
   * @param int The base value for the region.
   */
  final void setLinesFillInteriorBaseValue (int value) {

    needsUpdate = true;
    linesFillInteriorBaseValue = value;
  }


  /**
   * The colors of the line sets.  Each data set has its own color.  In every
   * set of lines, the left most line (or first line painted when components are
   * aligned) will be the color in the lowest order array position.
   * @param colors The colors for the data sets.
   */
  final void setLineColors (Color[] colors) {

    needsUpdate = true;
    lineColors = colors;
  }


  /**
   * Specifies the amount of the excess space to feed back to lines thickness.
   * Frequently the graphs are larger than necessary, the excess space can
   * be fedback to the lines, making them larger.  The ratio is the amount of
   * space to feed back to the lines, to the total amount of space.
   * @param ratio The ratio on the total amount of space to feedback.
   */
  final void setLinesExcessSpaceFeedbackRatio (float ratio) {

    needsUpdate = true;
    linesExcessSpaceFeedbackRatio = ratio;
  }


  /**
   * Specifies how much the lines can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @param ratio The ratio on the thickness of the line for overlap.
   */
   final void setLinesWithinCategoryOverlapRatio (float ratio) {
    needsUpdate = true;
    linesWithinCategoryOverlapRatio = ratio;
  }


  /**
   * Whether there exists a gap between plotted data sets.  For instance, after
   * plotting a single bar from each data set, the gap between these and the
   * next bars from the next location within the data sets.
   * @param existence If true, then it does.
   */
  final void setBetweenComponentsGapExistence (boolean existence) {

    needsUpdate = true;
    betweenComponentsGapExistence = existence;
  }


  /**
   * The model thickness for the gap.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.
   * @param thickness The model thickness for the gap.
   */
  final void setBetweenComponentsGapThicknessModel (int thickness) {

    needsUpdate = true;
    betweenComponentsGapThicknessModel = thickness;
  }


  /**
   * Gets the ratio of the rounding arc to the thickness of the bar.
   * Values need to be between zero and one.  Zero is square.  One is perfectly round.
   * @return The ratio.
   */
  final float getBarRoundingRatio() {
    return barRoundingRatio;
  }


  /**
   * Gets the side from which the lighting affect originates.
   * Use the fields of FancyShape:  TOP, BOTTOM, LEFT, RIGHT, TOPLEFT, BOTTOMRIGHT, and NONE.
   * Added for Siperian.
   * @return int The side.
   */
  final int getComponentsLightSource() {
    return lightSource;
  }


  /**
   * Gets the type of lighting affect.
   * Type refers to whether the lighting should begin and end by component or by graph (meaning
   * all components for each set).
   * For specifying the type use the fields COMPONENT and GRAPH.
   * Added for Siperian.
   * @return int The type.
   */
  final int getComponentsLightType() {
    return lightType;
  }


  /**
   * Gets the warning regions vector for applying to the graph background and the graph components.
   * @return The warning regions vector.
   */
  final Vector getWarningRegions() {
    return warningRegions;
  }


  /**
   * Gets the whether the overpaint of the graph components are clipped or not.
   * @return If true then the components are clipped.
   */
  final boolean getClip() {
    return clip;
  }


  /**
   * Gets the color array for the category coloring.
   * @return The color array.
   */
  final Color[] getComponentsColorsByCat() {
    return componentsColorsByCat;
  }


  /**
   * Gets whether the graph components are colored by category or by set.
   * @return If true, then colored by category.
   */
  final boolean getComponentsColoringByCat() {
    return componentsColoringByCat;
  }


  /**
   * Returns whether bars, lines, and/or dots should have a thin black
   * outline around them.
   * @return boolean If true, then outline.
   */
   final boolean getOutlineComponents() {

    return outlineComponents;
   }


  /**
   * Returns the color of the components outline if it exists.
   * @return Color The color of the outline.
   */
   final Color getOutlineComponentsColor() {

    return outlineComponentsColor;
   }


  /**
   * Returns the type of graph.
   * @return The type of graph.  [LABELSBOTTOM or LABELSLEFT]
   */
  final int getType() {

    return type;
  }


  /**
   * Returns whether the bars, lines or dots are aligned.
   * @return If true, then they are.
   */
  final boolean getAllowComponentAlignment() {

    return allowComponentAlignment;
  }


  /**
   * Returns the graph values for the bars, dots, and lines.
   * @return The graph values, heights or widths respective to the bottom or
   * side of the graph area.
   */
  final int[][] getGraphValues() {

    return graphValues;
  }


  /**
   * Returns the low graph values for bars.
   * @return int[][] The low values.
   */
   final int[][] getBarLowValues() {

    return barLowValues;
   }


  /**
   * Returns the ticks of the x axis.  Actually these mereley are the bounds of
   * the ticks, locations and sizes.
   * @return Bounds for the x axis ticks.
   */
  final Rectangle[] getXTicks() {

    return xTicks;
  }


  /**
   * Returns the ticks of the y axis.  Actually these mereley are the bounds of
   * the ticks, locations and sizes.
   * @return Bounds for the y axis ticks.
   */
  final Rectangle[] getYTicks() {

    return yTicks;
  }


  /**
   * Indicates whether the ticks are aligned between each pair of labels or in
   * the center of the each label of the labels axis.
   * @return int With the value of either Area.CENTERED or Area.BETWEEN
   */
   final int getLabelsAxisTicksAlignment() {

    return ticksAlignment;
   }


  /**
   * Returns this property.
   * @return If true, then they do.
   */
  final boolean getBarsExistence() {

    return barsExistence;
  }


  /**
   * Returns this property.
   * @return The model thickness of the bars.
   */
  final int getBarsThicknessModel() {

    return barsThicknessModel;
  }


  /**
   * Returns this property.
   * @return The colors of the bars.
   */
  final Color[] getBarColors() {

    return barColors;
  }


  /**
   * Returns the amount of the excess space to feed back to bars thickness.
   * @return float The ratio on the total amount of space to feedback.
   */
  final float getBarsExcessSpaceFeedbackRatio() {
    return barsExcessSpaceFeedbackRatio;
  }


  /**
   * Returns how much the bars can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @return float The ratio on the thickness of the bar for overlap.
   */
   final float getBarsWithinCategoryOverlapRatio() {
    return barsWithinCategoryOverlapRatio;
  }


  /**
   * Returns this property.
   * @return If true, then they do.
   */
  final boolean getDotsExistence() {

    return dotsExistence;
  }


  /**
   * Returns this property.
   * @return The model thickness of the dots.
   */
  final int getDotsThicknessModel() {

    return dotsThicknessModel;
  }


  /**
   * Returns this property.
   * @return The colors of the dots.
   */
  final Color[] getDotColors() {

    return dotColors;
  }


  /**
   * Returns the amount of the excess space to feed back to dots thickness.
   * @return float The ratio on the total amount of space to feedback.
   */
  final float getDotsExcessSpaceFeedbackRatio() {
    return dotsExcessSpaceFeedbackRatio;
  }


  /**
   * Returns how much the dots can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @return float The ratio on the thickness of the dot for overlap.
   */
   final float getDotsWithinCategoryOverlapRatio() {
    return dotsWithinCategoryOverlapRatio;
  }


  /**
   * Returns this property.
   * @return If true, then they do.
   */
  final boolean getLinesExistence() {

    return linesExistence;
  }


  /**
   * Returns this property.
   * @return The model thickness of the lines.
   */
  final int getLinesThicknessModel() {

    return linesThicknessModel;
  }


  /**
   * Returns whether the region between the lines and between its baseline
   * should be filled in.
   * @return boolean If true, then the region under/above the lines will be
   * filled.
   */
  final boolean getLinesFillInterior() {

    return linesFillInterior;
  }


  /**
   * Returns the length across the graph length, in the same direction of the
   * meaning of the graph values, that the forms the base for the filled region.
   * @return The base value for the region.
   */
  final int getLinesFillInteriorBaseValue() {

    return linesFillInteriorBaseValue;
  }


  /**
   * Returns this property.
   * @return The colors of the lines.
   */
  final Color[] getLineColors() {

    return lineColors;
  }


  /**
   * Returns the amount of the excess space to feed back to lines thickness.
   * @return float The ratio on the total amount of space to feedback.
   */
  final float getLinesExcessSpaceFeedbackRatio() {
    return linesExcessSpaceFeedbackRatio;
  }


  /**
   * Returns how much the lines can overlap eachother when there are multiple
   * data values per data set and per data category.
   * @return float The ratio on the thickness of the line for overlap.
   */
   final float getLinesWithinCategoryOverlapRatio() {
    return linesWithinCategoryOverlapRatio;
  }


  /**
   * Returns this property.
   * @return If true, then it does.
   */
  final boolean getBetweenComponentsGapExistence() {

    return betweenComponentsGapExistence;
  }


  /**
   * Returns this property.
   * @return The model thickness of this gap.
   */
  final int getBetweenComponentsGapThicknessModel() {

    return betweenComponentsGapThicknessModel;
  }


  /**
   * Returns a value indicating existence of vertical lines on the graph.
   * @return True if vertical lines exist.
   */
  final boolean getVerticalLinesExistence() {

    return verticalLinesExistence;
  }


  /**
   * Returns a value of the vertical lines model thickness.
   * @return The model thickness of this vertical lines.
   */
  final int getVerticalLinesThicknessModel() {

    return verticalLinesThicknessModel;
  }


  /**
   * Returns a value of the vertical lines thickness.
   * @return The thickness of this vertical lines.
   */
  final int getVerticalLinesThickness() {

    updateGraphArea();
    return verticalLinesThickness;
  }


  /**
   * Returns a value of the color of the vertical lines.
   * @return The color of the vertical lines.
   */
  final Color getVerticalLinesColor() {

    return verticalLinesColor;
  }


  /**
   * Returns a value of the style of the vertical lines.
   * Possible values are CONTINUOUS, DASHED, and DOTTED.
   * @return The style of the vertical lines.
   */
  final float[] getVerticalLinesStyle() {

    return verticalLinesStyle;
  }


  /**
   * Returns a value indicating existence of horizontal lines on the graph.
   * @return True if horizontal lines exist.
   */
  final boolean getHorizontalLinesExistence() {

    return horizontalLinesExistence;
  }


  /**
   * Returns a value of the horizontal lines model thickness.
   * @return The model thickness of this horizontal lines.
   */
  final int getHorizontalLinesThicknessModel() {

    return horizontalLinesThicknessModel;
  }


  /**
   * Returns a value of the horizontal lines thickness.
   * @return The thickness of this horizontal lines.
   */
  final int getHorizontalLinesThickness() {

    updateGraphArea();
    return horizontalLinesThickness;
  }


  /**
   * Returns a value of the color of the horizontal lines.
   * @return The color of the horizontal lines.
   */
  final Color getHorizontalLinesColor() {

    return horizontalLinesColor;
  }


  /**
   * Returns a value of the style of the horizontal lines.
   * Possible values are CONTINUOUS, DASHED, and DOTTED.
   * @return The style of the horizontal lines.
   */
  final float[] getHorizontalLinesStyle() {

    return horizontalLinesStyle;
  }


  /**
   *  Returns an array of the indexes, that is a sorted view of the array
   *  graphValues[i][barIndex] i ranges from 0 to graphValues.length and
   *  where barIndex is constant.  This is used for creating stacked bar charts
   *  where the tallest bars must be graphed first.
   *  @param graphValues An int[][] of heights of bars...
   *  @param barIndex The grouping of bars to work on/sort.
   */
  int[] stackedBarSort (int[][] graphValues, int barIndex) {

    boolean[] used = new boolean[graphValues.length];
    for (int i = 0; i < graphValues.length; ++i) {
      used[i] = false;
    }

    int[] sorted = new int[graphValues.length];
    for (int doing = 0; doing < graphValues.length; ++doing) {

      int lowestValue = Integer.MIN_VALUE;
      int lowestIndex = -1;
      for (int i = graphValues.length - 1; i >= 0 ; --i) {
        if (!used[i] && (graphValues[i][barIndex] > lowestValue)) {
          lowestIndex = i;
          lowestValue = graphValues[i][barIndex];
        }
      }
      sorted[doing] = lowestIndex;
      used[lowestIndex] = true;
    }
    return sorted;
  }


  /**
   * Converts an array of graph values -- values indicating how far across
   * a graph a bar should go into an array that the method sortStackedBar can
   * use.  For graphs that plot negative values, the graph values that indicate
   * negative bars, must be converted to be negative values so that they
   * can be sorted properly in an array with values of positive bars.
   */
  int[][] stackedBarConvert (int[][] graphValues, int[][] lowBarValues) {

    int[][] convertedValues =
      new int[graphValues.length][graphValues[0].length];
    for (int i = 0; i < graphValues.length; ++i) {
      for (int j = 0; j < graphValues[0].length; ++j) {
        convertedValues[i][j] = graphValues[i][j] < lowBarValues[i][j] ?
          -graphValues[i][j] :graphValues[i][j];
      }
    }
    return convertedValues;
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
  final void resetGraphAreaModel (boolean reset) {

    needsUpdate = true;
    resetAreaModel (reset);
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getGraphAreaNeedsUpdate() {
    return (needsUpdate || getAreaNeedsUpdate());
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  final void updateGraphArea() {

    if (getGraphAreaNeedsUpdate()) {

      updateArea();
      update();
    }
    needsUpdate = false;
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D  The graphics context for calculations and painting.
   */
  void paintComponent (Graphics2D g2D) {

    updateGraphArea();
    super.paintComponent (g2D);

    Color oldColor = g2D.getColor();
    Stroke oldStroke = g2D.getStroke();

    if (horizontalLinesThickness > 0 && horizontalLinesExistence && horizontalLines.length > 0) {
      g2D.setColor (horizontalLinesColor);
      g2D.setStroke (horizontalLinesStroke);
      for (int i = 0; i < horizontalLines.length; ++i) g2D.draw (horizontalLines[i]);
    }

    if (verticalLinesThickness > 0 && verticalLinesExistence && verticalLines.length > 0) {
      g2D.setColor (verticalLinesColor);
      g2D.setStroke (verticalLinesStroke);
      for (int i = 0; i < verticalLines.length; ++i) g2D.draw (verticalLines[i]);
    }

    g2D.setColor (oldColor);
    g2D.setStroke (oldStroke);
  }


  private void update() {

    horizontalLinesThickness = 0;
    int numHorizontalLines = type == LABELSBOTTOM ?
      yTicks.length - 2 : yTicks.length;
    int availableHeight = getSpaceSize (MIN).height;
    if (horizontalLinesExistence && numHorizontalLines > 0) {
      horizontalLinesThickness =
        applyRatio (horizontalLinesThicknessModel, getRatio (HEIGHT));
      horizontalLinesThickness =
        numHorizontalLines * horizontalLinesThickness <= availableHeight ?
        horizontalLinesThickness : availableHeight / numHorizontalLines;
    }
    else  numHorizontalLines = 0;

    verticalLinesThickness = 0;
    int numVerticalLines = type == LABELSBOTTOM ?
      xTicks.length :  xTicks.length - 2;
    int availableWidth = getSpaceSize (MIN).width;
    if (verticalLinesExistence && numVerticalLines > 0) {
      verticalLinesThickness =
        applyRatio (verticalLinesThicknessModel, getRatio (WIDTH));
      verticalLinesThickness =
        numVerticalLines * verticalLinesThickness <= availableWidth ?
        verticalLinesThickness : availableWidth / numVerticalLines;
    }
    else numVerticalLines = 0;

    if (linesThicknessAssociation) {
      verticalLinesThickness =
        horizontalLinesThickness < verticalLinesThickness &&
        horizontalLinesExistence ?
        horizontalLinesThickness : verticalLinesThickness;
      horizontalLinesThickness =
        verticalLinesThickness < horizontalLinesThickness &&
        verticalLinesExistence ?
        verticalLinesThickness : horizontalLinesThickness;
    }

    horizontalLinesStroke =
      new BasicStroke ((float)horizontalLinesThickness,
        BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
        horizontalLinesStyle, 0.0f);
    verticalLinesStroke =
      new BasicStroke ((float)verticalLinesThickness,
        BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
        verticalLinesStyle, 0.0f);

    int x1,x2,y1,y2;

    horizontalLines = new Line2D.Double[numHorizontalLines];
    int horizontalLinesOffset = 0;
    if (horizontalLinesExistence && yTicks.length > 0) {
      horizontalLinesOffset = +yTicks[0].height / 2;
    }
    x1 = getSpaceSizeLocation (MIN).x;
    x2 = x1 + getSpaceSize (MIN).width;
    int offsetI = type == LABELSBOTTOM ? 1 : 0;
    for (int i = 0; i < numHorizontalLines; ++i) {
      y1 = yTicks[i + offsetI].y + horizontalLinesOffset;
      y2 = y1;
      horizontalLines[i] =
        new Line2D.Double ((double)x1, (double)y1, (double)x2, (double)y2);
    }

    verticalLines = new Line2D.Double[numVerticalLines];
    int verticalLinesOffset = 0;
    if (verticalLinesExistence && xTicks.length > 0) {
      verticalLinesOffset = xTicks[0].width / 2;
    }

    y1 = getSpaceSizeLocation (MIN).y;
    y2 = y1 + getSpaceSize (MIN).height;
    offsetI = type == LABELSBOTTOM ? 0 : 1;
    for (int i = 0; i < numVerticalLines; ++i) {
      x1 = xTicks[i + offsetI].x + verticalLinesOffset;
      x2 = x1;
      verticalLines[i] = new Line2D.Double ((double)x1, (double)y1, (double)x2, (double)y2);
    }
  }
}