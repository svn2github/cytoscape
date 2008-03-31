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
 * The properties of a warning region for GraphChart2D charts.  A warning region is a rectangular
 * region of a graph that when a graph component enters it, the graph component in that region is
 * painted with a specific color;  also the background of that region is also a specific color.
 * Pass this to any number of GraphChart2D objects.
 */
final public class WarningRegionProperties {


  /**
   * Indicates the high value should be the maximum.
   * For use with setHigh().
   * Note that HIGH - N where N is some number is invalid.
   */
  public static final float HIGH = Float.POSITIVE_INFINITY;


  /**
   * Indicates the low value should be the minimum.
   * For use with setHigh().
   * Note that LOW - N where N is some number is invalid.
   */
  public static final float LOW = Float.NEGATIVE_INFINITY;


  /**
   * The default is HIGH.
   */
  public static final float HIGH_DEFAULT = HIGH;


  /**
   * The default is LOW.
   */
  public static final float LOW_DEFAULT = LOW;


  /**
   * The default is Color.red.
   */
  public static final Color COMPONENT_COLOR_DEFAULT = new Color (146, 0, 10);

  /**
   * The default is true.
   */
  public static final boolean BACKGROUND_EXISTENCE_DEFAULT = true;

  /**
   * The default is Color.pink.
   */
  public static final Color BACKGROUND_COLOR_DEFAULT = new Color (222, 177, 180);


  private float high;
  private float low;
  private Color componentColor;
  private boolean backgroundExistence;
  private Color backgroundColor;

  private boolean needsUpdate = true;
  private final Vector needsUpdateVector = new Vector (5, 5);
  private final Vector graphChart2DVector = new Vector (5, 5);


  /**
   * Creates a WarningRegionProperties object with the documented default values.
   */
  public WarningRegionProperties() {

    needsUpdate = true;
    setToDefaults();
  }


