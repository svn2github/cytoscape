package csplugins.mdnodes;


import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.*;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;
import cytoscape.plugin.jar.JarLoader;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.*;
import cytoscape.*;

import phoebe.util.ColorInterpolator;
import phoebe.PGraphView;

public class MDNodePlugin extends CytoscapePlugin {

  public static final int STAR_PLOT = 0;
  public static final int GRID_NODE = 1;
  public static final int PETAL_NODE = 2;
  public static final int RADAR_NODE = 3;
  public static final int PIE_CHART = 4;
  public static final int NORMAL = 5;

  /**
   * An <b>in order</b> list of attributes to be mapped
   */
  protected static String[] mappedAttributesPrimary;
  protected static String[] mappedAttributesSecondary;

  static ColorInterpolator colorInterpolatorPositive;
  static ColorInterpolator colorInterpolatorNegative;
   
  

  public MDNodePlugin () {

    colorInterpolatorPositive = new ColorInterpolator( 55, Color.green,
                                                       0, Color.white,
                                                       100, Color.green.darker().darker() );
    colorInterpolatorNegative = new ColorInterpolator( 55, Color.red,
                                                       0, Color.white,
                                                       100, Color.red.darker().darker() );

   


    Cytoscape.getDesktop().getCyMenus().getMenuBar().
      getMenu("Visualization.Multi-Dimenional Nodes").
      add( new JMenuItem ( new AbstractAction( "Normal" ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                applyToNodes( NORMAL );
              }
            } ); } } ) );
  
    Cytoscape.getDesktop().getCyMenus().getMenuBar().
      getMenu("Visualization.Multi-Dimenional Nodes").
      add( new JMenuItem ( new AbstractAction( "Star Plots", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt("star.gif")) ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                applyToNodes( STAR_PLOT );
              }
            } ); } } ) );
    
    Cytoscape.getDesktop().getCyMenus().getMenuBar().
      getMenu("Visualization.Multi-Dimenional Nodes").
      add( new JMenuItem ( new AbstractAction( "Grid Nodes", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt("grid.gif")) ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                applyToNodes( GRID_NODE );
              }
            } ); } } ) );

    Cytoscape.getDesktop().getCyMenus().getMenuBar().
      getMenu("Visualization.Multi-Dimenional Nodes").
      add( new JMenuItem ( new AbstractAction( "Petal Nodes", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt("petal.gif")) ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                applyToNodes( PETAL_NODE );
              }
            } ); } } ) );

    Cytoscape.getDesktop().getCyMenus().getMenuBar().
      getMenu("Visualization.Multi-Dimenional Nodes").
      add( new JMenuItem ( new AbstractAction( "Radar Nodes", new ImageIcon( JarLoader.getLoader().getObjectIfYouWantIt("radar.gif")) ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                applyToNodes( RADAR_NODE );
              }
            } ); } } ) );
  }


  protected static void applyToNodes ( int node_type ) {
   
    if ( Cytoscape.getCurrentNetworkView() == null) {
      JOptionPane.showMessageDialog (null, "There is no loaded network.", "Error",
                                     JOptionPane.ERROR_MESSAGE);
      return;
    }
   
    mappedAttributesPrimary = Cytoscape.getExpressionData().getConditionNames();
    //  mappedAttributesSecondary = Cytoscape.getExpressionData().getConditionNames();
    
    PGraphView graphView = (PGraphView)Cytoscape.getCurrentNetworkView();
    java.util.List list = graphView.getNodeViewsList();
    Iterator i = list.iterator();
    while (i.hasNext()) {



      NodeView nview =(NodeView) i.next();
      int index = nview.getNode().getRootGraphIndex();
      
      Vector data = new Vector();
      Vector lamda = new Vector();

      for ( int j = 0; j < mappedAttributesPrimary.length; ++j ) {
        mRNAMeasurement mrna =  Cytoscape.getExpressionData().getMeasurement( nview.getNode().getIdentifier(), mappedAttributesPrimary[j] );
        if ( mrna != null ) { 
          data.add( new Double( mrna.getRatio() ) );
          lamda.add( new Double( mrna.getSignificance() ) );
        }
      }
            
      if ( data.size() != 0 && lamda.size() != 0 ) {
        //node.addExpressionData( data, lamda );
        switch (node_type)  {
        case STAR_PLOT:
          ClipRadarNode cnode = new ClipRadarNode( index, graphView, data, lamda, mappedAttributesPrimary );
          try { 
            graphView.addNodeView( index, cnode );
          } catch ( Exception e ) {
            System.err.println( "Node: "+cnode+" is having trouble getting updated...");
            break;
          }
          break;
        case GRID_NODE:
          GridNode gnode = new GridNode( index, graphView, data, lamda, mappedAttributesPrimary  );
          try { 
            graphView.addNodeView( index, gnode );
          } catch ( Exception e ) {
            System.err.println( "Node: "+gnode+" is having trouble getting updated...");
            break;
          }
          break;
        case PETAL_NODE:
          PetalNode pnode = new PetalNode( index, graphView, data, lamda, mappedAttributesPrimary  );
          try { 
            graphView.addNodeView( index, pnode );
          } catch ( Exception e ) {
            System.err.println( "Node: "+pnode+" is having trouble getting updated...");
            break;
          }
          break;
        case RADAR_NODE:
          RadarNode rnode = new RadarNode( index, graphView, data, lamda, mappedAttributesPrimary  );
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
        }
      //end of switch
		
        //networkView.redrawGraph(true, false);
      }
    }
  }


}


