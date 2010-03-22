package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.MenuEvent;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

/**
 * Apply Visual Style for a set of selected networks.
 * One action represents a 
 * 
 * @author kono
 * @since Cytoscape 2.7.0
 *
 */
public class ApplyVisualStyleAction extends CytoscapeAction {

	private static final long serialVersionUID = 2435881456685787138L;
	
	private final String styleName;
	
	
	/**
	 * Create an action for the specified Visual Style.
	 * 
	 * @param styleName - Name of the Visual Style
	 */
	public ApplyVisualStyleAction(final String styleName) {
		super(styleName);
		this.styleName = styleName;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		final List<CyNetwork> selected = Cytoscape.getSelectedNetworks();
		final Set<CyNetworkView> views = new HashSet<CyNetworkView>();
		
		for (final CyNetwork network: selected) {
			final CyNetworkView targetView = Cytoscape.getNetworkView(network.getIdentifier());
			if (targetView != Cytoscape.getNullNetworkView()) {
				views.add(targetView);
			}
		}
		
		applyStyle(views);
	}
	
	private void applyStyle(final Set<CyNetworkView> targetViews) {
		// Switch current Visual Style
		Cytoscape.getVisualMappingManager().setVisualStyle(styleName);
		
		// Apply it for selected views.
		for (CyNetworkView view: targetViews) {
			view.setVisualStyle(styleName);
			view.redrawGraph(false, true);
			Cytoscape.getVisualMappingManager().setNetworkView(view);
		}
	}
	
	public void menuSelected(MenuEvent e) {
		this.enableForNetworkAndView();
	}
}
