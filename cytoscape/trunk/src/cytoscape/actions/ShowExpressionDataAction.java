//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.AbstractAction;
import phoebe.*;
import phoebe.util.*;
import giny.view.*;
import java.util.*;

import cytoscape.browsers.*;
import cytoscape.util.*;
import cytoscape.data.*;
import cytoscape.graphutil.*;


import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class ShowExpressionDataAction extends AbstractAction  {

    NetworkView networkView;
    ColorInterpolator colorInterpolatorPositive;
    ColorInterpolator colorInterpolatorNegative;
   
    public ShowExpressionDataAction(NetworkView networkView) {
        super ("Display Expression Data");
        this.networkView = networkView;
	colorInterpolatorPositive = new ColorInterpolator( 55, Color.green,
                                                       0, Color.white,
                                                       100, Color.green.darker() );
        colorInterpolatorNegative = new ColorInterpolator( 55, Color.red,
                                                       0, Color.black,
                                                       100, Color.red.darker() );
	
    }
    

    public void actionPerformed (ActionEvent ev) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  //not implemented for y files
	}
	else { // using giny
		PGraphView graphView = networkView.getView();
		java.util.List list = graphView.getNodeViewsList();
            Iterator i = list.iterator();
            while (i.hasNext())
              {
                NodeView nview =(NodeView) i.next();
                int index = nview.getNode().getRootGraphIndex();
                //NodeView neibview = (NodeView)v.getNodeView(index);
                //ClipRadarNode node = (ClipRadarNode) graphView.addNodeView( "cytoscape.graphutil.ClipRadarNode", index);
                Vector data = new Vector();
                Vector lamda = new Vector();
      
                String[] conds = networkView.getNetwork().getExpressionData().getConditionNames();
                for ( int j = 0; j < conds.length; ++j ) {
        
                  mRNAMeasurement mrna =  networkView.getNetwork().getExpressionData().getMeasurement( nview.getLabel(), conds[ j ] );
                  if ( mrna != null ) { 
                    data.add( new Double( mrna.getRatio() ) );
                    lamda.add( new Double( mrna.getSignificance() ) );
                  }
                }
                if ( data.size() != 0 && lamda.size() != 0 ) {
                  //node.addExpressionData( data, lamda );
        
                  ClipRadarNode node = new ClipRadarNode( index, graphView, data, lamda, conds , colorInterpolatorPositive, colorInterpolatorNegative );
                  try { 
                    graphView.addNodeView( index, node );
                  } catch ( Exception e ) {
                    System.err.println( "Node: "+node+" is having trouble getting updated...");
                  }

                }
              }
                  
	}//!Yfiles
			
		
    }//action performed

}

