package metaviz;

// Piccolo imports
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolox.util.*;

// GINY imports
import giny.view.*;
import giny.model.*;
import giny.util.*;

// Phoebe imports
import phoebe.*;
import phoebe.util.*;

// Java imports
// awt
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
// beans
import java.beans.*;
// io
import java.io.*;
// util
import java.util.*;
// swing events
import javax.swing.event.*;
import javax.swing.*;

/**
 * This node type will feature an embedded camera, among some other controls,
 * that are designed to make it possible to view the networks of nodes who have 
 * meta-children.
 */

public class InternalCameraNode extends PPath
  implements NodeView,
             PropertyChangeListener {


  //----------------------------------------//
  // Regular Node View Things
  //----------------------------------------//
  
  /**
   * The index of this node in the RootGraph
   * note that this is always a negative number.
   */
  protected int rootGraphIndex;

  /**
   * The View to which we belong.
   */
  protected PGraphView view;
  protected RootGraph rootGraph;

  /**
   * Our label 
   * TODO: more extendable
   */
  protected PLabel label;

  /**
   * Our Selection toggle
   */
  protected boolean selected;

  /**
   * Our Visibility
   */
  protected boolean visible;

  /**
   * A boolean that tells us if we are updated to the current 
   * position, i.e. after a layout
   */
  protected boolean notUpdated;

  //----------------------------------------//
  // InternalCameraNode Specific Things
  //----------------------------------------//

  /**
   * We need our own PGraphView, since some nodes
   * will need to be in multiple layer groups.
   */
  protected PGraphView internalView;
  protected GraphPerspective internalPerspective;


  // button nodes
  protected  ButtonNode labelButton;
  protected  ButtonNode hideButton;
  protected  ButtonNode showButton;
  protected  ButtonNode membersButton;
  protected  ButtonNode mainviewButton;
  protected  ButtonNode newwindowButton;

  


  //----------------------------------------//
  // Constructors
  //----------------------------------------//

  public InternalCameraNode ( int node_index, PGraphView view ) {
     this ( node_index,
           view,
           Double.MAX_VALUE,
           Double.MAX_VALUE,
           Integer.MAX_VALUE,
           ( Paint )null,
           ( Paint )null,
           ( Paint )null,
           Float.MAX_VALUE,
           Double.MAX_VALUE,
           Double.MAX_VALUE,
           ( String )null );
  }

  /**
   * Create a new InternalCameraNode with the given physical attributes.
   * @param node_index The RootGraph Index of this node 
   * @param view the PGraphVIew that we belong to
   * @param x_positon the x_positon desired for this node
   * @param y_positon the y_positon desired for this node
   * @param shape the shape type
   * @param paint the Paint for this node
   * @param selection_paint the Paint when this node is selected
   * @param border_paint the boder Paint
   * @param border_width the width of the border
   * @param width the width of the node
   * @param height the height of the node
   * @param label the String to display on the label
   */
  public InternalCameraNode ( int        node_index,
                              PGraphView view,
                              double     x_positon,
                              double     y_positon,
                              int        shape,
                              Paint      paint,
                              Paint      selection_paint,
                              Paint      border_paint,
                              float      border_width,
                              double     width,
                              double     height,
                              String     label ) {
    // Call PNode Super Constructor
    super();

    // Set the PGraphView that we belong to
    if ( view == null ) {
      throw new IllegalArgumentException( "A ClipRadarNode must belong to a PGraphView" );
    }
    this.view = view;

    // Set the Index
    if ( node_index == Integer.MAX_VALUE ) {
      throw new IllegalArgumentException( "A node_index must be passed to create a ClipRadarNode" ); }
    
    if ( node_index >= 0 ) {
      this.rootGraphIndex = view.getGraphPerspective().getRootGraphNodeIndex( node_index );
    } else {
      this.rootGraphIndex = node_index;
    }
        
     // set NODE_X_POSITION
    if ( x_positon != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty( rootGraphIndex,
                                  PGraphView.NODE_X_POSITION,
                                  x_positon );
    }
    
    // set NODE_Y_POSITION
    if ( y_positon != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty( rootGraphIndex,
                                  PGraphView.NODE_Y_POSITION,
                                  y_positon );
    }
    
    // set NODE_SHAPE
    if ( shape != Integer.MAX_VALUE ) {
    view.setNodeIntProperty( rootGraphIndex,
                             PGraphView.NODE_SHAPE,
                             shape );
    }
    
    // set NODE_PAINT
    if ( paint != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_PAINT,
                                  paint );
    }
    
    // set NODE_SELECTION_PAINT
    if ( paint != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_SELECTION_PAINT,
                                  selection_paint );
    }
    
    // set NODE_BORDER_PAINT
    if ( border_paint != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_BORDER_PAINT,
                                  border_paint );
    }
    
    // set NODE_BORDER_WIDTH
    if ( border_width != Float.MAX_VALUE ) {
      view.setNodeFloatProperty( rootGraphIndex,
                                 PGraphView.NODE_BORDER_WIDTH,
                                 border_width );
    }
    
    // set NODE_WIDTH
    if ( width != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty( rootGraphIndex,
                                  PGraphView.NODE_WIDTH,
                                  width );
    }
    
    // set NODE_HEIGHT
    if ( height != Double.MAX_VALUE ) {
      view.setNodeDoubleProperty ( rootGraphIndex,
                                   PGraphView.NODE_HEIGHT,
                                   height );
    }
    
    // set NODE_LABEL
    if ( label != null ) {
      view.setNodeObjectProperty( rootGraphIndex,
                                  PGraphView.NODE_LABEL,
                                  label );
				
    }
    initializeNodeView();

  }
  
  /**
   * In this method we will create a new PGraphView. Make all of the
   * buttons and stuff, and get the layout of this node set up.  This
   * type of node is expected to be in a circle or square, it might look funny
   * in an ellipse or rectangle.
   */
  protected void initializeNodeView () {

    // TODO:
    // We need to do some checks so that only nodes
    // that have meta-children actaully get initialized here.

    // set the RootGraph from the view 
    this.rootGraph = view.getRootGraph();
    
 
 
      // Create and Add the Label, which is not Pickable
    label = new PLabel( ( String )view.getNodeObjectProperty( rootGraphIndex, PGraphView.NODE_LABEL )+" I: "+rootGraphIndex, this );
    label.updatePosition();
    label.setPickable(false);
    addChild(label);
    addClientProperty("tooltip", label);
    


     
    setUnselectedPaint( null );
    


    // create the internal structures.
    Node node = rootGraph.getNode( rootGraphIndex );
    internalPerspective = node.getGraphPerspective();
    internalView = new PGraphView( "Node: "+rootGraphIndex+" internal view.", internalPerspective );

    giny.util.SpringEmbeddedLayouter lay = new  giny.util.SpringEmbeddedLayouter( internalView );
    lay.doLayout();
  
    System.out.println( "There are: "+internalPerspective.getNodeCount()+" nodes in the new Perspective" );

    Class[] null_arg = new Class[0];
    
    try {
      labelButton = new ButtonNode( "Label", 
                                    getClass().getMethod( "hideShowLabel", null_arg ),
                                    this,
                                    null_arg );
      
      hideButton = new ButtonNode( "Hide Network",
                                   getClass().getMethod( "hideNetwork", null_arg ),
                                   this,
                                   null_arg );
      
      showButton = new ButtonNode( "Show Network",
                                   getClass().getMethod( "showNetwork", null_arg ),
                                   this,
                                   null_arg );
      
      membersButton = new ButtonNode( "Show Members",
                                      getClass().getMethod( "showMembers", null_arg ),
                                      this,
                                      null_arg );
      
      mainviewButton = new ButtonNode( "Swap Main View",
                                       getClass().getMethod( "swapMainView", null_arg ),
                                       this,
                                       null_arg );
      
      newwindowButton = new ButtonNode( "New Window",
                                        getClass().getMethod( "toNewWindow", null_arg ),
                                        this,
                                        null_arg );
    } catch ( Exception e ) {
    }
              
    // we should be a circle

    // figure out layout stuff

 

    double x = view.getNodeDoubleProperty ( rootGraphIndex,
                                            PGraphView.NODE_X_POSITION );
    double y = view.getNodeDoubleProperty ( rootGraphIndex,
                                            PGraphView.NODE_Y_POSITION );
    double h = view.getNodeDoubleProperty ( rootGraphIndex,
                                            PGraphView.NODE_HEIGHT );
    double w = view.getNodeDoubleProperty ( rootGraphIndex,
                                            PGraphView.NODE_WIDTH );               
    double xr2 = h * Math.sqrt(2);

    double a = x + .5 * w;
    double b = y + .5 * h;
    this.setPathToRectangle( (float)x, (float)y, (float)h, (float)w );

    // add the camera and figure out how to make it 
    // inscribed by the outer node.
    PCamera camera = new PCamera();
    camera.addLayer( internalView.getCanvas().getLayer() );
    this.addChild( camera );
    camera.setBounds( a - xr2, b- xr2, xr2 , xr2  );
    camera.setPaint( java.awt.Color.orange );
    camera.animateViewToCenterBounds( internalView.getCanvas().getLayer().getGlobalFullBounds(), true, 500l );
 //    PCamera camera2 = new PCamera();
//     PLayer layer2 = new PLayer();
//     layer2.addChild( PPath.createEllipse( 20, 20, 20, 20 ) );
//     camera2.addLayer( layer2 );
//     this.addChild( camera2 );
    
   
     


    //camera.setWidth( 2 * xr2 );
    //camera.setHeight( 2 * xr2 );
    //camera.setOffset( a - xr2, b - xr2 );
    

    
   

   
    // TODO: Remove?
    this.visible = true;
    this.selected = false;
    this.notUpdated = false;
    setPickable(true);
    invalidatePaint();



  }
  
  
  public int getIndex () {
    return rootGraphIndex;
  }



  /**
   * @param stroke the new stroke for the border
   */
  public void setBorder ( Stroke stroke ) {
    super.setStroke( stroke );
  }


  /** 
   * @return the current border
   */
  public Stroke getBorder () {
    return super.getStroke();
  }

  /**
   * @return the view we are in
   */
  public GraphView getGraphView() {
    return view;
  }

  /**
   * @return The Node we are a view on
   */
  public Node getNode () {
    return view.getGraphPerspective().getNode( rootGraphIndex );
  }

  /**
   * @return the index of this node in the perspective to which we are in a view on.
   */
  public int getGraphPerspectiveIndex () {
    return view.getGraphPerspective().getNodeIndex( rootGraphIndex );
  }

  /**
   * @return The list of EdgeViews connecting these two nodes. Possibly null.
   */
  public java.util.List getEdgeViewsList(NodeView otherNode) {
    return view.getEdgeViewsList( getNode(), otherNode.getNode() );
  }

    /**
   * Shape is currently defined via predefined variables in 
   * the NodeView interface. To get the actual java.awt.Shape
   * use getPathReference()
   * @return the current int-tpye shape
   */
  public int getShape () {
    return view.getNodeIntProperty( rootGraphIndex,
                                    PGraphView.NODE_SHAPE );
  }
   /**
   * Shape is currently defined via predefined variables in 
   * the NodeView interface. To get the actual java.awt.Shape
   * use getPathReference()
   * @return the current int-tpye shape
   */
  public int getShapeType () {
    return view.getNodeIntProperty( rootGraphIndex,
                                    PGraphView.NODE_SHAPE );
  }
  /**
   * This sets the Paint that will be used by this node
   * when it is painted as selected.
   * @param paint The Paint to be used
   */
  public void setSelectedPaint (Paint paint) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_SELECTION_PAINT,
                                paint );
  }

  /**
   * @return the currently set selection Paint
   */
  public Paint getSelectedPaint () {
    return ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                PGraphView.NODE_SELECTION_PAINT );
  }

  /**
   * Set the deafult paint of this node
   * @param paint the default Paint of this node
   */
  public void setUnselectedPaint ( Paint paint ) { 
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_PAINT,
                                paint );
  
    super.setPaint( paint );
  }

  /**
   * @return the currently set paint
   */
  public Paint getUnselectedPaint () {
    return ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                PGraphView.NODE_PAINT );
  }

  /**
   * @param b_paint the paint the border will use
   */ 
  public void setBorderPaint ( Paint b_paint ) { 
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_BORDER_PAINT,
                                b_paint );
    super.setStrokePaint( b_paint );
  }
  
  /**
   * @return the currently set BOrder Paint
   */
  public Paint getBorderPaint () {
    return ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                PGraphView.NODE_BORDER_PAINT );
  }

  /**
   * @param border_width The width of the border.
   */
  public void setBorderWidth ( float border_width ) {
    view.setNodeFloatProperty( rootGraphIndex,
                               PGraphView.NODE_BORDER_WIDTH,
                               border_width );
    super.setStroke( new BasicStroke( border_width ) );
  }

  /**
   * @return the currently set Border width
   */
  public float getBorderWidth () {
    return view.getNodeFloatProperty( rootGraphIndex,
                                      PGraphView.NODE_BORDER_WIDTH );
  }

 
  /**
   * @param label the new value to be displayed by the Label
   */
  public void setLabel ( String label ) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_LABEL,
                                label );
    if( label != null ) {
      this.label.setText( label );
    }
  
  
  }
  
   /**
   * @param label the new value to be displayed by the Label
   */
  public void setLabelText ( String label ) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_LABEL,
                                label );
    if( label != null ) {
      this.label.setText( label );
    }
  
  
  }
  

  /**
   * @return The Value of the label
   */
  public String getLabel () {
    return ( String )view.getNodeObjectProperty( rootGraphIndex,
                                                 PGraphView.NODE_LABEL);
  }
