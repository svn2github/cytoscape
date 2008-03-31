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
 * A bordered area with a title and auto calculations for left over space.  This
 * class uses all the customizability of the bordered area class, adds a text
 * area for the title, automatic gap below title functionality and title within
 * area justification.  The title can be located at the top, bottom, left or
 * right, and sometimes centered.  With
 * any of these scenarios, the title can be rotated left.  The left over space
 * will be computed and is availabe through a get method.<br>
 * Note:  Do not pass any null values; instead pass an empty string if need be.
 */
class TitledArea extends FontArea {


  private boolean titleAutoLocate;
  private boolean titleExistence;
  private TextArea title;
  private Rectangle maxEntitledSpaceBounds;
  private Rectangle minEntitledSpaceBounds;
  private Color titleBackgroundColor;
  private boolean betweenTitleAndSpaceGapExistence;
  private int betweenTitleAndSpaceGapThicknessModel;
  private int betweenTitleAndSpaceGapThickness;
  private Dimension minUsedSpaceSize;
  private boolean needsUpdate;


  /**
   * Creates a new titled area with the default values:<br>
   * setTitleExistence (true);<br>
   * setFontPointModel (14);<br>
   * setBetweenTitleAndSpaceGapExistence (true);<br>
   * setBetweenTitleAndSpaceGapThicknessModel (5);<br>
   * setTitleJustifications (CENTER, TOP);<br>
   * setTitleAutoLocate (true);<br>
   * title.setAutoJustifys (false, false);<br>
   * title.setBorderExistences (false, false, false, false);<br>
   * title.setGapExistences (false, false, false, false);<br>
   * title.setBackgroundExistence (false);<br>
   * resetTitledAreaModel (true);<br>
   */
  TitledArea() {

    title = new TextArea();
    maxEntitledSpaceBounds = new Rectangle();
    minEntitledSpaceBounds = new Rectangle();

    setTitleExistence (true);
    setFontPointModel (14);
    setBetweenTitleAndSpaceGapExistence (true);
    setBetweenTitleAndSpaceGapThicknessModel (5);
    setTitleJustifications (CENTER, TOP);
    setTitleAutoLocate (true);

    title.setAutoJustifys (false, false);
    title.setBorderExistences (false, false, false, false);
    title.setGapExistences (false, false, false, false);
    title.setBackgroundExistence (false);
    resetTitledAreaModel (true);
    needsUpdate = true;
  }


  /**
   * Changes the existence of the title; whether the title exists or not.
   * Otherwise, title will not be included in calculations and will not be
   * painted.
   * @param existence If true, then title exists.
   */
  final void setTitleExistence (boolean existence) {

    needsUpdate = true;
    titleExistence = existence;
  }


  /**
   * Changes the text of the title.
   * @param title The new text of this title.
   */
  final void setTitle (String title) {

    needsUpdate = true;
    this.title.setText (title);
  }


  /**
   * Changes whether the title will locate itself automatically within
   * the area according to the title justifications.  Otherwise, the title
   * location must be set manually using the title set location method.
   * @param auto true if the title will locate itself
   */
  final void setTitleAutoLocate (boolean auto) {

    needsUpdate = true;
    titleAutoLocate = auto;
  }


  /**
   * Changes the location of the title.  The top, left part of the title
   * no matter what title orientation.
   * @param location The top left part of the title's location.
   */
  final void setTitleLocation (Point location) {

    needsUpdate = true;
    title.setSpaceSizeLocation (MIN, location);
  }


  /**
   * Changes whether this title should be rotated left of not.
   * When rotating left, the text will be rotated -90 degrees.  However, the
   * origin and the size of the label will not be rotated.  It will encapsulate
   * the newly rotated text.
   * @param rotate If true, then text will be rotated -90 degrees whenever
   * painted.  However, values are updated immediately.
   */
  final void setTitleRotateLeft (boolean rotate) {

    needsUpdate = true;
    title.setRotateLeft (rotate);
  }


