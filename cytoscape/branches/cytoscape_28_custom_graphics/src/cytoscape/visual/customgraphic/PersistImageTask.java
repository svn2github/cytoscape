package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class PersistImageTask implements Task {
	private File location;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.<br>
	 * 
	 * @param fileName
	 *            Absolute path to the Session file.
	 */
	protected PersistImageTask(File location) {
		this.location = location;
		// Create session writer object
	}

	/**
	 * Execute task.<br>
	 */
	public void run() {
		taskMonitor.setStatus("Saving image library to your local disk.\n\nPlease wait...");
		taskMonitor.setPercentCompleted(-1);
		// Remove all existing files
		final File[] files = location.listFiles();
		for (File old : files)
			old.delete();
		
		System.out.println("Old files deleted");

		final long startTime = System.currentTimeMillis();
		CustomGraphicsPool pool = Cytoscape.getVisualMappingManager()
				.getCustomGraphicsPool();

		final ExecutorService exService = Executors.newCachedThreadPool();

		for (CyCustomGraphics<?> cg : pool.getAll()) {
			if (cg == pool.getNullGraphics()
					|| cg instanceof URLImageCustomGraphics == false)
				continue;

			final Image img = cg.getImage();
			if (img != null) {
				final int hash = cg.hashCode();
				String newFileName = Integer.toString(hash);
				try {
					exService.submit(new SaveImageTask(location, newFileName,
							ImageUtil.toBufferedImage(img)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				;
			}
		}

		try {
			exService.shutdown();
			exService.awaitTermination(1000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		double sec = (endTime - startTime) / (1000.0);
		System.out.println("Image Saving Finished in " + sec + " sec.");

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
		return "Saving images to disk...";
	}
} // End of SaveSessionTask
