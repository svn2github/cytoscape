//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.graphutil;

import java.util.*;
import phoebe.*;
import giny.view.*;
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.util.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import javax.swing.*;
import java.awt.event.*;

public class EdgeAction {

  public EdgeAction () {
  }

  public static JMenuItem edgeLineType  (Object[] args, PNode node ) {
    final PEdgeView ev = ( PEdgeView )node;
    
    JMenu type_menu = new JMenu( "Edge Curve Type" );
    type_menu.add( new JMenuItem( new AbstractAction( "Bezier" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setLineType( PEdgeView.CURVED_LINES );
                } } ); } } ) );
    type_menu.add( new JMenuItem( new AbstractAction( "Polyline" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setLineType( PEdgeView.STRAIGHT_LINES );
                } } ); } } ) );
    return type_menu;
  }


  public static JMenuItem edgeSourceEndType (Object[] args, PNode node ) {
    final PEdgeView ev = ( PEdgeView )node;

    JMenu type_menu = new JMenu( "Source Edge End Type" );
    
     type_menu.add( new JMenuItem( new AbstractAction( "None" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.NO_END );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_DELTA );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_DELTA );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_DELTA );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_ARROW );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_ARROW );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_ARROW );
                } } ); } } ) );


      type_menu.add( new JMenuItem( new AbstractAction( "WHITE_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_DIAMOND );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_DIAMOND );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_DIAMOND );
                } } ); } } ) );

     

     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_CIRCLE );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_CIRCLE );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_CIRCLE );
                } } ); } } ) );

  
     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_T );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_T );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_T );
                } } ); } } ) );

    return type_menu;
  }

  public static JMenuItem edgeTargetEndType (Object[] args, PNode node ){
    final PEdgeView ev = ( PEdgeView )node;

    JMenu type_menu = new JMenu( "Target Edge End Type" );
    
     type_menu.add( new JMenuItem( new AbstractAction( "None" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.NO_END );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_DELTA );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_DELTA );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_DELTA );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_ARROW );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_ARROW );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_ARROW );
                } } ); } } ) );


      type_menu.add( new JMenuItem( new AbstractAction( "WHITE_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_DIAMOND );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_DIAMOND );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_DIAMOND );
                } } ); } } ) );

     

     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_CIRCLE );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_CIRCLE );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_CIRCLE );
                } } ); } } ) );

  
     type_menu.add( new JMenuItem( new AbstractAction( "WHITE_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_T );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "BLACK_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_T );
                } } ); } } ) );

     type_menu.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_T );
                } } ); } } ) );

    return type_menu;
  } 



  public static JMenuItem edgeEndBorderColor (Object[] args, PNode node ) {
    final PGraphView v = ( PGraphView )args[0];
    final PPath icon = ( PPath )node;

    return new JMenuItem( new AbstractAction( "Choose Border Color" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 icon.setStrokePaint( color.showDialog( v.getComponent() , "Choose a Border Color", (java.awt.Color)icon.getPaint() ) );
               } } ); } } );
  
  }



  public static JMenuItem edgeEndColor (Object[] args, PNode node ) {
    final PPath icon = ( PPath )node;
    final PGraphView v = ( PGraphView )args[0];

    return new JMenuItem( new AbstractAction( "Custom" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 icon.setPaint( color.showDialog( v.getComponent() , "Choose a Border Color", (java.awt.Color)icon.getPaint() ) );
               } } ); } } );
  }

  

  public static JMenuItem edgeWidth (Object[] args, PNode node ) {

    final PEdgeView ev = ( PEdgeView )node;
    final PGraphView v = ( PGraphView )args[0];
    
    JMenu width_menu = new JMenu( "Choose Edge Width" );
    width_menu.add( new JMenuItem( new AbstractAction( "1/2" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( .5f );
                } } ); } } ) );
   width_menu.add( new JMenuItem( new AbstractAction( "1" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 1f );
                } } ); } } ) );
    width_menu.add( new JMenuItem( new AbstractAction( "2" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 2f );
                } } ); } } ) );
    width_menu.add( new JMenuItem( new AbstractAction( "3" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 3f );
                } } ); } } ) );
    width_menu.add( new JMenuItem( new AbstractAction( "4" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 4f );
                } } ); } } ) );
    width_menu.add( new JMenuItem( new AbstractAction( "8" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 8f );
                } } ); } } ) );
     width_menu.add( new JMenuItem( new AbstractAction( "16" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 16f );
                } } ); } } ) );
     width_menu.add( new JMenuItem( new AbstractAction( "32" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 32f );
                } } ); } } ) );
      width_menu.add( new JMenuItem( new AbstractAction( "64" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setStrokeWidth( 64f );
                } } ); } } ) );

    return width_menu;
  }


  
   public static JMenuItem colorEdge (Object[] args, PNode node ) {

    final PEdgeView ev = ( PEdgeView )node;
    final PGraphView v = ( PGraphView )args[0];

    JMenu color_menu = new JMenu( "Choose node Color" );
    color_menu.add( new JMenuItem( new AbstractAction( "Black" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setUnselectedPaint( java.awt.Color.black );
                } } ); } } ) );


    color_menu.add( new JMenuItem( new AbstractAction( "White" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setUnselectedPaint( java.awt.Color.white );
                } } ); } } ) );

  
    color_menu.add( new JMenuItem( new AbstractAction( "Red" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setUnselectedPaint( java.awt.Color.red );
                } } ); } } ) );

    color_menu.add( new JMenuItem( new AbstractAction( "Orange" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setUnselectedPaint( java.awt.Color.orange );
                } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Green" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setUnselectedPaint( java.awt.Color.green );
                } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Blue" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 ev.setUnselectedPaint( java.awt.Color.blue );
               } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Magenta" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 ev.setUnselectedPaint( java.awt.Color.magenta );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Cyan" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 ev.setUnselectedPaint( java.awt.Color.cyan );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Custom" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 ev.setUnselectedPaint( color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)ev.getUnselectedPaint() ) );
               } } ); } } ) );

     return color_menu;
   }

   public static JMenuItem colorSelectEdge (Object[] args, PNode node ) {

    final PEdgeView ev = ( PEdgeView )node;
   
    final PGraphView v = ( PGraphView )args[0];
    JMenu color_menu = new JMenu( "Choose Selected Color" );
    color_menu.add( new JMenuItem( new AbstractAction( "Black" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSelectedPaint( java.awt.Color.black );
                } } ); } } ) );


    color_menu.add( new JMenuItem( new AbstractAction( "White" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSelectedPaint( java.awt.Color.white );
                } } ); } } ) );

  
    color_menu.add( new JMenuItem( new AbstractAction( "Red" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSelectedPaint( java.awt.Color.red );
                } } ); } } ) );

    color_menu.add( new JMenuItem( new AbstractAction( "Orange" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSelectedPaint( java.awt.Color.orange );
                } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Green" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSelectedPaint( java.awt.Color.green );
                } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Blue" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 ev.setSelectedPaint( java.awt.Color.blue );
               } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Magenta" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 ev.setSelectedPaint( java.awt.Color.magenta );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Cyan" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 ev.setSelectedPaint( java.awt.Color.cyan );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Custom" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 ev.setSelectedPaint( color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)ev.getSelectedPaint() ) );
               } } ); } } ) );

     return color_menu;
   }



}