  /**
   * Specifies the horizontal and vertical justification for the title text
   * respective to the size of the area.  Note:  A non rotated title cannot
   * be vertically centered.  And a rotated title cannot be horizontally
   * centered.
   * @param horizontal How to justify the title horizontally.  Possible values
   * area LEFT, RIGHT, and CENTER.
   * [see note above for more detail about centering]
   * @param vertical How to justify the title vertically.  Possible values area
   * TOP, BOTTOM, and CENTER. [see note above for more detail about centering]
   */
  final void setTitleJustifications (int horizontal, int vertical) {

    needsUpdate = true;
    title.setJustifications (horizontal, vertical);
  }


  /**
   * Specifies whether there should exist a gap between title and the available
   * space.  Part of the functionality of this class is to calculate space not
   * used by the title.  Setting this gap, makes the available space less so
   * drawing can begin exactly in the space, and look good becuase its spaced
   * nicely from the title.
   * @param existence If true, then gap is subtracted from space; else gap model
   * thickness is ignored.
   */
  final void setBetweenTitleAndSpaceGapExistence (boolean existence) {

    needsUpdate = true;
    betweenTitleAndSpaceGapExistence = existence;
  }


  /**
   * Specifies how large the model gap should be.  Part of the functionality of
   * this class is to calculate space not
   * used by the title.  This gap, makes the available space less so
   * drawing can begin exactly in the space, and look good becuase its spaced
   * nicely from the title.
   * @param gap The thickness of the model gap.  If model max size autosizing is
   * disabled, then a ratio based on max size / model max size will be applied
   * to this thickness to obtain the actual thickness; otherwise, the thickness
   * will be this model thickness.  Also, if gap existence is false, this
   * thickness will be ignored.
   */
  final void setBetweenTitleAndSpaceGapThicknessModel (int gap) {

    needsUpdate = true;
    betweenTitleAndSpaceGapThicknessModel = gap;
  }


  /**
   * Returns the model gap thickness between the title and the bounds.
   * @return The thickness of the model gap.
   */
  final int getBetweenTitleAndSpaceGapThicknessModel() {

    return betweenTitleAndSpaceGapThicknessModel;
  }


  /**
   * Returns the available space and location of that space.  The available
   * space is the area less the title and the gap.  Updates all variables
   * before calculating the bounds.
   * @param g2D The graphics context for calculations and painting.
   * @return The bounds of the available space.
   */
  final Rectangle getMaxEntitledSpaceBounds (Graphics2D g2D) {

    updateTitledArea (g2D);
    return maxEntitledSpaceBounds;
  }


  /**
   * Returns the available space and location of that space.  The available
   * space is the area less the title and the gap.  Updates all variables
   * before calculating the bounds.
   * @param g2D The graphics context for calculations and painting.
   * @return The bounds of the available space.
   */
  final Rectangle getMinEntitledSpaceBounds (Graphics2D g2D) {
    updateTitledArea (g2D);
    return minEntitledSpaceBounds;
  }


  /**
   * Returns the existence of the title; whether the title exists or not.
   * @return If true, then title exists.
   */
  final boolean getTitleExistence () {

    return titleExistence;
  }


  /**
   * Gets the text of the title.
   * @return The new text of this title.
   */
  final String getTitleText() {

    return title.getText();
  }


  /**
   * Gets the title.
   * @return The title's TextArea.
   */
  final TextArea getTitle() {
    return title;
  }


  /**
   * Returns the size of this title.  Even if the title is rotated, the width
   * of this dimension will correspond to width on the monitor screen, and
   * not height.  The same is true for the height of the dimension.
   * @return The size of the title.
   * @param which Which size MAXMODEL, MAX, or MIN.
   * @param g2D The graphics context for calculations and painting.
   */
  final Dimension getTitleSize (int which, Graphics2D g2D) {

    updateTitledArea (g2D);
    return title.getSize (which);
  }


  /**
   * Returns the horizontal or vertical justification of this title within the
   * area.
   * @param which Which type of justification to return.  Possible values are
   * HORIZONTAL and VERTICAL.
   * @return The justification of this title.  Possible values of
   * horizontal justification area LEFT, RIGHT, and CENTER.  Possible values of
   * vertical justification are TOP, BOTTOM, and CENTER.
   */
  final int getTitleJustifications (int which) {

    return title.getJustifications (which);
  }


