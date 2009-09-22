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

import csplugins.id.mapping.AttributeBasedIDMappingImpl;
import csplugins.id.mapping.util.DataSourceWrapper;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.CyNetwork;

import java.util.Set;
import java.util.Map;
/**
 *
 */
public class AttributeBasedIDMappingTask implements Task {
	private final Set<CyNetwork> networks;
    private final Map<String,Set<DataSourceWrapper>> mapSrcAttrIDTypes;
    private final Map<String, DataSourceWrapper> mapTgtAttrNameIDType;
    private final AttributeBasedIDMappingImpl service;
    
	private TaskMonitor taskMonitor;
        private boolean success;

	/**
         * 
         * @param networks
         * @param mapSrcAttrIDTypes
         * @param mapTgtAttrNameIDType
         */
	public AttributeBasedIDMappingTask(final Set<CyNetwork> networks,
                                       final Map<String,Set<DataSourceWrapper>> mapSrcAttrIDTypes,
                                       final Map<String, DataSourceWrapper> mapTgtAttrNameIDType) {
		this.networks = networks;
                this.mapSrcAttrIDTypes = mapSrcAttrIDTypes;
                this.mapTgtAttrNameIDType = mapTgtAttrNameIDType;
                service = new AttributeBasedIDMappingImpl();
                success = false;
	}

	/**
	 * Executes Task.
	 */
        //@Override
	public void run() {
                try {
                        service.setTaskMonitor(taskMonitor);
                        
                        service.map(networks, mapSrcAttrIDTypes, mapTgtAttrNameIDType);


                } catch (Exception e) {
                        taskMonitor.setPercentCompleted(100);
                        taskMonitor.setStatus("ID mapping failed.\n");
                        e.printStackTrace();
                }

                success = true;

	}

        public boolean success() {
            return success;
        }


	/**
	 * Halts the Task: Not Currently Implemented.
	 */
        //@Override
	public void halt() {
            service.interrupt();
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
