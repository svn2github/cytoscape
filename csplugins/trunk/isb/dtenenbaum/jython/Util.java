package csplugins.isb.dtenenbaum.jython;

import y.algo.*;
import java.util.*;

public class Util {
	
	public static double[][] allPairs(y.base.Graph g) {
        double[][] result;
        boolean tf;
        int numEdges = g.getEdgeArray().length;
        int numNodes = g.getNodeArray().length;
        double[] cost = new double[numEdges];
        for (int i = 0; i < cost.length; i++) {
			cost[i] = 1;
		}
        result = new double[numNodes][numNodes];
        tf = ShortestPaths.allPairs(g,true,cost,result);
        //System.out.println("negative cost cycle? " + tf);
        return result;
	}
	
	public static int getShortestPathLength(String startNode, String endNode, y.base.Graph g) {
		double[][] d = allPairs(g);
		y.base.Node[] nodeArray = g.getNodeArray();
		Hashtable ht = new Hashtable();
		for (int i = 0; i < nodeArray.length; i++) {
			String nodeStr = nodeArray[i].toString();
			ht.put(nodeStr,new Integer(i));
		}
		int start = ((Integer)ht.get(startNode)).intValue();
		int end = ((Integer)ht.get(endNode)).intValue();
		double dres = d[start][end];
		int ret = (int)Math.round(dres);
		return ++ret;
	}
}