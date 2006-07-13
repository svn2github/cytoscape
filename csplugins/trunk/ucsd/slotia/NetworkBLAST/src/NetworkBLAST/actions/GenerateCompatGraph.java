package NetworkBLAST.actions;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.ui.JTask;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

import nct.graph.Graph;
import nct.graph.Edge;
import nct.graph.SequenceGraph;
import nct.service.homology.HomologyModel;
import nct.networkblast.graph.HomologyGraph;
import nct.networkblast.graph.CompatibilityGraph;
import nct.networkblast.score.ScoreModel;
import nct.networkblast.graph.compatibility.CompatibilityCalculator;
import nct.networkblast.graph.compatibility.AdditiveCompatibilityCalculator;
import nct.visualization.cytoscape.CytoscapeConverter;
import nct.visualization.cytoscape.Monitor;
import nct.graph.basic.BasicDistanceGraph;

import NetworkBLAST.NetworkBLASTPlugin;
import NetworkBLAST.NetworkBLASTDialog;

import java.io.PrintWriter;

public class GenerateCompatGraph extends AbstractAction
{
  public GenerateCompatGraph(NetworkBLASTDialog _parentDialog)
  {
    super();
    this.parentDialog = _parentDialog;
  }

  public void actionPerformed(ActionEvent _e)
  {
    JTaskConfig jTaskConfig = new JTaskConfig();
    jTaskConfig.displayCancelButton(true);
    jTaskConfig.displayCloseButton(false);
    jTaskConfig.displayStatus(true);
    jTaskConfig.displayTimeElapsed(true);
    jTaskConfig.displayTimeRemaining(false);
    jTaskConfig.setAutoDispose(true);
    jTaskConfig.setModal(true);
    jTaskConfig.setOwner(Cytoscape.getDesktop());

    Task task = new Task()
    {
      private TaskMonitor monitor = null;
      private boolean needToHalt = false;
      private Monitor nctMonitor = null;

      public String getTitle()
      	{ return "NetworkBLAST: Generating Compatibility Graph..."; }
    
      public void halt()
      {
        needToHalt = true;
	if (nctMonitor != null) nctMonitor.halt();
      }

      public void setTaskMonitor(TaskMonitor monitor)
        { this.monitor = monitor; }

      public void run()
      {
        double expectation = 1e-10;
        boolean useZero = false;

	nctMonitor = new Monitor()
	{
	  public void setPercentCompleted(int percent)
	    { }
	};

	// Step 1: Convert graph 1 and graph 2 to NCT graphs. Add them to
	// the input species.
	//
	
        List<SequenceGraph<String,Double>> inputSpecies =
		new ArrayList<SequenceGraph<String,Double>>();
	
	if (needToHalt) return;
	if (monitor != null)
	{
	  monitor.setPercentCompleted(0);
	  monitor.setStatus("Converting Graph 1...");
	}

        SequenceGraph<String,Double> graph1 = new WrapperGraph<String,Double>
		(CytoscapeConverter.convert(getGraph1(), nctMonitor));
		
	if (needToHalt) return;
	if (monitor != null)
	{
	  monitor.setPercentCompleted(25);
	  monitor.setStatus("Converting Graph 2...");
	}
		
        SequenceGraph<String,Double> graph2 = new WrapperGraph<String,Double>
		(CytoscapeConverter.convert(getGraph2(), nctMonitor));
    
        inputSpecies.add(graph1);
        inputSpecies.add(graph2);

	// Step 2: Create the homology reader and graph.
	//
	
        HomologyModel homreader = new WrapperHomologyReader(getHomgraph());

        HomologyGraph homologyGraph = new HomologyGraph(homreader, expectation,
		inputSpecies);
	
	// Step 3: Create the score model and compatibility calculator;
	// these classes will support creating the compatibility graph.
	//
	
	if (monitor != null)
	  monitor.setStatus("Creating Compatibility Graph...");

        ScoreModel<String,Double> logScore = parentDialog
	  	.getCompatGraphPanel().getScoreModelComboBox()
		.getSelectedScoreModel();

	// Check if getSelectedScoreModel() was successful. If this method
	// has failed, it will display an error and return null.
	if (logScore == null) return;
    
        CompatibilityCalculator compatCalc =
	  	new AdditiveCompatibilityCalculator(0.01, logScore, useZero);

	// Step 4: Create the compatibility graph
	//

	nctMonitor = new Monitor()
	{
	  public void setPercentCompleted(int percent)
	  {
	    if (monitor != null)
	      monitor.setPercentCompleted(50 + percent * 25 / 100);
	  }
	};

        CompatibilityGraph compatGraph = new CompatibilityGraph(homologyGraph,
    		inputSpecies, compatCalc, nctMonitor);

	// Step 5: Convert the compatibility graph to a Cytoscape network
	//
	
	if (monitor != null)
	{
	  monitor.setStatus("Converting Compatibility Graph...");
	  monitor.setPercentCompleted(75);
	}
	
        String networkName = "Compatibility Graph " + (++compatGraphCount)
		+ " Result";
        CytoscapeConverter.convert(compatGraph, networkName, nctMonitor);
      }
    };

    TaskManager.executeTask(task, jTaskConfig);
  }

