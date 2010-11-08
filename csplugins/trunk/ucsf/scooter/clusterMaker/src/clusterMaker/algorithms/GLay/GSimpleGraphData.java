/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.glay;

import java.util.*;
import cytoscape.CyNetwork;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.function.*;
import giny.model.Node;
import giny.model.Edge;
/**
 *
 * @author Gang Su
 */
public class GSimpleGraphData {

    /*No encapisulation, just make the coding simpler*/
    public int nodeCount;
    public int edgeCount;
    public CyNetwork network;
    public int[] graphIndices;
    public int[] degree;
    public SparseDoubleMatrix2D edgeMatrix;
		private boolean selectedOnly;
		private boolean undirectedEdges;
		private List<Node> nodeList;
		private List<Edge> connectingEdges;

    public GSimpleGraphData(CyNetwork network, boolean selectedOnly, boolean undirectedEdges){
        this.network = network;
				this.selectedOnly = selectedOnly;
				this.undirectedEdges = undirectedEdges;
				if (!selectedOnly) {
					this.nodeList = (List<Node>)network.nodesList();
				} else {
					this.nodeList = new ArrayList<Node>(network.getSelectedNodes());
				}

				this.nodeCount = nodeList.size();
				this.connectingEdges = (List<Edge>)network.getConnectingEdges(nodeList);
				this.edgeCount = this.connectingEdges.size();
        this.graphIndices = new int[this.nodeCount];
        this.degree = new int[this.nodeCount];
        this.edgeMatrix = new SparseDoubleMatrix2D(nodeCount, nodeCount);
        this.simplify();
    }
    
    private void simplify(){

        //Assign index and degree
        for(int i=0; i<nodeList.size(); i++){
            this.graphIndices[i] = nodeList.get(i).getRootGraphIndex();
            this.degree[i] = network.getDegree(this.graphIndices[i]);
        }

        //Assign edge
        int ijEdge = 0; //Edge from i to j
        int jiEdge = 0; //Edge from j to i
        int ijUEdge = 0; //Undirectional edges
        int totalEdge = 0;
        for(int i=0; i<graphIndices.length-1; i++){
            for(int j=i+1; j<graphIndices.length; j++){
                //Count the number of edges
                if (undirectedEdges) {
                	jiEdge = network.getEdgeCount(graphIndices[j], graphIndices[i], true); //Count un-directional
                	ijEdge = network.getEdgeCount(graphIndices[i], graphIndices[j], true); //Count un-directional
								} else {
                	ijEdge = network.getEdgeCount(graphIndices[i], graphIndices[j], false); //Doesn't count un-directional
                	jiEdge = network.getEdgeCount(graphIndices[j], graphIndices[i], true); //Count un-directional
								}
                //ijUEdge = network.getEdgeCount(graphIndices[i], graphIndices[j], true);
                totalEdge = ijEdge + jiEdge;
                this.edgeMatrix.setQuick(i, j, totalEdge); //conversion from int to double is ok.

                //fix degree and edge count.
                if(totalEdge > 1){
                    edgeCount = edgeCount-totalEdge+1;
                    degree[i] = degree[i]-totalEdge+1;
                    degree[j] = degree[j]-totalEdge+1;
                }
            }
        }

        /*
         * If simplification is succesful, the toal degree should be twice of edge count
         * */
        //this.simplificationCheck();
        

    }

    public boolean hasEdge(int i, int j){
        /*Note i and j must 0< i,j < nodeCount - 1*/
        if(i==j)return false;
        if(i > j){
            int temp = i;
            i = j;
            j = temp;
        }

        if(edgeMatrix.getQuick(i, j) != 0){
            return true;
        }
        return false;
    }

    public void simplificationCheck(){
        System.out.println("---------------");
        System.out.println("Simplify check:");
        System.out.println("NodeCount:" + this.nodeCount);
        System.out.println("EdgeCount: O:" + network.getEdgeCount());
        System.out.println("EdgeCount: S:" + this.edgeCount);
        int totalDegree = 0;
        int totalDegreeBefore = 0;
        for(int i=0; i< this.graphIndices.length;i++){
            totalDegree += degree[i];
            totalDegreeBefore += network.getDegree(graphIndices[i]);
        }
        System.out.println("TotalDegree O:" + totalDegreeBefore);
        System.out.println("TotalDegree S:" + totalDegree);
    }

}
