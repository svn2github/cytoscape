package org.jmathplot.gui.plotObjects;

import java.awt.*;
import java.awt.font.*;

public class Label
    implements Plotable {

  protected Coord coord;
  protected String label;
  protected Color color;
  protected double cornerN;
  protected double cornerE;

  //private static DecimalFormat dec = new DecimalFormat("##0.###E0");

  public Label(String l, Coord c, Color col) {
    cornerN = 0.5;
    cornerE = 0.5;
    label = l;
    coord = c;
    color = col;
  }

  public Label(String l, Coord c) {
    this(l, c, Color.black);
  }

  /**
   * show coord itself
   */
  public Label(Coord c) {
    this(coordToString(c), c, Color.black);
  }

  /**
   * reference point
   * center: 0.5, 0.5  lowerleft: 0,0 upperleft 1, 0 ...
   */
  public void setCorner(double n, double e) {
    cornerN = n;
    cornerE = e;
  }

  /**
   * shift by given screen coordinates offset
   */
  public void setOffset(double[] offset) {
    double[] newCoord = coord.getPlotCoordCopy();
    for (int i = 0; i < newCoord.length; i++) {
      newCoord[i] += offset[i];
    }
    coord.setPlotCoord(newCoord);
  }

  /**
   * see Text for formatted text output
   */
  public void plot(Graphics comp) {

    Graphics2D comp2D = (Graphics2D) comp;

    FontRenderContext frc = comp2D.getFontRenderContext();
    comp2D.setColor(color);
    Font font = comp2D.getFont();

    // default start is left upper point
    int x = coord.getScreenCoordCopy()[0] ;
    int y = coord.getScreenCoordCopy()[1] ;
    float gh = font.getSize2D();

    // Corner adjustment
    x -= (int) (font.getStringBounds(label, frc).getWidth() * cornerE);
    y += (int) (gh * cornerN);
    comp2D.drawString(label, x, y);
  }


  public static double approx(double val, int decimal) {
    //double timesEn = val*Math.pow(10,decimal);
    //if (Math.rint(timesEn) == timesEn) {
    //return val;
    //} else {
    //to limit precision loss, you need to separate cases where decimal<0 and >0
    //if you don't you'll have this : approx(10000.0,-4) => 10000.00000001
    if (decimal < 0) {
      return Math.rint(val / Math.pow(10, -decimal)) * Math.pow(10, -decimal);
    }
    else {
      return Math.rint(val * Math.pow(10, decimal)) / Math.pow(10, decimal);
    }
    //}
  }

  public static String coordToString(Coord c) {
    StringBuffer sb = new StringBuffer("(");
    for (int i = 0; i < c.getPlotCoordCopy().length; i++) {
      sb.append(approx(c.getPlotCoordCopy()[i], 2)).append(",");
      //sb.append(dec.format(c.getPlotCoordCopy()[i])).append(",");
    }
    sb.setLength(sb.length() - 1);
    if (sb.length() > 0) {
      sb.append(")");
    }
    return sb.toString();
  }

}