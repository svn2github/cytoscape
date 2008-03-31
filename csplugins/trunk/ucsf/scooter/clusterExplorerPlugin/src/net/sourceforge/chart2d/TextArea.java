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
import java.util.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;


/**
 * A customizable text label.  This label has built-int bordering, spacing
 * between borders and text, text rotation, line breaking, auto justification
 * within the borders, growing and shrinking, and auto locating.  Much of this
 * functionality is provided by its super classes.<br>
 * Note:  Does not support null values.  Pass empty strings instead.
 */
final class TextArea extends FontArea {


  private String text;
  private Vector textLayouts;
  private boolean rotateLeft;
  private int textJustification;
  private boolean needsUpdate;


  /**
   * Creates a new text area with the following default values:
   * setText ("");<br>
   * setRotateLeft (false);<br>
   * setTextJustification (CENTER);<br>
   * resetTextAreaModel (true);<br>
   */
  TextArea () {

    setText ("");
    setRotateLeft (false);
    setTextJustification (CENTER);
    resetTextAreaModel (true);
    needsUpdate = true;
  }


  /**
   * Changes the text of this label.
   * @param t The new text.
   */
  final void setText (String t) {

    needsUpdate = true;
    text = t;
  }


  /**
   * Adjusts the rotation of the text within the label.  If not rotated, text
   * runs from left to right, top to bottom of the are.  If rotate, text runs
   * from bottom to top, left to right.  The text is rotated -90 degree.  Even
   * when rotate, the location or origin of this label is always the top left
   * corner of it, however, the text's actual origin is near the bottom left
   * corner.
   * @param r If true, then adjusts settings so that text is rotated.
   */
  final void setRotateLeft (boolean r) {

    needsUpdate = true;
    rotateLeft = r;
  }


  /**
   * Specifies whether text will be (in the case text is not rotated) left,
   * right, or center justified respective to the space within the bordered
   * area.  In the case text is rotated, then bottom, top or center
   * justification is available.  This only adjusts the horizontal
   * justification, or in the case of rotated text, the vertical justification.
   * @param which Which justification for the text.  Possible values, if not
   * rotated: LEFT, RIGHT, CENTER; if rotated: BOTTOM, TOP, CENTER.  Also,
   * if you prefer and when rotated, LEFT and RIGHT may be used to mean BOTTOM
   * and TOP respectively; this program translates them into BOTTOM and TOP for
   * you.
   */
  final void setTextJustification (int which) {

    needsUpdate = true;
    if (which == TOP) textJustification = RIGHT;
    else if (which == BOTTOM) textJustification = LEFT;
    else textJustification = which;
  }


  /**
   * Returns the text of this label.
   * @return The label's text.
   */
  final String getText() {

    return text;
  }


  /**
   * Returns whether this text is rotated left 90 degrees.
   * @return If rotated, then true.
   */
  final boolean getRotateLeft () {

    return rotateLeft;
  }


  /**
   * Indicates whether some property of this class has changed.
   * @return True if some property has changed.
   */
  final boolean getTextAreaNeedsUpdate() {

    return (needsUpdate || getFontAreaNeedsUpdate());
  }


