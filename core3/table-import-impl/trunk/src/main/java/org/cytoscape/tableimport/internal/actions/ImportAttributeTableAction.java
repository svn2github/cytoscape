package org.cytoscape.tableimport.internal.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.session.CyApplicationManager;
//import org.cytoscape.tableimport.internal.ui.ImportTextTableDialog;
import org.cytoscape.work.swing.GUITaskManager;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.cytoscape.model.CyTableManager;


public class ImportAttributeTableAction extends AbstractCyAction {
	private final static long serialVersionUID = 1205339869460898L;
	private final CySwingApplication desktop;
	private Bookmarks theBookmarks;
	private BookmarksUtil bookmarksUtil;
	private GUITaskManager guiTaskManagerServiceRef;
	private CyProperty cytoscapePropertiesServiceRef;
	private CyTableManager tblMgr;
	
	/**
	 * Creates a new ImportAttributeTableAction object.
	 */ 
	public ImportAttributeTableAction(CySwingApplication desktop,CyApplicationManager appMgr,
			CyProperty<Bookmarks> bookmarksProp, BookmarksUtil bookmarksUtil,
			GUITaskManager guiTaskManagerServiceRef, CyProperty cytoscapePropertiesServiceRef,
			CyTableManager tblMgr) 

	{
		super("Attribute from Table (Text/MS Excel)...", appMgr);
		setPreferredMenu("File.Import");
		
		this.desktop = desktop;
		this.theBookmarks = bookmarksProp.getProperties();	
		this.bookmarksUtil = bookmarksUtil;
		this.guiTaskManagerServiceRef = guiTaskManagerServiceRef;
		this.cytoscapePropertiesServiceRef = cytoscapePropertiesServiceRef;
		this.tblMgr = tblMgr;
		
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		
		System.out.println("\n\nImportAttributeTableAction.actionPerformed()...\n\n");
		
		/*
		ImportTextTableDialog iad;

		try {
			iad = new ImportTextTableDialog(desktop.getJFrame(), true,
			                                ImportTextTableDialog.SIMPLE_ATTRIBUTE_IMPORT);
			iad.pack();
			iad.setLocationRelativeTo(desktop.getJFrame());
			iad.setVisible(true);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
	}
}

