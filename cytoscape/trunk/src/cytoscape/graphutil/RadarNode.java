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
import  phoebe.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.beans.*;

import java.io.*;

import java.util.*;

import javax.swing.event.*;


/**
 * This Class is the BasicRadarNode. 
 * It has very limited support. Go look at plain old @see RadarNode.
  
 * If you are going to make your own NodeView then you might be in the
 * in the right place.  Because we are using Piccolo, it becomes
 * both appropriate and necessary to add only "PNode" Objects
 * to the SceneGraph.  At this point, if you are not familiar with
 * Piccolo, become that way, possibly by reading on.
  
 * So PNode is the Base class in Piccolo, and every viewable
 * object inherits from it.  There are a number of very useful
 * Piccolo provided nodes, like PClip, P3DRect, PPath, PText,
 * PImage and PLens. The phoebe default class, RadarNode is 
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
 * RadarNode class.

 * Any and all questions should be directed to me.
 
 * @author Rowan Christmas
 */
public class RadarNode extends PPath
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
  
   protected int size = 6;
  double scale = 10; 		     
  protected Vector data = new Vector ();
  protected Vector lamda;
  protected float[] values = new float[size];
  protected String[] conditions;
  float[] X = new float [size];
  float[] Y = new float [size];
  
  protected ColorInterpolator colorInterpolatorPositive;
  protected ColorInterpolator colorInterpolatorNegative;

  protected Map wedgeMap;

  //----------------------------------------//
  // Constructors and Initialization
  //----------------------------------------//

  public RadarNode ( int        node_index,
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
*/
  public RadarNode ( int node_index, PGraphView view, Vector data, Vector lamda, String[] conditions, ColorInterpolator interpolP, ColorInterpolator interpolN) {
    this( node_index, view );
    this.data = data;
    this.lamda = lamda;
    this.conditions = conditions;
    this.colorInterpolatorPositive = interpolP;
    this.colorInterpolatorNegative = interpolN;
    colorInterpolatorNegative.addPropertyChangeListener( this );
    colorInterpolatorPositive.addPropertyChangeListener( this );
    wedgeMap = new HashMap();
    // set up the node
    drawNodeView();
  }

  /**
   * Create a new RadarNode with the given physical attributes.
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
  public RadarNode ( int        node_index,
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
      throw new IllegalArgumentException( "A RadarNode must belong to a PGraphView" );
    }
    this.view = view;

    // Set the Index
    if ( node_index == Integer.MAX_VALUE ) {
    throw new IllegalArgumentException( "A node_index must be passed to create a RadarNode" ); }
    
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

    //initializeNodeView();

  }
  
   public void addExpressionData ( Vector data, Vector lamda ) {
    this.data = data;
    this.lamda = lamda;
    drawNodeView();
  }


  protected void updateWedges () {
    Iterator wedges = wedgeMap.keySet().iterator();
    while ( wedges.hasNext() ) {
      PPath wedge = ( PPath )wedges.next();
      double[] values = ( double[] )wedgeMap.get( wedge );
      if ( values[0] < 0 ) {
        wedge.setStrokePaint( colorInterpolatorNegative.colorFromValue( values[1] ) );
      } else {
        wedge.setStrokePaint( colorInterpolatorPositive.colorFromValue( values[1] ) );
      }
    }
  }
  
  
   protected void drawNodeView()
  {
	  RootGraph graph = view.getRootGraph();
    Node node = graph.getNode(getIndex());
    String l = node.getIdentifier();
    label = new PLabel ( l, this);
    label.updatePosition();
    label.setPickable(false);
    label.setLabelLocation( PLabel.NORTH );
    

    // set the Node Position
    setOffset( view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
               view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ) );
    
    	       
   
    // set the Stroke
    setStroke( new BasicStroke( .7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f )  );

    setPaint( ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                   PGraphView.NODE_PAINT ) );
    

    


    final float x = (float)getX();
    final float y = (float)getY();

    final float xstart = .5f * (float)getWidth()  + x;
    final float ystart = .5f * (float)getHeight() + y;
    
    
    
    //add circulars
    addCircularChildren(xstart, ystart);
	    
    //add wedges
    double theta;
    
    size = data.size();
    X = new float [size];
    Y = new float [size];
    values = new float[size];
    
    double expression;
    int lamdaValue;

    for (int n = 0; n < size; n++)
      {
        theta = (360*n)/size;

        expression = ( ( Double )data.get( n ) ).doubleValue();
        lamdaValue = ( ( Double )lamda.get( n ) ).intValue();
        expression = expression * 100;
      

        //System.out.println( "Value: "+expression+" lamda: "+lamdaValue+" for : "+getLabel() );



        X[n] = xstart + Math.abs( ( float )expression ) *
          (float)Math.sin(Math.toRadians(theta));
	   
	    
        Y[n] = ystart + Math.abs( ( float )expression ) *
          (float)Math.cos(Math.toRadians(theta));
     
        
        PPath wedge = new PPath();
        wedgeMap.put( wedge, new double[] { expression, lamdaValue } );
        wedge.moveTo( xstart, ystart );
        wedge.lineTo( X[n], Y[n] );
        wedge.closePath();
        wedge.setStroke( new BasicStroke( 5 ) );


        if ( expression < 0 ) {

          wedge.setStrokePaint( colorInterpolatorNegative.colorFromValue( lamdaValue ) );

	  //GradientPaint( bpx, bpy, start, X[n], Y[n], end ) );
          
        
        } else {
          // POSITIVE
          wedge.setStrokePaint( colorInterpolatorPositive.colorFromValue( lamdaValue ) );


        }
       
       // if ( n == 0)
         // moveTo(X[n], Y[n]);
        //else
          //lineTo(X[n], Y[n]);
        //if (n == size - 1)
          //closePath();
	   	
        addChild(wedge);
        wedge.addClientProperty("tooltip", getLabel()+'\n'+conditions[n] + '\n' +"value: " + ( expression/100 )+ '\n' +"lamda: "+lamdaValue);
	   
      }//end for
    
    PLocator locator = new PLocator () {
        
        public double locateX () {
          //return ( getX() + getWidth() * .5 );
          return (double)xstart;
        }

        public double locateY () {
          //return ( getY() + getHeight() * .5 );
          return (double)ystart;

        }

        public Point2D locatePoint () {
          return new Point2D.Double( locateX(), locateY() );
        }
      };



    final PHandle h = new PHandle( locator ) {
                                              
        public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent) {
          localToParent(aLocalDimension);
          getParent().translate(aLocalDimension.getWidth(), aLocalDimension.getHeight());
          updateOffset();
        }			
        
        public String toString () {
          return getLabel();
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
    h.addClientProperty("tooltip", getLabel() );
    this.visible = true;
    this.selected = false;
    this.notUpdated = false;
    setPickable(true);
    invalidatePaint();
	    
  }
  
  protected void addCircularChildren(float xstart, float ystart)
  {
	  PPath circluar10 = new PPath();
    circluar10.setPathToEllipse( xstart - 10, ystart -10, 20, 20 );
    circluar10.setPaint( null );
    circluar10.setStrokePaint( java.awt.Color.black );
    circluar10.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar20 = new PPath();
    circluar20.setPathToEllipse( xstart - 20, ystart -20, 40, 40 );
    circluar20.setPaint( null ); 
    circluar20.setStrokePaint( java.awt.Color.black );
    PPath circluar30 = new PPath();
    circluar30.setPathToEllipse( xstart - 30, ystart -30, 60, 60 );
    circluar30.setPaint( null );
    circluar30.setStrokePaint( java.awt.Color.black );
    circluar30.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar40 = new PPath();
    circluar40.setPathToEllipse( xstart - 40, ystart -40, 80, 80 );
    circluar40.setPaint( null );
    circluar40.setStrokePaint( java.awt.Color.black );
    PPath circluar50 = new PPath();
    circluar50.setPathToEllipse( xstart - 50, ystart -50, 100, 100 );
    circluar50.setPaint( null );
    circluar50.setStrokePaint( java.awt.Color.black );
    circluar50.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar60 = new PPath();
    circluar60.setPathToEllipse( xstart - 60, ystart -60, 120, 120 );
    circluar60.setPaint( null );
    circluar60.setStrokePaint( java.awt.Color.black );
    PPath circluar70 = new PPath();
    circluar70.setPathToEllipse( xstart - 70, ystart -70, 140, 140 );
    circluar70.setPaint( null );
    circluar70.setStrokePaint( java.awt.Color.black );
    circluar70.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar80 = new PPath();
    circluar80.setPathToEllipse( xstart - 80, ystart -80, 160, 160 );
    circluar80.setPaint( null );
    circluar80.setStrokePaint( java.awt.Color.black );
    PPath circluar90 = new PPath();
    circluar90.setPathToEllipse( xstart - 90, ystart -90, 180, 180 );
    circluar90.setPaint( null );
    circluar90.setStrokePaint( java.awt.Color.black );
    circluar90.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );


    addChild( circluar10 );
    addChild( circluar20 );
    addChild( circluar30 );
    addChild( circluar40 );
    addChild( circluar50 );
    addChild( circluar60 );
    addChild( circluar70 );
    addChild( circluar80 );
    addChild( circluar90 );
  }

  

  protected void initializeNodeView () {

    label = new PLabel( ( String )view.getNodeObjectProperty( rootGraphIndex, PGraphView.NODE_LABEL ), this );
    label.updatePosition();
    label.setPickable(false);
    label.setLabelLocation( PLabel.NORTH );
    

    // set the Node Position
    setOffset( view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
               view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ) );
   
    // set the Stroke
    setStroke( (new BasicStroke( view.getNodeFloatProperty( rootGraphIndex, PGraphView.NODE_BORDER_WIDTH ) ) ) );

    setBounds( 0, 0, 100, 100 );
     
    float x = (float)getX();
    float y = (float)getY();

    float x5 = .5f * (float)getWidth()  + x;
    float y5 = .5f * (float)getHeight() + y;


    double theta1 = 0;
    double theta3 = 45;
    double theta2 = ( theta3 - theta1 ) / 2 + theta1;
    
 
    PPath wedge1 = new PPath();
    wedge1.moveTo( x5, y5 );
    wedge1.lineTo( x5 + 50f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 50f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge1.closePath();
    wedge1.setStroke( new BasicStroke( 3 ) );
    wedge1.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.red, x5 + 50f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 50f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.orange ) );
     
    theta1 = 45;
    theta3 = 90;
    theta2 = ( theta3 - theta1 ) / 2 + theta1;
   
    PPath wedge2 = new PPath();
    wedge2.moveTo( x5, y5 );
    wedge2.lineTo( x5 + 25f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 25f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge2.closePath();
    wedge2.setStroke( new BasicStroke( 3 ) );
    wedge2.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.orange, x5 + 25f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 25f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.yellow ) );

    theta1 = 90;
    theta3 = 135;
    theta2 = ( theta3 - theta1 ) / 2 + theta1;
   
    PPath wedge3 = new PPath();
    wedge3.moveTo( x5, y5 );
    wedge3.lineTo( x5 + 70f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 70f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge3.setStroke( new BasicStroke( 3 ) );
    wedge3.closePath();
    wedge3.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.yellow, x5 + 70f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 70f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.green ) );

    theta1 = 135;
    theta3 = 180;
    theta2 = ( theta3 - theta1 ) / 2 + theta1;
   
    PPath wedge4 = new PPath();
    wedge4.moveTo( x5, y5 );
    wedge4.lineTo( x5 + 40f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 40f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge4.setStroke( new BasicStroke( 3 ) );
    wedge4.closePath();
    wedge4.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.green, x5 + 40f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 40f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.blue ) );

    theta1 = 180;
    theta3 = 225;
    theta2 = ( theta3 - theta1 ) / 2 + theta1;
   
    PPath wedge5 = new PPath();
    wedge5.moveTo( x5, y5 );
    wedge5.lineTo( x5 + 30f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 30f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge5.setStroke( new BasicStroke( 3 ) );
    wedge5.closePath();
    wedge5.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.blue, x5 + 30f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 30f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.magenta ) );
    
    theta1 = 225;
    theta3 = 270;
    theta2 = ( theta3 - theta1 ) / 2 + theta1;
    
    PPath wedge6 = new PPath();
    wedge6.moveTo( x5, y5 );
    wedge6.lineTo( x5 + 55f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 55f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge6.setStroke( new BasicStroke( 3 ) );
    wedge6.closePath();
    wedge6.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.magenta, x5 + 55f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 55f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.cyan ) );
    
    theta1 = 270;
    theta3 = 315;
    theta2 = ( theta3 - theta1 ) / 2 + theta1;
    
    PPath wedge7 = new PPath();
    wedge7.moveTo( x5, y5 );
    wedge7.lineTo( x5 + 10f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 10f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge7.setStroke( new BasicStroke( 3 ) );
    wedge7.closePath();
    wedge7.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.cyan, x5 + 10f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 10f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.black ) );

    theta1 = 315;
    theta3 = 360;
    theta2 = ( theta3 - theta1 ) / 2 + theta1;
    
    PPath wedge8 = new PPath();
    wedge8.moveTo( x5, y5 );
    wedge8.lineTo( x5 + 40f * (float)Math.cos( Math.toRadians( theta1 )  ), y5 + 40f * (float)Math.sin(Math.toRadians( theta1 ) ) );
    wedge8.setStroke( new BasicStroke( 3 ) );
    wedge8.closePath();
    wedge8.setStrokePaint( new GradientPaint( x5 ,y5 , java.awt.Color.black, x5 + 40f * (float)Math.cos( Math.toRadians( theta2 ) ), y5 + 40f * (float)Math.sin( Math.toRadians( theta2 )  ), java.awt.Color.red ) );


    PPath circluar10 = new PPath();
    circluar10.setPathToEllipse( x5 - 10, y5 -10, 20, 20 );
    circluar10.setPaint( null );
    circluar10.setStrokePaint( java.awt.Color.lightGray );
    circluar10.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar20 = new PPath();
    circluar20.setPathToEllipse( x5 - 20, y5 -20, 40, 40 );
    circluar20.setPaint( null ); 
    circluar20.setStrokePaint( java.awt.Color.lightGray );
    PPath circluar30 = new PPath();
    circluar30.setPathToEllipse( x5 - 30, y5 -30, 60, 60 );
    circluar30.setPaint( null );
    circluar30.setStrokePaint( java.awt.Color.lightGray );
    circluar30.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar40 = new PPath();
    circluar40.setPathToEllipse( x5 - 40, y5 -40, 80, 80 );
    circluar40.setPaint( null );
    circluar40.setStrokePaint( java.awt.Color.lightGray );
    PPath circluar50 = new PPath();
    circluar50.setPathToEllipse( x5 - 50, y5 -50, 100, 100 );
    circluar50.setPaint( null );
    circluar50.setStrokePaint( java.awt.Color.lightGray );
    circluar50.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar60 = new PPath();
    circluar60.setPathToEllipse( x5 - 60, y5 -60, 120, 120 );
    circluar60.setPaint( null );
    circluar60.setStrokePaint( java.awt.Color.lightGray );
    PPath circluar70 = new PPath();
    circluar70.setPathToEllipse( x5 - 70, y5 -70, 140, 140 );
    circluar70.setPaint( null );
    circluar70.setStrokePaint( java.awt.Color.lightGray );
    circluar70.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );
    PPath circluar80 = new PPath();
    circluar80.setPathToEllipse( x5 - 80, y5 -80, 160, 160 );
    circluar80.setPaint( null );
    circluar80.setStrokePaint( java.awt.Color.lightGray );
    PPath circluar90 = new PPath();
    circluar90.setPathToEllipse( x5 - 90, y5 -90, 180, 180 );
    circluar90.setPaint( null );
    circluar90.setStrokePaint( java.awt.Color.lightGray );
    circluar90.setStroke( new BasicStroke (.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 1, 2}, 2f ) );


    addChild( circluar10 );
    addChild( circluar20 );
    addChild( circluar30 );
    addChild( circluar40 );
    addChild( circluar50 );
    addChild( circluar60 );
    addChild( circluar70 );
    addChild( circluar80 );
    addChild( circluar90 );

    addChild( wedge1 );
    addChild( wedge2 );
    addChild( wedge3 );
    addChild( wedge4 );
    addChild( wedge5 );
    addChild( wedge6 );
    addChild( wedge7 );
    addChild( wedge8 );
      

    wedge1.addClientProperty("tooltip", "Condition 1");
    wedge2.addClientProperty("tooltip", "Condition 2");
    wedge3.addClientProperty("tooltip", "Condition 3");
    wedge4.addClientProperty("tooltip", "Condition 4");
    wedge5.addClientProperty("tooltip", "Condition 5");
    wedge6.addClientProperty("tooltip", "Condition 6");
    wedge7.addClientProperty("tooltip", "Condition 7");
    wedge8.addClientProperty("tooltip", "Condition 8");

  //   final PhoebeText tooltipNode = new PhoebeText();
//     final PCamera camera = view.getCanvas().getCamera();
//     tooltipNode.setPickable(false);
//     camera.addChild(tooltipNode);
//     camera.addInputEventListener(new PBasicInputEventHandler() {	
// 			public void mouseMoved(PInputEvent event) {
// 				updateToolTip(event);
// 			}

// 			public void mouseDragged(PInputEvent event) {
// 				updateToolTip(event);
// 			}

// 			public void updateToolTip(PInputEvent event) {


//         PNode n = event.getInputManager().getMouseOver().getPickedNode();
//         String tooltipString =  (String) n.getClientProperty("tooltip");
//         Point2D p = event.getCanvasPosition();
             
//         event.getPath().canvasToLocal(p, camera);
     
//         tooltipNode.setText(tooltipString);
//         tooltipNode.setOffset(p.getX() + 8, 
//                               p.getY() - 8);
//       }
      
// 		});



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

//   public class PhoebeText extends PNode {
    
//     PText text;

//     public PhoebeText () {
//       super();
//       text = new PText();
//       addChild( text );
//       setPaint( java.awt.Color.yellow );
//     }

//     public void setText ( String new_text ) {
//       text.setText( new_text );
//       setBounds( text.getFullBounds() );
//     }

//     public void setBackground ( int color ) {
//       switch ( color ) {
//       case 1: setPaint( java.awt.Color.yellow );
//         break;
//       }
//     }

//     public void setForeground ( int color ) {
//       switch ( color ) {
//       case 1: setPaint( java.awt.Color.yellow );
//         break;
//       }
//     }
//   }

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

  public int getIndex () {
    return rootGraphIndex;
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
  public String getLabelText () {
    return ( String )view.getNodeObjectProperty( rootGraphIndex,
                                                 PGraphView.NODE_LABEL);
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

} //class RadarNode
