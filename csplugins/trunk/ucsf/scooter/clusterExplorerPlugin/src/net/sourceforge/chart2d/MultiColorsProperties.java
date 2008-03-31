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


/**
 * A data structure for holding the properties common to all sets of colors.
 * Charting requires sets of colors (one color for each set for example).
 * Color generation can be done manually or automatically.
 * Even if manually, some automation adds convenience.
 * A MultiColorsProperties takes care of the generation of color sets either way.
 * Pass this to any number of color requiring objects.
 */
public final class MultiColorsProperties {


  /**
   *  NATURAL colors are more pure than PASTEL colors.
   */
  public static final int NATURAL = 0;

  /**
   *  PASTEL colors have other colors besides their primary ones added to them.
   */
  public static final int PASTEL = 1;


  /**
   * CHART2D colors are a set of 12 colors selected by a graphic designer.
   */
  public static final int CHART2D = 2;

  /**
   * The default is false.
   */
  public static final boolean COLORS_CUSTOMIZE_DEFAULT = false;

  /**
   * The default is new Color[0].
   */
  public static final Color[] COLORS_CUSTOM_DEFAULT = new Color[0];

  /**
   * The default is CHART2D.
   */
  public static final int COLORS_TYPE_DEFAULT = CHART2D;


  private static final int NUMCOLORS = 6;
  private static final int NUMCOLORDEGREES = 2;
  private static final int DEGREEVARIATION = 40;


  private boolean customize;
  private Color[] customArray;
  private int colorsType;
  private Color[] colorsArray;
  private boolean needsUpdate;
  private Vector object2DVector = new Vector (5, 5);
  private Vector needsUpdateVector = new Vector (5, 5);


  /**
   * Creates a MultiColorsProperties object with the documented default values.
   */
  public MultiColorsProperties() {

    needsUpdate = true;
    setMultiColorsPropertiesToDefaults();
  }


