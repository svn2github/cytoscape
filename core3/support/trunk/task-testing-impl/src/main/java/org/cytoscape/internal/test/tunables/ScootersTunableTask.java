
package org.cytoscape.internal.test.tunables;


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
