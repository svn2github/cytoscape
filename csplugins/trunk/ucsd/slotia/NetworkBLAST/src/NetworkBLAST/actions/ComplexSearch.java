package NetworkBLAST.actions;

import java.util.List;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.ui.JTask;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

import nct.graph.Graph;
import nct.visualization.cytoscape.CytoscapeConverter;
import nct.visualization.cytoscape.Monitor;
import nct.filter.Filter;
import nct.filter.SortFilter;
import nct.filter.DuplicateThresholdFilter;
import nct.networkblast.filter.UniqueCompatNodeFilter;
import nct.networkblast.search.SearchGraph;
import nct.networkblast.search.ColorCodingPathSearch;
import nct.networkblast.search.NewComplexSearch;
import nct.networkblast.score.ScoreModel;
import nct.networkblast.score.SimpleEdgeScoreModel;

import NetworkBLAST.NetworkBLASTDialog;

public class ComplexSearch extends AbstractAction
{
  public ComplexSearch(NetworkBLASTDialog parentDialog)
  {
    super();
    this.parentDialog = parentDialog;
  }

  public void actionPerformed(ActionEvent e)
  {
    final int maxSize, seedSize, maxResults, pathSize = 4;
    final double dupeThreshold = 1.0;
    final boolean limitResults = parentDialog.getComplexSearchPanel()
    		.getLimitCheckBox().isSelected();
    
    try
    {
      JTextField maxSizeTextField = parentDialog.getComplexSearchPanel()
      			.getMaxSizeTextField();
			
      JTextField seedSizeTextField = parentDialog.getComplexSearchPanel()
      			.getSeedSizeTextField();
			
      JTextField limitTextField = parentDialog.getComplexSearchPanel()
      			.getLimitTextField();
			
      maxSize = Integer.parseInt(maxSizeTextField.getText());
      seedSize = Integer.parseInt(seedSizeTextField.getText());
      maxResults = Integer.parseInt(limitTextField.getText());
    }
    catch (NumberFormatException exp)
    {
      JOptionPane.showMessageDialog(null,
      	"The parameters for Path Search are not specified\ncorrectly. " +
	"Please go back to the Path Search dialog and\nmake sure each " +
	"text field has been entered correctly.",
	"NetworkBLAST: Complex Search", JOptionPane.ERROR_MESSAGE);
      return;
    }

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
        { return "NetworkBLAST: Performing Complex Search..."; }
	
      public void halt()
      {
        needToHalt = true;
	if (nctMonitor != null) nctMonitor.halt();
      }
      
      public void setTaskMonitor(TaskMonitor monitor)
        { this.monitor = monitor; }
      
      public void run()
      {
	nctMonitor = new Monitor()
	{
	  public void setPercentCompleted(int percent)
	    { }
	};
	
	// Step 1: Convert the compatibility graph
	//
	
        if (needToHalt) return;
	if (monitor != null)
	{
	  monitor.setPercentCompleted(0);
	  monitor.setStatus("Converting graph...");
	}
	
        Graph<String,Double> graph = CytoscapeConverter.convert(getGraph(),
		nctMonitor);

	// Step 2: Create seeds
	//
	
        if (needToHalt) return;
	if (monitor != null)
	  monitor.setStatus("Creating seeds...");

	NewComplexSearch<String> greedyComplexes =
		new NewComplexSearch<String>(seedSize, maxSize);
				
        ColorCodingPathSearch<String> colorCoding = null;
	if (limitResults)
          colorCoding = new ColorCodingPathSearch<String>(pathSize, maxResults);
	else
          colorCoding = new ColorCodingPathSearch<String>(pathSize);
	  
	nctMonitor = new Monitor()
	{
	  public void setPercentCompleted(int percent)
	  {
	    if (monitor != null)
	      monitor.setPercentCompleted(25 + percent * 25 / 100);
	  }
	};
	colorCoding.setMonitor(nctMonitor);
	
    	ScoreModel<String,Double> scoreModel =
		new SimpleEdgeScoreModel<String>();
	
	List<Graph<String,Double>> resultPaths = colorCoding.searchGraph(
		graph, scoreModel);
	
        if (needToHalt) return;
	
	greedyComplexes.setSeeds(resultPaths);
	
	// Step 3: Perform complex search
	//

	if (monitor != null)
	{
	  monitor.setPercentCompleted(50);
	  monitor.setStatus("Performing complex search...");
	}
	
	List<Graph<String,Double>> resultComplexes =
		greedyComplexes.searchGraph(graph, scoreModel);

	// Step 4: Filter results
	//
	
        if (needToHalt) return;
        Filter<String,Double> dupeFilter = new DuplicateThresholdFilter
		<String,Double>(dupeThreshold);
        Filter<String,Double> dupeNodeFilter = new UniqueCompatNodeFilter();
        Filter<String,Double> sortFilter = new SortFilter<String,Double>(true);
	
        resultComplexes = dupeFilter.filter(resultComplexes);
        resultComplexes = dupeNodeFilter.filter(resultComplexes);
        resultComplexes = sortFilter.filter(resultComplexes);

	// Step 5: Convert results to Cytoscape network
	//

	complexSearchCount++;

        for (int i = 0; i < resultComplexes.size(); i++)
        {
	  if (needToHalt) return;
	  if (monitor != null)
	  {
	    monitor.setPercentCompleted(75 + (i + 1) * 25
	    	/ resultComplexes.size());
	    monitor.setStatus("Converting results... " + (i + 1)
	    	+ " of " + resultComplexes.size());
	  }
	  
	  CytoscapeConverter.convert( resultComplexes.get(i),
	  	"Complex Search " + complexSearchCount + ": Result " + (i + 1)
		+ " of " + resultComplexes.size(), nctMonitor );
        }
      }
    };

    TaskManager.executeTask(task, jTaskConfig);
  }
  
  private CyNetwork getGraph()
  {
    return this.parentDialog.getComplexSearchPanel().getGraphComboBox()
    			.getSelectedNetwork();
  }

  private NetworkBLASTDialog parentDialog;
  private static int complexSearchCount = 0;
}
