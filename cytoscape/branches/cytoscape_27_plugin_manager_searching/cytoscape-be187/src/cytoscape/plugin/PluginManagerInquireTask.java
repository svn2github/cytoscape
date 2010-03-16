/**
 * 
 */
package cytoscape.plugin;

import java.util.List;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

/**
 * @author skillcoy
 *
 */
public class PluginManagerInquireTask implements Task {


	private String url;

	private PluginInquireAction actionObj;

	private cytoscape.task.TaskMonitor taskMonitor;

	public PluginManagerInquireTask(String Url, PluginInquireAction Obj) {
		url = Url;
		actionObj = Obj;
	}

	public void setTaskMonitor(TaskMonitor monitor)
			throws IllegalThreadStateException {
		taskMonitor = monitor;
	}

	public void halt() {
		// not implemented
	}

	public String getTitle() {
		return "Attempting to connect to " + url;
	}

	public void run() {
		List<DownloadableInfo> Results = null;

		taskMonitor.setStatus(actionObj.getProgressBarMessage());
		taskMonitor.setPercentCompleted(-1);

		try {
			Results = PluginManager.getPluginManager().inquire(url);
		} catch (Exception e) {

			if (e.getClass().equals(java.lang.NullPointerException.class)) {
				e = new org.jdom.JDOMException(
						"XML was incorrectly formed", e);
			}
			actionObj.setExceptionThrown(e);
		} finally {
			taskMonitor.setPercentCompleted(100);
			actionObj.inquireAction(Results);
		}
	}

}
