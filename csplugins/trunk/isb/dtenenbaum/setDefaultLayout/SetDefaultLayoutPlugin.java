package csplugins.isb.dtenenbaum.setDefaultLayout;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.visual.VisualStyle;
import cytoscape.actions.FitContentAction;
import cytoscape.plugin.*;
import java.beans.*;

import phoebe.PGraphView;

import yfiles.YFilesLayout;
import yfiles.YFilesLayoutPlugin;

/**
 * For now this class just ensures that every new graph loaded is viewed in
 * organic layout to begin with. In the future it can take parameters telling 
 * it which layout to use. YFilesLayoutPlugin must also be loaded for this to work!   
 * A similar plugin could be written to center a graph every time one is loaded.
 * TODO - update this
 * @author dtenenba
 *
 */
public class SetDefaultLayoutPlugin extends CytoscapePlugin implements
		PropertyChangeListener {

	YFilesLayoutPlugin plugin;

	public SetDefaultLayoutPlugin() {
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				Cytoscape.ATTRIBUTES_CHANGED, this);

	}

	public void propertyChange(PropertyChangeEvent e) {

		System.out.println("Expect to see a big long stack trace after this.");
		System.out.println("Don't worry, it doesn't affect anything.");
		if (e.getPropertyName() == Cytoscape.ATTRIBUTES_CHANGED) {
			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		}

		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			CyNetworkView view = (CyNetworkView) e.getNewValue();

			YFilesLayout layout = new YFilesLayout(view, false);
			layout.doLayout(YFilesLayout.ORGANIC, 0);

			view.fitContent();
		}
		System.out.println("The previous big long stack trace can be ignored.");
	}
}

