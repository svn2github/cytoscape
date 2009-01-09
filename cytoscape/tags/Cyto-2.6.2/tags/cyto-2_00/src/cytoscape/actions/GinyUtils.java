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
/**
 * Utility operations for selection and hiding/unhiding nodes and edges
 * in a Giny GraphView. Most operations are self-explanatory.
 */
public class GinyUtils {
    
    public static void hideSelectedNodes(GraphView view) {
        //hides nodes and edges between them
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            // use GINY methods
            view.hideGraphObject( nview );
            
            int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
            for ( int i2 = 0; i2 < na.length; ++i2 ) {
                int[] edges = view.getGraphPerspective().
                getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true, true );
                if( edges != null )
                    //System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
                    for ( int j = 0; j < edges.length; ++j ) {
                        // use GINY methods
                        view.hideGraphObject( view.getEdgeView( edges[j] ) );
                    }
            }
        }
    }
    
    public static void unHideSelectedNodes(GraphView view) {
        //hides nodes and edges between them
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
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
        }
    }
    
    public static void unHideAll(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            view.showGraphObject( nview );
        }
        for (Iterator ei = view.getEdgeViewsList().iterator(); ei.hasNext(); ) {
            EdgeView eview =(EdgeView) ei.next();
            view.showGraphObject( eview );
        }	
    }
    
    public static void unHideNodesAndInterconnectingEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            Node n = nview.getNode();
            
            view.showGraphObject( nview );
            int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
            for ( int i2 = 0; i2 < na.length; ++i2 ) {
                int[] edges = view.getGraphPerspective().getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true );
                if( edges != null )
                for ( int j = 0; j < edges.length; ++j ) {
                    EdgeView ev = view.getEdgeView( edges[j] );
                    view.showGraphObject( ev );
                } else {
                    //	System.out.println( "Ah" +ev.getClass().toString());		
                }
            }
        }
    }

    public static void hideSelectedEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.hideGraphObject( eview );
        }
    }
    
    public static void unHideSelectedEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getSelectedEdges().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.showGraphObject( eview );
        }
    }
    
    
    public static void invertSelectedNodes(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            nview.setSelected( !nview.isSelected() );
        }
    }
    
    public static void invertSelectedEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            eview.setSelected( !eview.isSelected() );
        }
    }
    
    public static void selectFirstNeighbors(GraphView view) {
        if (view == null) {return;}
        
        GraphPerspective graphPerspective = view.getGraphPerspective();
        Set nodeViewsToSelect = new HashSet();
        for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            Node n = nview.getNode();
            for (Iterator ni = graphPerspective.neighborsList(n).iterator(); ni.hasNext(); ) {
                Node neib =(Node) ni.next();
                NodeView neibview = view.getNodeView(neib);
                nodeViewsToSelect.add(neibview);
            }
        }
        for (Iterator si = nodeViewsToSelect.iterator(); si.hasNext(); ) {
            NodeView nview = (NodeView)si.next();
            nview.setSelected(true);
        }
    }
    
    public static void selectAllNodes(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            nview.setSelected( true );
        }
    }
    
    public static void deselectAllNodes(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
            NodeView nview =(NodeView) i.next();
            nview.setSelected( false );
        }
    }

    
    public static void selectAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            eview.setSelected( true );
        }
    }
    
    public static void deselectAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            eview.setSelected( false );
        }
    }
    
    public static void hideAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.hideGraphObject( eview );
        }
    }
    
    public static void unHideAllEdges(GraphView view) {
        if (view == null) {return;}
        
        for (Iterator i = view.getEdgeViewsList().iterator(); i.hasNext(); ) {
            EdgeView eview =(EdgeView) i.next();
            view.showGraphObject( eview );
        }
    }
}

