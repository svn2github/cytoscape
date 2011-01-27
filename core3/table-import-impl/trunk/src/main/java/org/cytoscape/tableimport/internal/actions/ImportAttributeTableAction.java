package org.cytoscape.tableimport.internal.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.tableimport.internal.ui.ImportTextTableDialog;
import org.cytoscape.work.swing.GUITaskManager;
import java.io.IOException;
import javax.xml.bind.JAXBException;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.util.swing.FileUtil;

public class ImportAttributeTableAction extends AbstractCyAction {
	private final static long serialVersionUID = 1205339869460898L;
	
	/**
	 * Creates a new ImportAttributeTableAction object.
	 */ 
	public ImportAttributeTableAction(CySwingApplication desktop,CyApplicationManager appMgr,
			CyNetworkManager netMgr,
			CyProperty<Bookmarks> bookmarksProp, BookmarksUtil bookmarksUtil,
			GUITaskManager guiTaskManagerServiceRef, CyProperty cytoscapePropertiesServiceRef,
			CyTableManager tblMgr, FileUtil fileUtilService, OpenBrowser openBrowserService,
			CyTableFactory tableFactory) 

	{
		super("Attribute from Table (Text/MS Excel)...", appMgr);
		setPreferredMenu("File.Import");
		
		//
		CytoscapeServices.desktop = desktop;
		CytoscapeServices.bookmarksUtil = bookmarksUtil;
		CytoscapeServices.cytoscapePropertiesServiceRef= cytoscapePropertiesServiceRef;
		CytoscapeServices.guiTaskManagerServiceRef = guiTaskManagerServiceRef;
		CytoscapeServices.tblMgr =tblMgr;
		CytoscapeServices.theBookmarks = bookmarksProp.getProperties();
		CytoscapeServices.openBrowser = openBrowserService;
		CytoscapeServices.fileUtil = fileUtilService;
		CytoscapeServices.appMgr = appMgr;
		CytoscapeServices.netMgr = netMgr;
		CytoscapeServices.tableFactory = tableFactory;
		
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
				
		ImportTextTableDialog iad;

		try {
			iad = new ImportTextTableDialog(CytoscapeServices.desktop.getJFrame(), true, ImportTextTableDialog.SIMPLE_ATTRIBUTE_IMPORT);
			iad.pack();
			iad.setLocationRelativeTo(CytoscapeServices.desktop.getJFrame());
			iad.setVisible(true);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

