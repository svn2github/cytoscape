//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.graphutil;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolox.util.*;

import giny.model.*;

import giny.view.*;
import phoebe.util.*;
import phoebe.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.beans.*;

import java.io.*;

import java.util.*;

import javax.swing.event.*;


/**
 * This Class is the BasicPNodeView. 
 * It has very limited support. Go look at plain old @see PNodeView.
  
 * If you are going to make your own NodeView then you might be in the
 * in the right place.  Because we are using Piccolo, it becomes
 * both appropriate and necessary to add only "PNode" Objects
 * to the SceneGraph.  At this point, if you are not familiar with
 * Piccolo, become that way, possibly by reading on.
  
 * So PNode is the Base class in Piccolo, and every viewable
 * object inherits from it.  There are a number of very useful
 * Piccolo provided nodes, like PClip, P3DRect, PPath, PText,
 * PImage and PLens. The phoebe default class, PNodeView is 
 * in fact a PClip. 
 
 * So, to role your own node, you have two options:
 * 1. Extend this class and overide the 
 * <code>paint( PPaintContext )</code> method.  You will
 * find the PPaintContext is REALLY cool. It provides a lot
 * of information about the scene being displayed, in addition to
 * the Graphics2D Object.  Other methods of note are the 
 * animate* methods and the layout methods.

 * As a note, the "DataRose" plugin that PShannon wrote, should be
 * "just work" by extending this class instead of the Y-Files one.
 
 * Don't forget that any number of child nodes can be added onto
 * this node, and they will by default move and scale and get
 * painted and all that goodeness at the same time as this one.
 * This should make it really easy to add complex displays, and
 * is one of the reasons why I really think that Piccolo is a good fit
 * for the Cytoscape Project.

 * 2. If you want to use one of the above mentioned Piccolo 
 * provided nodes, then it will be necessary to extend from
 * that node, and not this one.  Multiple inheritance might 
 * solve the problem, but in the meantime, by simple copying
 * all of the code from this class into your new class, it 
 * should just work(tm).  Since that is what I did to get the
 * PNodeView class.

 * Any and all questions should be directed to me.
 
 * @author Rowan Christmas
 */
