package csplugins.layout.algorithms.graphPartition;

import giny.model.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.task.*;

import cern.colt.list.*;
import cern.colt.map.*;

import java.util.*;
import javax.swing.JOptionPane;
import java.lang.Throwable;

import cytoscape.layout.AbstractLayout;

/* NOTE: The AbstractGraphPartition class uses SGraphPartition to generate
   the partitions in the graph. Originally this class used GraphPartition,
   but it is broken. */

import csplugins.layout.algorithms.graphPartition.SGraphPartition;

public abstract class AbstractGraphPartition extends AbstractLayout {
  protected TaskMonitor taskMonitor = null;
	protected Layout layout;
	protected boolean selectedOnly = false;
    
  double incr = 100;

  public AbstractGraphPartition ( ) {
		super();
  }
   
  public abstract void layoutPartion ( GraphPerspective net ) ;

	public boolean supportsSelectedOnly() { return true; }

	public void setSelectedOnly (boolean v) {
		this.selectedOnly = v;
	}


	/* AbstractGraphPartitionLayout implements the constuct method
	 * and calls layoutPartion for each partition.
	 */
  public void construct () {
		layout = new Layout(networkView, true);
		initialize();
    List partitions = SGraphPartition.partition( networkView, selectedOnly );
    Iterator p = partitions.iterator();

    // monitor
    int percent = 0;
    double currentProgress = 0;
    double lengthOfTask = partitions.size();
    String statMessage = "Layout";
    
    double next_x_start = 0;
    double next_y_start = 0;
    double current_max_y = 0;

    
    double max_dimensions = Math.sqrt( ( double )network.getNodeCount() );
    // give each node room
    max_dimensions *= incr;

    GraphPerspective current_gp = null;

    currentProgress++;
    percent = (int)( (currentProgress * 100 )/lengthOfTask );
    statMessage = "Completed " + percent + "%";

    if (taskMonitor != null) {
      taskMonitor.setPercentCompleted(percent);
      taskMonitor.setStatus( statMessage );
    }

    //System.out.println( "AbstractLayout::There are "+partitions.size()+" Partitions!!");

    while ( p.hasNext() && !canceled ) {
      // get the array of node
      int[] nodes = ( int[] )p.next();
      if ( nodes.length == 0 ) {
        continue;
      }
      current_gp = network.getRootGraph().createGraphPerspective ( nodes, network.getConnectingEdgeIndicesArray( nodes ));;
      // Partitions Requiring Layout
      if ( nodes.length != 1 ) {
        try
        {
          layoutPartion( current_gp );
        }
        catch(Throwable _e)
        {
          _e.printStackTrace();
          return;
        }
        // offset GP
        double max_width = 0;

        // OFFSET
        offset( nodes, next_x_start, next_y_start );
                                                   
      } // end >1 node partitions  

      // Single Nodes
      else {
        layout.setX( nodes[0], next_x_start );
        layout.setY( nodes[0], next_y_start );
      }


      double[] last_max = maxXmaxY( nodes );
      double last_max_x = last_max[0];
      double last_max_y = last_max[1];
      
      if ( last_max_y > current_max_y ) {
        current_max_y = last_max_y;
      }

      if ( last_max_x > max_dimensions ) {
        next_x_start = 0;
        next_y_start = current_max_y;
        next_y_start += incr;
      } else {
        next_x_start = last_max_x;
        next_x_start += incr;
      }

    } // end iterate through partitions
		layout.applyLayout(networkView);
  }
  
  protected double[] maxXmaxY ( int[] nodes ) {
    double max_x = Double.MIN_VALUE;
    double max_y = Double.MIN_VALUE;

    for ( int i = 0; i < nodes.length; ++i ) {
      double x = layout.getX( nodes[i] );
      double y = layout.getY( nodes[i] );
      
      if ( x > max_x )
        max_x = x;
      
      if ( y > max_y )
        max_y = y;


    }
      
    double[] r = new double[2];
    r[0] = max_x;
    r[1] = max_y;
    return r;
    
  }

  protected double[] minXminY ( int[] nodes ) {
    double min_x = Double.MAX_VALUE;
    double min_y = Double.MAX_VALUE;

    for ( int i = 0; i < nodes.length; ++i ) {
      double x = layout.getX( nodes[i] );
      double y = layout.getY( nodes[i] );
      
      if ( x < min_x )
        min_x = x;
      
      if ( y < min_y )
        min_y = y;
    }
    //return new double[] {min_x, min_y };
    double[] r = new double[2];
    r[0] = min_x;
    r[1] = min_y;
    return r;
  }


  /**
   * Subtact the Min, add the offset
   */
  public void offset ( int[] nodes, double x_offset, double y_offset ) {
    double[] mins = minXminY( nodes );
    double min_x = mins[0];
    double min_y = mins[1];

      //System.out.println( "AbstractLayout::Offset: "+nodes.length+" nodes. MinX/Offset:" +min_x+"/"+x_offset+" MinY/Offset:" +min_y+"/"+y_offset );

    for ( int i = 0; i < nodes.length; ++i ) {
      layout.setX( nodes[i], layout.getX( nodes[i] ) - min_x + x_offset );
      layout.setY( nodes[i], layout.getY( nodes[i] ) - min_y + y_offset );
    }
  }
}

