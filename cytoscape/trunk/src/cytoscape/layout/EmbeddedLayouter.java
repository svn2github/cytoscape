//
// EmbeddedLayouter.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.layout;

import java.io.*;
import java.util.*;

import y.base.*;
import y.geom.*;
import y.layout.*;
import y.layout.transformer.GraphTransformer;
import y.layout.circular.CircularLayouter;

import java.awt.Rectangle;

/**
 * A Layouter based on LEDA's Embedded Spring
 * Layouter.
 *
 * This layouter is similar to yFiles'
 * <code>OrganicLayouter</code>
 * in that they both use spring forces to arrive at
 * a near-equilibrium graph. However,
 * <code>EmbeddedLayouter</code> adds a second pass
 * to the layouting, in which nodes only feel forces
 * locally.  This serves to allow the layout to look
 * natural, while better filling available space.
 *
 * Also, layouts produced by
 * <code>EmbeddedLayouter</code> can be constrained
 * to given dimensions.
 */
public class EmbeddedLayouter implements Layouter {
    /**
     * Target width of final layout
     */
    private float iTWidth;

    /**
     * Target height of final layout
     */
    private float iTHeight;

    /**
     * log of 2 (for taking log base 2 of things)
     */
    private static final double LOG2 = Math.log(2);

    /**
     * Connectedness table for any 2 nodes, by index
     * into the graph's node array
     */
    private boolean[][] connected;

    /**
     * Number of connections on a node by its index
     */
    private int[] degree;




    /**
     * Default constructor.  Assumes target dimensions
     * are current dimensions.
     */
    public EmbeddedLayouter() {
	iTWidth = Float.NEGATIVE_INFINITY;
	iTHeight = Float.NEGATIVE_INFINITY;
    }

    /**
     * Initialize target width and height to suplied values
     */
    public EmbeddedLayouter(float width, float height) {
	iTWidth = width;
	iTHeight = height;
    }


    /**
     * Can we layout the graph?
     *
     * @return <code>true</code>, always.
     */
    public boolean canLayout(LayoutGraph graph) {
	return true;
    }

