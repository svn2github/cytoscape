package metaNodeViewer;

import metaNodeViewer.ui.MNcollapserDialog;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cytoscape.*;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.plugin.CytoscapePlugin;
import java.beans.*;

/**
 * A plug-in that provides operations to create meta-nodes,
 * destroy meta-nodes, collapse graphs into meta-nodes, and expand meta-nodes.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 */

public class MetaNodeViewerCytoPlugin 
extends CytoscapePlugin 
implements PropertyChangeListener {
	
	protected static final String pluginTitle = "MetaNodes Plugin";
    public static final String VERSION = "1.0";
	
	/**
	 * Constructor.
	 */
	public MetaNodeViewerCytoPlugin (){
		
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
		
		final MNcollapserDialog dialog = new MNcollapserDialog();
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
	 * Implements PropertyChangeListener.propertyChange
	 * @param e
	 */
	public void propertyChange ( PropertyChangeEvent e ){
		
		if ( e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED ){
			// Add menu to the context dialog
			CyNetworkView view = ( CyNetworkView )e.getNewValue();
			view.addContextMethod( "class phoebe.PNodeView",
					"metaNodeViewer.ui.AbstractMetaNodeMenu",
					"getAbstractMetaNodeMenu",
					new Object[] { view } ,
					CytoscapeInit.getClassLoader() );
		}
	}//propertyChange

	
}//class MetaNodeViewerCytoPlugin