  /**
   * Indicates whether the title will locate itself automatically within
   * the area according to the title justifications.  Otherwise, the title
   * location must be set manually using the title set location method.
   * @return True if the title will locate itself
   */
  final boolean getTitleAutoLocate () {

    return titleAutoLocate;
  }


  /**
   * Returns the gap between the title and the available space.  This is not
   * the model gap thickness but the actual thickness, after applying the ratio
   * when relevant.  All variables area updated before returning this size.
   * @param g2D The graphics context to use for calculations.
   * @return The thickness of the gap between the title and the available space.
   */
  final int getBetweenTitleAndSpaceGapThickness (Graphics2D g2D) {

    updateTitledArea (g2D);
    return betweenTitleAndSpaceGapThickness;
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getTitledAreaNeedsUpdate() {

    return (needsUpdate || getFontAreaNeedsUpdate());
  }


  /**
   * Updates all the variables in this parent's classes, then all of this'
   * variables.
   * @param g2D The graphics context to use for calculations.
   */
  final void updateTitledArea (Graphics2D g2D) {

    if (getTitledAreaNeedsUpdate()) {
      updateFontArea();
      update (g2D);
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
   * @param reset True sets the max model size on the next max sizing.
   */
  final void resetTitledAreaModel (boolean reset) {

    needsUpdate = true;
    resetFontAreaModel (reset);
    title.resetTextAreaModel (reset);
  }


  /**
   * Paints all the components of this class.  First all variables are updated.
   * @param g2D The graphics context for calculations and painting.
   */
  void paintComponent (Graphics2D g2D) {

    updateTitledArea (g2D);
    super.paintComponent (g2D);
    title.paintComponent (g2D);
  }


  private void update (Graphics2D g2D) {
    updateMaxTitle();
    title.updateTextArea (g2D);
    updateGap();
    updateMaxBounds (g2D);
    updateMinBounds (g2D);
    updateMinTitle();
  }


  private void updateMaxTitle() {

    if (titleExistence) {
      title.setCustomRatio (WIDTH, true, getRatio (WIDTH));
      title.setCustomRatio (HEIGHT, true, getRatio (HEIGHT));
      title.setAutoSizes (getAutoSize (MAXMODEL), false);
      title.setSizeLocation (MAX, new Point (
        getSpaceSizeLocation (MAX).x, getSpaceSizeLocation (MAX).y));
      Dimension titleSize = new Dimension (
        getSpaceSize (MAX).width, getSpaceSize (MAX).height);
      title.setSize (MAX, titleSize);
      title.setFontPointModel (getFontPointModel());
      title.setFontColor (getFontColor());
      title.setFontName (getFont().getName());
      title.setFontStyle (getFont().getStyle());
    }
    else title.setSize (MAX, new Dimension());
  }


  private void updateGap() {

    if (betweenTitleAndSpaceGapExistence) {

      float ratio;
      int available;
      if (!title.getRotateLeft()) {
        ratio = getRatio (HEIGHT);
        available = getSpaceSize (MAX).height - title.getSize (MIN).height;
      }
      else {
        ratio = getRatio (WIDTH);
        available = getSpaceSize (MAX).width - title.getSize (MIN).width;
      }

      if (titleExistence &&
        title.getSize (MIN).width > 0 && title.getSize (MIN).height > 0) {
        betweenTitleAndSpaceGapThickness =
          applyRatio (betweenTitleAndSpaceGapThicknessModel, ratio);
        betweenTitleAndSpaceGapThickness =
          betweenTitleAndSpaceGapThickness < available ?
          betweenTitleAndSpaceGapThickness : available;
      }
      else betweenTitleAndSpaceGapThickness = 0;
    }

    else betweenTitleAndSpaceGapThickness = 0;
  }


  private void updateMaxBounds (Graphics2D g2D) {

    int spaceX;
    int spaceY;
    int spaceWidth;
    int spaceHeight;
    if (!title.getRotateLeft()) {
      spaceX = getSpaceSizeLocation (MAX).x;
      spaceWidth = getSpaceSize (MAX).width;
      spaceHeight = getSpaceSize (MAX).height -
        (title.getSize (MIN).height + betweenTitleAndSpaceGapThickness);
      if (title.getJustifications (VERTICAL) == TOP) {
        spaceY = getSpaceSizeLocation (MAX).y +
        title.getSize (MIN).height + betweenTitleAndSpaceGapThickness;
      }
      else spaceY = getSpaceSizeLocation (MAX).y;
    }
    else {
      spaceY = getSpaceSizeLocation (MAX).y;
      spaceWidth = getSpaceSize (MAX).width -
        (title.getSize (MIN).width + betweenTitleAndSpaceGapThickness);
      spaceHeight = getSpaceSize (MAX).height;
      if (title.getJustifications (HORIZONTAL) == LEFT) {
        spaceX = getSpaceSizeLocation (MAX).x +
        title.getSize (MIN).width + betweenTitleAndSpaceGapThickness;
      }
      else spaceX = getSpaceSizeLocation (MAX).x;
    }
    maxEntitledSpaceBounds.setBounds (spaceX, spaceY, spaceWidth, spaceHeight);
  }


  private void updateMinBounds (Graphics2D g2D) {

    int spaceX;
    int spaceY;
    int spaceWidth;
    int spaceHeight;
    if (!title.getRotateLeft()) {
      spaceX = getSpaceSizeLocation (MIN).x;
      spaceWidth = getSpaceSize (MIN).width;
      spaceHeight = getSpaceSize (MIN).height -
        (title.getSize (MIN).height + betweenTitleAndSpaceGapThickness);
      if (title.getJustifications (VERTICAL) == TOP) {
        spaceY = getSpaceSizeLocation (MIN).y +
        title.getSize (MIN).height + betweenTitleAndSpaceGapThickness;
      }
      else spaceY = getSpaceSizeLocation (MIN).y;
    }
    else {
      spaceY = getSpaceSizeLocation (MIN).y;
      spaceWidth = getSpaceSize (MIN).width -
        (title.getSize (MIN).width + betweenTitleAndSpaceGapThickness);
      spaceHeight = getSpaceSize (MIN).height;
      if (title.getJustifications (HORIZONTAL) == LEFT) {
        spaceX = getSpaceSizeLocation (MIN).x +
        title.getSize (MIN).width + betweenTitleAndSpaceGapThickness;
      }
      else spaceX = getSpaceSizeLocation (MIN).x;
    }
    minEntitledSpaceBounds.setBounds (spaceX, spaceY, spaceWidth, spaceHeight);
  }


  private void updateMinTitle() {

    if (titleAutoLocate) {

      int betweenWidth = title.getSizeLocation (MIN).x -
        title.getSpaceSizeLocation (MIN).x;
      int spaceX;
      if (title.getJustifications (HORIZONTAL) == LEFT) {
        spaceX = getSpaceSizeLocation (MIN).x + betweenWidth;
      }
      else if (title.getJustifications (HORIZONTAL) == RIGHT) {
        spaceX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width -
          title.getSpaceSize (MIN).width - betweenWidth;
      }
      else {
        spaceX = getSpaceSizeLocation(MIN).x +
          (getSpaceSize (MIN).width - title.getSpaceSize (MIN).width) / 2;
      }

      int betweenHeight =
        title.getSpaceSizeLocation (MIN).y - title.getSizeLocation (MIN).y;
      int spaceY;
      if (title.getJustifications (VERTICAL) == TOP) {
        spaceY = getSpaceSizeLocation (MIN).y + betweenHeight;
      }
      else if (title.getJustifications (VERTICAL) == BOTTOM) {
        spaceY = getSpaceSizeLocation (MIN).y + getSpaceSize (MIN).height -
          title.getSpaceSize (MIN).height - betweenHeight;
      }
      else {
        spaceY = getSpaceSizeLocation(MIN).y +
          (getSpaceSize (MIN).height - title.getSpaceSize (MIN).height) / 2;
      }
      title.setSpaceSizeLocation (MIN, new Point (spaceX, spaceY));
    }
  }
}