
/*
 File: CyAction.java

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
package cytoscape.util;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * This is a temporary interface that captures everything
 * in CytoscapeAction so that CytoscapeActions can be registered
 * as Services. 
 */
public interface CyAction {
	 void setName(String name);
	 String getName();
	 String actionHelp();
	 String[] completions();
	 void takeArgs(String[] argv);
	 void actionPerformed(ActionEvent e);
	 Object clone();
	 boolean isInMenuBar();
	 boolean isInToolBar();
	 void setPreferredIndex(int index);
	 Integer getPrefferedIndex();
	 void setAcceleratorCombo(int key_code, int key_mods);
	 boolean isAccelerated();
	 int getKeyCode();
	 int getKeyModifiers();
	 String getPreferredMenu();
	 void setPreferredMenu(String new_preferred);
	 String getPreferredButtonGroup();
	 void setPreferredButtonGroup(String new_preferred);
     void menuCanceled(MenuEvent e);
     void menuDeselected(MenuEvent e);
     void menuSelected(MenuEvent e);
}	
