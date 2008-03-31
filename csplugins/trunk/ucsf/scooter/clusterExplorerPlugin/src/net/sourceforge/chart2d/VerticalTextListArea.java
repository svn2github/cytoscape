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
 * A set of text labels layed out vertially with bullets on either the left or
 * right of the labels.  The bullets can be layed out so that they are aligned
 * with the center of each label, or aligned with the middle of the space
 * between each label (vertically).  It
 * was designed to be used as a legend and for making a y axis in a chart
 * program.  Adjustments include
 * bullets/no bullets, labels/no labels, bullet sizes, bullet colors, labels
 * font color, size, style, and type; plus it allows for all the functionality
 * of the the bordered area class including borders, spacing within borders,
 * auto horizontal and vertical justification, auto sizing, and auto growing and
 * shrinking of components.<br>
 * Note:  This class does not accept null values.  Pass zero length arrays or
 * empty strings instead.
 */
class VerticalTextListArea extends TextListArea {


  private boolean labelsExistence = true;
  private TextArea[] labels;
  private String[] labelStrings;

  private boolean bulletsExistence = true;
  private Rectangle[] bullets;
  private Dimension bulletsSizeModel;
  private int bulletsAlignment;
  private int bulletsRelation;
  private Color[] bulletColors;
  private boolean bulletsOutline;
  private Color bulletsOutlineColor;

  private boolean betweenLabelsGapExistence;
  private int betweenLabelsGapThicknessModel;
  private boolean betweenBulletsGapExistence;
  private int betweenBulletsGapThicknessModel;
  private boolean betweenBulletsAndLabelsGapExistence;
  private int betweenBulletsAndLabelsGapThicknessModel;
  private boolean customizeSpaceMinHeight;
  private int customSpaceMinHeight;

  private boolean allowSelfSize;
  private boolean needsUpdate;


  /**
   * Constructs a new vertical text list area with default values.   */
  VerticalTextListArea() {

    labelStrings = new String[0];
    labels = new TextArea[0];
    bullets = new Rectangle[0];
    bulletColors = new Color[0];

    setBulletsSizeModel (new Dimension (9, 9));
    setBulletsOutline (true);
    setBulletsOutlineColor (Color.black);
    setBetweenLabelsGapExistence (true);
    setBetweenLabelsGapThicknessModel (3);
    setBetweenBulletsGapExistence (true);
    setBetweenBulletsGapThicknessModel (3);
    setBetweenBulletsAndLabelsGapExistence (true);
    setBetweenBulletsAndLabelsGapThicknessModel (3);
    setBulletsAlignment (CENTERED);
    setBulletsRelation (LEFT);
    setAllowSelfSize (true);
    resetVerticalTextListAreaModel (true);
    needsUpdate = true;
    customizeSpaceMinHeight = false;
  }


  /**
   * Allows this text list area to determine its minimum size and reset its own
   * size.  This is useful when you want the component to be able to figure out
   * its minimum size, but not always to set itself to that miniumum size.
   * @param allow If true, then the component can reset its own minimum size.
   * This is only relevant when auto minimum sizing is enabled.
   */
  final void setAllowSelfSize (boolean allow) {

    needsUpdate = true;
    allowSelfSize = allow;
  }


  /**
   * Specifies the text for the labels.  This value cannot be null; however,
   * a zero or greater length array is fine.
   * @param labels An array of the strings to be used for the labels.
   */
  final void setLabels (String[] labels) {

    needsUpdate = true;
    labelStrings = labels;
  }


  /**
   * Allows for outside specification of the size of the text list.
   * This method was introduced as an afterthought.
   * It's used by LLChartArea for feeding back the size of the graph area.
   * @param customize True if you want to customize the space height.
   * @param height The custom space height (if the boolean is true).
   */
  final void setCustomSpaceMinHeight (boolean customize, int height) {

    needsUpdate = true;
    customizeSpaceMinHeight = customize;
    customSpaceMinHeight = height;
  }


  /**
   * Specifies the model size (width and height) of the bullets.  A ratio based
   * on maximum size / model size will be applied to this to find the actual
   * bullet size -- when auto sizing the model maximum size is disabled.
   * Otherwise, the actual size will be this size.
   * @param model The model size of a bullet.
   */
  final void setBulletsSizeModel (Dimension model) {

    needsUpdate = true;
    bulletsSizeModel = model;
  }


