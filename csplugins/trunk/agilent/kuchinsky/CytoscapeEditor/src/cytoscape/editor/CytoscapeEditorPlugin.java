/*
 * Created on Aug 1, 2005
 *
 */
package cytoscape.editor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.mskcc.biopax_plugin.mapping.MapBioPaxToVisualStyle;
import org.mskcc.biopax_plugin.plugin.BioPaxPlugIn;

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

	// TODO: code cloned from BioPAX importer; need to ask Ethan to make
	// BIOPAX_VISUAL_STYLE string public
	private static final String VERSION_POST_FIX = " v "
			+ BioPaxPlugIn.VERSION_MAJOR_NUM + "_"
			+ BioPaxPlugIn.VERSION_MINOR_NUM;

	private static final String BIO_PAX_VISUAL_STYLE = "BioPAX"
			+ VERSION_POST_FIX;

	/**
	 *  
	 */
	public CytoscapeEditorPlugin() {
		//		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
		MainPluginAction mpa = new MainPluginAction();
		CytoscapeEditorManager.setRunningEditorFramework(true);
		mpa.enableCytoscapeEditor();
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
			enableCytoscapeEditor();
		}

		public void enableCytoscapeEditor() {
			String[] cytoscapeArgs = CytoscapeInit.getArgs();

			// hook for enabling the Cytoscape editor framework
			// not advertised, only made available via bat script, to early
			// adopters of Cytoscape editor framework
			for (int i = 0; i < cytoscapeArgs.length; i++) {
				String arg = cytoscapeArgs[i];
				if (arg.equals("--EditorFramework")) {
					CytoscapeEditorManager.setRunningEditorFramework(true);
					break;
				}
			}
			

			CytoscapeEditorManager.initialize();

			// AJK: 10/05/05 BEGIN
			//      comment out the 'stamp-based' editor
			/*
			 * CytoscapeEditorManager.register("BasicCytoscapeEditor",
			 * "BasicNetworkEditEventHandler");
			 * CytoscapeEditorManager.setVisualStyleNameForEditorType("BasicCytoscapeEditor",
			 * CytoscapeEditorManager.ANY_VISUAL_STYLE);
			 */

			// add default palette-based editor
			CytoscapeEditorManager.register(CytoscapeEditorManager.DEFAULT_EDITOR_TYPE,
					"PaletteNetworkEditEventHandler",
					CytoscapeInit.getDefaultVisualStyle());
			
			
			CytoscapeEditorManager.setVisualStyleNameForEditorType(
					CytoscapeEditorManager.DEFAULT_EDITOR_TYPE,
					CytoscapeInit.getDefaultVisualStyle());

			// AJK: 10/05/05 END

			// Bring in BioMoleculeEditor visual style so to drive definition of
			// SimpleBioMoleculeEditor
			MapBioMoleculeEditorToVisualStyle mpbv = new MapBioMoleculeEditorToVisualStyle();
			mpbv.createVizMapper();
			CytoscapeEditorManager.register("SimpleBioMoleculeEditor",
					"PaletteNetworkEditEventHandler",
					MapBioMoleculeEditorToVisualStyle.BIOMOLECULE_VISUAL_STYLE);
			CytoscapeEditorManager.setVisualStyleNameForEditorType(
					"SimpleBioMoleculeEditor",
					MapBioMoleculeEditorToVisualStyle.BIOMOLECULE_VISUAL_STYLE);

			// Bring in MSKCC BioPAX visual style so to drive definition of
			// SimpleBioPAX_Editor
			MapBioPaxToVisualStyle mpb = new MapBioPaxToVisualStyle();
			mpb.createVizMapper();
			
			
			// hack: make Canonical name visible

			CytoscapeEditorManager.register("SimpleBioPAX_Editor",
					"BioPAXNetworkEditEventHandler", "BIOPAX_NODE_TYPE",
					"BIOPAX_EDGE_TYPE", BIO_PAX_VISUAL_STYLE);
			// TODO: ask Ethan to make BIO_PAX_VISUAL_STYLE constant public, so
			// that we don't need this hardcoded hack.
			CytoscapeEditorManager.setVisualStyleNameForEditorType(
					"SimpleBioPAX_Editor", BIO_PAX_VISUAL_STYLE);

			/**
			 * set visual styles for the editors
			 */

			// AJK: 08/12/05 BEGIN
			//      for version 2.2, just enable the DefaultCytoscapeEditor and have it
			// setup for all network views

				String editorName = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
				try {
					CytoscapeEditor cyEditor = CytoscapeEditorFactory.INSTANCE
							.getEditor(editorName);
					CytoscapeEditorManager.setCurrentEditor(cyEditor);
					//		System.out.println ("Set current editor to: " +
					// CytoscapeEditorManager.getCurrentEditor());
					//		System.out.println ("for editor name: " + editorName);
//					cyEditor.initializeControls(null);
					CytoscapeEditorManager.setDefaultEditor(cyEditor);
				} catch (InvalidEditorException ex) {
					System.out
							.println("Error: cannot set up Cytoscape Editor: "
									+ editorName);
				}

			// AJK: 08/12/05

			// AJK: 10/12/05 BEGIN
			//     reset to default visual style
			//     TODO: set default visual style programmatically, not with a hardcoded name!
			Cytoscape.getDesktop().setVisualStyle(
					Cytoscape.getDesktop().getVizMapManager().getCalculatorCatalog().getVisualStyle(CytoscapeInit.getDefaultVisualStyle()));			
			// AJK: 10/12/05 END
			
			
		}
	}

}