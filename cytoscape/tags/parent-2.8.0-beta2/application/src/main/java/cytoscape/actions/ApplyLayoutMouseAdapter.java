package cytoscape.actions;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class ApplyLayoutMouseAdapter extends MouseAdapter implements PropertyChangeListener {
	private JButton applyLayoutButton;
	private String layoutName;
	
	public ApplyLayoutMouseAdapter(JButton button){
		applyLayoutButton = button;
		this.layoutName = CytoscapeInit.getProperties().get("defaultLayoutAlgorithm").toString();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.PREFERENCES_UPDATED,this);
	}
	
	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.PREFERENCES_UPDATED)){
			String newLayoutName = CytoscapeInit.getProperties().get("defaultLayoutAlgorithm").toString();

			if (!newLayoutName.equalsIgnoreCase(layoutName)){
				layoutName = newLayoutName;
				applyLayoutButton.setToolTipText("Apply "+layoutName+" layout");
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		//Apply layout
		
		if ( Cytoscape.getCurrentNetworkView() == null || Cytoscape.getCurrentNetworkView() == Cytoscape.getNullNetworkView()){
			return;
		}
		if (CyLayouts.getLayout(layoutName)==null){
			// Can not find the layout algorithm specified
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Unknown layout algorithm -- "+layoutName,"Warning", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Create Task
		ApplyLayoutTask task = new ApplyLayoutTask(layoutName);

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
		applyLayoutButton.setSelected(true);
	}
	public void mouseReleased(MouseEvent e) {
		applyLayoutButton.setSelected(false);
	}
	
	//
	class ApplyLayoutTask implements Task {
		private TaskMonitor taskMonitor;
		private String layoutName;

		/**
		 * Constructor.
		 */
		public ApplyLayoutTask(String layoutAlgorithmName) {
			this.layoutName = layoutAlgorithmName;
		}

		/**
		 * Executes Task
		 */
		public void run() {
			taskMonitor.setStatus("Applying "+layoutName+" layout...");
			taskMonitor.setPercentCompleted(-1);

			CyLayoutAlgorithm fd = CyLayouts.getLayout(layoutName);
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
			return new String("Apply "+layoutName+" layout");
		}
	}

}