    /**
     * Layout the graph in two stages.
     *
     * The first stage is a simple spring force model.
     * The second phase adds the complexity of
     * "binning," during which nodes only feel forces
     * locally.
     */
    public void doLayout(LayoutGraph graph) {
	// first initialize useful variables
	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();

	// get target width and height from current
	// width and height, if none had been supplied
	if (iTWidth == Float.NEGATIVE_INFINITY) {
	    iTWidth = (float)graph.getBoundingBox().getWidth();
	    iTHeight = (float)graph.getBoundingBox().getHeight();
	}

	if (nC <= 1) return;

	// initialize array of connectedness (iterate over edges)
	// (cares about directionality)
	connected = new boolean[nC][nC];
	for (EdgeCursor edges = graph.edges(); edges.ok(); edges.next()) {
	    int s = 0; int t = 0;

	    for (int i = 0; i < nC; i++) {
		if (nodeList[i] == edges.edge().source())
		    s = i;
		else if (nodeList[i] == edges.edge().target())
		    t = i;
	    }

	    connected[s][t] = true;
	}

	double maxNodeSize = 0;

	// initialize array of degree (number of edges on given node)
	// and find largest node size
	degree = new int[nC];
	for (int i = 0; i < nC; i++) {
	    double s = graph.getWidth(nodeList[i])/Math.sqrt(2);
	    if (s > maxNodeSize)
		maxNodeSize = s;
	    
	    for (int j = 0; j < nC; j++)
		if (connected[i][j]) degree[i]++;
	}


	System.out.println("WeightedLayout ("+nC+" nodes)");


	// rescaler
	GraphTransformer scaler = new GraphTransformer();
	scaler.setOperation(GraphTransformer.SCALE);

	YRectangle size;
	double sx, sy, scale;

	// DEBUG:
	//System.out.println("  target:"+iTWidth+"x"+iTHeight);
	//System.out.println("  bounding box:"+graph.getBoundingBox().width+"x"
	//		   +graph.getBoundingBox().height);
	//System.out.println("  max node size:"+maxNodeSize);
	//System.out.println("  shrunk target:"+iTWidth+"x"+iTHeight);


	// step 1: scale graph to fit in smaller box
	//size = getSize(graph, true);
	//sx = (iTWidth/(2*maxNodeSize)) / size.getWidth();
	//sy = (iTHeight/(2*maxNodeSize)) / size.getHeight();
	//scale = (sx < sy ? sx : sy);
       	//scaler.setScaleFactor(scale);
	//scaler.doLayoutCore(graph);
	
	//size = getSize(graph, false);
	//sx = (iTWidth) / size.getWidth();
	//sy = (iTHeight) / size.getHeight();
	//scale = (sx < sy ? sx : sy);
       	//scaler.setScaleFactor(scale);
	//scaler.doLayoutCore(graph);

	// make sure nodes don't overlap
	//CircularLayouter mule = new CircularLayouter();
	//mule.doLayout(graph);

       	// step 2: spring first pass. everybody feels
	// repulsion from everybody, node size ignored,
	// confined to miniature box
	//doSpringLayoutFull(graph, 100,
	//		   iTWidth/(2*maxNodeSize),
	//		   iTHeight/(2*maxNodeSize));
	doSpringLayoutFull(graph, 100, iTWidth, iTHeight);

	// find scaling factor to make sure nodes don't
	// overlap
	//for (EdgeCursor edges = graph.edges(); edges.ok(); edges.next()) {
	//    Edge edge = edges.edge();
	//}

	// step 3: scale up graph back to target size
	size = getSize(graph, true);
	sx = iTWidth / size.getWidth();
	sy = iTHeight / size.getHeight();
       	scaler.setScaleFactor((sx < sy ? sx : sy));
	scaler.doLayoutCore(graph);

	// step 4: spring second pass. bin-wise layout,
	// node size counts
	doSpringLayoutBinned(graph, 200, iTWidth, iTHeight);

	// step 5: scale graph to make sure it fits in the
	// the target area *taking into account node sizes*
	// also, scale the nodes themselves so relative
	// sizes maintained.  (good idea?)
	size = getSize(graph, false);
	sx = iTWidth / size.getWidth();
	sy = iTHeight / size.getHeight();
	scaler.setScaleFactor((sx < sy ? sx : sy));
	scaler.setScaleNodeSize(true);
	scaler.doLayoutCore(graph);
    }