  /**
   * The horizontal alignment of the bullets respective to the labels.  The
   * bullets can either be place in line with each label, or in in line with
   * the middle of the space between each label.  That is, bullets can be
   * centered in the middle of the label, or placed between each label.
   * @param alignment The alignment for the bullets.
   * Possible values are CENTERED and BETWEEN.
   */
  final void setBulletsAlignment (int alignment) {

    needsUpdate = true;
    bulletsAlignment = alignment;
  }


  /**
   * Specifies the horizontal relation of the bullets to the labels.
   * The bullets
   * can either be placed along the left of the labels or along the right.
   * @param relation The horizontal relation of the bullets.
   * Possible values are LEFT and RIGHT.
   */
  final void setBulletsRelation (int relation) {

    needsUpdate = true;
    bulletsRelation = relation;
  }


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
  final void setBulletColors (Color[] colors) {

    needsUpdate = true;
    bulletColors = colors;
  }


  /**
   * Specifies whether the bullets should have a small outline.
   * Outline is 1 pixel on all sides, at all times, and is black.
   * Outline is included in size of bullet.
   * @param outline If true, then the outline will exist.
   */
  final void setBulletsOutline (boolean outline) {

    needsUpdate = true;
    bulletsOutline = outline;
  }


  /**
   * Specifies the color of the bullets outline.
   * @param color The outline color.
   */
  final void setBulletsOutlineColor (Color color) {

    bulletsOutlineColor = color;
  }


  /**
   * Specifies whether the <b>minimum</b> amount of space between each label,
   * the gap, shall be enforced.  If the gap does not exist, then it will
   * not be included in calculations.
   * @param existence The existence of the gap.  If true, then it exists.
   */
  final void setBetweenLabelsGapExistence (boolean existence) {

    needsUpdate = true;
    betweenLabelsGapExistence = existence;
  }


  /**
   * Specifies the model <b>minimum</b> thickness of the gap between the labels.
   * @param model The model minimum thickness of the gap.  Note:  The gap may end
   * up being more depending on sizing properties.
   */
  final void setBetweenLabelsGapThicknessModel (int model) {

    needsUpdate = true;
    betweenLabelsGapThicknessModel = model;
  }


  /**
   * Specifies the existence of a minimum gap between each bullets.  If the gap
   * doesn't exist, then it will not be included in calculations.
   * @param existence The existence of the minimum gap.  If true, then the gap
   * does exist.
   */
  final void setBetweenBulletsGapExistence (boolean existence) {

    needsUpdate = true;
    betweenBulletsGapExistence = existence;
  }


  /**
   * Specifies the model <b>minimum</b> thickness of the gap between each
   * bullet.
   * @param model The model minimum thickness of the gap.
   * Note:  The gap may end up being more depending on sizing properties.
   */
  final void setBetweenBulletsGapThicknessModel (int model) {

    needsUpdate = true;
    betweenBulletsGapThicknessModel = model;
  }


  /**
   * Specifies the existence of a minimum gap between the labels and the
   * bullets.  If the gap doesn't exist, then it will not be included in
   * calculations.
   * @param existence The existence of the minimum gap.  If true, then the gap
   * does exist.
   */
  final void setBetweenBulletsAndLabelsGapExistence (boolean existence) {

    needsUpdate = true;
    betweenBulletsAndLabelsGapExistence = existence;
  }


  /**
   * Specifies the model <b>minimum</b> thickness of the gap between the labels
   * and the bullets.
   * @param model The model minimum thickness of the gap.
   * Note:  The gap may end up being more depending on sizing properties.
   */
  final void setBetweenBulletsAndLabelsGapThicknessModel (int model) {

    needsUpdate = true;
    betweenBulletsAndLabelsGapThicknessModel = model;
  }


  /**
   * Returns the label strings of this text list.
   * @return The label strings.
   */
  final String[] getLabelStrings() {

    return labelStrings;
  }


  /**
   * Returns the model thickness of the minimum gap between bullets.
   * @return The model thickness.
   */
  final int getBetweenBulletsGapThicknessModel() {

    return betweenBulletsGapThicknessModel;
  }


  /**
   * Returns the model thickness of the minimum gap between labels.
   * @return The model thickness.
   */
  final int getBetweenLabelsGapThicknessModel() {

    return betweenLabelsGapThicknessModel;
  }


  /**
   * Returns the model <b>minimum</b> thickness of the gap between the labels
   * and the bullets.
   * @return The model minimum thickness of the gap.
   */
  final int getBetweenBulletsAndLabelsGapThicknessModel() {

    return betweenBulletsAndLabelsGapThicknessModel;
  }


