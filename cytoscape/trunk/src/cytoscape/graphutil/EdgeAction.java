//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.graphutil;

import cytoscape.view.NetworkView;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;
import cytoscape.browsers.*;
import cytoscape.util.*;
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

  public static String getTitle ( Object[] args, PNode node ) {
    //System.out.println( "Getting Title" );
    final NetworkView nv = ( NetworkView )args[0];
    //return nv.getNetwork().getNodeAttributes().getCanonicalName( node );
   
    if ( node instanceof PEdgeView ) {
      return  nv.getNetwork().
        getEdgeAttributes().
        getCanonicalName(  nv.getNetwork().getGraphPerspective().
                           getEdge( ( (PEdgeView)node).getGraphPerspectiveIndex() ) );
    }
    //      return nv.getNetwork().getGraphPerspective().
    //    getEdge( ( (PEdgeView)node).getGraphPerspectiveIndex() ).
    //    getIdentifier();
    
    return "";
  }

  public static JMenuItem viewEdgeAttributeBrowser ( Object[] args, PNode node ) {
    final NetworkView network = ( NetworkView )args[0];
    final PEdgeView view = ( PEdgeView )node;
    return new JMenuItem( new AbstractAction( "Attribute Browser" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {

                  List edges = network.getView().getSelectedEdges();
                  Object[] objects;
                  if ( !edges.isEmpty() ) {
                    if ( !view.isSelected() ) {
                      edges.add( view );
                    }
                    objects = new Object[ edges.size() ];
                    for ( int i = 0; i < edges.size(); ++i ) {
                      objects[i] = ( Object )( ( EdgeView )edges.get(i) ).getEdge();
                    }

                  } else {
                    objects =  new Object[] { view.getEdge() };
                  }
                  TabbedBrowser nodeBrowser = new TabbedBrowser (objects, 
                                                    network.getNetwork().getEdgeAttributes(),
                                                    new Vector(),
                                                    network.getCytoscapeObj().
                                                    getConfiguration().getProperties().
                                                    getProperty("webBrowserScript", 
                                                                 "noScriptDefined") ,
                                                    TabbedBrowser.BROWSING_NODES );
     } } ); } } );
  }


  public static JMenuItem editEdge ( Object[] args, PNode node ) {
    final NetworkView network = ( NetworkView )args[0];
    final GraphView v = network.getView();
    final PEdgeView ev = ( PEdgeView )node;

    JMenu edit_menu = new JMenu( "Edit Edge" );

    JMenu line_type_menu = new JMenu( "Curve Type" );
    line_type_menu.add( new JMenuItem( new AbstractAction( "Bezier" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setLineType( PEdgeView.CURVED_LINES );
                } } ); } } ) );
    line_type_menu.add( new JMenuItem( new AbstractAction( "Polyline" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setLineType( PEdgeView.STRAIGHT_LINES );
                } } ); } } ) );
    edit_menu.add( line_type_menu );

    
    JMenu source_end = new JMenu( "Source Edge End" );
 
    
     source_end.add( new JMenuItem( new AbstractAction( "None" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.NO_END );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "WHITE_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_DELTA );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "BLACK_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_DELTA );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_DELTA );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "WHITE_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_ARROW );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "BLACK_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_ARROW );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_ARROW );
                } } ); } } ) );


      source_end.add( new JMenuItem( new AbstractAction( "WHITE_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_DIAMOND );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "BLACK_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_DIAMOND );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_DIAMOND );
                } } ); } } ) );

     

     source_end.add( new JMenuItem( new AbstractAction( "WHITE_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_CIRCLE );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "BLACK_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_CIRCLE );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_CIRCLE );
                } } ); } } ) );

  
     source_end.add( new JMenuItem( new AbstractAction( "WHITE_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.WHITE_T );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "BLACK_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.BLACK_T );
                } } ); } } ) );

     source_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setSourceEdgeEnd( PEdgeView.EDGE_COLOR_T );
                } } ); } } ) );
     edit_menu.add( source_end );
  
     JMenu target_end = new JMenu( "Target Edge End" );
    
     target_end.add( new JMenuItem( new AbstractAction( "None" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.NO_END );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "WHITE_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_DELTA );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "BLACK_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_DELTA );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DELTA" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_DELTA );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "WHITE_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_ARROW );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "BLACK_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_ARROW );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_ARROW" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_ARROW );
                } } ); } } ) );


      target_end.add( new JMenuItem( new AbstractAction( "WHITE_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_DIAMOND );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "BLACK_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_DIAMOND );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_DIAMOND" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_DIAMOND );
                } } ); } } ) );

     

     target_end.add( new JMenuItem( new AbstractAction( "WHITE_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_CIRCLE );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "BLACK_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_CIRCLE );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_CIRCLE" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_CIRCLE );
                } } ); } } ) );

  
     target_end.add( new JMenuItem( new AbstractAction( "WHITE_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.WHITE_T );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "BLACK_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.BLACK_T );
                } } ); } } ) );

     target_end.add( new JMenuItem( new AbstractAction( "EDGE_COLOR_T" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  ev.setTargetEdgeEnd( PEdgeView.EDGE_COLOR_T );
                } } ); } } ) );
  
     edit_menu.add( target_end );
      
     JMenu width_menu = new JMenu( "Width (pts)" );
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
                //    float[] dash = {3.0f, 1.0f};
