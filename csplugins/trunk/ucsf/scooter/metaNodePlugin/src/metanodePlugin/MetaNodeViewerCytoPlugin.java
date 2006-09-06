package metanodePlugin;

import metanodePlugin.ui.MNcollapserDialog;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import cytoscape.*;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.plugin.CytoscapePlugin;
import java.beans.*;
import ding.view.*;
import giny.view.*;

/**
 * A plug-in that provides operations to create meta-nodes,
 * destroy meta-nodes, collapse graphs into meta-nodes, and expand meta-nodes.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 */

public class MetaNodeViewerCytoPlugin 
extends CytoscapePlugin 
implements NodeContextMenuListener, PropertyChangeListener {
	
	protected static final String pluginTitle = "MetaNodes Plugin";
	public static final String VERSION = "BETA";
	protected MNcollapserDialog dialog;
	
	/**
	 * Constructor.
	 */
	public MetaNodeViewerCytoPlugin (){
		
		try {
			// Add ourselves to the network view created change list
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
			// Add ourselves to the current network context menu
			((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(this);
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		
		dialog = new MNcollapserDialog();
		dialog.setResizable(false);
		
		// Add the plugin to the plugins menu
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
				new AbstractAction ("MetaNode Operations..."){
					public void actionPerformed (ActionEvent e){
						dialog.pack();
						dialog.setLocationRelativeTo(Cytoscape.getDesktop());
						dialog.setVisible(true);
					}//actionPerformed
				}
		);
	}//constructor

	/**
	 * Implements propertyChange
	 * @param e
	 **/
	public void propertyChange ( PropertyChangeEvent e ) {
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(this);
		}
	}
	
	/**
	 * Implements addNodeContextMenuItems
	 * @param nodeView
	 * @param menu
	 */
	public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu) {
		if (menu == null) {
			menu = new JPopupMenu();
		}
		menu.add(this.dialog.getMenu(nodeView));
	}

}//class MetaNodeViewerCytoPlugin