  /**
   * Returns the bounds for each bullet.  This is useful if other components
   * need to be aligned exactly with the bullet's location or should be the
   * exact same size.
   * @param g2D The graphics context used for calculations.
   * @return The array of rectangles which bound each bullet.
   */
  final Rectangle[] getBullets (Graphics2D g2D) {

    updateVerticalTextListArea (g2D);
    return bullets;
  }


  /**
   * Returns the number of bullets this text list has.  Calculates it each time.
   * @return The number of bullets of this text list.
   */
  final int getNumBullets() {
    int numBullets = bulletsExistence && labelsExistence ?
      (bulletsAlignment == CENTERED ?
      labelStrings.length : labelStrings.length - 1) : 0;
    return (numBullets > 0 ? numBullets : 0);
  }


  /**
   * Returns the model size (width and height) of the bullets.
   * @return The model size of a bullet.
   */
  final Dimension getBulletsSizeModel() {

    return bulletsSizeModel;
  }


  /**
   * Returns the colors of the bullets.  Each bullet can have a different
   * color so an array must be passed.  The number of colors must be equal to
   * the number of bullets.  If the bullets alignment is between the labels and
   * the lables do exist, then there should be one bullet less than the number
   * of labels.  If the bullets alignment is centered and the labels do exist,
   * then there should be the same number of bullets as labels.  Otherwise,
   * choose any number of bullets.  Number of bullets is set by setting the
   * bullet colors.  The number of bullets always equals the number of colors.
   * @return Color[] An array filled with a color for each bullet.  The first
   * bullet from left to right gets the lowest order color in the array.
   */
  final Color[] getBulletColors() {
    return bulletColors;
  }


  /**
   * Returns the vertical alignment of the bullets respective to the labels.
   * The bullets can either be place in line with each label, or in in line with
   * the middle of the space between each label.  That is, bullets can be
   * centered in the middle of the label, or placed between each label.
   * Possible values are CENTERED and BETWEEN.
   * @return int The alignment for the bullets [CENTERED or BETWEEN].
   */
  final int getBulletsAlignment() {

    return bulletsAlignment;
  }


  /**
   * Specifies whether the bullets should have a small outline.
   * Outline is 1 pixel on all sides, at all times, and is black.
   * Outline is included in size of bullet.
   * @return outline If true, then the outline will exist.
   */
  final boolean getBulletsOutline() {

    return bulletsOutline;
  }


  /**
   * Specifies the color of the bullets outline.
   * @return color The outline color.
   */
  final Color getBulletsOutlineColor() {

    return bulletsOutlineColor;
  }


