package metaNodeViewer;

import metaNodeViewer.ui.MNcollapserDialog;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;

public class MetaNodeViewerCytoPlugin extends CytoscapePlugin{
	
	protected static final String pluginTitle = "Meta-Node Abstraction";
	
	/**
	 * Constructor.
	 */
	public MetaNodeViewerCytoPlugin (){
		final MNcollapserDialog dialog = new MNcollapserDialog();
		dialog.setResizable(false);
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
				new AbstractAction (pluginTitle + "..."){
					public void actionPerformed (ActionEvent e){
						dialog.pack();
						dialog.setLocationRelativeTo(Cytoscape.getDesktop());
						dialog.setVisible(true);
					}//actionPerformed
				}
		);
	}//constructor
	
}//class MetaNodeViewerCytoPlugin
