// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GodFather.java

import csplugins.jActiveModules.ActivePathsFinder;
import csplugins.jActiveModules.Component;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.data.CyNetwork;
import cytoscape.data.CyNetworkFactory;
import giny.model.*;
import java.util.*;

public class GodFather
{

  public static void main(String bob[])
  {
    int size = 15;
    double signal_ratio = 5D;
    double percent = 1.0D;
    String sif_file = "temp.sif";
    String attr_names[] = {
      "placeholder"
    };
    CyNetwork network = CyNetworkFactory.createNetworkFromInteractionsFile(sif_file);
    ActivePathFinderParameters apfParams = new ActivePathFinderParameters();
    apfParams.setRegionalBoolean(false);
    apfParams.setMCboolean(false);
    apfParams.setNumberOfPaths(1);
    apfParams.setSearchDepth(2);
    apfParams.setMaxDepth(3);
				apfParams.setMaxThreads(2); 
				double selectivity = 0.0D;
    double sensitivity = 0.0D;
    int iterations = 1;
    for(int iteration = 0; iteration < iterations; iteration++)
      {
	System.err.println(""+iteration);
	Set module = getModule(network.getRootGraph(), size);
	HashMap expressionMap = createExpressionMap(network, module, signal_ratio, percent);
	ActivePathsFinder apf = new ActivePathsFinder(expressionMap, attr_names, network, apfParams, null);
	double result[] = compare(apf.findActivePaths()[0], module);
	selectivity += result[0];
	sensitivity += result[1];
      }
    System.out.println(""+selectivity/(double)iterations);
    System.out.println(""+sensitivity/(double)iterations);

  }

  protected static double[] compare(Component foundModule, Set definedModule)
  {
    int foundSize = foundModule.getNodes().size();
    int definedSize = definedModule.size();
    Iterator nodeIterator = definedModule.iterator();
    int overlap = 0;
    while(nodeIterator.hasNext()) 
      if(foundModule.contains((Node)nodeIterator.next()))
	overlap++;
    double result[] = new double[2];
    result[0] = (double)overlap / (double)foundSize;
    result[1] = (double)overlap / (double)definedSize;
    return result;
  }

  protected static Set getModule(RootGraph graph, int size)
  {
    HashSet result = new HashSet();
    List nodes = graph.nodesList();
    Random rand = new Random();
    Node seed = (Node)nodes.get(rand.nextInt(nodes.size()));
    result.add(seed);
    List neighbors = new LinkedList();
    neighbors.addAll(graph.neighborsList(seed));
    while(result.size() < size) 
      {
	Node nextNeighbor = (Node)neighbors.remove(rand.nextInt(neighbors.size()));
	if(result.add(nextNeighbor))
	  neighbors.addAll(graph.neighborsList(nextNeighbor));
      }
    return result;
  }

  protected static HashMap createExpressionMap(CyNetwork network, Set module, double signal_ratio, double percent)
  {
    Random rand = new Random();
    HashMap expressionMap = new HashMap();
    for(Iterator nodeIt = network.getGraphPerspective().nodesIterator(); nodeIt.hasNext();)
      {
	Node current = (Node)nodeIt.next();
	if(rand.nextDouble() < percent)
	  {
	    if(module.contains(current))
	      expressionMap.put(current, new double[] {
		signal_ratio - rand.nextGaussian()
	      });
	    else
	      expressionMap.put(current, new double[] {
		rand.nextGaussian()
	      });
	  } else
            {
	      expressionMap.put(current, new double[] {
		0.0D
	      });
            }
      }

    return expressionMap;
  }
}
