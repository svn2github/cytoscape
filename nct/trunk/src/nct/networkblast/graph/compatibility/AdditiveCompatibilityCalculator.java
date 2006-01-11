package nct.networkblast.graph.compatibility;

import java.lang.*;
import java.util.*;
import java.util.logging.Logger;

import nct.networkblast.score.ScoreModel;
import nct.graph.Graph;
import nct.graph.DistanceGraph;

public class AdditiveCompatibilityCalculator implements CompatibilityCalculator {
	

	protected double orthologyThreshold;
	protected ScoreModel scoreModel;

	public AdditiveCompatibilityCalculator( double orthologyThreshold, ScoreModel scoreModel ) {
		this.orthologyThreshold = orthologyThreshold;
		this.scoreModel = scoreModel;
	}

	
	public boolean calculate( Graph<String,Double> compatGraph, List<? extends DistanceGraph<String,Double>> partitionGraphs, String[] nodeBase, String[] nodeBranch ) {
		
		int numGraphs = partitionGraphs.size();

		// first do the distances
		byte[] distance = new byte[numGraphs];

		boolean foundOne = false;
//              boolean foundZero = false;
		for ( int z = 0; z < numGraphs; z++ ) {
			distance[z] = partitionGraphs.get(z).getDistance(nodeBase[z],nodeBranch[z]);
			if ( distance[z] == (byte)1 )
				foundOne = true;
//                      if ( distance[z] == (byte)0 )
//                      	foundZero = true;
		}

		if ( !foundOne )
			return false;
//              if ( foundZero )
//                      return false;

		// then the weights
		double edgeWeight = 0;
		for ( int z = 0; z < numGraphs; z++ )
			edgeWeight += scoreModel.scoreEdge(nodeBase[z],nodeBranch[z],partitionGraphs.get(z));
		if ( edgeWeight < orthologyThreshold )
			return false;

		String node1 = createNode( nodeBranch );
		String node2 = createNode( nodeBase );

		StringBuffer distDesc = new StringBuffer();
		for ( int z = 0; z < numGraphs; z++ )
			distDesc.append( Byte.toString(distance[z] ));

		//System.out.println( "final distance " + distDesc.toString() );
		compatGraph.addNode(node1);
		compatGraph.addNode(node2);
                boolean status = compatGraph.addEdge(node1,node2, new Double(edgeWeight), distDesc.toString());		
		return status;

	}

        private String createNode( String[] nodes ) {
                StringBuffer node1 = new StringBuffer();
                int numGraphs = nodes.length;
                for ( int z = 0; z < numGraphs-1; z++ ) {
                        node1.append(nodes[z]);
                        node1.append("|");
                }
                node1.append(nodes[numGraphs-1]);

                return node1.toString();
        }
}



