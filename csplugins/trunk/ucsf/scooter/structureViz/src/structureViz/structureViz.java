/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package structureViz;

// System imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// giny imports
import giny.view.NodeView;
import ding.view.*;

// Cytoscape imports
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

// structureViz imports
import structureViz.Chimera;


public class structureViz extends CytoscapePlugin 
  implements NodeContextMenuListener, PropertyChangeListener {

	public static final int NONE = 0;
	public static final int OPEN = 1;
	public static final int CLOSE = 2;
	public static final int ALIGN = 3;
	public static final int EXIT = 4;

  /**
   * Create our action and add it to the plugins menu
   */
  public structureViz() {
		try {
			// Set ourselves up to listen for new networks
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
	
			// Add ourselves to the current network context menu
			((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(this);
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
	    
		JMenu menu = new JMenu("Structure Visualization");
		menu.addMenuListener(new structureVizMenuListener());

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

  }

	public class structureVizMenuListener implements MenuListener {
		private structureVizCommandListener staticHandle;

		structureVizMenuListener() {
			this.staticHandle = new structureVizCommandListener(NONE,null);
		}

	  public void menuCanceled (MenuEvent e) {};
		public void menuDeselected (MenuEvent e) {};
		public void menuSelected (MenuEvent e)
		{
			JMenu m = (JMenu)e.getSource();
			// Clear the menu
			Component[] subMenus = m.getMenuComponents();
			for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }

			// Add our menu items
			{
			  JMenu item = new JMenu("Open structure(s)");
				List structures =  GetSelectedStructures();
				AddSubMenu(item, "all", OPEN, structures);
				Iterator iter = structures.iterator();
				while (iter.hasNext()) {
					AddSubMenu(item, (String)iter.next(), OPEN, null);
				}
				m.add(item);
			}
			{
				JMenuItem item = new JMenuItem("Align structures");
				structureVizCommandListener l = new structureVizCommandListener(ALIGN, null);
				item.addActionListener(l);
				if (l.getChimera() == null) item.setEnabled(false);
				m.add(item);
			}
			{
				if (staticHandle.getChimera() == null) 
				{
			  	JMenuItem item = new JMenuItem("Close structure(s)");
					item.setEnabled(false);
			  	m.add(item);
				} else {
			  	JMenu item = new JMenu("Close structure(s)");
					List openStructures = staticHandle.getOpenStructs();
					AddSubMenu(item, "all", CLOSE, openStructures);
					Iterator iter = openStructures.iterator();
					while (iter.hasNext()) {
						AddSubMenu(item, (String)iter.next(), CLOSE, null);
					}
					m.add(item);
				}
			}
			{
				JMenuItem item = new JMenuItem("Exit Chimera");
				structureVizCommandListener l = new structureVizCommandListener(EXIT, null);
				item.addActionListener(l);
				if (l.getChimera() == null) item.setEnabled(false);
				m.add(item);
			}
		}

		private void AddSubMenu(JMenu menu, String label, int command, Object userData) {
			System.out.println("Adding item "+label);
			JMenuItem item = new JMenuItem(label);
			structureVizCommandListener l = new structureVizCommandListener(command, userData);
			item.addActionListener(l);
		  menu.add(item);
		}

		private List GetSelectedStructures() {
			List<String>structureList = new ArrayList<String>();
      //get the network object; this contains the graph
      CyNetwork network = Cytoscape.getCurrentNetwork();
      //get the network view object
      CyNetworkView view = Cytoscape.getCurrentNetworkView();
      //get the list of node attributes
      CyAttributes cyAttributes = Cytoscape.getNodeAttributes();
      //can't continue if any of these is null
      if (network == null || view == null || cyAttributes == null) {return structureList;}
      //put up a dialog if there are no selected nodes
      if (view.getSelectedNodes().size() == 0) {
        JOptionPane.showMessageDialog(view.getComponent(),
                    "Please select one or more nodes.");
				return structureList;
      }
      //iterate over every node view
      for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
        NodeView nView = (NodeView)i.next();
        //first get the corresponding node in the network
        CyNode node = (CyNode)nView.getNode();
        String nodeID = node.getIdentifier();
        // See if there is a 'structure' attribute
        if (cyAttributes.hasAttribute(nodeID, "Structure")) {
          // Yes, add it to our list
          String structure = cyAttributes.getStringAttribute(nodeID, "Structure");
          structureList.add(structure);
        }
        else if (cyAttributes.hasAttribute(nodeID, "pdb")) {
          // Yes, add it to our list
          String structure = cyAttributes.getStringAttribute(nodeID, "pdb");
          structureList.add(structure);
        }
        else if (cyAttributes.hasAttribute(nodeID, "pdbFileName")) {
          // Yes, add it to our list
          String structure = cyAttributes.getStringAttribute(nodeID, "pdbFileName");
          structureList.add(structure);
        }
      }
			return structureList;
		}
	}
	
  /**
   * This class gets attached to the menu item.
   */
  static class structureVizCommandListener implements ActionListener {
  	private static final long serialVersionUID = 1;
		private static Chimera chimera = null;
		private static ArrayList openStructs = null;
		private int command;
		private Object userData = null;

		structureVizCommandListener(int command, Object userData) {
			this.command = command;
			this.userData = userData;
		}

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
			String label = ae.getActionCommand();
			if (command == OPEN) {
				openAction(label);
			} else if (command == EXIT) {
				exitAction();
			} else if (command == ALIGN) {
				alignAction(label);
			} else if (command == CLOSE) {
				closeAction(label);
			}
		}

		public Chimera getChimera() {
			return chimera;
		}

		public List getOpenStructs() {
			return openStructs;
		}

		private void alignAction(String label) {
			// This is a quick-and-dirty example
		}

		private void exitAction() {
			if (chimera != null) {
				try {
					chimera.exit();
				} catch (IOException e) {}
				chimera = null;
			}
		}

		private void closeAction(String pdbStruct) {
			if (chimera != null) {
				try {
					chimera.close(openStructs.indexOf(pdbStruct));
      	} catch (java.io.IOException e) {
        	// Put up error panel
        	JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView().getComponent(),
        			"Unable to close structure "+pdbStruct, "Unable to close structure "+pdbStruct,
          			JOptionPane.ERROR_MESSAGE);
        	return;
				}
			}
		}

		private void openAction(String pdbStruct) {
			if (openStructs == null) {
				openStructs = new ArrayList();
			}
      // Launch Chimera
      try {
        // Get a chimera instance
        chimera = new Chimera();
        chimera.launch();
      } catch (java.io.IOException e) {
        // Put up error panel
        JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView().getComponent(),
        			"Unable to launch Chimera", "Unable to launch Chimera",
          			JOptionPane.ERROR_MESSAGE);
        return;
      }
			ArrayList structList = null;
			if (pdbStruct.compareTo("all") == 0) {
				structList = (ArrayList)userData;
			} else {
				structList = new ArrayList();
				structList.add(pdbStruct);
			}

      // Send initial commands
			Iterator iter = structList.iterator();
			while (iter.hasNext()) {
				String structFile = (String) iter.next();
				openStructs.add(structFile);
				try {
					chimera.open(structFile, openStructs.indexOf(structFile));
      	} catch (java.io.IOException e) {
        	// Put up error panel
        	JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView().getComponent(),
        			"Unable to open structure "+structFile, "Unable to open structure "+structFile,
          			JOptionPane.ERROR_MESSAGE);
        	return;
				}
			}
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    if ( evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED ){
      // Add menu to the context dialog
			((DGraphView)Cytoscape.getCurrentNetworkView())
				.addNodeContextMenuListener(this);
    }
  }

	/**
	 * Implements addNodecontextMenuItems
	 * @param nodeView
	 * @param menu
	 */
	public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu pmenu) {
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		JMenu menu = new JMenu("Structure Visualization");
		menu.addMenuListener(new structureVizMenuListener());
		pmenu.add(menu);
	}
}
