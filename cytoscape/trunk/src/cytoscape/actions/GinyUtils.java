//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------

import giny.model.*;
import giny.view.*;
import java.util.*;

//-------------------------------------------------------------------------
public class GinyUtils {
    
    public static void hideSelectedNodes(GraphView view) {
	    
	    //hides nodes and edges between them
	    
	   if (view != null) {
			java.util.List list = view.getSelectedNodes();
			Iterator i = list.iterator();
			while ( i.hasNext() ) {
				NodeView nview =(NodeView) i.next();
        // use GINY methods
				view.hideGraphObject( nview );

				int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
				for ( int i2 = 0; i2 < na.length; ++i2 ) {
					int[] edges = view.
					getGraphPerspective().
					getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true, true );
					if( edges != null )
            //System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
					for ( int j = 0; j < edges.length; ++j ) {
						
            // use GINY methods
						view.hideGraphObject( view.getEdgeView( edges[j] ) );
					}
				}
			}//while
		}//if !null
       
    }
    
    
    public static void unHideSelectedNodes(GraphView view) {
	    
	    //hides nodes and edges between them
	    
	   if (view != null) {
			java.util.List list = view.getSelectedNodes();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				
				view.showGraphObject( nview );

				int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
				for ( int i2 = 0; i2 < na.length; ++i2 ) {
					int[] edges = view.
					getGraphPerspective().
					getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true, true );
					//if( edges != null )
					//System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
					for ( int j = 0; j < edges.length; ++j ) {
						
            
            				view.showGraphObject( view.getEdgeView( edges[j] ) );
					}
				}
		
			
			
			}//while
		}//if !null
       
    }
    
    
    public static void unHideAll(GraphView view) {
	    
	    Iterator i = view.getNodeViewsIterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				//Node n = nview.getNode();
				//( ( PNode )nview ).setVisible( true );
				view.showGraphObject( nview );
			}//while
	    java.util.List elist = view.getEdgeViewsList();
			Iterator ei = elist.iterator();
			while (ei.hasNext())
			{
				EdgeView eview =(EdgeView) ei.next();
				//Node n = nview.getNode();
				//( ( PNode )eview ).setVisible( true );
				view.showGraphObject( eview );
			}//while		
       
    }
    
    public static void unHideNodesAndInterconnectingEdges(GraphView view) {
	    Iterator i = view.getNodeViewsIterator();
            while (i.hasNext())
              {
                NodeView nview =(NodeView) i.next();
                Node n = nview.getNode();
          
                //( ( PNode )nview ).setVisible( true );
		view.showGraphObject( nview );
                int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
                for ( int i2 = 0; i2 < na.length; ++i2 ) {
                  int[] edges = view.getGraphPerspective().getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true );
                  if( edges != null )
                    for ( int j = 0; j < edges.length; ++j ) {
                      EdgeView ev = view.getEdgeView( edges[j] );
                      //if ( ev instanceof PNode ) {
                        //( ( PNode )ev ).setVisible( true );
			view.showGraphObject( ev );
                      } else {
                        //	System.out.println( "Ah" +ev.getClass().toString());		
                      }
                    }
                }
	      
    }

    public static void hideSelectedEdges(GraphView view) {
	    java.util.List list = view.getSelectedEdges();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				//Edge e = eview.getEdge();
				//eview.setVisible( false );
				view.hideGraphObject( eview );
			
			}//while
    }
    
    
     public static void unHideSelectedEdges(GraphView view) {
	    java.util.List list = view.getSelectedEdges();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				//Edge e = eview.getEdge();
				//eview.setVisible( true );
				view.showGraphObject( eview );
			
			}//while
    }
    
    public static void invertSelectedNodes(GraphView view) {
	    
			Iterator i = view.getNodeViewsIterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				nview.setSelected( !nview.isSelected() );
			
			}//while
        
    }
    
    public static void invertSelectedEdges(GraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				//Edge e = eview.getEdge();
				eview.setSelected( !eview.isSelected() );
			
			}//while
    }
    
    public static void selectFirstNeighbors(GraphView view) {
	    GraphPerspective graphPerspective = view.getGraphPerspective();
	    Iterator i = view.getNodeViewsIterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				if (nview.isSelected() ) {
					Node n = graphPerspective.getNode(nview.getGraphPerspectiveIndex());
					List nlist = graphPerspective.neighborsList(n);
					 Iterator ni = nlist.iterator();
					 while (ni.hasNext())
					{
						Node neib =(Node) ni.next();
						NodeView neibview = (NodeView)view.getNodeView(neib.getRootGraphIndex());
						neibview.setSelected(true);
					}
				}
			
			}
    }
    
    public static void selectAllNodes(GraphView view) {
	    
	    Iterator i = view.getNodeViewsIterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				nview.setSelected( true );
			
			}//while
    }
     public static void deselectAllNodes(GraphView view) {
	    
	    Iterator i = view.getNodeViewsIterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				nview.setSelected( false );
			
			}//while
    }

    
    public static void selectAllEdges(GraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				eview.setSelected( true );
			
			}//while
    }
    
    public static void deselectAllEdges(GraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				eview.setSelected( false );
			
			}//while
    }
    public static void hideAllEdges(GraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				view.hideGraphObject( eview );
			
			}//while
    }
    
    public static void unHideAllEdges(GraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				view.showGraphObject( eview );
			
			}//while
    }
}
	    
	    
    
   