    /**
     * Layout the graph based on a (relatively) pure
     * embedded spring force model.
     *
     * @param graph The graph to lay out
     * @param iterations The number of iterations of
     *    the force model to do
     * @param width The target width of the graph
     *    post-layout
     * @param height The target height of the graph
     *    post-layout
     */
    private void doSpringLayoutFull(LayoutGraph graph, int iterations,
				    double width, double height) {
	// first initialize useful variables
	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();

	// initialize arrays of x and y positions
	double[] xpos = new double[nC];
	double[] ypos = new double[nC];
	double[] size = new double[nC];
	double maxSize = Double.NEGATIVE_INFINITY;
	for (int i = 0; i < nC; i++) {
	    xpos[i] = graph.getCenterX(nodeList[i]);
	    ypos[i] = graph.getCenterY(nodeList[i]);
	    size[i] = graph.getWidth(nodeList[i])/Math.sqrt(2.0);
	    if (size[i] > maxSize)
		maxSize = size[i];
	}

	NodeMap slug = (NodeMap)graph.getDataProvider("Cytoscape:slug");


	// ideal radius around each node
	double kIdeal = Math.sqrt(width*height/(double)nC)/2.0;
	//double kIdeal = 2.5*maxSize;
	//double k = Math.sqrt(width*height/(double)nC)/2.0;
	//double kk = k*k;

	
	// the main loop.  iterate a metric bajillion times
	for (int iter = 1; iter <= iterations; iter++) {
	    int pct = (int)(((double)iter / (double)iterations)*100);
	    System.out.print("  "+pct+"%  \r");

	    // damping factor 25*(log base 2 of iterations+1)
	    double damp = 25.0*Math.log(iter+1)/LOG2; // Math.log(2.0);

	    // x and y damping factors taking into mind target
	    // width and height
	    double tx = width / damp;
	    double ty = height / damp;


	    // deltas for accumulating change
	    double[] dx = new double[nC];
	    double[] dy = new double[nC];


	    //
	    // repulsive forces
	    // 
	    for (int v = 0; v < nC; v++) {
		double vx = xpos[v];
		double vy = ypos[v];
		
		for (int u = 0; u < nC; u++) {
		    if (u == v) continue;

		    double xdist = vx - xpos[u];
		    double ydist = vy - ypos[u];

		    // square of the distance
		    // (physics obfuscated for speed)
		    double distSq = xdist*xdist + ydist*ydist;

		    //if (distSq < .001) distSq = .001;
		    if (distSq < .000001) distSq = .000001;

		    double k = kIdeal + size[v] + size[u];
		    double kk = k*k;


		    // expanding "force" with extra
		    // dist in denominator (accounted
		    // for in following two lines)
		    double frepulse = kk/distSq;

		    dx[v] += frepulse * xdist;
		    dy[v] += frepulse * ydist;
		}

		dx[v] += (Math.random()-.5)/100;
		dy[v] += (Math.random()-.5)/100;
	    }


	    //
	    // attractive forces
	    //
	    for (int v = 0; v < nC; v++) {
		for (int u = 0; u < nC; u++) {
		    if (!connected[u][v]) continue;

		    double xdist = xpos[v] - xpos[u];
		    double ydist = ypos[v] - ypos[u];
		    
		    double dist = Math.sqrt(xdist*xdist + ydist*ydist);

		    // component-wise scaling factor:
		    //  o find f = (degree[u]+degree[v]) / 16
		    //  o then dist / f conceptually makes
		    //    distances from highly connected nodes
		    //    feel smaller
		    //  o then scaling factor = (dist/f)/k
		    //                        = dist/(f*k)
		    //                        = 16 * dist / ((SUM deg)*k)
		    //
		    // the constant 16 determines how close the
		    // nodes are.  higher means closer

		    double k = kIdeal + size[v] + size[u];
		    double sf = 16.0*dist/((double)(degree[u]+degree[v])*k);

		    dx[v] -= xdist*sf;
		    dy[v] -= ydist*sf;
		    dx[u] += xdist*sf;
		    dy[u] += ydist*sf;
		}
	    }


	    //
	    // preventions
	    //
	    for (int v = 0; v < nC; v++) {
		double xd = dx[v];
		double yd = dy[v];
		
		double magnitude = Math.sqrt(xd*xd+yd*yd);

		// move nodes in unit vector direction
		// they want to move, but scaled down
		// by scaling factors tx, ty
		xd = tx*xd/magnitude;
		yd = ty*yd/magnitude;

		if (slug != null) {
		    double sluggish = slug.getDouble(nodeList[v]);
		    xd *= sluggish;
		    yd *= sluggish;
		}

		// update positions
		xpos[v] += xd;
		ypos[v] += yd;
	    }
	}


	// all done, move nodes in graph to reflect
	// new positions
	System.out.println("");
	for (int i = 0; i < nC; i++) {
	    //System.out.println("  1:"+xpos[i]+","+ypos[i]);
	    graph.setCenter(nodeList[i], xpos[i], ypos[i]);
	}
    }















