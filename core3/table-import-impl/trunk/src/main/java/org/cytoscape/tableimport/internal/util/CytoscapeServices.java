package org.cytoscape.tableimport.internal.util;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.work.swing.GUITaskManager;

public class  CytoscapeServices {
	
	public static CySwingApplication desktop;
	public static Bookmarks theBookmarks;
	public static BookmarksUtil bookmarksUtil;
	public static GUITaskManager guiTaskManagerServiceRef;
	public static CyProperty cytoscapePropertiesServiceRef;
	public static CyTableManager tblMgr;
	
}
