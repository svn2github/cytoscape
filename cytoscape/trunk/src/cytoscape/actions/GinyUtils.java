//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import phoebe.*;

import phoebe.util.*;
import giny.model.*;
import giny.view.*;
import java.util.*;

//-------------------------------------------------------------------------
public class GinyUtils {
    
    public static void hideSelectedNodes(PGraphView view) {
	    
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
    
    
    public static void unHideSelectedNodes(PGraphView view) {
	    
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
    
    
    public static void unHideAll(PGraphView view) {
	    
	    java.util.List list = view.getNodeViewsList();
			Iterator i = list.iterator();
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
    
    public static void unHideNodesAndInterconnectingEdges(PGraphView view) {
	    java.util.List list = view.getNodeViewsList();
            Iterator i = list.iterator();
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

    public static void hideSelectedEdges(PGraphView view) {
	    java.util.List list = view.getSelectedEdges();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				PEdgeView eview =(PEdgeView) i.next();
				//Edge e = eview.getEdge();
				eview.setVisible( false );
			
			}//while
    }
    
    
     public static void unHideSelectedEdges(PGraphView view) {
	    java.util.List list = view.getSelectedEdges();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				PEdgeView eview =(PEdgeView) i.next();
				//Edge e = eview.getEdge();
				eview.setVisible( true );
			
			}//while
    }
    
    public static void invertSelectedNodes(PGraphView view) {
	    java.util.List list = view.getNodeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				nview.setSelected( !nview.isSelected() );
			
			}//while
        
    }
    
    public static void invertSelectedEdges(PGraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				PEdgeView eview =(PEdgeView) i.next();
				//Edge e = eview.getEdge();
				eview.setSelected( !eview.isSelected() );
			
			}//while
    }
    
    public static void selectFirstNeighbors(PGraphView view) {
	    GraphPerspective graphPerspective = view.getGraphPerspective();
	    java.util.List list = view.getNodeViewsList();
			Iterator i = list.iterator();
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
    
    public static void selectAllNodes(PGraphView view) {
	    
	    java.util.List list = view.getNodeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				PNodeView nview =(PNodeView) i.next();
				nview.setSelected( true );
			
			}//while
    }
     public static void deselectAllNodes(PGraphView view) {
	    
	    java.util.List list = view.getNodeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				PNodeView nview =(PNodeView) i.next();
				nview.setSelected( false );
			
			}//while
    }

    
    public static void selectAllEdges(PGraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				eview.setSelected( true );
			
			}//while
    }
    
    public static void deselectAllEdges(PGraphView view) {
	    java.util.List list = view.getEdgeViewsList();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				EdgeView eview =(EdgeView) i.next();
				eview.setSelected( false );
			
			}//while
    }
}
	    
	    
    
   

