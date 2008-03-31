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


/**
 * An x Axis for chart graphs.  Supports both vertical and horizontal bar
 * charts, low number of points line and scatter charts.  Bullets can be either
 * aligned with the labels as in horizontal bar charts or between the labels as
 * int vertical bar charts.  Label font size, type, style, and color are
 * adjustable.  Minimum gaps between labels, ticks, and labels and bullets
 * may be specified.  Ticks size and color may be specified.  A title and
 * its font may also be customized.  The title may be justified withing the
 * width of the axis, left, right or center.
 * Supports all of bordered areas customizability except for auto min sizing.
 * If auto minimum sizing, then the title will likely be far apart from the
 * axis.  It does support everything with auto min sizing disabled.  It is
 * recommended that the auto justification be disabled.  The xAxis must be
 * butted up against a graph area, this is best done manually.
 * This is its default.
 */
final class XAxisArea extends AxisArea {


  private HorizontalTextListArea textList;
  private Color ticksColor;
  private int numTicks;
  private boolean needsUpdate;

  /**
   * Creates a new xAxis Area with the default settings.
   */
  XAxisArea() {

    textList = new HorizontalTextListArea();

    setAutoSizes (false, false);
    setAutoJustifys (false, false);
    setTitleJustifications (CENTER, BOTTOM);
    setBackgroundExistence (false);
    setBorderExistence (false);
    setGapExistence (false);

    setTicksSizeModel (new Dimension (3, 3));
    setType (LABELSBOTTOM);
    setTicksColor (Color.black);
    setNumTicks (0);

    textList.setBulletsRelation (TOP);
    textList.setAutoJustifys (false, false);
    textList.setBorderExistence (false);
    textList.setGapExistence (false);
    textList.setBackgroundExistence (false);
    textList.setBulletsOutline (false);

    resetXAxisModel (true);
    needsUpdate = true;
  }


  /**
   * Specifies the type of the x Axis.  The type refers to what type of chart
   * it supports.  In any chart the objects are plotted against one side,
   * the values side, the values side has raw numbers like 0, 100, 200, etc.
   * The other side, the labels side, has the labels for the data being plotted,
   * like September, November, etc.  If the labels side is along the bottom
   * of the graph, then set the type to LABELSBOTTOM.  If the labels side is
   * along the left/right side of the graph then set the type to LABELSLEFT.
   * @param type Whether this axis is used for labels bottom or labels left
   * applications.
   */
  final void setType (int type) {

    needsUpdate = true;
    if (type == LABELSBOTTOM) textList.setBulletsAlignment (BETWEEN);
    else textList.setBulletsAlignment (CENTERED);
  }


  /**
   * The color for the ticks.
   * @param color The x Axis tick color.
   */
  final void setTicksColor (Color color) {

    needsUpdate = true;
    ticksColor = color;
  }


  /**
   * The horizontal alignment of the ticks respective to the labels.  The
   * bullets can either be place in line with each label, or in in line with
   * the middle of the space between each label.  That is, bullets can be
   * centered in the middle of the label, or placed between each label.
   * @param alignment With values of either Area.CENTERED or Area.BETWEEN
   */
  final void setTicksAlignment (int alignment) {

    textList.setBulletsAlignment (alignment);
  }


 /**
   * The number of ticks should be equal to the number of x axis labels at all
   * times, EXCEPT with a type of chart with a (LABELSBOTTOM x axis and a
   * graph components alignment is true).
   * @param num The number of x axis ticks.
   */
  final void setNumTicks (int num) {

    needsUpdate = true;
    numTicks = num;
  }


  /**
   * The model size for the ticks.  If auto maximum sizing is enabled,
   * then the actual tick size can grow and shrink; in this case a ratio based
   * on the maximum area size / model area size is computed and applied to the
   * model size in order to find the actual size.  With maximum sizing
   * disabled, the actual size is the model size.
   * @param size The model size for the ticks.  [Do not pass null]
   */
  final void setTicksSizeModel (Dimension size) {

    needsUpdate = true;
    textList.setBulletsSizeModel (size);
  }


