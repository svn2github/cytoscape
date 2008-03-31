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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;


/**
 * A data structure for holding the properties common to all Object2D objects.
 * A Object2D object is one with an encolosing area and title.
 * Pass this to any number of Object2D objects.
 */
public final class Object2DProperties extends Properties {


  /**
   * Signifies left.
   */
  public static final int LEFT = 0;

  /**
   * Signifies right.
   */
  public static final int RIGHT = 1;

  /**
   * Signifies top.
   */
  public static final int TOP = 2;

  /**
   * Signifies bottom.
   */
  public static final int BOTTOM = 3;

  /**
   * Signifies none.
   */
  public static final int NONE = 6;

  /**
   * The default is true.
   */
  public static final boolean OBJECT_MAGNIFY_WHEN_RESIZE_DEFAULT = true;

  /**
   * The default is true.
   */
  public final static boolean OBJECT_BORDER_EXISTENCE_DEFAULT = true;

  /**
   * The default is 2.
   */
  public final static int OBJECT_BORDER_THICKNESS_MODEL_DEFAULT = 2;

  /**
   * The default is Color.black.
   */
  public final static Color OBJECT_BORDER_COLOR_DEFAULT = Color.black;

  /**
   * The default is true.
   */
  public final static boolean OBJECT_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 5.
   */
  public final static int OBJECT_GAP_THICKNESS_MODEL_DEFAULT = 5;

  /**
   * The default is true.
   */
  public final static boolean OBJECT_BACKGROUND_EXISTENCE_DEFAULT = true;

  /**
   * The default is new Color (215, 215, 215).
   */
  public final static Color OBJECT_BACKGROUND_COLOR_DEFAULT = new Color (215, 215, 255);

  /**
   * The default is TOP.
   */
  public final static int OBJECT_BACKGROUND_LIGHT_SOURCE_DEFAULT = TOP;

  /**
   * The default is true.
   */
  public final static boolean OBJECT_TITLE_EXISTENCE_DEFAULT = true;

  /**
   * The default is "".
   */
  public final static String OBJECT_TITLE_TEXT_DEFAULT = "";

  /**
   * The default is 12.
   */
  public final static int OBJECT_TITLE_FONT_POINT_MODEL_DEFAULT = 12;

  /**
   * The default is "SansSerif".
   */
  public final static String OBJECT_TITLE_FONT_NAME_DEFAULT = "SansSerif";

  /**
   * The default is Color.black.
   */
  public final static Color OBJECT_TITLE_FONT_COLOR_DEFAULT = Color.black;

  /**
   * The default is Font.PLAIN.
   */
  public final static int OBJECT_TITLE_FONT_STYLE_DEFAULT = Font.PLAIN;

  /**
   * The default is true.
   */
  public final static boolean OBJECT_TITLE_BETWEEN_REST_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public final static int OBJECT_TITLE_BETWEEN_REST_GAP_THICKNESS_MODEL_DEFAULT = 3;


  private boolean objectMagnifyWhenResize;
  private boolean objectBorderExistence;
  private int objectBorderThicknessModel;
  private Color objectBorderColor;
  private boolean objectGapExistence;
  private int objectGapThicknessModel;
  private boolean objectBackgroundExistence;
  private Color objectBackgroundColor;
  private int objectBackgroundLightSource;
  private boolean objectTitleExistence;
  private String objectTitleText;
  private int objectTitleFontPointModel;
  private String objectTitleFontName;
  private Color objectTitleFontColor;
  private int objectTitleFontStyle;
  private boolean objectTitleBetweenRestGapExistence;
  private int objectTitleBetweenRestGapThicknessModel;

  private boolean needsUpdate = true;
  private final Vector object2DVector = new Vector (5, 5);
  private final Vector needsUpdateVector = new Vector (5, 5);


  /**
   * Creates a Object2DProperties object with the documented default values.
   */
  public Object2DProperties() {

    needsUpdate = true;
    setObject2DPropertiesToDefaults();
  }