  private CyNetwork getGraph1()
  {
    return this.parentDialog.getCompatGraphPanel().getGraph1ComboBox()
			.getSelectedNetwork();
  }

  private CyNetwork getGraph2()
  {
    return this.parentDialog.getCompatGraphPanel().getGraph2ComboBox()
			.getSelectedNetwork();
  }

  private CyNetwork getHomgraph()
  {
    return this.parentDialog.getCompatGraphPanel().getHomgraphComboBox()
			.getSelectedNetwork();
  }

  private class WrapperGraph<NodeType extends Comparable<? super NodeType>,
  			WeightType extends Comparable<? super WeightType>>
			extends BasicDistanceGraph<NodeType,WeightType>
			implements SequenceGraph<NodeType, WeightType>
  {

    public WrapperGraph(Graph<NodeType,WeightType> g)
    	{ super(g); }

    public String getDBName()
    	{ return ""; }
    public String getDBLocation()
    	{ return ""; }
    public int getDBType()
    	{ return SequenceGraph.DUMMY; }
    public void setDBType(int type)
    	{ }
    public void setDBLocation(String loc)
    	{ }
    public void setDBName(String name)
    	{ }
  }

  private class WrapperHomologyReader implements HomologyModel
  {
    public WrapperHomologyReader(CyNetwork _network)
    	{ this.graph = CytoscapeConverter.convert(_network, null); }
	
    public Map<String,Map<String,Double>> expectationValues(
    			SequenceGraph<String,Double> sg1,
			SequenceGraph<String,Double> sg2)
    {
      Map<String,Map<String,Double>> homologyMap =
      				new HashMap<String,Map<String,Double>>();

      Set<String> nodes = this.graph.getNodes();
      for (String node : nodes)
      {
        Set<String> neighbors = this.graph.getNeighbors(node);
	for (String neighbor : neighbors)
	{
	  if ( (!sg1.isNode(node)     && !sg2.isNode(node)) ||
	       (!sg1.isNode(neighbor) && !sg2.isNode(neighbor)) ) 
            continue; 
	  
          if (!homologyMap.containsKey(node))
	    homologyMap.put(node, new HashMap<String,Double>());
	  
          if (!homologyMap.containsKey(neighbor))
	    homologyMap.put(neighbor, new HashMap<String,Double>());
	  
	  homologyMap.get(node).put(neighbor,
	  			this.graph.getEdgeWeight(node, neighbor));
	}
      }

      return homologyMap;
    }
    
    private Graph<String,Double> graph;
  }

  private NetworkBLASTDialog parentDialog;
  private static int compatGraphCount = 0;
}
