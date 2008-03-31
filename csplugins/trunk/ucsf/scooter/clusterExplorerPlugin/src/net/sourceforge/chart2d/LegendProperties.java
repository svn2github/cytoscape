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
 * A data structure for holding the properties common to all legends.
 * Pass this to any number of Chart2D objects.
 */
public final class LegendProperties extends Properties {


  /**
   * The default is true.
   */
  public final static boolean LEGEND_EXISTENCE_DEFAULT = true;

  /**
   * The default is String[0].
   */
  public static final String[] LEGEND_LABELS_TEXTS_DEFAULT = new String[0];

  /**
   * The default is true.
   */
  public final static boolean LEGEND_BORDER_EXISTENCE_DEFAULT = true;

  /**
   * The default is 2.
   */
  public final static int LEGEND_BORDER_THICKNESS_MODEL_DEFAULT = 2;

  /**
   * The default is Color.gray.
   */
  public final static Color LEGEND_BORDER_COLOR_DEFAULT = Color.gray;

  /**
   * The default is true.
   */
  public final static boolean LEGEND_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public final static int LEGEND_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is false.
   */
  public final static boolean LEGEND_BACKGROUND_EXISTENCE_DEFAULT = false;

  /**
   * The default is Color.white.
   */
  public final static Color LEGEND_BACKGROUND_COLOR_DEFAULT = Color.white;

  /**
   * The default is 11.
   */
  public final static int LEGEND_LABELS_FONT_POINT_MODEL_DEFAULT = 11;

  /**
   * The default is "SansSerif".
   */
  public final static String LEGEND_LABELS_FONT_NAME_DEFAULT = "SansSerif";

  /**
   * The default is Color.black.
   */
  public final static Color LEGEND_LABELS_FONT_COLOR_DEFAULT = Color.black;

  /**
   * The default is Font.PLAIN.
   */
  public final static int LEGEND_LABELS_FONT_STYLE_DEFAULT = Font.PLAIN;

  /**
   * The default is true.
   */
  public final static boolean LEGEND_BETWEEN_LABELS_OR_BULLETS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 5.
   */
  public final static int LEGEND_BETWEEN_LABELS_OR_BULLETS_GAP_THICKNESS_MODEL_DEFAULT = 5;

  /**
   * The default is true.
   */
  public final static boolean LEGEND_BETWEEN_LABELS_AND_BULLETS_GAP_EXISTENCE_DEFAULT = true;

  /**
   * The default is 3.
   */
  public final static int LEGEND_BETWEEN_LABELS_AND_BULLETS_GAP_THICKNESS_MODEL_DEFAULT = 3;

  /**
   * The default is true.
   */
  public final static boolean LEGEND_BULLETS_OUTLINE_EXISTENCE_DEFAULT = true;

  /**
   * The default is Color.black.
   */
  public final static Color LEGEND_BULLETS_OUTLINE_COLOR_DEFAULT = Color.black;

  /**
   * The default is the Dimension (9, 9).
   */
  public final static Dimension LEGEND_BULLETS_SIZE_MODEL_DEFAULT = new Dimension (9, 9);


  private boolean legendExistence;
  private String[] legendLabelsTexts;
  private boolean legendBorderExistence;
  private int legendBorderThicknessModel;
  private Color legendBorderColor;
  private boolean legendGapExistence;
  private int legendGapThicknessModel;
  private boolean legendBackgroundExistence;
  private Color legendBackgroundColor;
  private int legendLabelsFontPointModel;
  private String legendLabelsFontName;
  private Color legendLabelsFontColor;
  private int legendLabelsFontStyle;
  private boolean legendBetweenLabelsOrBulletsGapExistence;
  private int legendBetweenLabelsOrBulletsGapThicknessModel;
  private boolean legendBetweenLabelsAndBulletsGapExistence;
  private int legendBetweenLabelsAndBulletsGapThicknessModel;
  private boolean legendBulletsOutlineExistence;
  private Color legendBulletsOutlineColor;
  private Dimension legendBulletsSizeModel;

