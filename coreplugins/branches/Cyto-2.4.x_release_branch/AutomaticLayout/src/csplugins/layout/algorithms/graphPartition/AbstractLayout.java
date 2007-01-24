package csplugins.layout.algorithms.graphPartition;

import giny.model.*;

import cytoscape.task.*;

import cern.colt.list.*;
import cern.colt.map.*;

import java.util.*;
import javax.swing.JOptionPane;
import java.lang.Throwable;

/* NOTE: The AbstractLayout class uses SGraphPartition to generate
   the partitions in the graph. Originally this class used GraphPartition,
   but it is broken. */

import csplugins.layout.algorithms.SGraphPartition;

public abstract class AbstractLayout implements Task {

  private TaskMonitor taskMonitor = null;
  private boolean interrupted = false;

  private GraphPerspective gp;
  Layout layout;
    
  double incr = 100;

  public AbstractLayout ( GraphPerspective gp ) {
    this.gp = gp;
    layout = new Layout( gp );
  }
   
  public abstract void layoutPartion ( GraphPerspective net ) ;


  public Layout getLayout () {
    return layout;
  }

  public void layout () {

    List partitions = SGraphPartition.partition( gp );
    Iterator p = partitions.iterator();

    // monitor
    int percent = 0;
    double currentProgress = 0;
    double lengthOfTask = partitions.size();
    String statMessage = "Layout";
    
    double next_x_start = 0;
    double next_y_start = 0;
    double current_max_y = 0;

    
    double max_dimensions = Math.sqrt( ( double )gp.getNodeCount() );
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

    while ( p.hasNext() ) {
      // get the array of node
      int[] nodes = ( int[] )p.next();
      if ( nodes.length == 0 ) {
        continue;
      }
      current_gp = gp.getRootGraph().createGraphPerspective( nodes, gp.getConnectingEdgeIndicesArray( nodes ) );
      // Partitions Requiring Layout
      if ( nodes.length != 1 ) {
        try
        {
          layoutPartion( current_gp );
        }
        catch(Throwable _e)
        {
          Object buttons[] = { "Print Stack Trace", "OK" };
          if(JOptionPane.showOptionDialog(null, "Failed to layout graph.\n\n", 
                                          "Failed to Layout Graph",
                                          JOptionPane.YES_NO_OPTION,
                                          JOptionPane.ERROR_MESSAGE,
                                          null, buttons, buttons[1]) == 0)
          {
            _e.printStackTrace();
          }
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


    layout.applyLayout( cytoscape.Cytoscape.getCurrentNetworkView() );
    interrupted = true;
    cytoscape.Cytoscape.getCurrentNetworkView().fitContent();
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

  //////////////////////////////
  // implements Task
  
  public String getTitle () {
    return "Layout";
  }

  public void halt () {
    interrupted = true;
  }

  public void run () {
    //if (taskMonitor == null) {
    //  throw new IllegalStateException("Task Monitor is not set.");
    //}
    
    try {
      while ( !interrupted ) {
        layout();
      }
    } catch ( Exception e ) {}

  }

  public void setTaskMonitor ( TaskMonitor monitor ) {
    if (this.taskMonitor != null) {
      throw new IllegalStateException("Task Monitor is already set.");
    }
    this.taskMonitor = taskMonitor;

  }
   
}

