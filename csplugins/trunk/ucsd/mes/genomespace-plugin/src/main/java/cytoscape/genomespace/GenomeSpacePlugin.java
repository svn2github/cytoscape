package cytoscape.genomespace;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;


/**
 * This class is used to instantiate your plugin. Put whatever initialization code
 * you need into the no argument constructor (the only one that will be called).
 * The actual functionality of your plugin can be in this class, but should 
 * probably be separated into separted classes that get instantiated here.
 */
public class GenomeSpacePlugin extends CytoscapePlugin {

	public GenomeSpacePlugin() {
		// Properly initializes things.
		super();

		// This action represents the actual behavior of the plugin.
		UploadFileToGenomeSpace uploadAction = new UploadFileToGenomeSpace();
		Cytoscape.getDesktop().getCyMenus().addAction(uploadAction);

		DeleteFileInGenomeSpace deleteAction = new DeleteFileInGenomeSpace();
		Cytoscape.getDesktop().getCyMenus().addAction(deleteAction);

		DownloadFileFromGenomeSpace downloadAction = new DownloadFileFromGenomeSpace();
		Cytoscape.getDesktop().getCyMenus().addAction(downloadAction);

		ListFilesInGenomeSpace listAction = new ListFilesInGenomeSpace();
		Cytoscape.getDesktop().getCyMenus().addAction(listAction);

		LoadNetworkFromGenomeSpace loadNetworkAction = new LoadNetworkFromGenomeSpace();
		Cytoscape.getDesktop().getCyMenus().addAction(loadNetworkAction);

		LoadAttrsFromGenomeSpace loadAttrsAction = new LoadAttrsFromGenomeSpace();
		Cytoscape.getDesktop().getCyMenus().addAction(loadAttrsAction);

		LoadSessionFromGenomeSpace loadSessionAction = new LoadSessionFromGenomeSpace();
		Cytoscape.getDesktop().getCyMenus().addAction(loadSessionAction);
	}
}	
