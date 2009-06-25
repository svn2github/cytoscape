
package cytoscape.xtask;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.model.CyNetwork;

public interface CreateNetworkPresentationTaskFactory extends TaskFactory {

	Task getCreateNetworkPresentationTask(CyNetwork net); 
}
