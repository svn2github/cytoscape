//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import phoebe.*;

import phoebe.util.*;
import giny.model.*;
import giny.view.*;
import java.util.*;
import edu.umd.cs.piccolo.*;


import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class HideSelectedNodesAndInterconnectingEdgesAction extends AbstractAction  {
    CytoscapeWindow cytoscapeWindow;
    PGraphView view;
    
    public HideSelectedNodesAndInterconnectingEdgesAction (CytoscapeWindow cytoscapeWindow) {
        super ();
        this.cytoscapeWindow = cytoscapeWindow;
	this.view = cytoscapeWindow.getCyWindow().getView();
    }
    
    public void actionPerformed (ActionEvent e) {
		if (view != null) {
			java.util.List list = view.getSelectedNodes();
			Iterator i = list.iterator();
			while (i.hasNext())
			{
				NodeView nview =(NodeView) i.next();
				Node n = nview.getNode();
				//ColtGraphPerspective gp = (ColtGraphPerspective)graphView.getGraphPerspective();
				//gp.hideNode(n);
				( ( PNode )nview ).setVisible( false );
				//graphView.hideGraphObject( nview );
				int[] na = view.getGraphPerspective().neighborsArray( nview.getGraphPerspectiveIndex() );
				for ( int i2 = 0; i2 < na.length; ++i2 ) {
					int[] edges = view.
					getGraphPerspective().
					getEdgeIndicesArray( nview.getGraphPerspectiveIndex(), na[i2], true, true );
					//if( edges != null )
					//System.out.println( "There are: "+edges.length+" edge between "+nview.getGraphPerspectiveIndex()+" and "+na[i2] );
					for ( int j = 0; j < edges.length; ++j ) {
						PEdgeView ev = ( PEdgeView )view.getEdgeView( edges[j] );
						ev.setVisible( false );
						//graphView.hideGraphObject( ev );
					}
				}
		
			
			
			}//while
		}//if !null
			
		
    }//action performed
}