    /**
     * Layout the graph, but only consider repulsive
     * forces from close neighbors.
     *
     * @param graph The graph to lay out
     * @param iterations The number of iterations of
     *    the force model to do
     * @param width The target width of the graph
     *    post-layout
     * @param height The target height of the graph
     *    post-layout
     */
    private void doSpringLayoutBinned(LayoutGraph graph, int iterations,
				      double width, double height) {
	// first initialize useful variables
	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();

	if (nC <= 1) return;

	// initialize arrays of x and y positions
	double[] xpos = new double[nC];
	double[] ypos = new double[nC];
	double[] size = new double[nC];
	double maxSize = Double.NEGATIVE_INFINITY;
	for (int i = 0; i < nC; i++) {
	    xpos[i] = graph.getCenterX(nodeList[i]);
	    ypos[i] = graph.getCenterY(nodeList[i]);
	    size[i] = graph.getWidth(nodeList[i])/Math.sqrt(2.0);
	    if (size[i] > maxSize)
		maxSize = size[i];
	}

	// find top and left
	float left, top;
	left = top = Float.POSITIVE_INFINITY;
	for (NodeCursor nodes = graph.nodes(); nodes.ok(); nodes.next()) {
	    if (graph.getCenterX(nodes.node()) < left)
		left = (float) graph.getCenterX(nodes.node());
	    if (graph.getCenterY(nodes.node()) < top)
		top = (float) graph.getCenterY(nodes.node());
	}


	NodeMap slug = (NodeMap)graph.getDataProvider("Cytoscape:slug");

	// ideal radius around each node
        // (and various other useful values)
	double kIdeal
	    = Math.sqrt((double)iTWidth*(double)iTHeight/(double)nC)/2.0;
	//double kIdeal = 2.5*maxSize;
	//double kk = k*k;
	//double kk4 = 4*k*k;


	// find node sizes and the bin size to split x and y by.
	// use MAX(k, MAX(node sizes))
	int grid = 1;

	for (int i = 0; i < nC; i++) {
	    int s = (int)(graph.getWidth(nodeList[i])*Math.sqrt(2));
	    if (s > grid)
		grid = s;
	}

	if (kIdeal > grid)
	    grid = (int)kIdeal;


	// number of bins on x and y dimension
	int xA = (int)(iTWidth/grid)+1;
	int yA = (int)(iTHeight/grid)+1;

	// 2d array of node lists.  these are the bins
	// that store the indeces of the nodes the own
	//
	// the vector goes from -1 to xA, -1 to yA
	// (inclusive).  hence whenever accessing
	// A[c][d] use A[c+1][d+1]
	Vector A[][] = new Vector[xA+2][yA+2];
	for (int v = 0; v < nC; v++) {
	    int i = (int)(xpos[v] - left) / grid;
	    int j = (int)(ypos[v] - top) / grid;

	    if (A[i+1][j+1] == null)
		A[i+1][j+1] = new Vector();

	    A[i+1][j+1].addElement(new Integer(v));
	}



	for (int iter = 1; iter <= iterations; iter++) {
	    int pct = (int)(((double)iter / (double)iterations)*100);
	    System.out.print("  "+pct+"%   \r");

	    // damping factor 50*(log base 2 of iterations+1)
	    double damp = 50.0*Math.log(iter+1)/LOG2; // Math.log(2.0);

	    // x and y damping factors taking into mind target
	    // width and height
	    double tx = iTWidth / damp;
	    double ty = iTHeight / damp;

	    // deltas for accumulating change
	    double[] dx = new double[nC];
	    double[] dy = new double[nC];

	    //
	    // repulsive forces
	    // 
	    for (int v = 0; v < nC; v++) {
		double vx = xpos[v];
		double vy = ypos[v];

		int i = (int)(vx - left) / grid;
		int j = (int)(vy - top) / grid;


		// look at nodes in surrounding blocks (9)
		for (int m = -1; m <= 1; m++)
		    for (int n = -1; n <= 1; n++) {
			Vector list = A[i+m+1][j+n+1];

			if (list == null) continue;

			// look at all nodes in list
			for (int uL = 0; uL < list.size(); uL++) {
			    int u = ((Integer)list.elementAt(uL)).intValue();

			    if (u == v) continue;

			    double xdist = vx - xpos[u]; //- size[v] - size[u];
			    double ydist = vy - ypos[u]; //- size[v] - size[u];

			    // square of the distance
			    // (physics obfuscated for speed)
			    double distSq = xdist*xdist + ydist*ydist;

			    if (distSq < .001) distSq = .001;

			    double k = kIdeal + size[v] + size[u];
			    double kk = k*k;
			    //double kk4 = 4*k*k;

			    // expanding "force" with extra
			    // dist in denominator (accounted
			    // for in following two lines)
			    // also, cut off when 2k < dist
			    double frepulse = ((4*kk>distSq) ? kk/distSq : 0);

			    dx[v] += frepulse * xdist;
			    dy[v] += frepulse * ydist;

			    /*
			    if (u == v) continue;

			    double xdist = vx - xpos[u];
			    double ydist = vy - ypos[u];

			    double dist = Math.sqrt(xdist*xdist+ydist*ydist);
			    if (dist < .001) dist = .001;

			    // "force" of repulsion for close
			    double frepulse = ((k2 > dist) ? kk/dist : 0);

			    dx[v] += frepulse * xdist / dist;
			    dy[v] += frepulse * ydist / dist;
			    */
			}

			// heat
			dx[v] *= (.5*Math.random())+.75;
			dy[v] *= (.5*Math.random())+.75;

			dx[v] += (Math.random()-.5)/100;
			dy[v] += (Math.random()-.5)/100;
		    }
	    }


	    //
	    // attractive forces
	    //
	    for (int v = 0; v < nC; v++) {
		for (int u = 0; u < nC; u++) {
		    
		    if (!connected[u][v]) continue;

		    double xdist = xpos[v] - xpos[u]; // - size[v] - size[u];
		    double ydist = ypos[v] - ypos[u]; // - size[v] - size[u];
		    
		    double dist = Math.sqrt(xdist*xdist + ydist*ydist);

		    // same scaling factor as in doSpringLayoutFull,
		    // but with the constant 6 replacing 16
		    // (the higher the number, the closer the
		    // nodes will be)

		    double k = kIdeal + size[v] + size[u];
		    double sf = 4.0*dist/((double)(degree[u]+degree[v])*k);

		    dx[v] -= xdist*sf;
		    dy[v] -= ydist*sf;
		    dx[u] += xdist*sf;
		    dy[u] += ydist*sf;
		}
	    }


	    //
	    // preventions
	    //
	    for (int v = 0; v < nC; v++) {
		double xd = dx[v];
		double yd = dy[v];
		
		double dist = Math.sqrt(xd*xd+yd*yd);
		
		if (dist < 1) dist = 1;

		// move unitwise
		xd = tx*xd/dist;
		yd = ty*yd/dist;


		if (slug != null) {
		    double sluggish = slug.getDouble(nodeList[v]);
		    xd *= sluggish;
		    yd *= sluggish;
		}

		// current bins
		int i, i0, j, j0;
		i = i0 = (int)(xpos[v] - left) / grid;
		j = j0 = (int)(ypos[v] - top) / grid;

		// proposed new positions
		double xp = xpos[v] + xd;
		double yp = ypos[v] + yd;

		// reset position and bin if within bounds
		if (xp > left && xp < left + iTWidth) {
		    xpos[v] = xp;
		    i = (int)((xp - left) / grid);
		}
		if (yp > top && yp < top + iTHeight) {
		    ypos[v] = yp;
		    j = (int)((yp - top) / grid);
		}

		// update bins to reflect nodes that moved
		if (i != i0 || j != j0) {
		    Vector oldList = A[i0+1][j0+1];
		    for (int uL = 0; uL < oldList.size(); uL++) {
			if (((Integer)oldList.elementAt(uL)).intValue() == v)
			    oldList.removeElementAt(uL);
		    }

		    Vector newList = A[i+1][j+1];
		    if (newList == null)
			newList = A[i+1][j+1] = new Vector();
		    
		    newList.addElement(new Integer(v));
		}
	    }
	}


	System.out.println("");
	for (int i = 0; i < nC; i++) {
	    //System.out.println("  2:"+xpos[i]+","+ypos[i]);
	    graph.setCenter(nodeList[i], xpos[i], ypos[i]);
	}
    }



