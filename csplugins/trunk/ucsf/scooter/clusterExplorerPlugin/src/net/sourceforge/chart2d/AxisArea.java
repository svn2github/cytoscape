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
 * Contains shared methods of XAxisArea and YAxisArea.
 */
abstract class AxisArea extends TitledArea {



  /**
   * Specifies the type of the Axis.  The type refers to what type of chart
   * it supports.  In any chart the objects are plotted against one side,
   * the values side, the values side has raw numbers like 0, 100, 200, etc.
   * The other side, the labels side, has the labels for the data being plotted,
   * like September, November, etc.  If the labels side is along the bottom
   * of the graph, then set the type to LABELSBOTTOM.  If the labels side is
   * along the left/right side of the graph then set the type to LABELSLEFT.
   * @param type Whether this axis is used for labels bottom or labels left
   * applications.
   */
  abstract void setType (int type);


  /**
   * The color for the ticks.
   * @param color The x Axis tick color.
   */
  abstract void setTicksColor (Color color);


  /**
   * The alignment of the ticks respective to the labels.  The
   * bullets can either be place in line with each label, or in in line with
   * the middle of the space between each label.  That is, bullets can be
   * centered in the middle of the label, or placed between each label.
   * @param alignment With values of either Area.CENTERED or Area.BETWEEN
   */
  abstract void setTicksAlignment (int alignment);


 /**
   * The number of ticks should be equal to the number of axis labels at all
   * times, EXCEPT with a type of chart with a (LABELSBOTTOM axis and a
   * graph components alignment is true).
   * @param num The number of axis ticks.
   */
  abstract void setNumTicks (int num);


  /**
   * The model size for the ticks.  If auto maximum sizing is enabled,
   * then the actual tick size can grow and shrink; in this case a ratio based
   * on the maximum area size / model area size is computed and applied to the
   * model size in order to find the actual size.  With maximum sizing
   * disabled, the actual size is the model size.
   * @param size The model size for the ticks.  [Do not pass null]
   */
  abstract void setTicksSizeModel (Dimension size);


  /**
   * The existence of a gap between each tick and the next.  If the gap does
   * not exist, then it will not be used in calculations.
   * @param existence The existence of a gap between each tick and the next.  If
   * true, then they do.
   */
  abstract void setBetweenTicksGapExistence (boolean existence);


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
  abstract void setBetweenTicksGapThicknessModel (int gap);


  /**
   * The existence of a gap between the row of labels and the row of ticks.  If
   * the gap does
   * not exist, then it will not be used in calculations.
   * @param existence The existence of a gap between the labels and ticks.  If
   * true, then they do.
   */
  abstract void setBetweenTicksAndLabelsGapExistence (boolean existence);


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
  abstract void setBetweenTicksAndLabelsGapThicknessModel (int gap);


  /**
   * The labels of the axis.  The lowest order array label is the top
   * most label.
   * @param g2D The graphics context to use for calculations.
   * @return The text labels; this will never be null.
   */
  abstract TextArea[] getLabels (Graphics2D g2D);


  /**
   * The model size for the ticks.  If auto maximum sizing is enabled,
   * then the actual tick size can grow and shrink; in this case a ratio based
   * on the maximum area size / model area size is computed and applied to the
   * model size in order to find the actual size.  With maximum sizing
   * disabled, the actual size is the model size.
   * @return The model size for the ticks.  [Do not pass null]
   */
  abstract Dimension getTicksSizeModel();


  /**
   * The bounds of the ticks.  The bounds of the ticks specify the locations
   * and sizes of each actual tick.  The lowest order array tick is the left
   * most tick.
   * @param g2D The graphics context to use for calculations.
   * @return The bounds of the ticks.  This will never be null.
   */
  abstract Rectangle[] getTicks (Graphics2D g2D);


  /**
   * Returns the model thickness of the gap between the ticks.
   * @return The thickness.
   */
  abstract int getBetweenTicksGapThicknessModel();


  /**
   * Returns the color of the ticks.
   * @return The color.
   */
  abstract Color getTicksColor();


  /**
   * Returns how the ticks are aligned with respect to the labels.
   * @return int With values of either Area.CENTERED or Area.BETWEEN
   */
  abstract int getTicksAlignment();
}