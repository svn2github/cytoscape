
package  cytoscape.visual.unitTests;

import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.Point2D;

import giny.model.Node;

import giny.view.NodeView;
import giny.view.GraphView;
import giny.view.Label;

/**
 * This is a dummy implementation of NodeView that
 * can be used for unit testing.
 */
public class TestNodeView implements NodeView  {

  int shape = 0;
  Paint selectedPaint = Color.RED;
  Paint unselectedPaint = Color.BLUE;
  Stroke border = new BasicStroke();
  float borderWidth = 0;
  Paint borderPaint = Color.GREEN;
  float transparency = 0;
  double width = 0;
  double height = 0;
  double x_pos = 0;
  double y_pos = 0;
  boolean selected = false;
  String toolTip = "";
  Point2D offset = new Point2D.Double();
  int degree = 0;
  Label label = new TestLabel();
  double label_offset_x = 0.0;
  double label_offset_y = 0.0;
  int node_label_anchor = 0;

 
  public TestNodeView() {}
  
  public GraphView getGraphView() { return null;}

  public Node getNode () {return null;} 

  public int getGraphPerspectiveIndex () {return 1;}

  public int getRootGraphIndex () {return 1;}

  public java.util.List getEdgeViewsList(NodeView otherNode) {return null;}

  public int getShape () {return shape;}

  public void setShape(int s) { shape = s; }

  public void setSelectedPaint (Paint p) {selectedPaint = p;} 

  public Paint getSelectedPaint () {return selectedPaint;}

   public void setUnselectedPaint ( Paint p) {unselectedPaint = p;} 

   public Paint getUnselectedPaint () {return unselectedPaint;} 

  public void setBorderPaint ( Paint b ) {borderPaint = b;} 

  public Paint getBorderPaint () {return borderPaint;}

  public void setBorderWidth ( float bw ) {borderWidth = bw; } 

  public float getBorderWidth () {return borderWidth;}
  
  public void setBorder ( Stroke s) { border = s; }

  public Stroke getBorder () {return border;}

  public void setTransparency ( float trans ) { transparency = trans;} 
  
  public float getTransparency () { return transparency; }

  public boolean setWidth ( double w ) {width = w; return true;} 

  public double getWidth () {return width;}

  public boolean setHeight ( double h ) {height = h; return true;} 

  public double getHeight () { return height;}

  public giny.view.Label getLabel () { return label; }

  public void setLabel (Label l) { label = l; } // not in interface

  public int getDegree() { return degree; }

  public void setOffset ( double x, double y ) { offset.setLocation(x,y); }

  public Point2D getOffset () {return offset;}

  public void setXPosition(double x) {x_pos = x; } 

  public void setXPosition ( double x, boolean update ) {x_pos = x; } 
  
  public double getXPosition() { return x_pos; } 
  
  public void setYPosition(double y) {y_pos = y; }
  
  public void setYPosition ( double y, boolean update ) {y_pos = y; }
  
  public double getYPosition() {return y_pos;} 
  
  public void setNodePosition(boolean animate) {};  // WTF???
  
  public void select() {selected = true; } 
  
  public void unselect() {selected = false; } 
  
  public boolean isSelected() {return selected;} 

  public boolean setSelected(boolean s) { selected = s;  return selected;}
  
  public void setToolTip ( String tip ) { toolTip = tip;} 

  public String getToolTip ( ) { return toolTip;}  // not in the interface

  public void setLabelOffsetX(double x){ label_offset_x = x ;}

  public double getLabelOffsetX() {return label_offset_x;}

  public void setLabelOffsetY(double y){ label_offset_y = y;}

  public double getLabelOffsetY() {return label_offset_y;}

  public void setNodeLabelAnchor(int position) {node_label_anchor = position;}

  public int getNodeLabelAnchor(){return node_label_anchor;}


}
