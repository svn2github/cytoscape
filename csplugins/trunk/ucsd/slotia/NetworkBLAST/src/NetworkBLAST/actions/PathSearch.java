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
import nct.filter.Filter;
import nct.filter.SortFilter;
import nct.filter.DuplicateThresholdFilter;
import nct.filter.DuplicateThresholdFilter;
import nct.networkblast.filter.UniqueCompatNodeFilter;
import nct.networkblast.search.SearchGraph;
import nct.networkblast.search.ColorCodingPathSearch;
import nct.networkblast.score.ScoreModel;
import nct.networkblast.score.SimpleEdgeScoreModel;

import NetworkBLAST.NetworkBLASTDialog;

public class PathSearch extends AbstractAction
{
  public PathSearch(NetworkBLASTDialog _parentDialog)
  {
    super();
    parentDialog = _parentDialog;
  }

  public void actionPerformed(ActionEvent _e)
  {
    final int pathSize, numPaths, maxResults;
    final double dupeThreshold = 1.0;
    final boolean limitResults = parentDialog.getPathSearchPanel()
    		.getLimitCheckBox().isSelected();
    
    try
    {
      JTextField pathSizeTextField = parentDialog.getPathSearchPanel()
      				.getPathSizeTextField();
				
      JTextField numPathsTextField = parentDialog.getPathSearchPanel()
      				.getNumPathsTextField();

      JTextField limitTextField = parentDialog.getPathSearchPanel()
      				.getLimitTextField();
				
      pathSize = Integer.parseInt(pathSizeTextField.getText());
      numPaths = Integer.parseInt(numPathsTextField.getText());
      maxResults = Integer.parseInt(limitTextField.getText());
    }
    catch (NumberFormatException _exp)
    {
      JOptionPane.showMessageDialog(null,
      	"The parameters for Path Search are not specified\ncorrectly. " +
	"Please go back to the Path Search dialog and\nmake sure each " +
	"text field has been entered correctly.", "NetworkBLAST: Path Search",
	JOptionPane.ERROR_MESSAGE);
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
      public void run()
      {
        //
	// Step 1: Convert the compatibility graph
	//

	if (needToHalt) return;
	if (monitor != null)
	{
	  monitor.setPercentCompleted(0);
	  monitor.setStatus("Converting graph...");
	}
	
        Graph<String,Double> graph = CytoscapeConverter.convert(getGraph());

	//
	// Step 2: Perform path search
	//

	if (needToHalt) return;
        if (monitor != null)
	{
	  monitor.setPercentCompleted(33);
	  monitor.setStatus("Performing path search...");
	}
	
        SearchGraph<String,Double> colorCoding = null;
	if (limitResults)
          colorCoding = new ColorCodingPathSearch<String>(pathSize, maxResults);
	else
          colorCoding = new ColorCodingPathSearch<String>(pathSize);

    	ScoreModel<String,Double> scoreModel =
		new SimpleEdgeScoreModel<String>();

        List<Graph<String,Double>> resultPaths = colorCoding.searchGraph(
    		graph, scoreModel);

        //
	// Step 3: Filter results
	//
	
        Filter<String,Double> dupeFilter = new DuplicateThresholdFilter
		<String,Double>(dupeThreshold);
        Filter<String,Double> dupeNodeFilter = new UniqueCompatNodeFilter();
        Filter<String,Double> sortFilter = new SortFilter<String,Double>(true);
	
        resultPaths = dupeFilter.filter(resultPaths);
        resultPaths = dupeNodeFilter.filter(resultPaths);
        resultPaths = sortFilter.filter(resultPaths);

	//
	// Step 4: Convert results to Cytoscape network
	//

	pathSearchCount++;

        for (int i = 0; i < resultPaths.size(); i++)
        {
	  if (needToHalt) return;
	  if (monitor != null)
	  {
	    monitor.setPercentCompleted(66 + (i + 1) * 33 / resultPaths.size());
	    monitor.setStatus("Converting results... " + (i + 1) +
	    	" of " + resultPaths.size());
	  }
          CytoscapeConverter.convert( resultPaths.get(i),
	  	"Path Search " + pathSearchCount + " Result " + (i + 1));
        }
      }

      public String getTitle()
        { return "NetworkBLAST: Performing Path Search..."; }

      public void halt()
        { needToHalt = true; }
	
      public void setTaskMonitor(TaskMonitor _monitor)
        { monitor = _monitor; }
      
      private TaskMonitor monitor = null;
      private boolean needToHalt = false;
    };

    TaskManager.executeTask(task, jTaskConfig);

    parentDialog.setVisible(false);
  }
  
  private CyNetwork getGraph()
  {
    return this.parentDialog.getPathSearchPanel().getGraphComboBox()
    			.getSelectedNetwork();
  }

  private NetworkBLASTDialog parentDialog;
  private static int pathSearchCount = 0;
}
