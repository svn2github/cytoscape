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
import java.awt.image.*;
import javax.swing.*;


/**
 * An abstract class for the common methods of Chart2D, Clocks2D, and Progress2D objects.
 * An Object2D object is one with an enclosing area and a title.
 * Changes through its set methods are updated upon next repaint() or getImage() calls.
 */
public abstract class Object2D extends JComponent {


  /**
   * The default is new Dimension (1024, 768).
   */
  public static final Dimension MAX_SIZE_DEFAULT = new Dimension (1024, 768);

  /**
   * The default is new Dimension (1, 1).
   */
  public static final Dimension MIN_SIZE_DEFAULT = new Dimension (1, 1);


  private Object2DProperties object2DProps;
  private boolean needsUpdate;


  /**
   * Creates an Object2D object with its defaults.
   * An Object2DProperties object must be set for this object before it is used.
   */
  public Object2D() {

    needsUpdate = true;
    setMaximumSize (MAX_SIZE_DEFAULT);
    setMinimumSize (MIN_SIZE_DEFAULT);
  }


  /**
   * Sets the Object2DProperties for this Object2D.
   * @param props The Object2DProperties.
   */
  public final void setObject2DProperties (Object2DProperties props) {

    needsUpdate = true;
    props.addObject2D (this);
    if (object2DProps != null) object2DProps.removeObject2D (this);
    object2DProps = props;
  }


  /**
   * Sets a custom preferred size for the chart.
   * This custom size will override the preferred size calculations that normally occurr.
   * If null is passed, the preferred size calculations will be reinstated.
   * @param size  The custom preferred size for this chart.
   */
  public abstract void setPreferredSize (Dimension size);


  /**
   * Sets the maximum size of this object.
   * @param size The maximum size.
   */
  public final void setMaximumSize (Dimension size) {

    needsUpdate = true;
    super.setMaximumSize (size);
  }


  /**
   * Sets the minimum size of this object.
   * @param size The minimum size.
   */
  public final void setMinimumSize (Dimension size) {

    needsUpdate = true;
    super.setMinimumSize (size);
  }


  /**
   * Gets the Object2DProperties for this Object2D.
   * @return The Object2DProperties.
   */
  public final Object2DProperties getObject2DProperties() {
    return object2DProps;
  }


  /**
   * Gets the preferred size of the chart.
   * The preferred size is within the maximum and minimum sizes of the chart.
   * Much calculation is performed when calling this method.
   * @return The preferred minimum size of the chart.
   */
  public abstract Dimension getPreferredSize();


  /**
   * Gets whether this object needs to be updated with new properties.
   * @return If true then needs update.
   */
  final boolean getNeedsUpdateObject2D() {
    return (needsUpdate || object2DProps.getObject2DNeedsUpdate (this));
  }


  /**
   * Gets the TitledArea of this Object2D.
   * @return The TitledArea.
   */
  abstract TitledArea getObjectArea();


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  final boolean validateObject2D (boolean debug) {

    if (debug) System.out.println ("Validating Object2D");

    boolean valid = true;

    if (object2DProps == null) {
      valid = false;
      if (debug) System.out.println ("Object2DProperties is null");
    }
    else if (!object2DProps.validate (debug)) valid = false;

    if (getMaximumSize() == null ||
      getMaximumSize().height < 1 || getMaximumSize().width < 1 ||
      getMaximumSize().height < getMinimumSize().height ||
      getMaximumSize().width < getMinimumSize().width) {
      valid = false;
      if (debug) System.out.println ("Problem with maximum size");
    }
    if (getMinimumSize() == null ||
      getMinimumSize().height < 1 || getMinimumSize().width < 1) {
      valid = false;
      if (debug) System.out.println ("Problem with minimum size");
    }

    if (debug) {
      if (valid) System.out.println ("Object2D was valid");
      else System.out.println ("Object2D was invalid");
    }

    return valid;
  }


  /**
   * Updates this object.
   */
  final void updateObject2D() {

    if (getNeedsUpdateObject2D()) {

      needsUpdate = false;
      object2DProps.updateObject2D (this);

      TitledArea object = (TitledArea)getObjectArea();
      object.setAutoSizes (!object2DProps.getObjectMagnifyWhenResize(), true);
      object.setBorderExistence (object2DProps.getObjectBorderExistence());
      object.setBorderThicknessModel (object2DProps.getObjectBorderThicknessModel());
      object.setBorderColor (object2DProps.getObjectBorderColor());
      object.setGapExistence (object2DProps.getObjectGapExistence());
      object.setGapThicknessModel (object2DProps.getObjectGapThicknessModel());
      object.setBackgroundExistence (object2DProps.getObjectBackgroundExistence());
      object.setBackgroundColor (object2DProps.getObjectBackgroundColor());
      object.setLightSource (object2DProps.getObjectBackgroundLightSource());
      object.setTitleExistence (object2DProps.getObjectTitleExistence());
      object.setTitle (object2DProps.getObjectTitleText());
      object.setFontPointModel (object2DProps.getObjectTitleFontPointModel());
      object.setFontName (object2DProps.getObjectTitleFontName());
      object.setFontColor (object2DProps.getObjectTitleFontColor());
      object.setFontStyle (object2DProps.getObjectTitleFontStyle());
      object.setBetweenTitleAndSpaceGapExistence (
        object2DProps.getObjectTitleBetweenRestGapExistence());
      object.setBetweenTitleAndSpaceGapThicknessModel (
        object2DProps.getObjectTitleBetweenRestGapThicknessModel());
    }
  }


  /**
   * Gets a buffered image of the chart.
   * @return An image of this chart
   */
  public abstract BufferedImage getImage();


  /**
   * Causes the object to reinintialize to it's preferred size.
   */
  public abstract void pack();


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  public abstract boolean validate (boolean debug);
}