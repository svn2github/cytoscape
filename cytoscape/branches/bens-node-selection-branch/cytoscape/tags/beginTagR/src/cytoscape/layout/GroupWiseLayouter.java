//
// GraphWiseLayouter.java
//
// the oh-so-group-wise layout algorithm
//
// dramage : 2002.1.8
//


package cytoscape.layout;

import java.io.*;
import java.awt.Rectangle;

import y.base.*;
import y.layout.*;
import y.geom.*;

import y.layout.Layouter;
import y.layout.random.RandomLayouter;
import y.layout.circular.CircularLayouter;
import y.layout.organic.OrganicLayouter;
import y.layout.transformer.GraphTransformer;

import javax.swing.JOptionPane;

public class GroupWiseLayouter implements Layouter {
    public GroupWiseLayouter () {
    }


    // canLayoutCore
    //
    // can we layout the graph?
    //public boolean canLayoutCore(LayoutGraph graph) {
    public boolean canLayout(LayoutGraph graph) {
	return true;
    }


    // doLayoutCore
    //
    // do the layouting of graph
    //public void doLayoutCore(LayoutGraph graph) {
    public void doLayout(LayoutGraph graph) {
	// compare node number to threshold
	if (graph.nodeCount() >= 100) {
	    // large node count: do grouping
	    
	    // step 0: ask how many groups
	    int numGroups = 0;
	    
	    // wait to get a valid answer
	    while (numGroups == 0) {
		boolean error = false;
		String input
		    = JOptionPane.showInputDialog(null,
						  "Number of subgroups [1,"
						  + graph.nodeCount() + "]");
		
		if (input != null) {
		    try {
			numGroups = Integer.parseInt(input);
		    } catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "\"" + input +
						      "\" is not an integer");
			error = true;
		    }
		} else
		    // cancelled: leave nodes untouched
		    return;

		if (!error && (numGroups<=0)||(numGroups>graph.nodeCount())) {
		    JOptionPane.showMessageDialog(null,
						  input + " not in range [1,"
						  + graph.nodeCount() + "]");
		    error = true;
		    numGroups = 0;
		}
	    }

	
	    // step 1: group nodes
	    GroupWiseNodeGrouper grouper = new GroupWiseNodeGrouper(graph);
	    Subgraph group = grouper.getNodeGrouping(numGroups);

	    // return if unable to group
	    if (group == null)
		return;

	    // step 2: lay out group
	    layoutGrouping(group);

	    // now scale it up
	    // TEMP!! find a better way to do this
	    GraphTransformer scaler = new GraphTransformer();
	    scaler.setOperation(GraphTransformer.SCALE);
	    double sf =  ((double)graph.nodeCount())
		/ ((double)(numGroups));
	    scaler.setScaleFactor(sf);
	    scaler.doLayoutCore(group);
	    
	    // step 3: insert changes into full graph
	    grouper.putNodeGrouping(group);


	    // step 4: lay out clusters
	    for (NodeCursor nc = group.nodes(); nc.ok(); nc.next()) {
		// save the current node center
		YPoint center = group.getCenter(nc.node());

		// step 4.1: get cluster from grouper
		Subgraph cluster = grouper.getClusterByNode(nc.node(),group);

		// step 4.2: layout cluster
		layoutCluster(cluster);

		// shift its position
		Rectangle rect = cluster.getBoundingBox();
		GraphTransformer.translate(cluster,
					   center.getX()-(rect.width/2),
					   center.getY()-(rect.height/2));


		// step 4.3: put cluster back into graph
		grouper.putClusterByNode(nc.node(), cluster);
	    }


	} else {
	    // small node count: pass on layouting to
	    // CircularLayouter

	    CircularLayouter mule = new CircularLayouter();
	    mule.doLayout(graph);
	}
    }



    // layoutGrouping
    //
    // layout the graph of nodes representing
    // group positions
    private void layoutGrouping (LayoutGraph graph) {
	OrganicLayouter mule = new OrganicLayouter();
	//CircularLayouter mule = new CircularLayouter();
	//mule.getSingleCycleLayouter().setMinimalRadius(240.0);
	//mule.getBalloonLayouter().setMinimalEdgeLength(120);
	mule.doLayout(graph);
    }


    private void layoutCluster(LayoutGraph cluster) {
	Node[] nodeList = cluster.getNodeArray();
	int nC = cluster.nodeCount();

	// center the clusters at 0
	for (int i = 0; i < nC; i++)
	    cluster.setCenter(nodeList[i], 0.0, 0.0);
	
	// do a circular layout on them
	CircularLayouter bob = new CircularLayouter();
	bob.doLayout(cluster);
    }
}