    /**
     * Return the smallest rectangle containing all
     * nodes in the graph.
     *
     * @return The bounding rectangle
     * @param graph The graph to consider
     * @param ignoreSizes Set to <code>true</code> to
     *     only look at node centers, ignoring their radii.
     */
    private YRectangle getSize(LayoutGraph graph, boolean ignoreSizes) {
	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();
	double top = Double.POSITIVE_INFINITY;
	double bottom = Double.NEGATIVE_INFINITY;
	double left = Double.POSITIVE_INFINITY;
	double right = Double.NEGATIVE_INFINITY;
	

	if (ignoreSizes) {
	    for (int i = 0; i < nC; i++) {
		double x = graph.getCenterX(nodeList[i]);
		double y = graph.getCenterY(nodeList[i]);

		if (x < left)
		    left = x;
		if (x > right)
		    right = x;
		if (y < top)
		    top = y;
		if (y > bottom)
		    bottom = y;
	    }
	} else {
	    for (int i = 0; i < nC; i++) {
		double x = graph.getCenterX(nodeList[i]);
		double y = graph.getCenterY(nodeList[i]);

		double w = graph.getWidth(nodeList[i])/2;
		double h = graph.getHeight(nodeList[i])/2;

		if (x-w < left)
		    left = x-w;
		if (x+w > right)
		    right = x+w;
		if (y-h < top)
		    top = y-h;
		if (y+h > bottom)
		    bottom = y+h;
	    }
	}

	//System.out.println("  !:("+left+","+top+"),("+right+","+bottom+")");
	return new YRectangle(left, top, (right - left), (bottom - top));
    }
    
