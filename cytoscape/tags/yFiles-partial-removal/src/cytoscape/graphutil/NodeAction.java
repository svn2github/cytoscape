//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------


package cytoscape.graphutil;

import java.util.*;
import giny.model.*;
import giny.view.*;
import phoebe.*;
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.util.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import javax.swing.*;
import java.awt.event.*;

public class NodeAction {
	
  public NodeAction () {
  }


  public static JMenuItem openSGD ( PGraphView view, PNode node ) {

    final PNode nv = node;
    return new JMenuItem( new AbstractAction( "SGD" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  String gene = null;
                  if ( nv instanceof PNodeView ) {
                     gene = ( ( PNodeView ) nv).getLabel();
                  } 
                  //System.out.println( "Node: "+nv.getLabel() );
                  //System.out.println( "GEne: "+gene );
                  if ( gene == null ) {
                    gene = ( String )nv.getClientProperty("tooltip");
                    //System.out.println( "Gene: "+gene );
                  }
                  OpenBrowser.openURL( "http://db.yeastgenome.org/cgi-bin/SGD/locus.pl?locus="+gene );
               
                } } ); } } );
   }

 
   public static JMenuItem colorNodeWhite ( PGraphView view, PNode node ) {

    final PNodeView nv = ( PNodeView )node;
    return new JMenuItem( new AbstractAction( "Color Node White" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                   
                  nv.setUnselectedPaint( java.awt.Color.white );
                  nv.setBorderPaint( java.awt.Color.black );
               
                } } ); } } );
   }

  public static JMenuItem colorNodeBlack ( PGraphView view, PNode node ) {

    final PNodeView nv = ( PNodeView )node;
    return new JMenuItem( new AbstractAction( "Color Node Black" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  
                  nv.setUnselectedPaint( java.awt.Color.black );
                  nv.setBorderPaint( java.awt.Color.white );
               
           } } ); } } );
   }


   public static JMenuItem colorNode ( PGraphView view, PNode node ) {

    final PNodeView nv = ( PNodeView )node;
    final PGraphView v = view;

    JMenu color_menu = new JMenu( "Choose node Color" );
    color_menu.add( new JMenuItem( new AbstractAction( "Black" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setUnselectedPaint( java.awt.Color.black );
                } } ); } } ) );


    color_menu.add( new JMenuItem( new AbstractAction( "White" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setUnselectedPaint( java.awt.Color.white );
                } } ); } } ) );

  
    color_menu.add( new JMenuItem( new AbstractAction( "Red" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setUnselectedPaint( java.awt.Color.red );
                } } ); } } ) );

    color_menu.add( new JMenuItem( new AbstractAction( "Orange" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setUnselectedPaint( java.awt.Color.orange );
                } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Green" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setUnselectedPaint( java.awt.Color.green );
                } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Blue" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setUnselectedPaint( java.awt.Color.blue );
               } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Magenta" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setUnselectedPaint( java.awt.Color.magenta );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Cyan" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setUnselectedPaint( java.awt.Color.cyan );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Custom" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 nv.setUnselectedPaint( color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)nv.getUnselectedPaint() ) );
               } } ); } } ) );

     return color_menu;
   }

   public static JMenuItem colorSelectNode ( PGraphView view, PNode node ) {

    final PNodeView nv = ( PNodeView )node;
    final PGraphView v = view;

    JMenu color_menu = new JMenu( "Choose Selected Color" );
    color_menu.add( new JMenuItem( new AbstractAction( "Black" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setSelectedPaint( java.awt.Color.black );
                } } ); } } ) );


    color_menu.add( new JMenuItem( new AbstractAction( "White" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setSelectedPaint( java.awt.Color.white );
                } } ); } } ) );

  
    color_menu.add( new JMenuItem( new AbstractAction( "Red" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setSelectedPaint( java.awt.Color.red );
                } } ); } } ) );

    color_menu.add( new JMenuItem( new AbstractAction( "Orange" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setSelectedPaint( java.awt.Color.orange );
                } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Green" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  nv.setSelectedPaint( java.awt.Color.green );
                } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Blue" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setSelectedPaint( java.awt.Color.blue );
               } } ); } } ) );
     
     color_menu.add( new JMenuItem( new AbstractAction( "Magenta" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setSelectedPaint( java.awt.Color.magenta );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Cyan" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setSelectedPaint( java.awt.Color.cyan );
               } } ); } } ) );

     color_menu.add( new JMenuItem( new AbstractAction( "Custom" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
                 nv.setSelectedPaint( color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)nv.getSelectedPaint() ) );
               } } ); } } ) );

     return color_menu;
   }

  public static JMenuItem shapeNode ( PGraphView view , PNode node ) {

    final PNodeView nv = ( PNodeView )node;
    final PGraphView v = view;
    final GraphPerspective graphPerspective = view.getGraphPerspective();

    JMenu shape_menu = new JMenu( "Choose Node Shape" );
    shape_menu.add( new JMenuItem( new AbstractAction( "Diamond" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setShape( PNodeView.DIAMOND );
               } } ); } } ) );

    shape_menu.add( new JMenuItem( new AbstractAction( "Ellipse" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setShape( PNodeView.ELLIPSE );
               } } ); } } ) );
    shape_menu.add( new JMenuItem( new AbstractAction( "Hexagon" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setShape( PNodeView.HEXAGON );
               } } ); } } ) );
    shape_menu.add( new JMenuItem( new AbstractAction( "Octagon" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setShape( PNodeView.OCTAGON );
               } } ); } } ) );
    shape_menu.add( new JMenuItem( new AbstractAction( "Triangle" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setShape( PNodeView.TRIANGLE );
               } } ); } } ) );
    shape_menu.add( new JMenuItem( new AbstractAction( "Parallelogram" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setShape( PNodeView.PARALELLOGRAM );
               } } ); } } ) );
    shape_menu.add( new JMenuItem( new AbstractAction( "Rectangle" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 nv.setShape( PNodeView.RECTANGLE );
               } } ); } } ) );
	       shape_menu.add( new JMenuItem( new AbstractAction( "Gene Simbol" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 //set to nv to the Errow Node
		 //int n = nv.getIndex();
		 //graphPerspective.restoreNode(n);
                  PNode node = (PNode)v.addNodeView( "cytoscape.graphutil.ArrowNode",nv.getIndex());
               } } ); } } ) );
    return shape_menu;
  }
  public static JMenuItem showData ( PGraphView view, PNode node ) {

    final PNodeView nv = ( PNodeView )node;
    final PGraphView v = view;
    final GraphPerspective graphPerspective = view.getGraphPerspective();

    JMenu datashow_menu = new JMenu( "Show the Node" );
    datashow_menu.add( new JMenuItem( new AbstractAction( "As a Star Node" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
		
                 PNode node = (PNode)v.addNodeView( "cytoscape.graphutil.StarNode",nv.getIndex());
                  
                } } ); } } ) );


    datashow_menu.add( new JMenuItem( new AbstractAction( "As a Grid Node" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  
                  PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.GridNode",nv.getIndex());
                } } ); } } ) );

  
    datashow_menu.add( new JMenuItem( new AbstractAction( "As a Petal Node" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                 
                  PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.PetalNode",nv.getIndex());
                } } ); } } ) );

    datashow_menu.add( new JMenuItem( new AbstractAction( "As a Radar Node" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  
                  PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.RadarNode",nv.getIndex());
                } } ); } } ) );

     datashow_menu.add( new JMenuItem( new AbstractAction( "As a Clip Radar Node" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  //set nv to Clip Radar Node
		  
                  PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.ClipRadarNode",nv.getIndex());
                } } ); } } ) );
     
     
     return datashow_menu;
   }//end of datashow menu
   
   public static JMenuItem changeFirstNeighbors (PGraphView view, PNode node)
   {
	  //final PNode thenode = node;
	  final PNodeView nv = ( PNodeView )node;
	  final PGraphView v = view;
	  final GraphPerspective graphPerspective = view.getGraphPerspective();

    JMenu firstn_menu = new JMenu( "Change Fist Neighbors" );
	 
	 firstn_menu.add( new JMenuItem( new AbstractAction( "Color" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JColorChooser color = new JColorChooser();
		 java.awt.Color custom = color.showDialog( v.getComponent() , "Choose a Node Color", (java.awt.Color)nv.getUnselectedPaint() );
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			NodeView neibview = (NodeView)v.getNodeView(neib.getRootGraphIndex());
			neibview.setUnselectedPaint( custom);
		}
	 } } ); } } ) );
	 
	 
	/* firstn_menu.add( new JMenuItem( new AbstractAction( "To star plots" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			int index = neib.getRootGraphIndex();
			NodeView neibview = (NodeView)v.getNodeView(index);
			
			PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.ClipRadarNode", index);
		}
                  
               } } ); } } ) );
	       firstn_menu.add( new JMenuItem( new AbstractAction( "To radars nodes" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			int index = neib.getRootGraphIndex();
			NodeView neibview = (NodeView)v.getNodeView(index);
			
			PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.RadarNode", index);
		}
                  
               } } ); } } ) );
	       
	       firstn_menu.add( new JMenuItem( new AbstractAction( "To grid nodes" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			int index = neib.getRootGraphIndex();
			NodeView neibview = (NodeView)v.getNodeView(index);
			
			PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.GridNode", index);
		}
                  
               } } ); } } ) );
	       
	 firstn_menu.add( new JMenuItem( new AbstractAction( "To petal nodes" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			int index = neib.getRootGraphIndex();
			NodeView neibview = (NodeView)v.getNodeView(index);
			
			PNode node = (PNode) v.addNodeView( "cytoscape.graphutil.PetalNode", index);
		}
                  
               } } ); } } ) );
	       
	       **/
	 firstn_menu.add( new JMenuItem( new AbstractAction( "To parallelogram" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			NodeView neibview = (NodeView)v.getNodeView(neib.getRootGraphIndex());
			neibview.setShape( NodeView.PARALELLOGRAM );
		}
                  
               } } ); } } ) );
	 
	 firstn_menu.add( new JMenuItem( new AbstractAction( "To circles" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			NodeView neibview = (NodeView)v.getNodeView(neib.getRootGraphIndex());
			neibview.setShape( PNodeView.ELLIPSE );
		}
                  
               } } ); } } ) ); 
	       
	 firstn_menu.add( new JMenuItem( new AbstractAction( "To triangles" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			NodeView neibview = (NodeView)v.getNodeView(neib.getRootGraphIndex());
			neibview.setShape( PNodeView.TRIANGLE );
		}
                  
               } } ); } } ) );
	       
	  firstn_menu.add( new JMenuItem( new AbstractAction( "To diamonds" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		 Node n = graphPerspective.getNode(nv.getIndex());
		 
		 List list = graphPerspective.neighborsList(n);
		 Iterator i = list.iterator();
		 while (i.hasNext())
		{
			Node neib =(Node) i.next();
			NodeView neibview = (NodeView)v.getNodeView(neib.getRootGraphIndex());
			neibview.setShape( PNodeView.DIAMOND );
		}
                  
               } } ); } } ) );        

     return firstn_menu;
   }//end of changeFirstNeighbors
   
    public static JMenuItem zoomToNode ( PGraphView view, PNode node ) {
    final PNode n = ( PNode )node;
    final PGraphView v = view;
    
    return new  JMenuItem( new AbstractAction( "Zoom To" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		       PTransformActivity activity =  v.getCanvas().getCamera().animateViewToCenterBounds( n.getGlobalFullBounds(), true, 500 );
               } } ); } } );
  }
  public static JMenuItem zoomToNeighbors ( PGraphView view, PNode node ) {
    final PNode n = ( PNode )node;
    final PGraphView v = view;
    
    return new  JMenuItem( new AbstractAction( "Zoom To Neghbors" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
		       PTransformActivity activity =  v.getCanvas().getCamera().animateViewToCenterBounds( n.getUnionOfChildrenBounds(n.getFullBounds()), true, 500 );
                  
		       //PTransformActivity activity =  v.getCanvas().getCamera().animateViewToCenterBounds( n.getGlobalFullBounds(), true, 500 );
               } } ); } } );
  }
}