  /**
   * The model size for the ticks.  If auto maximum sizing is enabled,
   * then the actual tick size can grow and shrink; in this case a ratio based
   * on the maximum area size / model area size is computed and applied to the
   * model size in order to find the actual size.  With maximum sizing
   * disabled, the actual size is the model size.
   * @return The model size for the ticks.  [Do not pass null]
   */
  final Dimension getTicksSizeModel() {
    return textList.getBulletsSizeModel();
  }


  /**
   * The existence of a gap between each tick and the next.  If the gap does
   * not exist, then it will not be used in calculations.
   * @param existence The existence of a gap between each tick and the next.  If
   * true, then they do.
   */
  final void setBetweenTicksGapExistence (boolean existence) {

    needsUpdate = true;
    textList.setBetweenBulletsGapExistence (existence);
  }


  /**
   * The model thickness for the gap between each tick.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.
   * @param gap The model thickness for the gap between each tick.
   */
  final void setBetweenTicksGapThicknessModel (int gap) {

    needsUpdate = true;
    textList.setBetweenBulletsGapThicknessModel (gap);
  }


  /**
   * The existence of a gap between the row of labels and the row of ticks.  If
   * the gap does
   * not exist, then it will not be used in calculations.
   * @param existence The existence of a gap between the labels and ticks.  If
   * true, then they do.
   */
  final void setBetweenTicksAndLabelsGapExistence (boolean existence) {

    needsUpdate = true;
    textList.setBetweenBulletsAndLabelsGapExistence (existence);
  }


  /**
   * The model thickness for the gap between the row of labels and the row of
   * ticks.  If auto maximum sizing
   * is enabled,
   * then the actual thickness size can grow and shrink; in this case a ratio
   * based
   * on the maximum area size / model area size is computed and applied to the
   * model thickness in order to find the actual thickness.  With maximum sizing
   * disabled, the actual thickness is the model thickness.  This thickness
   * is not used in calculations if either the ticks or labels do not exist.
   * @param gap The model thickness for the gap between the labels and ticks.
   */
  final void setBetweenTicksAndLabelsGapThicknessModel (int gap) {

    needsUpdate = true;
    textList.setBetweenBulletsAndLabelsGapThicknessModel (gap);
  }


  /**
   * Returns the HorizontalTextListArea for this X Axis.
   * Get for standard configuration of the x axis.
   * @return This x axis component.
   */
  final HorizontalTextListArea getTextList() {

    return textList;
  }


  /**
   * The labels of the axis.  The lowest order array label is the top
   * most label.
   * @param g2D The graphics context to use for calculations.
   * @return The text labels; this will never be null.
   */
  final TextArea[] getLabels (Graphics2D g2D) {

    updateXAxisArea (g2D);
    return textList.getLabels (g2D);
  }


  /**
   * The bounds of the ticks.  The bounds of the ticks specify the locations
   * and sizes of each actual tick.  The lowest order array tick is the left
   * most tick.
   * @param g2D The graphics context to use for calculations.
   * @return The bounds of the ticks.  This will never be null.
   */
  final Rectangle[] getTicks (Graphics2D g2D) {

    updateXAxisArea (g2D);
    return textList.getBullets (g2D);
  }


  /**
   * Returns the model thickness of the gap between the ticks.
   * @return The thickness.
   */
  final int getBetweenTicksGapThicknessModel() {

    return textList.getBetweenBulletsGapThicknessModel();
  }


  /**
   * Returns the color of the ticks.
   * @return The color.
   */
  final Color getTicksColor() {

    return ticksColor;
  }


