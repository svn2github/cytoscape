package integration.view;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.activities.*;
import edu.umd.cs.piccolox.event.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolox.swing.*;
import edu.umd.cs.piccolox.util.*;

import integration.util.*;
import integration.data.*;

import javax.swing.*;
import java.awt.*;

import java.lang.reflect.Method;

import cern.colt.matrix.*;
import java.util.*;

/**
 * IntegrationView will show elements of a DataCube/Slice/Vector/Element
 * and will alow you to get information about a data element, select elements,
 * filter elements, and integrate the selection with the Main Cytoscape window.
 */  
public class IntegrationView {

  //protected HashMap contextMenuStore;
  //protected PContextMenu contextMenu;
  //protected PToolTipHandler toolTipHandler;

  // The Piccolo PCanvas that we will draw on
  private PCanvas canvas;

   // A JPanel for the Canvas in the middle, and some other stuff around the sides
  public JPanel viewComponent;

  protected Color DEFAULT_BACKGROUND_COLOR = new java.awt.Color(60, 60, 60);

  VerticalLayoutNode vln1, vln2;
  boolean shown_1 = true;

  
  DataCube dataCube;
  Integration integration;

  ObjectMatrix3D matrix3d;

  public IntegrationView ( Integration integration ) {


    this.integration = integration;
    this.dataCube = integration.getDataCube();

    // Create the JPanel that we will put ourselves on.
    // initialize the PCanvas and Enclosing Scroll Pane
    // and add the whole shebang to the JPanel
    viewComponent = new JPanel();
    viewComponent.setLayout(new BorderLayout());
    canvas = new PCanvas();
    PScrollPane scroll = new PScrollPane(canvas);
    viewComponent.add(scroll, BorderLayout.CENTER);
    canvas.getCamera().setPaint( DEFAULT_BACKGROUND_COLOR );

    canvas.setSize( 1000, 1000 );

    //   PStyledTextEventHandler textHandler = new PStyledTextEventHandler( canvas ) {
    //         public  void 	keyPressed(PInputEvent event) {
    //           System.out.println( "Key Code: "+event.getKeyCode() );
    //           System.out.println( "Key Char: "+event.getKeyChar() );
    //         }
    
    //         public void mousePressed(PInputEvent event) {
    
    //           //System.out.println( "Mouse Pressed" );
    //           //System.out.println( "Key Code: "+event.getKeyCode() );
    //           //System.out.println( "Key Char: "+event.getKeyChar() );
    //           //PNode pickedNode = inputEvent.getPickedNode();
    //           //stopEditing();
    //           //if (pickedNode instanceof PStyledText) {
    //           //  startEditing(inputEvent,(PStyledText)pickedNode);
    //           }
    //       };
    
    
    // 		canvas.addInputEventListener( textHandler );


    //init();
    initialize();
    viewComponent.setSize( 1000, 1000 );
  }

  public JComponent getComponent () {
    return viewComponent;
  }
                 
  protected void initialize () {

    
    matrix3d = dataCube.getMatrix().like();
    Object[] slices = dataCube.getSliceNames().toArray();
    Object[] rows = dataCube.getRowNames().toArray();
    Object[] columns = dataCube.getColumnNames().toArray();

    String slice, row, column;
    int s, r, c;



//     while ( slices.hasNext() ) {
//       slice = ( String )slices.next(); 
//       s = dataCube.getSliceIndex( slice );
//       while ( rows.hasNext() ) {
//         row = ( String )rows.next();
//         r = dataCube.getRowIndex( row );
//         while ( columns.hasNext() ) {
//           column = ( String )columns.next();

    for ( int sl = 0; sl < slices.length; ++sl ) {
      slice = ( String )slices[ sl ];
      s = dataCube.getSliceIndex( slice );
      //System.out.println( "slice: "+slice+" : "+s );
      for ( int ro = 0; ro < rows.length; ++ro ) {
        row = ( String )rows[ ro ];
        r = dataCube.getRowIndex( row );
        //System.out.println( "row: "+row+" : "+r );
        for ( int co = 0; co < columns.length; ++co ) {
          column = ( String )columns[ co ];
          c = dataCube.getColumnIndex( column );
          double value = ( ( Double )dataCube.getObject( slice, row, column ) ).doubleValue();
          DataElementView view = new DataElementView( value, slice+"\n"+row+"\n"+column+"\n : "+value );
          matrix3d.set( s, r, c, view );
          //System.out.println( "Created: "+s+" "+r+" "+c+" :: " +view );
          
        }
      }
    } 

    PNotificationCenter.defaultCenter().addListener( this, "updateListener", "INTEGRATION_VIEW_UPDATE", null );

  }


