package cytoscape.coreplugins.biopax.ui;

import org.biopax.paxtools.model.Model;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.BioPaxGraphReader;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CyNetworkNaming;

/**
 * example task
 * 
 * @author rodche
 *
 */
class FooTask implements Task {
    private TaskMonitor taskMonitor;
    private CyNetwork network1;

    public FooTask(CyNetwork network1) {
        this.network1 = network1;
    }

    public void run() {
        taskMonitor.setStatus("Mocking Task for a BioPAX network...");
        Model newModel = BioPaxUtil.getNetworkModel(network1);
        CyNetwork cyNetwork = Cytoscape.createNetwork(new BioPaxGraphReader(newModel), true, null);
        cyNetwork.setTitle(CyNetworkNaming.getSuggestedNetworkTitle("(Integrated) " + cyNetwork.getTitle()));

        // TODO perform task...
        taskMonitor.setPercentCompleted(100);
	    taskMonitor.setStatus("Successful.");
    }

    public void halt() {
        // No halt support
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Mocking BioPAX network task";
    }

}