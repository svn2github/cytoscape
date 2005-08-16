/*
 * Created on Aug 1, 2005
 *
 */
package cytoscape.editor;

import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;

/**
 * enapsulating plugin for CytoscapeEditor.  Will be obsoleted when CytoscapeEditor moves to 
 * the Cytoscape core.
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * 
 */
public class CytoscapeEditorPlugin extends CytoscapePlugin {

	/**
	 *  
	 */
	public CytoscapeEditorPlugin() {
		super();
		String[] cytoscapeArgs = CytoscapeInit.getArgs();

		// hook for enabling the Cytoscape editor framework
		// not advertised, only made available via bat script, to early adopters of Cytoscape editor framework
		for (int i = 0; i < cytoscapeArgs.length; i++) {
			String arg = cytoscapeArgs[i];
			if (arg.equals("--EditorFramework")) {
				CytoscapeEditorManager.setRunningEditorFramework(true);
				break;
			}
		}

		CytoscapeEditorManager.initialize();

		CytoscapeEditorManager.register("BasicCytoscapeEditor",
				"BasicNetworkEditEventHandler");

		CytoscapeEditorManager.register("SimpleBioPAXEditor",
				"PaletteNetworkEditEventHandler");

		// AJK: 08/12/05 BEGIN
		//      for version 2.2, just enable the BasicCytoscapeEditor and have it
		// setup for all network views

		if (!CytoscapeEditorManager.isRunningEditorFramework()) {
			String editorName = "BasicCytoscapeEditor";
			try {
				CytoscapeEditor cyEditor = CytoscapeEditorFactory.INSTANCE
						.getEditor(editorName);
				CytoscapeEditorManager.setCurrentEditor(cyEditor);
				//		System.out.println ("Set current editor to: " +
				// CytoscapeEditorManager.getCurrentEditor());
				//		System.out.println ("for editor name: " + editorName);
				cyEditor.initializeControls(null);
			} catch (InvalidEditorException ex) {
				System.out.println("Error: cannot set up Cytoscape Editor: "
						+ editorName);
			}
		}

		// AJK: 08/12/05

	}

}