  /**
   * Creates a Object2DProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param object2DProps The properties to copy.
   */
  public Object2DProperties (Object2DProperties object2DProps) {

    needsUpdate = true;
    setObject2DProperties (object2DProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setObject2DPropertiesToDefaults() {

    needsUpdate = true;
    setObjectMagnifyWhenResize (OBJECT_MAGNIFY_WHEN_RESIZE_DEFAULT);
    setObjectBorderExistence (OBJECT_BORDER_EXISTENCE_DEFAULT);
    setObjectBorderThicknessModel (OBJECT_BORDER_THICKNESS_MODEL_DEFAULT);
    setObjectBorderColor (OBJECT_BORDER_COLOR_DEFAULT);
    setObjectGapExistence (OBJECT_GAP_EXISTENCE_DEFAULT);
    setObjectGapThicknessModel (OBJECT_GAP_THICKNESS_MODEL_DEFAULT);
    setObjectBackgroundExistence (OBJECT_BACKGROUND_EXISTENCE_DEFAULT);
    setObjectBackgroundColor (OBJECT_BACKGROUND_COLOR_DEFAULT);
    setObjectBackgroundLightSource (OBJECT_BACKGROUND_LIGHT_SOURCE_DEFAULT);
    setObjectTitleExistence (OBJECT_TITLE_EXISTENCE_DEFAULT);
    setObjectTitleText (OBJECT_TITLE_TEXT_DEFAULT);
    setObjectTitleFontPointModel (OBJECT_TITLE_FONT_POINT_MODEL_DEFAULT);
    setObjectTitleFontName (OBJECT_TITLE_FONT_NAME_DEFAULT);
    setObjectTitleFontColor (OBJECT_TITLE_FONT_COLOR_DEFAULT);
    setObjectTitleFontStyle (OBJECT_TITLE_FONT_STYLE_DEFAULT);
    setObjectTitleBetweenRestGapExistence (OBJECT_TITLE_BETWEEN_REST_GAP_EXISTENCE_DEFAULT);
    setObjectTitleBetweenRestGapThicknessModel (
      OBJECT_TITLE_BETWEEN_REST_GAP_THICKNESS_MODEL_DEFAULT);
  }


  /**
   * Sets all properties to be the values of another Object2DProperties object.
   * The copying is a deep copy.
   * @param object2DProps The properties to copy.
   */
  public final void setObject2DProperties (Object2DProperties object2DProps) {

    needsUpdate = true;
    setObjectMagnifyWhenResize (object2DProps.getObjectMagnifyWhenResize());
    setObjectBorderExistence (object2DProps.getObjectBorderExistence());
    setObjectBorderThicknessModel (object2DProps.getObjectBorderThicknessModel());
    setObjectBorderColor (object2DProps.getObjectBorderColor());
    setObjectGapExistence (object2DProps.getObjectGapExistence());
    setObjectGapThicknessModel (object2DProps.getObjectGapThicknessModel());
    setObjectBackgroundExistence (object2DProps.getObjectBackgroundExistence());
    setObjectBackgroundColor (object2DProps.getObjectBackgroundColor());
    setObjectBackgroundLightSource (object2DProps.getObjectBackgroundLightSource());
    setObjectTitleExistence (object2DProps.getObjectTitleExistence());
    setObjectTitleText (object2DProps.getObjectTitleText());
    setObjectTitleFontPointModel (object2DProps.getObjectTitleFontPointModel());
    setObjectTitleFontName (object2DProps.getObjectTitleFontName());
    setObjectTitleFontColor (object2DProps.getObjectTitleFontColor());
    setObjectTitleFontStyle (object2DProps.getObjectTitleFontStyle());
    setObjectTitleBetweenRestGapExistence (object2DProps.getObjectTitleBetweenRestGapExistence());
    setObjectTitleBetweenRestGapThicknessModel (
      object2DProps.getObjectTitleBetweenRestGapThicknessModel());
  }


  /**
   * Sets whether a object's components will grow or shrink as the size of
   * the space allocated to the object grows or shrinks.
   * The Object's preferred size will be the model size (i.e. the size at which it isn't magnified).
   * @param magnify If true, the object will be magnified on resize.
   */
  public final void setObjectMagnifyWhenResize (boolean magnify) {

    needsUpdate = true;
    objectMagnifyWhenResize = magnify;
  }


  /**
   * Sets whether a border around the object will exist.
   * @param existence If true, then a object border exists.
   */
  public final void setObjectBorderExistence (boolean existence) {

    needsUpdate = true;
    objectBorderExistence = existence;
  }


  /**
   * Sets the thickness of the border for the model size of the object.
   * @param thickness The model thickness of the object's border.
   */
  public final void setObjectBorderThicknessModel (int thickness) {

    needsUpdate = true;
    objectBorderThicknessModel = thickness;
  }


  /**
   * Sets the color of the object's border.
   * @param color The color of the border.
   */
  public final void setObjectBorderColor (Color color) {

    needsUpdate = true;
    objectBorderColor = color;
  }


  /**
   * Sets whether a gap between the border or edge of the object and the object's interior
   * components exists.
   * @param existence If true, then a gap exists.
   */
  public final void setObjectGapExistence (boolean existence) {

    needsUpdate = true;
    objectGapExistence = existence;
  }


  /**
   * Sets the thickness of the object's gap for the model size of the object.
   * @param thickness The model thickness of the object's gap.
   */
  public final void setObjectGapThicknessModel (int thickness) {

    needsUpdate = true;
    objectGapThicknessModel = thickness;
  }


  /**
   * Sets whether the object will have a painted background or not.
   * If not, then the background of the content pane to which the object was
   * added will show through which by default is gray, or if the object was not
   * added to a content pane but only a BufferedImage of the object is obtained,
   * then the background will be the default background of BufferedImage which
   * is black.  The existence of a background can improve performance considerably.
   * @param existence If true, a background for the object will be painted.
   */
  public final void setObjectBackgroundExistence (boolean existence) {

    needsUpdate = true;
    objectBackgroundExistence = existence;
  }


  /**
   * Sets the color of the background for the object.
   * @param color The color of the object's background.
   */
  public final void setObjectBackgroundColor (Color color) {

    needsUpdate = true;
    objectBackgroundColor = color;
  }


  /**
   * Sets the light source of the object's background.
   * If there is a light source, the side of the source will be one shade brighter than the
   * specified background color.  Use the TOP, BOTTOM, LEFT, RIGHT, and NONE fields.
   * @param source The source of the light.
   */
  public final void setObjectBackgroundLightSource (int source) {

    needsUpdate = true;
    objectBackgroundLightSource = source;
  }


  /**
   * Sets whether the object is to have a title.
   * @param existence If true, then the object will have a title.
   */
  public final void setObjectTitleExistence (boolean existence) {

    needsUpdate = true;
    objectTitleExistence = existence;
  }


  /**
   * Sets the text for the object's title.
   * @param text The text for the object's title.
   */
  public final void setObjectTitleText (String text) {

    needsUpdate = true;
    objectTitleText = text;
  }


  /**
   * Sets the point of the font of the title for the object at its model size.
   * @param point The model font point for the object's title.
   */
  public final void setObjectTitleFontPointModel (int point) {

    needsUpdate = true;
    objectTitleFontPointModel = point;
  }


  /**
   * Sets the name of the font for the object's title.
   * Accepts all values accepted by java.awt.Font.
   * @param name The name of the font for the object's title.
   */
  public final void setObjectTitleFontName (String name) {

    needsUpdate = true;
    objectTitleFontName = name;
  }


  /**
   * Sets the color of the font for the object's title.
   * @param color The color of the font for the object's title.
   */
  public final void setObjectTitleFontColor (Color color) {

    needsUpdate = true;
    objectTitleFontColor = color;
  }


  /**
   * Sets the style of the font for the object's title.
   * Accepts all values that java.awt.Font accepts.
   * @param style The style of the font for the object's title.
   */
  public final void setObjectTitleFontStyle (int style) {

    needsUpdate = true;
    objectTitleFontStyle = style;
  }


  /**
   * Sets whether a gap below the title and the rest of the object's components exists.
   * @param existence If true, then a gap exists.
   */
  public final void setObjectTitleBetweenRestGapExistence (boolean existence) {

    needsUpdate = true;
    objectTitleBetweenRestGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap below the title and the rest of the object's components for
   * the object's model size.
   * @param thickness The model thickness of the gap.
   */
  public final void setObjectTitleBetweenRestGapThicknessModel (int thickness) {

    needsUpdate = true;
    objectTitleBetweenRestGapThicknessModel = thickness;
  }


  /**
   * Gets whether a object's components will grow or shrink as the size of the space allocated to
   * the object grows or shrinks.
   * @return If true, the object will be magnified on resize.
   */
  public final boolean getObjectMagnifyWhenResize() {
    return objectMagnifyWhenResize;
  }


  /**
   * Gets whether a border around the object will exist.
   * @return If true, then a object border exists.
   */
  public final boolean getObjectBorderExistence() {
    return objectBorderExistence;
  }


  /**
   * Gets the thickness of the border for the model size of the object.
   * @return The model thickness of the object's border.
   */
  public final int getObjectBorderThicknessModel() {
    return objectBorderThicknessModel;
  }


  /**
   * Gets the color of the object's border.
   * @return The color of the border.
   */
  public final Color getObjectBorderColor() {
    return objectBorderColor;
  }


  /**
   * Gets whether a gap between the border or edge of the object and the object's interior components
   * exists.
   * @return If true, then a gap exists.
   */
  public final boolean getObjectGapExistence() {
    return objectGapExistence;
  }


  /**
   * Gets the thickness of the object's gap for the model size of the object.
   * @return The model thickness of the object's gap.
   */
  public final int getObjectGapThicknessModel() {
    return objectGapThicknessModel;
  }


  /**
   * Gets whether the object will have a painted background or not.
   * If not, then the background of the content pane to which the object was
   * added will show through which by default is gray, or if the object was not
   * added to a content pane but only a BufferedImage of the object is obtained,
   * then the background will be the default background of BufferedImage which
   * is black.  Painting the background improves performance considerably.
   * @return If true, a background for the object will be painted.
   */
  public final boolean getObjectBackgroundExistence() {
    return objectBackgroundExistence;
  }


  /**
   * Gets the color of the background for the object.
   * @return The color of the object's background.
   */
  public final Color getObjectBackgroundColor() {
    return objectBackgroundColor;
  }


  /**
   * Gets the light source of the object's background.
   * If there is a light source, the side of the source will be one shade brighter than the
   * specified background color.  Use the TOP, BOTTOM, LEFT, RIGHT, and NONE fields.
   * @return The source of the light.
   */
  public final int getObjectBackgroundLightSource() {
    return objectBackgroundLightSource;
  }


  /**
   * Gets whether the object is to have a title.
   * @return If true, then the object will have a title.
   */
  public final boolean getObjectTitleExistence() {
    return objectTitleExistence;
  }


  /**
   * Gets the text for the object's title.
   * @return The text for the object's title.
   */
  public final String getObjectTitleText() {
    return objectTitleText;
  }


  /**
   * Gets the point of the font of the title for the object at its model size.
   * @return The model font point for the object's title.
   */
  public final int getObjectTitleFontPointModel() {
    return objectTitleFontPointModel;
  }


  /**
   * Gets the name of the font for the object's title.
   * Accepts all values accepted by java.awt.Font.
   * @return The name of the font for the object's title.
   */
  public final String getObjectTitleFontName() {
    return objectTitleFontName;
  }


  /**
   * Gets the color of the font for the object's title.
   * @return The color of the font for the object's title.
   */
  public final Color getObjectTitleFontColor() {
    return objectTitleFontColor;
  }


  /**
   * Gets the style of the font for the object's title.
   * Accepts all values that java.awt.Font accepts.
   * @return The style of the font for the object's title.
   */
  public final int getObjectTitleFontStyle() {
    return objectTitleFontStyle;
  }


  /**
   * Gets whether a gap below the title and the rest of the object's components exists.
   * @return If true, then a gap exists.
   */
  public final boolean getObjectTitleBetweenRestGapExistence() {
    return objectTitleBetweenRestGapExistence;
  }


  /**
   * Gets the thickness of the gap below the title and the rest of the object's components for
   * the object's model size.
   * @return The model thickness of the gap.
   */
  public final int getObjectTitleBetweenRestGapThicknessModel() {
    return objectTitleBetweenRestGapThicknessModel;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param object2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getObject2DNeedsUpdate (Object2D object2D) {

    if (needsUpdate) return true;

    int index = -1;
    if ((index = object2DVector.indexOf (object2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }

    return false;
  }


  /**
   * Adds an Object2D to the set of objects using these properties.
   * @param object2D The Object2D to add.
   */
  final void addObject2D (Object2D object2D) {

    if (!object2DVector.contains (object2D)) {
      object2DVector.add (object2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes a Object2D from the set of objects using these properties.
   * @param object2D The Object2D to remove.
   */
  final void removeObject2D (Object2D object2D) {

    int index = -1;
    if ((index = object2DVector.indexOf (object2D)) != -1) {
      object2DVector.remove (index);
      needsUpdateVector.remove (index);
    }
  }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  final boolean validate (boolean debug) {

    if (debug) System.out.println ("Validating Object2DProperties");

    boolean valid = true;

    if (objectBorderThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("ObjectBorderThicknessModel < 0");
    }
    if (objectBorderColor == null) {
      valid = false;
      if (debug) System.out.println ("ObjectBorderColor == null");
    }
    if (objectGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("ObjectGapThicknessModel < 0");
    }
    if (objectBackgroundColor == null) {
      valid = false;
      if (debug) System.out.println ("ObjectBackgroundColor == null");
    }
    if (objectBackgroundLightSource != TOP && objectBackgroundLightSource != BOTTOM &&
      objectBackgroundLightSource != LEFT && objectBackgroundLightSource != RIGHT &&
      objectBackgroundLightSource != NONE) {
      valid = false;
      if (debug) System.out.println ("Problem with ObjectBackgroundLightSource");
    }
    if (objectTitleText == null) {
      valid = false;
      if (debug) System.out.println ("ObjectTitleText == null");
    }
    if (objectTitleFontPointModel < 0) {
      valid = false;
      if (debug) System.out.println ("ObjectTitleFontPointModel < 0");
    }
    if (objectTitleFontName == null || !isFontNameExists (objectTitleFontName)) {
      valid = false;
      if (debug) System.out.println ("Problem with ObjectTitleFontName");
    }
    if (objectTitleFontColor == null) {
      valid = false;
      if (debug) System.out.println ("ObjectTitleFontColor == null");
    }
    if (objectTitleFontStyle != Font.PLAIN && objectTitleFontStyle != Font.ITALIC &&
      objectTitleFontStyle != Font.BOLD && objectTitleFontStyle != (Font.ITALIC|Font.BOLD)) {
      valid = false;
      if (debug) System.out.println ("Problem with ObjectTitleFontStyle");
    }
    if (objectTitleBetweenRestGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("ObjectTitleBetweenRestGapThicknessModel < 0");
    }

    if (debug) {
      if (valid) System.out.println ("Object2DProperties was valid");
      else System.out.println ("Object2DProperties was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this Object2D.
   * @param object2D The object to update.
   */
  final void updateObject2D (Object2D object2D) {

    if (getObject2DNeedsUpdate (object2D)) {

      if (needsUpdate) {
        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }
        needsUpdate = false;
      }

      int index = -1;
      if ((index = object2DVector.indexOf (object2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }
}