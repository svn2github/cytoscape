
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

package csplugins.id.mapping.ui;

import cytoscape.data.CyAttributes;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import csplugins.id.mapping.IDMapper;
import csplugins.id.mapping.model.AttributeBasedIDMappingData;

import csplugins.id.mapping.util.IDMappingDataUtils;

import java.util.Set;

/**
 *
 */
public class ReadIDMappingTask implements Task {
        private AttributeBasedIDMappingData idMapping;
        private final IDMapper idMapper;
        final Set<String> goIDs;
        final CyAttributes cyAttributes;
        final String attrName;
        final Set<String> potentialSrcTypes;
        final Set<String> tgtTypes;
	private TaskMonitor taskMonitor;

	/**
	 * Creates a new ReadIDMappingFileTask object.
	 *
	 * @param idMapper  DOCUMENT ME!
	 */
	public ReadIDMappingTask(AttributeBasedIDMappingData idMapping,
                                 final IDMapper idMapper,
                                 final Set<String> goIDs,
                                 final CyAttributes cyAttributes,
                                 final String attrName,
                                 final Set<String> potentialSrcTypes,
                                 final Set<String> tgtTypes) {
            this.idMapping = idMapping;
            this.idMapper = idMapper;
            this.goIDs = goIDs;
            this.cyAttributes = cyAttributes;
            this.attrName = attrName;
            this.potentialSrcTypes = potentialSrcTypes;
            this.tgtTypes = tgtTypes;
	}

	/**
	 * Executes Task.
	 */
        //@Override
	public void run() {
                try {
                        taskMonitor.setStatus("Mapping IDs.\n\nIt may take a while.\nPlease wait...");
                        taskMonitor.setPercentCompleted(-1);

                        IDMappingDataUtils.addAttributeBasedIDMappingFromIDMapper(
                                idMapping, idMapper, goIDs, cyAttributes, attrName,
                                potentialSrcTypes, tgtTypes);

                        taskMonitor.setPercentCompleted(100);
                        taskMonitor.setStatus("Succesfully mapped.\n");
                } catch (Exception e) {
                        taskMonitor.setPercentCompleted(100);
                        taskMonitor.setStatus("ID mapping failed.\n");
                        e.printStackTrace();
                } 

	}


	/**
	 * Halts the Task: Not Currently Implemented.
	 */
        //@Override
	public void halt() {
		// Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 *
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
        //@Override
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 *
	 * @return Task Title.
	 */
        //@Override
	public String getTitle() {
		return new String("ID mapping");
	}
}