//                   ev.setStroke ( new java.awt.BasicStroke( 4f, java.awt.BasicStroke.CAP_SQUARE,
//                                                            java.awt.BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f) );
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

      edit_menu.add( width_menu );
   

     edit_menu.add( new JMenuItem( new AbstractAction( "<html>Color <small><i>(short-term)</i></small></html>" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 ev.setUnselectedPaint( color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)ev.getUnselectedPaint() ) );
               } } ); } } ) );

     return edit_menu;

  }

  /**
   * This will open an web page that will give you more info.
   */
  public static JMenuItem openWebInfo ( Object[] args, PNode node ) {

    final PNode nv = node;

    JMenu web_menu = new JMenu( "Web Info" );

    web_menu.add(  new JMenuItem( new AbstractAction( "<html>SGD <small><i>yeast only</i></small></html>" ) {
        public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL( "http://db.yeastgenome.org/cgi-bin/SGD/locus.pl?locus="+gene );
               
                } } ); } } ) );

    web_menu.add(  new JMenuItem( new AbstractAction( "Google" ) {
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL( "http://www.google.com/search?q="+gene);
               
                } } ); } } ) );

   
    JMenu gn_menu = new JMenu( "GenomeNet" );
    web_menu.add( gn_menu );
    gn_menu.add(  new JMenuItem( new AbstractAction( "Pathway" ) {
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=pathway&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "KO" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=ko&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "Genes" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=genes&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "Genome" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=genome&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "Ligand" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=ligand&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "Compound" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=compound&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "Glycan" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                      //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=glycan&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "Reaction" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=reaction&keywords="+gene );
               
                } } ); } } ));

    gn_menu.add(  new JMenuItem( new AbstractAction( "Enzyme" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=enzyme&keywords="+gene );
               
                } } ); } }) );

           gn_menu.addSeparator();
           
           gn_menu.add(  new JMenuItem( new AbstractAction( "Swiss-Prot" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=swissprot&keywords="+gene );
               
                } } ); } }) );

         

           gn_menu.add(  new JMenuItem( new AbstractAction( "Ref-Seq" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=refseq&keywords="+gene );
               
                } } ); } } ));

           gn_menu.add(  new JMenuItem( new AbstractAction( "GenBank" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=genbank&keywords="+gene );
               
                } } ); } } ));

           gn_menu.add(  new JMenuItem( new AbstractAction( "Embl" ){
            public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PEdgeView ) {
                    gene = ( ( PEdgeView ) nv).getLabel().getText();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL("http://www.genome.ad.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&dbkey=embl&keywords="+gene );
               
                } } ); } } ));

           return web_menu;

   }

  //----------------------------------------//
  // Edge Ends
  //----------------------------------------//

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

    return new JMenuItem( new AbstractAction( "Color" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 icon.setPaint( color.showDialog( v.getComponent() , "Border Color", (java.awt.Color)icon.getPaint() ) );
               } } ); } } );
  }



}
