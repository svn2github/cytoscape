package giny.view;

import java.awt.*;
import cytoscape.*;
import java.awt.geom.Point2D;

/** 
 * Any and all questions should be directed to me.
 
 * @author Rowan Christmas
 */
public interface NodeView  extends GraphViewObject {
  
  public static final int TRIANGLE = 0;
  public static final int DIAMOND = 1;
  public static final int ELLIPSE = 2;
  public static final int HEXAGON = 3;
  public static final int OCTAGON = 4;
  public static final int PARALELLOGRAM = 5;
  public static final int RECTANGLE = 6;
  public static final int ROUNDED_RECTANGLE = 7;

  /**
   * @return The Node we are a view on
   */
  public Node getNode () ;

  /**
   * @return the index of this node in the perspective to which we are in a view on.
   */
  public int getGraphPerspectiveIndex () ;

  /**
   * @return the index of this node in the root graph to which we are in a view on.
   */
  public int getRootGraphIndex () ;


  /**
   * @return The list of EdgeViews connecting these two nodes. Possibly null.
   */
  public java.util.List<EdgeView> getEdgeViewsList(NodeView otherNode) ;

  /**
   * Shape is currently defined via predefined variables in 
   * the NodeView interface. To get the actual java.awt.Shape
   * use getPathReference()
   * @return the current int-tpye shape
   */
  public int getShape () ;


  /**
   * This sets the Paint that will be used by this node
   * when it is painted as selected.
   * @param paint The Paint to be used
   */
  public void setSelectedPaint (Paint paint) ;

  /**
   * @return the currently set selection Paint
   */
  public Paint getSelectedPaint () ;



  /**
    * Set the deafult paint of this node
    * @param paint the default Paint of this node
    */
   public void setUnselectedPaint ( Paint paint ) ; 

   /**
    * @return the currently set paint
    */
   public Paint getUnselectedPaint () ;



  /**
   * @param b_paint the paint the border will use
   */ 
  public void setBorderPaint ( Paint b_paint ) ; 

  /**
   * @return the currently set BOrder Paint
   */
  public Paint getBorderPaint () ;

  /**
   * @param border_width The width of the border.
   */
  public void setBorderWidth ( float border_width ) ;

  /**
   * @return the currently set Border width
   */
  public float getBorderWidth () ;

  
  /**
   * @param stroke the new stroke for the border
   */
  public void setBorder ( Stroke stroke );


  /** 
   * @return the current border
   */
  public Stroke getBorder ();

  /**
   * @param trans new value for the transparency
   */
  public void setTransparency ( float trans );
  
  /**
   * @return the value for the transparency for this node
   */
  public float getTransparency ();


  /**
   * TODO: Reconcile with Border Methods
   * @param width the currently set width of this node
   */
  public boolean setWidth ( double width ) ;

  /**
   * TODO: Reconcile with Border Methods
   * @return the currently set width of this node
   */
  public double getWidth () ;

  /**
   * TODO: Reconcile with Border Methods
   * @param height the currently set height of this node
   */
  public boolean setHeight ( double height ) ;

  /**
   * TODO: Reconcile with Border Methods
   * @return the currently set height of this node
   */
  public double getHeight () ;

  /**
   * @return The Value of the label
   */
  public giny.view.Label getLabel () ;

  /**
   * @return the degree of the Node in the GraphPerspective.
   */
  public int getDegree() ;

  public void setOffset ( double x, double y );

  public Point2D getOffset ();

  /**
   * @param new_x_position the new X position for this node
   */
  public void setXPosition(double new_x_position) ;

  /**
   * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
   // TODO -- HACKY
   * @param  new_x_position for this node
   * @param  update if this is true, the node will move immediatly. 
   */
  public void setXPosition ( double new_x_position, boolean update ) ;
  
  /**
   * note that unless updateNode() has been called, this may not be 
   * the "real" location of this node
   * @return the current x position of this node
   * @see #setXPosition
   */
  public double getXPosition() ;
  
  /**
   * @param new_y_position the new Y position for this node
   */
  public void setYPosition(double new_y_position) ;
  
  /**
   * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
   // TODO -- HACKY
   * @param  new_y_position for this node
   * @param  update if this is true, the node will move immediatly. 
  */
  public void setYPosition ( double new_y_position, boolean update ) ;
  
  /**
   * note that unless updateNode() has been called, this may not be 
   * the "real" location of this node
   * @return the current y position of this node
   * @see #setYPosition
   */
  public double getYPosition() ;
  
  /**
   * moves this node to its stored x and y locations.
   */
  public void setNodePosition(boolean animate) ;
  
  /**
   * This draws us as selected
   */
  public void select() ;
  
  /**
   * This draws us as unselected
   */
  public void unselect() ;
  
  /**
   *
   */
  public boolean isSelected() ;

  /**
   *
   */
  public boolean setSelected(boolean selected) ;
  
  
  /**
   * Set a new shape for the Node, based on one of the pre-defined shapes
   * <B>Note:</B> calling setPathTo( Shape ), allows one to define their own
   * java.awt.Shape ( i.e. A picture of Johnny Cash )
   */
  public void setShape(int shape) ;
  
  /**
   * Sets what the tooltip will be for this NodeView
   */
  public void setToolTip ( String tip );

  public void setLabelOffsetX(double x);
  public void setLabelOffsetY(double y);
  public void setNodeLabelAnchor(int position);

  public double getLabelOffsetX();
  public double getLabelOffsetY();
  public int getNodeLabelAnchor();
}
