/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.coreplugin.psi_mi.plugin;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapFromCytoscape;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapInteractionsToPsiOne;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapInteractionsToPsiTwoFive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


/**
 * Task to Export to PSI-MI.
 *
 * @author Ethan Cerami
 */
public class SaveAsPsiTask implements Task {
	/**
	 * Export to PSI-MI Level 1.
	 */
	public static final int EXPORT_PSI_MI_LEVEL_1 = 0;

	/**
	 * Export to PSI-MI Level 2.5.
	 */
	public static final int EXPORT_PSI_MI_LEVEL_2_5 = 1;
	private String fileName;
	private TaskMonitor taskMonitor;
	private int exportOption;

	/**
	 * Constructor.
	 *
	 * @param fileName Filename to save to
	 * @param exportOption EXPORT_PSI_MI_LEVEL_1 or EXPORT_PSI_MI_LEVEL_2_5.
	 */
	public SaveAsPsiTask(String fileName, int exportOption) {
		this.fileName = fileName;
		this.exportOption = exportOption;
	}

	/**
	 * Executes the Task.
	 */
	public void run() {
		taskMonitor.setStatus("Saving to PSI-MI...");

		try {
			StringWriter writer = new StringWriter();

			if (Cytoscape.getCurrentNetwork().getNodeCount() == 0) {
				throw new IllegalArgumentException("Network is empty.");
			}

			FileWriter f = new FileWriter(fileName);
			CyNetwork netToSave = Cytoscape.getCurrentNetwork();

			//  First, map to Data Service Objects
			MapFromCytoscape mapper1 = new MapFromCytoscape(netToSave);
			mapper1.doMapping();

			ArrayList interactions = mapper1.getInteractions();

			//  Second, map to PSI-MI
			if (exportOption == SaveAsPsiTask.EXPORT_PSI_MI_LEVEL_1) {
				MapInteractionsToPsiOne mapper2 = new MapInteractionsToPsiOne(interactions);
				mapper2.doMapping();

				org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet entrySet = mapper2.getPsiXml();
				Marshaller marshaller = createMarshaller("org.cytoscape.coreplugin.psi_mi.schema.mi1");
				marshaller.marshal(entrySet, writer);
			} else {
				MapInteractionsToPsiTwoFive mapper2 = new MapInteractionsToPsiTwoFive(interactions);
				mapper2.doMapping();

				org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet entrySet = mapper2.getPsiXml();
				Marshaller marshaller = createMarshaller("org.cytoscape.coreplugin.psi_mi.schema.mi25");
				marshaller.marshal(entrySet, writer);
			}

			f.write(writer.toString());
			f.close();

			Object[] retValue = new Object[3];
			retValue[0] = netToSave;
			retValue[1] = new File(fileName).toURI();
			retValue[2] = new Integer(Cytoscape.FILE_PSI_MI);
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, retValue);

			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Network successfully saved to:  " + fileName);
		} catch (IllegalArgumentException e) {
			taskMonitor.setException(e, "Network is Empty.  Cannot be saved.");
		} catch (IOException e) {
			taskMonitor.setException(e, "Unable to save network.");
		} catch (JAXBException e) {
			taskMonitor.setException(e, "Unable to save network.");
		}
	}

	private Marshaller createMarshaller(String schema) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(schema);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		return marshaller;
	}

	/**
	 * Halts the Task:  Not Currently Implemented.
	 */
	public void halt() {
		//   Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 *
	 * @param taskMonitor TaskMonitor Object.
	 * @throws IllegalThreadStateException Illegal Thread State Error.
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
		return new String("Saving Network");
	}
}