public class StarNode extends PClip
  implements NodeView,
             PropertyChangeListener {

  /**
   * The index of this node in the RootGraph
   * note that this is always a negative number.
   */
  protected int rootGraphIndex;

  /**
   * The View to which we belong.
   */
  protected PGraphView view;
  
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
  // Constructors and Initialization
  //----------------------------------------//

  public StarNode ( int        node_index,
                     PGraphView view ) {
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
   * Create a new StarNode with the given physical attributes.
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
  public StarNode ( int        node_index,
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
      throw new IllegalArgumentException( "A StarNode must belong to a PGraphView" );
    }
    this.view = view;

    // Set the Index
    if ( node_index == Integer.MAX_VALUE ) {
    throw new IllegalArgumentException( "A node_index must be passed to create a StarNode" ); }
    
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

  protected void initializeNodeView () {
    

    // set the Stroke
    setStroke( (new BasicStroke( view.getNodeFloatProperty( rootGraphIndex, PGraphView.NODE_BORDER_WIDTH ) ) ) );
 
   
    // Create and Add the Label, which is not Pickable
    //label = new PLabel( ( String )view.getNodeObjectProperty( rootGraphIndex, PGraphView.NODE_LABEL ), this );
    RootGraph graph = view.getRootGraph();
     Node node = graph.getNode(getIndex());
     String l = node.getIdentifier();
     label = new PLabel ( l, this);
    label.updatePosition();
    label.setPickable(false);
    label.setLabelLocation( PLabel.NORTH );
    addChild(label);

    //tPathToEllipse( 0, 0, 100, 100 );

    setPathToPolyline( new float[] { 50, 75, 50, -20, 50 }, new float[] { 0, 50, 90, 50, 0 } );
    setBounds( 0, 0, 100, 100 );

    final PCamera camera1 = new PCamera();
    PLayer layer1 = new PLayer();

    PLocator locator = new PLocator () {
        
        public double locateX () {
          return ( getX() + getWidth() * .5 );
        }

        public double locateY () {
          return ( getY() + getHeight() * .5 );
        }

        public Point2D locatePoint () {
          return new Point2D.Double( locateX(), locateY() );
        }
      };




    StarShape s1 = new StarShape( "Alfred", new GradientPaint( 0,50 , java.awt.Color.red, 50, 0, java.awt.Color.orange ) );
    s1.setPathToRectangle( (float)getX(), (float)getY() , 50, 50 );
    layer1.addChild( s1 );

    StarShape s2 = new StarShape( "Betty", new GradientPaint( 50, 0, java.awt.Color.orange, 100, 50, java.awt.Color.green ) );
    s2.setPathToRectangle( (float)getX() + 50, (float)getY(), 50, 50 );
    layer1.addChild( s2 );
 
    StarShape s3 = new StarShape( "Cathy", new GradientPaint( 100, 50, java.awt.Color.green, 50, 110, java.awt.Color.blue ) );
    s3.setPathToRectangle( (float)getX()+ 50, (float)getY() + 50, 50, 50 );
    layer1.addChild( s3 );

    StarShape s4 = new StarShape( "Dennis", new GradientPaint( 50, 100, java.awt.Color.blue, 0, 50, java.awt.Color.red ) );
    s4.setPathToRectangle( (float)getX(), (float)getY() + 50, 50, 50 );
    layer1.addChild( s4 );
    
    
    s1.setPickable( false );
    s2.setPickable( false );
    s3.setPickable( false );
    s4.setPickable( false );
  

    camera1.setBounds( 0,0, 100, 100 );

    camera1.addLayer( layer1 );
    addChild( camera1 );
    
                                                             

    final PText tooltipNode = new PText();
    final PCamera camera = view.getCanvas().getCamera();
    tooltipNode.setPickable(false);
    camera1.addChild(tooltipNode);
		
    camera1.scaleView( 1.2 );

    camera1.addInputEventListener(new PBasicInputEventHandler() {	
			public void mouseMoved(PInputEvent event) {
				updateToolTip(event);
			}

			public void mouseDragged(PInputEvent event) {
				updateToolTip(event);
			}

			public void updateToolTip(PInputEvent event) {


        PNode n = event.getInputManager().getMouseOver().getPickedNode();
        String tooltipString;
        Point2D p = event.getCanvasPosition();
        
        //System.out.println( "Detect: "+ tooltipString+"."+n.getClass() );
        
        event.getPath().canvasToLocal(p, camera1);
        
        if ( p.getX() <= 50 && p.getY() <= 50 ) {
          tooltipString = "AAAAAAA";
        } else if (  p.getX() >= 50 && p.getY() <= 50 ) {
          tooltipString = "BBBBBBB";
        } else if (  p.getX() >= 50 && p.getY() >= 50 ) {
          tooltipString = "CCCCCCC";
        } else {
          tooltipString = "DDDDDDD";
        }


        tooltipNode.setText(tooltipString);
        tooltipNode.setOffset(p.getX() + 8, 
                              p.getY() - 8);
      }
      
		});

    
    final PHandle h = new PHandle( locator ) {
                                              
        public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
				localToParent(aLocalDimension);
				getParent().translate(aLocalDimension.getWidth(), aLocalDimension.getHeight());
				updateOffset();
			}			
		};
		
		h.addInputEventListener(new PBasicInputEventHandler() {
			public void mousePressed(PInputEvent aEvent) {
				h.setPaint(Color.YELLOW);
			}
			
			public void mouseReleased(PInputEvent aEvent) {
				h.setPaint(Color.WHITE);
			}
		});

    this.addChild( h );
    h.setParent( this );

 
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

  //------------------------------------------------------//
  // Get and Set Methods for all Common Viewable Elements
  //------------------------------------------------------//
public void moveBy(double x, double y)
  {
  }
  
public void setCenter ( double x, double y)
{
}
public void setLocation (double x, double y)
  {
  }
  public void setSize (double x, double y )
  {
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
   * TODO: Reconcile with Border Methods
   * @param width the currently set width of this node
   */
  public void setWidth ( double width ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                               PGraphView.NODE_WIDTH,
                               width );
  }

  /**
   * TODO: Reconcile with Border Methods
   * @return the currently set width of this node
   */
  public double getWidth () {
    return super.getWidth();
  }

  /**
   * TODO: Reconcile with Border Methods
   * @param height the currently set height of this node
   */
  public void setHeight ( double height ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                               PGraphView.NODE_HEIGHT,
                               height );
  }

  /**
   * TODO: Reconcile with Border Methods
   * @return the currently set height of this node
   */
  public double getHeight () {
    return super.getHeight();
  }

  /**
   * @param label the new value to be displayed by the Label
   */
  public void setLabel ( String label ) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_LABEL,
                                label );
  }

  /**
   * @param label the new value to be displayed by the Label
   */
  public void setLabelText ( String label ) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_LABEL,
                                label );
  }
  public void setSloppySelectionColor(Color c) {
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
   * public void actionPerformed(ActionEvent e) { if (
   * view.getSelectionHandler().isSelected( this ) ) {
   * view.getSelectionHandler().unselect( this ); } else {
   * view.getSelectionHandler().select( this ); }}
   */
  /**
   * We want to be able to hear when a Node changes a property, like  its
   * identifier or selected state. If the identifier changes the Label
   * chages to the new value If selection changes, this NodeView draws
   * itself as selected.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    // if (evt.getPropertyName()
//         .equals("identifier")) {
//       if (label != null) {
//         //pcs.firePropertyChange("identifier", null, evt.getNewValue() );
//         //label.setText(getNode().getIdentifier());

//         // PBounds lBounds = label.getBounds();
//         //                 float lWidth = new Double(lBounds.getWidth()).floatValue();
//         //                 float lHeight = new Double(lBounds.getHeight()).floatValue();
//         //                 setPathToRectangle(0f, 0f, lWidth + 5f, lHeight + 5f);

//         //                 //fitShapeToLabel();
//         //                 moveToFront();
//       }
//     } else if (evt.getPropertyName()
//                .equals("selected")) {
//       //System.out.println("Selction being changed");
//       Boolean bool = (Boolean) evt.getNewValue();

//       if (bool.booleanValue()) {
//         view.getSelectionHandler()
//           .select(this);
//       } else {
//         view.getSelectionHandler()
//           .unselect(this);
//       }

//       // set selection
//     }
  }
  
  
   public void updateOffset()
  {
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
  
  public class StarShape extends PPath {

    public StarShape ( String proprty, Paint color ) {
      setPathToRectangle( 0, 0, 50, 50 );
      setPaint( color );
      addClientProperty( "tooltip", proprty );
    }

  }
   /**
   * @deprecated
   * @see NodeView#setUnselectedPaint( Paint ) setUnselectedPaint
   */
  public void setFillColor ( Color color ) {
    setUnselectedPaint( color );
  }

  /**
   * @deprecated
   * @see NodeView#setBorderPaint( Paint ) setBorderPaint
   */  
  public void setLineColor ( Color color ) {
    setBorderPaint( color );
  }

  /**
   * @deprecated
   * @see NodeView#setBorder( Stroke ) setBorder
   * <B>Note:</B> The Y-Files "LineType" class is just a subclass of java.awt.BasicStroke,
   * so try using a java.awt.BasicStroke instead.  If needed I can make some defaults. 
   */
  public void setLineType ( Stroke stroke ) {
    setBorder( stroke );
  }

  /**
   * @deprecated
   * @see NodeView#setShape( int ) setShape
   * Although Y-Files uses ints, and I used ints, it shouldn't really matter.
   */
  public void setShapeType ( int shape ) {
    setShape( shape );
  }

  /**
   * @deprecated
   * @see NodeView#setLabel( String ) setLabel
   * <B>Note:</B> this replaces: <I>NodeLabel nl = nr.getLabel();
   *    nl.setText(na.getLabel());</I>
   */
  public void setText ( String label ) {
    setLabel( label );
  }
  
  /**
   * @deprecated
   * @see NodeView#setLabel( String ) setLabel
   * <B>Note:</B> this replaces: <I>NodeLabel nl = nr.getLabel();
   *    nl.setFont(na.getFont());</I>
   */
  public void setFont ( Font font ) {
    label.setFont( font );
  }

  /**
   * @deprecated
   * @see phoebe.PNodeView#addClientProperty( String, String ) setToolTip
   */
  public void setToolTip ( String tip ) {
    addClientProperty( "tooltip", tip );
  }


} //class StarNode
