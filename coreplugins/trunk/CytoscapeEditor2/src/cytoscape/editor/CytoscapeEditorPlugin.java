/*
 * Created on Aug 1, 2005
 *
 */
package cytoscape.editor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.editor.editors.MapBioMoleculeEditorToVisualStyle;
import cytoscape.plugin.CytoscapePlugin;

/**
 * core plugin for CytoscapeEditor.
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * 
 */
public class CytoscapeEditorPlugin extends CytoscapePlugin {

	/**
	 * 
	 */
	public CytoscapeEditorPlugin() {

		System.out.println("CytoscapeEditor loaded ");
		MainPluginAction mpa = new MainPluginAction();
		CytoscapeEditorManager.setRunningEditorFramework(true);
		System.out.println("Setting up CytoscapeEditor");
		mpa.initializeCytoscapeEditor();
	}

	public class MainPluginAction extends AbstractAction {
		public MainPluginAction() {
			super("Cytoscape Editor");
		}

		/**
		 * Gives a description of this plugin.
		 */
		public String describe() {
			StringBuffer sb = new StringBuffer();
			sb.append("Add nodes and edges to a Cytoscape Network. ");
			return sb.toString();
		}

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {
			initializeCytoscapeEditor();
		}

		/**
		 * sets various flags and registers various editors with the CytoscapeEditorManager
		 *
		 */
		public void initializeCytoscapeEditor() {
			

			CytoscapeEditorManager.setEditingEnabled(false);

			CytoscapeEditorManager.initialize();

			// add default palette-based editor
			CytoscapeEditorManager.register(
					CytoscapeEditorManager.DEFAULT_EDITOR_TYPE,
					"cytoscape.editor.event.PaletteNetworkEditEventHandler",
					// AJK: 02/03/06 have Default editor use current visual
					// style
					CytoscapeEditorManager.NODE_TYPE,
					CytoscapeEditorManager.EDGE_TYPE,
					CytoscapeEditorManager.ANY_VISUAL_STYLE);

			CytoscapeEditorManager.register("SimpleBioMoleculeEditor",
					"cytoscape.editor.event.PaletteNetworkEditEventHandler",
					CytoscapeEditorManager.NODE_TYPE,
					CytoscapeEditorManager.EDGE_TYPE,
					MapBioMoleculeEditorToVisualStyle.BIOMOLECULE_VISUAL_STYLE);

			String editorName = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
			try {
				CytoscapeEditor cyEditor = CytoscapeEditorFactory.INSTANCE
						.getEditor(editorName);
				CytoscapeEditorManager.setCurrentEditor(cyEditor);
				CytoscapeEditorManager.setDefaultEditor(cyEditor);
			} catch (InvalidEditorException ex) {
				System.out.println("Error: cannot set up Cytoscape Editor: "
						+ editorName);
			}

			Cytoscape.getDesktop().setVisualStyle(
					Cytoscape.getVisualMappingManager()
							.getCalculatorCatalog().getVisualStyle(
									CytoscapeInit.getDefaultVisualStyle()));
		}

	}
}