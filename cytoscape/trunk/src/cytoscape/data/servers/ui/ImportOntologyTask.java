package cytoscape.data.servers.ui;

import java.io.IOException;
import java.net.URL;

import cytoscape.Cytoscape;
import static cytoscape.data.servers.OntologyServer.OntologyType.*;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


/**
 * Task to import OBO file<br>
 * 
 * @since Cytoscape 2.4
 * @version 0.8
 * @author Keiichiro Ono
 *
 */
public class ImportOntologyTask implements Task {
	
	private String dataSource;
	private String ontologyType;
	private String ontologyName;
	private String ontologyDescription;

	private TaskMonitor taskMonitor;

	/**
	 * Constructor of the task.<br>
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param dataSource URL of the data source as String.
	 * @param type Type of Ontology.
	 * @param name Name of Ontology.
	 * @param description Description for this ontology.
	 */
	public ImportOntologyTask(String dataSource, String type, String name,
			String description) {
		this.dataSource = dataSource;
		this.ontologyType = type;
		this.ontologyName = name;
		this.ontologyDescription = description;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		
		taskMonitor.setStatus("Loading Ontology Data...");

		try {
			URL targetUrl = new URL(dataSource);
			taskMonitor.setPercentCompleted(-1);
			
			if (ontologyType != null && ontologyType.equals(GO.toString())) {
				Cytoscape.getOntologyServer().addOntology(targetUrl,
						GO, ontologyName, ontologyDescription);
			} else {
				Cytoscape.getOntologyServer().addOntology(targetUrl,
						BASIC, ontologyName, ontologyDescription);
			}

			if (Cytoscape.getOntologyServer().getOntologies().get(ontologyName) != null) {
				informUserOfOntologyStats(ontologyName, targetUrl);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not load ontology from the data source: "
						+ targetUrl.toString());
				sb
						.append("\nThis URL may not contain the correct ontology data.");
				taskMonitor.setException(new IOException(sb.toString()), sb
						.toString());
			}
			taskMonitor.setPercentCompleted(100);

		} catch (Exception e) {
			taskMonitor.setException(e, "Unable to load ontology data.");
		}
	}

	/**
	 * Display result of ontology import.<br>
	 * 
	 * @param ontologyName
	 * @param source
	 */
	private void informUserOfOntologyStats(String ontologyName, URL source) {

		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Succesfully loaded ontology from:  " + source.toString());
		sb.append("\n\nThis ontology DAG is called " + ontologyName);

		taskMonitor.setStatus(sb.toString());
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
		return new String("Loading Ontology");
	}

}