  /**
   * Creates a MultiColorsProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param multiColorsProps The properties to copy.
   */
  public MultiColorsProperties (MultiColorsProperties multiColorsProps) {

    needsUpdate = true;
    setMultiColorsProperties (multiColorsProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setMultiColorsPropertiesToDefaults() {

    needsUpdate = true;
    setColorsCustomize (COLORS_CUSTOMIZE_DEFAULT);
    setColorsCustom (COLORS_CUSTOM_DEFAULT);
    setColorsType (COLORS_TYPE_DEFAULT);
  }


  /**
   * Sets all properties to be the values of another MultiColorsProperties object.
   * The copying is a deep copy.
   * @param multiColorsProps The properties to copy.
   */
  public final void setMultiColorsProperties (MultiColorsProperties multiColorsProps) {

    needsUpdate = true;
    setColorsCustomize (multiColorsProps.getColorsCustomize());
    setColorsCustom (multiColorsProps.getColorsCustom());
    setColorsType (multiColorsProps.getColorsType());
  }


  /**
   * Sets whether the colors are to be manual (custom) or to be automatically selected.
   * If true, then use the setColorsCustom method to pass in an array of Color objects.
   * @param cust If true, then manual colors are used.
   */
  public final void setColorsCustomize (boolean cust) {

    needsUpdate = true;
    customize = cust;
  }


  /**
   * Sets the custom color array.
   * To be used, the setColorsCustomize method must be set to true.
   * @param colors The custom color array.
   */
  public final void setColorsCustom (Color[] colors) {

    needsUpdate = true;
    customArray = colors;
  }


  /**
   *  Sets the type of colors.
   *  Possible values are NATURAL and PASTEL.
   *  @param type The type of colors in the array.
   */
  public final void setColorsType (int type) {

    needsUpdate = true;
    colorsType = type;
  }


  /**
   * Gets whether the colors are to be manual (custom) or to be automatically selected.
   * If true, then use the setColorsCustom method to pass in an array of Color objects.
   * @return If true, then manual colors are used.
   */
  public final boolean getColorsCustomize() {
    return customize;
  }


  /**
   * Gets the custom color array.
   * To be used, the setColorsCustomize method must be set to true.
   * @return The custom color array.
   */
  public final Color[] getColorsCustom() {
    return customArray;
  }


  /**
   * Gets the type of colors.
   * Possible values are NATURAL and PASTEL.
   * @return The type of colors in the array.
   */
  public final int getColorsType() {
    return colorsType;
  }


  /**
   *  Gets an array of Colors of the length requested.
   *  @param length The number of colors requested.
   *  @return The color array.
   */
  public final Color[] getColorsArray (int length) {

    updateObject2D (null);
    Color[] colorsArrayOut = new Color[length];
    for (int i = 0; i < length; ++i) colorsArrayOut[i] = colorsArray [i % colorsArray.length];
    return colorsArrayOut;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param object2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getObject2DNeedsUpdate (Object2D object2D) {

    if (needsUpdate) return true;

    int index = -1;
    if ((index = object2DVector.indexOf (object2D)) != -1 &&
      ((Boolean)needsUpdateVector.get (index)).booleanValue()) return true;

    return false;
  }


  /**
   * Adds an Object2D to the set of objects using these properties.
   * @param object2D The Object2D to add.
   */
  final void addObject2D (Object object2D) {

    if (!object2DVector.contains (object2D)) {
      object2DVector.add (object2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes an Object2D object from the set of objects associated with this properties object.
   * @param object2D The object to remove.
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

    if (debug) System.out.println ("Validating MultiColorsProperties");

    boolean valid = true;

    if (customArray == null) {
      valid = false;
      if (debug) System.out.println ("CustomArray == null");
    }
    if (colorsType != NATURAL && colorsType != PASTEL && colorsType != CHART2D) {
      valid = false;
      if (debug) System.out.println ("Problem with ColorsType");
    }

    if (debug) {
      if (valid) System.out.println ("MultiColorsProperties was valid");
      else System.out.println ("MultiColorsProperties was invalid");
    }

    return (valid);
  }


  /**
   * Updates the properties of this Object2D.
   * @param object2D The object to update.
   */
  final void updateObject2D (Object2D object2D) {

    if (object2D == null || getObject2DNeedsUpdate (object2D)) {

      if (needsUpdate) {

        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }

        if (customize) colorsArray = customArray;
        else {

          if (colorsType == CHART2D) {

            colorsArray = new Color[12];
            colorsArray[0] =  new Color (000, 000, 102);
            colorsArray[1] =  new Color (051, 153, 102);
            colorsArray[2] =  new Color (204, 204, 102);
            colorsArray[3] =  new Color (153, 051, 102);
            colorsArray[4] =  new Color (051, 051, 153);
            colorsArray[5] =  new Color (000, 102, 051);
            colorsArray[6] = new Color (153, 153, 051);
            colorsArray[7] =  new Color (204, 102, 153);
            colorsArray[8] =  new Color (102, 102, 204);
            colorsArray[9] =  new Color (102, 204, 153);
            colorsArray[10] =  new Color (102, 102, 000);
            colorsArray[11] = new Color (102, 000, 051);
          }
          else {

            int maxNum = NUMCOLORS * NUMCOLORDEGREES;
            colorsArray = new Color[maxNum];
            int i = 0;
            while (i < maxNum) {

              for (int j = 0; j < NUMCOLORDEGREES; j = (++j) % maxNum) {

                float varyDegree = 4.5f;  //if j == 0
                if (j == 1) {varyDegree = 3.5f;}
                else if (j == 2) {varyDegree = 2.5f;}

                float varyType = colorsType == NATURAL ? 0f : varyDegree - 1.5f;

                colorsArray[i] = new Color ( //blue
                  (int)(varyType   * DEGREEVARIATION),
                  (int)(varyType   * DEGREEVARIATION),
                  (int)(varyDegree * DEGREEVARIATION));
                if (++i == maxNum) break;

                colorsArray[i] = new Color (  //green
                  (int)(varyType   * DEGREEVARIATION),
                  (int)(varyDegree * DEGREEVARIATION),
                  (int)(varyType   * DEGREEVARIATION));
                if (++i == maxNum) break;

                colorsArray[i] = new Color ( //red
                  (int)(varyDegree * DEGREEVARIATION),
                  (int)(varyType   * DEGREEVARIATION),
                  (int)(varyType   * DEGREEVARIATION));
                if (++i == maxNum) break;

                colorsArray[i] = new Color ( //cyan
                  (int)(varyType   * DEGREEVARIATION),
                  (int)(varyDegree * DEGREEVARIATION),
                  (int)(varyDegree * DEGREEVARIATION));
                if (++i == maxNum) break;

                colorsArray[i] = new Color ( //yellow
                  (int)(varyDegree * DEGREEVARIATION),
                  (int)(varyDegree * DEGREEVARIATION),
                  (int)(varyType   * DEGREEVARIATION));
                if (++i == maxNum) break;

                colorsArray[i] = new Color ( //magenta
                  (int)(varyDegree * DEGREEVARIATION),
                  (int)(varyType   * DEGREEVARIATION),
                  (int)(varyDegree * DEGREEVARIATION));
                if (++i == maxNum) break;
              }
            }
          }
        }
        needsUpdate = false;
      }

      int index = -1;
      if (object2D != null && (index = object2DVector.indexOf (object2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }
}