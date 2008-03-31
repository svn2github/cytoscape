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
 * Maintains a font to be used within an area.  Contains all variables for the
 * font, such as point, name, style, and color.  Builds a font from these
 * variable.  When building font, uses the lesser ratio of the area class.
 * This ensures proper growing and shrinking of the font respective to the
 * changing in the area's size.  Grows and shrinks if and only if max model
 * area auto size is disabled.
 */
class FontArea extends Area {


  private int fontPointModel;
  private String fontName;
  private int fontStyle;
  private Color fontColor;
  private Font font;
  private boolean needsUpdate;


  /**
   * Builds a new FontArea with the default values.
   */
  FontArea () {

    setFontPointModel (12);
    setFontName ("SansSerif");
    setFontStyle (Font.PLAIN);
    setFontColor (Color.black);
    resetFontAreaModel (true);
    needsUpdate = true;
  }


  /**
   * Changes the model font's point size.  This is the size that the font would
   * be if the maximum size of the area was equal to the model size of the area.
   * Otherwise, a ratio based on maximum size / model size is taken and applied
   * to this point, producing the actual font point.
   * @param p The new model font point.
   */
  final void setFontPointModel (int p) {

    needsUpdate = true;
    fontPointModel = p;
  }


  /**
   * Changes the name of this font.  This accepts the same values as
   * the Font classes constructor
   * <code>Font (String name, int style, int point)</code>.
   * @param n Then new name of the font.
   */
  final void setFontName (String n) {

    needsUpdate = true;
    fontName = n;
  }


  /**
   * Changes the style of this font.  This accepts the same values as
   * the Font classes constructor
   * <code>Font (String name, int style, int point)</code>.
   * @param s Then new style of the font.
   */
  final void setFontStyle (int s) {

    needsUpdate = true;
    fontStyle = s;
  }


  /**
   * Changes the color of this font.  This accepts all Color.* compatible
   * values.
   * @param c Then new color of the font.
   */
  final void setFontColor (Color c) {

    needsUpdate = true;
    fontColor = c;
  }


  /**
   * Returns the model font's point; not the actual font point.
   * @return The model font's point.
   */
  final int getFontPointModel() {

    return fontPointModel;
  }


  /**
   * Returns the name of this font.
   * @return Then name of the font.
   */
  final String getFontName() {

    return fontName;
  }


  /**
   * Returns the style of this font.
   * @return Then style of the font.
   */
  final int getFontStyle() {

    return fontStyle;
  }


  /**
   * Returns this font's color.
   * @return This font's color.
   */
  final Color getFontColor() {

    return fontColor;
  }


  /**
   * Returns a font built from the present variable values.  Updates
   * the font point based on the model font point, builds an font, and return
   * it.
   * @return The current font.
   */
  final Font getFont() {

    updateFontArea();
    return font;
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
    final boolean getFontAreaNeedsUpdate() {

      return (needsUpdate  || getAreaNeedsUpdate());
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
  final void resetFontAreaModel (boolean reset) {

    needsUpdate = true;
    resetAreaModel (reset);
  }


  /**
   * Updates all this classes variables.  First updates it's parent class, then
   * then updates its own variables.
   */
  final void updateFontArea () {

    if (getFontAreaNeedsUpdate()) {
      updateArea ();
      update();
    }
    needsUpdate = false;
  }


  /**
   * Paints this class.  Updates all variables, then paints its parent, since
   * this class itself doesn't have anything to paint.
   * @param g2D The graphics context for calculations and painting.
   */
  void paintComponent (Graphics2D g2D) {

    updateFontArea();
    super.paintComponent (g2D);
  }


  private void update() {

    int fontPoint = 0;
    if (fontPointModel > 0) {
      fontPoint = applyRatio (fontPointModel, getRatio (LESSER));
    }
    else fontPoint = 0;
    font = new Font (fontName, fontStyle, fontPoint);
  }
}