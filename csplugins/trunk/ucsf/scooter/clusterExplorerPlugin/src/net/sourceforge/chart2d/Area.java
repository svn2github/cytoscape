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


import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * An Advanced Bordered Area.  Allows auto and manual resizing and
 * auto and manual positioning. Contains many public static variables for use
 * when extending this class.
 * <p><b>Basic Sizing and Colors:</b><br>
 * The typical bordered area class will simply paint a border of a specified
 * thickness within an area, where the inner edges of the area touch the outer
 * edges of the border.  This class can do much more.  Each border can be set
 * to a different thickness and color.  This introduces the problem of handling
 * the corners of the borders.  If the left border has color green, and the top
 * border has color red, which color is the top left corner?  Each corner can
 * be associated with any adjacent border.
 * <p><b>Inner Space And Gaps:</b><br>
 * Sometimes, some space between the thing bordered and the border is desired.
 * That is, if bordering some text, then having some space between the
 * text and the border may be desirable.  The thickness of the gap between the
 * the left, right, top, and bottom borders may each be individually set.
 * The location and size of this inner space may be obtained directly.
 * <p><b>Resizing And Growing Borders And Gaps:</b><br>
 * The borders and gaps can automatically grow when the size is changed.  In
 * order to maintain the look of zooming in on something, the borders and gaps
 * can grow with the resizing of the area.  For example, if the area grows in
 * the horizontal direction, then the left and right borders and gaps will
 * grow respective of the amount of change in the area width, depending
 * on this classes settings.  If keeping any of the borders equal to any of the
 * others is desireable, such as a "grow only when all grow" policy, then
 * any border may be associated with any other border.  The sizes of the
 * associated borders will be equal to the least of them.
 * <p><b>More Basics:</b><br>
 * This area can have a background color, or no background at all. And
 * each border and gap can be set to not exist, individually.
 * <p><b>Locating:  Automatically and Manually:</b><br>
 * Sometimes the size of component to be bordered cannot be known until after
 * the borders and gaps have been calculated (relevant when using growing
 * borders and gaps).   This class allows the available space, after subtracting
 * borders and gaps, to obtained.  The size of the component to be bordered can
 * then be calculated, making sure it fits within this space.  The size of the
 * component can be passed to this class, this class will place the gaps and
 * borderes around the component for a (near) perfect fit.  This means that
 * where to place this bordered area within the maximum area is an open
 * question.  This class allows options of automatic centering (both
 * horizontally and vertically, justifying to the left, right, top, and/or
 * bottom edges of the maximum area, and doing nothing such that the area can
 * be manually set, and any combination thereof.
 * <p><b>Examples:</b><br>
 * <ul>
 *   <li>No Growing And Border Encloses Maximum Area:<br>
 *     <code>setAutoSizes (true, true);</code>
 *   <li>Growing And Border Encloses Maximum Area:<br>
 *     <code>setAutoSizes (false, true);</code>
 *   <li>No Growing, Border Encloses Minimum Area, And Centered:<br>
 *     <code>setAutoSizes (true, false);<br>
 *      setAutoJustifys (true, true);<br>
 *      setJustifications (CENTER, CENTER);</code>
 *   <li>Growing, Border Encloses Minimum Area, And Manually Located:<br>
 *     <code>setAutoSizes (false, false);<br>
 *      setAutoJustifys (false, false);</code>
 * </ul>
 * <p><b>Details Of Growing:</b><br>
 *   Growing is based on "model" sizes.  By default, everything has a
 *   model size (the area, the borders, and the gaps).  The defaults
 *   currently are the first maximum size setting for the area,
 *   2 (for all borders), and 2 (for all gaps).  Growing
 *   is accomplished by dividing the maximum area size by the model area size
 *   and applying this ratio the border and gap thicknesses.
 *   If growing in the horizontal direction, the width sizes are used.  If
 *   growing in the vertical direction, the height sizes are used.  If growing
 *   in both directions, then both are calculated and the lesser is used.
 *   However, all of these values can be changed.  Ideally, one would set
 *   the model area size to your normal viewing size, and all of the
 *   inner components model sizes (borders and thicknesses) to the best sizes
 *   at this model area size.  However, just setting the model area size to the
 *   normal viewing size of the area most likely will be sufficient.  Generally,
 *   the initial size is the normal size, do set the model size to the initial
 *   size, use the resetAreaModel method before setting the maximum size.  When
 *   you set the maximum size, the area model size will automatically reset
 *   itself to this size.
 * <p><b>Notes:</b><br>
 * <ul>
 *  <li>This class assumes no null values.  Pass zero based values intead. For
 *  example, <code>new Point()</code> instead of <code>null</code>.
 *  <li>Both Growing and shrinking are supported, and are the same.  When
 *  is written, in these comments, gowing and shrinking is meant.
 * </ul>
 */
 class Area {


  /**
   *  Indicates the maximum.
   */
  static final int MAX = 0;
  /**
   *  Indicates the model or maximum model.
   */
  static final int MAXMODEL = 1;
  /**
   *  Indicates the minimum.
   */
  static final int MIN = 2;
  /**
   *  Indicates the width.
   */
  static final int WIDTH = 0;
  /**
   *  Indicates the height.
   */
  static final int HEIGHT = 1;
  /**
   * Indicates the lesser.
   */
  static final int LESSER = 2;
  /**
   * Indicates the left.
   */
  static final int LEFT = 0;
  /**
   * Indicates the right.
   */
  static final int RIGHT = 1;
  /**
   * Indicates the top.
   */
  static final int TOP = 2;
  /**
   * Indicates the bottom.
   */
  static final int BOTTOM = 3;
  /**
   * Indicates the center.
   */
  static final int CENTER = 4;
  /**
   * Indicates the left right.
   */
  static final int LEFTRIGHT = 0;
  /**
   * Indicates the left top.
   */
  static final int LEFTTOP = 1;
  /**
   * Indicates the right bottom.
   */
  static final int LEFTBOTTOM = 2;
  /**
   * Indicates the right top.
   */
  static final int RIGHTTOP = 3;
  /**
   * Indicates the right bottom.
   */
  static final int RIGHTBOTTOM = 4;
  /**
   * Indicates the top bottom.
   */
  static final int TOPBOTTOM = 5;
  /**
   * Indicates the horizontal.
   */
  static final int HORIZONTAL = 0;
  /**
   * Indicates the vertical.
   */
  static final int VERTICAL = 1;
  /**
   * Indicates the centered.
   */
  static final int CENTERED = 0;
  /**
   * Indicates the betweem.
   */
  static final int BETWEEN = 1;
  /**
   * Indicates the labels bottom.
   */
  static final int LABELSBOTTOM = 0;
  /**
   * Indicates the labels left.
   */
  static final int LABELSLEFT = 1;
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
   * Indicates none.
   */
  static int NONE = 6;


  private Point[] sizeLocations;
  private Dimension[] sizes;
  private boolean[] autoSizes;

  private Point[] spaceSizeLocations;
  private Dimension[] spaceSizes;
  private boolean[] autoJustifys;
  private int[] justifications;

  private boolean backgroundExistence;
  private Rectangle background;
  private Color backgroundColor;

  private float[] ratios;
  private boolean[] customizeRatios;

  private boolean[] borderExistences;
  private Rectangle[] borders;
  private int[] borderThicknessModels;
  private int[] borderThicknesses;
  private int[] borderCornerAssociations;
  private boolean[] borderAssociations;
  private Color[] borderColors;

  private boolean[] gapExistences;
  private int[] gapThicknessModels;
  private int[] gapThicknesses;
  private boolean[] gapAssociations;

  private boolean lockRatios;
  private int lightSource;
  private Paint backgroundPaint;

  private boolean resetAreaModel;
  private boolean unsetAreaModelOnUpdate;
  private boolean needsUpdate;


  /**
   * Creates a new Area.
   */
  Area() {

    createObjects();

    setAutoSizes (true, true);
    setAutoJustifys (true, true);
    setJustifications (CENTER, CENTER);

    setBackgroundExistence (true);
    setBackgroundColor (Color.white);

    setBorderExistences (true, true, true, true);
    setBorderThicknessModels (2, 2, 2, 2);
    setBorderAssociations (true, true, true, true, true, true);
    setBorderCornerAssociations (LEFT, BOTTOM, TOP, RIGHT);
    setBorderColors (Color.black, Color.black, Color.black, Color.black);

    setGapExistences (true, true, true, true);
    setGapThicknessModels (5, 5, 5, 5);

    setLockRatios (true);
    setCustomRatio (WIDTH, false, 0f);
    setCustomRatio (HEIGHT, false, 0f);
    setCustomRatio (LESSER, false, 0f);

    setLightSource (NONE);

    resetAreaModel (true);

    unsetAreaModelOnUpdate = false;
    needsUpdate = true;
  }


  /**
   * Sets the source of light for gradient paint of the background.
   * Possible values for the source parameter are:  LEFT, RIGHT, TOP, BOTTOM, and NONE.
   * @param source Which side the light is coming from.
   */
  void setLightSource (int source) {
    lightSource = source;
    needsUpdate = true;
  }


  /**
   * Sets the location of a size/area.
   * @param which Which area to relocate.  MAX is the only possible accepted
   * value.
   * @param location The new point of the top left of the area.  The outer
   * edges of the left and top borders will touch this.  Must not be null.
   */
  final void setSizeLocation (int which, Point location) {

    needsUpdate = true;
    sizeLocations[MAX] = location;
  }


  /**
   * Sets the location of an internal space area of a size area.
   * @param which Which space area to relocate.  MIN is the only possible
   * value.  If horizontal justification is true, then the x value will be
   * ignored.  If vertical justification is true, then the y value will be
   * ignored.
   * @param location The new point of the top left of the space area.  The
   * inner edges of the top and left gaps will touch this.  Must not be null.
   */
  final void setSpaceSizeLocation (int which, Point location) {

    needsUpdate = true;
    int x = autoJustifys[HORIZONTAL] ? spaceSizeLocations[MIN].x : location.x;
    int y = autoJustifys[VERTICAL] ? spaceSizeLocations[MIN].y : location.y;
    spaceSizeLocations[MIN] = new Point (x, y);
  }


  /**
   * Adjusts settings to enable/disable auto justification of a minimum area
   * that is less than a maximum area.
   * @param horizontal If true, then horizontal justification is
   * enabled.
   * @param vertical If true, then vertical justification is enabled.
   */
  final void setAutoJustifys (boolean horizontal, boolean vertical) {

    needsUpdate = true;
    autoJustifys[HORIZONTAL] = horizontal;
    autoJustifys[VERTICAL] = vertical;
  }


  /**
   * Adjusts the actual justifications of minimum areas that are less than
   * maximum areas.  Justifications effect the locations of minimum areas.
   * Either are only respected when their auto justification is enabled.
   * For example, if horizontal justify is center, then the minimum area will
   * be horizontally centered within the maximum area.
   * @param horizontal Sets the horizontal justification for the minimum
   * area.  Possible values are LEFT, RIGHT and CENTER.
   * @param vertical Sets the vertical justification for the minimum
   * area.  Possible values are LEFT, RIGHT and CENTER.
   */
  final void setJustifications (int horizontal, int vertical) {

    needsUpdate = true;
    justifications[HORIZONTAL] = horizontal;
    justifications[VERTICAL] = vertical;
  }


  /**
   * Adjusts the settings that allow automatic (or default) sizing.  Both are
   * on the maximum size which is required, and must be set to be useful.
   * @param maxModel If true, then the maximum model size will always
   * be equal to the maximum size.  This disables growing because it keeps the
   * resizing ratios at 1.  If false, then maximum model is constant; so if the
   * maximum size changes then the ratios will adjust accordingly.
   * @param min If true, then the minimum size will be kept equal to
   * the maximum size.  This means that the borders' outer edges will touch
   * the maximum size.
   */
  final void setAutoSizes (boolean maxModel, boolean min) {

    needsUpdate = true;
    autoSizes[MAXMODEL] = maxModel;
    autoSizes[MIN] = min;
  }


  /**
   * Makes such that the widh and the heighh ratios are the same as the
   * lesser ratio.  If you want components to grow only if all of them grow,
   * then lock ratios, true, will cause this.
   * @param lock True causes all the ratios to equal the lesser ratio.
   */
  final void setLockRatios (boolean lock) {

    lockRatios = lock;
  }


  /**
   * Specifies whether to customize a particular ratio, overriding the
   * calculation of it.
   * @param which Which ratio to customize.
   * @param customize Whether to customize the ratio.
   * @param ratio The custom ratio.
   */
  final void setCustomRatio (int which, boolean customize, float ratio) {

    needsUpdate = true;
    customizeRatios[which] = customize;
    ratios[which] = ratio;
  }



  /**
   * Sets the sizes of the areas.  If setting the maximum size and if
   * resetAreaModel was called previously, then the maximum model size will
   * be made equal to the maximum size; this ensures that all the default sizes
   * of borders and gaps will be used at this maximum size; at this maximum size
   * the resizing ratio will be 1.
   * @param which Which size to change.  Possible values are MAX and
   * MAXMODEL.
   * @param size The new size.  Must not be null.
   */
  final void setSize (int which, Dimension size) {

    needsUpdate = true;
    sizes[which] = size;
    if (resetAreaModel && !unsetAreaModelOnUpdate && which == MAX) {
      unsetAreaModelOnUpdate = true;
      sizes[MAXMODEL] = size;
    }
  }


  /**
   * Sets the size of the space; that is the area less the borders and gaps.
   * @param which Which space size to change.  Possible values are MIN.
   * @param size The new size.  Must not be null.
   */
  final void setSpaceSize (int which, Dimension size) {

    needsUpdate = true;
    spaceSizes[which] = size;
  }


  /**
   * Adjusts whether there exists a background or not.
   * @param existence If true, then there is.
   */
  final void setBackgroundExistence (boolean existence) {

    needsUpdate = true;
    backgroundExistence = existence;
  }


  /**
   * Adjusts the color of the background.  Doesn't change the background's
   * existence.
   * @param color The color of the background.
   */
  final void setBackgroundColor (Color color) {

    needsUpdate = true;
    backgroundColor = color;
  }


  /**
   * Adjusts whether there exists borders, each individually.  If a border does
   * not exist, then its is not used in calculations or in painting.
   * @param left If true, then the left border does exist.
   * @param right If true, then the right border does exist.
   * @param top If true, then the top border does exist.
   * @param bottom If true, then the bottom border does exist.
   */
  final void setBorderExistences
    (boolean left, boolean right, boolean top, boolean bottom) {

    needsUpdate = true;
    borderExistences[LEFT] = left;
    borderExistences[RIGHT] = right;
    borderExistences[TOP] = top;
    borderExistences[BOTTOM] = bottom;
  }


  /**
   * Adjusts whether there exists borders.  If borders do
   * not exist, then they are not used in calculations or in painting.
   * @param existences If true, then the borders do not exist.
   */
  final void setBorderExistence (boolean existences) {

    needsUpdate = true;
    borderExistences[LEFT] = existences;
    borderExistences[RIGHT] = existences;
    borderExistences[TOP] = existences;
    borderExistences[BOTTOM] = existences;
  }


  /**
   * Adjusts the thickness of the border models.  These values will be
   * applied to a ratio to determine their final thicknesses.  The ratio is the
   * maximum size divided by the maximum model size.  More information is in the
   * introductory notes for this class.
   * @param left The model thickness for the left border.
   * @param right The model thickness for the right border.
   * @param top The model thickness for the top border.
   * @param bottom The model thickness for the bottom border.
   */
  final void setBorderThicknessModels
    (int left, int right, int top, int bottom) {

    needsUpdate = true;
    borderThicknessModels[LEFT] = left;
    borderThicknessModels[RIGHT] = right;
    borderThicknessModels[TOP] = top;
    borderThicknessModels[BOTTOM] = bottom;
  }


  /**
   * Adjusts the thickness of the border models.  These values will be
   * applied to a ratio to determine their final thicknesses.  The ratio is the
   * maximum size divided by the maximum model size.  More information is in the
   * introductory notes for this class.
   * @param thickness The model thickness for the border.
   */
  final void setBorderThicknessModel (int thickness) {

    needsUpdate = true;
    borderThicknessModels[LEFT] = thickness;
    borderThicknessModels[RIGHT] = thickness;
    borderThicknessModels[TOP] = thickness;
    borderThicknessModels[BOTTOM] = thickness;
  }


  /**
   * Associates each corner with one border.  The corner will take on the same
   * color as the border associated with it.
   * @param leftTop The border to associate with the leftTop corner.
   * Possible values are LEFT and TOP.
   * @param rightTop The border to associate with the rightTop corner.
   * Possible values are RIGHT and TOP.
   * @param  leftBottomThe border to associate with the leftBottom corner.
   * Possible values are LEFT and BOTTOM.
   * @param rightBottom The border to associate with the rightBottom corner.
   * Possible values are RIGHT and BOTTOM.
   */
  final void setBorderCornerAssociations
    (int leftTop, int leftBottom, int rightTop, int rightBottom) {

    needsUpdate = true;
      borderCornerAssociations[LEFTTOP] = leftTop;
      borderCornerAssociations[RIGHTTOP] = rightTop;
      borderCornerAssociations[LEFTBOTTOM] = leftBottom;
      borderCornerAssociations[RIGHTBOTTOM] = rightBottom;
  }


  /**
   * Associates border thicknesses with other border thicknesses.  Each possible
   * association between the four borders can be represented by setting six
   * booleans.  When a border is associated with another border, then their
   * thickness is equal to the lesser thickness between the two.
   * @param leftRight If true, then associates the left and right
   * borders.
   * @param leftTop If true, then associates the left and top borders.
   * @param leftBottom If true, then associates the left and bottom
   * borders.
   * @param rightTop If true, then associates the right and top borders.
   * @param rightBottom If true, then associates the right and bottom
   * borders.
   * @param topBottom If true, then associates the top and bottom
   * borders.
   */
  final void setBorderAssociations
    (boolean leftRight, boolean leftTop, boolean leftBottom,
    boolean rightTop, boolean rightBottom, boolean topBottom) {

    needsUpdate = true;
    borderAssociations[LEFTRIGHT] = leftRight;
    borderAssociations[LEFTTOP] = leftTop;
    borderAssociations[LEFTBOTTOM] = leftBottom;
    borderAssociations[RIGHTTOP] = rightTop;
    borderAssociations[RIGHTBOTTOM] = rightBottom;
    borderAssociations[TOPBOTTOM] = topBottom;
  }


  /**
   * Sets the color of each border, individually.
   * @param left The color of the left border.
   * @param right The color of the right border.
   * @param top The color of the top border.
   * @param bottom The color of the bottom border.
   */
  final void setBorderColors
    (Color left, Color right, Color top, Color bottom) {

    needsUpdate = true;
    borderColors[LEFT] = left;
    borderColors[RIGHT] = right;
    borderColors[TOP] = top;
    borderColors[BOTTOM] = bottom;
  }



  /**
   * Sets the color of the border (each border).
   * @param color The color of the border.
   */
  final void setBorderColor (Color color) {

    needsUpdate = true;
    borderColors[LEFT] = color;
    borderColors[RIGHT] = color;
    borderColors[TOP] = color;
    borderColors[BOTTOM] = color;
  }



  /**
   * Adjusts whether there exists gaps, each individually.  If a gap does
   * not exist, then its is not used in calculations.
   * @param left If true, then the left gap does exist.
   * @param right If true, then the right gap does exist.
   * @param top If true, then the top gap does exist.
   * @param bottom If true, then the bottom gap does exist.
   */
  final void setGapExistences
    (boolean left, boolean right, boolean top, boolean bottom) {

    needsUpdate = true;
    gapExistences[LEFT] = left;
    gapExistences[RIGHT] = right;
    gapExistences[TOP] = top;
    gapExistences[BOTTOM] = bottom;
  }


  /**
   * Adjusts whether there exists gaps.  If a gap does
   * not exist, then its is not used in calculations.
   * This is equivalent to calling
   * setGapExistences (boolean, boolean, boolean, boolean).
   * @param existence If true, then the gap exists.
   */
  final void setGapExistence(boolean existence) {

    setGapExistences (existence, existence, existence, existence);
  }


  /**
   * Adjusts the thickness of the gap models.  These values will be
   * applied to a ratio to determine their final thicknesses.  The ratio is the
   * maximum size divided by the maximum model size.  More information is in the
   * introductory notes for this class.
   * @param left The model thickness for the left gap.
   * @param right The model thickness for the right gap.
   * @param top The model thickness for the top gap.
   * @param bottom The model thickness for the bottom gap.
   */
  final void setGapThicknessModels (int left, int right, int top, int bottom) {

    needsUpdate = true;
    gapThicknessModels[LEFT] = left;
    gapThicknessModels[RIGHT] = right;
    gapThicknessModels[TOP] = top;
    gapThicknessModels[BOTTOM] = bottom;
  }


  /**
   * Adjusts the thickness of the gap models.  These values will be
   * applied to a ratio to determine their final thicknesses.  The ratio is the
   * maximum size divided by the maximum model size.  More information is in the
   * introductory notes for this class.
   * @param thickness The model thickness for the gap.
   */
  final void setGapThicknessModel (int thickness) {

    needsUpdate = true;
    gapThicknessModels[LEFT] = thickness;
    gapThicknessModels[RIGHT] = thickness;
    gapThicknessModels[TOP] = thickness;
    gapThicknessModels[BOTTOM] = thickness;
  }


  /**
   * Associates gap thicknesses with other gap thicknesses.  Each possible
   * association between the four gaps can be represented by setting six
   * booleans.  When a gap is associated with another gap, then their
   * thickness is equal to the lesser thickness between the two.
   * @param leftRight If true, then associates the left and right gaps.
   * @param leftTop If true, then associates the left and top gaps.
   * @param leftBottom If true, then associates the left and bottom
   * gaps.
   * @param rightTop If true, then associates the right and top gaps.
   * @param rightBottom If true, then associates the right and bottom
   * gaps.
   * @param topBottom If true, then associates the top and bottom gaps.
   */
  final void setGapAssociations
    (boolean leftRight, boolean leftTop, boolean leftBottom,
    boolean rightTop, boolean rightBottom, boolean topBottom) {

    needsUpdate = true;
    gapAssociations[LEFTRIGHT] = leftRight;
    gapAssociations[LEFTTOP] = leftTop;
    gapAssociations[LEFTBOTTOM] = leftBottom;
    gapAssociations[RIGHTTOP] = rightTop;
    gapAssociations[RIGHTBOTTOM] = rightBottom;
    gapAssociations[TOPBOTTOM] = topBottom;
  }


  /**
   * Gets the source of light for gradient paint of the background.
   * Possible values for the source parameter are:  LEFT, RIGHT, TOP, BOTTOM, and NONE.
   * @return Which side the light is coming from.
   */
  int getLightSource() {
    return lightSource;
  }


  /**
   * Returns the space area location.  The space area is the area within the
   * bordered area.  It is the area (max or min) less borderss and gaps.
   * Updates everything before returning the value.
   * @param which Which space area location to return.  Posible values are MAX
   * and MIN.
   * @return The location of the space area.
   */
  final Point getSpaceSizeLocation (int which) {

    updateArea();
    return spaceSizeLocations[which];
  }


  /**
   * Returns the auto sizing properties.  Both the max model area size and the
   * min area size can be auto sized.  Autosizing is based on the max area size.
   * If autosizing, then the area is equal to the max area size.
   * @param which Which auto size property to return.  Possible values are
   * MAXMODEL and MIN.
   * @return True, if this area is auto sizing.
   */
  final boolean getAutoSize (int which) {

    return autoSizes[which];
  }


  /**
   * Returns the size of the area.  The area size is the dimension enclosing the
   * border.
   * Updates everything before returning the value.
   * @param which Which size to return.  Possible values are MAX, MAXMODEL, and
   * MIN.
   * @return The size of the area.
   */
  final Dimension getSize (int which) {

    updateArea();
    return sizes[which];
  }


  /**
   * Returns the location of the area.  The location is the top left corner of
   * the area enclosing the border.
   * Updates everything before returning the value.
   * @param which Which size location to return.  Possible values are MAX and MIN.
   * @return The loation of the area.
   */
  final Point getSizeLocation (int which) {

    updateArea();
    return sizeLocations[which];
  }


  /**
   * Returns the size of the space area.  The size of the space area is the
   * area size less the borders and gaps.
   * Updates everything before returning the value.
   * @param which Which space area size to return.  Possible values are MAX and
   * MIN.
   * @return The size of the space area.
   */
  final Dimension getSpaceSize (int which) {

    updateArea();
    return spaceSizes[which];
  }


  /**
   * Returns the specified ratio.  Ratios area based on maximum area size
   * divided by model area size.  The width ratio uses the area widths.  The
   * height ratio uses the area heights.  And the lesser ratio returns the
   * lesser of these two.
   * Updates everything before returning the value.
   * @param which Which ratio to return.  Possible values are WIDTH, HEIGHT, and
   * LESSER.
   * @return The ratio; value is between 0 and 1.
   */
  final float getRatio (int which) {
    updateArea();
    return ratios[which];
  }


  /**
   * Returns the current justifications for the minimum area.  Justifications
   * are used only when justifications are enabled.  Justifications auto
   * locate the minimum area within the maximum area.  This is only used when
   * auto minimum area sizing is disabled.
   * @param which Which justification to return.  Possible values are HORIZONTAL
   * and VERTICAL.
   * @return The justification.  Possible values for a horizontal
   * justification are LEFT, RIGHT, and CENTER; values for a vertical
   * justification are TOP, BOTTOM, and CENTER.
   */
  final int getJustifications (int which) {

    return justifications[which];
  }


  /**
   * Returns whether there exists a background or not.
   * @return If there is, then true.
   */
  final boolean getBackgroundExistence() {

    return backgroundExistence;
  }


  /**
   * Returns the color of the background.
   * @return The color of the background.
   */
  final Color getBackgroundColor() {

    return backgroundColor;
  }


  /**
   * Gets whether there exists borders.  All must exist for this to return true.
   * @return If true, then the borders do not exist.
   */
  final boolean getBorderExistence() {

    return (borderExistences[LEFT] && borderExistences[RIGHT] &&
    borderExistences[TOP] && borderExistences[BOTTOM]);
  }


  final boolean getBorderExistence (int which) {

    return (borderExistences[which]);
  }


  /**
   * Returns the color of the border (each border).
   * @return The color of the border.
   */
  final Color getBorderColor() {

    return borderColors[LEFT];
  }


  /**
   * Returns the color of the border (specific border).
   * @param which Which border you want the color of (BOTTOM, TOP, LEFT, RIGHT).
   * @return The color of the chosen border.
   */
  final Color getBorderColor (int which) {

    return borderColors[which];
  }


  /**
   * Gets the thickness of the gap model.  These values will be
   * applied to a ratio to determine their final thicknesses.  The ratio is the
   * maximum size divided by the maximum model size.  More information is in the
   * introductory notes for this class.
   * @return The model thickness for the gap.
   */
  final int getGapThicknessModel() {

    return gapThicknessModels[LEFT];
  }


  /**
   * Gets whether there exists gaps.  All must exist for this to return true.
   * @return If true, then the gaps do not exist.
   */
  final boolean getGapExistence() {

    return (gapExistences[LEFT] && gapExistences[RIGHT] &&
    gapExistences[TOP] && gapExistences[BOTTOM]);
  }


  final boolean getGapExistence (int which) {
    return (gapExistences[which]);
  }


  /**
   * Returns the thickness of one of the gaps.  Should be used only when all
   * gaps have same thickness model.
   * @return The thickness of the gap.
   */
  final int getGapThickness() {
    updateArea();
    return gapThicknesses[LEFT];
  }


  /**
   * Returns the thickness of one of the gaps.  Should be used only when all
   * gaps have same thickness model.
   * @param which Which gap to return the thickness of.
   * @return The thickness of the gap.
   */
  final int getGapThickness (int which) {
    updateArea();
    return gapThicknesses[which];
  }


  /**
   * Gets the thickness of the border model.  These values will be
   * applied to a ratio to determine their final thicknesses.  The ratio is the
   * maximum size divided by the maximum model size.  More information is in the
   * introductory notes for this class.
   * @return The model thickness for the border.
   */
  final int getBorderThicknessModel() {

    return borderThicknessModels[LEFT];
  }


  /**
   * Gets the thickness of a border model.  These values will be
   * applied to a ratio to determine their final thicknesses.  The ratio is the
   * maximum size divided by the maximum model size.  More information is in the
   * introductory notes for this class.
   * @return The model thickness for this border.
   */
  final int getBorderThicknessModel(int which) {
    return borderThicknessModels[which];
  }


  /**
   * Returns the thickness of a border.
   * @return The thickness of the particular border.
   */
  final int getBorderThickness (int which) {
    updateArea();
    return borderThicknesses[which];
  }


  /**
   * Returns the thickness of a border + the gap.
   * @return The thickness of the border + the gap.
   */
  final int getOffsetThickness() {
    updateArea();
    return borderThicknesses[LEFT] + gapThicknesses[LEFT];
  }


  /**
   * Returns the thickness of the border.
   * @return The thickness of the border.
   */
  final int getBorderThickness() {
    updateArea();
    return borderThicknesses[TOP];
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getAreaNeedsUpdate () {

    return needsUpdate;
  }


  /**
   * Applies the given ratio to the given integer.
   * @param model The integer.
   * @param ratio The ratio.
   * @return The ratio multiplied by the integer.
   */
  final int applyRatio (int model, float ratio) {

    int applied = (int)(ratio * model);
    applied = applied < 1 && model > 0 ? 1 : applied;
    return applied;
  }


  /**
   * Returns whether the max model will be reset, in the next max sizing.
   * @return True if the max model size needs to be reset.
   */
  final boolean getResetAreaModel () {

    return resetAreaModel;
  }


  /**
   * Updates this area.  This area maintains many variables.  Calling this
   * methods assures they are all updates with respect to eachother.
   */
  final void updateArea () {

    if (getAreaNeedsUpdate()) {
      update();
    }
    needsUpdate = false;
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
  final void resetAreaModel (boolean reset) {

    if (reset != resetAreaModel) {
      needsUpdate = true;
      resetAreaModel = reset;
      unsetAreaModelOnUpdate = false;
    }
  }


  /**
   * Paints this bordered area.  Paints borders and background if they exist.
   * Updates everything before painting.
   * @param g2D The graphics context for calculations and painting.
   */
  void paintComponent (Graphics2D g2D) {

    updateArea();
    Paint oldPaint = g2D.getPaint();
    g2D.setPaint (backgroundPaint);
    g2D.fill (background);
    g2D.setPaint (borderColors[LEFT]);
    g2D.fill (borders[LEFT]);
    g2D.setPaint (borderColors[RIGHT]);
    g2D.fill (borders[RIGHT]);
    g2D.setPaint (borderColors[TOP]);
    g2D.fill (borders[TOP]);
    g2D.setPaint (borderColors[BOTTOM]);
    g2D.fill (borders[BOTTOM]);
    g2D.setPaint (oldPaint);
  }


  private void createObjects() {

    sizeLocations = new Point[3];
    sizeLocations[MAX] = new Point();
    sizeLocations[MIN] = new Point();

    spaceSizeLocations = new Point[3];
    spaceSizeLocations[MAX] = new Point();
    spaceSizeLocations[MIN] = new Point();

    autoSizes = new boolean[3];
    sizes = new Dimension[3];
    sizes[MAX] = new Dimension();
    sizes[MAXMODEL] = new Dimension();
    sizes[MIN] = new Dimension();

    spaceSizes = new Dimension[3];
    spaceSizes[MAX] = new Dimension();
    spaceSizes[MIN] = new Dimension();

    autoJustifys = new boolean[2];
    justifications = new int[2];

    ratios = new float[3];
    customizeRatios = new boolean[3];

    background = new Rectangle();

    borderExistences = new boolean[4];
    borders = new Rectangle[4];
    borders[LEFT] = new Rectangle();
    borders[RIGHT] = new Rectangle();
    borders[TOP] = new Rectangle();
    borders[BOTTOM] = new Rectangle();
    borderThicknesses = new int[4];
    borderThicknessModels = new int[4];
    borderCornerAssociations = new int[6];
    borderAssociations = new boolean[6];
    borderColors = new Color[6];

    gapExistences = new boolean[4];
    gapThicknesses = new int[4];
    gapThicknessModels = new int[4];
    gapAssociations = new boolean[6];
  }


  private void update() {

    if (unsetAreaModelOnUpdate) resetAreaModel = false;
    updateMaxSizeObjects();
    updateRatios();
    updateBorderThicknesses();
    updateGapThicknesses();
    updateMaxSpaceSizeObjects();
    updateMinObjects();
    updateBackground();
    updateBorderRectangles();
    updateBackgroundPaint();
  }


  private void updateMaxSizeObjects() {

    sizes[MAXMODEL] = autoSizes[MAXMODEL] ? sizes[MAX] : sizes[MAXMODEL];
  }


  private void updateRatios() {

    if (!customizeRatios[WIDTH]) {
      ratios[WIDTH] = sizes[MAXMODEL].width == 0 ? 0f :
        (float)sizes[MAX].width / sizes[MAXMODEL].width;
    }
    if (!customizeRatios[HEIGHT]) {
      ratios[HEIGHT] = sizes[MAXMODEL].height == 0 ? 0f :
        (float)sizes[MAX].height / sizes[MAXMODEL].height;
    }
    if (!customizeRatios[LESSER]) {
      ratios[LESSER] =
        ratios[WIDTH] < ratios[HEIGHT] ? ratios[WIDTH] : ratios[HEIGHT];
    }
    if (lockRatios) {
      ratios[WIDTH] = ratios[LESSER];
      ratios[HEIGHT] = ratios[LESSER];
    }
  }


  private void updateBorderThicknesses() {

    if (borderExistences[LEFT]) {
      if ((borderAssociations[LEFTTOP] && borderExistences[TOP]) ||
        (borderAssociations[LEFTBOTTOM] && borderExistences[BOTTOM])) {
        borderThicknesses[LEFT] =
          applyRatio (borderThicknessModels[LEFT], ratios[LESSER]);
      }
      else {
        borderThicknesses[LEFT] =
          applyRatio (borderThicknessModels[LEFT], ratios[WIDTH]);
      }
    }
    else borderThicknesses[LEFT] = 0;

    if (borderExistences[RIGHT]) {
      if ((borderAssociations[RIGHTTOP] && borderExistences[TOP]) ||
        (borderAssociations[RIGHTBOTTOM] && borderExistences[BOTTOM])) {
        borderThicknesses[RIGHT] =
          applyRatio (borderThicknessModels[RIGHT], ratios[LESSER]);
      }
      else {
        borderThicknesses[RIGHT] =
          applyRatio (borderThicknessModels[RIGHT], ratios[WIDTH]);
      }
    }
    else borderThicknesses[RIGHT] = 0;

    if (borderExistences[TOP]) {
      if ((borderAssociations[LEFTTOP] && borderExistences[LEFT]) ||
        (borderAssociations[RIGHTTOP] && borderExistences[RIGHT])) {
        borderThicknesses[TOP] =
          applyRatio (borderThicknessModels[TOP], ratios[LESSER]);
      }
      else {
        borderThicknesses[TOP] =
          applyRatio (borderThicknessModels[TOP], ratios[HEIGHT]);
      }
    }
    else borderThicknesses[TOP] = 0;

    if (borderExistences[BOTTOM]) {
      if ((borderAssociations[LEFTBOTTOM] && borderExistences[LEFT]) ||
        (borderAssociations[RIGHTBOTTOM] && borderExistences[RIGHT])) {
        borderThicknesses[BOTTOM] =
          applyRatio (borderThicknessModels[BOTTOM], ratios[LESSER]);
      }
      else {
        borderThicknesses[BOTTOM] =
          applyRatio (borderThicknessModels[BOTTOM], ratios[HEIGHT]);
      }
    }
    else borderThicknesses[BOTTOM] = 0;

    int availableWidth = sizes[MAX].width;
    if (borderExistences[LEFT]) {
      if (borderAssociations[LEFTTOP] && borderExistences[TOP]) {
        borderThicknesses[LEFT] =
          borderThicknesses[LEFT] > borderThicknesses[TOP] ?
          borderThicknesses[TOP] : borderThicknesses[LEFT];
      }
      if (borderAssociations[LEFTBOTTOM] && borderExistences[BOTTOM]) {
        borderThicknesses[LEFT] =
          borderThicknesses[LEFT] > borderThicknesses[BOTTOM] ?
          borderThicknesses[BOTTOM] : borderThicknesses[LEFT];
      }
      borderThicknesses[LEFT] = borderThicknesses[LEFT] > availableWidth ?
        availableWidth : borderThicknesses[LEFT];
      availableWidth -= borderThicknesses[LEFT];
    }

    if (borderExistences[RIGHT]) {
      if (borderAssociations[RIGHTTOP] && borderExistences[TOP]) {
        borderThicknesses[RIGHT] =
          borderThicknesses[RIGHT] > borderThicknesses[TOP] ?
          borderThicknesses[TOP] : borderThicknesses[RIGHT];
      }
      if (borderAssociations[RIGHTBOTTOM] && borderExistences[BOTTOM]) {
        borderThicknesses[RIGHT] =
          borderThicknesses[RIGHT] > borderThicknesses[BOTTOM] ?
          borderThicknesses[BOTTOM] : borderThicknesses[RIGHT];
      }
      borderThicknesses[RIGHT] = borderThicknesses[RIGHT] > availableWidth ?
        availableWidth : borderThicknesses[RIGHT];
    }

    int availableHeight = sizes[MAX].height;
    if (borderExistences[TOP]) {
      if (borderAssociations[LEFTTOP] && borderExistences[LEFT]) {
        borderThicknesses[TOP] =
          borderThicknesses[TOP] > borderThicknesses[LEFT] ?
          borderThicknesses[TOP] : borderThicknesses[TOP];
      }
      if (borderAssociations[RIGHTTOP] && borderExistences[RIGHT]) {
        borderThicknesses[TOP] =
          borderThicknesses[TOP] > borderThicknesses[RIGHT] ?
          borderThicknesses[BOTTOM] : borderThicknesses[TOP];
      }
      borderThicknesses[TOP] = borderThicknesses[TOP] > availableHeight ?
        availableHeight : borderThicknesses[TOP];
      availableHeight -= borderThicknesses[TOP];
    }

    if (borderExistences[BOTTOM]) {
      if (borderAssociations[LEFTBOTTOM] && borderExistences[LEFT]) {
        borderThicknesses[BOTTOM] =
          borderThicknesses[BOTTOM] > borderThicknesses[LEFT] ?
          borderThicknesses[BOTTOM] : borderThicknesses[BOTTOM];
      }
      if (borderAssociations[RIGHTBOTTOM] && borderExistences[RIGHT]) {
        borderThicknesses[BOTTOM] =
          borderThicknesses[BOTTOM] > borderThicknesses[RIGHT] ?
          borderThicknesses[BOTTOM] : borderThicknesses[BOTTOM];
      }
      borderThicknesses[BOTTOM] = borderThicknesses[BOTTOM] > availableHeight ?
        availableHeight : borderThicknesses[BOTTOM];
    }
  }


  private void updateGapThicknesses () {

    if (gapExistences[LEFT]) {
      if ((gapAssociations[LEFTTOP] && gapExistences[TOP]) ||
        (gapAssociations[LEFTBOTTOM] && gapExistences[BOTTOM])) {
        gapThicknesses[LEFT] =
          applyRatio (gapThicknessModels[LEFT], ratios[LESSER]);
      }
      else {
        gapThicknesses[LEFT] =
          applyRatio (gapThicknessModels[LEFT], ratios[WIDTH]);
      }
    }
    else gapThicknesses[LEFT] = 0;

    if (gapExistences[RIGHT]) {
      if ((gapAssociations[RIGHTTOP] && gapExistences[TOP]) ||
        (gapAssociations[RIGHTBOTTOM] && gapExistences[BOTTOM])) {
        gapThicknesses[RIGHT] =
          applyRatio (gapThicknessModels[RIGHT], ratios[LESSER]);
      }
      else {
        gapThicknesses[RIGHT] =
          applyRatio (gapThicknessModels[RIGHT], ratios[WIDTH]);
      }
    }
    else gapThicknesses[RIGHT] = 0;

    if (gapExistences[TOP]) {
      if ((gapAssociations[LEFTTOP] && gapExistences[LEFT]) ||
        (gapAssociations[RIGHTTOP] && gapExistences[RIGHT])) {
        gapThicknesses[TOP] =
          applyRatio (gapThicknessModels[TOP], ratios[LESSER]);
      }
      else {
        gapThicknesses[TOP] =
          applyRatio (gapThicknessModels[TOP], ratios[HEIGHT]);
      }
    }
    else gapThicknesses[TOP] = 0;

    if (gapExistences[BOTTOM]) {
      if ((gapAssociations[LEFTBOTTOM] && gapExistences[LEFT]) ||
        (gapAssociations[RIGHTBOTTOM] && gapExistences[RIGHT])) {
        gapThicknesses[BOTTOM] =
          applyRatio (gapThicknessModels[BOTTOM], ratios[LESSER]);
      }
      else {
        gapThicknesses[BOTTOM] =
          applyRatio (gapThicknessModels[BOTTOM], ratios[HEIGHT]);
      }
    }
    else gapThicknesses[BOTTOM] = 0;

    int availableWidth =
      sizes[MAX].width - borderThicknesses[LEFT] - borderThicknesses[RIGHT];
    if (gapExistences[LEFT]) {
      if (gapAssociations[LEFTTOP] && gapExistences[TOP]) {
        gapThicknesses[LEFT] = gapThicknesses[LEFT] > gapThicknesses[TOP] ?
          gapThicknesses[TOP] : gapThicknesses[LEFT];
      }
      if (gapAssociations[LEFTBOTTOM] && gapExistences[BOTTOM]) {
        gapThicknesses[LEFT] = gapThicknesses[LEFT] > gapThicknesses[BOTTOM] ?
          gapThicknesses[BOTTOM] : gapThicknesses[LEFT];
      }
      gapThicknesses[LEFT] = gapThicknesses[LEFT] > availableWidth ?
        availableWidth : gapThicknesses[LEFT];
      availableWidth -= gapThicknesses[LEFT];
    }

    if (gapExistences[RIGHT]) {
      if (gapAssociations[RIGHTTOP] && gapExistences[TOP]) {
        gapThicknesses[RIGHT] = gapThicknesses[RIGHT] > gapThicknesses[TOP] ?
          gapThicknesses[TOP] : gapThicknesses[RIGHT];
      }
      if (gapAssociations[RIGHTBOTTOM] && gapExistences[BOTTOM]) {
        gapThicknesses[RIGHT] = gapThicknesses[RIGHT] > gapThicknesses[BOTTOM] ?
          gapThicknesses[BOTTOM] : gapThicknesses[RIGHT];
      }
      gapThicknesses[RIGHT] = gapThicknesses[RIGHT] > availableWidth ?
        availableWidth : gapThicknesses[RIGHT];
    }

    int availableHeight =
      sizes[MAX].height - borderThicknesses[TOP] - borderThicknesses[BOTTOM];
    if (gapExistences[TOP]) {
      if (gapAssociations[LEFTTOP] && gapExistences[LEFT]) {
        gapThicknesses[TOP] = gapThicknesses[TOP] > gapThicknesses[LEFT] ?
          gapThicknesses[TOP] : gapThicknesses[TOP];
      }
      if (gapAssociations[RIGHTTOP] && gapExistences[RIGHT]) {
        gapThicknesses[TOP] = gapThicknesses[TOP] > gapThicknesses[RIGHT] ?
          gapThicknesses[BOTTOM] : gapThicknesses[TOP];
      }
      gapThicknesses[TOP] = gapThicknesses[TOP] > availableHeight ?
        availableHeight : gapThicknesses[TOP];
      availableHeight -= gapThicknesses[TOP];
    }

    if (gapExistences[BOTTOM]) {
      if (gapAssociations[LEFTBOTTOM] && gapExistences[LEFT]) {
        gapThicknesses[BOTTOM] = gapThicknesses[BOTTOM] > gapThicknesses[LEFT] ?
          gapThicknesses[BOTTOM] : gapThicknesses[BOTTOM];
      }
      if (gapAssociations[RIGHTBOTTOM] && gapExistences[RIGHT]) {
        gapThicknesses[BOTTOM] =
          gapThicknesses[BOTTOM] > gapThicknesses[RIGHT] ?
          gapThicknesses[BOTTOM] : gapThicknesses[BOTTOM];
      }
      gapThicknesses[BOTTOM] = gapThicknesses[BOTTOM] > availableHeight ?
        availableHeight : gapThicknesses[BOTTOM];
    }
  }


  private void updateMaxSpaceSizeObjects() {

    spaceSizeLocations[MAX] = new Point (
      sizeLocations[MAX].x + borderThicknesses[LEFT] + gapThicknesses[LEFT],
      sizeLocations[MAX].y + borderThicknesses[TOP] + gapThicknesses[TOP]);
    spaceSizes[MAX] = new Dimension (
      sizes[MAX].width - (borderThicknesses[LEFT] + borderThicknesses[RIGHT]) -
        (gapThicknesses[LEFT] + gapThicknesses[RIGHT]),
      sizes[MAX].height - (borderThicknesses[TOP] + borderThicknesses[BOTTOM]) -
        (gapThicknesses[TOP] + gapThicknesses[BOTTOM]));
  }


  private void updateMinObjects() {

    if (autoSizes[MIN]) {
      sizeLocations[MIN] = sizeLocations[MAX];
      sizes[MIN] = sizes[MAX];
      spaceSizeLocations[MIN] = spaceSizeLocations[MAX];
      spaceSizes[MIN] = spaceSizes[MAX];
    }

    else {

      //spaceSizes[MIN] is set by sub-class
      sizes[MIN] = new Dimension (
        spaceSizes[MIN].width + borderThicknesses[LEFT] +
        borderThicknesses[RIGHT] + gapThicknesses[LEFT] + gapThicknesses[RIGHT],
        spaceSizes[MIN].height + borderThicknesses[TOP] +
        borderThicknesses[BOTTOM] + gapThicknesses[TOP] +
        gapThicknesses[BOTTOM]);

      //find locations
      int x = -1, y = -1;
      if (autoJustifys[HORIZONTAL]) {
        if (justifications[HORIZONTAL] == LEFT) {
          x = sizeLocations[MAX].x;
        }
        else if (justifications[HORIZONTAL] == RIGHT) {
          x = sizeLocations[MAX].x + sizes[MAX].width - sizes[MIN].width;
        }
        else if (justifications[HORIZONTAL] == CENTER) {
          x = sizeLocations[MAX].x +
            (int)((sizes[MAX].width - sizes[MIN].width) / 2);
        }
      }
      else x = spaceSizeLocations[MIN].x -
        borderThicknesses[LEFT] - gapThicknesses[LEFT];
      if (autoJustifys[VERTICAL]) {
        if (justifications[VERTICAL] == TOP) {
          y = sizeLocations[MAX].y;
        }
        else if (justifications[VERTICAL] == BOTTOM) {
          y = sizeLocations[MAX].y + sizes[MAX].height - sizes[MIN].height;
        }
        else if (justifications[VERTICAL] == CENTER) {
          y = sizeLocations[MAX].y +
            (int)((sizes[MAX].height  - sizes[MIN].height) / 2);
        }
      }
      else y = spaceSizeLocations[MIN].y -
        borderThicknesses[TOP] - gapThicknesses[TOP];
      sizeLocations[MIN] =  new Point (x, y);

      spaceSizeLocations[MIN] = new Point (
        x + borderThicknesses[LEFT] + gapThicknesses[LEFT],
          y + borderThicknesses[TOP] + gapThicknesses[TOP]);
    }
  }


  private void updateBackground() {

    if (backgroundExistence) {
    background = new Rectangle (sizeLocations[MIN].x, sizeLocations[MIN].y,
      sizes[MIN].width, sizes[MIN].height);
    }
    else background = new Rectangle();
  }


  private void updateBorderRectangles() {

    boolean[] usedCorner = new boolean[4];

    if (borderCornerAssociations[LEFTTOP] == LEFT) {

      borders[LEFT].setLocation (sizeLocations[MIN]);
      borders[TOP].setLocation (sizeLocations[MIN].x + borderThicknesses[LEFT],
        sizeLocations[MIN].y);
    }
    else {
      borders[LEFT].setLocation (sizeLocations[MIN].x,
        sizeLocations[MIN].y + borderThicknesses[TOP]);
      borders[TOP].setLocation (sizeLocations[MIN]);
    }

    if (borderCornerAssociations[RIGHTTOP] == RIGHT) {

      borders[RIGHT].setLocation (
        sizeLocations[MIN].x + sizes[MIN].width - borderThicknesses[RIGHT],
        sizeLocations[MIN].y);
    }
    else {

      borders[RIGHT].setLocation (
        sizeLocations[MIN].x + sizes[MIN].width - borderThicknesses[RIGHT],
        sizeLocations[MIN].y + borderThicknesses[TOP]);
    }

    if (borderCornerAssociations[LEFTBOTTOM] == BOTTOM) {
      borders[BOTTOM].setLocation (sizeLocations[MIN].x,
        sizeLocations[MIN].y + sizes[MIN].height - borderThicknesses[BOTTOM]);
    }
    else {
      borders[BOTTOM].setLocation (
        sizeLocations[MIN].x + borderThicknesses[LEFT],
        sizeLocations[MIN].y + sizes[MIN].height - borderThicknesses[BOTTOM]);
    }

    int delta = 0;
    delta += (borderCornerAssociations[LEFTTOP] == LEFT ?
      0 : borderThicknesses[TOP]);
    delta += (borderCornerAssociations[LEFTBOTTOM] == LEFT ?
      0 : borderThicknesses[BOTTOM]);
    borders[LEFT].setSize (borderThicknesses[LEFT], sizes[MIN].height - delta);

    delta = 0;
    delta += (borderCornerAssociations[RIGHTTOP] == RIGHT ?
      0 : borderThicknesses[TOP]);
    delta += (borderCornerAssociations[RIGHTBOTTOM] == RIGHT ?
      0 : borderThicknesses[BOTTOM]);
    borders[RIGHT].setSize (
      borderThicknesses[RIGHT], sizes[MIN].height - delta);

    delta = 0;
    delta += (borderCornerAssociations[LEFTTOP] == TOP ?
      0 : borderThicknesses[LEFT]);
    delta += (borderCornerAssociations[RIGHTTOP] == TOP ?
      0 : borderThicknesses[RIGHT]);
    borders[TOP].setSize (sizes[MIN].width - delta, borderThicknesses[TOP]);

    delta = 0;
    delta += (borderCornerAssociations[LEFTBOTTOM] == BOTTOM ?
      0 : borderThicknesses[LEFT]);
    delta += (borderCornerAssociations[RIGHTBOTTOM] == BOTTOM ?
      0 : borderThicknesses[RIGHT]);
    borders[BOTTOM].setSize (
      sizes[MIN].width - delta, borderThicknesses[BOTTOM]);
  }


  private void updateBackgroundPaint() {

    if (lightSource == TOP) {
      backgroundPaint = new GradientPaint (
        sizeLocations[MIN].x, sizeLocations[MIN].y, backgroundColor.brighter(),
        sizeLocations[MIN].x, sizeLocations[MIN].y + sizes[MIN].height, backgroundColor);
    }
    else if (lightSource == BOTTOM) {
      backgroundPaint = new GradientPaint (
        sizeLocations[MIN].x, sizeLocations[MIN].y, backgroundColor,
        sizeLocations[MIN].x, sizeLocations[MIN].y + sizes[MIN].height, backgroundColor.brighter());
    }
    else if (lightSource == LEFT) {
      backgroundPaint = new GradientPaint (
        sizeLocations[MIN].x, sizeLocations[MIN].y, backgroundColor.brighter(),
        sizeLocations[MIN].x + sizes[MIN].width, sizeLocations[MIN].y, backgroundColor);
    }
    else if (lightSource == RIGHT) {
      backgroundPaint = new GradientPaint (
        sizeLocations[MIN].x, sizeLocations[MIN].y, backgroundColor,
        sizeLocations[MIN].x + sizes[MIN].width, sizeLocations[MIN].y, backgroundColor.brighter());
    }
    else {
      backgroundPaint = backgroundColor;
    }
  }
}