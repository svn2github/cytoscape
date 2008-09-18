package org.cytoscape.view;

import java.awt.*;
import java.util.Iterator;
import org.cytoscape.*;
import java.awt.geom.Point2D;
import cytoscape.render.stateful.CustomGraphic;

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
  public org.cytoscape.view.Label getLabel () ;

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

    // MLC 09/18/08 BEGIN:
    //	public int getCustomGraphicCount();
    //	public Shape getCustomGraphicShape(int index);
    //	public Paint getCustomGraphicPaint(int index);
    //	public void removeCustomGraphic(int index);
    //	public void addCustomGraphic(Shape s, Paint p, int index);

    /**
     * Returns the number of custom graphic objects currently set on this
     * node view.
     * @deprecated use {@link #getNumCustomGraphics() getNumCustomGraphics()}.
     * Note that the new API methods work independent of the old API methods.
     * See {@link #addCustomGraphic(Shape,Paint,int) addCustomGraphic(Shape,Paint,int)}
     * for details.
     */
    @Deprecated public int getCustomGraphicCount();
    /**
     * Returns the shape of the custom graphic object at specified index on
     * this node view.  The index parameter must be in the range
     * [0, getCustomGraphicCount()-1].
     * @deprecated use {@link cytoscape.render.stateful.CustomGraphic#getShape() cytoscape.render.stateful.CustomGraphic.getShape()}.
     * Note that the new API methods work independent of the old API methods.
     * See {@link #addCustomGraphic(Shape,Paint,int) addCustomGraphic(Shape,Paint,int)}
     * for details.
     */
    @Deprecated public Shape getCustomGraphicShape(int index);
    /**
     * Returns the paint on the custom graphic object at specified index on
     * this node view.  The index parameter must be in the range
     * [0, getCustomGraphicCount()-1].
     * @deprecated use {@link cytoscape.render.stateful.CustomGraphic#getPaint() cytoscape.render.stateful.CustomGraphic.getPaint()}.
     * Note that the new API methods work independent of the old API methods.
     * See {@link #addCustomGraphic(Shape,Paint,int) addCustomGraphic(Shape,Paint,int)}
     * for details.
     */
    @Deprecated public Paint getCustomGraphicPaint(int index);
    /**
     * Removes the custom graphic object at specified index.  The index parameter
     * must be in the range [0, getCustomGraphicCount()-1].  Once the object
     * at specified index is removed, all object remaining and at a higher index
     * will be shifted such that their index is decreased by one.
     * @deprecated use {@link #removeCustomGraphic(CustomGraphic) removeCustomGraphic(CustomGraphic)}.
     * Note that the new API methods work independent of the old API methods.
     * See {@link #addCustomGraphic(Shape,Paint,int) addCustomGraphic(Shape,Paint,int)}
     * for details.
     */
    @Deprecated public void removeCustomGraphic(int index);

    /**
     * Adds a custom graphic object at specified index.  The index of an object
     * is only important in that objects with lower index are rendered before
     * objects with higher index; if objects overlap, this order may be important
     * to consider.  A custom graphic object consists of the specified shape
     * that is filled with the specified paint; the shape is placed relative to
     * this node's location.
     * @deprecated use {@link #addCustomGraphic(Shape,Paint,byte) addCustomGraphic(Shape,Paint,byte)}.
     * <P>The entire index-based custom graphic API has been deprecated.
     * This includes all the methods that refer to custom graphics using indices:
     * <PRE>
     *   public int addCustomGraphic(Shape s, Paint p, int index);
     *   public void removeCustomGraphic(int index);
     *   public Paint getCustomGraphicPaint(int index);
     *   public Shape getCustomGraphicShape(int index);
     *   public int getCustomGraphicCount();
     * </PRE>
     * <B>To keep things completetly backwards compatible
     * and to avoid introducing bugs, the new API methods are
     * completely independent from the the old API methods.  Thus,
     * a custom graphic added using the new API will not be
     * accessible from the old API and visa versa.</B>
     * <P>The reason for the deprecation is:
     * <OL>
     * <LI>Complexity in managing the indices.
     * <P>In order for multiple plugins to use the old API, each
     * must monitor deletions to custom graphics and update their
     * saved indices, since the indices will shift down as graphics
     * are deleted. This management isn't even possible with the old
     * API because there's no event mechanism to inform plugins when
     * the indices change. Also, each plugin must keep a list of all
     * indices for all graphics added, since the indices may not be
     * contiguous.
     * <LI>There is no way to ensure that an index you want to use
     * will not be used by another plugin by the time you attempt
     * to assign it (thread safety).
     * <P>Using indices forces the need for a locking mechanism to
     * ensure you are guaranteed a unique and correct index
     * independent of any other plugins.
     * </OL>
     * For more information, see <A HREF="http://cbio.mskcc.org/cytoscape/bugs/view.php?id=1500">Mantis Bug 1500</A>.
     */
    @Deprecated public void addCustomGraphic(Shape s, Paint p, int index);

    // NEW CUSTOM GRAPHIC OPS:

    /**
     * Adds a custom graphic, <EM>in draw order</EM>, to this
     * NodeView in a thread-safe way.  This is a convenience method
     * that is equivalent to calling:
     * <CODE>
     *   addCustomGraphic (new CustomGraphic (shape,paint,anchor))
     * </CODE>
     * except the the new CustomGraphic created is returned.
     * @param shape
     * @param paint
     * @param anchor The byte value from NodeDetails, that defines where the graphic anchor point lies on this NodeView's extents rectangle. A common anchor is NodeDetails.ANCHOR_CENTER.
     * @since Cytoscape 2.6
     * @throws IllegalArgumentException if shape or paint are null or anchor is not in the range 0 <= anchor <= NodeDetails.MAX_ANCHOR_VAL.
     * @return The CustomGraphic added to this NodeView.
     * @see #addCustomGraphic(CustomGraphic)
     * @see cytoscape.render.stateful.CustomGraphic
     */
    public CustomGraphic addCustomGraphic(Shape s, Paint p, byte anchor);
    /**
     * Adds a given CustomGraphic, <EM>in draw order</EM>, to this
     * NodeView in a thread-safe way.  Each CustomGraphic will be
     * drawn in the order is was added. So, if you care about draw
     * order (as for overlapping graphics), make sure you add them in
     * the order you desire.  Note that since CustomGraphics may be
     * added by multiple plugins, your additions may be interleaved
     * with others.
     *
     * <P>A CustomGraphic can only be associated with a NodeView
     * once.  If you wish to have a custom graphic, with the same
     * paint and shape information, occur in multiple places in the
     * draw order, simply create a new CustomGraphic and add it.
     *
     * @since Cytoscape 2.6
     * @throws IllegalArgumentException if shape or paint are null.
     * @return true if the CustomGraphic was added to this NodeView.
     *         false if this NodeView already contained this CustomGraphic.
     * @see cytoscape.render.stateful.CustomGraphic
     */
    public boolean addCustomGraphic (CustomGraphic cg);
    /**
     * A thread-safe way to determine if this NodeView contains a given custom graphic.
     * @param cg the CustomGraphic for which we are checking containment.
     * @since Cytoscape 2.6
     */
    public boolean containsCustomGraphic (CustomGraphic cg);
    /**
     * Return a non-null, read-only Iterator over all CustomGraphics contained in this NodeView.
     * The Iterator will return each CustomGraphic in draw order.
     * The Iterator cannot be used to modify the underlying set of CustomGraphics.
     * @return The CustomGraphics Iterator. If no CustomGraphics are
     * associated with this DNOdeView, an empty Iterator is returned.
     * @throws UnsupportedOperationException if an attempt is made to use the Iterator's remove() method.
     * @since Cytoscape 2.6
     */
    public Iterator<CustomGraphic> customGraphicIterator();
    /**
     * A thread-safe method for removing a given custom graphic from this NodeView.
     * @return true if the custom graphic was found an removed. Returns false if 
     *         cg is null or is not a custom graphic associated with this NodeView.
     * @since Cytoscape 2.6
     */
    public boolean removeCustomGraphic(CustomGraphic cg);
    /**
     * A thread-safe method returning the number of custom graphics
     * associated with this NodeView. If none are associated, zero is
     * returned.
     * @since Cytoscape 2.6
     */
    public int getNumCustomGraphics ();
    /**
     * Obtain the lock used for reading information about custom
     * graphics.  This is <EM>not</EM> needed for thread-safe custom graphic
     * operations, but only needed for use with
     * thread-compatible methods, such as customGraphicIterator().
     * For example, to iterate over all custom graphics without fear of
     * the underlying custom graphics being mutated, you could perform:
     * <PRE>
     *    NodeView nv = ...;
     *    CustomGraphic cg = null;
     *    synchronized (nv.customGraphicLock()) {
     *       Iterator<CustomGraphic> cgIt = nv.customGraphicIterator();
     *       while (cgIt.hasNext()) {
     *          cg = cgIt.next();
     *          // PERFORM your operations here.
     *       }
     *   }
     * </PRE>
     * NOTE: A better concurrency approach would be to return the read
     *       lock from a
     *       java.util.concurrent.locks.ReentrantReadWriteLock.
     *       However, this requires users to manually lock and unlock
     *       blocks of code where many times try{} finally{} blocks
     *       are needed and if any mistake are made, a NodeView may be
     *       permanently locked. Since concurrency will most
     *       likely be very low, we opt for the simpler approach of
     *       having users use synchronized {} blocks on a standard
     *       lock object.
     * @return the lock object used for custom graphics of this NodeView.
     */
    public Object customGraphicLock ();
    // MLC 09/18/08 END.
}