  /**
   * Returns the labels.  This is useful if other components
   * need to be aligned exactly with the label's location or should be the
   * exact same size.
   * @param g2D The graphics context used for calculations.
   * @return The array of TextArea's which are the labels.
   */
  final TextArea[] getLabels (Graphics2D g2D) {

    updateVerticalTextListArea (g2D);
    return labels;
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
  final void resetVerticalTextListAreaModel (boolean reset) {

    needsUpdate = true;
    resetFontAreaModel (reset);
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getVerticalTextListAreaNeedsUpdate() {

    return (needsUpdate || getFontAreaNeedsUpdate());
  }


  /**
   * Updates all variables.  First updates the variables of its parent class,
   * then updates its own variables.
   * @param g2D The graphics context used for calculations.
   */
  final void updateVerticalTextListArea (Graphics2D g2D) {

    if (getVerticalTextListAreaNeedsUpdate()) {
      updateFontArea ();
      update (g2D);
    }
    needsUpdate = false;
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D  The graphics context for calculations and painting.
   */
  void paintComponent (Graphics2D g2D) {

    updateVerticalTextListArea (g2D);
    super.paintComponent (g2D);

    Color oldColor = g2D.getColor();

    int num = labelsExistence ? labels.length : 0;
    for (int i = 0; i < num; ++i) {
      labels[i].paintComponent (g2D);
    }

    num = bulletsExistence ? bullets.length : 0;
    for (int i = 0; i < num; ++i) {

      g2D.setColor (bulletColors[i]);
      g2D.fill (bullets[i]);

      if (bulletsOutline) {
        g2D.setColor (bulletsOutlineColor);
        g2D.draw (bullets[i]);
      }
    }

    g2D.setColor (oldColor);
  }


  private void update (Graphics2D g2D) {

    int numLabels = labelsExistence ? labelStrings.length : 0;
    labels = new TextArea[numLabels];
    int numBullets = getNumBullets();
    bullets = new Rectangle[numBullets];

    int numBulletGaps = bulletsAlignment == CENTERED ?
      (numBullets - 1) : (numBullets + 1);
    int requiredNumBulletsForGaps = bulletsAlignment == CENTERED ? 2 : 1;
    int requiredNumBulletsForSpacing = bulletsAlignment == CENTERED ?
      numBullets : (numBullets + 1);

    int betweenBulletsAndLabelsGapThickness = 0;
    int availableWidth = getSpaceSize (MAX).width;
    if (betweenBulletsAndLabelsGapExistence && labelsExistence) {
      betweenBulletsAndLabelsGapThickness = applyRatio (
        betweenBulletsAndLabelsGapThicknessModel, getRatio (WIDTH));
      betweenBulletsAndLabelsGapThickness =
        betweenBulletsAndLabelsGapThickness <= availableWidth ?
        betweenBulletsAndLabelsGapThickness : availableWidth;
      availableWidth -= betweenBulletsAndLabelsGapThickness;
    }

    int availableHeightForLabels = getSpaceSize (MAX).height;
    int availableHeightForBullets = getSpaceSize (MAX).height;
    int betweenLabelsGapThickness = 0;
    int betweenBulletsGapThickness = 0;
    if (betweenLabelsGapExistence && numLabels  > 1) {
      betweenLabelsGapThickness =
       applyRatio (betweenLabelsGapThicknessModel, getRatio (HEIGHT));
      betweenLabelsGapThickness = betweenLabelsGapThickness <=
        (availableHeightForLabels / (numLabels - 1)) ?
        betweenLabelsGapThickness :
        (availableHeightForLabels / (numLabels - 1));
      availableHeightForLabels -=
        ((numLabels - 1) * betweenLabelsGapThickness);
    }
    if (betweenBulletsGapExistence && numBullets >= requiredNumBulletsForGaps) {
      betweenBulletsGapThickness =
        applyRatio (betweenBulletsGapThicknessModel, getRatio (HEIGHT));
      betweenBulletsGapThickness = betweenBulletsGapThickness <=
        (availableHeightForBullets / numBulletGaps) ?
        betweenBulletsGapThickness :
        (availableHeightForBullets / numBulletGaps);
      availableHeightForBullets -=
        (numBulletGaps * betweenBulletsGapThickness);
    }

    int bulletsWidth = 0;
    int bulletsHeight = 0;
    if (bulletsExistence) {
      if (numBullets > 0) {
        bulletsWidth = applyRatio (bulletsSizeModel.width, getRatio (WIDTH));
        bulletsWidth =
          bulletsWidth <= availableWidth ? bulletsWidth : availableWidth;
        bulletsHeight = applyRatio (bulletsSizeModel.height, getRatio (HEIGHT));
        bulletsHeight = bulletsHeight <=
          (availableHeightForBullets / requiredNumBulletsForSpacing) ?
          bulletsHeight :
          (availableHeightForBullets / requiredNumBulletsForSpacing);
      }
      if (bulletsWidth <= 0 || bulletsHeight <= 0) {
        bulletsWidth = 0;
        bulletsHeight = 0;
      }
      else {
        availableWidth -= bulletsWidth;
      }
    }

    int labelsWidth = 0;
    int labelsHeight = 0;
    if (labelsExistence && numLabels > 0) {
      labelsWidth = availableWidth;
      labelsHeight = availableHeightForLabels / numLabels;
    }

    for (int i = 0; i < numLabels; ++i) {
      labels[i] = new TextArea();
      labels[i].setCustomRatio (WIDTH, true, getRatio (WIDTH));
      labels[i].setCustomRatio (HEIGHT, true, getRatio (HEIGHT));
      labels[i].setAutoSizes (true, false);
      labels[i].setSize (MAX, new Dimension (labelsWidth, labelsHeight));
      labels[i].setBorderExistence (false);
      labels[i].setGapExistence (false);
      labels[i].setBackgroundExistence (false);
      labels[i].setText (labelStrings[i]);
      labels[i].setFontName(getFontName());
      labels[i].setFontStyle(getFontStyle());
      labels[i].setFontPointModel(getFontPointModel());
      labels[i].setFontColor(getFontColor());
    }

    int greatestLabelWidth = 0;
    int smallestLabelWidth = Integer.MAX_VALUE;
    int greatestLabelHeight = 0;
    for (int i = 0; i < numLabels; ++i) {
      labels[i].updateTextArea (g2D);
      int thisLabelWidth = labels[i].getSize (MIN).width;
      greatestLabelWidth = thisLabelWidth > greatestLabelWidth ?
        thisLabelWidth : greatestLabelWidth;
      smallestLabelWidth = thisLabelWidth < smallestLabelWidth ?
        thisLabelWidth : smallestLabelWidth;
      int thisLabelHeight = labels[i].getSize (MIN).height;
      greatestLabelHeight = thisLabelHeight > greatestLabelHeight ?
        thisLabelHeight : greatestLabelHeight;
    }

    if (smallestLabelWidth > 0) {
      labelsWidth = greatestLabelWidth;
      labelsHeight = greatestLabelHeight;
    }
    else {
      labelsWidth = 0;
      labelsHeight = 0;
      betweenLabelsGapThickness =  0;
      for (int i = 0; i < numLabels; ++i) {
        labels[i].setSize (MAX, new Dimension());
      }

      betweenBulletsAndLabelsGapThickness =  0;
      availableWidth = getSpaceSize (MAX).width;
      bulletsWidth = 0;
      bulletsHeight = 0;
      if (bulletsExistence && numBullets > 0) {
        bulletsWidth = applyRatio (bulletsSizeModel.width, getRatio (WIDTH));
        bulletsWidth =
          bulletsWidth <= availableWidth ? bulletsWidth : availableWidth;
        bulletsHeight = applyRatio (bulletsSizeModel.height, getRatio (HEIGHT));
        bulletsHeight = bulletsHeight <=
          (availableHeightForBullets / requiredNumBulletsForSpacing) ?
          bulletsHeight :
          (availableHeightForBullets / requiredNumBulletsForSpacing);
      }
      if (bulletsWidth <= 0 || bulletsHeight <= 0) {
        bulletsWidth = 0;
        bulletsHeight = 0;
        betweenBulletsGapThickness = 0;
      }
    }

    if (!getAutoSize (MIN) && allowSelfSize) {
      int minSpaceWidth =
        bulletsWidth + betweenBulletsAndLabelsGapThickness + labelsWidth;
      int possibleMinSpaceHeightLabels =
        numLabels * labelsHeight + (numLabels - 1) * betweenLabelsGapThickness;
      int possibleMinSpaceHeightBullets =
        requiredNumBulletsForSpacing * bulletsHeight +
        numBulletGaps * betweenBulletsGapThickness;
      int minSpaceHeight =
        possibleMinSpaceHeightLabels > possibleMinSpaceHeightBullets ?
        possibleMinSpaceHeightLabels : possibleMinSpaceHeightBullets;
      minSpaceHeight =
        customizeSpaceMinHeight && customSpaceMinHeight > minSpaceHeight ?
        customSpaceMinHeight : minSpaceHeight;
      setSpaceSize (MIN,
        new Dimension (minSpaceWidth, minSpaceHeight));
    }

    int numComps = numLabels;
    float compHeight = getSpaceSize (MIN).height / (float)numComps;
    int x = getSpaceSizeLocation (MIN).x;
    int y = getSpaceSizeLocation (MIN).y;
    int labelsX = 0, labelsY = 0, bulletsX = 0, bulletsY = 0;
    for (int i = 0; i < numComps; ++i) {

      labels[i].setAutoJustifys (false, false);
      if (bulletsRelation == LEFT) {
        labelsX = x + bulletsWidth + betweenBulletsAndLabelsGapThickness;
        bulletsX = x;
      }
      else {
        labelsX = x + labelsWidth - labels[i].getSpaceSize(MIN).width;
        bulletsX = x + labelsWidth + betweenBulletsAndLabelsGapThickness;
      }
      labelsY = (int)(y + i * compHeight +
        (compHeight - labels[i].getSpaceSize (MIN).height) / 2f);
      labels[i].setSpaceSizeLocation (MIN, new Point (labelsX, labelsY));
      if (i < numBullets) {
        if (bulletsAlignment == CENTERED) {
          bulletsY =
            (int)(y + i * compHeight + (compHeight - bulletsHeight) / 2f);
        }
        else {
          bulletsY =
            (int)(y + (i + 1) * compHeight - bulletsHeight / 2f);
        }
        bullets[i] = new Rectangle();
        bullets[i].setLocation (bulletsX, bulletsY);
        bullets[i].setSize (bulletsWidth, bulletsHeight);
      }
    }
  }
}