/* -*-Java-*-
********************************************************************************
*
* File:         CytoscapeEditorPlugin.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Mon Aug 01 08:42:41 2005
* Modified:     Mon Aug 18 14:27:54 2008 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Wed Jul 09 10:21:03 2008 (Michael L. Creech) creech@w235krbza760
*  Updated to version 2.60.
* Tue Oct 30 11:22:23 2007 (Michael L. Creech) creech@w235krbza760
*  Updated to version 2.53.
* Thu Oct 25 13:30:25 2007 (Michael L. Creech) creech@w235krbza760
*  Updated to version 2.52.
* Thu Jul 19 13:29:22 2007 (Michael L. Creech) creech@w235krbza760
*  Removed use of _initialized and simplified to assume CytoscapeEditor
*  is always loaded before any other editor. Removed
*  unneeded definitions of  getPluginInfoObject() and describe().
* Fri May 11 16:37:23 2007 (Michael L. Creech) creech@w235krbza760
*  Updated VERSION to 2.50 and added getPluginInfoObject() for Cytoscape 2.5.
* Fri Dec 15 10:08:07 2006 (Michael L. Creech) creech@w235krbza760
*  Hacked a fix for not reinitializing the plugin if it is loaded
*  first by another plugin.
* Sat Aug 05 08:14:51 2006 (Michael L. Creech) creech@w235krbza760
*  Removed deprecated call to CytoscapeInit.getDefaultVisualStyle() in
*  initializeCytoscapeEditor().
* Mon Jul 24 08:43:51 2006 (Michael L. Creech) creech@w235krbza760
*  Removed some misleading code--println that editor is loaded and
*  initialization code as though editor is initialized via a menu item, when
*  it is not.
********************************************************************************
*/
package cytoscape.editor;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;



/**
 * core plugin for CytoscapeEditor.
 *
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class CytoscapeEditorPlugin implements BundleActivator {
    // MLC 07/20/07:
    private static final double VERSION = 2.60;

	/**
	 * Creates a new CytoscapeEditorPlugin object.
	 */
	public void start(BundleContext bc) {
	    CytoscapeEditorManager.initialize();

		CytoscapeEditorManager.register(CytoscapeEditorManager.DEFAULT_EDITOR_TYPE,
		                                "cytoscape.editor.event.PaletteNetworkEditEventHandler",
		CytoscapeEditorManager.NODE_TYPE, CytoscapeEditorManager.EDGE_TYPE,
		                                CytoscapeEditorManager.ANY_VISUAL_STYLE);


		String editorName = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;

		try {
			CytoscapeEditor cyEditor = CytoscapeEditorFactory.INSTANCE.getEditor(editorName);
			CytoscapeEditorManager.setCurrentEditor(cyEditor);

		} catch (InvalidEditorException ex) {
			CytoscapeEditorManager.log("Error: cannot set up Cytoscape Editor: " + editorName);
		}

		Cytoscape.getDesktop().setVisualStyle(Cytoscape.getVisualMappingManager()
		                                               .getCalculatorCatalog()
		                                               .getVisualStyle( 
		CytoscapeInit.getProperties().getProperty("defaultVisualStyle")));
	}
	public void stop(BundleContext bc) {
	}
}
