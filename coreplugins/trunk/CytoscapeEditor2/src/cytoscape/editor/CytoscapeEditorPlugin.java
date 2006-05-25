/*
 * Created on Aug 1, 2005
 *
 */
package cytoscape.editor;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

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
	// private static final String VERSION_POST_FIX = " v "
	// + BioPaxPlugIn.VERSION_MAJOR_NUM + "_"
	// + BioPaxPlugIn.VERSION_MINOR_NUM;

	private static final String ICONS_REL_LOC = "images/";

	private static final String BIO_PAX_VISUAL_STYLE = "BioPAX"
	// + VERSION_POST_FIX;
			+ "_editor";

	private static final String marker = "editorVisualStyleMap.";

	/**
	 * 
	 */
	public CytoscapeEditorPlugin() {

		// Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
		System.out.println("CytoscapeEditor loaded ");
		MainPluginAction mpa = new MainPluginAction();
		CytoscapeEditorManager.setRunningEditorFramework(true);
		System.out.println("Setting up CytoscapeEditor");
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
			
			// AJK: 05/22/06 experiment with enabling editor by default, so as to 
			//            pick up editors when a session is restored
			CytoscapeEditorManager.setEditingEnabled(true);
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
			// comment out the 'stamp-based' editor
			/*
			 * CytoscapeEditorManager.register("BasicCytoscapeEditor",
			 * "BasicNetworkEditEventHandler");
			 * CytoscapeEditorManager.setVisualStyleNameForEditorType("BasicCytoscapeEditor",
			 * CytoscapeEditorManager.ANY_VISUAL_STYLE);
			 */

			// add default palette-based editor
			System.out.println("Registering editor: "
					+ CytoscapeEditorManager.DEFAULT_EDITOR_TYPE
					+ " with visual style:  "
					+ CytoscapeEditorManager.ANY_VISUAL_STYLE);
			CytoscapeEditorManager.register(
					CytoscapeEditorManager.DEFAULT_EDITOR_TYPE,
					"cytoscape.editor.event.PaletteNetworkEditEventHandler",
					// AJK: 02/03/06 have Default editor use current visual
					// style
					CytoscapeEditorManager.NODE_TYPE,
					CytoscapeEditorManager.EDGE_TYPE,
					CytoscapeEditorManager.ANY_VISUAL_STYLE);

			// CytoscapeInit.getDefaultVisualStyle());

			CytoscapeEditorManager.setVisualStyleNameForEditorType(
					CytoscapeEditorManager.DEFAULT_EDITOR_TYPE,
					// AJK: 02/03/06 have Default editor use current visual
					// style
					CytoscapeEditorManager.ANY_VISUAL_STYLE);
			// CytoscapeInit.getDefaultVisualStyle());

			// AJK: 10/05/05 END

			// Bring in BioMoleculeEditor visual style so to drive definition of
			// SimpleBioMoleculeEditor

			// AJK: 03/29/06 BEGIN
			// move creation of visual style to the time of editor
			// initialization
			// to avoid any conflicts with other plugins that alter current
			// visual style
			// MapBioMoleculeEditorToVisualStyle mpbv = new
			// MapBioMoleculeEditorToVisualStyle();
			// mpbv.createVizMapper();
			// AJK: 03/29/06 END
			CytoscapeEditorManager.register("SimpleBioMoleculeEditor",
					"cytoscape.editor.event.PaletteNetworkEditEventHandler",
					CytoscapeEditorManager.NODE_TYPE,
					CytoscapeEditorManager.EDGE_TYPE,
					MapBioMoleculeEditorToVisualStyle.BIOMOLECULE_VISUAL_STYLE);
			CytoscapeEditorManager.setVisualStyleNameForEditorType(
					"SimpleBioMoleculeEditor",
					MapBioMoleculeEditorToVisualStyle.BIOMOLECULE_VISUAL_STYLE);

			// AJK: 03/23/06 END

			/**
			 * set visual styles network map
			 */
//			processEditorProperties();

			// AJK: 08/12/05 BEGIN
			// for version 2.2, just enable the DefaultCytoscapeEditor and have
			// it
			// setup for all network views

			String editorName = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
			try {
				CytoscapeEditor cyEditor = CytoscapeEditorFactory.INSTANCE
						.getEditor(editorName);
				CytoscapeEditorManager.setCurrentEditor(cyEditor);
				// System.out.println ("Set current editor to: " +
				// CytoscapeEditorManager.getCurrentEditor());
				// System.out.println ("for editor name: " + editorName);
				// cyEditor.initializeControls(null);
				CytoscapeEditorManager.setDefaultEditor(cyEditor);
			} catch (InvalidEditorException ex) {
				System.out.println("Error: cannot set up Cytoscape Editor: "
						+ editorName);
			}

			// AJK: 08/12/05

			// AJK: 10/12/05 BEGIN
			// reset to default visual style
			// TODO: set default visual style programmatically, not with a
			// hardcoded name!
			Cytoscape.getDesktop().setVisualStyle(
					Cytoscape.getDesktop().getVizMapManager()
							.getCalculatorCatalog().getVisualStyle(
									CytoscapeInit.getDefaultVisualStyle()));
			// AJK: 10/12/05 END

		}

//		public void processEditorProperties() {
//			Properties editorProperties = new Properties();
//			loadProperties("editor.props", editorProperties);
//			// iterate through properties list
//
//			for (Enumeration e = editorProperties.propertyNames(); e
//					.hasMoreElements();) {
//				String propKey = (String) e.nextElement();
//				int p = propKey.lastIndexOf(marker);
//				if (p == -1)
//					continue;
//				p = p + marker.length();
//
//				// the URL
//				String vizStyle = editorProperties.getProperty(propKey);
//				if (vizStyle == null) {
//					continue;
//				}
//
//				// the link name
//				String[] netNames = ((String) propKey.substring(p)).split("\\.");
//				String netName = netNames[netNames.length - 1];
//
//				System.out.println("Setting visual style for network: " + netName 
//						+ " = " + vizStyle);
//				CytoscapeEditorManager.setVisualStyleforNetwork(netName, vizStyle);
//			}
//		}
//
//		private void loadProperties(String defaultName, Properties props) {
//			if (props == null)
//				props = new Properties();
//
//			String tryName = "";
//			try {
//				// load the props from the jar file
//				tryName = "cytoscape.jar";
//				URL vmu = ClassLoader.getSystemClassLoader().getSystemResource(
//						defaultName);
//				if (vmu != null)
//					props.load(vmu.openStream());
//
//				// load the props file from $HOME/.cytoscape
//				tryName = "$HOME/.cytoscape";
//				File vmp = CytoscapeInit.getConfigFile(defaultName);
//				if (vmp != null)
//					props.load(new FileInputStream(vmp));
//
//			} catch (IOException ioe) {
//				System.err.println("couldn't open " + tryName + " "
//						+ defaultName + " file - creating a hardcoded default");
//				ioe.printStackTrace();
//			}
//
//		}
	}

}