  private boolean needsUpdate = true;
  private final Vector chart2DVector = new Vector (5, 5);
  private final Vector needsUpdateVector = new Vector (5, 5);


  /**
   * Creates a LegendProperties object with the documented default values.
   */
  public LegendProperties() {

    needsUpdate = true;
    setLegendPropertiesToDefaults();
  }


  /**
   * Creates a LegendProperties object with property values copied from another object.
   * The copying is a deep copy.
   * @param legendProps The properties to copy.
   */
  public LegendProperties (LegendProperties legendProps) {

    needsUpdate = true;
    setLegendProperties (legendProps);
  }


  /**
   * Sets all properties to their default values.
   */
  public final void setLegendPropertiesToDefaults() {

    needsUpdate = true;
    setLegendExistence (LEGEND_EXISTENCE_DEFAULT);
    setLegendLabelsTexts (LEGEND_LABELS_TEXTS_DEFAULT);
    setLegendBorderExistence (LEGEND_BORDER_EXISTENCE_DEFAULT);
    setLegendBorderThicknessModel (LEGEND_BORDER_THICKNESS_MODEL_DEFAULT);
    setLegendBorderColor (LEGEND_BORDER_COLOR_DEFAULT);
    setLegendGapExistence (LEGEND_GAP_EXISTENCE_DEFAULT);
    setLegendGapThicknessModel (LEGEND_GAP_THICKNESS_MODEL_DEFAULT);
    setLegendBackgroundExistence (LEGEND_BACKGROUND_EXISTENCE_DEFAULT);
    setLegendBackgroundColor (LEGEND_BACKGROUND_COLOR_DEFAULT);
    setLegendLabelsFontPointModel (LEGEND_LABELS_FONT_POINT_MODEL_DEFAULT);
    setLegendLabelsFontName (LEGEND_LABELS_FONT_NAME_DEFAULT);
    setLegendLabelsFontColor (LEGEND_LABELS_FONT_COLOR_DEFAULT);
    setLegendLabelsFontStyle (LEGEND_LABELS_FONT_STYLE_DEFAULT);
    setLegendBetweenLabelsOrBulletsGapExistence (
      LEGEND_BETWEEN_LABELS_OR_BULLETS_GAP_EXISTENCE_DEFAULT);
    setLegendBetweenLabelsOrBulletsGapThicknessModel (
      LEGEND_BETWEEN_LABELS_OR_BULLETS_GAP_THICKNESS_MODEL_DEFAULT);
    setLegendBetweenLabelsAndBulletsGapExistence (
      LEGEND_BETWEEN_LABELS_AND_BULLETS_GAP_EXISTENCE_DEFAULT);
    setLegendBetweenLabelsAndBulletsGapThicknessModel (
      LEGEND_BETWEEN_LABELS_AND_BULLETS_GAP_THICKNESS_MODEL_DEFAULT);
    setLegendBulletsOutlineExistence (
      LEGEND_BULLETS_OUTLINE_EXISTENCE_DEFAULT);
    setLegendBulletsOutlineColor (LEGEND_BULLETS_OUTLINE_COLOR_DEFAULT);
    setLegendBulletsSizeModel (LEGEND_BULLETS_SIZE_MODEL_DEFAULT);
  }


