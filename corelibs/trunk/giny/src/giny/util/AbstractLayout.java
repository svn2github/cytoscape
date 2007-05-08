package giny.util;

import giny.model.*;
import giny.view.*;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
abstract public class AbstractLayout  {

  protected Set staticNodes;
  protected GraphView graphView;
  protected Dimension currentSize;


  public AbstractLayout ( GraphView view  ) {
    this.graphView = view;
    this.staticNodes = new HashSet();
  }


  public abstract void doLayout ( );




  public boolean dontMove ( NodeView nv ) {
    return staticNodes.contains( nv );
  }

  /**
	 * Initializer, calls <tt>intialize_local</tt> and
	 * <tt>initializeLocations</tt> to start construction
	 * process.
	 */
  public void initialize( Dimension size ) {
    if ( currentSize != null ) {
      this.currentSize = size;
    } else {
      // currentSize = new Dimension(  graphView.getComponent().getWidth(),
      //                              graphView.getComponent().getHeight() );
       currentSize = new Dimension( 1000, 1000 );
    }

    initialize_local();
    initializeLocations();
    
  }

  /**
	 * Initializes all local information, and is called immediately
	 * within the <tt>initialize()</tt> process.
	 * The user is responsible for overriding this method
	 * to do any construction that may be necessary:
	 * for example, to initialize local per-edge or
	 * graph-wide data.
	 */
  protected abstract void initialize_local();

	/**
	 * Initializes the local information on a single vertex.
	 * The user is responsible for overriding this method
	 * to do any vertex-level construction that may be
	 * necessary: for example, to attach vertex-level
	 * information to each vertex.
	 */
  protected abstract void initialize_local_node_view ( NodeView nv);

  /**
	 * This method calls <tt>initialize_local_vertex</tt> for
	 * each vertex, and also adds initial coordinate information
	 * for each vertex. (The vertex's initial location is
	 * set by calling <tt>initializeLocation</tt>.
	 */
  protected void initializeLocations() {
    for (Iterator iter = graphView.getNodeViewsIterator(); iter.hasNext();) {
      NodeView v = ( NodeView ) iter.next();
      
      if ( !staticNodes.contains( v ) )
        initializeLocation( v, currentSize);
      initialize_local_node_view(v);
    }
  }
  
  /**
   * Sets random locations for a vertex within the dimensions of the space.
   * If you want to initialize in some different way, override this method.
   *
   * @param	v
   * @param d
   */
  protected void initializeLocation ( NodeView v, Dimension d ) {
    double x = Math.random() * d.getWidth();
    double y = Math.random() * d.getHeight();
    v.setXPosition( x, false );
    v.setYPosition( y, false );
    }
  
  /**
	 * {@inheritDoc}
	 * By default, an <tt>AbstractLayout</tt> returns
	 * null for its status.
	 */
  public String getStatus() {
    return null;
  }

  	/**
	 * Implementors must override this method in order
	 * to create a Layout. If the Layout is
	 * the sort that only calculates locations once,
	 * this method may be overridden with an empty
	 * method.<p>
	 * Note that "locked" vertices are not to be moved;
	 * however, it is the policy of the visualization
	 * to decide how to handle them, and what to do with
	 * the vertices around them. Prototypical code might
	 * include a clipping like
	 * <pre>
	 * 		for (Iterator i = getVertices().iterator(); i.hasNext() ) {
	 * 			Vertex v = (Vertex) i.next();
	 * 			if (! dontmove.contains( v ) ) {
	 * 				... // handle the node
	 * 			} else { // ignore the node
	 * 			}
	 * 		}
	 * </pre>
	 * @see Layout#advancePositions()
	 */
  public abstract void advancePositions();
  
  /**
	 * Returns the current size of the visualization
	 * space, accoring to the last call to resize().
	 * @return the current size of the screen
	 */
  public Dimension getCurrentSize() {
    return currentSize;
  }

  /**
	 * When a visualizetion is resized, it presumably
	 * wants to fix the locations of the vertices
	 * and possibly to reinitialize its data. The
	 * current method calls <tt>initializeLocations</tt>
	 * followed by <tt>initialize_local</tt>.
	 * TODO: A better
	 * implementation wouldn't destroy the current
	 * information, but would either scale the
	 * current visualization, or move the nodes toward
	 * the new center.
	 */
  public void resize ( Dimension size ) {
    // are we initialized yet?
    
    Dimension oldSize;
    synchronized (currentSize) {
      if (currentSize.equals( size )) return;
      oldSize = currentSize;
      this.currentSize = size;
    }
    
    int xOffset = (size.width - oldSize.width ) / 2;
		int yOffset = (size.height - oldSize.height) / 2;
    
    // now, move each vertex to be at the new screen center
    for (Iterator iter = graphView.getNodeViewsIterator(); iter.hasNext();) {
			NodeView e = ( NodeView ) iter.next();
			e.setOffset( e.getXPosition() + xOffset, e.getYPosition() + yOffset );
    }
    // optionally, we may want to restart
  }

  /**
	 * Restarts the visualization entirely, as if the the
	 * user had pressed the "scramble" button. Calls
	 * <tt>initializeLocation</tt> for each vertex.
	 * TODO: Is this enough? Should it call the whole
	 * initialization process? Why does resize do more?
	 */
  public void restart() {
		initializeLocations();
		initialize_local();
  }

  /**
   * @return the closest NodeView to these coords.
   */
  public NodeView getNodeView ( double x, double y ) {
    double minDistance = Double.MAX_VALUE;
    NodeView closest = null;
    for ( Iterator iter = graphView.getNodeViewsIterator();
          iter.hasNext(); ) {
      NodeView v = ( NodeView )iter.next();
      double dx = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION ) - x;
      double dy = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION ) - y;
      double dist = dx * dx + dy * dy;
      if ( dist < minDistance ) {
        minDistance = dist;
        closest = v;
      }
    }
    return closest;
  }



  /**
	 * Forcibly moves a vertex to the (x,y) location by
	 * setting its x and y locations to the inputted
	 * location. Does not add the vertex to the "dontmove"
	 * list, and (in the default implementation) does not
	 * make any adjustments to the rest of the graph.
	 */
    public void forceMove ( NodeView picked, double x, double y) {
      picked.setOffset( x, y );
    }

  public void lockNodes ( NodeView[] nodes ) {
    for ( int i = 0; i < nodes.length; ++i ) {
      staticNodes.add( nodes[i] );
    }

  }

  


	/**
	 * Adds the vertex to the DontMove list
	 */
    public void lockVertex ( NodeView v ) {
        staticNodes.add(v);
    }

	/**
	 * Removes the vertex from the DontMove list
	 */
    public void unlockVertex( NodeView v ) {
        staticNodes.remove(v);
    }


}
