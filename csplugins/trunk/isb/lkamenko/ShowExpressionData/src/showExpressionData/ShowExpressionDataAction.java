//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package csplugins.showExpressionData;

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

import cytoscape.*;
import cytoscape.browsers.*;
import cytoscape.util.*;
import cytoscape.data.*;
import csplugins.showExpressionData.graphutil.*;

import cytoscape.view.*;

public class ShowExpressionDataAction extends AbstractAction  {
  public static final int STAR_PLOT = 0;
  public static final int GRID_NODE = 1;
  public static final int PETAL_NODE = 2;
  public static final int RADAR_NODE = 3;
  public static final int PIE_CHART = 4;
  public static final int NORMAL = 5;
    
  public int nodeType = 0;
  ColorInterpolator colorInterpolatorPositive;
  ColorInterpolator colorInterpolatorNegative;
   
  public ShowExpressionDataAction(  int nodeType, String title, ImageIcon icon) {
    super (title, icon);
    this.nodeType = nodeType;
    colorInterpolatorPositive = new ColorInterpolator( 55, Color.green,
                                                       0, Color.white,
                                                       100, Color.green.darker().darker() );
    colorInterpolatorNegative = new ColorInterpolator( 55, Color.red,
                                                       0, Color.white,
                                                       100, Color.red.darker().darker() );
	
  }
    

  public void actionPerformed (ActionEvent ev) {
    if ( Cytoscape.getCurrentNetworkView() == null) {
      JOptionPane.showMessageDialog (null, "There is no loaded network.", "Error",
                                     JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (  Cytoscape.getCurrentNetwork().getExpressionData() == null) {
      JOptionPane.showMessageDialog (null, "No loaded expression data for this network.", "Error",
                                     JOptionPane.ERROR_MESSAGE);
      return;
    }
    // CyWindow win = (CyWindow)networkView;
    //win.setVisualMapperEnabled(false);
    PGraphView graphView = (PGraphView)Cytoscape.getCurrentNetworkView();
    java.util.List list = graphView.getNodeViewsList();
    Iterator i = list.iterator();
    while (i.hasNext()) {
      NodeView nview =(NodeView) i.next();
      int index = nview.getNode().getRootGraphIndex();
      //NodeView neibview = (NodeView)v.getNodeView(index);
      //ClipRadarNode node = (ClipRadarNode) graphView.addNodeView( "cytoscape.graphutil.ClipRadarNode", index);
      Vector data = new Vector();
      Vector lamda = new Vector();
            
      String[] conds = Cytoscape.getExpressionData().getConditionNames();
            
      for ( int j = 0; j < conds.length; ++j ) {
        mRNAMeasurement mrna =  Cytoscape.getExpressionData().getMeasurement( nview.getLabel().getText(), conds[ j ] );
        if ( mrna != null ) { 
          data.add( new Double( mrna.getRatio() ) );
          lamda.add( new Double( mrna.getSignificance() ) );
        }
      }
      if ( data.size() != 0 && lamda.size() != 0 ) {
        //node.addExpressionData( data, lamda );
        switch (nodeType) {
        case STAR_PLOT:
          ClipRadarNode cnode = new ClipRadarNode( index, graphView, data, lamda, conds , colorInterpolatorPositive, colorInterpolatorNegative );
          try { 
            graphView.addNodeView( index, cnode );
          } catch ( Exception e ) {
            System.err.println( "Node: "+cnode+" is having trouble getting updated...");
            break;
          }
          break;
        case GRID_NODE:
          GridNode gnode = new GridNode( index, graphView, data, lamda, conds , colorInterpolatorPositive, colorInterpolatorNegative );
          try { 
            graphView.addNodeView( index, gnode );
          } catch ( Exception e ) {
            System.err.println( "Node: "+gnode+" is having trouble getting updated...");
            break;
          }
          break;
        case PETAL_NODE:
          PetalNode pnode = new PetalNode( index, graphView, data, lamda, conds , colorInterpolatorPositive, colorInterpolatorNegative );
          try { 
            graphView.addNodeView( index, pnode );
          } catch ( Exception e ) {
            System.err.println( "Node: "+pnode+" is having trouble getting updated...");
            break;
          }
          break;
        case RADAR_NODE:
          RadarNode rnode = new RadarNode( index, graphView, data, lamda, conds , colorInterpolatorPositive, colorInterpolatorNegative );
          try { 
            graphView.addNodeView( index, rnode );
          } catch ( Exception e ) {
            System.err.println( "Node: "+rnode+" is having trouble getting updated...");
            break;
          }
          break;
        case PIE_CHART:
          break;
        case NORMAL:
          try { 
            graphView.addNodeView( "phoebe.PNodeView", index );
          } catch ( Exception e ) {
            //System.err.println( "Node: "+rnode+" is having trouble getting updated...");
            break;
          }
          break;
        default:		  
          break;
        }//end of switch
		
        //networkView.redrawGraph(true, false);
      }
    }
  }//action performed
}

