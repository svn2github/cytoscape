
package cytoscape.xtask;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.model.CyNetwork;

public interface CreateNetworkViewTaskFactory extends TaskFactory {

	Task getCreateNetworkViewTask(CyNetwork net); 
}
