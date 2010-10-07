package cytoscape.actions;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import cytoscape.Cytoscape;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;

import javax.swing.JButton;

public class ApplyForcedDirectedLayoutMouseAdapter extends MouseAdapter {
	private JButton forceDirectLayoutButton;
	public ApplyForcedDirectedLayoutMouseAdapter(JButton button){
		forceDirectLayoutButton = button;
	}
	
	public void mouseClicked(MouseEvent e) {
		//Apply force-directed layout
		
		// Create Task
		ApplyForceDirectedLayoutTask task = new ApplyForceDirectedLayoutTask();

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}

	public void mousePressed(MouseEvent e) {
		forceDirectLayoutButton.setSelected(true);
	}
	public void mouseReleased(MouseEvent e) {
		forceDirectLayoutButton.setSelected(false);
	}
	
	class ApplyForceDirectedLayoutTask implements Task {
		private TaskMonitor taskMonitor;

		/**
		 * Constructor.
		 */
		public ApplyForceDirectedLayoutTask() {
		}

		/**
		 * Executes Task
		 */
		public void run() {
			taskMonitor.setStatus("Applying force-directed layout...");
			taskMonitor.setPercentCompleted(-1);

			CyLayoutAlgorithm fd = CyLayouts.getLayout("force-directed");
			fd.setSelectedOnly(false);
			fd.getSettings().updateValues();
			fd.updateSettings();					
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			if (view != null){
				view.applyLayout(fd);
				view.redrawGraph(true, true);						
			}

			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Done!");
		}

		/**
		 * Halts the Task: Not Currently Implemented.
		 */
		public void halt() {
			// Task can not currently be halted.
		}

		/**
		 * Sets the Task Monitor.
		 *
		 * @param taskMonitor
		 *            TaskMonitor Object.
		 */
		public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
			this.taskMonitor = taskMonitor;
		}

		/**
		 * Gets the Task Title.
		 *
		 * @return Task Title.
		 */
		public String getTitle() {
			return new String("Apply force-directed layout");
		}
	}

}
