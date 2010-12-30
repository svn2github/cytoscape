/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package structureViz.ui;

// System imports
import java.awt.Component;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

// structureViz imports
import structureViz.StructureViz;
import structureViz.model.Structure;
import structureViz.actions.Chimera;
import structureViz.actions.CyChimera;

/**
 * The StructureViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class StructureVizMenuListener implements MenuListener {

	private CyLogger logger = null;

	/**
	 * The StructureVizMenuListener provides the interface to the structure viz
	 * Node context menu and the plugin menu.
	 */
	private NodeView overNode = null;

	/**
	 * Create the structureViz menu listener
	 *
	 * @param nv the Cytoscape NodeView the mouse was over
	 */
	public StructureVizMenuListener(NodeView nv, CyLogger logger) {
		this.overNode = nv;
		this.logger = logger;
	}

  public void menuCanceled (MenuEvent e) {};
	public void menuDeselected (MenuEvent e) {};

	/**
	 * Process the selected menu
	 *
	 * @param e the MenuEvent for the selected menu
	 */
	public void menuSelected (MenuEvent e)
	{
		JMenu m = (JMenu)e.getSource();
		Chimera chimera = Chimera.GetChimeraInstance(Cytoscape.getCurrentNetworkView(), logger);

		// Clear the menu
		Component[] subMenus = m.getMenuComponents();
		for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }

		// Add our menu items
		{
		  JMenu item = new JMenu("Open structure(s)");
			List<Structure>structures =  CyChimera.getSelectedStructures(overNode, false);
			if (structures.size() == 0) {
				item.setEnabled(false);
			} else {
				if (structures.size() > 1)
					addSubMenu(item, "all", StructureViz.OPEN, structures);
				for (Structure structure: structures) {
					addSubMenu(item, structure.name(), StructureViz.OPEN, structure);
				}
			}
			m.add(item);
		}
		{
			JMenuItem item = new JMenuItem("Align structures");
			List structures = CyChimera.getSelectedStructures(overNode, false);
			StructureVizMenuHandler l = new StructureVizMenuHandler(StructureViz.ALIGN, structures, logger);
			item.addActionListener(l);
			if (structures.size() < 2) {
				item.setEnabled(false);
			}
			m.add(item);
		}
		{
			if (overNode != null) {
				String residueList = CyChimera.getResidueList((CyNode)overNode.getNode());
				if (residueList != null) {
					// Get the structures for this node
					List<Structure>structures =  CyChimera.getSelectedStructures(overNode, true);
					if (structures.size() > 0) {
		  			JMenuItem item = new JMenuItem("Select residues");
						StructureVizMenuHandler l = 
						    new StructureVizMenuHandler(StructureViz.SELECTRES, structures, logger);
						item.addActionListener(l);
						m.add(item);
					}
				}
			}
		}
		{
			if (overNode != null) {
		  	JMenuItem item = new JMenuItem("Find modelled structures");
				StructureVizMenuHandler l = 
			 	   new StructureVizMenuHandler(StructureViz.FINDMODELS, (CyNode)overNode.getNode(), logger);
				item.addActionListener(l);
				m.add(item);
			}
		}
		{
			if (!chimera.isLaunched())  
			{
		  	JMenuItem item = new JMenuItem("Close structure(s)");
				item.setEnabled(false);
		  	m.add(item);
			} else {
		  	JMenu item = new JMenu("Close structure(s)");
				List<Structure>openStructures = chimera.getOpenStructs();
				addSubMenu(item, "all", StructureViz.CLOSE, openStructures);
				for (Structure structure: openStructures) {
					addSubMenu(item, structure.name(), StructureViz.CLOSE, structure);
				}
				m.add(item);
			}
		}
		{
			JMenuItem item = new JMenuItem("Exit Chimera");
			StructureVizMenuHandler l = new StructureVizMenuHandler(StructureViz.EXIT, null, logger);
			item.addActionListener(l);
			if (chimera.isLaunched()) item.setEnabled(false);
			m.add(item);
		}
/*
		m.addSeparator();
		{
			JMenuItem item = new JMenuItem("Compare sequences");
			List sequences = CyChimera.getSelectedSequences(overNode);
			if (sequences.size() < 2) item.setEnabled(false);
			StructureVizMenuHandler l = new StructureVizMenuHandler(StructureViz.COMPARE, sequences, logger);
			item.addActionListener(l);
			m.add(item);
		}
		{
			JMenuItem item = new JMenuItem("Align sequences");
			List sequences = CyChimera.getSelectedSequences(overNode);
			if (sequences.size() < 2) item.setEnabled(false);
			StructureVizMenuHandler l = new StructureVizMenuHandler(StructureViz.SALIGN, sequences, logger);
			item.addActionListener(l);
			m.add(item);
		}
*/
	}

	/**
	 * Add a submenu item to an existing menu
	 *
	 * @param menu the JMenu to add the new submenu to
	 * @param label the label for the submenu
	 * @param command the command to execute when selected
	 * @param userData data associated with the menu
	 */
	private void addSubMenu(JMenu menu, String label, int command, Object userData) {
		StructureVizMenuHandler l = new StructureVizMenuHandler(command, userData, logger);
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(l);
	  menu.add(item);
	}
}
