package cytoscape.bubbleRouter;

import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

/**
 * Import Attributes Task opens a file browser to allow the user to select and
 * load node attributes that can be used with BubbleRouter.
 * 
 */
public class ImportAttributesTask implements Task {
	private TaskMonitor taskMonitor;

	private File[] files;

	private int type;

	static final int NODE_ATTRIBUTES = 0;

	static final int EDGE_ATTRIBUTES = 1;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            File Object.
	 * @param type
	 *            NODE_ATTRIBUTES or EDGE_ATTRIBUTES
	 */
	ImportAttributesTask(File[] files, int type) {
		this.files = files;
		this.type = type;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Reading in Attributes");

			/**
			 * Read in Data // track progress. CyAttributes has separation
			 * between reading attributes and storing them so we need to
			 * find a different way of monitoring this task: 
			 * attributes.setTaskMonitor(taskMonitor);
			 */

			for (int i = 0; i < files.length; ++i) {
				taskMonitor.setPercentCompleted(100 * i / files.length);
				if (type == NODE_ATTRIBUTES)
					Cytoscape.loadAttributes(new String[] { files[i]
							.getAbsolutePath() }, new String[] {});
				else if (type == EDGE_ATTRIBUTES)
					Cytoscape.loadAttributes(new String[] {},
							new String[] { files[i].getAbsolutePath() });
				else
					throw new Exception("Unknown attribute type: "
							+ Integer.toString(type));
			}

			/**
			 * Inform others via property change event
			 */
			taskMonitor.setPercentCompleted(100);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null,
					null);
			taskMonitor.setStatus("Done");
		} catch (Exception e) {
			taskMonitor.setException(e, e.getMessage());
		}
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
	}

	/**
	 * Sets the Task Monitor Object.
	 * 
	 * @param taskMonitor
	 * @throws IllegalThreadStateException
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		if (type == NODE_ATTRIBUTES) {
			return new String("Loading Node Attributes");
		} else {
			return new String("Loading Edge Attributes");
		}
	}
}