  /**
   * Returns how the ticks are aligned with respect to the labels.
   * @return int With values of either Area.CENTERED or Area.BETWEEN
   */
  final int getTicksAlignment() {

    return textList.getBulletsAlignment();
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getXAxisAreaNeedsUpdate() {

    return (needsUpdate || getTitledAreaNeedsUpdate() ||
      textList.getHorizontalTextListAreaNeedsUpdate());
  }


  /**
   * Updates this parent's variables, and this' variables.
   * @param g2D The graphics context to use for calculations.
   */
  final void updateXAxisArea (Graphics2D g2D) {

    if (getXAxisAreaNeedsUpdate()) {
      updateTitledArea (g2D);
      update (g2D);
      textList.updateHorizontalTextListArea(g2D);
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
  final void resetXAxisModel (boolean reset) {

    needsUpdate = true;
    resetTitledAreaModel (reset);
    textList.resetHorizontalTextListAreaModel (reset);
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D  The graphics context for calculations and painting.
   */
  final void paintComponent (Graphics2D g2D) {

    updateXAxisArea (g2D);
    super.paintComponent (g2D);
    textList.paintComponent (g2D);
  }


  private void update (Graphics2D g2D) {
    textList.setCustomRatio (WIDTH, true, getRatio (WIDTH));
    textList.setCustomRatio (HEIGHT, true, getRatio (HEIGHT));
    updateTickColors();
    updateMaxTextList (g2D);
    textList.updateHorizontalTextListArea (g2D);
    updateMinSizes (g2D);
    updateMinTextList (g2D);
  }


  private void updateTickColors() {

    Color[] tickColors = new Color[numTicks];
    for (int i = 0; i < numTicks; ++i) {

      tickColors[i] = ticksColor;
    }

    textList.setBulletColors (tickColors);
  }


  private void updateMaxTextList (Graphics2D g2D) {

    textList.setAllowSelfSize(true);
    textList.setAutoSizes (getAutoSize(MAXMODEL), false);
    Rectangle maxBounds = getMaxEntitledSpaceBounds (g2D);
    textList.setSize (MAX, maxBounds.getSize());
    textList.setSizeLocation (MAX, maxBounds.getLocation());
  }


  private void updateMinSizes (Graphics2D g2D) {

    if (!getAutoSize(MIN)) {
      Dimension titleSize = getTitleSize (MIN, g2D);
      Dimension textListSize = textList.getSize (MIN);
      int minWidth = titleSize.width > textListSize.width ?
        titleSize.width : textListSize.width;
      int minHeight;
      if (titleSize.height > 0) {
        minHeight = titleSize.height +
          getBetweenTitleAndSpaceGapThickness (g2D) + textListSize.height;
      }
      else minHeight = titleSize.height + textListSize.height;
      setSpaceSize (MIN, new Dimension (minWidth, minHeight));
    }
  }


  private void updateMinTextList (Graphics2D g2D) {

    int spaceWidth = textList.getSize (MIN).width;
    textList.setAllowSelfSize (false);
    spaceWidth = spaceWidth < getSpaceSize(MIN).width ?
      getSpaceSize(MIN).width : spaceWidth;
    int betweenWidth =
      textList.getSize (MIN).width - textList.getSpaceSize (MIN).width;
    spaceWidth = spaceWidth - betweenWidth;
    textList.setSpaceSize (MIN,
      new Dimension (spaceWidth, textList.getSpaceSize(MIN).height));

    int spaceX = getSpaceSizeLocation(MIN).x;
    int spaceY;
    int betweenHeight =
      textList.getSize (MIN).height - textList.getSpaceSize (MIN).height;
    if (textList.getJustifications (VERTICAL) == TOP)
      spaceY = getSpaceSizeLocation (MIN).y + betweenHeight / 2;
    else
      spaceY = getSpaceSizeLocation (MIN).y + getSpaceSize (MIN).height -
        getTitleSize (MIN, g2D).height -
        getBetweenTitleAndSpaceGapThickness (g2D) -
        textList.getSize (MIN).height  + betweenHeight / 2;

    textList.setSpaceSizeLocation (MIN, new Point (spaceX, spaceY));
  }
}