/**
   * @return The Value of the label
   */
  public String getLabelText () {
    return ( String )view.getNodeObjectProperty( rootGraphIndex,
                                                 PGraphView.NODE_LABEL);
  }

  /**
   * @return the degree of the Node in the GraphPerspective.
   */
  public int getDegree() {
    return view.getGraphPerspective().getDegree(getNode());
  }

   /**
   * We want to be able to hear when a Node changes a property, like  its
   * identifier or selected state. If the identifier changes the Label
   * chages to the new value If selection changes, this NodeView draws
   * itself as selected.
   */
  public void propertyChange(PropertyChangeEvent evt) {

        
    
   
  }
  
  public void updateOffset () {
    firePropertyChange("Offset", null, this);
  }


  /**
   * Set the location of this node
   */
  public void setOffset(
                        double x,
                        double y) {
    //Point2D oldOffset = getOffset();
    super.setOffset(x, y);
    firePropertyChange("Offset", null, this);
  }

  /**
   *  Set the location of this node
   */
  public void setOffset(java.awt.geom.Point2D point) {
    //Point2D oldOffset = getOffset();
    super.setOffset(point);
    firePropertyChange("Offset", null, this);
  }

  /**
   * Move this node relative to its current location
   */
  public void offset(
                     double dx,
                     double dy) {
    //Point2D oldOffset = getOffset();
    super.offset(dx, dy);
    firePropertyChange("Offset", null, this);
  }

 
  /**
   * @param the new_x_position for this node
   */
  public void setXPosition(double new_x_position) {
    setXPosition( new_x_position, true );
  }

  /**
   * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
   // TODO -- HACKY
   * @param  new_x_position for this node
   * @param  update if this is true, the node will move immediatly. 
   */
  public void setXPosition ( double new_x_position, boolean update ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_X_POSITION,
                                new_x_position );
    if ( update ) {
      setNodePosition( false );
    } else {
      notUpdated = true;
    }
  }

  /**
   * @return the current x position of this node
   * @see setXPosition
   * note that unless updateNode() has been called, this may not be 
   * the "real" location of this node
   */
  public double getXPosition() {
    
    // Note that this is rather sneaky, hehe.
    // The reason is that to allow for nodes to be moved around by the 
    // mouse using common Piccolo methods, like the PSelectionEventHandler
    // it is necessary to return where the node was moved to

    if (notUpdated) {
      return view.getNodeDoubleProperty( rootGraphIndex,
                                         PGraphView.NODE_X_POSITION );
    } else {
      return getOffset().getX();
    }
  }

  /**
   * @param the new_y_position for this node
   */
  public void setYPosition(double new_y_position) {
    setYPosition( new_y_position, true );
  }

  /**
   * Set udpdate to false in order to do a layout, and then call updateNode on all the nodes..
   // TODO -- HACKY
   * @param  new_y_position for this node
   * @param  update if this is true, the node will move immediatly. 
   */
  public void setYPosition ( double new_y_position, boolean update ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                                PGraphView.NODE_Y_POSITION,
                                new_y_position );
    if ( update ) {
      setNodePosition( false );
    } else {
      notUpdated = true;
    }
  }

  /**
   * @return the current y position of this node
   * @see setYPosition
   * note that unless updateNode() has been called, this may not be 
   * the "real" location of this node
   */
  public double getYPosition() {
    
    // Note that this is rather sneaky, hehe.
    // The reason is that to allow for nodes to be moved around by the 
    // mouse using common Piccolo methods, like the PSelectionEventHandler
    // it is necessary to return where the node was moved to

    if (notUpdated) {
      return view.getNodeDoubleProperty( rootGraphIndex,
                                         PGraphView.NODE_Y_POSITION );
    } else {
      return getOffset().getY();
    }
  }

  /**
   * moves this node to its stored x and y locations.
   */
  public void setNodePosition(boolean animate) {
    if (notUpdated) {
      if (animate) {
        PTransformActivity activity = 
          this.
          animateToPositionScaleRotation( 
                                         view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
                                         view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ),
                                         1, 0, 500);
        //animate the movement to the new position
      }

      //just move to the new position
      setOffset( view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
                 view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ) );
      firePropertyChange("Offset", null, this);
    }
    notUpdated = false;
  }




  /**
   * This draws us as selected
   */
  public void select() {
    selected = true;
    drawSelected();

    //graphNode.setSelected( true ); // TODO
  }

  /**
   * This draws us as unselected
   */
  public void unselect() {
    selected = false;
    drawUnSelected();

    //graphNode.setSelected( false ); // TODO
  }

  /**
   *
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   *
   */
  public boolean setSelected(boolean selected) {
    if (selected) {
      //  drawSelected();
      view.getSelectionHandler()
        .select(this);
    } else {
      //  drawUnSelected();
      view.getSelectionHandler()
        .unselect(this);
    }

    return selected;
  }
  //****************************************************************
  // Painting
  //****************************************************************

  /**
   *
   */
  protected void paint(PPaintContext paintContext) {
    super.paint( paintContext );
    // This Might be a good place to do some overriding
  }

  /**
   * Sets the Color to a darker shade
   */
  public void drawSelected() {
    
    super.setPaint( ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                         PGraphView.NODE_SELECTION_PAINT ) );
  }

  /**
   * Draws the node with normal color
   */
  public void drawUnSelected() {
    super.setPaint( ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                         PGraphView.NODE_PAINT ) );
  }

  

  /**
   * Overridden method so that this node is aware of its bounds being changed
   * so  that it can tell its label and edges to change their position
   * accordingly.
   */
  public boolean setBounds(
                           double x,
                           double y,
                           double width,
                           double height) {
    boolean b = super.setBounds(x, y, width, height);

    firePropertyChange("BoundsChanged", null, this);
    label.updatePosition();

    return b;
  }

  /**
   * Set a new shape for the Node, based on one of the pre-defined shapes
   * <B>Note:</B> calling setPathTo( Shape ), allows one to define their own
   * java.awt.Shape ( i.e. A picture of Johnny Cash )
   */
  public void setShape(int shape) {
  
    float x = ( new Float( view.getNodeDoubleProperty( rootGraphIndex,
                                                       PGraphView.NODE_WIDTH ) ) ).floatValue();

    float y = ( new Float( view.getNodeDoubleProperty( rootGraphIndex,
                                                       PGraphView.NODE_HEIGHT) ) ).floatValue();

    view.setNodeIntProperty( rootGraphIndex,
                             PGraphView.NODE_SHAPE,
                             shape );


    if (shape == TRIANGLE) {
      //make a trianlge
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {
                                        .5f * x,
                                        0f * x,
                                        1f * x,
                                        .5f * x
                                      },
                                      new float[] {
                                        1f * y,
                                        0f * y,
                                        0f * y,
                                        1f * y
                                      })).getPathReference());
    } else if (shape == DIAMOND) {
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {
                                        10f * x,
                                        20f * x,
                                        10f * x,
                                        0f * x,
                                        10f * x
                                      },
                                      new float[] {
                                        0f * y,
                                        10f * y,
                                        20f * y,
                                        10f * y,
                                        0f * y
                                      })).getPathReference());
    } else if (shape == ELLIPSE) {
      setPathTo(
                (PPath.createEllipse((float) getBounds().getX(),
                                     (float) getBounds().getY(), (float) getBounds().getWidth(),
                                     (float) getBounds().getHeight())).getPathReference());
    } else if (shape == HEXAGON) {
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {
                                        0f * x,
                                        1f * x,
                                        2f * x,
                                        3f * x,
                                        2f * x,
                                        1f * x,
                                        0f * x
                                      },
                                      new float[] {
                                        1f * y,
                                        2f * y,
                                        2f * y,
                                        1f * y,
                                        0f * y,
                                        0f * y,
                                        1f * y
                                      })).getPathReference());
    } else if (shape == OCTAGON) {
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {
                                        0f * x,
                                        0f * x,
                                        1f * x,
                                        2f * x,
                                        3f * x,
                                        3f * x,
                                        2f * x,
                                        1f * x,
                                        0f * x
                                      },
                                      new float[] {
                                        1f * y,
                                        2f * y,
                                        3f * y,
                                        3f * y,
                                        2f * y,
                                        1f * y,
                                        0f * y,
                                        0f * y,
                                        1f * y
                                      })).getPathReference());
    } else if (shape == PARALELLOGRAM) {
      setPathTo(
                (PPath.createPolyline(
                                      new float[] {
                                        0f * x,
                                        1f * x,
                                        3f * x,
                                        2f * x,
                                        0f * x
                                      },
                                      new float[] {
                                        0f * y,
                                        1f * y,
                                        1f * y,
                                        0f * y,
                                        0f * y
                                      })).getPathReference());
    } else if (shape == RECTANGLE) {
      setPathTo(
                (PPath.createRectangle((float) getBounds().getX(),
                                       (float) getBounds().getY(), (float) getBounds().getWidth(),
                                       (float) getBounds().getHeight())).getPathReference());
    }
  }

  /**
   * Set the new shape of the node, with a given
   * new height and width
   *
   * @param shape the shape type
   * @param width the new width
   * @param height the new height
   */
  public void setShape(
                       int shape,
                       double width,
                       double height) {
    setWidth( width );
    setHeight( height );
    setShape( shape );
  }
  

}
