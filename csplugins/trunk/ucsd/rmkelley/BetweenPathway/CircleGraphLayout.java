package ucsd.rmkelley.BetweenPathway;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import java.awt.Dimension;
class CircleGraphLayout extends AbstractLayout{
    
  Collection leftCollection;
  Collection rightCollection;
    
  public CircleGraphLayout ( CyNetworkView networkView, Collection leftCollection, Collection rightCollection) {
    super( networkView );
    this.leftCollection = leftCollection;
    this.rightCollection = rightCollection;
  }
    
    
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public Object construct () {
    initialize();
    int[] nodes = networkView.getNetwork().getNodeIndicesArray();
    
    int r = (int)currentSize.getHeight();
    double OFFSET = r*2.0;
    double phi = Math.PI / (leftCollection.size()-1);
    int i=0;
    for(Iterator nodeIt = leftCollection.iterator();nodeIt.hasNext();i++){
      int node = ((Node)nodeIt.next()).getRootGraphIndex();
      networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_X_POSITION, 	OFFSET + r + r * Math.sin(i * phi) );
      networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_Y_POSITION, 	r + r * Math.cos(i * phi) );
      PNodeView nodeView = (PNodeView)networkView.getNodeView(node);
      nodeView.setNodePosition(false);
    }

    phi = Math.PI / (rightCollection.size()-1);
    i = 0;
    for(Iterator nodeIt = rightCollection.iterator();nodeIt.hasNext();i++){
      int node = ((Node)nodeIt.next()).getRootGraphIndex();
      networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_X_POSITION, 	r - r * Math.sin(i * phi) );
      networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_Y_POSITION, 	r + r * Math.cos(i * phi) );
      PNodeView nodeView = (PNodeView)networkView.getNodeView(node);
      nodeView.setNodePosition(false);
    }



    return null;
  }
 
}