  /**
   * Creates a WarningRegionProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param warningRegionProps The properties to copy.
   */
  public WarningRegionProperties (WarningRegionProperties warningRegionProps) {

    needsUpdate = true;
    setWarningRegionProperties (warningRegionProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setToDefaults() {

    needsUpdate = true;
    setHigh (HIGH_DEFAULT);
    setLow (LOW_DEFAULT);
    setComponentColor (COMPONENT_COLOR_DEFAULT);
    setBackgroundExistence (BACKGROUND_EXISTENCE_DEFAULT);
    setBackgroundColor (BACKGROUND_COLOR_DEFAULT);
  }


  /**
   * Sets all properties to be the values of another WarningRegionProperties object.
   * The copying is a deep copy.
   * @param warningRegionProps The properties to copy.
   */
  public final void setWarningRegionProperties (WarningRegionProperties warningRegionProps) {

    needsUpdate = true;
    setHigh (warningRegionProps.getHigh());
    setLow (warningRegionProps.getLow());
    setComponentColor (warningRegionProps.getComponentColor());
    setBackgroundExistence (warningRegionProps.getBackgroundExistence());
    setBackgroundColor (warningRegionProps.getBackgroundColor());
  }


  /**
   * Sets the high value of this warning region.  For example, if your data ranges from 10000 to
   * 0 and you want an orange region from 6000 to 8000, then set the high to 8000.  If you want the
   * region to extend from 8000 to the top of the graph, then set the high to HIGH.
   * @param h The high value of this region.
   */
  public final void setHigh (float h) {

    high = h;
    needsUpdate = true;
  }


  /**
   * Sets the low value of this warning region.  For example, if your data ranges from 10000 to
   * 0 and you want an orange region from 6000 to 8000, then set the low to 6000.  If you want the
   * region to extend from 6000 to the bottom of the graph, then set the low to LOW.
   * @param l The low value of this region.
   */
  public final void setLow (float l) {

    low = l;
    needsUpdate = true;
  }


  /**
   * Sets the color that any component entering this region should become.  Only the portion of the
   * component that is in the region will be this color.  Examples of components are: bars, lines,
   * and dots.
   * @param c The color of the components sections in the region.
   */
  public final void setComponentColor (Color c) {

    componentColor = c;
    needsUpdate = true;
  }


  /**
   * Sets the existence of the background irrespective of the existence of the graph's background.
   * @param existence If true, then the background of the warning region will exist.
   */
  public final void setBackgroundExistence (boolean existence) {

    backgroundExistence = existence;
    needsUpdate = true;
  }


  /**
   * Sets the color that the graph background becomes in this region if the graph background exists.
   * @param c The color of the section of the graph background.
   */
  public final void setBackgroundColor (Color c) {

    backgroundColor = c;
    needsUpdate = true;
  }


  /**
   * Gets the high value of this warning region.  For example, if your data ranges from 10000 to
   * 0 and you want an orange region from 6000 to 8000, then set the high to 8000.  If you want the
   * region to extend from 8000 to the top of the graph, then set the high to HIGH.
   * @return The high value of this region.
   */
  public final float getHigh() {
    return high;
  }


  /**
   * Gets the low value of this warning region.  For example, if your data ranges from 10000 to
   * 0 and you want an orange region from 6000 to 8000, then set the low to 6000.  If you want the
   * region to extend from 6000 to the bottom of the graph, then set the low to LOW.
   * @return The low value of this region.
   */
  public final float getLow() {
    return low;
  }


  /**
   * Gets the color that any component entering this region should become.  Only the portion of the
   * component that is in the region will be this color.  Examples of components are: bars, lines,
   * and dots.
   * @return The color of the components sections in the region.
   */
  public final Color getComponentColor() {
    return componentColor;
  }


  /**
   * Gets the existence of the background irrespective of the existence of the graph's background.
   * @return If true, then the background of the warning region will exist.
   */
  public final boolean getBackgroundExistence() {
    return backgroundExistence;
  }


  /**
   * Gets the color that the graph background becomes in this region if the graph background exists.
   * @return The color of the section of the graph background.
   */
  public final Color getBackgroundColor() {
    return backgroundColor;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param graphChart2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getGraphChart2DNeedsUpdate (GraphChart2D graphChart2D) {

    if (needsUpdate) return true;
    int index = -1;
    if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }
    return false;
  }


  /**
   * Adds a GraphChart2D to the set of objects using these properties.
   * @param graphChart2D The Object2D to add.
   */
  final void addGraphChart2D (GraphChart2D graphChart2D) {

    if (!graphChart2DVector.contains (graphChart2D)) {
      graphChart2DVector.add (graphChart2D);
      needsUpdateVector.add (new Boolean (true));
  } }


  /**
   * Removes a GraphChart2D from the set of objects using these properties.
   * @param graphChart2D The Object2D to remove.
   */
  final void removeGraphChart2D (GraphChart2D graphChart2D) {

    int index = -1;
    if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
      graphChart2DVector.remove (index);
      needsUpdateVector.remove (index);
  } }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  final boolean validate (boolean debug) {

    if (debug) System.out.println ("Validating WarningRegionProperties");

    boolean valid = true;

    if ((high != HIGH && (low == HIGH || high < low)) ||
      (low != LOW && (high == LOW || high < low))) {
      valid = false;
      if (debug) System.out.println ("High was lower than low");
    }
    if (componentColor == null) {
      valid = false;
      if (debug) System.out.println ("ComponentColor == null");
    }
    if (backgroundColor == null) {
      valid = false;
      if (debug) System.out.println ("BackgroundColor == null");
    }

    if (debug) {
      if (valid) System.out.println ("WarningRegionProperties was valid");
      else System.out.println ("WarningRegionProperties was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this GraphChart2D.
   * @param graphChart2D The object to update.
   */
  final void updateGraphChart2D (GraphChart2D graphChart2D) {

    if (getGraphChart2DNeedsUpdate (graphChart2D)) {

      if (needsUpdate) {
        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }
        needsUpdate = false;
      }

      int index = -1;
      if ((index = graphChart2DVector.indexOf (graphChart2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }


  /**
   * A convencience method for creating a WarningRegion set with these properties.
   * @return The appropriately set warning region.
   */
  final WarningRegion configureWarningRegion() {

    WarningRegion warningRegion = new WarningRegion();
    warningRegion.setHigh (getHigh());
    warningRegion.setLow (getLow());
    warningRegion.setComponentColor (getComponentColor());
    warningRegion.setBackgroundExistence (getBackgroundExistence());
    warningRegion.setBackgroundColor (getBackgroundColor());
    return warningRegion;
  }
}