  public void updateListener ( PNotification note ) {

    Object[] stuff = ( Object[] )note.getObject();
    updateDisplay( ( String[] )stuff[0], ( String[] )stuff[1], ( String )stuff[2] );


  }


  public void updateDisplay ( String[] slices, String[] rows, String column ) {
    // clear the canvas
    canvas.getLayer().removeAllChildren();

    //System.out.println( "Slices: "+slices.length+" Rows: "+rows.length+" Columns: "+column );


    VerticalLayoutNode vert = new VerticalLayoutNode();
    int cint = dataCube.getColumnIndex( column );

    HorizontalLayoutNode label = new HorizontalLayoutNode();
    label.addChild( new DataElementView(-5, column ) );
    for ( int r = 0; r < rows.length; ++r ) {
      label.addChild( new DataElementView( -5, rows[r] ) );
    }
    vert.addChild( label );

    for ( int s = 0; s < slices.length; ++s ) {
      HorizontalLayoutNode hor = new HorizontalLayoutNode();
      hor.addChild( new DataElementView( -5, slices[s] ) );
      int sint = dataCube.getSliceIndex( slices[s] );
      for ( int r = 0; r < rows.length; ++r ) {
        int rint = dataCube.getRowIndex( rows[r] );
        //System.out.println( "Adding: "+sint+" "+rint+" "+cint );
        DataElementView dev = ( DataElementView )matrix3d.get( sint, rint, cint );
        //System.out.println( "Adding: "+dev );
        hor.addChild( ( DataElementView )matrix3d.get( sint, rint, cint ) );
      }
      vert.addChild( hor );
    }
    canvas.getLayer().addChild( vert );
  }

                   

  /**
   * For now this is just a sample implementation
   */
  protected void init () {

    vln1 = new VerticalLayoutNode();
    for ( int i = 0; i < 10; ++i ) {
      HorizontalLayoutNode hln = new HorizontalLayoutNode();
      for ( int j = 0; j < 10; ++j ) {
        DataElementView each = new DataElementView( j * i );
        hln.addChild( each );
      }
      vln1.addChild( hln );
    }
    canvas.getLayer().addChild( vln1 );


    vln2 = new VerticalLayoutNode();
    for ( int i = 0; i < 10; ++i ) {
      HorizontalLayoutNode hln = new HorizontalLayoutNode();
      for ( int j = 0; j < 10; ++j ) {
        PNode each = PPath.createRectangle( 0, 0, 100, 100 );
        if ( j < 3 && i < 3 ) {
          each.setPaint( Color.orange );
        } else if ( j < 3 && i > 3 ) {
          each.setPaint( Color.pink );
        } else {
          each.setPaint( Color.white );
        }
        hln.addChild( each );
      }
      vln2.addChild( hln );
    }
    // canvas.getLayer().addChild( vln2 );
    try {
      ButtonNode b = new ButtonNode( "Switch", getClass().getDeclaredMethod( "testButton", new Class[] {} ) , this  , new Class[] {} );
      canvas.getLayer().getCamera(0).addChild( b );
    } catch ( Exception e ) {
      System.out.println( "Button not added" );
      e.printStackTrace();
    }

  }
  
  public void testButton () {

    if ( shown_1 ) {
      canvas.getLayer().removeChild( vln1 );
      canvas.getLayer().addChild( vln2 );
      shown_1 = false;
    } else {
      canvas.getLayer().removeChild( vln2 );
      canvas.getLayer().addChild( vln1 );
      shown_1 = true;
    }


  }


  public static void main ( String[] args ) {
    
    IntegrationView iv = new IntegrationView( null );
    JFrame f = new JFrame( "Test" );
    f.getContentPane().add( iv.viewComponent );
    f.pack();
    f.setVisible( true );
  }


}
