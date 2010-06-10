/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package edu.ucsd.bioeng.coreplugin.tableImport.ui;

import static cytoscape.data.servers.OntologyServer.OntologyType.BASIC;
import static cytoscape.data.servers.OntologyServer.OntologyType.GO;

import java.io.IOException;
import java.net.URL;

import cytoscape.Cytoscape;
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
	 * @param dataSource
	 *            URL of the data source as String.
	 * @param type
	 *            Type of Ontology.
	 * @param name
	 *            Name of Ontology.
	 * @param description
	 *            Description for this ontology.
	 */
	public ImportOntologyTask(String dataSource, String type, String name, String description) {
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

			if ((ontologyType != null) && ontologyType.equals(GO.toString())) {
				Cytoscape.getOntologyServer()
				         .addOntology(targetUrl, GO, ontologyName, ontologyDescription);
			} else {
				Cytoscape.getOntologyServer()
				         .addOntology(targetUrl, BASIC, ontologyName, ontologyDescription);
			}

			if (Cytoscape.getOntologyServer().getOntologies().get(ontologyName) != null) {
				informUserOfOntologyStats(ontologyName, targetUrl);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not load ontology from the data source: " + targetUrl.toString());
				sb.append("\nThis URL may not contain the correct ontology data.");
				taskMonitor.setException(new IOException(sb.toString()), sb.toString());
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
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
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