	/*

	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();

	// initialize arrays of x and y positions
	double[] xpos = new double[nC];
	double[] ypos = new double[nC];
	for (int i = 0; i < nC; i++) {
	    xpos[i] = graph.getCenterX(nodeList[i]);
	    ypos[i] = graph.getCenterY(nodeList[i]);
	}

	// initialize array of connectedness (iterate over edges)
	boolean[][] connected = new boolean[nC][nC];
	for (EdgeCursor edges = graph.edges(); edges.ok(); edges.next()) {
	    int s = 0; int t = 0;

	    for (int i = 0; i < nC; i++) {
		if (nodeList[i] == edges.edge().source())
		    s = i;
		else if (nodeList[i] == edges.edge().target())
		    t = i;
	    }

	    connected[s][t] = connected[t][s] = true;
	}

	// initialize array of degree (number of edges on given node)
	int[] degree = new int[nC];
	for (int i = 0; i < nC; i++)
	    for (int j = 0; j < nC; j++)
		if (connected[i][j]) degree[i]++;

	
	double k = Math.sqrt((double)iTWidth*(double)iTHeight/(double)nC)/2.0;

	// the main loop.  iterate a metric bajillion times
	//if (false)
	for (int iter = 1; iter <= iIter; iter++) {
	    int pct = (int)(((double)iter / (double)iIter)*100);
	    System.out.print("  "+pct+"%\r");


	    // deltas for accumulating change
	    double[] dx = new double[nC];
	    double[] dy = new double[nC];


	    //
	    // repulsive forces
	    // 
	    for (int v = 0; v < nC; v++) {
		double vx = xpos[v];
		double vy = ypos[v];
		
		for (int u = 0; u < nC; u++) {
		    if (u == v) continue;

		    double xdist = vx - xpos[u];
		    double ydist = vy - ypos[u];

		    double dist = Math.sqrt(xdist*xdist + ydist*ydist);

		    if (dist < .001)
			xdist = ydist = dist = .001;

		    //double frepulse = (dist > 2*k ? (k*k)/(dist) : 0);
		    double frepulse = 100.0 * Math.pow(0.1, dist * .04);
		    // System.out.println("  "+dist+" "+frepulse);

		    dx[v] += frepulse * xdist / dist;
		    dy[v] += frepulse * ydist / dist;
		}
	    }


	    //
	    // attractive forces
	    //
	    for (int v = 0; v < nC; v++) {
		for (int u = 0; u < nC; u++) {
		    if (!connected[u][v]) continue;

		    double xdist = xpos[v] - xpos[u];
		    double ydist = ypos[v] - ypos[u];
		    
		    double dist = Math.sqrt(xdist*xdist + ydist*ydist);

		    double fattract = (dist - 10.0);
		    
		    dx[v] -= fattract * xdist/dist;
		    dy[v] -= fattract * ydist/dist;
		    // reverse?
		}
	    }


	    //
	    // move
	    //
	    for (int v = 0; v < nC; v++) {
		double scale = Math.pow(.8, iter);
		double xd = dx[v] * scale;
		double yd = dy[v] * scale;
		
		xpos[v] += xd;
		ypos[v] += yd;
	    }
	}


	for (int i = 0; i < nC; i++) {
	    System.out.println("  "+xpos[i]+","+ypos[i]);
	    graph.setCenter(nodeList[i], xpos[i], ypos[i]);
	}

	*/


}
