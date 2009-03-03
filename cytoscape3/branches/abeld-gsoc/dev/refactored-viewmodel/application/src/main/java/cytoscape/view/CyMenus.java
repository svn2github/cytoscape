/*
 File: CyMenus.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.view;

import javax.swing.JMenu;
import cytoscape.util.CyMenuBar;
import cytoscape.util.CyToolBar;
import cytoscape.util.CyAction;
import java.util.Map;
import org.cytoscape.work.TaskFactory;

// TODO clean up the menus offered - these should match what's implemented
public interface CyMenus { 

	public CyMenuBar getMenuBar();
	public CyToolBar getToolBar();

	public JMenu getFileMenu();
	public JMenu getLoadSubMenu();
	public JMenu getSaveSubMenu();
	public JMenu getEditMenu();
	public JMenu getViewMenu();
	public JMenu getSelectMenu();
	public JMenu getLayoutMenu();
	public JMenu getVizMenu();
	public JMenu getHelpMenu();
	public JMenu getOperationsMenu();
	public JMenu getNewNetworkMenu();

	public void addAction(CyAction action, Map props);
	public void removeAction(CyAction action, Map props);
	public void addTaskFactory(TaskFactory action, Map props);
	public void removeTaskFactory(TaskFactory action, Map props);

	public void addAction(CyAction action);
	public void addAction(CyAction action, int index);
}
