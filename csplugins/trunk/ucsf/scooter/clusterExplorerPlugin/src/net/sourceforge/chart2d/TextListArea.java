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
 * An abstract class for shared methods between HorizontalTextListArea and
 * VerticalTextListArea.
 */
abstract class TextListArea extends FontArea {


  /**
   * Allows this text list area to determine its minimum size and reset its own
   * size.  This is useful when you want the component to be able to figure out
   * its minimum size, but not always to set itself to that miniumum size.
   * @param allow If true, then the component can reset its own minimum size.
   * This is only relevant when auto minimum sizing is enabled.
   */
  abstract void setAllowSelfSize (boolean allow);


  /**
   * Specifies the text for the labels.  This value cannot be null; however,
   * a zero or greater length array is fine.
   * @param labels An array of the strings to be used for the labels.
   */
  abstract void setLabels (String[] labels);


  /**
   * Specifies the model size (width and height) of the bullets.  A ratio based
   * on maximum size / model size will be applied to this to find the actual
   * bullet size -- when auto sizing the model maximum size is disabled.
   * Otherwise, the actual size will be this size.
   * @param model The model size of a bullet.
   */
  abstract void setBulletsSizeModel (Dimension model);


  /**
   * The horizontal alignment of the bullets respective to the labels.  The
   * bullets can either be place in line with each label, or in in line with
   * the middle of the space between each label.  That is, bullets can be
   * centered in the middle of the label, or placed between each label.
   * @param alignment The alignment for the bullets.
   * Possible values are CENTERED and BETWEEN.
   */
  abstract void setBulletsAlignment (int alignment);


  /**
   * Specifies the horizontal relation of the bullets to the labels.
   * The bullets
   * can either be placed along the left of the labels or along the right.
   * @param relation The horizontal relation of the bullets.
   * Possible values are LEFT and RIGHT.
   */
  abstract void setBulletsRelation (int relation);


  /**
   * Specifies the colors of the bullets.  Each bullet can have a different
   * color so an array must be passed.  The number of colors must be equal to
   * the number of bullets.  If the bullets alignment is between the labels and
   * the lables do exist, then there should be one bullet less than the number
   * of labels.  If the bullets alignment is centered and the labels do exist,
   * then there should be the same number of bullets as labels.  Otherwise,
   * choose any number of bullets.  Number of bullets is set by setting the
   * bullet colors.  The number of bullets always equals the number of colors.
   * @param colors An array filled with a color for each bullet.  The first
   * bullet from left to right gets the lowest order color in the array.
   */
  abstract void setBulletColors (Color[] colors);


  /**
   * Specifies whether the bullets should have a small black outline.
   * Outline is 1 pixel on all sides, at all times, and is black.
   * Outlien is included in size of bullet.
   * @param outline If true, then the outline will exist.
   */
  abstract void setBulletsOutline (boolean outline);


  /**
   * Specifies the color of the bullets outline.
   * @param color The outline color.
   */
  abstract void setBulletsOutlineColor (Color color);


  /**
   * Specifies whether the <b>minimum</b> amount of space between each label,
   * the gap, shall be enforced.  If the gap does not exist, then it will
   * not be included in calculations.
   * @param existence The existence of the gap.  If true, then it exists.
   */
  abstract void setBetweenLabelsGapExistence (boolean existence);


  /**
   * Specifies the model <b>minimum</b> thickness of the gap between the labels.
   * @param model The model minimum thickness of the gap.
   * Note:  The gap may end up being more depending on sizing properties.
   */
  abstract void setBetweenLabelsGapThicknessModel (int model);


  /**
   * Specifies the existence of a minimum gap between each bullets.  If the gap
   * doesn't exist, then it will not be included in calculations.
   * @param existence The existence of the minimum gap.  If true, then the gap
   * does exist.
   */
  abstract void setBetweenBulletsGapExistence (boolean existence);


  /**
   * Specifies the model <b>minimum</b> thickness of the gap between each
   * bullet.
   * @param model The model minimum thickness of the gap.
   * Note:  The gap may end up being more depending on sizing properties.
   */
  abstract void setBetweenBulletsGapThicknessModel (int model);


  /**
   * Specifies the existence of a minimum gap between the labels and the
   * bullets.  If the gap doesn't exist, then it will not be included in
   * calculations.
   * @param boolean The existence of the minimum gap.  If true, then the gap
   * does exist.
   */
  abstract void setBetweenBulletsAndLabelsGapExistence (boolean existence);


  /**
   * Specifies the model <b>minimum</b> thickness of the gap between the labels
   * and the bullets.
   * @param model The model minimum thickness of the gap.
   * Note:  The gap may end up being more depending on sizing properties.
   */
  abstract void setBetweenBulletsAndLabelsGapThicknessModel (int model);


  /**
   * Returns the label strings of this text list.
   * @return The label strings.
   */
  abstract String[] getLabelStrings();


  /**
   * Returns the labels.  This is useful if other components
   * need to be aligned exactly with the label's location or should be the
   * exact same size.
   * @param g2D The graphics context used for calculations.
   * @return The array of TextArea's which are the labels.
   */
  abstract TextArea[] getLabels (Graphics2D g2D);


  /**
   * Returns the model thickness of the minimum gap between bullets.
   * @return int The model thickness.
   */
  abstract int getBetweenBulletsGapThicknessModel();

  /**
   * Returns the model thickness of the minimum gap between labels.
   * @return The model thickness.
   */
  abstract int getBetweenLabelsGapThicknessModel();


  /**
   * Returns the model <b>minimum</b> thickness of the gap between the labels
   * and the bullets.
   * @return The model minimum thickness of the gap.
   */
  abstract int getBetweenBulletsAndLabelsGapThicknessModel();


  /**
   * Returns the bounds for each bullet.  This is useful if other components
   * need to be aligned exactly with the bullet's location or should be the
   * exact same size.
   * @param g2D The graphics context used for calculations.
   * @return The array of rectangles which bound each bullet.
   */
  abstract Rectangle[] getBullets (Graphics2D g2D);


  /**
   * Returns the model size of the bullets.
   * @return The size.
   */
  abstract Dimension getBulletsSizeModel();


  /**
   * The horizontal alignment of the bullets respective to the labels.  The
   * bullets can either be place in line with each label, or in in line with
   * the middle of the space between each label.  That is, bullets can be
   * centered in the middle of the label, or placed between each label.
   * @return The alignment for the bullets.
   * Possible values are CENTERED and BETWEEN.
   */
  abstract int getBulletsAlignment();


  /**
   * Specifies whether the bullets should have a small outline.
   * Outline is 1 pixel on all sides, at all times, and is black.
   * Outline is included in size of bullet.
   * @return outline If true, then the outline will exist.
   */
  abstract boolean getBulletsOutline();


  /**
   * Specifies the color of the bullets outline.
   * @return color The outline color.
   */
  abstract Color getBulletsOutlineColor();
}