  /**
   * Sets all properties to be the values of another LegendProperties object.
   * The copying is a deep copy.
   * @param legendProps The properties to copy.
   */
  public final void setLegendProperties (LegendProperties legendProps) {

    needsUpdate = true;
    setLegendExistence (legendProps.getLegendExistence());
    setLegendBorderExistence (legendProps.getLegendBorderExistence());
    setLegendBorderThicknessModel (legendProps.getLegendBorderThicknessModel());
    setLegendBorderColor (legendProps.getLegendBorderColor());
    setLegendGapExistence (legendProps.getLegendGapExistence());
    String[] legendTexts = legendProps.getLegendLabelsTexts();
    String[] copiedLegendTexts = new String[legendTexts.length];
    for (int i = 0; i < legendTexts.length; ++i) copiedLegendTexts[i] = legendTexts[i];
    setLegendLabelsTexts (copiedLegendTexts);
    setLegendGapThicknessModel (legendProps.getLegendGapThicknessModel());
    setLegendBackgroundExistence (legendProps.getLegendBackgroundExistence());
    setLegendBackgroundColor (legendProps.getLegendBackgroundColor());
    setLegendLabelsFontPointModel (legendProps.getLegendLabelsFontPointModel());
    setLegendLabelsFontName (legendProps.getLegendLabelsFontName());
    setLegendLabelsFontColor (legendProps.getLegendLabelsFontColor());
    setLegendLabelsFontStyle (legendProps.getLegendLabelsFontStyle());
    setLegendBetweenLabelsOrBulletsGapExistence (
      legendProps.getLegendBetweenLabelsOrBulletsGapExistence());
    setLegendBetweenLabelsOrBulletsGapThicknessModel (
      legendProps.getLegendBetweenLabelsOrBulletsGapThicknessModel());
    setLegendBetweenLabelsAndBulletsGapExistence (
      legendProps.getLegendBetweenLabelsAndBulletsGapExistence());
    setLegendBetweenLabelsAndBulletsGapThicknessModel (
      legendProps.getLegendBetweenLabelsAndBulletsGapThicknessModel());
    setLegendBulletsOutlineExistence (legendProps.getLegendBulletsOutlineExistence());
    setLegendBulletsOutlineColor (legendProps.getLegendBulletsOutlineColor());
    setLegendBulletsSizeModel (new Dimension (legendProps.getLegendBulletsSizeModel()));
  }


  /**
   * Sets whether there will exist a legend on the chart.
   * @param existence If true, there will be a legend for the chart.
   */
  public final void setLegendExistence (boolean existence) {

    needsUpdate = true;
    legendExistence = existence;
  }


  /**
   * Sets the texts of the legend labels.  The number of texts must be
   * equal to the total number of data sets of this chart.
   * @param texts The texts for the legend labels.
   */
  public final void setLegendLabelsTexts (String[] texts) {

    needsUpdate = true;
    legendLabelsTexts = texts;
  }


  /**
   * Sets whether there will exist a border around the legend.
   * @param existence If true, there will be a border around the legend.
   */
  public final void setLegendBorderExistence (boolean existence) {

    needsUpdate = true;
    legendBorderExistence = existence;
  }


  /**
   * Sets the thickness of the border around the legend for the chart's
   * model size.
   * @param thickness The model thicknes for the chart.
   */
  public final void setLegendBorderThicknessModel (int thickness) {

    needsUpdate = true;
    legendBorderThicknessModel = thickness;
  }


  /**
   * Sets the color of the border around the legend.
   * @param color The color of the legend's border.
   */
  public final void setLegendBorderColor (Color color) {

    needsUpdate = true;
    legendBorderColor = color;
  }


  /**
   * Sets whether a gap between the legend's border or edge and the
   * legend's inner components exists.
   * @param existence If true, then a gap exists.
   */
  public final void setLegendGapExistence (boolean existence) {

    needsUpdate = true;
    legendGapExistence = existence;
  }


  /**
   * Sets the thickness of the legend's gap for the chart's model size.
   * @param thickness The model thickness of the legend's gap.
   */
  public final void setLegendGapThicknessModel (int thickness) {

    needsUpdate = true;
    legendGapThicknessModel = thickness;
  }


  /**
   * Sets whether the legend will have a painted background or not.  If not
   * whatever is behind the legend will show through the spaces of the legend.
   * @param existence If true, the background of the legend will be painted.
   */
  public final void setLegendBackgroundExistence (boolean existence) {

    needsUpdate = true;
    legendBackgroundExistence = existence;
  }


  /**
   * Sets the color of the legend's background.
   * @param color The color of the legend's background.
   */
  public final void setLegendBackgroundColor (Color color) {

    needsUpdate = true;
    legendBackgroundColor = color;
  }


  /**
   * Sets the point of the font of the legend's labels for the chart's
   * model size.
   * @param point The model font point of the legend's labels.
   */
  public final void setLegendLabelsFontPointModel (int point) {

    needsUpdate = true;
    legendLabelsFontPointModel = point;
  }