  /**
   * Updates all the variables of all this parent's classes, then all the
   * variables of this class.
   * @param g2D The graphics context under which to make calculations.
   */
  final void updateTextArea (Graphics2D g2D) {

    if (getTextAreaNeedsUpdate()) {
      updateFontArea ();
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
   * @param reset True causes the max model size to be set upon the next max
   * sizing.
   */
  final void resetTextAreaModel (boolean reset) {

    needsUpdate = true;
    resetFontAreaModel (reset);
  }


  /**
   * Paints all of the components of this class.  First all the variables are
   * updated.  Then all the components are painted.
   * @param g2D The graphics context for calculations and painting.
   */
  final void paintComponent (Graphics2D g2D) {

    updateTextArea (g2D);
    super.paintComponent (g2D);

    Color oldColor = g2D.getColor();
    g2D.setColor (getFontColor());

    int delta = 0;
    int count = textLayouts.size();
    for (int i = 0; i < count; ++i) {

      TextLayout layout = (TextLayout)textLayouts.get(i);
      Shape shape = layout.getOutline(new AffineTransform());
      int ascent = (int)Math.abs (shape.getBounds(). y);
      int descent = shape.getBounds().height - ascent;
      int height = ascent + descent;
      int leading = (int)layout.getLeading();

      if (!rotateLeft) {

        int clipHeight = delta + height > getSpaceSize(MIN).height ?
          getSpaceSize(MIN).height - delta : height;
        Rectangle rect = new Rectangle
          (getSpaceSizeLocation (MIN).x, getSpaceSizeLocation (MIN).y + delta,
            getSpaceSize(MIN).width, clipHeight);
        g2D.clip (rect);
        int translateX;
        if (textJustification == LEFT) {
          translateX =
            getSpaceSizeLocation (MIN).x - shape.getBounds().x;
        }
        else if (textJustification == RIGHT) {
          translateX = getSpaceSizeLocation (MIN).x + getSpaceSize (MIN).width -
            shape.getBounds().width - shape.getBounds().x;
        }
        else {
          translateX = getSpaceSizeLocation (MIN).x +
          (getSpaceSize (MIN).width - shape.getBounds().width) / 2 -
            shape.getBounds().x;
        }
        int translateY = getSpaceSizeLocation (MIN).y + delta + ascent;
        g2D.translate (translateX, translateY);
        g2D.fill (shape);

        g2D.setClip (null);
        g2D.translate (-translateX, -translateY);
        delta = delta + height + leading;
      }
      else {

        int clipHeight = delta + height > getSpaceSize(MIN).width ?
          getSpaceSize(MIN).width - delta : height;
        Rectangle rect = new Rectangle
          (getSpaceSizeLocation (MIN).x + delta, getSpaceSizeLocation (MIN).y,
            clipHeight, getSpaceSize(MIN).height);
        g2D.clip (rect);
        int translateX = getSpaceSizeLocation (MIN).x + delta + ascent;
        int translateY;
        if (textJustification == LEFT) {
          translateY = getSpaceSizeLocation (MIN).y + getSpaceSize(MIN).height +
            shape.getBounds().x;
        }
        else if (textJustification == RIGHT) {
          translateY = getSpaceSizeLocation (MIN).y + shape.getBounds().width +
            shape.getBounds().x;
        }
        else {
          translateY = getSpaceSizeLocation (MIN).y +
            (getSpaceSize (MIN).height + shape.getBounds().width) / 2
            + shape.getBounds().x;
        }
        g2D.translate (translateX, translateY);
        g2D.rotate(Math.toRadians(-90d));
        g2D.fill (shape);

        g2D.setClip (null);
        g2D.rotate (Math.toRadians(90d));
        g2D.translate (-translateX, -translateY);
        delta = delta + height + leading;
      }
    }

    g2D.setColor (oldColor);
  }


  private void update (Graphics2D g2D) {

    int greatestAdvance = 0;
    int lineBreaksMeasurement = 0;
    int leading = 0;
    textLayouts = new Vector (0, 1);
    if (text.length() > 0) {
      int wrapping =
        !rotateLeft ? getSpaceSize (MAX).width : getSpaceSize (MAX).height;
      int mockWrapping = wrapping;
      boolean fits = true;
      AttributedString attributedString =
        new AttributedString (text, getFont().getAttributes());
      AttributedCharacterIterator attributedCharacterIterator =
        attributedString.getIterator();
      FontRenderContext fontRenderContext = g2D.getFontRenderContext();
      for(;;) {
        LineBreakMeasurer measurer = new LineBreakMeasurer
          (attributedCharacterIterator, fontRenderContext);
        textLayouts = new Vector (0, 1);
        greatestAdvance = 0;
        lineBreaksMeasurement = 0;
        for (TextLayout layout = measurer.nextLayout (mockWrapping);
          layout != null;
          layout = measurer.nextLayout(mockWrapping)) {
          textLayouts.add (layout);
          Shape shape = layout.getOutline(new AffineTransform());
          int width = shape.getBounds().width;
          greatestAdvance = greatestAdvance < width ? width : greatestAdvance;
          int ascent = (int)Math.abs (shape.getBounds().y);
          int descent = shape.getBounds().height - ascent;
          int height = ascent + descent;
          leading = (int)layout.getLeading();
          lineBreaksMeasurement = lineBreaksMeasurement + height + leading;

        }
        lineBreaksMeasurement -= leading;

        if (lineBreaksMeasurement >
          (!rotateLeft ? getSpaceSize (MAX).height : getSpaceSize (MAX).width))
          {
          fits = false;
          greatestAdvance = 0;
          lineBreaksMeasurement = 0;
          textLayouts = new Vector(0,1);
          break;
        }
        else if (greatestAdvance > wrapping) {
          mockWrapping = mockWrapping - (greatestAdvance - mockWrapping);
          if (mockWrapping <= 0) {
            fits = false;
            greatestAdvance = 0;
            lineBreaksMeasurement = 0;
            textLayouts = new Vector(0,1);
            break;
          }
          //else loop again
        }
        else break; //it fits
      }
    }

    if (!getAutoSize (MIN)) {
      if (!rotateLeft) {
        setSpaceSize (MIN,
          new Dimension (greatestAdvance, lineBreaksMeasurement));
      }
      else {
        setSpaceSize (MIN,
          new Dimension (lineBreaksMeasurement, greatestAdvance));
      }
      updateFontArea();
    }
  }
}