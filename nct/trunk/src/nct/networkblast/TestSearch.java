package nct.networkblast;

import nct.graph.*;
import nct.graph.basic.*;
import nct.networkblast.search.*;
import java.util.*;
import nct.networkblast.score.*;

public class TestSearch{
    public static void main(String [] args){
        BasicGraph<BasicWeightedNode<Integer,Double>,Double> graph = 
			new BasicGraph<BasicWeightedNode<Integer,Double>,Double>();
        //System.out.println(graph);
        
        /*
         * Create all of the nodes in the graph
         */
        BasicWeightedNode<Integer,Double> a = new BasicWeightedNode<Integer,Double>(1,0.0);
        BasicWeightedNode<Integer,Double> b = new BasicWeightedNode<Integer,Double>(2,0.0);
        BasicWeightedNode<Integer,Double> c = new BasicWeightedNode<Integer,Double>(3,0.0);
       
        /*
         * Add the nodes to the graph
         */
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        //System.out.println(graph);

        /*
         * Add the edges to the graph
         */
        graph.addEdge(a,b,0.5,"a to b");
        graph.addEdge(b,c,1.0,"b to c");
        graph.addEdge(c,a,1.5,"c to a");
        System.out.println(graph);

        SimpleScoreModel<BasicWeightedNode<Integer,Double>> model = new SimpleScoreModel<BasicWeightedNode<Integer,Double>>();
        /*
         * Perform the greedy search and print
         * out the results
         */
        NewComplexSearch<BasicWeightedNode<Integer,Double>> greedyComplexes = new NewComplexSearch<BasicWeightedNode<Integer,Double>>(1,3);
        List<Graph<BasicWeightedNode<Integer,Double>,Double>> results = greedyComplexes.searchGraph(graph,model);
        System.out.println("Printing out results");
        System.out.println(results); 
    }
}