  /**
   * Sets name of the font of the legend's labels.
   * Accepts all values accepted by java.awt.Font.
   * @param name The name of the font for the legend's labels.
   */
  public final void setLegendLabelsFontName (String name) {

    needsUpdate = true;
    legendLabelsFontName = name;
  }


  /**
   *  Sets the color of the font of the legend's labels.
   *  @param color The color of the font of the legend's labels.
   */
  public final void setLegendLabelsFontColor (Color color) {

    needsUpdate = true;
    legendLabelsFontColor = color;
  }


  /**
   * Sets the style of the font of the legend's labels.
   * Accepts all values that java.awt.Font accepts.
   * @param style The style of the font of the legend's labels.
   */
  public final void setLegendLabelsFontStyle (int style) {

    needsUpdate = true;
    legendLabelsFontStyle = style;
  }


  /**
   * Sets whether a gap between the legend's bullets or labels exists in
   * their vertical placement.
   * @param existence If true, a gap exists.
   */
  public final void setLegendBetweenLabelsOrBulletsGapExistence (boolean existence) {

    needsUpdate = true;
    legendBetweenLabelsOrBulletsGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between the legend's bullets or labels
   * exists in their vertical placement, for the chart's model size.
   * @param thickness The model thickness of the gap.
   */
  public final void setLegendBetweenLabelsOrBulletsGapThicknessModel (int thickness) {

    needsUpdate = true;
    legendBetweenLabelsOrBulletsGapThicknessModel = thickness;
  }


  /**
   * Sets whether the gap between the legend's bullets and labels exists
   * in their horizontal placement.
   * @param existence If true, a gap exists.
   */
  public final void setLegendBetweenLabelsAndBulletsGapExistence (boolean existence) {

    needsUpdate = true;
    legendBetweenLabelsAndBulletsGapExistence = existence;
  }


  /**
   * Sets the thickness of the gap between the legend's bullets and labels
   * in their horizontal placement, for the chart's model size.
   * @param thickness The model thickness of teh gap.
   */
  public final void setLegendBetweenLabelsAndBulletsGapThicknessModel (int thickness) {

    needsUpdate = true;
    legendBetweenLabelsAndBulletsGapThicknessModel = thickness;
  }


  /**
   * Sets whether a thin line is painted around the legend's bullets.
   * @param existence If true, the legend's bullets will be outlined.
   */
  public final void setLegendBulletsOutlineExistence (boolean existence) {

    needsUpdate = true;
    legendBulletsOutlineExistence = existence;
  }


  /**
   * Sets the color of the legend's bullets outline.
   * @param color The color of the legend's bullets outline.
   */
  public final void setLegendBulletsOutlineColor (Color color) {

    needsUpdate = true;
    legendBulletsOutlineColor = color;
  }


  /**
   * Sets the size of the legend's bullets for the chart's model size.
   * @param size The size of the legend's bullets.
   */
  public final void setLegendBulletsSizeModel (Dimension size) {

    needsUpdate = true;
    legendBulletsSizeModel = size;
  }


  /**
   * Gets whether there will exist a legend on the chart.
   * @return boolean If true, there will be a legend for the chart.
   */
  public final boolean getLegendExistence() {
    return legendExistence;
  }


  /**
   * Gets the texts of the legend labels.  The number of texts must be
   * equal to the total number of data sets of this chart.
   * @return String[] The texts for the legend labels.
   */
  public final String[] getLegendLabelsTexts() {
    return legendLabelsTexts;
  }


  /**
   * Gets whether there will exist a border around the legend.
   * @return boolean If true, there will be a border around the legend.
   */
  public final boolean getLegendBorderExistence() {
    return legendBorderExistence;
  }


  /**
   * Gets the thickness of the border around the legend for the chart's
   * model size.
   * @return int The model thicknes for the chart.
   */
  public final int getLegendBorderThicknessModel() {
    return legendBorderThicknessModel;
  }


  /**
   * Gets the color of the border around the legend.
   * @return Color The color of the legend's border.
   */
  public final Color getLegendBorderColor() {
    return legendBorderColor;
  }


  /**
   * Gets whether a gap between the legend's border or edge and the
   * legend's inner components exists.
   * @return boolean If true, then a gap exists.
   */
  public final boolean getLegendGapExistence() {
    return legendGapExistence;
  }


  /**
   * Gets the thickness of the legend's gap for the chart's model size.
   * @return int The model thickness of the legend's gap.
   */
  public final int getLegendGapThicknessModel() {
    return legendGapThicknessModel;
  }


  /**
   * Gets whether the legend will have a painted background or not.  If not
   * whatever is behind the legend will show through the spaces of the legend.
   * @return boolean If true, the background of the legend will be painted.
   */
  public final boolean getLegendBackgroundExistence() {
    return legendBackgroundExistence;
  }


  /**
   * Gets the color of the legend's background.
   * @return Color The color of the legend's background.
   */
  public final Color getLegendBackgroundColor() {
    return legendBackgroundColor;
  }


  /**
   * Gets the point of the font of the legend's labels for the chart's
   * model size.
   * @return int The model font point of the legend's labels.
   */
  public final int getLegendLabelsFontPointModel() {
    return legendLabelsFontPointModel;
  }

  /**
   * Gets name of the font of the legend's labels.
   * Accepts all values accepted by java.awt.Font.
   * @return String The name of the font for the legend's labels.
   */
  public final String getLegendLabelsFontName() {
    return legendLabelsFontName;
  }


  /**
   *  Gets the color of the font of the legend's labels.
   *  @return Color The color of the font of the legend's labels.
   */
  public final Color getLegendLabelsFontColor() {
    return legendLabelsFontColor;
  }


  /**
   * Gets the style of the font of the legend's labels.
   * Accepts all values that java.awt.Font accepts.
   * @return int The style of the font of the legend's labels.
   */
  public final int getLegendLabelsFontStyle() {
    return legendLabelsFontStyle;
  }


  /**
   * Gets whether a gap between the legend's bullets or labels exists in
   * their vertical placement.
   * @return boolean If true, a gap exists.
   */
  public final boolean getLegendBetweenLabelsOrBulletsGapExistence() {
    return legendBetweenLabelsOrBulletsGapExistence;
  }


  /**
   * Gets the thickness of the gap between the legend's bullets or labels
   * exists in their vertical placement, for the chart's model size.
   * @return int The model thickness of the gap.
   */
  public final int getLegendBetweenLabelsOrBulletsGapThicknessModel() {
    return legendBetweenLabelsOrBulletsGapThicknessModel;
  }


  /**
   * Gets whether the gap between the legend's bullets and labels exists
   * in their horizontal placement.
   * @return boolean If true, a gap exists.
   */
  public final boolean getLegendBetweenLabelsAndBulletsGapExistence() {
    return legendBetweenLabelsAndBulletsGapExistence;
  }


  /**
   * Gets the thickness of the gap between the legend's bullets and labels
   * in their horizontal placement, for the chart's model size.
   * @return int The model thickness of teh gap.
   */
  public final int getLegendBetweenLabelsAndBulletsGapThicknessModel() {
    return legendBetweenLabelsAndBulletsGapThicknessModel;
  }


  /**
   * Gets whether a thin line is painted around the legend's bullets.
   * @return boolean If true, the legend's bullets will be outlined.
   */
  public final boolean getLegendBulletsOutlineExistence() {
    return legendBulletsOutlineExistence;
  }


  /**
   * Gets the color of the legend's bullets outline.
   * @return Color The color of the legend's bullets outline.
   */
  public final Color getLegendBulletsOutlineColor() {
    return legendBulletsOutlineColor;
  }


  /**
   * Gets the size of the legend's bullets for the chart's model size.
   * @return Dimension The size of the legend's bullets.
   */
  public final Dimension getLegendBulletsSizeModel() {
    return legendBulletsSizeModel;
  }


  /**
   * Gets whether this object needs to be updated with new properties.
   * @param chart2D The object that may need to be updated.
   * @return If true then needs update.
   */
  final boolean getChart2DNeedsUpdate (Chart2D chart2D) {

    if (needsUpdate) return true;

    int index = -1;
    if ((index = chart2DVector.indexOf (chart2D)) != -1) {
      return ((Boolean)needsUpdateVector.get (index)).booleanValue();
    }

    return false;
  }


  /**
   * Adds a Chart2D to the set of objects using these properties.
   * @param chart2D The Object2D to add.
   */
  final void addChart2D (Chart2D chart2D) {

    if (!chart2DVector.contains (chart2D)) {
      chart2DVector.add (chart2D);
      needsUpdateVector.add (new Boolean (true));
    }
  }


  /**
   * Removes a Chart2D from the set of objects using these properties.
   * @param chart2D The Object2D to add.
   */
  final void removeChart2D (Chart2D chart2D) {

    int index = -1;
    if ((index = chart2DVector.indexOf (chart2D)) != -1) {
      chart2DVector.remove (index);
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

    if (debug) System.out.println ("Validating Chart2DProperties");

    boolean valid = true;

    if (legendLabelsTexts == null) {
      valid = false;
      if (debug) System.out.println ("LegendLabelsTexts == null");
    }
    if (legendBorderThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("LegendBorderThicknessModel < 0");
    }
    if (legendBorderColor == null) {
      valid = false;
      if (debug) System.out.println ("LegendBorderColor == null");
    }
    if (legendGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("LegendGapThicknessModel < 0");
    }
    if (legendBackgroundColor == null) {
      valid = false;
      if (debug) System.out.println ("LegendBackgroundColor == null");
    }
    if (legendLabelsFontPointModel < 0) {
      valid = false;
      if (debug) System.out.println ("LegendLabelsFontPointModel < 0");
    }
    if (legendLabelsFontName == null || !isFontNameExists (legendLabelsFontName)) {
      valid = false;
      if (debug) System.out.println ("Problem with LegendLabelsFontName");
    }
    if (legendLabelsFontColor == null) {
      valid = false;
      if (debug) System.out.println ("LegendLabelsFontColor == null");
    }
    if (legendLabelsFontStyle != Font.PLAIN && legendLabelsFontStyle != Font.ITALIC &&
      legendLabelsFontStyle != Font.BOLD && legendLabelsFontStyle != (Font.ITALIC|Font.BOLD)) {
      valid = false;
      if (debug) System.out.println ("Problem with LegendLabelsFontStyle");
    }
    if (legendBetweenLabelsOrBulletsGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("LegendBetweenLabelsOrBulletsGapThicknessModel < 0");
    }
    if (legendBetweenLabelsAndBulletsGapThicknessModel < 0) {
      valid = false;
      if (debug) System.out.println ("LegendBetweenLabelsAndBulletsGapThicknessModel < 0");
    }
    if (legendBulletsOutlineColor == null) {
      valid = false;
      if (debug) System.out.println ("LegendBulletsOutlineColor == null");
    }
    if (legendBulletsSizeModel == null ||
      legendBulletsSizeModel.width < 0 || legendBulletsSizeModel.height < 0) {
      valid = false;
      if (debug) System.out.println ("Problem with LegendBulletsSizeModel");
    }

    if (debug) {
      if (valid) System.out.println ("LegendProperties was valid");
      else System.out.println ("LegendProperties was invalid");
    }

    return valid;
  }


  /**
   * Updates the properties of this Chart2D.
   * @param chart2D The object to update.
   */
  final void updateChart2D (Chart2D chart2D) {

    if (getChart2DNeedsUpdate (chart2D)) {

      if (needsUpdate) {
        needsUpdate = false;
        for (int i = 0; i < needsUpdateVector.size(); ++i) {
          needsUpdateVector.set (i, new Boolean (true));
        }
      }

      int index = -1;
      if ((index = chart2DVector.indexOf (chart2D)) != -1) {
        needsUpdateVector.set (index, new Boolean (false));
      }
    }
  }
}