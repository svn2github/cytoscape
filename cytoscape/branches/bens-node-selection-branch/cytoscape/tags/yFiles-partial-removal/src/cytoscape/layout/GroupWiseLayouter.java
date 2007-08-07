//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// GroupWiseLayouter.java
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
    // the object used to group the subgraphs
    GroupingAlgorithm iGrouper;

    
    // GroupWiseLayouter
    //
    // save the grouping algorithm for later
    public GroupWiseLayouter (GroupingAlgorithm aGrouper) {
	iGrouper = aGrouper;
    }


    // canLayout
    //
    // can we layout the graph?
    public boolean canLayout(LayoutGraph graph) {
	return true;
    }


    // doLayout
    //
    // do the layouting of graph
    public void doLayout(LayoutGraph graph) {
	// compare node number to threshold
	if (graph.nodeCount() >= 40) {
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
	    iGrouper.useGraph(graph);
	    Subgraph group = iGrouper.getNodeGrouping(numGroups);

	    // return if unable to group
	    if (group == null)
		return;

	    // step 2: lay out group
	    // step 2.1: find average node size
	    float size = 0.0f;
	    for (NodeCursor nc = group.nodes(); nc.ok(); nc.next())
		size += (float)group.getWidth(nc.node());

	    size /= group.nodeCount();

	    // size of graph = avg node size * 2.0 apart * num nodes
	    size *= Math.sqrt(group.nodeCount())*2.0f;
	    layoutGrouping(group, size, size);

	    // step 3: insert changes into full graph
	    iGrouper.putNodeGrouping(group);


	    // step 4: lay out clusters
	    for (NodeCursor nc = group.nodes(); nc.ok(); nc.next()) {
		// save the current node center
		YPoint center = group.getCenter(nc.node());

		// TEMP!!: see the sizes of the nodes in the full graph
		//graph.setSize(group.mapSubFullNode(nc.node()),
		//	      group.getWidth(nc.node()),
		//	      group.getHeight(nc.node()));



		// step 4.1: get cluster from grouper
		Subgraph cluster = iGrouper.getClusterByNode(nc.node(),group);

		// step 4.2: layout cluster to target width and height
		layoutCluster(cluster,
			      (float)group.getWidth(nc.node()),
			      (float)group.getHeight(nc.node()));

		// shift its position
		Rectangle rect = cluster.getBoundingBox();
		GraphTransformer.translate(cluster,
					   center.getX()-(rect.width/2),
					   center.getY()-(rect.height/2));


		// step 4.3: put cluster back into graph
		iGrouper.putClusterByNode(cluster);
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
    private void layoutGrouping (LayoutGraph graph,
				 float width, float height) {
	//OrganicLayouter mule = new OrganicLayouter();
	//CircularLayouter mule = new CircularLayouter();
	//mule.getSingleCycleLayouter().setMinimalRadius(240.0);
	//mule.getBalloonLayouter().setMinimalEdgeLength(120);
	//mule.setObeyNodeSize(true);

	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();
	// center the clusters at around 0
	for (int i = 0; i < nC; i++)
	    graph.setCenter(nodeList[i],
			    10*(Math.random()-.5),
			    10*(Math.random()-.5));

	EmbeddedLayouter mule = new EmbeddedLayouter(width, height);
      	mule.doLayout(graph);
    }



    // layoutCluster
    //
    // layout the graph of nodes in a cluster
    private void layoutCluster(LayoutGraph cluster,
			       float width, float height) {
	//Node[] nodeList = cluster.getNodeArray();
	//int nC = cluster.nodeCount();

	// center the clusters at 0
	//for (int i = 0; i < nC; i++)
	//    cluster.setCenter(nodeList[i],
	//		      10*(Math.random()-.5),
	//		      10*(Math.random()-.5));
	
	// do a circular layout on them
	//CircularLayouter bob = new CircularLayouter();
	EmbeddedLayouter bob = new EmbeddedLayouter(width, height);
	bob.doLayout(cluster);
    }
}


