//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.graphutil;

import java.util.*;
import phoebe.*;

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

  public static JMenuItem edgeLineType  ( PGraphView view, PNode node ) {
    final PEdgeView ev = ( PEdgeView )node;
    
    JMenu type_menu = new JMenu( "Edge Line Type" );
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


  public static JMenuItem edgeSourceEndType ( PGraphView view, PNode node ) {
    final PEdgeView ev = ( PEdgeView )node;

    JMenu type_menu = new JMenu( "Source End Type" );
    type_menu.add( new JMenuItem( new AbstractAction( "Arrow" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.ARROW_END );
                } } ); } } ) );
  
    type_menu.add( new JMenuItem( new AbstractAction( "Circle" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.CIRCLE_END );
                } } ); } } ) );
    
    type_menu.add( new JMenuItem( new AbstractAction( "Diamond" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.DIAMOND_END );
                } } ); } } ) );
    type_menu.add( new JMenuItem( new AbstractAction( "T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.T_END );
                } } ); } } ) );
    return type_menu;
  }

  public static JMenuItem edgeTargetEndType ( PGraphView view, PNode node ) {
    final PEdgeView ev = ( PEdgeView )node;

    JMenu type_menu = new JMenu( "Target End Type" );
    type_menu.add( new JMenuItem( new AbstractAction( "Arrow" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.ARROW_END );
                } } ); } } ) );
  
    type_menu.add( new JMenuItem( new AbstractAction( "Circle" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.CIRCLE_END );
                } } ); } } ) );
    
    type_menu.add( new JMenuItem( new AbstractAction( "Diamond" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.DIAMOND_END );
                } } ); } } ) );
    type_menu.add( new JMenuItem( new AbstractAction( "T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.T_END );
                } } ); } } ) );
    return type_menu;
  }



  public static JMenuItem edgeEndBorderColor ( PGraphView view, PNode node ) {
    final PGraphView v = view;
    final PPath icon = ( PPath )node;

    JMenu color_menu = new JMenu( "Choose Edge Color" );
    color_menu.add( new JMenuItem( new AbstractAction( "Black" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setStrokePaint( java.awt.Color.black );
                } } ); } } ) );


    color_menu.add( new JMenuItem( new AbstractAction( "White" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setStrokePaint( java.awt.Color.white );
                } } ); } } ) );

  
    color_menu.add( new JMenuItem( new AbstractAction( "Red" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setStrokePaint( java.awt.Color.red );
                } } ); } } ) );

    color_menu.add( new JMenuItem( new AbstractAction( "Orange" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setStrokePaint( java.awt.Color.orange );
                } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Green" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setStrokePaint( java.awt.Color.green );
                } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Blue" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 icon.setStrokePaint( java.awt.Color.blue );
               } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Magenta" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 icon.setStrokePaint( java.awt.Color.magenta );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Cyan" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 icon.setStrokePaint( java.awt.Color.cyan );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Custom" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 icon.setStrokePaint( color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)icon.getPaint() ) );
               } } ); } } ) );

     return color_menu;
  }



  public static JMenuItem edgeEndColor ( PGraphView view, PNode node ) {
    final PPath icon = ( PPath )node;
    final PGraphView v = view;
    
    JMenu color_menu = new JMenu( "Choose edge end Color" );
    color_menu.add( new JMenuItem( new AbstractAction( "Black" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setPaint( java.awt.Color.black );
                } } ); } } ) );


    color_menu.add( new JMenuItem( new AbstractAction( "White" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setPaint( java.awt.Color.white );
                } } ); } } ) );

  
    color_menu.add( new JMenuItem( new AbstractAction( "Red" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setPaint( java.awt.Color.red );
                } } ); } } ) );

    color_menu.add( new JMenuItem( new AbstractAction( "Orange" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setPaint( java.awt.Color.orange );
                } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Green" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  icon.setPaint( java.awt.Color.green );
                } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Blue" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 icon.setPaint( java.awt.Color.blue );
               } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Magenta" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 icon.setPaint( java.awt.Color.magenta );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Cyan" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 icon.setPaint( java.awt.Color.cyan );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Custom" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 icon.setPaint( color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)icon.getPaint() ) );
               } } ); } } ) );

     return color_menu;
  }

  

  public static JMenuItem edgeWidth ( PGraphView view, PNode node ) {

    final PEdgeView ev = ( PEdgeView )node;
    final PGraphView v = view;

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


  
   public static JMenuItem colorEdge ( PGraphView view, PNode node ) {

    final PEdgeView ev = ( PEdgeView )node;
    final PGraphView v = view;

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

   public static JMenuItem colorSelectEdge ( PGraphView view, PNode node ) {

    final PEdgeView ev = ( PEdgeView )node;
    final PGraphView v = view;

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
