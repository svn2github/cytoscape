package cytoscape.tutorial22;

import java.awt.event.ActionEvent;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;

/**
 * 
 */
public class Tutorial22 extends CytoscapePlugin {

	/**
	 * 
	 */
	public Tutorial22() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial22 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial22");
			setPreferredMenu("Plugins");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			int[] myInts = {10,20,30,40,50,60,70,80,90};
			
			MyTask task = new MyTask(myInts);
			
			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);

			jTaskConfig.displayCancelButton(true);

			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(true);

			// Execute Task in New Thread; pops open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		}
	}	
	
	public class MyTask implements Task {
		private cytoscape.task.TaskMonitor taskMonitor;

		private int[] myInts;
		public MyTask(int[] pInts) {
			this.myInts = pInts;
		}

		public void setTaskMonitor(TaskMonitor monitor)
				throws IllegalThreadStateException {
			taskMonitor = monitor;
		}

		public void halt() {
			// not implemented
		}

		public String getTitle() {
			return "My Title";
		}

		public void run() {
			taskMonitor.setStatus("Test only...");
			taskMonitor.setPercentCompleted(-1);
			
			try {
				for (int i=0; i< myInts.length; i++) {
					taskMonitor.setPercentCompleted(myInts[i]);
					Thread.sleep(1000);					
				}
			}
			catch (InterruptedException ex) {
				
			}
			taskMonitor.setPercentCompleted(100);
		}
	}
}
