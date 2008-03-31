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


/**
 * A class for the methods of GraphChart2D charts of the "Labels Left" type.
 * A LLChart2D object is one with it's category descriptor labels along the left (or y) axis.
 * For example, a horizontal bar chart is a LLChart2D type chart.
 * Changes through its set methods are updated upon next repaint() or getImage() calls.
 */
public final class LLChart2D extends GraphChart2D {


  private boolean needsUpdate;
  private LLChartArea chart;
  private BufferedImage image;
  private Dimension size;
  private Dimension imageSize;
  private Dimension prefSize;
  private boolean customizePrefSize;
  private Dimension customPrefSize;


  /**
   * Creates a LLChart2D object with its defaults.
   */
  public LLChart2D() {

    needsUpdate = true;
    chart = new LLChartArea();
    size = new Dimension();
    imageSize = new Dimension();
    prefSize = null;
    customizePrefSize = false;
    customPrefSize = null;
  }


  /**
   * Sets a custom preferred size for the chart.
   * This custom size will override the preferred size calculations that normally occurr.
   * If null is passed, the preferred size calculations will be reinstated.
   * @param size  The custom preferred size for this chart.
   */
  public final void setPreferredSize (Dimension size) {

    needsUpdate = true;
    customizePrefSize = size != null;
    customPrefSize = size;
    prefSize = null;
  }


  /**
   * Gets a buffered image of the chart.
   * @return An image of this chart
   */
  public final BufferedImage getImage() {

    if (getSize().width <= 0 || getSize().height <= 0) pack();
    else updateImage (getSize());

    if (!chart.getBackgroundExistence()) {

      Graphics2D imageG2D = image.createGraphics();
      imageG2D.setRenderingHint (
        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.paintComponent (imageG2D);
      Point location = chart.getSizeLocation (chart.MIN);
      imageSize = chart.getSize (chart.MIN);
      image = image.getSubimage (location.x, location.y, imageSize.width, imageSize.height);
    }

    return image;
  }


  /**
   * Gets the preferred size of the chart.
   * The preferred size is within the maximum and minimum sizes of the chart.
   * Much calculation is performed when calling this method.
   * @return The preferred minimum size of the chart.
   */
  public final Dimension getPreferredSize() {

    updateGraphChart2D();

    if (!customizePrefSize) {

      boolean autoModel = chart.getAutoSize (chart.MAXMODEL);
      boolean autoMin = chart.getAutoSize (chart.MIN);
      chart.setAutoSizes (true, false);
      chart.resetLLChartAreaModel (true);
      chart.setAutoSetLayoutRatios (true);
      chart.setSize (chart.MAX, getMaximumSize());
      BufferedImage image = new BufferedImage (
        getMaximumSize().width, getMaximumSize().height, BufferedImage.TYPE_INT_BGR);
      Graphics2D imageG2D = image.createGraphics();
      prefSize = chart.getPrefSize (imageG2D);
      chart.setAutoSizes (autoModel, autoMin);
    }
    else prefSize = customPrefSize;

    int prefWidth =
      prefSize.width < getMinimumSize().width ? getMinimumSize().width : prefSize.width;
    int prefHeight =
      prefSize.height < getMinimumSize().height ? getMinimumSize().height : prefSize.height;
    prefSize.setSize (prefWidth, prefHeight);

    this.size = prefSize;
    chart.resetLLChartAreaModel (true);
    chart.setAutoSetLayoutRatios (true);
    chart.setSize (chart.MAX, size);
    if (!chart.getBackgroundExistence()) {

      image = new BufferedImage (
          getMaximumSize().width, getMaximumSize().height, BufferedImage.TYPE_INT_BGR);
      Graphics2D imageG2D = image.createGraphics();
      imageG2D.setRenderingHint (
        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.updateLLChartArea (imageG2D);
    }
    else {

      image = new BufferedImage (size.width, size.height, BufferedImage.TYPE_INT_BGR);
      Graphics2D imageG2D = image.createGraphics();
      imageG2D.setRenderingHint (
        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.paintComponent (imageG2D);
      Point location = chart.getSizeLocation (chart.MIN);
      imageSize = chart.getSize (chart.MIN);
      image = image.getSubimage (location.x, location.y, imageSize.width, imageSize.height);
    }

    needsUpdate = false;

    return prefSize;
  }


  /**
   * Causes the object to reinintialize to it's preferred size.
   */
  public final void pack() {

    needsUpdate = true;
    setSize (getPreferredSize());
  }


  /**
   * Validates the properties of this object.
   * If debug is true then prints a messages indicating whether each property is valid.
   * Returns true if all the properties were valid and false otherwise.
   * @param debug If true then will print status messages.
   * @return If true then valid.
   */
  public final boolean validate (boolean debug) {

    if (debug) System.out.println ("Validating LLChart2D");

    boolean valid = true;

    if (!validateGraphChart2D (debug)) valid = false;

    if (debug) {

      if (valid) System.out.println ("LLChart2D was valid");
      else System.out.println ("LLChart2D was invalid");
    }

    return valid;
  }


  /**
   * Paints the chart.
   * This is provided for the layout manager to call.
   * @param g The graphics context for calculations and painting.
   */
  public final void paintComponent (Graphics g) {

    super.paintComponent (g);
    Graphics2D g2D = (Graphics2D)g;

    updateImage (getSize());

    if (!chart.getBackgroundExistence()) {

      g2D.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      chart.paintComponent (g2D);
    }
    else g2D.drawImage (image, 0, 0, imageSize.width, imageSize.height, this);
  }


  /**
   * Gets the LLChartArea of this LLChart2D.
   * @return The LLChartArea of this LLChart2D.
   */
  final TitledArea getObjectArea() {
    return chart;
  }


  /**
   * Gets the chart type, LABELS_LEFT.
   * @return The type of the chart, LABELS_LEFT.
   */
  final int getGraphChartType() {
    return LABELS_LEFT;
  }


  private boolean getNeedsUpdate() {

    return (needsUpdate || size.width != getSize().width || size.height != getSize().height ||
      getNeedsUpdateGraphChart2D());
  }


  private void updateImage (Dimension size) {

    if (prefSize == null) getPreferredSize();

    if (getNeedsUpdate()) {

      updateGraphChart2D();

      this.size = size;
      chart.setSize (chart.MAX, size);

      if (!chart.getBackgroundExistence()) {

        image = new BufferedImage (
          getMaximumSize().width, getMaximumSize().height, BufferedImage.TYPE_INT_BGR);
        Graphics2D imageG2D = image.createGraphics();
        imageG2D.setRenderingHint (
          RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.updateLLChartArea (imageG2D);
      }
      else {

        image = new BufferedImage (
          size.width, size.height, BufferedImage.TYPE_INT_BGR);
        Graphics2D imageG2D = image.createGraphics();
        imageG2D.setRenderingHint (
          RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.paintComponent (imageG2D);
        Point location = chart.getSizeLocation (chart.MIN);
        imageSize = chart.getSize (chart.MIN);
        image = image.getSubimage (location.x, location.y, imageSize.width, imageSize.height);
      }

      needsUpdate = false;
    }
  }
}