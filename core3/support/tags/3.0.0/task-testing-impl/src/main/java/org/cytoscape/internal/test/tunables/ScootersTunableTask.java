
package org.cytoscape.internal.test.tunables;

/*
 * #%L
 * Tasks for Testing
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.util.ListSingleSelection;


public class ScootersTunableTask extends AbstractTask {

	@Tunable(description="Enable Aggregation")
	public boolean enableAggregation;

	private final ListSingleSelection<String> attrSelection;

	@Tunable(description="Attribute Selection",groups="Overrides",dependsOn="enableAggregation=true")
	public ListSingleSelection<String> getAttrSelection() {	
		return attrSelection;
	}

	public void setAttrSelection(ListSingleSelection<String> input) {
		// Ignore because ListSingleSelection is set in the handler and not here.
	}

	@Tunable(description="Attribute Type",groups="Overrides",dependsOn="enableAggregation=true")
	public String getAttrType() { return "Float"; }

	public void setAttrType(String t) { 
		// Ignore because this is just here for show.  Normally we'd set the string value!	
	}

	private ListSingleSelection<String> aggregationType; 

	@Tunable(description="Aggregation Type",groups="Overrides",dependsOn="AttrSelection!=",listenForChange="AttrSelection")
	public ListSingleSelection<String> getAggregationType()	{
		// We dynamically update the contents of the ListSingleSelection
		// based on the selection made in AttrSelection. The "listenForChange"
		// parameter in the tunable triggers this method to be called when
		// AttrSelection changes.
		String selected = attrSelection.getSelectedValue();
		System.out.println("updating aggregation type with: " + selected);

		if ( selected != null && selected.equals("attr 1") ) {
			System.out.println("using mean/med/max");
			aggregationType = new ListSingleSelection<String>("mean","median","max","min");
		} else {
			System.out.println("using first/last");
			aggregationType = new ListSingleSelection<String>("first","last");
		}

		return aggregationType;
	}

	@ContainsTunables
	public JustTunables someSortOfConfigObject = new JustTunables();

	public void setAggregationType(ListSingleSelection<String> input) {
		// Ignore because ListSingleSelection is set in the handler and not here.
	}


	public ScootersTunableTask() {
		attrSelection = new ListSingleSelection<String>("attr 1","attr 2","asdf");
		aggregationType = new ListSingleSelection<String>("mean","median","max","min");
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		System.out.println("selected column: " + attrSelection.getSelectedValue());
		System.out.println("selected agg type: " + aggregationType.getSelectedValue());
	}
}
