package nct.networkblast;

import nct.graph.*;
import nct.graph.basic.*;
import nct.networkblast.search.*;
import java.util.*;
import nct.networkblast.score.*;

public class TestSearch {
    public static void main(String [] args) {

        BasicGraph<WeightedNode<Integer,Double>,Double> graph = 
		new BasicGraph<WeightedNode<Integer,Double>,Double>();
        
        /*
         * Create all of the nodes in the graph
         */
        WeightedNode<Integer,Double> a = 
		new BasicWeightedNode<Integer,Double>(1,0.0);
        WeightedNode<Integer,Double> b = 
		new BasicWeightedNode<Integer,Double>(2,0.0);
        WeightedNode<Integer,Double> c = 
		new BasicWeightedNode<Integer,Double>(3,0.0);
       
        /*
         * Add the nodes to the graph
         */
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);

        /*
         * Add the edges to the graph
         */
        graph.addEdge(a,b,0.5,"a to b");
        graph.addEdge(b,c,1.0,"b to c");
        graph.addEdge(c,a,1.5,"c to a");

        SimpleScoreModel<WeightedNode<Integer,Double>> model = 
		new SimpleScoreModel<WeightedNode<Integer,Double>>();
        /*
         * Perform the greedy search and print
         * out the results
         */
	 doSearch(1,3,graph,model);
	 doSearch(2,3,graph,model);
	 doSearch(1,2,graph,model);
    }

    protected static void doSearch(
               int min, 
               int max, 
               Graph<WeightedNode<Integer,Double>,Double> graph, 
               SimpleScoreModel<WeightedNode<Integer,Double>> model) {

        NewComplexSearch<WeightedNode<Integer,Double>> greedyComplexes = 
	  new NewComplexSearch<WeightedNode<Integer,Double>>(min,max);

        List<Graph<WeightedNode<Integer,Double>,Double>> results = 
	  greedyComplexes.searchGraph(graph,model);

	for ( Graph<WeightedNode<Integer,Double>,Double> G : results ) {
        	System.out.println(G); 
        	System.out.println(); 
	}
    }
}

