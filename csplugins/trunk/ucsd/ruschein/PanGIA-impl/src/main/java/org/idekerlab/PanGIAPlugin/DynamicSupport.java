package org.idekerlab.PanGIAPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class DynamicSupport {

	private static final Logger logger = LoggerFactory.getLogger(DynamicSupport.class);

	private static final String nodeAttriName = "Module Overview Network";
	
	public static String createDetailedView = "createDetailedView";
	public static String saveModules = "saveModules";
	public static String saveOverviewNetwork = "saveOverviewNetwork";
	public static String saveNodesToMatrix = "saveNodesToMatrix"; 

	private ListSingleSelection<String> menuTitleSelection;

	@Tunable(description="List of configurable URLs")
	public ListSingleSelection<String> getSubmenuOptions() {
		return menuTitleSelection;
	}

    /**
     * This method is a no-op.  Don't use it.
     */
    public void setSubmenuOptions(ListSingleSelection<String> opts) {
        // no-op
    }

	private Map<String,String> menuTitleActionMap = new HashMap<String,String>();
	private CyIdentifiable[] tableEntries;

	private CyNetworkView netView;
	private View<CyNode> nodeView;
	private String action = null;
	
	public DynamicSupport() {
	}

	
	protected synchronized void setViews(View<CyNode> nodeView, CyNetworkView netView){
		menuTitleActionMap.clear();
		
//	     boolean isOverviewNetwork = PanGIAPlugin.output.containsKey(nodeView.getModel().getCyRow().get("name", String.class));
//	     if (!isOverviewNetwork){
//	    	 return;
//	     }
//
//		if ( nodeView.getModel().getCyRow().get(nodeAttriName, String.class) == null ) {
//			menuTitleSelection = null;
//			menuTitleActionMap.clear();
//			return;
//		}

		
		System.out.println("DynamicSupport...........AAAA");

		// determine the actions
		menuTitleActionMap.put("Create Detailed View", createDetailedView);
		
	    // boolean selectedHasNested = false;
	    // if (nodeView.getModel().getNetwork() != null){
	 		menuTitleActionMap.put("Export Modules to Tab-Delimited File", saveModules);
	     //}
	     
		//menuTitleActionMap.put("Save Selected Nodes to Matrix File", xxx);
	     
		List<String> menuTitles = new ArrayList<String>( menuTitleActionMap.keySet() );
		
		System.out.println("menuTitles.toString() ="+ menuTitles.toString());
		
		Collections.sort(menuTitles);
		menuTitleSelection = new ListSingleSelection<String>(menuTitles);
	}
	


	public TaskIterator getTaskIterator() {
		String action = "none found"; 
		synchronized (this) {
			//System.out.println("Selected menu: " + menuTitleSelection.getSelectedValue());
			action = menuTitleActionMap.get( menuTitleSelection.getSelectedValue() );	
		}
			//System.out.println("url for LinkoutTask: " + url);
		return new TaskIterator(new PanGIANodeViewTask(this.netView, this.nodeView, action));
	}
}