package metaNodeViewer;

import metaNodeViewer.ui.MNcollapserDialog;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;

public class MetaNodeViewerCytoPlugin extends CytoscapePlugin{
	
	/**
	 * Constructor.
	 */
	public MetaNodeViewerCytoPlugin (){
		final MNcollapserDialog dialog = new MNcollapserDialog();
		dialog.setOptions(false,true,false,false);
		dialog.setResizable(false);
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
				new AbstractAction ("Meta Node Collapser..."){
					public void actionPerformed (ActionEvent e){
						dialog.pack();
						dialog.setVisible(true);
					}//actionPerformed
				}
		);
	}//constructor
	
}//class MetaNodeViewerCytoPlugin
