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
import nct.graph.basic.BasicDistanceGraph;

import NetworkBLAST.NetworkBLASTPlugin;
import NetworkBLAST.NetworkBLASTDialog;

/*
 * The action that generates compatibility graphs.
 * This action is invoked by the Generate button of CompatGraphPanel.
 */

public class GenerateCompatGraph extends AbstractAction
{
  public GenerateCompatGraph(NetworkBLASTDialog _parentDialog)
  {
    super();
    this.parentDialog = _parentDialog;
  }

  public void actionPerformed(ActionEvent _e)
  {
    // Create the JTaskConfig. This specifies how the progress dialog will
    // be displayed.
    
    JTaskConfig jTaskConfig = new JTaskConfig();
    jTaskConfig.displayCancelButton(true);
    jTaskConfig.displayCloseButton(false);
    jTaskConfig.displayStatus(true);
    jTaskConfig.displayTimeElapsed(true);
    jTaskConfig.displayTimeRemaining(false);
    jTaskConfig.setAutoDispose(true);
    jTaskConfig.setModal(true);
    jTaskConfig.setOwner(Cytoscape.getDesktop());

    // Create the Task. This is where the compatibility graph will be generated.

    Task task = new Task()
    {
      public void run()
      {
        double expectation = 1e-10;
        boolean useZero = false;

	/*******************************
	 *
	 * Create the input species
	 * inputSpecies will contain graph 1 and graph 2
	 *
	 *******************************/
	
        List<SequenceGraph<String,Double>> inputSpecies =
		new ArrayList<SequenceGraph<String,Double>>();
	
	if (monitor != null)
	{
	  monitor.setPercentCompleted(0);
	  monitor.setStatus("Converting Graph 1...");
	}

        SequenceGraph<String,Double> graph1 = new WrapperGraph<String,Double>
		(CytoscapeConverter.convert(getGraph1()));
		
	if (monitor != null)
	{
	  monitor.setPercentCompleted(20);
	  monitor.setStatus("Converting Graph 2...");
	}
		
        SequenceGraph<String,Double> graph2 = new WrapperGraph<String,Double>
		(CytoscapeConverter.convert(getGraph2()));
    
        inputSpecies.add(graph1);
        inputSpecies.add(graph2);

	/*******************************
	 *
	 * Create the homology graph.
	 * First the homology reader will be created,
	 * then the homology graph.
	 *
	 *******************************/
	
	if (monitor != null)
	{
	  monitor.setPercentCompleted(40);
	  monitor.setStatus("Creating Homology Graph...");
	}

        HomologyModel homreader = new WrapperHomologyReader(getHomgraph());
	

        HomologyGraph homologyGraph = new HomologyGraph(
	  	homreader, expectation, inputSpecies);
		
	if (monitor != null)
	{
	  monitor.setPercentCompleted(60);
	  monitor.setStatus("Creating Compatibility Graph...");
	}

	/*******************************
	 *
	 * Create the ScoreModel and CompatibilityCalculator
	 * classes for the CompatibilityGraph.
	 *
	 *******************************/

        ScoreModel<String,Double> logScore = parentDialog.
	  	getCompatGraphPanel().getScoreModelComboBox().
			getSelectedScoreModel();
    
        CompatibilityCalculator compatCalc =
	  	new AdditiveCompatibilityCalculator(0.01, logScore, useZero);

	/*******************************
	 *
	 * Create the CompatibilityGraph.
	 *
	 *******************************/


        CompatibilityGraph compatGraph = new CompatibilityGraph(homologyGraph,
    		inputSpecies, compatCalc);
	
	/*******************************
	 *
	 * Create the Cytoscape network.
	 *
	 *******************************/
		
	if (monitor != null)
	{
	  monitor.setPercentCompleted(80);
	  monitor.setStatus("Converting Compatibility Graph...");
	}
	
	// networkName holds the name of the new Cytoscape network.
	// If it is the first compatibility graph, networkName will be
	// "Untitled Compatibility Graph." Subsequent created graphs
	// are called "Untitled Compatibility Graph 2,"
	// "Untitled Compatibility Graph 3," etc.
	
        String networkName = "Untitled Compatibility Graph";
	
        compatGraphCount++;
        if (compatGraphCount > 1)
          networkName += " " + compatGraphCount;
      
        CyNetwork newNetwork = Cytoscape.createNetwork(networkName, true);
        newNetwork.appendNetwork(CytoscapeConverter.convert(compatGraph));

	// Close the NetworkBLAST dialog

        parentDialog.setVisible(false);
      }
      
      public String getTitle()
      	{ return "NetworkBLAST: Generating Compatibility Graph..."; }
    
      public void halt() { }
      public void setTaskMonitor(TaskMonitor _monitor)
      	{ monitor = _monitor; }

      private TaskMonitor monitor = null;
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
    	{ this.graph = CytoscapeConverter.convert(_network); }
	
    public Map<String,Map<String,Double>> expectationValues(
    			SequenceGraph<String,Double> sg1,
			SequenceGraph<String,Double> sg2)
    {
      Map<String,Map<String,Double>> homologyMap =
      				new HashMap<String,Map<String,Double>>();

      Set<String> nodes = this.graph.getNodes();
      for (String node : nodes)
      {
        Set<String> connectedNodes = this.graph.getNeighbors(node);
	for (String connectedNode : connectedNodes)
	{
	  if ( (!sg1.isNode(node)          && !sg2.isNode(node)) ||
	       (!sg1.isNode(connectedNode) && !sg2.isNode(connectedNode)) ) 
            continue; 
	  
          if (!homologyMap.containsKey(node))
	    homologyMap.put(node, new HashMap<String,Double>());
	  
          if (!homologyMap.containsKey(connectedNode))
	    homologyMap.put(connectedNode, new HashMap<String,Double>());
	  
	  homologyMap.get(node).put(connectedNode,
	  			this.graph.getEdgeWeight(node, connectedNode));
	}
      }

      return homologyMap;
    }
    
    private Graph<String,Double> graph;
  }

  private NetworkBLASTDialog parentDialog;
  private static int compatGraphCount = 0;
}
